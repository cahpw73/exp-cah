package ch.swissbytes.Service.business.project;

import ch.swissbytes.Service.infrastructure.Filter;
import ch.swissbytes.Service.infrastructure.GenericDao;
import ch.swissbytes.domain.model.entities.BrandEntity;
import ch.swissbytes.domain.model.entities.ProjectEntity;
import ch.swissbytes.domain.types.ProcurementStatus;
import ch.swissbytes.domain.types.StatusEnum;
import org.apache.commons.lang.StringUtils;

import javax.persistence.Query;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Created by alvaro on 9/10/14.
 */

public class ProjectDao extends GenericDao<ProjectEntity> implements Serializable {

    private static final Logger log = Logger.getLogger(ProjectDao.class.getName());


    public void doSave(ProjectEntity projectEntity) {
        super.save(projectEntity);
    }

    public void doUpdate(ProjectEntity detachedEntity) {
        ProjectEntity entity = super.merge(detachedEntity);
        super.saveAndFlush(entity);
    }

    public List<ProjectEntity> getProjectList() {
        StringBuilder sb = new StringBuilder();
        sb.append(" SELECT p ");
        sb.append(" FROM ProjectEntity p ");
        sb.append(" LEFT JOIN p.client c ");
        sb.append(" WHERE p.status = :ENABLE ");
        sb.append(" ORDER BY p.projectNumber");
        Map<String, Object> params = new HashMap<>();
        params.put("ENABLE", StatusEnum.ENABLE);
        return super.findBy(sb.toString(), params);
    }

    public List<ProjectEntity> findByPermissionForUser(final Long userId){
        StringBuilder sb = new StringBuilder();
        sb.append(" SELECT pr ");
        sb.append(" FROM ProjectUserEntity p ");
        sb.append(" LEFT JOIN p.project pr ");
        sb.append(" WHERE pr.status = :ENABLE ");
        sb.append(" AND p.status = :ENABLE ");
        sb.append(" AND p.user.id = :USER_ID ");
        sb.append(" ORDER BY pr.projectNumber");
        Map<String, Object> params = new HashMap<>();
        params.put("ENABLE", StatusEnum.ENABLE);
        params.put("USER_ID", userId);
        return super.findBy(sb.toString(), params);
    }

    public List<ProjectEntity> getAllProjectList() {
        StringBuilder sb = new StringBuilder();
        sb.append(" SELECT p ");
        sb.append(" FROM ProjectEntity p ");
        sb.append(" WHERE p.status = :ENABLE ");
        sb.append(" ORDER BY p.projectNumber");
        Map<String, Object> params = new HashMap<>();
        params.put("ENABLE", StatusEnum.ENABLE);
        return super.findBy(sb.toString(), params);
    }

    public List<ProjectEntity> getProjectsAssignables() {
        StringBuilder sb = new StringBuilder();
        sb.append(" SELECT DISTINCT pr ");
        sb.append(" FROM PurchaseOrderEntity po ");
        sb.append(" INNER JOIN po.projectEntity pr ");
        sb.append(" INNER JOIN po.purchaseOrderProcurementEntity p ");
        sb.append(" WHERE pr.status = :ENABLE ");
        sb.append(" AND po.status.id = :ENABLE_ID ");
        sb.append(" AND p.poProcStatus = :COMMITTED ");
        sb.append(" ORDER BY pr.projectNumber");
        Map<String, Object> params = new HashMap<>();
        params.put("ENABLE", StatusEnum.ENABLE);
        params.put("ENABLE_ID", StatusEnum.ENABLE.getId());
        params.put("COMMITTED", ProcurementStatus.COMMITTED);
        return super.findBy(sb.toString(), params);
    }


    public List<ProjectEntity> findByProjectNumber(final String name) {
        StringBuilder sb = new StringBuilder();
        sb.append(" SELECT p ");
        sb.append(" FROM ProjectEntity p ");
        sb.append(" WHERE LOWER(p.projectNumber) = :PROJECT_NAME ");
        sb.append(" AND p.status = :ENABLE ");
        Map<String, Object> params = new HashMap<>();
        params.put("PROJECT_NAME", name.toLowerCase().trim());
        params.put("ENABLE", StatusEnum.ENABLE);
        return super.findBy(sb.toString(), params);
    }

