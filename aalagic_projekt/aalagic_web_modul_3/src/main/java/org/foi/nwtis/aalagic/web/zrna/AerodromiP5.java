package org.foi.nwtis.aalagic.web.zrna;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import javax.inject.Inject;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import lombok.Getter;
import lombok.Setter;
import org.foi.nwtis.aalagic.ejb.eb.Korisnici;
import org.foi.nwtis.aalagic.ws.klijenti.AerodromWS2_Klijent;
import org.foi.nwtis.aalagic.rest.klijenti.AerodromiRS_1;
import org.foi.nwtis.podaci.Aerodrom;
import org.foi.nwtis.podaci.OdgovorAerodrom;

/**
 * Zrno koje se koristi prilikom dohvata i azuriranja podataka korisnika,
 * aerodroma, letova na sučeljima web stranice. Zrno podatke dohvaća preko EJB
 * resursa i WebSocketa poslužitelja za meteorološke informacije.
 *
 * @author Aldin Alagic
 */
@Named(value = "aerodromiP5")
@ViewScoped
public class AerodromiP5 implements Serializable {

    @Getter
    Korisnici korisnik;
    @Getter
    @Setter
    String odabraniAerodrom = "";
    @Getter
    @Setter
    String odabraniAerodromUdaljenost = "";
    @Getter
    @Setter
    String prviAerodrom = "";
    @Getter
    @Setter
    String drugiAerodrom = "";
    @Getter
    @Setter
    String udaljenostAerodroma;
    @Getter
    List<Aerodrom> aerodromiKorisnika = new ArrayList<>();
    @Getter
    List<org.foi.nwtis.aalagic.ws.klijenti.Aerodrom> filtriraniAerodromi = new ArrayList<>();
    @Getter
    @Setter
    String minUdaljenost = "";
    @Getter
    @Setter
    String maxUdaljenost = "";
    @Inject
    ServletContext context;

    @Inject
    FacesContext facesContext;

    @Inject
    private GrowlPoruka growlPoruka;

    /**
     * Metoda koja se poziva nakon otvaranja stranice i učitavanja ovisnosti, a
     * prije poziva prve poslovne metode. Dohvaća api ključ za pristup servisima
     * s meterološkim podacima. Dohvaća preko EJB resursa korisnike.
     */
    @PostConstruct
    public void pripremiKorisnika() {
        HttpSession session = (HttpSession) facesContext.getExternalContext().getSession(true);
        korisnik = (Korisnici) session.getAttribute("korisnik");
        if (korisnik != null) {
            dohvatiAerodromeKorisnika();
        }
    }

    /**
     * Konstruktor objekata klase.
     */
    public AerodromiP5() {
    }

    public String dohvatiAerodromeKorisnika() {
        AerodromiRS_1 servis = new AerodromiRS_1(korisnik.getKorIme(), korisnik.getLozinka());
        OdgovorAerodrom odgovor = servis.dajAerodomeKorisnika(OdgovorAerodrom.class);
        aerodromiKorisnika = Arrays.asList(odgovor.getOdgovor());
        return "";
    }

    public String odaberiAerodrom() {
        growlPoruka.prikaziPoruku("Odabir aerodroma", "Uspješno odabran aerodrom " + odabraniAerodrom + ".");
        return "";
    }

    public String obrisiAerodrom() {
        AerodromiRS_1 servis = new AerodromiRS_1(korisnik.getKorIme(), korisnik.getLozinka());
        OdgovorAerodrom odgovor = servis.brisiAerodromKorisnika(OdgovorAerodrom.class, odabraniAerodrom);
        if (odgovor.getStatus().equals("10")) {
            growlPoruka.prikaziPoruku("Brisanje aerodroma", "Uspješno brisanje aerodroma.");
            odabraniAerodrom = "";
        } else {
            growlPoruka.prikaziPoruku("Brisanje aerodroma", "Neuspješno brisanje aerodroma.");
        }
        return "";
    }

    public String obrisiLetoveAerodroma() {
        AerodromiRS_1 servis = new AerodromiRS_1(korisnik.getKorIme(), korisnik.getLozinka());
        OdgovorAerodrom odgovor = servis.brisiAerodromKorisnika(OdgovorAerodrom.class, odabraniAerodrom);
        if (odgovor.getStatus().equals("10")) {
            growlPoruka.prikaziPoruku("Brisanje letova aerodroma", "Uspješno brisanje letova aerodroma.");
            odabraniAerodrom = "";

        } else {
            growlPoruka.prikaziPoruku("Brisanje letova aerodroma", "Neuspješno brisanje letova aerodroma.");
        }
        return "";
    }

    public String dohvatiAerodromeUdaljenost() {
        AerodromWS2_Klijent servis = new AerodromWS2_Klijent();
        if (odabraniAerodrom.isEmpty()) {
            growlPoruka.prikaziPoruku("Aerodromi u blizini", "Neispravni podaci.");
            return "";
        }

        try {
            int min = Integer.parseInt(minUdaljenost);
            int max = Integer.parseInt(maxUdaljenost);
            filtriraniAerodromi = servis.dohvatiAerodromeUdaljenost(korisnik.getKorIme(), korisnik.getLozinka(),
                    odabraniAerodrom, min, max);
            growlPoruka.prikaziPoruku("Aerodromi u blizini", "Uspješno dohvaćeni aerodromi u blizini odabranog aerodroma.");
        } catch (NumberFormatException e) {
            minUdaljenost = "";
            maxUdaljenost = "";
            growlPoruka.prikaziPoruku("Aerodromi u blizini", "Neispravni podaci.");
        }
        return "";

    }

    public String dohvatiUdaljenost() {
        if (prviAerodrom.isEmpty() || drugiAerodrom.isEmpty()) {
            growlPoruka.prikaziPoruku("Udaljenost aerodroma", "Neispravni podaci.");
            return "";
        }
        AerodromWS2_Klijent servis = new AerodromWS2_Klijent();
        double udaljenost = servis.dohvatiUdaljenost(korisnik.getKorIme(), korisnik.getLozinka(), prviAerodrom, drugiAerodrom);
        udaljenostAerodroma = Double.toString(udaljenost);
        growlPoruka.prikaziPoruku("Udaljenost aerodroma", "Uspješno dohvaćena udaljenost.");
        return "";
    }

    public String odaberiAerodromUdaljenost(String aerodrom) {
        if (prviAerodrom.isEmpty()) {
            prviAerodrom = aerodrom;
        } else if (drugiAerodrom.isEmpty()) {
            drugiAerodrom = aerodrom;
        } else {
            prviAerodrom = aerodrom;
            drugiAerodrom = "";
        }
        return "";
    }

}
