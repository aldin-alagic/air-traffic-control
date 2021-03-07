package org.foi.nwtis.aalagic.socket.klijent;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author Aldin Alagić
 */
public class SocketKlijent {

    @Getter
    @Setter
    String adresa;
    int port;

    public SocketKlijent(String adresa, int port) {
        this.adresa = adresa;
        this.port = port;
    }

    public String posaljiPoruku(String poruka) {
        String odgovor;
        try (Socket veza = new Socket(adresa, port);
                DataInputStream din = new DataInputStream(veza.getInputStream());
                DataOutputStream dout = new DataOutputStream(veza.getOutputStream());) {
            dout.writeUTF(poruka);
            dout.flush();
            odgovor = din.readUTF();
            din.close();
            veza.close();
        } catch (SocketException e) {
            odgovor = "Ugašen";
        } catch (IOException ex) {
            odgovor = "Ugašen";
        }
        System.out.println("Odgovor poslužitelja: " + odgovor);
        return odgovor;
    }

}
