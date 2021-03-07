package org.foi.nwtis.aalagic.rest.serveri;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PUT;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.DELETE;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.foi.nwtis.aalagic.ejb.eb.Airplanes;
import org.foi.nwtis.aalagic.ejb.eb.Airports;
import org.foi.nwtis.aalagic.ejb.eb.Korisnici;
import org.foi.nwtis.aalagic.ejb.eb.Myairports;
import org.foi.nwtis.aalagic.ejb.sb.AirplanesFacadeLocal;
import org.foi.nwtis.aalagic.ejb.sb.AirportsFacadeLocal;
import org.foi.nwtis.aalagic.ejb.sb.KorisniciFacadeLocal;
import org.foi.nwtis.aalagic.ejb.sb.MyairportsFacadeLocal;
import org.foi.nwtis.aalagic.ejb.sb.SlanjePoruka;
import org.foi.nwtis.aalagic.ws.klijenti.Aerodrom;
import org.foi.nwtis.aalagic.ws.klijenti.AerodromiWS2;
import org.foi.nwtis.aalagic.ws.klijenti.AerodromiWS2_Service;
import org.foi.nwtis.aalagic.ws.klijenti.Lokacija;
import org.foi.nwtis.aalagic.wsocket.klijenti.WebSocketKlijent;
import org.foi.nwtis.podaci.Odgovor;

/**
 * REST Web Service
 *
 * @author Aldin Alagić
 */
@Path("AerodromiRS_1")
@RequestScoped
public class AerodromiRS_1 {

    private static int redniBrojPoruke = 0;

    @Context
    private UriInfo context;

    @Inject
    AerodromiWS2_Service service;

    @Inject
    KorisniciFacadeLocal korisniciFacadeLocal;

    @Inject
    MyairportsFacadeLocal myairportsFacadeLocal;

    @Inject
    AirportsFacadeLocal airportsFacadeLocal;

    @Inject
    AirplanesFacadeLocal airplanesFacadeLocal;

    @Inject
    SlanjePoruka slanjePoruka;

    private Korisnici autenticirajKorisnika(String korisnickoIme, String lozinka) {
        Korisnici korisnik = korisniciFacadeLocal.find(korisnickoIme);
        if (korisnik != null && korisnik.getLozinka().equals(lozinka)) {
            return korisnik;
        }
        return null;
    }

    /**
     * Creates a new instance of AerodromiRest
     */
    public AerodromiRS_1() {
    }

    /**
     * Operacija dohvaća podatke o aerodromima koje korisnici prate.
     *
     * OPERACIJA 1
     *
     * @param korisnik - korisničko ime korisnika koji vrši upit
     * @param lozinka - lozinka korisnika koji vrši upit
     * @return vraća odgovor s aerodromima koje prate korisnici
     */
    @GET
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    public Response dajAerodomeKorisnika(
            @HeaderParam("korisnik") String korisnik,
            @HeaderParam("lozinka") String lozinka) {

        List<Aerodrom> aerodromi = new ArrayList<>();
        Odgovor odgovor = new Odgovor();
        try { // Call Web Service Operation
            AerodromiWS2 port = service.getAerodromiWS2Port();
            aerodromi = port.dajAerodromeKorisnika(korisnik, lozinka);

            if (aerodromi != null && autenticirajKorisnika(korisnik, lozinka) != null) {
                odgovor.setStatus("10");
                odgovor.setPoruka("OK");
            } else {
                odgovor.setStatus("40");
                odgovor.setPoruka("Korisnik nije autoriziran");
            }

        } catch (Exception ex) {
            odgovor.setStatus("40");
            odgovor.setPoruka("Greška pri dohvatu aerodroma korisnika");
        }
        odgovor.setOdgovor(aerodromi.toArray());
        return Response.ok(odgovor).status(200).build();
    }