    public List<ProjectEntity> findByLikeProjectNumber(final String name, final List<Long> ids) {
        StringBuilder sb = new StringBuilder();
        sb.append(" SELECT p ");
        sb.append(" FROM ProjectEntity p ");
        sb.append(" WHERE LOWER(p.projectNumber) like :PROJECT_NAME ");
        sb.append(" AND p.status = :ENABLE ");
        sb.append(" AND NOT p.id IN(:IDS) ");
        Map<String, Object> params = new HashMap<>();
        params.put("PROJECT_NAME", "%" + name.toLowerCase().trim() + "%");
        params.put("ENABLE", StatusEnum.ENABLE);
        params.put("IDS", ids);
        return super.findBy(sb.toString(), params);
    }

    public List<ProjectEntity> findBySearchTerm(final String searchTerm) {
        StringBuilder sb = new StringBuilder();
        sb.append(" SELECT p ");
        sb.append(" FROM ProjectEntity p  ");
        sb.append(" LEFT JOIN p.client.reportLogo rl ");
        sb.append(" LEFT JOIN p.client.clientLogo cl ");
        sb.append(" WHERE NOT p.status = :DELETED ");

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("DELETED", StatusEnum.DELETED);

        if (StringUtils.isNotEmpty(searchTerm) && StringUtils.isNotBlank(searchTerm)) {
            sb.append(" AND (  ");
            sb.append(" LOWER(p.projectNumber) like :PROJECT_NUMBER ");
            sb.append(" OR LOWER(p.title) like :TITLE ");
            sb.append(" OR LOWER(rl.description) like :DESCRIPTION ");
            sb.append(" OR LOWER(cl.description) like :DESCRIPTION ");
            sb.append(" )");

            parameters.put("PROJECT_NUMBER", "%" + searchTerm.toLowerCase().trim() + "%");
            parameters.put("TITLE", "%" + searchTerm.toLowerCase().trim() + "%");
            parameters.put("DESCRIPTION", "%" + searchTerm.toLowerCase().trim() + "%");
        }
        sb.append(" ORDER BY p.projectNumber ");
        return super.findBy(sb.toString(), parameters);
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

    public List<ProjectEntity> findDuplicityProjectNumber(String projectNumber, Long id) {
        StringBuilder sb = new StringBuilder();
        sb.append(" SELECT p ");
        sb.append(" FROM ProjectEntity p ");
        sb.append(" WHERE LOWER(p.projectNumber) = :PROJECT_NUMBER ");
        sb.append(" AND p.status = :ENABLE ");
        sb.append(" AND p.id <> :ID ");
        Map<String, Object> params = new HashMap<>();
        params.put("PROJECT_NUMBER", projectNumber.toLowerCase().trim());
        params.put("ID", id);
        params.put("ENABLE", StatusEnum.ENABLE);
        return super.findBy(sb.toString(), params);
    }

    public List<ProjectEntity> findByLogoId(Long logoId) {
        StringBuilder sb = new StringBuilder();
        sb.append(" SELECT p ");
        sb.append(" FROM ProjectEntity p ");
        sb.append(" WHERE p.status = :ENABLE ");
        sb.append(" AND (p.client.headerLogo.id = :LOGO_ID  ");
        sb.append(" OR p.client.clientLogo.id = :LOGO_ID )");
        Map<String, Object> params = new HashMap<>();
        params.put("LOGO_ID", logoId);
        params.put("ENABLE", StatusEnum.ENABLE);
        return super.findBy(sb.toString(), params);
    }

    public List<ProjectEntity> findByClient(Long clientId) {
        StringBuilder sb = new StringBuilder();
        sb.append(" SELECT p ");
        sb.append(" FROM ProjectEntity p ");
        sb.append(" WHERE p.status = :ENABLE ");
        sb.append(" AND p.client.id = :ID  ");
        Map<String, Object> params = new HashMap<>();
        params.put("ID", clientId);
        params.put("ENABLE", StatusEnum.ENABLE);
        return super.findBy(sb.toString(), params);
    }
}
