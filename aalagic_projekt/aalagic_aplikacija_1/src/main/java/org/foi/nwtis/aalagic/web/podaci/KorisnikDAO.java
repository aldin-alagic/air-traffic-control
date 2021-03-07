package org.foi.nwtis.aalagic.web.podaci;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.foi.nwtis.aalagic.konfiguracije.bp.BP_Konfiguracija;

/**
 * Klasa sadrži metode za dohvat i izmjenu podataka o korisnicima iz baze
 * podataka.
 *
 *
 * @author Aldin Alagić
 */
public class KorisnikDAO {

    /**
     * Metoda dohvaća korisnika koji se prijavljuje iz tablice KORISNICI.
     *
     * @param korisnickoIme
     * @param lozinka - lozinka korisnika koji se dohvaća
     * @param bpk - konfiguracija koja sadrži korisničko ime i lozinku za
     * pristup bazi podataka
     * @return vraća korisnika koji se prijavljuje
     */
    public Boolean dohvatiKorisnika(String korisnickoIme, String lozinka, BP_Konfiguracija bpk) {
        String url = bpk.getServerDatabase() + bpk.getUserDatabase();
        String bpkorisnik = bpk.getUserUsername();
        String bplozinka = bpk.getUserPassword();
        String upit = "SELECT * FROM korisnici WHERE KORISNICKO_IME = '" + korisnickoIme + "' AND LOZINKA = '" + lozinka + "'";
        System.out.println("Spojen na bazu");
        try {
            Class.forName(bpk.getDriverDatabase(url));

            try (Connection con = DriverManager.getConnection(url, bpkorisnik, bplozinka);
                    Statement s = con.createStatement();
                    ResultSet rs = s.executeQuery(upit)) {

                while (rs.next()) {
                    System.out.println("Postoji korisnik");
                    return true;
                }
            } catch (SQLException ex) {
                Logger.getLogger(KorisnikDAO.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(KorisnikDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }
    
        /**
     * Metoda dodaje korisnika koji se registrirao u tablicu KORISNICI.
     * 
     * @param korisnickoIme
     * @param lozinka
     * @param bpk - konfiguracija koja sadrži korisničko ime i lozinku za
     * pristup bazi podataka
     * @return vraća true ako je dodan korisnik, odnosno false ako nije dodan.
     */
    public boolean dodajKorisnika(String korisnickoIme, String lozinka, BP_Konfiguracija bpk) {
        String url = bpk.getServerDatabase() + bpk.getUserDatabase();
        String bpkorisnik = bpk.getUserUsername();
        String bplozinka = bpk.getUserPassword();
        String upit = "INSERT INTO korisnici (KORISNICKO_IME, LOZINKA) VALUES ("
                + "'" + korisnickoIme + "', '" + lozinka + "')";

        try {
            Class.forName(bpk.getDriverDatabase(url));

            try (
                    Connection con = DriverManager.getConnection(url, bpkorisnik, bplozinka);
                    Statement s = con.createStatement()) {

                int brojAzuriranja = s.executeUpdate(upit);
                return brojAzuriranja == 1;

            } catch (SQLException ex) {
                Logger.getLogger(KorisnikDAO.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(KorisnikDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }
}