    /**
     * Operacija dodaje aerodrom s danom icao oznakom korisniku.
     *
     * OPERACIJA 2
     *
     * @param korisnik - korisničko ime korisnika koji vrši upit
     * @param lozinka - lozinka korisnika koji vrši upit
     * @param icao - oznaka aerodroma koji se dodaje korisniku
     * @return vraća true akoje dodan aerodrom, odnosno false ako nije dodan
     */
    @POST
    @Produces({MediaType.APPLICATION_JSON})
    public Response dodajAerodromKorisnika(
            @HeaderParam("korisnik") String korisnik,
            @HeaderParam("lozinka") String lozinka, String icao) {

        JsonObject jobj = new Gson().fromJson(icao, JsonObject.class);
        jobj.addProperty("korisnik", korisnik);
        jobj.addProperty("lozinka", lozinka);
        String jsonPoruka = jobj.toString();
        System.out.println("Saljem web socketu poruku: " + jsonPoruka);

        boolean dodan = WebSocketKlijent.posaljiPoruku(jsonPoruka);
        Odgovor odgovor = new Odgovor();
        Boolean o[] = new Boolean[1];

        if (dodan) {
            odgovor.setStatus("10");
            odgovor.setPoruka("OK");
            o[0] = true;
        } else {
            odgovor.setStatus("40");
            odgovor.setPoruka("Greška pri dodavanju aerodroma");
        }

        odgovor.setOdgovor(o);
        return Response.ok(odgovor).status(200).build();
    }

    /**
     * Operacija vraća pojedini aerodrom kojeg prati korisnik.
     *
     * OPERACIJA 3
     *
     * @param korisnik - korisničko ime korisnika koji vrši upit
     * @param lozinka - lozinka korisnika koji vrši upit
     * @param icao - oznaka aerodroma koji se dohvaća
     * @return
     */
    @Path("{icao}")
    @GET
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    public Response dajAerodomKorisnika(
            @HeaderParam("korisnik") String korisnik,
            @HeaderParam("lozinka") String lozinka,
            @PathParam("icao") String icao) {

        Odgovor odgovor = new Odgovor();
        Korisnici k = autenticirajKorisnika(korisnik, lozinka);
        List<Aerodrom> aerodromi = new ArrayList<>();
        if (k != null) {
            Aerodrom aerodrom = dajAerodrom(k, icao);
            if (aerodrom != null) {
                aerodromi.add(aerodrom);
                odgovor.setStatus("10");
                odgovor.setPoruka("OK");
            } else {
                odgovor.setStatus("40");
                odgovor.setPoruka("Korisnik ne promatra aerodrom");
            }
        } else {
            odgovor.setStatus("40");
            odgovor.setPoruka("Korisnik nije autoriziran");
        }

        odgovor.setOdgovor(aerodromi.toArray());
        return Response.ok(odgovor).status(200).build();
    }

    /**
     * Operacija briše pojedini aerodrom kojeg prati korisnik.
     *
     * OPERACIJA 4
     *
     * @param korisnik - korisničko ime korisnika koji vrši upit
     * @param lozinka - lozinka korisnika koji vrši upit
     * @param icao - oznaka aerodroma koji se briše
     * @return
     */
    @Path("{icao}")
    @DELETE
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    public Response brisiAerodromKorisnika(
            @HeaderParam("korisnik") String korisnik,
            @HeaderParam("lozinka") String lozinka,
            @PathParam("icao") String icao) {

        Odgovor odgovor = new Odgovor();
        Korisnici k = autenticirajKorisnika(korisnik, lozinka);
        List<Aerodrom> aerodromi = new ArrayList<>();
        if (k != null) {
            Aerodrom aerodrom = dajAerodrom(k, icao);
            if (aerodrom != null) {
                obrisiAerodrom(k, icao);
                odgovor.setStatus("10");
                odgovor.setPoruka("OK"); //TODO NAPRAVITI AKO IMA LETOVE DA BUDE PROVJERA AKO IMA LETOVE
            } else {
                odgovor.setStatus("40");
                odgovor.setPoruka("Korisnik ne promatra aerodrom");
            }
        } else {
            odgovor.setStatus("40");
            odgovor.setPoruka("Korisnik nije autoriziran");
        }

        odgovor.setOdgovor(aerodromi.toArray());
        return Response.ok(odgovor).status(200).build();
    }

