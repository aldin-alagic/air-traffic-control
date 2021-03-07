package org.foi.nwtis.aalagic.ejb.sb;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

/**
 *
 * @author Aldin Alagić
 */
@Stateless
public class SlanjePoruka {

    int red = 0;
    Connection conn = null;
    Session s = null;
    MessageProducer mp;

    @Resource(mappedName = "jms/NWTiS_aalagic_1")
    private Queue nWtiS_aalagic_1;

    @Resource(mappedName = "jms/NWTiS_aalagic_2")
    private Queue nWtiS_aalagic_2;

    @Resource(mappedName = "jms/NWTiS_aalagic_3")
    private Queue nWtiS_aalagic_3;

    @Resource(mappedName = "jms/NWTiS_QF_aalagic_1")
    private ConnectionFactory nWtiS_QF_aalagic_1;

    @Resource(mappedName = "jms/NWTiS_QF_aalagic_2")
    private ConnectionFactory nWtiS_QF_aalagic_2;

    @Resource(mappedName = "jms/NWTiS_QF_aalagic_3")
    private ConnectionFactory nWtiS_QF_aalagic_3;

    public void posalji(int red, Object poruka) {
        this.red = red;
        try {
            saljiJMSPoruku(poruka);
        } catch (JMSException | NamingException ex) {
            Logger.getLogger(SlanjePoruka.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private Message kreirajJMSPoruku(Session session, Object messageData) throws JMSException {
        TextMessage tm = session.createTextMessage();
        System.out.println("Kreiram poruku s sadržajem: " + messageData.toString());
        tm.setText(messageData.toString());
        return tm;
    }

    private void saljiJMSPoruku(Object messageData) throws JMSException, NamingException {
        Context c = new InitialContext();
        try {
            mp.send(kreirajJMSPoruku(s, messageData));
            System.out.println("Šaljem poruku");
        } finally {
            if (s != null) {
                try {
                    s.close();
                } catch (JMSException e) {
                    Logger.getLogger(this.getClass().getName()).log(Level.WARNING, "Ne mogu zatvoriti vezu", e);
                }
            }

            if (conn != null) {
                conn.close();
            }

        }
    }

    private void pripremiResurse() throws JMSException {
        switch (red) {
            case 1:
                conn = nWtiS_QF_aalagic_1.createConnection();
                s = conn.createSession(false, s.AUTO_ACKNOWLEDGE);
                mp = s.createProducer(nWtiS_aalagic_1);
                break;
            case 2:
                conn = nWtiS_QF_aalagic_2.createConnection();
                s = conn.createSession(false, s.AUTO_ACKNOWLEDGE);
                mp = s.createProducer(nWtiS_aalagic_2);
                break;
            case 3:
                conn = nWtiS_QF_aalagic_3.createConnection();
                s = conn.createSession(false, s.AUTO_ACKNOWLEDGE);
                mp = s.createProducer(nWtiS_aalagic_3);
                break;
            default:
                System.out.println("Izabran nepostojeći red!");
        }
    }

}
