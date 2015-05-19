package ch.swissbytes.Service.business.moduleGrantedAccess;


import ch.swissbytes.Service.business.Service;
import ch.swissbytes.Service.business.brand.BrandDao;
import ch.swissbytes.domain.model.entities.BrandEntity;
import ch.swissbytes.domain.model.entities.ModuleGrantedAccessEntity;
import ch.swissbytes.domain.types.ModuleSystemEnum;

import javax.inject.Inject;
import javax.transaction.Transactional;
import java.io.Serializable;
import java.util.List;
import java.util.logging.Logger;

/**
 * Created by christian  on 15/05/15.
 */
public class ModuleGrantedAccessService extends Service<ModuleGrantedAccessEntity> implements Serializable{

    private static final Logger log = Logger.getLogger(ModuleGrantedAccessService.class.getName());

    @Inject
    private ModuleGrantedAccessDao moduleGrantedAccessDao;

    public void doSave(ModuleGrantedAccessEntity moduleGrantedAccessEntity){
        log.info("doSave");
        moduleGrantedAccessDao.doSave(moduleGrantedAccessEntity);
    }

    public void doUpdate(ModuleGrantedAccessEntity detachedEntity){
        log.info("doUpdate");
        moduleGrantedAccessDao.doUpdate(detachedEntity);
    }

    public ModuleGrantedAccessEntity findByUserIdAndModuleSystem(final Long userId, final ModuleSystemEnum moduleSystemEnum){
        List<ModuleGrantedAccessEntity> moduleGrantedAccessEntities = moduleGrantedAccessDao.findByUserIAndModuleSystem(userId, moduleSystemEnum);
        ModuleGrantedAccessEntity entity = null;
        if(!moduleGrantedAccessEntities.isEmpty()){
            entity = moduleGrantedAccessEntities.get(0);
        }
        return entity;
    }

    public List<ModuleGrantedAccessEntity> findListByUserId(Long userId) {
        List<ModuleGrantedAccessEntity> moduleGrantedAccessEntities = moduleGrantedAccessDao.findByUserId(userId);
        return moduleGrantedAccessEntities;
    }
}
