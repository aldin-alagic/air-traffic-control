package org.foi.nwtis.aalagic.web.zrna;

import java.io.Serializable;
import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import lombok.Getter;
import lombok.Setter;
import org.foi.nwtis.aalagic.ejb.eb.Korisnici;
import org.foi.nwtis.aalagic.konfiguracije.Konfiguracija;
import org.foi.nwtis.aalagic.konfiguracije.bp.BP_Konfiguracija;
import org.foi.nwtis.aalagic.socket.klijent.SocketKlijent;

/**
 *
 * @author Aldin Alagić
 */
@Named(value = "prikazPosluzitelja")
@ViewScoped
public class PrikazPosluzitelja implements Serializable {

    @Inject
    private FacesContext facesContext;

    @Inject
    ServletContext context;

    @Getter
    @Setter
    String statusPosluzitelja = "NEPOZNATO";
    @Getter
    @Setter
    String statusGrupe = "NEPOZNATO";

    Korisnici korisnik;
    private static final String SVN_KORISNIK = "aalagic";
    private static final String SVN_LOZINKA = "7ed9FQ";

    @PostConstruct
    private void dohvatiPocetnoStanje() {
        HttpSession session = (HttpSession) facesContext.getExternalContext().getSession(true);
        korisnik = ((Korisnici) session.getAttribute("korisnik"));
        if (korisnik != null) {
            statusPosluzitelja = dajStanjePosluzitelja();
            statusGrupe = dajStanjeGrupe();
        }
    }

    private String posaljiPoruku(String poruka) {
        Konfiguracija konfiguracija = ((BP_Konfiguracija) context.getAttribute("BP_Konfig")).getKonfig();
        String adresa = "localhost";
        int port = Integer.parseInt(konfiguracija.dajPostavku("server.port"));
        SocketKlijent server = new SocketKlijent(adresa, port);
        return server.posaljiPoruku(poruka);
    }

    private String dajStanjePosluzitelja() {
        String poruka = String.format("KORISNIK %s; LOZINKA %s; STANJE;", korisnik.getKorIme(), korisnik.getLozinka());
        String odgovor = posaljiPoruku(poruka);
        if (odgovor.equals("OK 11;")) {
            return "Preuzima podatke za aerodrome";
        } else if (odgovor.equals("OK 12;")) {
            return "Ne preuzima podatke za aerodrome";
        }
        return "Ugašen";
    }

    private String dajStanjeGrupe() {
        String poruka = String.format("KORISNIK %s; LOZINKA %s; GRUPA STANJE;", korisnik.getKorIme(), korisnik.getLozinka());
        String odgovor = posaljiPoruku(poruka);
        System.out.println("STANJE GRUPE: " + odgovor);
        if (odgovor.equals("OK 21;")) {
            return "Grupa je registrirana";
        } else if (odgovor.equals("OK 22;")) {
            return "Grupa je blokirana";
        } else if (odgovor.equals("OK 23;")) {
            return "Grupa je aktivna";
        } else if (odgovor.equals("ERR 21;")) {
            return "Grupa ne postoji";
        } else if (odgovor.equals("ERR 23;")) {
            return "Grupa je pasivna";
        } else {
            return "NEPOZNATO";
        }
    }

    public String aktivirajPosluzitelja() {
        String poruka = String.format("KORISNIK %s; LOZINKA %s; RADI;", korisnik.getKorIme(), korisnik.getLozinka());
        String odgovor = posaljiPoruku(poruka);
        statusPosluzitelja = dajStanjePosluzitelja();
        return "";
    }

    public String pauzirajPosluzitelja() {
        String poruka = String.format("KORISNIK %s; LOZINKA %s; PAUZA;", korisnik.getKorIme(), korisnik.getLozinka());
        String odgovor = posaljiPoruku(poruka);
        statusPosluzitelja = dajStanjePosluzitelja();
        return "";
    }

    public String zaustaviPosluzitelja() {
        String poruka = String.format("KORISNIK %s; LOZINKA %s; KRAJ;", korisnik.getKorIme(), korisnik.getLozinka());
        String odgovor = posaljiPoruku(poruka);
        statusPosluzitelja = dajStanjePosluzitelja();
        return "";
    }

    public String registrirajGrupu() {
        String poruka = String.format("KORISNIK %s; LOZINKA %s; GRUPA PRIJAVI;", SVN_KORISNIK, SVN_LOZINKA);
        String odgovor = posaljiPoruku(poruka);
        System.out.println("Odgovor za grupu: " + odgovor);
        statusGrupe = dajStanjeGrupe();
        return "";
    }

    public String deregistrirajGrupu() {
        String poruka = String.format("KORISNIK %s; LOZINKA %s; GRUPA ODJAVI;", SVN_KORISNIK, SVN_LOZINKA);
        String odgovor = posaljiPoruku(poruka);
        System.out.println("Odgovor za grupu: " + odgovor);
        statusGrupe = dajStanjeGrupe();
        return "";
    }

    public String aktivirajGrupu() {
        String poruka = String.format("KORISNIK %s; LOZINKA %s; GRUPA AKTIVIRAJ;", SVN_KORISNIK, SVN_LOZINKA);
        String odgovor = posaljiPoruku(poruka);
        System.out.println("Odgovor za grupu: " + odgovor);
        statusGrupe = dajStanjeGrupe();
        return "";
    }

    public String blokirajGrupu() {
        String poruka = String.format("KORISNIK %s; LOZINKA %s; GRUPA BLOKIRAJ;", SVN_KORISNIK, SVN_LOZINKA);
        String odgovor = posaljiPoruku(poruka);
        System.out.println("Odgovor za grupu: " + odgovor);
        statusGrupe = dajStanjeGrupe();
        return "";
    }

    /**
     * Creates a new instance of PrikazPosluzitelja
     */
    public PrikazPosluzitelja() {
    }

}
