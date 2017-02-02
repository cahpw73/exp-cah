package ch.swissbytes.Service.business.projectDocument;

import ch.swissbytes.Service.business.mainDocument.AttachmentMainDocumentService;
import ch.swissbytes.Service.business.mainDocument.MainDocumentService;
import ch.swissbytes.domain.model.entities.AttachmentMainDocumentEntity;
import ch.swissbytes.domain.model.entities.MainDocumentEntity;
import ch.swissbytes.domain.model.entities.ProjectDocumentEntity;
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
public class ProjectDocumentService implements Serializable {

    @Inject
    private ProjectDocumentDao dao;

    @Inject
    private AttachmentMainDocumentService attachmentService;

    @Transactional
    public void doSave(ProjectDocumentEntity entity) {
        if (entity != null) {
            entity.setStatus(StatusEnum.ENABLE);
            entity.setLastUpdate(new Date());
            dao.doSave(entity);
        }
    }

    @Transactional
    public void doSaveNewProjectDocWithProject(ProjectDocumentEntity entity) {
        if (entity != null) {
            entity.setStatus(StatusEnum.ENABLE);
            entity.setLastUpdate(new Date());
            dao.doSave(entity);
            MainDocumentEntity mainDocument = new MainDocumentEntity();
            mainDocument.setCode(entity.getCode().toUpperCase());
            mainDocument.setLastUpdate(new Date());
            mainDocument.setStatus(StatusEnum.ENABLE);
            mainDocument.setDescription(entity.getDescription());
            //mainDocument.setDescriptionDocument(entity.getDescriptionDocument());
            mainDocument.setProject(entity.getProject());
            dao.saveAndFlush(mainDocument);
        }
    }

    @Transactional
    public void doSaveWithPdf(ProjectDocumentEntity entity, AttachmentMainDocumentEntity attachmentMainDocumentEntity) {
        if (entity != null) {
            attachmentService.doSave(attachmentMainDocumentEntity);
            entity.setAttachmentProjectDocument(attachmentMainDocumentEntity);
            entity.setStatus(StatusEnum.ENABLE);
            entity.setLastUpdate(new Date());
            dao.doSave(entity);
            MainDocumentEntity mainDocument = new MainDocumentEntity();
            mainDocument.setCode(entity.getCode().toUpperCase());
            mainDocument.setLastUpdate(new Date());
            mainDocument.setStatus(StatusEnum.ENABLE);
            mainDocument.setAttachmentMainDocument(attachmentMainDocumentEntity);
            mainDocument.setProject(entity.getProject());
            dao.saveAndFlush(mainDocument);
        }
    }

    public void doUpdate(ProjectDocumentEntity detachedEntity) {
        if (detachedEntity != null) {
            ProjectDocumentEntity entity = dao.merge(detachedEntity);
            entity.setLastUpdate(new Date());
            dao.doUpdate(entity);
        }
    }

    @Transactional
    public List<ProjectDocumentEntity> findByProjectId(final Long id) {
        return dao.findByProjectId(id);
    }

    @Transactional
    public List<ProjectDocumentEntity> findByProjectIdToEdit(final Long id, final Long poId) {
        return dao.findByProjectIdToEdit(id, poId);
    }

    @Transactional
    public List<ProjectDocumentEntity> findByProjectIdToCreate(final Long id) {
        return dao.findByProjectIdToCreate(id);
    }

    @Transactional
    public ProjectDocumentEntity findById(Long id) {
        List<ProjectDocumentEntity> list = dao.findById(ProjectDocumentEntity.class, id);
        return list.isEmpty() ? null : list.get(0);
    }

    @Transactional
    public List<ProjectDocumentEntity> doBulkUpdateCode(MainDocumentEntity mainDocument){
        List<ProjectDocumentEntity> list = dao.findByMainDocumentIdOnly(mainDocument.getId());
        for(ProjectDocumentEntity p : list){
            p.setCode(mainDocument.getCode().toUpperCase());
            dao.doUpdate(p);
        }
        return list;
    }

    @Transactional
    public List<ProjectDocumentEntity> findByProjectDocIdAndPoId(final Long projectDocId, final Long poId) {
        return dao.findByProjectDocIdAndPoId(projectDocId,poId);
    }

    @Transactional
    public List<ProjectDocumentEntity> getAll() {
        return dao.getAll();
    }

}
