package org.foi.nwtis.aalagic.web.zrna;

import javax.inject.Named;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;

/**
 * Klasa koja omogućava postavljanje poruke za growl ui komponentu.
 * 
 * @author Aldin Alagić
 */
@Named
@RequestScoped
public class GrowlPoruka {

    @Inject
    private FacesContext facesContext;
    
    /**
     * Metoda kojom se postavlja i poziva growl poruka.
     * 
     * @param naslov - tekst naslova poruke
     * @param poruka - tekst sadržaja poruke
     */
    public void prikaziPoruku(String naslov, String poruka) {
        facesContext.addMessage(null, new FacesMessage(naslov, poruka));
    }
}
