package org.foi.nwtis.aalagic.util;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.Getter;
import lombok.Setter;

/**
 * Klasa koja se koristi pri radu s zahtjevom korisnika i poslužitelja. Objekt
 * klase preuzima podatke o zahtjevu putem konstruktora, a na osnovu toga
 * izračunavaju se ostali podaci. Klasa se koristi pri provjeri ispravnosti
 * zahtjeva i pripremi novih zahtjeva za slanje poslužitelju. Klasa sadrži i
 * regex izraze kojima se provjerava ispravnost zahtjeva.
 *
 * @author Aldin Alagić
 * @version 1.0
 */
public class Komanda {

    private static final String KOMANDA_AUTH_RGX = "^KORISNIK ([a-zA-Z0-9-_]{3,20}); LOZINKA ([a-zA-Z0-9-#!]{3,20});$";
    private static final String KOMANDA_DODAJ_RGX = "^KORISNIK ([a-zA-Z0-9-_]{3,20}); LOZINKA ([a-zA-Z0-9-#!]{3,20}); DODAJ;$";
    private static final String KOMANDA_PAUZA_RGX = "^KORISNIK ([a-zA-Z0-9-_]{3,20}); LOZINKA ([a-zA-Z0-9-#!]{3,20}); PAUZA;$";
    private static final String KOMANDA_RADI_RGX = "^KORISNIK ([a-zA-Z0-9-_]{3,20}); LOZINKA ([a-zA-Z0-9-#!]{3,20}); RADI;$";
    private static final String KOMANDA_KRAJ_RGX = "^KORISNIK ([a-zA-Z0-9-_]{3,20}); LOZINKA ([a-zA-Z0-9-#!]{3,20}); KRAJ;$";
    private static final String KOMANDA_STANJE_RGX = "^KORISNIK ([a-zA-Z0-9-_]{3,20}); LOZINKA ([a-zA-Z0-9-#!]{3,20}); STANJE;$";

    private static final String KOMANDA_GRUPA_PRIJAVI_RGX = "^KORISNIK ([a-zA-Z0-9-_]{3,20}); LOZINKA ([a-zA-Z0-9-#!]{3,20}); GRUPA PRIJAVI;$";
    private static final String KOMANDA_GRUPA_ODJAVI_RGX = "^KORISNIK ([a-zA-Z0-9-_]{3,20}); LOZINKA ([a-zA-Z0-9-#!]{3,20}); GRUPA ODJAVI;$";
    private static final String KOMANDA_GRUPA_AKTIVIRAJ_RGX = "^KORISNIK ([a-zA-Z0-9-_]{3,20}); LOZINKA ([a-zA-Z0-9-#!]{3,20}); GRUPA AKTIVIRAJ;$";
    private static final String KOMANDA_GRUPA_BLOKIRAJ_RGX = "^KORISNIK ([a-zA-Z0-9-_]{3,20}); LOZINKA ([a-zA-Z0-9-#!]{3,20}); GRUPA BLOKIRAJ;$";
    private static final String KOMANDA_GRUPA_STANJE_RGX = "^KORISNIK ([a-zA-Z0-9-_]{3,20}); LOZINKA ([a-zA-Z0-9-#!]{3,20}); GRUPA STANJE;$";
    private static final String KOMANDA_GRUPA_AERODROMI_RGX = "^KORISNIK ([a-zA-Z0-9-_]{3,20}); LOZINKA ([a-zA-Z0-9-#!]{3,20}); GRUPA AERODROMI (([A-Z0-9-]{3,8})(, [A-Z0-9-]{3,8})*);$";

    private final String komanda;
    @Getter
    private VrstaIzraza vrsta;
    @Getter
    private String[] parametri;

    /**
     * Konstruktor objekata klase Komanda.
     *
     * @param komanda podaci o komandi
     */
    public Komanda(String komanda) {
        this.komanda = komanda;
        dohvatiVrijednosti();
    }

    public boolean ispravna() {
        return this.vrsta != VrstaIzraza.GRESKA;
    }

