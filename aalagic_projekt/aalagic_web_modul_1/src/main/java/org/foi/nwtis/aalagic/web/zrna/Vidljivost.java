package org.foi.nwtis.aalagic.web.zrna;

import java.io.Serializable;
import javax.inject.Named;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author Aldin Alagić
 */
@Named(value = "vidljivost")
public class Vidljivost implements Serializable {
    public  static boolean  prijava = true;
    public  static boolean registracija = true;
    public  static boolean korisnici = false;

    public static boolean isPrijava() {
        return prijava;
    }

    public static void setPrijava(boolean prijava) {
        Vidljivost.prijava = prijava;
    }

    public static boolean isRegistracija() {
        return registracija;
    }

    public static void setRegistracija(boolean registracija) {
        Vidljivost.registracija = registracija;
    }

    public static boolean isKorisnici() {
        return korisnici;
    }

    public static void setKorisnici(boolean korisnici) {
        Vidljivost.korisnici = korisnici;
    }

    public Vidljivost() {
    }

}
