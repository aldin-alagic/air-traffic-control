package org.foi.nwtis.aalagic.web.zrna;

import com.google.gson.JsonObject;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import javax.inject.Inject;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import lombok.Getter;
import lombok.Setter;
import org.foi.nwtis.aalagic.ejb.eb.Airports;
import org.foi.nwtis.aalagic.ejb.eb.Korisnici;
import org.foi.nwtis.aalagic.ejb.eb.Myairports;
import org.foi.nwtis.aalagic.ejb.sb.AirportsFacadeLocal;
import org.foi.nwtis.aalagic.ejb.sb.KorisniciFacadeLocal;
import org.foi.nwtis.aalagic.ejb.sb.MyairportsFacadeLocal;
import org.foi.nwtis.aalagic.ejb.sb.MyairportslogFacadeLocal;
import org.foi.nwtis.aalagic.konfiguracije.bp.BP_Konfiguracija;
import org.foi.nwtis.aalagic.wsocket.klijenti.WebSocketKlijent;
import org.foi.nwtis.rest.klijenti.LIQKlijent;
import org.foi.nwtis.rest.klijenti.OSKlijent;
import org.foi.nwtis.rest.klijenti.OWMKlijent;
import org.foi.nwtis.rest.podaci.Lokacija;
import org.foi.nwtis.rest.podaci.MeteoPodaci;

/**
 * Zrno koje se koristi prilikom dohvata i azuriranja podataka korisnika,
 * aerodroma, letova na sučeljima web stranice. Zrno podatke dohvaća preko EJB
 * resursa i WebSocketa poslužitelja za meteorološke informacije.
 *
 * @author Aldin Alagic
 */
@Named(value = "aerodromiP3")
@ViewScoped
public class AerodromiP3 implements Serializable {

    @EJB
    KorisniciFacadeLocal korisniciFacade;

    @EJB
    MyairportsFacadeLocal myairportsFacade;

    @EJB
    MyairportslogFacadeLocal myairportslogFacade;

    @EJB
    AirportsFacadeLocal airportsFacade;

    @Getter
    List<Myairports> aerodromi = new ArrayList<>();
    @Getter
    List<Airports> aerodromiFiltrirani = new ArrayList<>();
    @Getter
    @Setter
    String odabraniAerodrom = null;
    @Getter
    @Setter
    String odabraniVlastitiAerodrom = null;
    @Getter
    @Setter
    String odabraniNaziv = null;
    @Getter
    int brojPromatranihAerodroma = 0;
    @Getter
    Korisnici korisnik;
    @Getter
    String adresa;
    @Getter
    String sirina;
    @Getter
    String duzina;
    @Getter
    MeteoPodaci meteoPodaci;

    @Inject
    ServletContext context;

    @Inject
    FacesContext facesContext;

    private String OWMkey;
    private String LIQkey;
    private String OSNkorisnik;
    private String OSNlozinka;

    /**
     * Konstruktor objekata klase.
     */
    public AerodromiP3() {
    }

    /**
     * Metoda koja se poziva nakon otvaranja stranice i učitavanja ovisnosti, a
     * prije poziva prve poslovne metode. Dohvaća api ključ za pristup servisima
     * s meterološkim podacima. Dohvaća preko EJB resursa korisnike.
     */
    @PostConstruct
    private void dohvatiAerodromeKorisnika() {
        HttpSession session = (HttpSession) facesContext.getExternalContext().getSession(true);
        korisnik = (Korisnici) session.getAttribute("korisnik");
        if (korisnik != null) {
            BP_Konfiguracija bpk = (BP_Konfiguracija) context.getAttribute("BP_Konfig");
            OWMkey = bpk.getKonfig().dajPostavku("OpenWeatherMap.apikey");
            LIQkey = bpk.getKonfig().dajPostavku("LocationIQ.token");
            OSNkorisnik = bpk.getKonfig().dajPostavku("OpenSkyNetwork.korisnik");
            OSNlozinka = bpk.getKonfig().dajPostavku("OpenSkyNetwork.lozinka");
            aerodromi = myairportsFacade.findAllUser(korisnik);
        }
    }

    /**
     * @param aerodrom
     * @return vraća prazan string
     */
    public String odaberiAerodrom(Airports aerodrom) {
        odabraniVlastitiAerodrom = aerodrom.getIdent() + " - " + aerodrom.getName();
        LIQKlijent klijentLIQ = new LIQKlijent(LIQkey);
        OSKlijent klijentOSK = new OSKlijent(OSNkorisnik, OSNlozinka);
        OWMKlijent klijentOWM = new OWMKlijent(OWMkey);
        
        duzina = aerodrom.getCoordinates().split(", ")[0];
        sirina = aerodrom.getCoordinates().split(", ")[1];
        meteoPodaci = klijentOWM.getRealTimeWeather(sirina, duzina);

        Lokacija lokacija = new Lokacija();
        lokacija.setLatitude(sirina);
        lokacija.setLongitude(duzina);
        adresa = klijentLIQ.getAddress(lokacija).getNaziv();
        return "";
    }

    /**
     * Metoda dohvaća sve aerodrome pomoću CAPI upita i EJB resursa.
     *
     * @return vraća prazan string
     */
    public String dohvatiAerodrome() {
        aerodromiFiltrirani = airportsFacade.findAllName(odabraniNaziv);
        return "";
    }

    public String dodajAerodrom() {
        JsonObject jobj = new JsonObject();
        jobj.addProperty("korisnik", korisnik.getKorIme());
        jobj.addProperty("icao", odabraniAerodrom);
        String jsonPoruka = jobj.toString();
        System.out.println("Saljem web socketu poruku za dodavanje aerodroma: " + jsonPoruka);
        WebSocketKlijent.posaljiPoruku(jsonPoruka);
        aerodromi = myairportsFacade.findAllUser(korisnik);
        return "";
    }

}
