package ch.swissbytes.Service.security;

import ch.swissbytes.Service.business.purchase.PurchaseOrderService;
import ch.swissbytes.Service.business.user.UserService;
import ch.swissbytes.domain.model.entities.PurchaseOrderEntity;
import ch.swissbytes.domain.model.entities.UserEntity;
import org.picketlink.Identity;
import org.picketlink.idm.model.basic.User;

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

   /* @Inject
    private PoBean poBean;*/

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

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpReq = (HttpServletRequest) request;
        HttpServletResponse httpRes = (HttpServletResponse) response;
        final String url = ((HttpServletRequest) httpReq).getRequestURI();
        String viewMode = ((HttpServletRequest) httpReq).getParameter("modeView") != null ? ((HttpServletRequest) httpReq).getParameter("modeView") : "false";
        if (url.endsWith("edit.jsf")) {
            if (isAJAXRequest(((HttpServletRequest) httpReq))) {
                if (!viewMode.equals("true")) {
                    String poId = ((HttpServletRequest) httpReq).getParameter("poId");
                    PurchaseOrderEntity po = service.findById(Long.valueOf(poId));
                    User user = (User) identity.getAccount();
                    UserEntity userEntity = userService.findByUsername(user.getLoginName());
                    if (service.canEdit(po, userEntity)) {
                        service.resetActivity(po);
                    }
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
