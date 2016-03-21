package ch.swissbytes.Service.business.mainDocument;

import ch.swissbytes.Service.infrastructure.Filter;
import ch.swissbytes.Service.infrastructure.GenericDao;
import ch.swissbytes.domain.model.entities.MainDocumentEntity;
import ch.swissbytes.domain.model.entities.ProjectDocumentEntity;
import ch.swissbytes.domain.types.StatusEnum;
import org.apache.commons.lang3.StringUtils;

import javax.persistence.Query;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Created by christian on 27/01/16.
 */

public class MainDocumentDao extends GenericDao<MainDocumentEntity> implements Serializable {

    private static final Logger log = Logger.getLogger(MainDocumentDao.class.getName());


    public void doSave(MainDocumentEntity entity) {
        super.save(entity);
    }

    public void doUpdate(MainDocumentEntity detachedEntity) {
        MainDocumentEntity entity = super.merge(detachedEntity);
        super.update(entity);
    }

    public List<MainDocumentEntity> getMainDocumentList() {
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT x ");
        sb.append("FROM MainDocumentEntity x ");
        sb.append("WHERE x.status=:ENABLED ");
        sb.append("AND x.project is null ");
        sb.append("ORDER BY x.code ");
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("ENABLED", StatusEnum.ENABLE);
        return super.findBy(sb.toString(), map);
    }


    public List<MainDocumentEntity> findByCodeButWithNoId(String code, Long id) {
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT x ");
        sb.append("FROM MainDocumentEntity x ");
        sb.append("WHERE x.status=:ENABLED ");
        sb.append("AND trim(lower(x.code))=:CODE ");
        sb.append("AND NOT x.id=:MAIN_DOC_ID ");
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("ENABLED", StatusEnum.ENABLE);
        map.put("CODE", code.toLowerCase().trim());
        map.put("MAIN_DOC_ID", id != null ? id : 0L);
        return super.findBy(sb.toString(), map);
    }

    public List<MainDocumentEntity> findByCode(final String code) {
        StringBuilder sb = new StringBuilder();
        sb.append(" SELECT x ");
        sb.append(" FROM MainDocumentEntity x ");
        sb.append(" WHERE x.status=:ENABLED ");
        sb.append(" AND x.project is null ");
        Map<String, Object> map = new HashMap<>();
        map.put("ENABLED", StatusEnum.ENABLE);
        if (StringUtils.isNotEmpty(code)) {
            sb.append("AND lower(x.code) LIKE :CODE ");
            map.put("CODE", "%" + code.toLowerCase().trim() + "%");
        }
        return super.findBy(sb.toString(), map);
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

    public List<MainDocumentEntity> findByProject() {
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT x ");
        sb.append("FROM MainDocumentEntity x ");
        sb.append("WHERE x.status=:ENABLED ");
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("ENABLED", StatusEnum.ENABLE);
        return super.findBy(sb.toString(), map);
    }

    public List<MainDocumentEntity> findMainDocumentToCrate() {
        StringBuilder sb = new StringBuilder();
        sb.append(" SELECT x ");
        sb.append(" FROM MainDocumentEntity x ");
        sb.append(" WHERE x.status=:ENABLED ");
        sb.append(" AND x.project is null ");
        sb.append(" ORDER BY x.code ");
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("ENABLED", StatusEnum.ENABLE);
        return super.findBy(sb.toString(), map);
    }

    public List<MainDocumentEntity> findMainDocumentToEdit(final Long projectId) {
        StringBuilder sb = new StringBuilder();
        sb.append(" SELECT x ");
        sb.append(" FROM MainDocumentEntity x ");
        sb.append(" WHERE x.status=:ENABLED ");
        sb.append(" AND (x.project is null ");
        sb.append(" OR x.project.id = :PROJECT_ID) ");
        sb.append("ORDER BY x.code ");
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("ENABLED", StatusEnum.ENABLE);
        map.put("PROJECT_ID", projectId);
        return super.findBy(sb.toString(), map);
    }

    public List<MainDocumentEntity> findByProjectIdAndCode(Long projectId, String code) {
        StringBuilder sb = new StringBuilder();
        sb.append(" SELECT x ");
        sb.append(" FROM MainDocumentEntity x ");
        sb.append(" WHERE x.status=:ENABLED ");
        sb.append(" AND x.project.id = :PROJECT_ID ");
        sb.append(" AND x.code = :CODE ");
        sb.append("ORDER BY x.code ");
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("ENABLED", StatusEnum.ENABLE);
        map.put("PROJECT_ID", projectId);
        map.put("CODE", code);
        return super.findBy(sb.toString(), map);
    }

    public List<ProjectDocumentEntity> findProjectDocumentByAttachmentIdOrMainDocumentId(final Long attachId,final Long mainId) {
        StringBuilder sb = new StringBuilder();
        sb.append(" SELECT p ");
        sb.append(" FROM ProjectDocumentEntity p ");
        sb.append(" WHERE p.status = :ENABLE ");
        sb.append(" AND (p.attachmentProjectDocument.id = :ATTACH_ID ");
        sb.append(" OR p.mainDocumentEntity.id =:MAIN_ID) ");
        Map<String, Object> params = new HashMap<>();
        params.put("ENABLE", StatusEnum.ENABLE);
        params.put("ATTACH_ID", attachId);
        params.put("MAIN_ID",mainId);
        return super.findBy(sb.toString(), params);
    }
}
