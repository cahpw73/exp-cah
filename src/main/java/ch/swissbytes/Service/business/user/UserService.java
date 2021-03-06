package ch.swissbytes.Service.business.user;

import ch.swissbytes.Service.business.moduleGrantedAccess.ModuleGrantedAccessService;
import ch.swissbytes.Service.business.userRole.UserRoleService;
import ch.swissbytes.domain.model.entities.ModuleGrantedAccessEntity;
import ch.swissbytes.domain.model.entities.RoleEntity;
import ch.swissbytes.domain.model.entities.UserEntity;
import ch.swissbytes.domain.model.entities.UserRoleEntity;
import ch.swissbytes.domain.types.ModuleSystemEnum;
import ch.swissbytes.domain.types.StatusEnum;

import javax.inject.Inject;
import javax.transaction.Transactional;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

/**
 * Created by christian on 12/09/14.
 */
public class UserService implements Serializable {

    private static final Logger log = Logger.getLogger(UserService.class.getName());

    @Inject
    private UserDao userDao;

    @Inject
    private ModuleGrantedAccessService moduleGrantedAccessService;

    @Inject
    private UserRoleService userRoleService;

    @Transactional
    public void doSave(UserEntity user) {
        if (user != null) {
            user.setLastUpdate(new Date());
            userDao.doSave(user);
        }
    }

    @Transactional
    public void doUpdate(UserEntity detachedUser) {
        userDao.doUpdate(detachedUser);
    }

    @Transactional
    public void doSaveUser(UserEntity user, List<ModuleGrantedAccessEntity> moduleGrAcList, List<UserRoleEntity> userRoleList) {
        if (user != null) {
            user.setLastUpdate(new Date());
            userDao.doSave(user);
            for (ModuleGrantedAccessEntity mga : moduleGrAcList) {
                mga.setUserEntity(user);
                moduleGrantedAccessService.doSave(mga);
            }
            for (UserRoleEntity ure : userRoleList) {
                ure.setUser(user);
                userRoleService.doSave(ure);
            }
        }
    }

    @Transactional
    public void doUpdateUser(UserEntity user, List<ModuleGrantedAccessEntity> moduleGrAcList, List<UserRoleEntity> userRoleList) {
        if (user != null) {
            user.setLastUpdate(new Date());
            userDao.doUpdate(user);
            for (ModuleGrantedAccessEntity mga : moduleGrAcList) {
                if (mga != null) {
                    if(mga.getId()!=null){
                        moduleGrantedAccessService.doUpdate(mga);
                    }else{
                        moduleGrantedAccessService.doSave(mga);
                    }
                }
            }
            for (UserRoleEntity ure : userRoleList) {
                if (ure != null) {
                    if(ure.getId()!=null) {
                        userRoleService.doUpdate(ure);
                    }else{
                        ure.setModuleSystem(ModuleSystemEnum.PROCUREMENT);
                        ure.setUser(user);
                        userRoleService.doSave(ure);
                    }
                }
            }
        }
    }


    public List<UserEntity> findUserByEmail(final String email) {
        return userDao.findUserByEmail(email);
    }

    public boolean existsEmail(final String email) {
        log.info("existsEmail(final String email)");
        List<UserEntity> list = userDao.findUserByEmail(email);
        boolean validateEmail = false;
        if (!list.isEmpty()) {
            validateEmail = true;
        }
        return validateEmail;
    }

    public boolean existsUsername(final String username) {
        List<UserEntity> list = userDao.findUserByUserName(username);
        boolean validateUsername = false;
        if (!list.isEmpty()) {
            validateUsername = verifyStatusDelete(list);
        }
        return validateUsername;
    }

    public boolean validateDuplicityEmail(final String email, final Long id) {
        log.info("existsEmail(final String email)");
        List<UserEntity> list = userDao.findDuplicityEmail(email, id);
        boolean validateEmail = false;
        if (!list.isEmpty()) {
            validateEmail = true;
        }
        return validateEmail;
    }

    public boolean validateDuplicityUsername(final String username, final Long id) {
        List<UserEntity> list = userDao.findDuplicityUserName(username, id);
        boolean validateUsername = false;
        if (!list.isEmpty()) {
            validateUsername = verifyStatusDelete(list);
        }
        return validateUsername;
    }

    private boolean verifyStatusDelete(final List<UserEntity> list) {
        boolean result = false;
        for (UserEntity user : list) {
            if (!StatusEnum.DELETED.equalsTo(user.getStatus().getId())) {
                result = true;
                break;
            }
        }
        return result;
    }

    public List<RoleEntity> findRoleById(Integer id) {
        return userDao.findRoleById(id);
    }

    public List<UserEntity> getUserList() {
        return userDao.getUserList();
    }


    public boolean canAccess(final String userName, final String password) {
        return userDao.getUser(userName, password) != null;
    }

    public UserEntity findUserById(Long userId) {
        List<UserEntity> list = userDao.findById(UserEntity.class, userId);
        UserEntity entity = null;
        if (!list.isEmpty()) {
            entity = list.get(0);
        }
        return entity;
    }


    public UserEntity getByUserName(final String userName) {
        return userDao.getUser(userName);
    }

    public UserEntity getUserEntity(String username, String pass) {
        return userDao.getUser(username, pass);
    }

    public List<UserEntity> findAllUser() {
        return userDao.findAllUser();
    }

    public List<UserEntity> doSearch(final String searchTerm, final StatusEnum userStatus) {
        return userDao.findBySearchTerm(searchTerm, userStatus);
    }

    public UserEntity findByUsername(String username) {
        List<UserEntity> list = userDao.findUserByUserName(username);
        UserEntity entity = null;
        if (!list.isEmpty()) {
            entity = list.get(0);
        }
        return entity;
    }


    public List<ModuleGrantedAccessEntity> getModulesGranted(final String userName) {
        return userDao.getModuleGrantedAccess(userName);
    }
}
