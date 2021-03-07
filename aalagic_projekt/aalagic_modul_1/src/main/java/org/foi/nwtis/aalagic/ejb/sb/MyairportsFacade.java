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
import org.foi.nwtis.aalagic.ejb.eb.Korisnici;
import org.foi.nwtis.aalagic.ejb.eb.Myairports;
import org.foi.nwtis.aalagic.ejb.eb.Myairports_;

/**
 *
 * @author NWTiS_4
 */
@Stateless
public class MyairportsFacade extends AbstractFacade<Myairports> implements MyairportsFacadeLocal {

    @PersistenceContext(unitName = "NWTiS_DZ3_PU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public MyairportsFacade() {
        super(Myairports.class);
    }

    /**
     * Metoda koja pomoću CAPI upita vraća aerodrome koje prati korisnik.
     *
     * @param korisnik vrijednost na osnovu koje se vraćaju aerodromi korisnika
     * @param aerodrom aerodrom koji se dohvaća
     * @return vraća listu aerodroma
     */
    @Override
    public List<Myairports> findOneUser(Korisnici korisnik, Airports aerodrom) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery cq = cb.createQuery(Myairports.class);
        Root<Airports> promatraniAerodromi = cq.from(Myairports.class);
        Expression<String> zaKorisnika = promatraniAerodromi.get("username");
        Expression<String> zaAerodrom = promatraniAerodromi.get("ident");
        cq.where(cb.and(cb.equal(zaKorisnika, korisnik)), cb.and(cb.equal(zaAerodrom, aerodrom)));
        Query q = em.createQuery(cq);
        return q.getResultList();
    }

    /**
     * Metoda koja pomoću CAPI upita vraća aerodrome koje prati korisnik.
     *
     * @param korisnik vrijednost na osnovu koje se vraćaju aerodromi korisnika
     * @return vraća listu aerodroma
     */
    @Override
    public List<Myairports> findAllUser(Korisnici korisnik) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery cq = cb.createQuery(Myairports.class);
        Root<Airports> promatraniAerodromi = cq.from(Myairports.class);
        Expression<String> zaKorisnika = promatraniAerodromi.get("username");
        cq.where(cb.equal(zaKorisnika, korisnik));
        Query q = em.createQuery(cq);
        return q.getResultList();
    }
}
