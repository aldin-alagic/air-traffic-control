package org.foi.nwtis.aalagic.web.dretve;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.foi.nwtis.aalagic.konfiguracije.Konfiguracija;
import org.foi.nwtis.aalagic.konfiguracije.bp.BP_Konfiguracija;
import org.foi.nwtis.aalagic.web.podaci.KorisnikDAO;
import org.foi.nwtis.aalagic.util.Komanda;
import org.foi.nwtis.aalagic.util.VrstaIzraza;
import org.foi.nwtis.aalagic.ws.klijenti.AerodromiWS_WS;

/* *
 * @author Aldin Alagic
 */
public class ServerGrupe extends Thread {

    private final BP_Konfiguracija bpk;
    private final Konfiguracija konf;
    int port;
    int maxZahtjeva;
    ServerSocket server;
    private Boolean aktivna = true;
    private Boolean stanjePreuzimanja = true;

    /**
     * Konstruktor klase koji prima konfiguraciju koja sadrži podatke potrebne
     * za rad dretve: - trajanje ciklusa dretve - trajanje pauze između
     * preuzimanja letova - pocetni datum preuzimanja - završni datum
     * preuzimanja - status preuzimanja
     *
     * @param bkonf - objekt koji sadrži postavke za rad dretve
     */
    public ServerGrupe(BP_Konfiguracija bkonf) {
        this.bpk = bkonf;
        this.konf = bkonf.getKonfig();
        this.port = Integer.parseInt(konf.dajPostavku("server.port"));
        this.maxZahtjeva = Integer.parseInt(konf.dajPostavku("server.maxZahtjeva"));
    }

    @Override
    public synchronized void start() {
        super.start();
    }

    @Override
    public void interrupt() {
        try {
            server.close();
        } catch (IOException ex) {
            Logger.getLogger(ServerGrupe.class.getName()).log(Level.SEVERE, null, ex);
        }
        super.interrupt();
    }

