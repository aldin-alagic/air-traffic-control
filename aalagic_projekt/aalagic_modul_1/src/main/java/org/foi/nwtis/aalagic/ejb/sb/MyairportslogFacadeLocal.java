/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.aalagic.ejb.sb;

import java.util.List;
import javax.ejb.Local;
import org.foi.nwtis.aalagic.ejb.eb.Myairportslog;

/**
 *
 * @author NWTiS_4
 */
@Local
public interface MyairportslogFacadeLocal {

    void create(Myairportslog myairportslog);

    void edit(Myairportslog myairportslog);

    void remove(Myairportslog myairportslog);

    Myairportslog find(Object id);

    List<Myairportslog> findAll();

    List<Myairportslog> findRange(int[] range);

    int count();
    
}
