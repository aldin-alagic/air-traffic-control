package org.foi.nwtis.aalagic.ws.klijenti;

import org.foi.nwtis.dkermek.ws.serveri.AerodromiWS_Service;
import org.foi.nwtis.dkermek.ws.serveri.AerodromiWS;
import org.foi.nwtis.dkermek.ws.serveri.StatusKorisnika;

/**
 *
 * @author Aldin Alagić
 */
public class AerodromiWS_WS {

    public String prijaviGrupu(String korisnickoIme, String lozinka) {
        String poruka;
        try { // Call Web Service Operation
            AerodromiWS_Service service = new AerodromiWS_Service();
            AerodromiWS port = service.getAerodromiWSPort();
            Boolean result = port.autenticirajGrupu(korisnickoIme, lozinka);
            result = port.registrirajGrupu(korisnickoIme, lozinka);
            if (result) {
                System.out.println("APP1-tu sam");
                poruka = "OK 20;";
            } else {
                poruka = "ERR 20;";
            }
        } catch (Exception ex) {
            poruka = "ERR 20;";
        }
        return poruka;
    }

    public String odjaviGrupu(String korisnickoIme, String lozinka) {
        String poruka;
        try { // Call Web Service Operation
            AerodromiWS_Service service = new AerodromiWS_Service();
            AerodromiWS port = service.getAerodromiWSPort();
            Boolean result = port.deregistrirajGrupu(korisnickoIme, lozinka);
            System.out.println("Result = " + result);
            if (result) {
                poruka = "OK 20;";
            } else {
                poruka = "ERR 21;";
            }
        } catch (Exception ex) {
            poruka = "ERR 21;";
        }
        return poruka;
    }

    public String aktivirajGrupu(String korisnickoIme, String lozinka) {
        String poruka;
        try { // Call Web Service Operation
            StatusKorisnika stanje = dohvatiStanjeGrupe("aalagic", "7ed9FQ");
            switch (stanje) {
                case AKTIVAN:
                    poruka = "ERR 22;";
                    break;
                case NEPOSTOJI:
                case DEREGISTRIRAN:
                    poruka = "ERR 21;";
                    break;
                case REGISTRIRAN:
                case BLOKIRAN:
                    AerodromiWS_Service service = new AerodromiWS_Service();
                    AerodromiWS port = service.getAerodromiWSPort();
                    Boolean result = port.aktivirajGrupu(korisnickoIme, lozinka);
                    if (result) {
                        poruka = "OK 20;";
                    } else {
                        poruka = "ERR Nepoznato;";
                    }
                    break;
                default:
                    poruka = "ERR Nepoznato;";
                    break;
            }
            return poruka;
        } catch (Exception ex) {
            poruka = "ERR Nepoznato;";
        }
        return poruka;
    }

    public String blokirajGrupu(String korisnickoIme, String lozinka) {
        String poruka;
        try { // Call Web Service Operation
            StatusKorisnika stanje = dohvatiStanjeGrupe(korisnickoIme, lozinka);
            switch (stanje) {
                case AKTIVAN:
                    AerodromiWS_Service service = new AerodromiWS_Service();
                    AerodromiWS port = service.getAerodromiWSPort();
                    Boolean result = port.blokirajGrupu(korisnickoIme, lozinka);
                    if (result) {
                        poruka = "OK 20;";
                    } else {
                        poruka = "ERR Nepoznato;";
                    }
                    break;
                case DEREGISTRIRAN:
                case NEPOSTOJI:
                    poruka = "ERR 21;";
                    break;
                case NEAKTIVAN:
                    poruka = "ERR 23;";
                    break;
                default:
                    poruka = "ERR Nepoznato;";
                    break;
            }
            return poruka;
        } catch (Exception ex) {
            poruka = "ERR Nepoznato;";
        }
        return poruka;
    }

    public String dodajAerodromeGrupi(String korisnickoIme, String lozinka) {
        String poruka = "";

        return poruka;
    }

    public String dajStanjeGrupe(String korisnickoIme, String lozinka) {
        String poruka;
        try { // Call Web Service Operation
            StatusKorisnika result = dohvatiStanjeGrupe("aalagic", "7ed9FQ");
            System.out.println("Result = " + result.value());
            switch (result) {
                case REGISTRIRAN:
                    poruka = "OK 21;";
                    break;
                case BLOKIRAN:
                    poruka = "OK 22;";
                    break;
                case AKTIVAN:
                    poruka = "OK 23;";
                    break;
                case PASIVAN:
                    poruka = "ERR 23;";
                    break;
                case DEREGISTRIRAN:
                    poruka = "ERR 21;";
                    break;
                case NEPOSTOJI:
                    poruka = "ERR 21;";
                    break;
                default:
                    poruka = "ERR Nepoznato;";
                    break;
            }
        } catch (Exception ex) {
            poruka = "ERR 22;";
        }
        return poruka;
    }

    private StatusKorisnika dohvatiStanjeGrupe(String korisnickoIme, String lozinka) {
        try { // Call Web Service Operation
            AerodromiWS_Service service = new AerodromiWS_Service();
            AerodromiWS port = service.getAerodromiWSPort();
            return port.dajStatusGrupe(korisnickoIme, lozinka);
        } catch (Exception ex) {
            return null;
        }
    }

}
