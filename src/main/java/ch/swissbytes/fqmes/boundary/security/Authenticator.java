package ch.swissbytes.fqmes.boundary.security;

import ch.swissbytes.Service.business.role.RoleDao;
import ch.swissbytes.Service.business.user.UserDao;
import ch.swissbytes.Service.business.user.UserService;
import ch.swissbytes.domain.model.entities.RoleEntity;
import ch.swissbytes.domain.model.entities.UserEntity;
import ch.swissbytes.domain.types.ModuleSystemEnum;
import ch.swissbytes.fqmes.util.Encode;
import org.omnifaces.util.Messages;
import org.picketlink.annotations.PicketLink;
import org.picketlink.authentication.BaseAuthenticator;
import org.picketlink.credential.DefaultLoginCredentials;
import org.picketlink.idm.model.Attribute;
import org.picketlink.idm.model.basic.User;
import org.primefaces.context.RequestContext;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.ArrayList;
import java.util.List;
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
    private RoleDao roleDao;

    @Inject
    private UserDao userDao;


    @Override
    public void authenticate(){
        log.log(Level.INFO,"trying to authenticate");

        final  String passwordHashed= Encode.encode(credentials.getPassword());
        if(userCtrl.canAccess(credentials.getUserId(),passwordHashed)){
            UserEntity userEntity = getUserEntity(credentials.getUserId(),passwordHashed);
            List<String> roleList = new ArrayList<>();
            List<RoleEntity> roleEntities = roleDao.getRolesAssignedBy(userDao.findUserByUserName(credentials.getUserId()).get(0).getId());
            for (RoleEntity re : roleEntities){
                roleList.add(re.getName());
            }
            User user = new User(credentials.getUserId());
            user.setFirstName(userEntity.getFirstName());
            user.setLastName(userEntity.getName());
            user.setEmail(userEntity.getEmail());
            List<Object> attributes=new ArrayList<>();
            attributes.add(roleList);
            user.setAttribute(new Attribute<ArrayList>("attributes", (ArrayList) attributes));
            setAccount(user);
            setStatus(AuthenticationStatus.SUCCESS);
            log.log(Level.INFO,"user authenticated");
        }else{
            Messages.addGlobalError("User cannot log in");
            setStatus(AuthenticationStatus.FAILURE);
            log.log(Level.INFO, "user fail");

        }
    }




    private UserEntity getUserEntity(final String username, final String pass){
        return userCtrl.getUserEntity(username,pass);
    }
}
