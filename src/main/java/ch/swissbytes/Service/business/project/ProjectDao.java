package ch.swissbytes.Service.business.project;

import ch.swissbytes.Service.infrastructure.Filter;
import ch.swissbytes.Service.infrastructure.GenericDao;
import ch.swissbytes.domain.model.entities.BrandEntity;
import ch.swissbytes.domain.model.entities.ProjectEntity;
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


    public void doSave(ProjectEntity projectEntity){
        super.save(projectEntity);
    }

    public void doUpdate(ProjectEntity detachedEntity){
        ProjectEntity entity = super.merge(detachedEntity);
        super.saveAndFlush(entity);
    }

    public List<ProjectEntity> getProjectList(){
        StringBuilder sb = new StringBuilder();
        sb.append(" SELECT p ");
        sb.append(" FROM ProjectEntity p ");
        sb.append(" LEFT JOIN p.client c ");
        sb.append(" WHERE p.status = :ENABLE ");
        sb.append(" ORDER BY p.projectNumber");
        Map<String,Object> params = new HashMap<>();
        params.put("ENABLE", StatusEnum.ENABLE);
        return super.findBy(sb.toString(),params);
    }

    public List<ProjectEntity> findByProjectNumber(final String name){
        log.info("findByName: " + name);
        StringBuilder sb = new StringBuilder();
        sb.append(" SELECT p ");
        sb.append(" FROM ProjectEntity p ");
        sb.append(" WHERE LOWER(p.projectNumber) = :PROJECT_NAME ");
        sb.append(" AND p.status = :ENABLE ");
        Map<String,Object> params = new HashMap<>();
        params.put("PROJECT_NAME", name.toLowerCase().trim());
        params.put("ENABLE", StatusEnum.ENABLE);
        return super.findBy(sb.toString(),params);
    }

    public List<ProjectEntity> findByLikeProjectNumber(final String name){
        log.info("findByLikeName: " + name);
        StringBuilder sb = new StringBuilder();
        sb.append(" SELECT p ");
        sb.append(" FROM ProjectEntity p ");
        sb.append(" WHERE LOWER(p.projectNumber) = :PROJECT_NAME ");
        sb.append(" AND b.status = :ENABLE ");
        Map<String,Object> params = new HashMap<>();
        params.put("PROJECT_NAME", "%" + name.toLowerCase().trim() + "%");
        params.put("ENABLE", StatusEnum.ENABLE);
        return super.findBy(sb.toString(),params);
    }

    public List<ProjectEntity> findBySearchTerm(final String searchTerm) {
        log.info("Search Term: " + searchTerm);
        StringBuilder sb = new StringBuilder();
        sb.append(" SELECT p ");
      //  sb.append(" FROM ProjectEntity p LEFT JOIN p.supplierProcurement sp ");
        sb.append(" FROM ProjectEntity p  ");
        sb.append(" LEFT JOIN p.client.reportLogo rl ");
        sb.append(" LEFT JOIN p.client.clientLogo cl ");
        sb.append(" LEFT JOIN p.client.clientFooter cf ");
        sb.append(" WHERE NOT p.status = :DELETED ");

        Map<String,Object> parameters = new HashMap<>();
        parameters.put("DELETED",StatusEnum.DELETED);

        if(StringUtils.isNotEmpty(searchTerm) && StringUtils.isNotBlank(searchTerm)) {
            sb.append(" AND (  ");
            sb.append(" LOWER(p.projectNumber) like :PROJECT_NUMBER ");
            sb.append(" OR LOWER(p.title) like :TITLE ");
            //sb.append(" OR LOWER(sp.company) like :SUPPLIER ");
            sb.append(" OR LOWER(rl.description) like :DESCRIPTION ");
            sb.append(" OR LOWER(cl.description) like :DESCRIPTION ");
            sb.append(" OR LOWER(cf.description) like :DESCRIPTION ");
            sb.append(" )");

            parameters.put("PROJECT_NUMBER", "%" + searchTerm.toLowerCase().trim() + "%");
            parameters.put("TITLE", "%" + searchTerm.toLowerCase().trim() + "%");
            parameters.put("DESCRIPTION", "%" + searchTerm.toLowerCase().trim() + "%");
        }
        return super.findBy(sb.toString(),parameters);
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
        Map<String,Object> params = new HashMap<>();
        params.put("PROJECT_NUMBER", projectNumber.toLowerCase().trim());
        params.put("ID",id);
        params.put("ENABLE", StatusEnum.ENABLE);
        return super.findBy(sb.toString(),params);
    }

    public List<ProjectEntity> findByLogoId(Long logoId) {
        StringBuilder sb = new StringBuilder();
        sb.append(" SELECT p ");
        sb.append(" FROM ProjectEntity p ");
        sb.append(" WHERE p.status = :ENABLE ");
        sb.append(" AND (p.client.reportLogo.id = :LOGO_ID  ");
        sb.append(" OR p.client.clientLogo.id = :LOGO_ID  ");
        sb.append(" OR p.client.clientFooter.id = :LOGO_ID  ");
        sb.append(" OR p.client.defaultLogo.id = :LOGO_ID  ");
        sb.append(" OR p.client.defaultFooter.id = :LOGO_ID) ");
        Map<String,Object> params = new HashMap<>();
        params.put("LOGO_ID",logoId);
        params.put("ENABLE", StatusEnum.ENABLE);
        return super.findBy(sb.toString(),params);
    }
    public List<ProjectEntity> findByClient(Long clientId) {
        StringBuilder sb = new StringBuilder();
        sb.append(" SELECT p ");
        sb.append(" FROM ProjectEntity p ");
        sb.append(" WHERE p.status = :ENABLE ");
        sb.append(" AND p.client.id = :ID  ");
        Map<String,Object> params = new HashMap<>();
        params.put("ID",clientId);
        params.put("ENABLE", StatusEnum.ENABLE);
        return super.findBy(sb.toString(),params);
    }
}
