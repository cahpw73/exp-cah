package ch.swissbytes.fqmes.boundary.permission;




import ch.swissbytes.fqmes.control.permission.PermissionService;
import ch.swissbytes.fqmes.model.dao.PermissionDao;
import ch.swissbytes.fqmes.model.entities.PermissionGrantedEntity;
import ch.swissbytes.fqmes.model.entities.RoleEntity;
import org.picketlink.Identity;
import org.picketlink.idm.model.basic.User;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.Stateful;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;


/**
 * @author Christian
 */
@Named
@Stateful
@RequestScoped
public class PermissionBean implements Serializable {

    public static final String NAME = "permissionAction";

    @Inject
    private Identity identity;

    @Inject
    private PermissionDao dao;
     @Inject
    private PermissionService service;

    private Logger log = Logger.getLogger(PermissionBean.class.getName());

    private List<PermissionGrantedEntity> permissions;


    private Date initialDate;

    @PostConstruct
    public void init(){
        log.info("Permission Bean initialize...");
        initialDate=new Date();
        User user=(User)identity.getAccount();
        List<RoleEntity> currentRoles=service.getRolesFor(user.getLoginName());
        permissions=service.getPermissions(collectIds(currentRoles));
        log.info("permission granted for "+user.getLoginName());
        log.info("================================");
        for(PermissionGrantedEntity pe:permissions){
            log.info(pe.getOptions().getName());
        }
        log.info("===================================");

    }

    public boolean hasPermission(Integer option){
       // log.info("public boolean hasPermission(Integer option="+option+")");
        boolean  granted=false;
        for(PermissionGrantedEntity permission:permissions){
            if(permission.getOptions().getId().intValue()==option.intValue()){
                granted=true;
                break;
            }
        }
        return granted;
    }

    public String canAccess(Integer option){
        log.info("public String canAccess(Integer option="+option+")");
        return hasPermission(option)?"GRANTED":"ACCESS_DENIED";
    }

    private List<Integer> collectIds(final List<RoleEntity>roles){
        List<Integer>ids=new ArrayList<>();
        for(RoleEntity role:roles){
            ids.add(role.getId());
        }
        return ids;
    }

    @PreDestroy
    public void destroy(){
        log.info("Permission Bean destroy...");
        Date endDate=new Date();
        Long diff=endDate.getTime()-initialDate.getTime();
        log.info(String.format("duration time for permission loading [%s]",diff.longValue()));
    }











}
