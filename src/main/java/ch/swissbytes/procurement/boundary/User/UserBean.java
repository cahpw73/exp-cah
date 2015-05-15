package ch.swissbytes.procurement.boundary.User;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import java.io.Serializable;
import java.util.logging.Logger;

/**
 * Created by Christian on 15/05/2015.
 */
@ViewScoped
@Named
public class UserBean implements Serializable {

    public static final Logger log = Logger.getLogger(UserBean.class.getName());

    private boolean isCreateUser;

    @PostConstruct
    public void init (){
        log.info("UserBean created");

    }

    @PreDestroy
    public void destroy(){
        log.info("UserBean destroying");
    }

    public boolean isCreateUser() {
        return isCreateUser;
    }

    public void setCreateUser(boolean isCreateUser) {
        this.isCreateUser = isCreateUser;
    }
}
