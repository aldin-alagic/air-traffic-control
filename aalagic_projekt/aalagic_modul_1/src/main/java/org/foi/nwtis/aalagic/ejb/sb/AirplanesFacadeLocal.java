/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.aalagic.ejb.sb;

import java.util.List;
import javax.ejb.Local;
import org.foi.nwtis.aalagic.ejb.eb.Airplanes;
import org.foi.nwtis.aalagic.ejb.eb.Airports;

/**
 *
 * @author NWTiS_4
 */
@Local
public interface AirplanesFacadeLocal {

    void create(Airplanes airplanes);

    void edit(Airplanes airplanes);

    void remove(Airplanes airplanes);

    Airplanes find(Object id);

    List<Airplanes> findAll();

    List<Airplanes> findRange(int[] range);
    
    List<Airplanes> findAllAirport(Airports aerodrom, int odVrijeme, int doVrijeme);

    List<Airplanes> findAllName(String icao24, int odVrijeme, int doVrijeme);
    int count();
    
}
