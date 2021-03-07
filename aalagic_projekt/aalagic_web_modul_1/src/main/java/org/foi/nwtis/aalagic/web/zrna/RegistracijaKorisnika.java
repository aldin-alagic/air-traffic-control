package org.foi.nwtis.aalagic.web.zrna;

import javax.inject.Named;
import java.io.Serializable;
import java.util.Date;
import javax.enterprise.context.RequestScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import lombok.Getter;
import lombok.Setter;
import org.foi.nwtis.aalagic.ejb.eb.Korisnici;
import org.foi.nwtis.aalagic.ejb.sb.KorisniciFacadeLocal;
import org.foi.nwtis.aalagic.konfiguracije.Konfiguracija;
import org.foi.nwtis.aalagic.konfiguracije.bp.BP_Konfiguracija;
import org.foi.nwtis.aalagic.socket.klijent.SocketKlijent;

/**
 * Zrno koje se koristi prilikom registriranja korisnika.
 *
 * @author Aldin Alagic
 */
@Named(value = "registracijaKorisnika")
@RequestScoped
public class RegistracijaKorisnika implements Serializable {

    @Getter
    @Setter
    private String ime = "";
    @Getter
    @Setter
    private String prezime = "";
    @Getter
    @Setter
    private String korisnickoIme = "";
    @Getter
    @Setter
    private String email = "";
    @Getter
    @Setter
    private String lozinka = "";
    @Getter
    @Setter
    private String lozinkaPotvrda = "";

    @Inject
    private GrowlPoruka growlPoruka;

    @Inject
    private FacesContext facesContext;

    @Inject
    KorisniciFacadeLocal korisnici;

    /**
     * Konstruktor klase kreira instancu klase
     */
    public RegistracijaKorisnika() {
    }

    /**
     * Metoda koja na osnovu odgovarajućih servisa dodaje korisnika u bazu
     * podataka.
     */
    public void registrirajKorisnika() {
        boolean registriran = false;
        if (unosDobar()) {
            Korisnici korisnik = kreirajKorisnika();
            registriran = dodajKorisnika(korisnik);
        } else {
            return;
        }

        if (registriran) {
            Vidljivost.registracija = false;
            Vidljivost.prijava = false;
            Vidljivost.korisnici = true;
            growlPoruka.prikaziPoruku("Registracija korisnika", "Uspješna registracija korisnika.");
        } else {
            growlPoruka.prikaziPoruku("Registracija korisnika", "Neuspješna registracija korisnika.");
        }
    }

    /**
     * Metoda koja provjerava jesu li sve vrijednosti unesene i jesu li lozinke
     * jednake.
     *
     * @return vraća true ako su podaci ispravni, odnosno false ako nisu
     * ispravni podaci
     */
    private boolean unosDobar() {
        if (ime.trim().isEmpty() || prezime.trim().isEmpty() || korisnickoIme.trim().isEmpty()
                || email.trim().isEmpty() || lozinka.trim().isEmpty()) {
            growlPoruka.prikaziPoruku("Registracija korisnika", "Svi podaci moraju biti uneseni!");
            return false;
        } else if (lozinkaPotvrda.compareTo(lozinka) != 0) {
            growlPoruka.prikaziPoruku("Registracija korisnika", "Lozinke ne odgovaraju!");
            return false;
        }
        return true;
    }

    /**
     * Metoda koja kreira instancu korisnika na osnovu podataka iz forme za
     * registraciju korisnika.
     *
     * @return objekt korisnika
     */
    private Korisnici kreirajKorisnika() {
        Korisnici k = new Korisnici(korisnickoIme, ime, prezime, lozinka, email, new Date(), new Date());
        return k;
    }

    private boolean dodajKorisnika(Korisnici korisnik) {
        Konfiguracija konfiguracija = ((BP_Konfiguracija) facesContext.getAttributes().get("BP_Konfig")).getKonfig();
        String adresa = "localhost";
        int port = Integer.parseInt(konfiguracija.dajPostavku("server.port"));
        SocketKlijent server = new SocketKlijent(adresa, port);
        String poruka = String.format("KORISNIK %s; LOZINKA %s; DODAJ;", korisnickoIme, lozinka);
        String odgovor = server.posaljiPoruku(poruka);
        if (odgovor.startsWith("OK")) {
            korisnici.create(korisnik);
            return true;
        }
        return false;
    }
    
}
