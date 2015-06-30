package ch.swissbytes.Service.business.client;

import ch.swissbytes.Service.infrastructure.Filter;
import ch.swissbytes.Service.infrastructure.GenericDao;
import ch.swissbytes.domain.model.entities.ClientEntity;
import ch.swissbytes.domain.types.StatusEnum;
import org.apache.commons.lang.StringUtils;

import javax.persistence.Query;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by alvaro on 6/29/2015.
 */
public class ClientDao extends GenericDao<ClientEntity> implements Serializable {
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

    public List<ClientEntity>findAll(String term){
        StringBuilder sb=new StringBuilder();
        sb.append("SELECT cl ");
        sb.append("FROM ClientEntity cl ");
        sb.append("WHERE cl.status=:ENABLED ");

        if(StringUtils.isNotEmpty(term)&&StringUtils.isNotBlank(term)){
            sb.append(" AND lower(cl.title) like :TERM ");
        }
        sb.append("ORDER BY cl.title ");
        Map<String,Object> map=new HashMap<String,Object>();
        map.put("ENABLED", StatusEnum.ENABLE);
        if(StringUtils.isNotEmpty(term)&&StringUtils.isNotBlank(term)){
            map.put("TERM","%"+term.toLowerCase().trim()+"%");
        }
        return super.findBy(sb.toString(),map);
    }

}
