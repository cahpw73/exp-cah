package ch.swissbytes.fqm.boundary;

import ch.swissbytes.Service.business.user.UserService;
import ch.swissbytes.domain.model.entities.ModuleGrantedAccessEntity;
import ch.swissbytes.domain.types.ModuleSystemEnum;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.picketlink.Identity;
import org.picketlink.idm.model.Attribute;
import org.picketlink.idm.model.basic.User;
import org.primefaces.context.RequestContext;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.List;
import java.util.logging.Level;
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

    public boolean isProcurement() {
        if (StringUtils.isNotEmpty(currentModule) && StringUtils.isNotBlank(currentModule)) {
            boolean result = ModuleSystemEnum.PROCUREMENT.name().equalsIgnoreCase(currentModule);
            System.out.println("Result current module : " + result);
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
