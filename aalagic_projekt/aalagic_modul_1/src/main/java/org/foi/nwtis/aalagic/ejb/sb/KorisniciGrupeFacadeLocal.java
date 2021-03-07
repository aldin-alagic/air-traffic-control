/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.aalagic.ejb.sb;

import java.util.List;
import javax.ejb.Local;
import org.foi.nwtis.aalagic.ejb.eb.KorisniciGrupe;

/**
 *
 * @author NWTiS_4
 */
@Local
public interface KorisniciGrupeFacadeLocal {

    void create(KorisniciGrupe korisniciGrupe);

    void edit(KorisniciGrupe korisniciGrupe);

    void remove(KorisniciGrupe korisniciGrupe);

    KorisniciGrupe find(Object id);

    List<KorisniciGrupe> findAll();

    List<KorisniciGrupe> findRange(int[] range);

    int count();
    
}