    private void dohvatiVrijednosti() {
        if (provjeriIzraz(komanda, KOMANDA_AUTH_RGX)) {
            vrsta = VrstaIzraza.KOMANDA_AUTH;
            parametri = dohvatiParametre(komanda, KOMANDA_AUTH_RGX);
        } else if (provjeriIzraz(komanda, KOMANDA_DODAJ_RGX)) {
            vrsta = VrstaIzraza.KOMANDA_DODAJ;
            parametri = dohvatiParametre(komanda, KOMANDA_DODAJ_RGX);
        } else if (provjeriIzraz(komanda, KOMANDA_PAUZA_RGX)) {
            vrsta = VrstaIzraza.KOMANDA_PAUZA;
            parametri = dohvatiParametre(komanda, KOMANDA_PAUZA_RGX);
        } else if (provjeriIzraz(komanda, KOMANDA_RADI_RGX)) {
            vrsta = VrstaIzraza.KOMANDA_RADI;
            parametri = dohvatiParametre(komanda, KOMANDA_RADI_RGX);
        } else if (provjeriIzraz(komanda, KOMANDA_KRAJ_RGX)) {
            vrsta = VrstaIzraza.KOMANDA_KRAJ;
            parametri = dohvatiParametre(komanda, KOMANDA_KRAJ_RGX);
        } else if (provjeriIzraz(komanda, KOMANDA_STANJE_RGX)) {
            vrsta = VrstaIzraza.KOMANDA_STANJE;
            parametri = dohvatiParametre(komanda, KOMANDA_STANJE_RGX);
        } else if (provjeriIzraz(komanda, KOMANDA_GRUPA_PRIJAVI_RGX)) {
            vrsta = VrstaIzraza.KOMANDA_GRUPA_PRIJAVI;
            parametri = dohvatiParametre(komanda, KOMANDA_GRUPA_PRIJAVI_RGX);
        } else if (provjeriIzraz(komanda, KOMANDA_GRUPA_ODJAVI_RGX)) {
            vrsta = VrstaIzraza.KOMANDA_GRUPA_ODJAVI;
            parametri = dohvatiParametre(komanda, KOMANDA_GRUPA_ODJAVI_RGX);
        } else if (provjeriIzraz(komanda, KOMANDA_GRUPA_AKTIVIRAJ_RGX)) {
            vrsta = VrstaIzraza.KOMANDA_GRUPA_AKTIVIRAJ;
            parametri = dohvatiParametre(komanda, KOMANDA_GRUPA_AKTIVIRAJ_RGX);
        } else if (provjeriIzraz(komanda, KOMANDA_GRUPA_BLOKIRAJ_RGX)) {
            vrsta = VrstaIzraza.KOMANDA_GRUPA_BLOKIRAJ;
            parametri = dohvatiParametre(komanda, KOMANDA_GRUPA_BLOKIRAJ_RGX);
        } else if (provjeriIzraz(komanda, KOMANDA_GRUPA_STANJE_RGX)) {
            vrsta = VrstaIzraza.KOMANDA_GRUPA_STANJE;
            parametri = dohvatiParametre(komanda, KOMANDA_GRUPA_STANJE_RGX);
        }  else if (provjeriIzraz(komanda, KOMANDA_GRUPA_AERODROMI_RGX)) {
            vrsta = VrstaIzraza.KOMANDA_GRUPA_AERODROMI;
            parametri = dohvatiParametre(komanda, KOMANDA_GRUPA_AERODROMI_RGX);
        } else {
            vrsta = VrstaIzraza.GRESKA;
        }
    }

    protected String[] dohvatiParametre(String izraz, String regex) {
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(izraz);
        List<String> par = new ArrayList<>();
        if (m.matches()) {
            int kraj = m.groupCount();
            for (int i = 0; i <= kraj; i++) {
                if (m.group(i) != null) {
                    par.add(m.group(i));
                }
            }
        }
        return par.stream().toArray(String[]::new);
    }

    /**
     * Funkcija testira ispravnost prosljeđene komande korištenjem proslijeđenog
     * regex izraza.
     *
     * @param komanda izraz koji se testira
     * @param regex regex izraz kojim se testira komanda
     * @return vraća true ako je izraz ispravan, u protivnom vraća false
     */
    protected Boolean provjeriIzraz(String komanda, String regex) {
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(komanda);
        return m.matches();
    }

}
