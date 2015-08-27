package ch.swissbytes.fqm.boundary;

import ch.swissbytes.Service.business.user.UserService;
import ch.swissbytes.domain.model.entities.ModuleGrantedAccessEntity;
import ch.swissbytes.domain.types.ModuleSystemEnum;
import org.apache.commons.lang.StringUtils;
import org.picketlink.Identity;
import org.picketlink.idm.model.basic.User;
import org.primefaces.context.RequestContext;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by alvaro on 5/25/2015.
 */
@RequestScoped
@Named
public class AuthenticatorBean {

    @Inject
    private Identity identity;

    @Inject
    private UserService service;

    @Inject
    private UserSession session;


    private static final Logger log = Logger.getLogger(AuthenticatorBean.class.getName());


    public String login() {
        log.info("using another login");
        loginIfAny();
        if (identity.isLoggedIn()) {
            User user = (User) identity.getAccount();
            List<ModuleGrantedAccessEntity> systemList = service.getModulesGranted(user.getLoginName());
            if (systemList.isEmpty()) {
                identity.logout();
                log.log(Level.WARNING, "this user has no access any module system");
                //FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR,"Error","this user has no access any module system");

                return "NONE";
            }
            if (systemList.size() == 1) {
                session.setCurrentModule(systemList.get(0).getModuleSystem().name());
                session.setHasAccessBoth(false);
                return systemList.get(0).getModuleSystem().name();
            }
            session.setHasAccessBoth(true);
            openDialogToPickModuleSystem();
        }
        return "";
    }
    private void loginIfAny(){
        if (!identity.isLoggedIn()) {
            identity.login();
        } else {
            log.log(Level.WARNING, String.format("user is already logged in"));
        }
    }

    public String currentUser() {
        User user = (User) identity.getAccount();
        return user != null ? user.getFirstName() + " " + user.getLastName() : "";
    }

    public void openDialogToPickModuleSystem() {
        log.info("openDialogToPickModuleSystem !");
        RequestContext context = RequestContext.getCurrentInstance();
        RequestContext.getCurrentInstance().update("pickSystemFormId");
        context.execute("PF('pickSystemModal').show();");
    }

    public String enterExpeditingModule() {
        session.setCurrentModule(ModuleSystemEnum.EXPEDITING.name());
        return "home?faces-redirect=true";
    }

    public String enterProcurementModule() {
        session.setCurrentModule(ModuleSystemEnum.PROCUREMENT.name());
        return "/procurement/project/list?faces-redirect=true";
    }


    public void cancel(){
        if(identity.isLoggedIn()){
            identity.logout();
        }
    }

    public String validation(){
        return "true";
    }


    public String currentHome(){
        return session.getAbsoluteCurrentHome()+".xhtml";
    }
    public String validationLogin() {
        if(!identity.isLoggedIn()){
            return "";
        }else{
            if(StringUtils.isNotEmpty(session.getCurrentModule())&&StringUtils.isNotBlank(session.getCurrentModule())){
                return "true";
            }else{
                openDialogToPickModuleSystem();
                return "";
            }
        }
    }
}
