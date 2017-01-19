package ch.swissbytes.Service.business.mainDocument;


import ch.swissbytes.Service.business.Service;
import ch.swissbytes.domain.model.entities.AttachmentMainDocumentEntity;
import ch.swissbytes.domain.model.entities.MainDocumentEntity;
import ch.swissbytes.domain.model.entities.PODocumentEntity;
import ch.swissbytes.domain.model.entities.ProjectDocumentEntity;
import ch.swissbytes.domain.types.StatusEnum;
import org.omnifaces.util.Messages;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.transaction.Transactional;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

/**
 * Created by christian  on 27/01/16.
 */
public class MainDocumentService extends Service<MainDocumentEntity> implements Serializable {

    private static final Logger log = Logger.getLogger(MainDocumentService.class.getName());

    @Inject
    private MainDocumentDao dao;

    @Inject
    private AttachmentMainDocumentService attachmentService;

    @PostConstruct
    public void create() {
        super.initialize(dao);
    }

    @Transactional
    public void delete(MainDocumentEntity entity) {
        if (canDeleteMainDocumentSelected(entity)) {
            entity.setStatus(StatusEnum.DELETED);
            entity.setLastUpdate(new Date());
            if (entity.getAttachmentMainDocument() != null) {
                AttachmentMainDocumentEntity toDelete = entity.getAttachmentMainDocument();
                entity.setAttachmentMainDocument(null);
                attachmentService.delete(toDelete);
            }
            dao.update(entity);
        } else {
            Messages.addFlashGlobalError("Can not delete " + entity.getCode() + " because it is already being used");
        }
    }

    @Transactional
    public void doSave(MainDocumentEntity entity) {
        entity.setLastUpdate(new Date());
        entity.setStatus(StatusEnum.ENABLE);
        entity.setCode(entity.getCode().toUpperCase());
        super.doSave(entity);
    }

    @Transactional
    public void doSaveWithPdf(MainDocumentEntity entity, AttachmentMainDocumentEntity attachmentMainDocumentEntity) {
        attachmentService.doSave(attachmentMainDocumentEntity);
        entity.setAttachmentMainDocument(attachmentMainDocumentEntity);
        entity.setLastUpdate(new Date());
        entity.setStatus(StatusEnum.ENABLE);
        entity.setCode(entity.getCode().toUpperCase());
        super.doSave(entity);
    }


    @Override
    public MainDocumentEntity save(MainDocumentEntity entity) {
        entity.setLastUpdate(new Date());
        entity.setStatus(StatusEnum.ENABLE);
        return super.save(entity);
    }

    @Transactional
    public MainDocumentEntity findById(Long id) {
        List<MainDocumentEntity> list = dao.findById(MainDocumentEntity.class, id);
        return !list.isEmpty() ? list.get(0) : null;
    }

    @Transactional
    public List<MainDocumentEntity> getAll() {
        return dao.getAll();
    }

    @Transactional
    public ProjectDocumentEntity findProjectDocumentEntityById(Long id) {
        List<ProjectDocumentEntity> list = dao.findById(ProjectDocumentEntity.class, id);
        return !list.isEmpty() ? list.get(0) : null;
    }

    @Transactional
    public PODocumentEntity findPODocumentEntityById(Long id) {
        List<PODocumentEntity> list = dao.findById(PODocumentEntity.class, id);
        return !list.isEmpty() ? list.get(0) : null;
    }

    @Transactional
    public List<MainDocumentEntity> getMainDocumentList() {
        return dao.getMainDocumentList();
    }

    @Transactional
    public boolean isCodeDuplicated(Long id, String code) {
        return !dao.findByCodeButWithNoId(code, id).isEmpty();
    }

    @Transactional
    public List<MainDocumentEntity> findByText(final String code) {
        return dao.findByCode(code);
    }

    @Transactional
    public List findByProjectId() {
        return dao.findByProject();
    }

    @Transactional
    public List<MainDocumentEntity> findMainDocumentsToCreate() {
        return dao.findMainDocumentToCrate();
    }

    @Transactional
    public List<MainDocumentEntity> findMainDocumentsToEdit(final Long projectId) {
        return dao.findMainDocumentToEdit(projectId);
    }

    public AttachmentMainDocumentEntity findAttachmentMainDocument(Long attachmentMainDocumentId) {
        return attachmentService.findById(attachmentMainDocumentId);
    }

    @Transactional
    public MainDocumentEntity findByProjectIdAndCode(Long projectId, String code) {
        List<MainDocumentEntity> list = dao.findByProjectIdAndCode(projectId, code);
        return list.isEmpty() ? null : list.get(0);
    }

    public boolean canDeleteMainDocumentSelected(MainDocumentEntity entity) {
        Long attachId = entity.getAttachmentMainDocument() != null ? entity.getAttachmentMainDocument().getId() : -1l;
        return dao.findProjectDocumentByAttachmentIdOrMainDocumentId(attachId, entity.getId()).isEmpty() ? true : false;
    }

    @Transactional
    public List<MainDocumentEntity> findByMainDocIdAndProjectId(final Long mainDocId, final Long projectId) {
        return dao.findByMaindDocIdAndProjectId(mainDocId,projectId);
    }
}
