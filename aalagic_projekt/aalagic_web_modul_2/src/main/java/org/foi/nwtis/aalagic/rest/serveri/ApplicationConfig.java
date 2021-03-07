package org.foi.nwtis.aalagic.rest.serveri;

import java.util.Set;
import javax.ws.rs.core.Application;

/**
 *
 * Klasa kojom se definiraju klase REST resursi.
 * 
 * @author Aldin Alagić
 */
@javax.ws.rs.ApplicationPath("webresources")
public class ApplicationConfig extends Application {

    /**
     * Metoda vraća kolekciju set klasa REST resursa.
     * 
     * @return vraća set klasa REST resursa
     */
    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> resources = new java.util.HashSet<>();
        addRestResourceClasses(resources);
        return resources;
    }

    /**
     * Do not modify addRestResourceClasses() method.
     * It is automatically populated with
     * all resources defined in the project.
     * If required, comment out calling this method in getClasses().
     */
    private void addRestResourceClasses(Set<Class<?>> resources) {
        resources.add(org.foi.nwtis.aalagic.rest.serveri.AerodromiRS_1.class);
    }
    
}
