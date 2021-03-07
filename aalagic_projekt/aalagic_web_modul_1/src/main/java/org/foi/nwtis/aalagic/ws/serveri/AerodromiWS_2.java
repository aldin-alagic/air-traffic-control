package org.foi.nwtis.aalagic.ws.serveri;

import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import javax.jws.WebService;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.servlet.ServletContext;
import org.foi.nwtis.aalagic.ejb.eb.Airplanes;
import org.foi.nwtis.aalagic.ejb.eb.Airports;
import org.foi.nwtis.aalagic.ejb.eb.Korisnici;
import org.foi.nwtis.aalagic.ejb.eb.Myairports;
import org.foi.nwtis.aalagic.ejb.sb.AirplanesFacadeLocal;
import org.foi.nwtis.aalagic.ejb.sb.AirportsFacadeLocal;
import org.foi.nwtis.aalagic.ejb.sb.KorisniciFacadeLocal;
import org.foi.nwtis.aalagic.ejb.sb.MyairportsFacadeLocal;
import org.foi.nwtis.aalagic.util.Koordinata;
import org.foi.nwtis.podaci.Aerodrom;
import org.foi.nwtis.rest.podaci.AvionLeti;
import org.foi.nwtis.rest.podaci.Lokacija;

/**
 *
 * @author NWTiS_4
 */
@WebService(serviceName = "AerodromiWS_2")
public class AerodromiWS_2 {

    @Inject
    ServletContext context;

    @Inject
    KorisniciFacadeLocal korisniciFacadeLocal;

    @Inject
    AirportsFacadeLocal airportsFacadeLocal;

    @Inject
    MyairportsFacadeLocal myairportsFacadeLocal;

    @Inject
    AirplanesFacadeLocal airplanesFacadeLocal;

    /**
     * Metoda koja autenticira korisnika na osnovu njegovog korisničkog imena i
     * lozinke.
     *
     * @param korisnickoIme - korisničko ime korisnika koji se autenticira
     * @param lozinka - lozinka korisnika koji se autenticira
     * @return
     */
    private Korisnici autenticirajKorisnika(String korisnickoIme, String lozinka) {
        Korisnici korisnik = korisniciFacadeLocal.find(korisnickoIme);
        if (korisnik != null && korisnik.getLozinka().equals(lozinka)) {
            return korisnik;
        }
        return null;
    }

    /**
     * Web servis operacija za dohvat podataka o aerodromima koji imaju naziv
     * sličan zadanom nazivu.
     *
     * OPERACIJA 1
     *
     * @param korisnickoIme - korisničko ime korisnika koji vrši upit
     * @param lozinka - lozinka korisnika koji vrši upit
     * @param naziv - naziv po kojem se dohvaćaju aerodromi
     * @return vraća listu aerodroma
     */
    @WebMethod(operationName = "dajAerodromeNaziv")
    public List<Aerodrom> dajAerodromeNaziv(
            @WebParam(name = "korisnickoIme") String korisnickoIme,
            @WebParam(name = "lozinka") String lozinka,
            @WebParam(name = "naziv") String naziv) {

        if (autenticirajKorisnika(korisnickoIme, lozinka) != null) {
            List<Airports> airports = airportsFacadeLocal.findAllName(naziv);
            List<Aerodrom> aerodromi = dohvatiListuAerodroma(airports);
            return aerodromi;
        }
        return null;
    }

    /**
     * Web servis operacija za dohvat podataka o aerodromima koji pripadaju
     * zadanoj državi.
     *
     * OPERACIJA 2
     *
     * @param korisnickoIme - korisničko ime korisnika koji vrši upit
     * @param lozinka - lozinka korisnika koji vrši upit
     * @param drzava - država čiji se aerodromi dohvaćaju
     * @return vraća listu aerodroma
     */
    @WebMethod(operationName = "dajAerodromeDrzava")
    public List<Aerodrom> dajAerodromeDrzava(@WebParam(name = "korisnickoIme") String korisnickoIme,
            @WebParam(name = "lozinka") String lozinka, @WebParam(name = "drzava") String drzava) {

        if (autenticirajKorisnika(korisnickoIme, lozinka) != null) {
            List<Airports> airports = airportsFacadeLocal.findAllCountry(drzava);
            List<Aerodrom> aerodromi = dohvatiListuAerodroma(airports);
            return aerodromi;
        }
        return null;
    }

