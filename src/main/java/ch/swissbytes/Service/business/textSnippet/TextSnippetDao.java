package ch.swissbytes.Service.business.textSnippet;

import ch.swissbytes.Service.infrastructure.Filter;
import ch.swissbytes.Service.infrastructure.GenericDao;
import ch.swissbytes.domain.model.entities.TextSnippetEntity;
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
        sb.append("FROM CurrencyEntity x ");
        sb.append("WHERE x.status=:ENABLED ");
        sb.append("ORDER BY x.name ");
        Map<String,Object> map=new HashMap<String,Object>();
        map.put("ENABLED", StatusEnum.ENABLE);
        return super.findBy(sb.toString(),map);
    }


    public List<TextSnippetEntity> findByCodeButWithNoId(String code, Long id){
        StringBuilder sb=new StringBuilder();
        sb.append("SELECT x ");
        sb.append("FROM CurrencyEntity x ");
        sb.append("WHERE x.status=:ENABLED ");
        sb.append("AND trim(lower(x.code))=:CODE ");
        sb.append("AND NOT x.id=:TEXT_SNIPPET_ID ");
        Map<String,Object> map=new HashMap<String,Object>();
        map.put("ENABLED", StatusEnum.ENABLE);
        map.put("CODE", code.toLowerCase().trim());
        map.put("TEXT_SNIPPET_ID", id!=null?id:0L);
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
}
