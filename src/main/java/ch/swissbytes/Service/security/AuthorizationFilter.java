package ch.swissbytes.Service.security;

import ch.swissbytes.Service.business.security.SecurityService;
import org.picketlink.Identity;
import org.picketlink.idm.model.basic.User;

import javax.inject.Inject;
import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by alvaro on 7/1/2015.
 */
public class AuthorizationFilter implements Filter {

    @Inject
    private SecurityService securityService;
    @Inject
    private Identity identity;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        String context = ((HttpServletRequest) servletRequest).getContextPath();
        System.out.println("contexto "+context);
        User user= (User)identity.getAccount();
        final String url = ((HttpServletRequest) servletRequest).getRequestURI();
        System.out.println("url "+url);
        if (user!=null) {
            System.out.println("url "+url.substring(context.length()+1));
            if (securityService.canAccess(url.substring(context.length()+1),user.getLoginName())) {
                filterChain.doFilter(servletRequest, servletResponse);
            //TODO Borrar luego esta condincion que solo es de TEST
            }else if(url.equals("/procurement/procurement/report/spreadsheetTest.jsf")){
                filterChain.doFilter(servletRequest, servletResponse);
            }else{
                ((HttpServletResponse) servletResponse).sendRedirect(context + "/login.jsf");
            }
        } else {
            ((HttpServletResponse) servletResponse).sendRedirect(context + "/login.jsf");
        }
    }

    @Override
    public void destroy() {

    }
}
