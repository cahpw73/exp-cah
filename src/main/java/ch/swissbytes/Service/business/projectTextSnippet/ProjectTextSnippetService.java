package ch.swissbytes.Service.business.projectTextSnippet;

import ch.swissbytes.domain.model.entities.ProjectTextSnippetEntity;
import ch.swissbytes.domain.model.entities.TextSnippetEntity;
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
public class ProjectTextSnippetService implements Serializable {

    public static final Logger log = Logger.getLogger(ProjectTextSnippetService.class.getName());

    @Inject
    private ProjectTextSnippetDao dao;

    public void doSave(ProjectTextSnippetEntity entity){
        if(entity != null){
            entity.setStatus(StatusEnum.ENABLE);
            entity.setLastUpdate(new Date());
            dao.doSave(entity);
        }
    }

    @Transactional
    public void doSaveNewProjectText(ProjectTextSnippetEntity entity){
        if(entity != null){
            if(entity.getId()<0){
                entity.setId(null);
            }
            entity.setStatus(StatusEnum.ENABLE);
            entity.setLastUpdate(new Date());
            dao.doSave(entity);
        }
    }

    @Transactional
    public void doUpdate(ProjectTextSnippetEntity detachedEntity){
        if(detachedEntity != null){
            ProjectTextSnippetEntity entity = dao.merge(detachedEntity);
            entity.setLastUpdate(new Date());
            dao.doUpdate(entity);
        }
    }

    @Transactional
    public List<ProjectTextSnippetEntity> findByProjectId(final Long id) {
        return dao.findByProjectId(id);
    }

    @Transactional
    public List<ProjectTextSnippetEntity> getAllProjectText() {
        return dao.getAllProjectText();
    }

    @Transactional
    public List<ProjectTextSnippetEntity> findByProjectIdToEditPo(final Long id,final Long poId) {
        return dao.findByProjectIdToEditPO(id, poId);
    }

    @Transactional
    public List<ProjectTextSnippetEntity> findByProjectIdCreatePO(final Long id) {
        return dao.findByProjectIdToCreatePO(id);
    }

    @Transactional
    public ProjectTextSnippetEntity findById(Long id) {
        List<ProjectTextSnippetEntity> list = dao.findById(ProjectTextSnippetEntity.class,id);
        ProjectTextSnippetEntity entity = null;
        if(!list.isEmpty()){
            entity = list.get(0);
        }
        return entity;
    }

    @Transactional
    public List<ProjectTextSnippetEntity> doBulkUpdateCode(TextSnippetEntity textSnippet){
        List<ProjectTextSnippetEntity> list = dao.findByTextSnippetIdOnly(textSnippet.getId());
        for(ProjectTextSnippetEntity p : list){
            p.setCode(textSnippet.getCode().toUpperCase());
            dao.doUpdate(p);
        }
        return list;
    }

    @Transactional
    public boolean canDeleteProjectTextCreateOnPO(final Long projectTextId){
        List<ProjectTextSnippetEntity> list = dao.isProjectTextCreateInPO(projectTextId);
        return !list.isEmpty();
    }
}
