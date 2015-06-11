package ch.swissbytes.Service.business.projectTextSnippet;

import ch.swissbytes.Service.infrastructure.Filter;
import ch.swissbytes.Service.infrastructure.GenericDao;
import ch.swissbytes.domain.model.entities.ProjectCurrencyEntity;
import ch.swissbytes.domain.model.entities.ProjectTextSnippetEntity;
import ch.swissbytes.domain.types.StatusEnum;

import javax.persistence.Query;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Created by alvaro on 9/10/14.
 */

public class ProjectTextSnippetDao extends GenericDao<ProjectTextSnippetEntity> implements Serializable {

    private static final Logger log = Logger.getLogger(ProjectTextSnippetDao.class.getName());


    public void doSave(ProjectTextSnippetEntity entity){
        super.save(entity);
    }

    public void doUpdate(ProjectTextSnippetEntity detachedEntity){
        ProjectTextSnippetEntity entity = super.merge(detachedEntity);
        super.saveAndFlush(entity);
    }

    public List<ProjectTextSnippetEntity> findByProjectId(final Long projectId){
        StringBuilder sb = new StringBuilder();
        sb.append(" SELECT p ");
        sb.append(" FROM ProjectTextSnippetEntity p ");
        sb.append(" WHERE p.status = :ENABLE ");
        sb.append(" AND p.project.id = :PROJECT_ID ");
        Map<String,Object> params = new HashMap<>();
        params.put("ENABLE", StatusEnum.ENABLE);
        params.put("PROJECT_ID",projectId);
        return super.findBy(sb.toString(),params);
    }

    public ProjectTextSnippetEntity findByTextSnippetId(Long id) {
        StringBuilder sb = new StringBuilder();
        sb.append(" SELECT p ");
        sb.append(" FROM ProjectTextSnippetEntity p ");
        sb.append(" WHERE p.status = :ENABLE ");
        sb.append(" AND p.textSnippet.id = :TEXT_SNIPPET_ID ");
        Query query = super.entityManager.createQuery(sb.toString());
        query.setParameter("ENABLE",StatusEnum.ENABLE);
        query.setParameter("TEXT_SNIPPET_ID",id);
        /*Map<String,Object> params = new HashMap<>();
        params.put("ENABLE", StatusEnum.ENABLE);
        params.put("TEXT_SNIPPET_ID",id);*/
        return (ProjectTextSnippetEntity) query.getSingleResult();
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
