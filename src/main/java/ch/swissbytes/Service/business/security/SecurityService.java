package ch.swissbytes.Service.business.security;

import ch.swissbytes.domain.model.entities.OptionsEntity;
import org.picketlink.Identity;

import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import java.io.Serializable;
import java.util.List;

/**
 * Created by alvaro on 7/1/2015.
 */
@SessionScoped
public class SecurityService implements Serializable{

    @Inject
    private Identity identity;

    private List<OptionsEntity> permissions;

    @PostConstruct
    public void create(){
        //identity
    }

    public boolean canAccess(String url,String user){
        System.out.println("url ...."+url+"- "+user);
        return true;
    }

    public boolean isLoggedIn(){
        return true;
    }
}
