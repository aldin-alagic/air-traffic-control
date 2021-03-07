package org.foi.nwtis.aalagic.slusaci;

import java.net.URL;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;
import org.foi.nwtis.aalagic.PreuzimanjeLetova;
import org.foi.nwtis.aalagic.ejb.sb.AirplanesFacadeLocal;
import org.foi.nwtis.aalagic.ejb.sb.MyairportsFacadeLocal;
import org.foi.nwtis.aalagic.ejb.sb.MyairportslogFacadeLocal;
import org.foi.nwtis.aalagic.konfiguracije.Konfiguracija;
import org.foi.nwtis.aalagic.konfiguracije.KonfiguracijaApstraktna;
import org.foi.nwtis.aalagic.konfiguracije.NeispravnaKonfiguracija;
import org.foi.nwtis.aalagic.konfiguracije.NemaKonfiguracije;

/**
 *
 * @author Aldin Alagić
 */
@Startup
@Singleton
public class SlusacAplikacije {

    @Inject
    MyairportsFacadeLocal myairportsFacade;
    @Inject
    MyairportslogFacadeLocal myairportslogFacade;
    @Inject
    AirplanesFacadeLocal airplanesFacade;

    PreuzimanjeLetova dretva;
    static final String NAZIV_KONFIGURACIJE = "NWTiS.db.config_2.xml";

    @PostConstruct
    private void pokreni() {
        try {
            KonfiguracijaApstraktna.kreirajKonfiguraciju(NAZIV_KONFIGURACIJE);
            URL datoteka = getClass().getClassLoader().getResource(NAZIV_KONFIGURACIJE);
            Konfiguracija konfiguracija = KonfiguracijaApstraktna.preuzmiKonfiguraciju(datoteka.getFile());
            dretva = new PreuzimanjeLetova(konfiguracija, myairportsFacade, myairportslogFacade, airplanesFacade);
            dretva.start();
        } catch (NemaKonfiguracije | NeispravnaKonfiguracija ex) {
            System.out.println("Problem kod učitavanja konfiguracijske datoteke");
            return;
        }
    }

    @PreDestroy
    private void zaustaviDretvu() {
        if (dretva != null) {
            dretva.interrupt();
        }
    }
}
