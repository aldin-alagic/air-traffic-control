/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.aalagic.ejb.sb;

import java.util.List;
import javax.ejb.Local;
import org.foi.nwtis.aalagic.ejb.eb.Airports;
import org.foi.nwtis.aalagic.ejb.eb.Korisnici;
import org.foi.nwtis.aalagic.ejb.eb.Myairports;

/**
 *
 * @author NWTiS_4
 */
@Local
public interface MyairportsFacadeLocal {

    void create(Myairports myairports);

    void edit(Myairports myairports);

    void remove(Myairports myairports);

    Myairports find(Object id);

    List<Myairports> findAll();

    List<Myairports> findRange(int[] range);
    
    List<Myairports> findOneUser(Korisnici korisnik, Airports aerodrom);
    
    List<Myairports> findAllUser(Korisnici korisnik);
    
    int count();

}
