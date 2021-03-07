package org.foi.nwtis.aalagic;

import java.util.Date;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.foi.nwtis.aalagic.ejb.eb.Airplanes;
import org.foi.nwtis.aalagic.ejb.eb.Airports;
import org.foi.nwtis.aalagic.ejb.eb.Myairports;
import org.foi.nwtis.aalagic.ejb.eb.Myairportslog;
import org.foi.nwtis.aalagic.ejb.eb.MyairportslogPK;
import org.foi.nwtis.aalagic.ejb.sb.AirplanesFacadeLocal;
import org.foi.nwtis.aalagic.ejb.sb.MyairportsFacadeLocal;
import org.foi.nwtis.aalagic.ejb.sb.MyairportslogFacadeLocal;
import org.foi.nwtis.aalagic.ejb.sb.PosluziteljAerodroma;
import org.foi.nwtis.aalagic.konfiguracije.Konfiguracija;
import org.foi.nwtis.aalagic.socket.klijent.SocketKlijent;
import org.foi.nwtis.rest.klijenti.OSKlijent;
import org.foi.nwtis.rest.podaci.AvionLeti;

/**
 * Klasa dretva koja obavlja preuzimanje letova aviona koji polijeću s aerodroma
 * koje korisnici prate. Dretva započinje s preuzimanjem konfiguracijski
 * postavki koje su potrebne za rad dretve. Dretva nakon toga redom preuzima
 * letove aerodroma koje prate korisnici po zadanim datumima.
 *
 * @author Aldin Alagic
 */
public class PreuzimanjeLetova extends Thread {

    MyairportsFacadeLocal myairportsFacade;

    MyairportslogFacadeLocal myairportslogFacade;

    AirplanesFacadeLocal airplanesFacade;

    private static final String SVN_KORISNIK = "aalagic";
    private static final String SVN_LOZINKA = "7ed9FQ";

    Konfiguracija konfiguracija;
    int port;
    int preuzimanjeCiklus;
    int preuzimanjePauza;
    Timestamp preuzimanjePocetakDatum;
    Timestamp preuzimanjeKrajDatum;
    OSKlijent klijent;
    List<Myairportslog> obradeniDaniAerodroma;
    boolean aktivna = true;
    boolean statusPreuzimanja = false;

    public PreuzimanjeLetova(
            Konfiguracija k,
            MyairportsFacadeLocal maf,
            MyairportslogFacadeLocal malf,
            AirplanesFacadeLocal af) {

        konfiguracija = k;
        myairportsFacade = maf;
        myairportslogFacade = malf;
        airplanesFacade = af;
    }

    @Override
    public void start() {
        if (!dohvatiPostavke(konfiguracija)) {
            System.out.println("Greška kod učitavanja postavki za rad dretve!");
            return;
        }
        postaviOSklijenta(konfiguracija);
        System.out.println("Krece dretva");
        super.start();
    }

    @Override
    public void interrupt() {
        statusPreuzimanja = true;
        super.interrupt();
    }

    /**
     * Nadjačana metoda run u kojoj dretva dohvaća letove aerodroma u određenom
     * danu. U svakom ciklusu dolazi do povećanja datuma za jedan dan te dohvat
     * letova na dobiveni datum. Kada se doće do datuma koji je određen
     * konfiguracijskom datotekom dretva završava preuzimanje. U slučaju da se
     * dođe do važećeg datuma, dretva spava jedan dan. Ako dretva obradi sve
     * aerodrome prije kraja svog intervala, onda spava do kraja intervala.
     *
     */
    @Override
    public void run() {

        int brojac = 0;
        while (aktivna) {
            long vrijemeObrade = 0;
            long vrijemePocetak = System.currentTimeMillis();
            provjeriStanje();

            List<Myairports> promatraniAerodromi = myairportsFacade.findAll();
            try {
                if (!statusPreuzimanja) {
                    System.out.println("Dretva ne smije preuzimati letove! Preskačem ciklus.");
                    vrijemeObrade += System.currentTimeMillis() - vrijemePocetak;
                    PosluziteljAerodroma.posaljiSvima(promatraniAerodromi.size());
                    sleep(preuzimanjeCiklus - vrijemeObrade);
                    continue;
                }

                Airports aerodrom = promatraniAerodromi.get(brojac).getIdent();
                long datum = (preuzimanjePocetakDatum.getTime() / 1000L);
                long danSekunde = 24 * 60 * 60;
                long sljedeciDan = datum + (danSekunde);
                System.out.println("Sad je na redu aerodrom: " + aerodrom);
                List<AvionLeti> avioni = klijent.getDepartures(aerodrom.getIdent(), datum, sljedeciDan);
                if (!avioni.isEmpty()) {
                    obradiAvioneAerodroma(avioni, aerodrom, preuzimanjePocetakDatum);
                }
                brojac++;
                vrijemeObrade += System.currentTimeMillis() - vrijemePocetak;
                long danMilisekunde = 24 * 60 * 60 * 1000;

                if (promatraniAerodromi.size() > (brojac + 1)) {
                    brojac++;
                    sleep(preuzimanjePauza);
                    System.out.println("Obradujem sljedeci!");
                } else {
                    brojac = 0;
                    System.out.println("Obradila sve za ovaj datum!");
                    preuzimanjePocetakDatum.setTime(preuzimanjePocetakDatum.getTime() + danMilisekunde);
                    if (preuzimanjeCiklus - vrijemeObrade > 0) {
                        sleep(preuzimanjeCiklus - vrijemeObrade);
                        PosluziteljAerodroma.posaljiSvima(promatraniAerodromi.size());
                    }
                    if (preuzimanjePocetakDatum.after(preuzimanjeKrajDatum)) {
                        System.out.println("Došla do zadnjeg dana, gotova sa preuzimanjem!");
                        aktivna = false;
                    } else if (preuzimanjePocetakDatum.after(new Timestamp(System.currentTimeMillis()))) {
                        System.out.println("Došla do današenjh dana, spavam jedan dan!");
                        sleep(danMilisekunde);
                    }
                }
            } catch (InterruptedException ex) {
                Logger.getLogger(PreuzimanjeLetova.class.getName()).log(Level.SEVERE, null, ex);
                aktivna = false;
            }
        }
    }