    @Override
    public void run() {
        kreirajSocket();
        while (aktivna) {
            try {
                obradiZahtjev();
                sleep(1000);
            } catch (InterruptedException ex) {
                System.out.println("Dretva prekinuta!");
                aktivna = false;
            }
        }

        System.out.println("Završavam radom...");
        stanjePreuzimanja = false;
        try {
            server.close();
        } catch (IOException ex) {
            Logger.getLogger(ServerGrupe.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void kreirajSocket() {
        try {
            server = new ServerSocket(port, maxZahtjeva);
        } catch (IOException e) {
            System.out.println("Greška pri stvaranju utičnice");
        }
    }

    private boolean autenticiraj(String korisnickoIme, String lozinka) {
        KorisnikDAO korisnikDAO = new KorisnikDAO();
        return korisnikDAO.dohvatiKorisnika(korisnickoIme, lozinka, bpk);
    }

    /**
     * Funkcija otvara mrežnu utičnicu te čeka i obrađuje zahtjeve poslužitelja
     * aviona.Funkcija u petlji čeka da stigne zahtjev poslužitelja avion. Nakon
     * što je zahtjev stigao kreira se dretva i započinje se obrada zahtjeva.
     *
     * Ako se desi greška pri čitanju ili pisanju podataka veze baca se iznimka
     * IOException.
     *
     */
    public void obradiZahtjev() {
        try {
            System.out.println("Cekam vezu:");
            Socket veza = server.accept();
            System.out.println("Ostvarena veza!");
            obradiKomandu(veza);
        } catch (IOException ex) {
            Logger.getLogger(ServerGrupe.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void obradiKomandu(Socket veza) {
        try (
                DataInputStream din = new DataInputStream(veza.getInputStream());
                DataOutputStream dout = new DataOutputStream(veza.getOutputStream());) {
            String k = din.readUTF();
            Komanda komanda = new Komanda(k);
            String rezultat = izvrsiKomandu(komanda);
            dout.writeUTF(rezultat);
            dout.flush();
            dout.close();
            veza.close();
        } catch (IOException ex) {
            System.out.println("Greška pri obradi komande!");
            System.out.println(ex.getMessage());
        }
    }

    private String izvrsiKomandu(Komanda komanda) {
        String korisnickoIme = komanda.getParametri()[1];
        String lozinka = komanda.getParametri()[2];
        System.out.println("Korisnicko ime i lozinka iz komande: " + korisnickoIme + " " + lozinka);
        if (komanda.getVrsta() != VrstaIzraza.KOMANDA_DODAJ && !autenticiraj(korisnickoIme, lozinka)) {
            return "ERR 11;";
        }

        if (komanda.ispravna()) {
            String poruka = "";
            switch (komanda.getVrsta()) {
                case KOMANDA_AUTH:
                    poruka = "OK 10;";
                    break;
                case KOMANDA_DODAJ:
                    poruka = izvrsiKomanduDodaj(korisnickoIme, lozinka);
                    break;
                case KOMANDA_PAUZA:
                    poruka = izvrsiKomanduPauza();
                    break;
                case KOMANDA_RADI:
                    poruka = izvrsiKomanduRadi();
                    break;
                case KOMANDA_KRAJ:
                    poruka = izvrsiKomanduKraj();
                    break;
                case KOMANDA_STANJE:
                    poruka = izvrsiKomanduStanje();
                    break;
                case KOMANDA_GRUPA_PRIJAVI:
                    poruka = izvrsiKomanduGrupaPrijavi(korisnickoIme, lozinka);
                    break;
                case KOMANDA_GRUPA_ODJAVI:
                    poruka = izvrsiKomanduGrupaOdjavi(korisnickoIme, lozinka);
                    break;
                case KOMANDA_GRUPA_AKTIVIRAJ:
                    poruka = izvrsiKomanduGrupaAktiviraj(korisnickoIme, lozinka);
                    break;
                case KOMANDA_GRUPA_BLOKIRAJ:
                    poruka = izvrsiKomanduGrupaBlokiraj(korisnickoIme, lozinka);
                    break;
                case KOMANDA_GRUPA_STANJE:
                    poruka = izvrsiKomanduGrupaStanje(korisnickoIme, lozinka);
                    break;
                case KOMANDA_GRUPA_AERODROMI:
                    poruka = izvrsiKomanduGrupaAerodromi();
                    break;
            }
            return poruka;
        } else {
            return "ERR: Pogrešna komanda";
        }
    }

    private String izvrsiKomanduDodaj(String korisnickoIme, String lozinka) {
        KorisnikDAO korisnikDAO = new KorisnikDAO();
        if (korisnikDAO.dodajKorisnika(korisnickoIme, lozinka, bpk)) {
            return "OK 10;";
        }
        return "ERR 12;";
    }

    private String izvrsiKomanduPauza() {
        if (stanjePreuzimanja) {
            stanjePreuzimanja = false;
            return "OK 10;";
        }
        return "ERR 13;";
    }

    private String izvrsiKomanduRadi() {
        if (!stanjePreuzimanja) {
            stanjePreuzimanja = true;
            return "OK 10;";
        }
        return "ERR 14;";
    }

    private String izvrsiKomanduKraj() {
        aktivna = false;
        return "OK 10;";
    }

    private String izvrsiKomanduStanje() {
        if (stanjePreuzimanja) {
            return "OK 11;";
        }
        return "OK 12;";
    }

    private String izvrsiKomanduGrupaPrijavi(String korisnickoIme, String lozinka) {
        AerodromiWS_WS servis = new AerodromiWS_WS();
        return servis.prijaviGrupu(korisnickoIme, lozinka);
    }

    private String izvrsiKomanduGrupaOdjavi(String korisnickoIme, String lozinka) {
        AerodromiWS_WS servis = new AerodromiWS_WS();
        return servis.odjaviGrupu(korisnickoIme, lozinka);
    }

    private String izvrsiKomanduGrupaAktiviraj(String korisnickoIme, String lozinka) {
        AerodromiWS_WS servis = new AerodromiWS_WS();
        return servis.aktivirajGrupu(korisnickoIme, lozinka);
    }

    private String izvrsiKomanduGrupaBlokiraj(String korisnickoIme, String lozinka) {
        AerodromiWS_WS servis = new AerodromiWS_WS();
        return servis.blokirajGrupu(korisnickoIme, lozinka);
    }

    private String izvrsiKomanduGrupaStanje(String korisnickoIme, String lozinka) {
        AerodromiWS_WS servis = new AerodromiWS_WS();
        return servis.dajStanjeGrupe(korisnickoIme, lozinka);
    }

    private String izvrsiKomanduGrupaAerodromi() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}

/*
 MQTT mqtt = new MQTT();
            try {
                mqtt.setHost(host, port);
            } catch (URISyntaxException ex) {
                Logger.getLogger(PreuzimanjeLetovaAvionaAerodroma.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            mqtt.setUserName(user);
            mqtt.setPassword(password);

            final CallbackConnection connection = mqtt.callbackConnection();

            connection.listener(new org.fusesource.mqtt.client.Listener() {
                long count = 0;

                @Override
                public void onConnected() {
                    System.out.println("Otvorena veza na MQTT");
                }

                @Override
                public void onDisconnected() {
                    System.out.println("Prekinuta veza na MQTT");
                    System.exit(0);
                }

                @Override
                public void onFailure(Throwable value
                ) {
                    System.out.println("Problem u vezi na MQTT");
                    System.exit(-2);
                }

                @Override
                public void onPublish(UTF8Buffer topic, Buffer msg,
                        Runnable ack
                ) {
                    String body = msg.utf8().toString();
                    System.out.println("Stigla poruka br: " + count);
                    count++;
                }
            });

            connection.connect(new Callback<Void>() {
                @Override
                public void onSuccess(Void value
                ) {
                    Topic[] topics = {new Topic(destination, QoS.AT_LEAST_ONCE)};
                    connection.subscribe(topics, new Callback<byte[]>() {
                        @Override
                        public void onSuccess(byte[] qoses) {
                            System.out.println("Pretplata na: " + destination);
                        }

                        @Override
                        public void onFailure(Throwable value) {
                            System.out.println("Problem kod pretplate na: " + destination);
                            System.exit(-2);
                        }
                    });
                }

                @Override
                public void onFailure(Throwable value
                ) {
                    System.out.println("Neuspjela pretplata na: " + destination);
                    System.exit(-2);
                }
            });

 */
