package ch.swissbytes.Service.business.deliverable;

import ch.swissbytes.Service.infrastructure.Filter;
import ch.swissbytes.Service.infrastructure.GenericDao;
import ch.swissbytes.domain.model.entities.DeliverableItem;
import ch.swissbytes.domain.types.StatusEnum;

import javax.persistence.Query;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by alvaro on 9/11/2015.
 */
public class DeliverableItemDao extends GenericDao<DeliverableItem> implements Serializable {


    public List<DeliverableItem> findAllDeliverableItems(){
        StringBuilder sb = new StringBuilder();
        sb.append(" SELECT d ");
        sb.append(" FROM DeliverableItem d ");
        sb.append(" WHERE d.statusEnum = :ENABLE ");
        sb.append(" ORDER BY d.item");
        Map<String,Object> params = new HashMap<>();
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
