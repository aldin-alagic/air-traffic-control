/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.aalagic.ejb.sb;

import java.util.List;
import javax.ejb.Local;
import org.foi.nwtis.aalagic.ejb.eb.Poruke;

/**
 *
 * @author NWTiS_4
 */
@Local
public interface PorukeFacadeLocal {

    void create(Poruke poruke);

    void edit(Poruke poruke);

    void remove(Poruke poruke);

    Poruke find(Object id);

    List<Poruke> findAll();

    List<Poruke> findRange(int[] range);

    int count();
    
}
