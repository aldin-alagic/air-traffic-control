/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.aalagic.ejb.sb;

import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Root;
import org.foi.nwtis.aalagic.ejb.eb.Airports;

/**
 *
 * @author NWTiS_4
 */
@Stateless
public class AirportsFacade extends AbstractFacade<Airports> implements AirportsFacadeLocal {

    @PersistenceContext(unitName = "NWTiS_DZ3_PU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public AirportsFacade() {
        super(Airports.class);
    }

    /**
     * Metoda koja pomoću CAPI upita vraća aerodrome s sličnim nazivom kao što
     * je proslijeđeni naziv.
     *
     * @param naziv vrijednost na osnovu koje se vraćaju slični aerodromi
     * @return vraća listu aerodroma
     */
    @Override
    public List<Airports> findAllName(String naziv) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery cq = cb.createQuery(Airports.class);
        Root<Airports> aerodromi = cq.from(Airports.class);
        Expression<String> zaNaziv = aerodromi.get("name");
        cq.where(cb.like(zaNaziv, naziv));
        Query q = em.createQuery(cq);
        return q.getResultList();
    }

    /**
     * Metoda koja pomoću CAPI upita vraća aerodrome s državom kao što je
     * proslijeđena država.
     *
     * @param drzava vrijednost na osnovu koje se vraćaju aerodromi
     * @return vraća listu aerodroma
     */
    @Override
    public List<Airports> findAllCountry(String drzava) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery cq = cb.createQuery(Airports.class);
        Root<Airports> aerodromi = cq.from(Airports.class);
        Expression<String> zaDrzavu = aerodromi.get("isoCountry");
        cq.where(cb.equal(zaDrzavu, drzava));
        Query q = em.createQuery(cq);
        return q.getResultList();
    }
}
