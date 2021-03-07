package org.foi.nwtis.aalagic.util;

/**
 *
 * @author aalagic
 */
public class Koordinata {

    public double geoSirina;
    public double geoDuzina;

    public Koordinata() {

    }

    public static double izracunajUdaljenost(Koordinata polaziste, Koordinata odrediste) {
        int radijusZemlje = 6371; 
        double dSirina = Math.toRadians(odrediste.geoSirina - polaziste.geoSirina);
        double dDuzina = Math.toRadians(odrediste.geoDuzina - polaziste.geoDuzina );
        double a = Math.sin(dSirina / 2) * Math.sin(dSirina / 2)
                + Math.cos(Math.toRadians(polaziste.geoSirina)) * Math.cos(Math.toRadians(odrediste.geoSirina))
                * Math.sin(dDuzina / 2) * Math.sin(dDuzina / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return radijusZemlje * c;
    }

   /* public static int izracunajUdaljenost(Koordinata polaziste, Koordinata odrediste) {
        double d = Math.sqrt(Math.pow((odrediste.geoSirina - polaziste.geoSirina), 2)
                + Math.pow((odrediste.geoDuzina - polaziste.geoDuzina), 2));
        return (int) d;
    }*/

    public static Koordinata dohvatiKoordinatu(String podaci) {
        String[] k = podaci.split(", ");
        double sirina = Double.parseDouble(k[1]);
        double duzina = Double.parseDouble(k[0]);
        return new Koordinata(sirina, duzina);
    }

    public Koordinata(double geoSirina, double geoDuzina) {
        this.geoDuzina = geoDuzina;
        this.geoSirina = geoSirina;
    }

    @Override
    public String toString() {

        return "Dužina: " + zaokruzi(this.geoDuzina) + ", Širina: " + zaokruzi(this.geoSirina);
    }

    @Override
    public boolean equals(Object obj) {
        Koordinata o = null;
        if (obj instanceof Koordinata) {
            o = (Koordinata) obj;
        } else {
            return false;
        }

        if (zaokruzi(this.geoDuzina) == zaokruzi(o.geoDuzina)
                && zaokruzi(this.geoSirina) == zaokruzi(o.geoSirina)) {
            return true;
        } else {
            return false;
        }
    }

    private double zaokruzi(double broj) {
        return Math.round(broj * 1000000) / 1000000d;
    }
}
