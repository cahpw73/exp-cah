package ch.swissbytes.Service.business.security;

import ch.swissbytes.Service.business.permission.PermissionDao;
import ch.swissbytes.domain.model.entities.OptionsEntity;
import ch.swissbytes.domain.model.entities.RoleEntity;
import ch.swissbytes.domain.types.ModuleSystemEnum;
import ch.swissbytes.fqm.boundary.UserSession;
import org.apache.commons.lang.StringUtils;
import org.picketlink.Identity;

import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by alvaro on 7/1/2015.
 */
@SessionScoped
public class SecurityService implements Serializable{

    @Inject
    private Identity identity;

    private List<OptionsEntity> permissions;
    @Inject
    private PermissionDao dao;

    @Inject
    private UserSession userSession;

    private ModuleSystemEnum moduleSystemEnum;

    @PostConstruct
    public void create(){
    }

    public void loadPermissions(String user){
        ModuleSystemEnum currentModule=ModuleSystemEnum.valueOf(userSession.getCurrentModule());
        if(moduleSystemEnum==null||currentModule.ordinal()!=moduleSystemEnum.ordinal()) {
            List<RoleEntity> roles = dao.getRolesBy(user, currentModule);
            List<Integer> idRoles = new ArrayList<>();
            for (RoleEntity role : roles) {
                idRoles.add(role.getId());
            }
            permissions = dao.findPermissions(idRoles);
            moduleSystemEnum=currentModule;
        }else{
            System.out.println("already loaded ");
        }
    }

    public boolean canAccess(String url,String user){
        boolean hasPermission=false;
        loadPermissions(user);
        for(OptionsEntity optionsEntity:permissions){
            if(StringUtils.isNotEmpty(optionsEntity.getUrl())&&StringUtils.isNotBlank(optionsEntity.getUrl())&&optionsEntity.getUrl().equalsIgnoreCase(url.trim())) {
                hasPermission=true;
                break;
            }
        }
        return hasPermission;
    }

    public boolean isLoggedIn(){
        return true;
    }
}
