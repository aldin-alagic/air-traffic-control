package org.foi.nwtis.aalagic.ejb.sb;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import javax.inject.Inject;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import org.foi.nwtis.aalagic.ejb.eb.Airports;
import org.foi.nwtis.aalagic.ejb.eb.Korisnici;
import org.foi.nwtis.aalagic.ejb.eb.Myairports;

/**
 * Klasa koja ima ulogu poslužitelja koji dohvaća podatke o aerodromima i
 * korisnicima. Klasa ima endpoint "/posluzitelj". Podatke dohvaća preko EJB
 * resursa. Komunikacija se obavlja preko Web socketa.
 *
 * @author Aldin Alagić
 */
@ServerEndpoint("/aerodromi")
public class PosluziteljAerodroma {

    static int redniBrojPoruke = 1;
    static Queue<Session> queue = new ConcurrentLinkedQueue<>();

    @Inject
    MyairportsFacadeLocal myairportsFacade;

    @Inject
    KorisniciFacadeLocal korisniciFacade;

    @Inject
    AirportsFacadeLocal airportsFacade;

    @Inject
    SlanjePoruka slanjePoruka;

    /**
     * Metoda kojom se šalje poruka klijentu.
     *
     * @param poruka sadržaj koji se šalje klijentu
     * @param session veza s klijentom kojem se šalje poruka
     */
    public static void posalji(String poruka, Session session) {
        System.out.println("Server šalje jednom: " + poruka);
        try {
            session.getBasicRemote().sendText(poruka);
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }

    public static void posaljiSvima(int brojPromatranihAerodroma) {
        String odgovor = String.format("BrojPromatranihAerodroma:%d", brojPromatranihAerodroma);
        System.out.println("Server šalje svima: " + odgovor);
        try {
            for (Session session : queue) {
                session.getBasicRemote().sendText(odgovor);
            }
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }

    /**
     * Metoda koja se poziva kad poslužitelj primi poruku od klijenta dodaje
     * aerodrom u njegove promatrane aerodrome. Poruka sadrži podatke o zapisu
     * koji se dodaje u bazu podataka pomoću EJB resursa.
     *
     * @param poruka sadržaj koji se vraća klijentu
     */
    @OnMessage
    public void onMessage(String poruka) {
        System.out.println("Server primio: " + poruka);
        JsonObject jobj = new Gson().fromJson(poruka, JsonObject.class);
        String icao = jobj.get("icao").getAsString();
        String korisnickoIme = jobj.get("korisnik").getAsString();
        obradiPoruku(icao, korisnickoIme);
    }

    /**
     * Metoda koja se poziva prilikom stvaranja veze s klijentom. Metoda šalje
     * broj promatranih aerodroma klijentu.
     *
     * @param session veza s klijentom koja se otvara
     */
    @OnOpen
    public void openConnection(Session session) {
        queue.add(session);
        System.out.println("Otvorena veza.");
    }

    /**
     * Metoda koja se poziva prilikom zatvaranja veze s klijentom.
     *
     * @param session veza s klijentom koja se zatvara
     */
    @OnClose
    public void closedConnection(Session session) {
        queue.remove(session);
        System.out.println("Zatvorena veza.");
    }

    /**
     * Metoda koja se poziva prilikom pojave greške u vezi s klijentom.
     *
     * @param session veza klijenta koja uzrokuje grešku
     * @param t podaci o greški
     */
    @OnError
    public void error(Session session, Throwable t) {
        queue.remove(session);
        System.out.println("Zatvorena veza.");
    }

    private void obradiPoruku(String icao, String korisnickoIme) {
        Airports aerodrom = airportsFacade.find(icao);
        Korisnici korisnik = korisniciFacade.find(korisnickoIme);
        Myairports aerodromKorisnika = new Myairports();
        aerodromKorisnika.setIdent(aerodrom);
        aerodromKorisnika.setUsername(korisnik);
        aerodromKorisnika.setStored(new Date());
        myairportsFacade.create(aerodromKorisnika);
        saljiJMSPoruku(korisnik, aerodrom);
        System.out.println("Primljen i dodan aerodrom s web socketa!");
    }

    private void saljiJMSPoruku(Korisnici korisnik, Airports aerodrom) {
        Date datum = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy hh.mm.ss.SSSS");

        JsonObject jsonPoruka = new JsonObject();
        jsonPoruka.addProperty("id", redniBrojPoruke++);
        jsonPoruka.addProperty("korisnik", korisnik.getKorIme());
        jsonPoruka.addProperty("aerodrom", aerodrom.getIdent());
        jsonPoruka.addProperty("vrijeme", formatter.format(datum));

        int redPoruka = 1;
        slanjePoruka.posalji(redPoruka, jsonPoruka);
    }

}
