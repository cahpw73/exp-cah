package ch.swissbytes.Service.security;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.logging.Logger;

/**
 * Created by Christian on 07/10/2015.
 */
public class ResetActivity implements Filter {


    public static final Logger log = Logger.getLogger(ResetActivity.class.getName());

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

  /*  private boolean isAJAXRequest(HttpServletRequest request) {
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
           // log.info(key+"  "+value);
        }
        return map;
    }*/

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpReq = (HttpServletRequest) request;
        HttpServletResponse httpRes = (HttpServletResponse) response;
        final String url = ((HttpServletRequest) httpReq).getRequestURI();
        if(url.endsWith("edit.jsf")){
            System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!filtering.....");
        }
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {

    }
}
