package org.foi.nwtis.aalagic.web.zrna;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
import org.foi.nwtis.aalagic.ws.klijenti.Aerodrom;
import org.foi.nwtis.aalagic.ws.klijenti.AerodromWS2_Klijent;
import org.foi.nwtis.aalagic.ws.klijenti.AvionLeti;

/**
 * Zrno koje se koristi prilikom dohvata i azuriranja podataka korisnika,
 * aerodroma, letova na sučeljima web stranice. Zrno podatke dohvaća preko EJB
 * resursa i WebSocketa poslužitelja za meteorološke informacije.
 *
 * @author Aldin Alagic
 */
@Named(value = "aerodromiP4")
@ViewScoped
public class AerodromiP4 implements Serializable {

    @Getter
    @Setter
    Date vrijemeOd;
    @Getter
    @Setter
    Date vrijemeDo;
    @Getter
    @Setter
    Date vrijemeOdAvion;
    @Getter
    @Setter
    Date vrijemeDoAvion;
    @Getter
    Korisnici korisnik;
    @Getter
    @Setter
    String odabraniAerodrom = "";
    @Getter
    @Setter
    String odabraniAvion = "";
    @Getter
    List<Aerodrom> aerodromiKorisnika = new ArrayList<>();
    @Getter
    List<AvionLeti> letoviAerodroma = new ArrayList<>();
    @Getter
    List<AvionLeti> letoviAviona = new ArrayList<>();

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
    private void pripremiKorisnika() {
        HttpSession session = (HttpSession) facesContext.getExternalContext().getSession(true);
        korisnik = (Korisnici) session.getAttribute("korisnik");
        if (korisnik != null) {
            dohvatiAerodromeKorisnika();
        }
    }

    /**
     * Konstruktor objekata klase.
     */
    public AerodromiP4() {
    }

    public String odaberiAerodrom(String aerodrom) {
        odabraniAerodrom = aerodrom;
        return "";
    }

    public String odaberiAvion(String avion) {
        odabraniAvion = avion;
        return "";
    }

    public String dohvatiAerodromeKorisnika() {
        AerodromWS2_Klijent servis = new AerodromWS2_Klijent();
        aerodromiKorisnika = servis.dohvatiAerodromeKorisnika(korisnik.getKorIme(), korisnik.getLozinka());
        return "";
    }

    public String dohvatiLetoveAerodroma() {
        if (odabraniAerodrom.isEmpty()) {
            growlPoruka.prikaziPoruku("Letovi aerodroma", "Podaci nisu ispravni.");
            letoviAerodroma = new ArrayList<>();
            return "";
        }
        AerodromWS2_Klijent servis = new AerodromWS2_Klijent();
        letoviAerodroma = servis.dohvatiLetoveAerodroma(korisnik.getKorIme(), korisnik.getLozinka(), odabraniAerodrom, vrijemeOd, vrijemeDo);
        growlPoruka.prikaziPoruku("Letovi aerodroma", "Uspješno dohvaćeni letovi.");
        return "";
    }

    public String dohvatiLetoveAviona() {
        if (odabraniAvion.isEmpty() || vrijemeOdAvion == null || vrijemeDoAvion == null) {
            growlPoruka.prikaziPoruku("Letovi aviona", "Podaci nisu ispravni.");
            letoviAviona = new ArrayList<>();
            return "";
        }

        AerodromWS2_Klijent servis = new AerodromWS2_Klijent();
        letoviAviona = servis.dohvatiLetoveAviona(korisnik.getKorIme(), korisnik.getLozinka(), odabraniAvion, vrijemeOdAvion, vrijemeDoAvion);
        growlPoruka.prikaziPoruku("Letovi aerodroma", "Uspješno dohvaćeni letovi.");
        return "";
    }

    public String dohvatiVrijeme(int v) {
        Date vrijeme = new Date(v * 1000L);
        SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
        System.out.println(formatter.format(vrijeme));
        return formatter.format(vrijeme);
    }

}
