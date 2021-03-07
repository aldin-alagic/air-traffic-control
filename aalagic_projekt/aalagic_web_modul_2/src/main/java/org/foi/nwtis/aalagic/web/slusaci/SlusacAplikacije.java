package org.foi.nwtis.aalagic.web.slusaci;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

/**
 * Klasa Slusac/Listener koja daje podatke o stanju aplikacije te upravlja
 * dretvom za preuzimanje letova aerodroma koje prate korisnici.
 *
 * @author Aldin Alagić
 */
@WebListener
public class SlusacAplikacije implements ServletContextListener {

    /**
     * Metoda se poziva prilikom inicijalizacije konteksta i dobavlja
     * konfiguraciju za rad aplikacije. Konfiguracija sadrži podatke za rad s
     * bazom podataka, podatke za rad dretve i poziv servisa.
     *
     * @param sce - objekt iz kojeg se dobiva servlet context
     */
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        System.out.println("Aplikacija 3 je pokrenuta!");
    }

    /**
     * Metoda se poziva prilikom uništavanja konteksta, odnosno zaustavljanja
     * aplikacije. Metoda zaustavlja dretvu za obradu letova aerodroma.
     *
     * @param sce - objekt iz kojeg se dobiva servlet context
     */
    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        System.out.println("Aplikacija 3 je zaustavljena!");
    }

}
