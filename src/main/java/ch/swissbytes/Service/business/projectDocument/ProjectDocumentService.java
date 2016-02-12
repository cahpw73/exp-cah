package ch.swissbytes.Service.business.projectDocument;

import ch.swissbytes.domain.model.entities.ClausesEntity;
import ch.swissbytes.domain.model.entities.ProjectDocumentEntity;
import ch.swissbytes.domain.model.entities.ProjectTextSnippetEntity;
import ch.swissbytes.domain.types.StatusEnum;

import javax.inject.Inject;
import javax.transaction.Transactional;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

/**
 * Created by christian on 12/09/14.
 */
public class ProjectDocumentService implements Serializable {

    private static final Logger log = Logger.getLogger(ProjectDocumentService.class.getName());

    @Inject
    private ProjectDocumentDao dao;

    @Transactional
    public void doSave(ProjectDocumentEntity entity) {
        if (entity != null) {
            entity.setStatus(StatusEnum.ENABLE);
            entity.setLastUpdate(new Date());
            dao.doSave(entity);
        }
    }

    public void doUpdate(ProjectDocumentEntity entity) {
        if (entity != null) {
            entity.setLastUpdate(new Date());
            dao.doUpdate(entity);
        }
    }

    @Transactional
    public List<ProjectDocumentEntity> findByProjectId(final Long id) {
        return dao.findByProjectId(id);
    }

    @Transactional
    public ProjectDocumentEntity findById(Long id) {
        List<ProjectDocumentEntity> list = dao.findById(ProjectDocumentEntity.class, id);
        return list.isEmpty() ? null : list.get(0);
    }
}
