package org.foi.nwtis.aalagic.web.zrna;

import java.io.Serializable;
import javax.inject.Named;

/**
 *
 * @author Aldin Alagić
 */
@Named(value = "vidljivost")
public class Vidljivost implements Serializable {
    public static boolean odjava = false;
    public static boolean dodajAerodrom = false; 

    public static boolean isOdjava() {
        return odjava;
    }

    public static void setOdjava(boolean odjava) {
        Vidljivost.odjava = odjava;
    }

    public static boolean isDodajAerodrom() {
        return dodajAerodrom;
    }

    public static void setDodajAerodrom(boolean dodajAerodrom) {
        Vidljivost.dodajAerodrom = dodajAerodrom;
    }
 

    public Vidljivost() {
    }

}
