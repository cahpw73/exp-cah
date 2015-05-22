package ch.swissbytes.Service.business.userRole;


import ch.swissbytes.Service.business.Service;
import ch.swissbytes.Service.business.brand.BrandDao;
import ch.swissbytes.domain.model.entities.BrandEntity;
import ch.swissbytes.domain.model.entities.UserRoleEntity;
import ch.swissbytes.domain.types.ModuleSystemEnum;

import javax.inject.Inject;
import javax.transaction.Transactional;
import java.io.Serializable;
import java.util.List;
import java.util.logging.Logger;

/**
 * Created by christian  on 11/05/15.
 */
public class UserRoleService extends Service<UserRoleEntity> implements Serializable{

    private static final Logger log = Logger.getLogger(UserRoleService.class.getName());

    @Inject
    private UserRoleDao userRoleDao;


    public void doSave(UserRoleEntity userRoleEntity){
        log.info("doSave");
         userRoleDao.doSave(userRoleEntity);
    }

    public void doUpdate(UserRoleEntity detachedEntity){
        log.info("doUpdate");
         userRoleDao.doUpdate(detachedEntity);
    }

    public UserRoleEntity findByUserIdAndModuleSystem(final Long userId, final ModuleSystemEnum moduleSystem){
        List<UserRoleEntity> userRoleList =  userRoleDao.findByUserIdAndModuleSystem(userId, moduleSystem);
        UserRoleEntity userRole = new UserRoleEntity();
        if(!userRoleList.isEmpty()){
            userRole = userRoleList.get(0);
        }
        return userRole;
    }

    public List<UserRoleEntity> findListByUserId(Long userId) {
        List<UserRoleEntity> userRoleList =  userRoleDao.findByUserId(userId);
        return userRoleList;
    }
}
