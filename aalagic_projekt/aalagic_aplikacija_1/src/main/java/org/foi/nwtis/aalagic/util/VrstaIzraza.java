package org.foi.nwtis.aalagic.util;

/**
 * Enumeracija koja služi za razlikovanje vrsta izraza.
 * Enumeraciju koriste objekti klasa Zahtjev i Unos kod
 * svoje varijable vrsta.
 *
 * @author Aldin Alagić
 * @version 1.0
 */
public enum VrstaIzraza {
    KOMANDA_AUTH,
    KOMANDA_DODAJ,
    KOMANDA_PAUZA,
    KOMANDA_RADI,
    KOMANDA_KRAJ,
    KOMANDA_STANJE,
    KOMANDA_GRUPA_PRIJAVI,
    KOMANDA_GRUPA_ODJAVI,
    KOMANDA_GRUPA_AKTIVIRAJ,
    KOMANDA_GRUPA_BLOKIRAJ,
    KOMANDA_GRUPA_STANJE,
    KOMANDA_GRUPA_AERODROMI,
    GRESKA
}
