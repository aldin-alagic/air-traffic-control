/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.aalagic.rest.klijenti;

import java.text.MessageFormat;
import javax.ws.rs.ClientErrorException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.MediaType;
import org.foi.nwtis.podaci.Odgovor;

/**
 * Jersey REST client generated for REST resource:AerodromiRS_1
 * [AerodromiRS_1]<br>
 * USAGE:
 * <pre>
 *        AerodromiRS_1 client = new AerodromiRS_1();
 *        Object response = client.XXX(...);
 *        // do whatever with response
 *        client.close();
 * </pre>
 *
 * @author NWTiS_4
 */
public class AerodromiRS_1 {

    private WebTarget webTarget;
    private Client client;
    private static final String BASE_URI = "http://localhost:8084/aalagic_web_modul_2/webresources";
    private String korisnik = "";
    private String lozinka = "";

    public AerodromiRS_1(String korisnik, String lozinka) {
        this.korisnik = korisnik;
        this.lozinka = lozinka;
        client = javax.ws.rs.client.ClientBuilder.newClient();
        webTarget = client.target(BASE_URI).path("AerodromiRS_1");
    }

    public <T> T dajAerodomeKorisnika(Class<T> responseType) throws ClientErrorException {
        WebTarget resource = webTarget;
        resource = resource.path("");
        return resource.request(MediaType.APPLICATION_JSON)
                .header("korisnik", korisnik)
                .header("lozinka", lozinka)
                .get(responseType);
    }

    public  <T> T  brisiLetoveAerodromaKorisnika(Class<T> responseType, String icao) throws ClientErrorException {
        WebTarget resource = webTarget;
        resource = resource.path(MessageFormat.format("{0}/avioni", new Object[]{icao}));
        return resource.request(MediaType.APPLICATION_JSON)
                .header("korisnik", korisnik)
                .header("lozinka", lozinka)
                .delete(responseType);
    }

    public  <T> T  brisiAerodromKorisnika(Class<T> responseType, String icao) throws ClientErrorException {
        WebTarget resource = webTarget;
        resource = resource.path(MessageFormat.format("{0}", new Object[]{icao}));
        return resource.request(MediaType.APPLICATION_JSON)
                .header("korisnik", korisnik)
                .header("lozinka", lozinka)
                .delete(responseType);
    }

    public void close() {
        client.close();
    }

}
