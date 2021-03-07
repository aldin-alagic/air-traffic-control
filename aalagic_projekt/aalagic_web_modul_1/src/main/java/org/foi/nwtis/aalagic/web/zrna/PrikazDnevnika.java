package org.foi.nwtis.aalagic.web.zrna;

import java.io.Serializable;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.servlet.ServletContext;
import lombok.Getter;
import org.foi.nwtis.aalagic.ejb.eb.Dnevnik;
import org.foi.nwtis.aalagic.ejb.sb.DnevnikFacadeLocal;
import org.foi.nwtis.aalagic.konfiguracije.Konfiguracija;
import org.foi.nwtis.aalagic.konfiguracije.bp.BP_Konfiguracija;

/**
 *
 * @author Aldin Alagić
 */
@Named(value = "prikazDnevnika")
@ViewScoped
public class PrikazDnevnika implements Serializable {

    @EJB
    DnevnikFacadeLocal dnevnikFacade;

    @Inject
    ServletContext context;

    @Getter
    List<Dnevnik> zahtjevi;

    @Getter
    int brojRedaka;

    @PostConstruct
    private void dohvatiZahtjeve() {
        zahtjevi = dnevnikFacade.findAll();
        Konfiguracija konfiguracija = ((BP_Konfiguracija) context.getAttribute("BP_Konfig")).getKonfig();
        brojRedaka = Integer.parseInt(konfiguracija.dajPostavku("dnevnik.redovi"));
    }

    /**
     * Creates a new instance of PrikazDnevnika
     */
    public PrikazDnevnika() {

    }

}