    /**
     * Web servis operacija za dohvat podataka o aerodromima koji pripadaju
     * zadanoj državi.
     *
     * OPERACIJA 3
     *
     * @param korisnickoIme - korisničko ime korisnika koji vrši upit
     * @param lozinka - lozinka korisnika koji vrši upit
     * @return vraća listu aerodroma
     */
    @WebMethod(operationName = "dajAerodromeKorisnika")
    public List<Aerodrom> dajAerodromeKorisnika(@WebParam(name = "korisnickoIme") String korisnickoIme,
            @WebParam(name = "lozinka") String lozinka) {
        Korisnici korisnik = autenticirajKorisnika(korisnickoIme, lozinka);
        if (korisnik != null) {
            List<Myairports> airports = myairportsFacadeLocal.findAllUser(korisnik);
            List<Aerodrom> aerodromi = dohvatiListuAerodromaKorisnika(airports);
            return aerodromi;
        }
        return null;
    }

    /**
     * Web servis operacija za dohvat podataka o letovima koji su polijetali sa
     * zadanog aerodroma u rasponu određenom parametrima.
     *
     * OPERACIJA 4
     *
     * @param korisnickoIme korisničko ime korisnika koji vrši upit
     * @param lozinka lozinka korisnika koji vrši upit
     * @param icao identifikacija aerodroma
     * @param odVrijeme vrijeme od kojeg se traže letovi
     * @param doVrijeme vrijeme do kojeg se traže letovi
     * @return vraća listu letova
     */
    @WebMethod(operationName = "dajLetoveAerodroma")
    public List<AvionLeti> dajLetoveAerodroma(@WebParam(name = "korisnickoIme") String korisnickoIme,
            @WebParam(name = "lozinka") String lozinka,
            @WebParam(name = "icao") String icao,
            @WebParam(name = "od") String odVrijeme,
            @WebParam(name = "do") String doVrijeme) {

        if (autenticirajKorisnika(korisnickoIme, lozinka) != null) {
            int odVrijemeBroj = Integer.parseInt(odVrijeme);
            int doVrijemeBroj = Integer.parseInt(doVrijeme);
            Airports aerodrom = airportsFacadeLocal.find(icao);
            List<Airplanes> airports = airplanesFacadeLocal.findAllAirport(aerodrom, odVrijemeBroj, doVrijemeBroj);
            List<AvionLeti> letovi = dohvatiListuLetova(airports);
            return letovi;
        }
        return null;
    }

    /**
     * Web servis operacija za dohvat podataka o letovima zadanog aviona u
     * rasponu određenom parametrima.
     *
     * OPERACIJA 5
     *
     * @param korisnickoIme korisničko ime korisnika koji vrši upit
     * @param lozinka lozinka korisnika koji vrši upit
     * @param icao24 identifikacija aviona
     * @param odVrijeme vrijeme od kojeg se traže letovi
     * @param doVrijeme vrijeme do kojeg se traže letovi
     * @return vraća listu letova
     */
    @WebMethod(operationName = "dajLetoveAviona")
    public List<AvionLeti> dajLetoveAviona(
            @WebParam(name = "korisnickoIme") String korisnickoIme,
            @WebParam(name = "lozinka") String lozinka,
            @WebParam(name = "icao24") String icao24,
            @WebParam(name = "od") String odVrijeme,
            @WebParam(name = "do") String doVrijeme) {

        if (autenticirajKorisnika(korisnickoIme, lozinka) != null) {
            int odVrijemeBroj = Integer.parseInt(odVrijeme);
            int doVrijemeBroj = Integer.parseInt(doVrijeme);
            List<Airplanes> airports = airplanesFacadeLocal.findAllName(icao24, odVrijemeBroj, doVrijemeBroj);
            List<AvionLeti> letovi = dohvatiListuLetova(airports);
            return letovi;
        }
        return null;
    }

    /**
     * Web servis operacija za dohvat udaljenosti između zadanih aerodroma.
     *
     * OPERACIJA 6
     *
     * @param korisnickoIme korisničko ime korisnika koji vrši upit
     * @param lozinka lozinka korisnika koji vrši upit
     * @param icao1 identifikacija prvog aerodroma
     * @param icao2 identifikacija drugog aerodroma
     * @return vraća udaljenost između aerodroma
     */
    @WebMethod(operationName = "dajUdaljenostAerodroma")
    public double dajUdaljenostAerodroma(
            @WebParam(name = "korisnickoIme") String korisnickoIme,
            @WebParam(name = "lozinka") String lozinka,
            @WebParam(name = "icao1") String icao1,
            @WebParam(name = "icao2") String icao2) {

        Airports aerodrom1 = airportsFacadeLocal.find(icao1);
        Airports aerodrom2 = airportsFacadeLocal.find(icao2);

        if (aerodrom1 == null || aerodrom2 == null) {
            return -1;
        }
        Koordinata koordinata1 = Koordinata.dohvatiKoordinatu(aerodrom1.getCoordinates());
        Koordinata koordinata2 = Koordinata.dohvatiKoordinatu(aerodrom2.getCoordinates());
        return Koordinata.izracunajUdaljenost(koordinata1, koordinata2);
    }

