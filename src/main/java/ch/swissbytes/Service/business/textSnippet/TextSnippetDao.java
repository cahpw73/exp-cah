package ch.swissbytes.Service.business.textSnippet;

import ch.swissbytes.Service.infrastructure.Filter;
import ch.swissbytes.Service.infrastructure.GenericDao;
import ch.swissbytes.domain.model.entities.ProjectDocumentEntity;
import ch.swissbytes.domain.model.entities.ProjectTextSnippetEntity;
import ch.swissbytes.domain.model.entities.TextSnippetEntity;
import ch.swissbytes.domain.types.StatusEnum;
import org.apache.commons.lang3.StringUtils;

import javax.persistence.Query;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Created by alvaro on 9/10/14.
 */

public class TextSnippetDao extends GenericDao<TextSnippetEntity> implements Serializable {

    private static final Logger log = Logger.getLogger(TextSnippetDao.class.getName());


    public void doSave(TextSnippetEntity textSnippet){
        super.save(textSnippet);
    }

    public void doUpdate(TextSnippetEntity textSnippet){
       super.update(textSnippet);
    }

    public List<TextSnippetEntity> getTextSnippetList(){
        StringBuilder sb=new StringBuilder();
        sb.append("SELECT x ");
        sb.append("FROM TextSnippetEntity x ");
        sb.append("WHERE x.status=:ENABLED ");
        sb.append(" AND  x.project IS NULL ");
        sb.append("ORDER BY x.textSnippet ");
        Map<String,Object> map=new HashMap<String,Object>();
        map.put("ENABLED", StatusEnum.ENABLE);
        return super.findBy(sb.toString(),map);
    }


    public List<TextSnippetEntity> findByCodeButWithNoId(String code, Long id){
        StringBuilder sb=new StringBuilder();
        sb.append("SELECT x ");
        sb.append("FROM TextSnippetEntity x ");
        sb.append("WHERE x.status=:ENABLED ");
        sb.append("AND trim(lower(x.code))=:CODE ");
        sb.append("AND NOT x.id=:TEXT_SNIPPET_ID ");
        Map<String,Object> map=new HashMap<String,Object>();
        map.put("ENABLED", StatusEnum.ENABLE);
        map.put("CODE", code.toLowerCase().trim());
        map.put("TEXT_SNIPPET_ID", id!=null?id:0L);
        return super.findBy(sb.toString(),map);
    }

    public List<TextSnippetEntity> findByText(final String code){
        StringBuilder sb=new StringBuilder();
        sb.append("SELECT x ");
        sb.append("FROM TextSnippetEntity x ");
        sb.append("WHERE x.status=:ENABLED ");
        if(StringUtils.isNotEmpty(code)&&StringUtils.isNotBlank(code)){
            sb.append("AND lower(x.code) LIKE :CODE ");
        }
        Map<String,Object> map=new HashMap<>();
        map.put("ENABLED", StatusEnum.ENABLE);
        if(StringUtils.isNotEmpty(code)&&StringUtils.isNotBlank(code)){
            map.put("CODE", "%"+code.toLowerCase().trim()+"%");
        }
        return super.findBy(sb.toString(),map);
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

    public List<TextSnippetEntity> findGlobalAndByProject(Long id) {
        StringBuilder sb=new StringBuilder();
        sb.append("SELECT x ");
        sb.append("FROM TextSnippetEntity x ");
        sb.append("WHERE x.status=:ENABLED ");
        sb.append(" AND ( x.project IS NULL ");
        sb.append(" OR x.project.id=:PROJECT_ID ");
        sb.append(" ) ");
        Map<String,Object> map=new HashMap<String,Object>();
        map.put("ENABLED", StatusEnum.ENABLE);
        map.put("PROJECT_ID", id==null?0L:id);
        return super.findBy(sb.toString(),map);
    }

    public List<ProjectTextSnippetEntity> findProjectTextSnippetByTextSnippetId(final Long textSnippetId) {
        StringBuilder sb = new StringBuilder();
        sb.append(" SELECT x ");
        sb.append(" FROM ProjectTextSnippetEntity x ");
        sb.append(" WHERE x.status = :ENABLE ");
        sb.append(" AND x.textSnippet.id = :TEXT_SNIPPET_ID ");
        Map<String, Object> params = new HashMap<>();
        params.put("ENABLE", StatusEnum.ENABLE);
        params.put("TEXT_SNIPPET_ID",textSnippetId);
        return super.findBy(sb.toString(), params);
    }

    public List<TextSnippetEntity> findTextSnippetByTextSnippetIdAndProjectId(final Long textSnippetId,final Long projectId) {
        StringBuilder sb = new StringBuilder();
        sb.append(" SELECT x ");
        sb.append(" FROM TextSnippetEntity x ");
        sb.append(" WHERE x.status=:ENABLED ");
        sb.append(" AND x.id=:ID ");
        sb.append(" AND x.project.id=:PROJECT_ID ");
        Map<String,Object> map=new HashMap<String,Object>();
        map.put("ENABLED", StatusEnum.ENABLE);
        map.put("PROJECT_ID", projectId);
        map.put("ID", textSnippetId);
        return super.findBy(sb.toString(),map);
    }

    public List<TextSnippetEntity> getAllTextSnippetList(){
        StringBuilder sb=new StringBuilder();
        sb.append("SELECT x ");
        sb.append("FROM TextSnippetEntity x ");
        Map<String,Object> map=new HashMap<String,Object>();
        return super.findBy(sb.toString(),map);
    }
}
