package org.example.servlet;

import org.hippoecm.hst.servlet.BinariesServlet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * This class add the functionality to the {@link BinariesServlet} to make sure it takes a very long time before the browser cache for and
 * binary invalidates.
 * <p>If the init parameter {@link #NEVER_EXPIRES_INIT_PARAM} is not set or set to true the this behaviour is applied
 *
 * <p>If the init parameter {@link #NEVER_EXPIRES_INIT_PARAM} is set to false, the default behaviour of the BinariesServlet is used.
 *
 * @see BinariesServlet
 * @see org.hippoecm.hst.servlet.utils.HeaderUtils
 * @see BrowserCachingLeveragingResponseWrapper
 */
public class BrowserCachingLeveragingBinariesServlet extends BinariesServlet {

    /**
     * Initialization parameter, if set to false the default behaviour of the BinariesServlet is used.
     */
    public final String NEVER_EXPIRES_INIT_PARAM = "never-expires";
    public final boolean DEFAULT_NEVER_EXPIRES = true;

    /**
     * If the initialization parameter is set to false,
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        boolean neverExpires = getBooleanInitParameter(NEVER_EXPIRES_INIT_PARAM, DEFAULT_NEVER_EXPIRES);
        if (neverExpires){
            super.doGet(request, new BrowserCachingLeveragingResponseWrapper(response));
        }
        else{
            super.doGet(request,response);
        }
    }
}
