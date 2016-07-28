package ch.swissbytes.Service.business.projectDocument;

import ch.swissbytes.Service.infrastructure.Filter;
import ch.swissbytes.Service.infrastructure.GenericDao;
import ch.swissbytes.domain.model.entities.ProjectDocumentEntity;
import ch.swissbytes.domain.types.StatusEnum;

import javax.persistence.Query;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Created by christian on 27/01/16.
 */

public class ProjectDocumentDao extends GenericDao<ProjectDocumentEntity> implements Serializable {

    private static final Logger log = Logger.getLogger(ProjectDocumentDao.class.getName());


    public void doSave(ProjectDocumentEntity entity) {
        super.save(entity);
    }

    public void doUpdate(ProjectDocumentEntity detachedEntity) {
        ProjectDocumentEntity entity = super.merge(detachedEntity);
        super.saveAndFlush(entity);
    }

    public List<ProjectDocumentEntity> findByProjectIdToEdit(final Long projectId, final Long poId) {
        StringBuilder sb = new StringBuilder();
        sb.append(" SELECT p ");
        sb.append(" FROM ProjectDocumentEntity p ");
        sb.append(" WHERE p.status = :ENABLE ");
        sb.append(" AND p.project.id = :PROJECT_ID ");
        sb.append(" AND (p.purchaseOrder.id = :PO_ID ");
        sb.append(" OR p.purchaseOrder is null) ");
        Map<String, Object> params = new HashMap<>();
        params.put("ENABLE", StatusEnum.ENABLE);
        params.put("PROJECT_ID", projectId);
        params.put("PO_ID",poId);
        return super.findBy(sb.toString(), params);
    }

    public List<ProjectDocumentEntity> findByProjectIdToCreate(final Long projectId) {
        StringBuilder sb = new StringBuilder();
        sb.append(" SELECT p ");
        sb.append(" FROM ProjectDocumentEntity p ");
        sb.append(" WHERE p.status = :ENABLE ");
        sb.append(" AND p.project.id = :PROJECT_ID ");
        sb.append(" AND p.purchaseOrder is null ");
        Map<String, Object> params = new HashMap<>();
        params.put("ENABLE", StatusEnum.ENABLE);
        params.put("PROJECT_ID", projectId);
        return super.findBy(sb.toString(), params);
    }

    public List<ProjectDocumentEntity> findByProjectId(final Long projectId) {
        StringBuilder sb = new StringBuilder();
        sb.append(" SELECT p ");
        sb.append(" FROM ProjectDocumentEntity p ");
        sb.append(" WHERE p.status = :ENABLE ");
        sb.append(" AND p.project.id = :PROJECT_ID ");
        sb.append(" AND p.purchaseOrder is null ");
        Map<String, Object> params = new HashMap<>();
        params.put("ENABLE", StatusEnum.ENABLE);
        params.put("PROJECT_ID", projectId);
        return super.findBy(sb.toString(), params);
    }

    public ProjectDocumentEntity findByMainDocumentId(Long id, final Long projectId) {
        StringBuilder sb = new StringBuilder();
        sb.append(" SELECT p ");
        sb.append(" FROM ProjectDocumentEntity p ");
        sb.append(" WHERE p.status = :ENABLE ");
        sb.append(" AND p.mainDocumentEntity.id = :MAIN_DOCUMENT_ID ");
        sb.append(" AND p.project.id = :PROJECT_ID ");
        Map<String, Object> params = new HashMap<>();
        params.put("ENABLE", StatusEnum.ENABLE);
        params.put("MAIN_DOCUMENT_ID", id);
        params.put("PROJECT_ID", projectId);
        List<ProjectDocumentEntity> list = super.findBy(sb.toString(), params);
        return list.isEmpty() ? null : list.get(0);
    }

    public List<ProjectDocumentEntity> findByMainDocumentIdOnly(Long id) {
        StringBuilder sb = new StringBuilder();
        sb.append(" SELECT p ");
        sb.append(" FROM ProjectDocumentEntity p ");
        sb.append(" WHERE p.status = :ENABLE ");
        sb.append(" AND p.mainDocumentEntity.id = :MAIN_DOCUMENT_ID ");
        Map<String, Object> params = new HashMap<>();
        params.put("ENABLE", StatusEnum.ENABLE);
        params.put("MAIN_DOCUMENT_ID", id);
        return super.findBy(sb.toString(), params);
    }

    public List<ProjectDocumentEntity> findByProjectDocIdAndPoId(final Long projectDocId, final Long poId) {
        StringBuilder sb = new StringBuilder();
        sb.append(" SELECT p ");
        sb.append(" FROM ProjectDocumentEntity p ");
        sb.append(" WHERE p.status = :ENABLE ");
        sb.append(" AND p.id = :PROJECT_DOC_ID ");
        sb.append(" AND p.purchaseOrder.id = :PO_ID ");
        Map<String, Object> params = new HashMap<>();
        params.put("ENABLE", StatusEnum.ENABLE);
        params.put("PROJECT_DOC_ID", projectDocId);
        params.put("PO_ID",poId);
        return super.findBy(sb.toString(), params);
    }

    @Override
    protected void applyCriteriaValues(Query query, Filter filter) {
    }

    @Override
    protected String getEntity() {
        return null;
    }

    @Override
    protected String addCriteria(Filter filter) {
        return null;
    }

}
