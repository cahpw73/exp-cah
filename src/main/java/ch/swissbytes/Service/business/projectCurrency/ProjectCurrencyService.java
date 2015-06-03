package ch.swissbytes.Service.business.projectCurrency;

import ch.swissbytes.Service.business.project.ProjectDao;
import ch.swissbytes.domain.model.entities.ProjectCurrencyEntity;
import ch.swissbytes.domain.model.entities.ProjectEntity;
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
public class ProjectCurrencyService implements Serializable {

    private static final Logger log = Logger.getLogger(ProjectCurrencyService.class.getName());

    @Inject
    private ProjectCurrencyDao dao;

    public void doSave(ProjectCurrencyEntity entity){
        if(entity != null){
            entity.setStatus(StatusEnum.ENABLE);
            entity.setLastUpdate(new Date());
            dao.doSave(entity);
        }
    }

    public void doUpdate(ProjectCurrencyEntity entity){
        if(entity != null){
            entity.setLastUpdate(new Date());
            dao.doUpdate(entity);
        }
    }

    public List<ProjectCurrencyEntity> findByProjectId(final Long id) {
        return dao.findByProjectId(id);
    }

    public ProjectCurrencyEntity findById(final Long id){
        List<ProjectCurrencyEntity>list=dao.findById(ProjectCurrencyEntity.class, id);
        return list.isEmpty()?null:list.get(0);
    }



}
