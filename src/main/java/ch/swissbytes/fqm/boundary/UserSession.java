package ch.swissbytes.fqm.boundary;

import ch.swissbytes.domain.types.ModuleSystemEnum;
import org.apache.commons.lang.StringUtils;
import org.picketlink.Identity;

import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.logging.Logger;

/**
 * Created by alvaro on 5/25/2015.
 */
@Named
@SessionScoped
public class UserSession implements Serializable{

    public static final Logger log = Logger.getLogger(UserSession.class.getName());

    private String currentModule;
    private String currentHome;
    private boolean hasAccessBoth;
    @Inject
    private Identity identity;

     public boolean isProcurement() {
        if (StringUtils.isNotEmpty(currentModule) && StringUtils.isNotBlank(currentModule)) {
            boolean result = ModuleSystemEnum.PROCUREMENT.name().equalsIgnoreCase(currentModule);
            return result;
        }
        return false;
    }

    public String switchToModuleSelected(){
        log.info("switch to module selected");
        if(StringUtils.isNotEmpty(currentModule) && StringUtils.isNotBlank(currentModule)){
            if(ModuleSystemEnum.PROCUREMENT.name().equalsIgnoreCase(currentModule)){
                currentModule = ModuleSystemEnum.EXPEDITING.name();
            }else{
                currentModule = ModuleSystemEnum.PROCUREMENT.name();
            }
        }
        return currentModule;
    }

    public String switchToExpediting(){
        log.info("switch to expediting");
        currentModule = ModuleSystemEnum.EXPEDITING.name();
        return currentModule;
    }

    public String switchToProcurement(){
        log.info("switch to procurement");
        currentModule = ModuleSystemEnum.PROCUREMENT.name();
        return currentModule;
    }

    public void logout(){
        if(identity.isLoggedIn()) {
            identity.logout();
            currentModule=null;
        }
    }

    public String getAbsoluteCurrentHome(){
        String home="";
        if(currentModule!=null) {
            if (currentModule.equalsIgnoreCase(ModuleSystemEnum.EXPEDITING.name())) {
                home = "/home";
            } else if (currentModule.equalsIgnoreCase(ModuleSystemEnum.PROCUREMENT.name())) {
                home = "/procurement/project/list";
            }
        }
        return home;
    }

    public String getCurrentModule() {
        return currentModule;
    }

    public void setCurrentModule(String currentModule) {
        this.currentModule = currentModule;
    }

    public String getCurrentHome() {
        return currentHome;
    }

    public boolean isHasAccessBoth() {
        return hasAccessBoth;
    }

    public void setHasAccessBoth(boolean hasAccessBoth) {
        this.hasAccessBoth = hasAccessBoth;
    }


}
