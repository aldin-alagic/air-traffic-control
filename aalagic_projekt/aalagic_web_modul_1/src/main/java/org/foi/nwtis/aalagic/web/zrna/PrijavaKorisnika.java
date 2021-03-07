package org.foi.nwtis.aalagic.web.zrna;

import java.io.IOException;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.servlet.http.HttpSession;
import lombok.Getter;
import lombok.Setter;
import org.foi.nwtis.aalagic.ejb.eb.Korisnici;
import org.foi.nwtis.aalagic.ejb.sb.KorisniciFacadeLocal;

/**
 * Zrno koje se koristi prilikom prijave korisnika.
 *
 * @author Aldin Alagic
 */
@Named(value = "prijavaKorisnika")
@SessionScoped
public class PrijavaKorisnika implements Serializable {

    @Getter
    @Setter
    private String korisnickoIme = "";
    @Getter
    @Setter
    private String lozinka = "";

    @Inject
    private GrowlPoruka growlPoruka;

    @Inject
    private FacesContext facesContext;

    @Inject
    KorisniciFacadeLocal korisniciFacade;

    /**
     * Konstruktor klase kreira instancu klase.
     */
    public PrijavaKorisnika() {
    }

    /**
     * Metoda koja na osnovu odgovarajućih servisa i podataka iz forme
     * prijavljuje korisnika.
     */
    public void prijavaKorisnika() {
        boolean prijavljen = false;
        if (!korisnickoIme.trim().isEmpty() && !lozinka.trim().isEmpty()) {
            Korisnici korisnik = autenticirajKorisnika(korisnickoIme, lozinka);
            if (korisnik != null) {
                HttpSession session = (HttpSession) facesContext.getExternalContext().getSession(true);
                session.setAttribute("korisnik", korisnik);
                prijavljen = true;
            }
        } else {
            growlPoruka.prikaziPoruku("Prijava korisnika", "Svi podaci moraju biti uneseni!");
            return;
        }

        if (prijavljen) {
            Vidljivost.registracija = false;
            Vidljivost.prijava = false;
            Vidljivost.korisnici = true;
            growlPoruka.prikaziPoruku("Prijava korisnika", "Uspješna prijava korisnika.");
        } else {
            growlPoruka.prikaziPoruku("Prijava korisnika", "Neuspješna prijava korisnika.");
        }
    }

    /**
     * Metoda koja odjavljuje korisnika brisanjem varijable sesije korisnik.
     */
    public void odjavaKorisnika() {
        HttpSession session = (HttpSession) facesContext.getExternalContext().getSession(false);
        session.removeAttribute("korisnik");
        try {
            Vidljivost.registracija = true;
            Vidljivost.prijava = true;
            Vidljivost.korisnici = false;
            growlPoruka.prikaziPoruku("Odjava korisnika", "Korisnik uspješno odjavljen.");
            facesContext.getExternalContext().redirect("pogled1.xhtml");
        } catch (IOException ex) {
            growlPoruka.prikaziPoruku("Odjava korisnika", "Greška prilikom odjave korisnika.");
            Logger.getLogger(PrijavaKorisnika.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private Korisnici autenticirajKorisnika(String korisnickoIme, String lozinka) {
        Korisnici k = korisniciFacade.find(korisnickoIme);
        if (k != null && k.getLozinka().equals(lozinka)) {
            return k;
        }
        return null;
    }
    
}
