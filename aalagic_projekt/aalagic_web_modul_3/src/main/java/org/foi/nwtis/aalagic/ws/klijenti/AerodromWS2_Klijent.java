package org.foi.nwtis.aalagic.ws.klijenti;

import java.util.Date;
import java.util.List;
import org.foi.nwtis.aalagic.ws.klijenti.AerodromiWS2;
import org.foi.nwtis.aalagic.ws.klijenti.AerodromiWS2_Service;

/**
 * Klasa JAX-WS (SOAP) klijent koja služi za poziv servisa za dohvat podataka o
 * aerodromima i avionima.
 *
 * @author Aldin Alagić
 */
public class AerodromWS2_Klijent {

    public List<Aerodrom> dohvatiAerodromeKorisnika(String korisnickoIme, String lozinka) {
        AerodromiWS2_Service service = new AerodromiWS2_Service();
        AerodromiWS2 port = service.getAerodromiWS2Port();
        return port.dajAerodromeKorisnika(korisnickoIme, lozinka);
    }

    public List<AvionLeti> dohvatiLetoveAerodroma(
            String korisnickoIme,
            String lozinka,
            String aerodrom,
            Date vrijemeOd,
            Date vrijemeDo) {

        String vrijemeOdUnix = Long.toString(vrijemeOd.getTime() / 1000);
        String vrijemeDoUnix = Long.toString(vrijemeDo.getTime() / 1000);
        AerodromiWS2_Service service = new AerodromiWS2_Service();
        AerodromiWS2 port = service.getAerodromiWS2Port();
        return port.dajLetoveAerodroma(korisnickoIme, lozinka, aerodrom, vrijemeOdUnix, vrijemeDoUnix);
    }

    public List<AvionLeti> dohvatiLetoveAviona(
            String korisnickoIme,
            String lozinka,
            String avion,
            Date vrijemeOd,
            Date vrijemeDo) {

        String vrijemeOdUnix = Long.toString(vrijemeOd.getTime() / 1000);
        String vrijemeDoUnix = Long.toString(vrijemeDo.getTime() / 1000);
        AerodromiWS2_Service service = new AerodromiWS2_Service();
        AerodromiWS2 port = service.getAerodromiWS2Port();
        return port.dajLetoveAviona(korisnickoIme, lozinka, avion, vrijemeOdUnix, vrijemeDoUnix);
    }

    public List<Aerodrom> dohvatiAerodromeUdaljenost(
            String korisnickoIme,
            String lozinka,
            String aerodrom,
            int min,
            int max) {

        AerodromiWS2_Service service = new AerodromiWS2_Service();
        AerodromiWS2 port = service.getAerodromiWS2Port();
        return port.dajAerodromeIzBlizine(korisnickoIme, lozinka, aerodrom, min, max);
    }

    public double dohvatiUdaljenost(
            String korisnickoIme,
            String lozinka,
            String aerodrom1,
            String aerodrom2){

        AerodromiWS2_Service service = new AerodromiWS2_Service();
        AerodromiWS2 port = service.getAerodromiWS2Port();
        return port.dajUdaljenostAerodroma(korisnickoIme, lozinka, aerodrom1, aerodrom2);
    }
}
