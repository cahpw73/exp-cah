package ch.swissbytes.fqm.boundary;

import ch.swissbytes.Service.business.user.UserService;
import ch.swissbytes.domain.model.entities.ModuleGrantedAccessEntity;
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


    private static final Logger log= Logger.getLogger(AuthenticatorBean.class.getName());


    public String login(){
        log.info("using another login");
        if(!identity.isLoggedIn()){
            identity.login();
        }else{
            log.log(Level.WARNING,String.format("user is already logged in"));
        }
        if(identity.isLoggedIn()){
            User user=(User)identity.getAccount();
            List<ModuleGrantedAccessEntity>systemList=service.getModulesGranted(user.getLoginName());
            if(systemList.isEmpty()){
                identity.logout();
                log.log(Level.WARNING,"this user has no access any module system");
                return "NONE";
            }
            if(systemList.size()==1){
                return systemList.get(0).getModuleSystem().name();
            }
            openDialogToPickModuleSystem();
        }
        return "";
    }

    public String isLoggedIn(){
        if(!identity.isLoggedIn()){
            return "NONE";
        }
       // return currentModule!=null?currentModule.name():"NONE";
        return null;
    }

    public String currentUser(){
        User user=(User)identity.getAccount();
        return user!=null?user.getFirstName()+" "+user.getLastName():"";
    }
    public void openDialogToPickModuleSystem(){
        log.info("openDialogToPickModuleSystem open1!!");
        RequestContext context = RequestContext.getCurrentInstance();
        RequestContext.getCurrentInstance().update("pickSystemFormId");
        context.execute("PF('pickSystemModal').show();");
        //RequestContext.getCurrentInstance().execute("PF('moduleDlgId').show();");
    }
    public String enterExpeditingModule(){
      //  currentModule=ModuleSystemEnum.EXPEDITING;
        return "home?faces-redirect=true";
    }
    public String enterProcurementModule(){
        //currentModule=ModuleSystemEnum.PROCUREMENT;
        return "/procurement/home?faces-redirect=true";
    }
}
