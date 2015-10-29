package ch.swissbytes.Service.security;

import ch.swissbytes.Service.business.purchase.PurchaseOrderService;
import ch.swissbytes.Service.business.user.UserService;
import ch.swissbytes.domain.model.entities.PurchaseOrderEntity;
import ch.swissbytes.domain.model.entities.UserEntity;
import org.omnifaces.util.Faces;
import org.picketlink.Identity;
import org.picketlink.idm.jpa.annotations.Identifier;
import org.picketlink.idm.model.basic.User;

import javax.faces.context.FacesContext;
import javax.inject.Inject;
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

    @Inject
    private PurchaseOrderService service;

    @Inject
    private UserService userService;
    @Inject
    private Identity identity;

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

  /*  private Map<String, String> getHeadersInfo(HttpServletRequest request) {
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
        String viewMode = ((HttpServletRequest) httpReq).getParameter("") != null ? ((HttpServletRequest) httpReq).getParameter("modeView") : "false";
        if (url.endsWith("edit.jsf")) {
            if (isAJAXRequest(((HttpServletRequest) httpReq)) && viewMode.equals("false")) {
                String poId = ((HttpServletRequest) httpReq).getParameter("poId");
                try {
                    PurchaseOrderEntity po = service.findById(Long.valueOf(poId));
                    User user = (User) identity.getAccount();
                    UserEntity userEntity = userService.findByUsername(user.getLoginName());
                    if (po != null) {
                        if (service.canEdit(po, userEntity)) {
                            service.resetActivity(po);
                        }
                    }
                } catch (NumberFormatException nfe) {

                }
            }
        }
        chain.doFilter(request, response);
    }

    private PurchaseOrderEntity findPurchaseOrder(String poId) {
        boolean valid = false;
        try {
            Long.parseLong(poId);
        } catch (NumberFormatException nfe) {

        }
        return null;
    }

    @Override
    public void destroy() {

    }
}
