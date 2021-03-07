/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.aalagic.wsocket.klijenti;

import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketAdapter;
import com.neovisionaries.ws.client.WebSocketException;
import com.neovisionaries.ws.client.WebSocketExtension;
import com.neovisionaries.ws.client.WebSocketFactory;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Aldin Alagić
 */
public class WebSocketKlijent {

    private static final String SERVER = "ws://localhost:8084/aalagic_web_modul_1/aerodromi";

    private static final int TIMEOUT = 2000;

    public static boolean posaljiPoruku(String poruka) {
        try {
            WebSocket ws = new WebSocketFactory()
                    .setConnectionTimeout(TIMEOUT)
                    .createSocket(SERVER)
                    .addListener(new WebSocketAdapter() {
                        @Override
                        public void onTextMessage(WebSocket websocket, String message) {
                            System.out.println(message);
                        }
                    })
                    .addExtension(WebSocketExtension.PERMESSAGE_DEFLATE)
                    .connect();

            ws.sendText(poruka);
            ws.disconnect();
            return true;
        } catch (WebSocketException | IOException ex) {
            Logger.getLogger(WebSocketKlijent.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }
}
