package ch.swissbytes.fqmes.boundary.security;

import ch.swissbytes.Service.business.user.UserService;
import ch.swissbytes.domain.model.entities.UserEntity;
import ch.swissbytes.fqmes.util.Encode;
import org.omnifaces.util.Messages;
import org.picketlink.Identity;
import org.picketlink.annotations.PicketLink;
import org.picketlink.authentication.BaseAuthenticator;
import org.picketlink.credential.DefaultLoginCredentials;
import org.picketlink.idm.model.Attribute;
import org.picketlink.idm.model.basic.User;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by alvaro on 9/1/14.
 */
@PicketLink
@Named
public class Authenticator extends BaseAuthenticator{

    private static final Logger log= Logger.getLogger(Authenticator.class.getName());

    @Inject
    private DefaultLoginCredentials credentials;

    @Inject
    private UserService userCtrl;

    @Inject
    private Identity identity;

    @Override
    public void authenticate(){
        log.log(Level.INFO,"trying to authenticate");
        if(!identity.isLoggedIn()) {
            final String passwordHashed = Encode.encode(credentials.getPassword());
            //log.log(Level.INFO,String.format("password hashed [%s]",passwordHashed));
            if (userCtrl.canAccess(credentials.getUserId(), passwordHashed)) {
                UserEntity userEntity = getUserEntity(credentials.getUserId(), passwordHashed);
                ArrayList<String> roleList = new ArrayList<String>();
                //roleList.add(userEntity.getRoleEntity().getName());
                setStatus(AuthenticationStatus.SUCCESS);
                User user = new User(credentials.getUserId());
                user.setFirstName(userEntity.getFirstName());
                user.setLastName(userEntity.getName());
                user.setEmail(userEntity.getEmail());
                user.setAttribute(new Attribute<ArrayList>("roles", roleList));
                setAccount(user);
                log.log(Level.INFO, "user authenticated");
            } else {
                Messages.addGlobalError("User cannot log in");
                setStatus(AuthenticationStatus.FAILURE);
                log.log(Level.INFO, "user fail");
            }
        }else{
            //log.info("this user has already logged in ["identity."]");
            log.info("this user is trying to log ["+credentials.getUserId()+"]");
        }
    }

    private UserEntity getUserEntity(final String username, final String pass){
        return userCtrl.getUserEntity(username,pass);
    }
}