    /**
     * Web servis operacija za dohvat aerodroma korisnika koji se nalaze u
     * blizini zadanog aerodroma.Blizina je određena gornjom i donjom granicom.
     *
     * OPERACIJA 7
     *
     * @param korisnickoIme korisničko ime korisnika koji vrši upit
     * @param lozinka lozinka korisnika koji vrši upit
     * @param icao identifikacija aerodroma
     * @param donjaGranica donja granica raspona udaljenosti
     * @param gornjaGranica gornja granica raspona udaljenosti
     * @return vraća listu aerodroma u blizini zadanog aerodroma
     */
    @WebMethod(operationName = "dajAerodromeIzBlizine")
    public List<Aerodrom> dajAerodromeIzBlizine(
            @WebParam(name = "korisnickoIme") String korisnickoIme,
            @WebParam(name = "lozinka") String lozinka,
            @WebParam(name = "icao") String icao,
            @WebParam(name = "donjaGranica") int donjaGranica,
            @WebParam(name = "gornjaGranica") int gornjaGranica) {

        Korisnici korisnik = autenticirajKorisnika(korisnickoIme, lozinka);
        if (korisnik != null) {
            Airports aerodrom = airportsFacadeLocal.find(icao);
            Koordinata koordinataAerodrom = Koordinata.dohvatiKoordinatu(aerodrom.getCoordinates());
            List<Myairports> aerodromiKorisnika = myairportsFacadeLocal.findAllUser(korisnik);

            List<Airports> aerodromiIzBlizine = new ArrayList<>();
            for (Myairports ma : aerodromiKorisnika) {
                Airports a = ma.getIdent();
                Koordinata koordinata = Koordinata.dohvatiKoordinatu(a.getCoordinates());
                double udaljenost = Koordinata.izracunajUdaljenost(koordinataAerodrom, koordinata);
                if (udaljenost > donjaGranica && udaljenost < gornjaGranica) {
                    aerodromiIzBlizine.add(a);
                }
            }
            return dohvatiListuAerodroma(aerodromiIzBlizine);
        } else {
            return null;
        }
    }

    private List<Aerodrom> dohvatiListuAerodroma(List<Airports> airports) {
        if (airports.isEmpty()) {
            return null;
        }

        List<Aerodrom> aerodromi = new ArrayList<>();
        for (Airports a : airports) {
            String[] koordinate = a.getCoordinates().split(", ");
            Lokacija lokacija = new Lokacija(koordinate[1], koordinate[0]);
            Aerodrom aerodrom = new Aerodrom(a.getIdent(), a.getName(), a.getIsoCountry(), lokacija);
            aerodromi.add(aerodrom);
        }
        return aerodromi;
    }

    private List<Aerodrom> dohvatiListuAerodromaKorisnika(List<Myairports> airports) {
        if (airports.isEmpty()) {
            return null;
        }

        List<Aerodrom> aerodromi = new ArrayList<>();
        for (Myairports a : airports) {
            String[] koordinate = a.getIdent().getCoordinates().split(", ");
            Lokacija lokacija = new Lokacija(koordinate[1], koordinate[0]);
            Aerodrom aerodrom = new Aerodrom(a.getIdent().getIdent(), a.getIdent().getName(), a.getIdent().getIsoCountry(), lokacija);
            aerodromi.add(aerodrom);
        }
        return aerodromi;
    }

    private List<AvionLeti> dohvatiListuLetova(List<Airplanes> airplanes) {
        if (airplanes.isEmpty()) {
            return null;
        }

        List<AvionLeti> letovi = new ArrayList<>();
        for (Airplanes a : airplanes) {
            AvionLeti let = new AvionLeti(
                    a.getIcao24(),
                    a.getFirstseen(),
                    a.getEstdepartureairport().getIdent(),
                    a.getLastseen(),
                    a.getEstarrivalairport(),
                    a.getCallsign(),
                    a.getEstdepartureairporthorizdistance(),
                    a.getEstdepartureairportvertdistance(),
                    a.getEstarrivalairporthorizdistance(),
                    a.getEstarrivalairportvertdistance(),
                    a.getDepartureairportcandidatescount(),
                    a.getArrivalairportcandidatescount()
            );
            letovi.add(let);
        }
        return letovi;
    }
}
