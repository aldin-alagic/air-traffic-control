package org.foi.nwtis.aalagic.web.filteri;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.foi.nwtis.aalagic.ejb.eb.Korisnici;

/**
 * Klasa filter koja omogućava kontrolu pristupa putanjama kojima mogu
 * pristupiti samo prijavljeni korisnici. Klasa koristi varijablu sesije
 * kontektsa "korisnik" prilikom provjere smije li korisnik pristupiti putanji.
 *
 *
 * @author Aldin Alagić
 */
public class KontrolaPristupa implements Filter {

    private static final boolean debug = true;

    // The filter configuration object we are associated with.  If
    // this value is null, this filter instance is not currently
    // configured. 
    private FilterConfig filterConfig = null;

    /**
     * Konstruktor klase koji kreira instancu klase i omogućava njen rad.
     *
     */
    public KontrolaPristupa() {
    }

    /**
     * Metoda koja se poziva prilikom otvaranja pojedine jsp stranice. Metoda
     * provjerava da li postoji varijabla sesije korisnik. Ako ne postoji
     * onemogućava pristup stranici i vrši redirekciju na index stranicu.
     *
     */
    private void doBeforeProcessing(ServletRequest request, ServletResponse response)
            throws IOException, ServletException {

        boolean prijavljen = false;

        if (request instanceof HttpServletRequest) {
            HttpSession session = ((HttpServletRequest) request).getSession(false);
            if (session != null && session.getAttribute("korisnik") != null) {
                Korisnici korisnik = (Korisnici) session.getAttribute("korisnik");
                if (korisnik.getKorIme() != null && !korisnik.getKorIme().trim().isEmpty()) {
                    prijavljen = true;
                }
            }
        }
        if (!prijavljen) {
            RequestDispatcher rd = request.getServletContext().getRequestDispatcher("/pogled1.xhtml");
            rd.forward(request, response);
        }
    }

    /**
     * Metoda koja se poziva nakon procesiranja jsp stranice.
     *
     */
    private void doAfterProcessing(ServletRequest request, ServletResponse response)
            throws IOException, ServletException {
    }

    /**
     *
     * @param request The servlet request we are processing
     * @param response The servlet response we are creating
     * @param chain The filter chain we are processing
     *
     * @exception IOException if an input/output error occurs
     * @exception ServletException if a servlet error occurs
     */
    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
            FilterChain chain)
            throws IOException, ServletException {

        doBeforeProcessing(request, response);

        chain.doFilter(request, response);

        doAfterProcessing(request, response);

    }

    /**
     * Return the filter configuration object for this filter.
     *
     * @return
     */
    public FilterConfig getFilterConfig() {
        return (this.filterConfig);
    }

    /**
     * Set the filter configuration object for this filter.
     *
     * @param filterConfig The filter configuration object
     */
    public void setFilterConfig(FilterConfig filterConfig) {
        this.filterConfig = filterConfig;
    }

    /**
     * Destroy method for this filter
     */
    @Override
    public void destroy() {
    }

    /**
     * Init method for t
     *
     * @param filterConfig
     */
    @Override
    public void init(FilterConfig filterConfig) {
        this.filterConfig = filterConfig;
    }

    public void log(String msg) {
        filterConfig.getServletContext().log(msg);
    }

}
