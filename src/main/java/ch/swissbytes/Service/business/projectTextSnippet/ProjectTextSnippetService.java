package ch.swissbytes.Service.business.projectTextSnippet;

import ch.swissbytes.Service.business.projectCurrency.ProjectCurrencyDao;
import ch.swissbytes.domain.model.entities.ClausesEntity;
import ch.swissbytes.domain.model.entities.ProjectCurrencyEntity;
import ch.swissbytes.domain.model.entities.ProjectTextSnippetEntity;
import ch.swissbytes.domain.types.StatusEnum;

import javax.inject.Inject;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

/**
 * Created by christian on 12/09/14.
 */
public class ProjectTextSnippetService implements Serializable {

    private static final Logger log = Logger.getLogger(ProjectTextSnippetService.class.getName());

    @Inject
    private ProjectTextSnippetDao dao;

    public void doSave(ProjectTextSnippetEntity entity){
        if(entity != null){
            entity.setStatus(StatusEnum.ENABLE);
            entity.setLastUpdate(new Date());
            dao.doSave(entity);
        }
    }

    public void doUpdate(ProjectTextSnippetEntity entity){
        if(entity != null){
            entity.setLastUpdate(new Date());
            dao.doUpdate(entity);
        }
    }

    public List<ProjectTextSnippetEntity> findByProjectId(final Long id) {
        return dao.findByProjectId(id);
    }


    public List<ProjectTextSnippetEntity> findTextSnippetByClausesId(List<ClausesEntity> clausesEntities,Long projectId) {
        log.info("findTextSnippetByClausesId");
        List<ProjectTextSnippetEntity> list = new ArrayList<>();
        /*for(ClausesEntity entity : clausesEntities){
            log.info("TextSnippetID "+entity.getTextSnippet().getId());
            ProjectTextSnippetEntity ps;
            ps= dao.findByTextSnippetId(entity.getTextSnippet().getId(),projectId);
            list.add(ps);
        }*/
        return list;
    }

    public ProjectTextSnippetEntity findById(Long id) {
        List<ProjectTextSnippetEntity> list = dao.findById(ProjectTextSnippetEntity.class,id);
        ProjectTextSnippetEntity entity = null;
        if(!list.isEmpty()){
            entity = list.get(0);
        }
        return entity;
    }
}
