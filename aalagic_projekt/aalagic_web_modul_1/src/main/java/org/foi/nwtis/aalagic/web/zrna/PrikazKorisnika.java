package org.foi.nwtis.aalagic.web.zrna;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.servlet.ServletContext;
import lombok.Getter;
import org.foi.nwtis.aalagic.ejb.eb.Korisnici;
import org.foi.nwtis.aalagic.ejb.sb.KorisniciFacadeLocal;

/**
 * Zrno koje se koristi prilikom dohvata i azuriranja podataka korisnika,
 * aerodroma, letova na sučeljima web stranice. Zrno podatke dohvaća preko EJB
 * resursa i WebSocketa poslužitelja za meteorološke informacije.
 *
 * @author Aldin Alagic
 */
@Named(value = "prikazKorisnika")
@ViewScoped
public class PrikazKorisnika implements Serializable {

    @EJB
    KorisniciFacadeLocal korisniciFacade;

    @Inject
    ServletContext context;

    @Getter
    List<Korisnici> korisnici = new ArrayList<>();

    /**
     * Konstruktor objekata klase.
     */
    public PrikazKorisnika() {
    }

    /**
     * Metoda koja se poziva nakon otvaranja stranice i učitavanja ovisnosti, a
     * prije poziva prve poslovne metode. Dohvaća api ključ za pristup servisima
     * s meterološkim podacima. Dohvaća preko EJB resursa korisnike.
     */
    @PostConstruct
    private void dohvatiKorisnike() {
        korisnici = korisniciFacade.findAll();
    }
}
