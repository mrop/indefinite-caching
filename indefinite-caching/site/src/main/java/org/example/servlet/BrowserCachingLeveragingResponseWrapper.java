package org.example.servlet;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

/**
 * This class is a wrapper for the {@link HttpServletResponseWrapper}.
 *
 * If the {@link #EXPIRES_HEADER_NAME} is set, it is replaced by a date a year in future
 *
 * @see org.hippoecm.hst.servlet.utils.HeaderUtils#setExpiresHeaders(HttpServletResponse, org.hippoecm.hst.servlet.utils.BinaryPage)
 * @see HttpServletResponseWrapper
 */
public class BrowserCachingLeveragingResponseWrapper extends HttpServletResponseWrapper {


    public static final long YEAR_IN_MILLIS = 365L * 24L * 60L * 60L * 1000L;
    public static final String EXPIRES_HEADER_NAME = "Expires";

    /**
     * Constructs a response adaptor wrapping the given response.
     *
     * @param response
     * @throws IllegalArgumentException if the response is null
     */
    public BrowserCachingLeveragingResponseWrapper(HttpServletResponse response) {
        super(response);
    }

    /**
     * If the name of the header is {@link #EXPIRES_HEADER_NAME}, it is replaced by the date a year from now.
     * {@inheritDoc}
     *
     */
    @Override
    public void setDateHeader(String name, long date) {
        if (EXPIRES_HEADER_NAME.equals(name)){
            super.setDateHeader(EXPIRES_HEADER_NAME,  System.currentTimeMillis()+ YEAR_IN_MILLIS);
        }
        else{
            super.setDateHeader(name,date);
        }
    }

    /**
     *  If the name of the header is "Cache-Control", the max-age directive is set to a year.
     *
     *  <p>
     *  {@inheritDoc}
     */
    @Override
    public void setHeader(String name, String value) {
        if ("Cache-Control".equals(name)){
            super.setHeader(name,"max-age=" + (YEAR_IN_MILLIS / 1000L) );
        }
        else{
            super.setHeader(name,value);
        }
    }
}