    private void provjeriStanje() {
        String poruka = String.format("KORISNIK %s; LOZINKA %s; STANJE;", SVN_KORISNIK, SVN_LOZINKA);
        String adresa = "localhost";
        int port = Integer.parseInt(konfiguracija.dajPostavku("server.port"));
        SocketKlijent server = new SocketKlijent(adresa, port);
        String odgovor = server.posaljiPoruku(poruka);
        System.out.println("Stigao odgovor za dretvu: " + odgovor);
        if (odgovor.equals("OK 11;")) {
            statusPreuzimanja = true;
        } else {
            statusPreuzimanja = false;
        }
    }

    /**
     * Metoda koja preuzima iz konfiguracije postavke potrebne za rad klase.
     *
     */
    private boolean dohvatiPostavke(Konfiguracija konfiguracija) {
        try {
            port = Integer.parseInt(konfiguracija.dajPostavku("server.port"));
            preuzimanjeCiklus = Integer.parseInt(konfiguracija.dajPostavku("preuzimanje.ciklus"));
            preuzimanjePauza = Integer.parseInt(konfiguracija.dajPostavku("preuzimanje.pauza"));
            String pocetakDatum = konfiguracija.dajPostavku("preuzimanje.pocetak");
            String krajDatum = konfiguracija.dajPostavku("preuzimanje.kraj");
            SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
            preuzimanjePocetakDatum = new Timestamp(sdf.parse(pocetakDatum).getTime());
            preuzimanjeKrajDatum = new Timestamp(sdf.parse(krajDatum).getTime());
            return true;
        } catch (ParseException ex) {
            Logger.getLogger(PreuzimanjeLetova.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }

    private void postaviOSklijenta(Konfiguracija konfig) {
        String korisnickoIme = konfig.dajPostavku("OpenSkyNetwork.korisnik");
        String lozinka = konfig.dajPostavku("OpenSkyNetwork.lozinka");
        klijent = new OSKlijent(korisnickoIme, lozinka);
    }

    private void obradiAvioneAerodroma(List<AvionLeti> avioni, Airports aerodrom, Timestamp vrijeme) {
        Date datum = new Date(vrijeme.getTime());
        MyairportslogPK pk = new MyairportslogPK(aerodrom.getIdent(), datum);
        if (myairportslogFacade.find(pk) != null) {
            System.out.println("Postoji zapis, preskačem: " + aerodrom);
            return;
        }

        obradeniDaniAerodroma = myairportslogFacade.findAll();
        for (AvionLeti a : avioni) {
            if (a.getEstArrivalAirport() != null && !a.getEstArrivalAirport().isEmpty()) {
                Airplanes avion = new Airplanes();
                avion.setIcao24(a.getIcao24());
                avion.setFirstseen(a.getFirstSeen());
                avion.setLastseen(a.getLastSeen());
                avion.setEstarrivalairport(a.getEstArrivalAirport());
                avion.setEstdepartureairport(aerodrom);
                avion.setEstarrivalairporthorizdistance(a.getEstArrivalAirportHorizDistance());
                avion.setEstarrivalairportvertdistance(a.getEstArrivalAirportVertDistance());
                avion.setEstdepartureairporthorizdistance(a.getEstDepartureAirportHorizDistance());
                avion.setEstarrivalairportvertdistance(a.getEstDepartureAirportVertDistance());
                avion.setArrivalairportcandidatescount(a.getArrivalAirportCandidatesCount());
                avion.setDepartureairportcandidatescount(a.getDepartureAirportCandidatesCount());
                avion.setCallsign(a.getCallsign());
                avion.setStored(new Date());
                airplanesFacade.create(avion);
            }
        }

        Myairportslog noviZapis = new Myairportslog(new MyairportslogPK(aerodrom.getIdent(), datum), new Date());
        noviZapis.setAirports(aerodrom);
        myairportslogFacade.create(noviZapis);
    }
}