    /**
     * Operacija briše letove pojedinog aerodroma kojeg prati korisnik.
     *
     * OPERACIJA 5
     *
     * @param korisnik - korisničko ime korisnika koji vrši upit
     * @param lozinka - lozinka korisnika koji vrši upit
     * @param icao - oznaka aerodroma čiji se letovi brišu
     * @return
     */
    @Path("{icao}/avioni")
    @DELETE
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    public Response brisiLetoveAerodromaKorisnika(
            @HeaderParam("korisnik") String korisnik,
            @HeaderParam("lozinka") String lozinka,
            @PathParam("icao") String icao) {

        Odgovor odgovor = new Odgovor();
        Korisnici k = autenticirajKorisnika(korisnik, lozinka);
        List<Aerodrom> aerodromi = new ArrayList<>();
        if (k != null) {
            Aerodrom aerodrom = dajAerodrom(k, icao);
            if (aerodrom != null) {
                obrisiLetoveAerodroma(k, icao);
                odgovor.setStatus("10");
                odgovor.setPoruka("OK"); //TODO NAPRAVITI AKO IMA LETOVE DA BUDE PROVJERA AKO IMA LETOVE
            } else {
                odgovor.setStatus("40");
                odgovor.setPoruka("Korisnik ne promatra aerodrom");
            }
        } else {
            odgovor.setStatus("40");
            odgovor.setPoruka("Korisnik nije autoriziran");
        }

        odgovor.setOdgovor(aerodromi.toArray());
        return Response.ok(odgovor).status(200).build();
    }

    /**
     * Operacija koja dohvaća aerodrome čiji naziv je sličan prosljeđenom
     * nazivu, odnosno državi ako je prosljeđena država. U slučaju da su oba
     * parametra prazna dohvaćaju se svi aerodromi.
     *
     * OPERACIJA 6
     *
     * @param korisnik - korisničko ime korisnika koji vrši upit
     * @param lozinka - lozinka korisnika koji vrši upit
     * @param naziv naziv aerodroma po kojem se pretražuju aerodromi
     * @param drzava država aerodroma po kojoj se pretražuju aerodromi
     * @return
     */
    @Path("/svi")
    @GET
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    public Response dajAerodrome(
            @HeaderParam("korisnik") String korisnik,
            @HeaderParam("lozinka") String lozinka,
            @HeaderParam("naziv") String naziv,
            @HeaderParam("drzava") String drzava) {

        Odgovor odgovor = new Odgovor();
        Korisnici k = autenticirajKorisnika(korisnik, lozinka);
        List<Aerodrom> aerodromi = new ArrayList<>();
        if (k != null) {
            AerodromiWS2 port = service.getAerodromiWS2Port();
            if (!naziv.isEmpty()) {
                aerodromi = port.dajAerodromeNaziv(korisnik, lozinka, naziv);
                odgovor.setStatus("10");
                odgovor.setPoruka("OK");
            } else if (!drzava.isEmpty()) {
                aerodromi = port.dajAerodromeDrzava(korisnik, lozinka, drzava);
                odgovor.setStatus("10");
                odgovor.setPoruka("OK");
            } else if (naziv.isEmpty() && drzava.isEmpty()) {
                naziv = "%";
                aerodromi = port.dajAerodromeNaziv(korisnik, lozinka, naziv);
                odgovor.setStatus("10");
                odgovor.setPoruka("OK");
            }
        } else {
            odgovor.setStatus("40");
            odgovor.setPoruka("Korisnik nije autoriziran");
        }

        odgovor.setOdgovor(aerodromi.toArray());
        return Response.ok(odgovor).status(200).build();
    }

