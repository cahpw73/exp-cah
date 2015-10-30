package ch.swissbytes.Service.security;

import javax.faces.application.ResourceHandler;
import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Created by Christian on 07/10/2015.
 */
public class NoCacheFilter implements Filter {


    public static final Logger log = Logger.getLogger(NoCacheFilter.class.getName());

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    private boolean isAJAXRequest(HttpServletRequest request) {
        boolean check = false;
        String facesRequest = request.getHeader("Faces-Request");
        if (facesRequest != null && facesRequest.equals("partial/ajax")) {
            check = true;
        }
        return check;
    }

    private Map<String, String> getHeadersInfo(HttpServletRequest request) {
        Map<String, String> map = new HashMap<>();
        Enumeration headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String key = (String) headerNames.nextElement();
            String value = request.getHeader(key);
            map.put(key, value);
            log.info(key+"  "+value);
        }
        return map;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpReq = (HttpServletRequest) request;
        HttpServletResponse httpRes = (HttpServletResponse) response;
         if (!isAJAXRequest(httpReq)) {
             if (!httpReq.getRequestURI().startsWith(httpReq.getContextPath() + ResourceHandler.RESOURCE_IDENTIFIER)) { // Skip JSF resources (CSS/JS/Images/etc)
                 httpRes.setHeader("Cache-Control", "no-cache, no-store, must-revalidate"); // HTTP 1.1.
                 httpRes.setHeader("Pragma", "no-cache"); // HTTP 1.0.
                 httpRes.setDateHeader("Expires", 0); // Proxies.
             }
         }
       // getHeadersInfo(httpReq);
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {

    }
}
