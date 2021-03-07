/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.aalagic.ejb.sb;

import java.util.List;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Root;
import org.foi.nwtis.aalagic.ejb.eb.Airplanes;
import org.foi.nwtis.aalagic.ejb.eb.Airplanes_;
import org.foi.nwtis.aalagic.ejb.eb.Airports;

/**
 *
 * @author NWTiS_4
 */
@Stateless
public class AirplanesFacade extends AbstractFacade<Airplanes> implements AirplanesFacadeLocal {

    @PersistenceContext(unitName = "NWTiS_DZ3_PU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @Inject
    AirportsFacadeLocal airportsFacadeLocal;

    public AirplanesFacade() {
        super(Airplanes.class);
    }

    /**
     * Metoda koja pomoću CAPI upita vraća letove koji polijeću s zadanog
     * aerodroma u zadanom vremenskom rasponu koji je određen parametrima.
     *
     * @param aerodrom aerodrom s kojeg letovi polijeću
     * @param odVrijeme početno vrijeme raspona
     * @param doVrijeme završno vrijeme raspona
     * @return vraća listu aerodroma
     */
    @Override
    public List<Airplanes> findAllAirport(Airports aerodrom, int odVrijeme, int doVrijeme) {

        
               /* return em.createQuery("SELECT a FROM Airplanes a WHERE a.estdepartureairport = :icao "
                        + "AND a.firstseen BETWEEN :odVrijeme AND :doVrijeme")
                        .setParameter("icao", icao)
                        .setParameter("odVrijeme", odVrijeme)
                        .setParameter("doVrijeme", doVrijeme).getResultList();*/

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery cq = cb.createQuery(Airplanes.class);
        Root<Airplanes> letovi = cq.from(Airplanes.class);
        Expression<String> zaAerodromRaspon = letovi.get("estdepartureairport");
        cq.where(cb.and(cb.equal(zaAerodromRaspon, aerodrom)),
                cb.and(cb.between(letovi.get(Airplanes_.firstseen), odVrijeme, doVrijeme))
        );
        return em.createQuery(cq).getResultList();
    }

    /**
     * Metoda koja pomoću CAPI upita vraća letove zadanog aviona u zadanom
     * vremenskom rasponu koji je određen parametrima.
     *
     * @param icao24 identifikacija aviona
     * @param odVrijeme početno vrijeme raspona
     * @param doVrijeme završno vrijeme raspona
     * @return vraća listu aerodroma
     */
    @Override
    public List<Airplanes> findAllName(String icao24, int odVrijeme, int doVrijeme) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery cq = cb.createQuery(Airplanes.class);
        Root<Airplanes> letovi = cq.from(Airplanes.class);
        Expression<String> zaAvionRaspon = letovi.get("icao24");
        cq.where(cb.and(cb.equal(zaAvionRaspon, icao24)),
                cb.and(cb.between(letovi.get(Airplanes_.firstseen), odVrijeme, doVrijeme))
        );
        return em.createQuery(cq).getResultList();
    }

}