    /**
     * Operacija koja šalje komandu u JMS red poruka NWTiS_aalagic_2.
     *
     * OPERACIJA 7
     *
     * @param korisnik korisničko ime korisnika koji vrši upit
     * @param lozinka lozinka korisnika koji vrši upit
     * @param komanda komanda koja se šalje
     * @param vrijeme vrijeme komande
     * @return
     */
    @Path("/komande")
    @POST
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    public Response posaljiKomandu(
            @HeaderParam("korisnik") String korisnik,
            @HeaderParam("lozinka") String lozinka,
            @HeaderParam("komanda") String komanda,
            @HeaderParam("vrijeme") String vrijeme) {

        Odgovor odgovor = new Odgovor();
        Korisnici k = autenticirajKorisnika(korisnik, lozinka);
        List<Aerodrom> aerodromi = new ArrayList<>();
        if (k != null) {
            // TODO PROVJERI ISPRAVNOST ZAHTJEVA
            saljiJMSPoruku(k, komanda, vrijeme);
            odgovor.setStatus("10");
            odgovor.setPoruka("OK");
        } else {
            odgovor.setStatus("40");
            odgovor.setPoruka("Korisnik nije autoriziran");
        }

        odgovor.setOdgovor(aerodromi.toArray());
        return Response.ok(odgovor).status(200).build();
    }

    private Aerodrom dajAerodrom(Korisnici korisnik, String icao) {
        Airports airport = airportsFacadeLocal.find(icao);
        List<Myairports> aerodromiKorisnika = myairportsFacadeLocal.findOneUser(korisnik, airport);
        Aerodrom aerodrom = null;

        if (aerodromiKorisnika != null && !aerodromiKorisnika.isEmpty()) {
            String[] koordinate = airport.getCoordinates().split(", ");
            Lokacija lokacija = new Lokacija();
            lokacija.setLongitude(koordinate[0]);
            lokacija.setLatitude(koordinate[1]);

            aerodrom = new Aerodrom();
            aerodrom.setIcao(airport.getIdent());
            aerodrom.setDrzava(airport.getIsoCountry());
            aerodrom.setLokacija(lokacija);
            aerodrom.setNaziv(airport.getName());
        }
        return aerodrom;
    }

    private void obrisiAerodrom(Korisnici korisnik, String icao) {
        Airports airport = airportsFacadeLocal.find(icao);
        List<Myairports> aerodromiKorisnika = myairportsFacadeLocal.findOneUser(korisnik, airport);
        if (aerodromiKorisnika != null && !aerodromiKorisnika.isEmpty()) {
            List<Myairports> myAirports = korisnik.getMyairportsList();
            myairportsFacadeLocal.remove(aerodromiKorisnika.get(0));
        }
    }

    private void obrisiLetoveAerodroma(Korisnici korisnik, String icao) {
        Airports airport = airportsFacadeLocal.find(icao);
        List<Myairports> aerodromiKorisnika = myairportsFacadeLocal.findOneUser(korisnik, airport);
        if (aerodromiKorisnika != null && !aerodromiKorisnika.isEmpty()) {
            List<Airplanes> letovi = airport.getAirplanesList();
            letovi.forEach((let) -> {
                airplanesFacadeLocal.remove(let);
            });
        }
    }

    private void saljiJMSPoruku(Korisnici korisnik, String komanda, String vp) {
        SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy hh.mm.ss.SSSS");
        Date vrijemeSlanja = new Date();
        Date vrijemePrijema;
        
        try {
            vrijemePrijema = formatter.parse(vp);
        } catch (ParseException ex) {
            vrijemePrijema = new Date();
            System.out.println("Vrijeme prijema nije dobro");
        }
        
        JsonObject jsonPoruka = new JsonObject();
        jsonPoruka.addProperty("id", redniBrojPoruke++);
        jsonPoruka.addProperty("korisnik", korisnik.getKorIme());
        jsonPoruka.addProperty("komanda", komanda);
        jsonPoruka.addProperty("vrijemePrijema", formatter.format(vrijemePrijema));
        jsonPoruka.addProperty("vrijemeSlanja", formatter.format(vrijemeSlanja));

        int redPoruka = 2;
        slanjePoruka.posalji(redPoruka, jsonPoruka);
    }
}
//TODO METODA KOJA VRACA ODGOVOR AUZTORIZACIJE
//TODO METODA KOJA DOHVAĆA KORISNIKA
