package org.foi.nwtis.aalagic.web.slusaci;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import org.foi.nwtis.aalagic.konfiguracije.NeispravnaKonfiguracija;
import org.foi.nwtis.aalagic.konfiguracije.NemaKonfiguracije;
import org.foi.nwtis.aalagic.konfiguracije.bp.BP_Konfiguracija;

/**
 * Klasa Slusac/Listener koja daje podatke o stanju aplikacije te upravlja
 * dretvom za preuzimanje letova aerodroma koje prate korisnici.
 *
 * @author Aldin Alagić
 */
@WebListener
public class SlusacAplikacije implements ServletContextListener {

    //@EJB
    // PreuzimanjeLetova plaa;
    /**
     * Metoda se poziva prilikom inicijalizacije konteksta i dobavlja
     * konfiguraciju za rad aplikacije. Konfiguracija sadrži podatke za rad s
     * bazom podataka, podatke za rad dretve i poziv servisa.
     *
     * @param sce - objekt iz kojeg se dobiva servlet context
     */
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        ServletContext sc = sce.getServletContext();
        String datoteka = sc.getInitParameter("konfiguracija");
        String putanja = sc.getRealPath("WEB-INF") + File.separator + datoteka;
        try {
            BP_Konfiguracija konf = new BP_Konfiguracija(putanja);
            sc.setAttribute("BP_Konfig", konf);
            //plaa = new PreuzimanjeLetovaAvionaAerodroma();
            // plaa.izvrši(konf);
        } catch (NemaKonfiguracije | NeispravnaKonfiguracija ex) {
            Logger.getLogger(SlusacAplikacije.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println("Aplikacija 4 je pokrenuta!");
    }

    /**
     * Metoda se poziva prilikom uništavanja konteksta, odnosno zaustavljanja
     * aplikacije. Metoda zaustavlja dretvu za obradu letova aerodroma.
     *
     * @param sce - objekt iz kojeg se dobiva servlet context
     */
    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        System.out.println("Aplikacija 4 je zaustavljena!");
    }

}
