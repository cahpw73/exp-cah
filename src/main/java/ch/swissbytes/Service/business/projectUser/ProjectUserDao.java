package ch.swissbytes.Service.business.projectUser;

import ch.swissbytes.Service.infrastructure.Filter;
import ch.swissbytes.Service.infrastructure.GenericDao;
import ch.swissbytes.domain.model.entities.ProjectEntity;
import ch.swissbytes.domain.model.entities.ProjectUserEntity;
import ch.swissbytes.domain.types.StatusEnum;
import org.apache.commons.lang.StringUtils;

import javax.persistence.Query;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Created by christian 02/03/2016
 */

public class ProjectUserDao extends GenericDao<ProjectUserEntity> implements Serializable {

    public void doSave(ProjectUserEntity projectEntity){
        super.save(projectEntity);
    }

    public void doUpdate(ProjectUserEntity detachedEntity){
        ProjectUserEntity entity = super.merge(detachedEntity);
        super.saveAndFlush(entity);
    }

    public List<ProjectUserEntity> findByProjectId(final Long id){
        StringBuilder sb = new StringBuilder();
        sb.append(" SELECT x ");
        sb.append(" FROM ProjectUserEntity x ");
        sb.append(" WHERE x.status = :ENABLE ");
        sb.append(" AND x.project.id = :PROJECT_ID ");
        Map<String,Object> params = new HashMap<>();
        params.put("PROJECT_ID", id);
        params.put("ENABLE", StatusEnum.ENABLE);
        return super.findBy(sb.toString(),params);
    }

    public List<ProjectUserEntity> findByUserId(final Long id){
        StringBuilder sb = new StringBuilder();
        sb.append(" SELECT x ");
        sb.append(" FROM ProjectUserEntity x ");
        sb.append(" WHERE x.status = :ENABLE ");
        sb.append(" AND x.user.id = :USER_ID ");
        Map<String,Object> params = new HashMap<>();
        params.put("USER_ID", id);
        params.put("ENABLE", StatusEnum.ENABLE);
        return super.findBy(sb.toString(),params);
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
