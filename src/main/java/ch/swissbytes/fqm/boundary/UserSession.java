package ch.swissbytes.fqm.boundary;

import ch.swissbytes.Service.business.user.UserService;
import ch.swissbytes.domain.model.entities.ModuleGrantedAccessEntity;
import ch.swissbytes.domain.types.ModuleSystemEnum;
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


    private String currentModule;
    private String currentHome;

    public String getCurrentModule() {
        return currentModule;
    }

    public void setCurrentModule(String currentModule) {
        this.currentModule = currentModule;
    }

    public String getCurrentHome() {
        return currentHome;
    }


}
