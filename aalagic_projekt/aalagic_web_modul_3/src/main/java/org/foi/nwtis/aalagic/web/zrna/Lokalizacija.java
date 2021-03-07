package org.foi.nwtis.aalagic.web.zrna;

import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import java.util.Locale;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import lombok.Getter;
import lombok.Setter;

/**
 * Zrno koje omogućava lokalizaciju stranica na hrvatskom, engleskom i njemačkom
 * jeziku.
 *
 * @author Aldin Alagić
 */
@Named(value = "lokalizacija")
@SessionScoped
public class Lokalizacija implements Serializable {

    @Getter
    @Setter
    private String jezik = "hr";

    @Inject
    private FacesContext facesContext;
    @Inject
    private GrowlPoruka growlPoruka;

    /**
     * Konstruktor klase kreira instancu klase
     */
    public Lokalizacija() {
    }

    /**
     * Metoda koja postavlja lokalizaciju stranice na odabrani jezik.
     * 
     * @return
     */
    public Object odaberiJezik() {
        facesContext.getViewRoot().setLocale(new Locale(jezik));
        growlPoruka.prikaziPoruku("Promijena jezika", "Uspješno promijenjen jezik stranice.");
        return "";
    }
}
