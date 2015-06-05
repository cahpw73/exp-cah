package ch.swissbytes.Service.business.deliverable;

import ch.swissbytes.Service.infrastructure.Filter;
import ch.swissbytes.Service.infrastructure.GenericDao;
import ch.swissbytes.domain.model.entities.DeliverableEntity;
import ch.swissbytes.domain.model.entities.POEntity;
import ch.swissbytes.domain.model.entities.RequisitionEntity;
import ch.swissbytes.domain.types.StatusEnum;

import javax.persistence.Query;
import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Created by alvaro on 04-06-15.
 */
public class DeliverableDao extends GenericDao<DeliverableEntity> implements Serializable {

    private static final Logger log = Logger.getLogger(DeliverableDao.class.getName());

    @Override
    protected void applyCriteriaValues(Query query, Filter filter) {

    }
    public void doSave(POEntity poEntity,List<DeliverableEntity>deliverables){
        Date now=new Date();
        for(DeliverableEntity deliverable:deliverables){
            deliverable.setId(null);
            deliverable.setStatus(StatusEnum.ENABLE);
            deliverable.setLastUpdate(now);
            deliverable.setPoEntity(poEntity);
            save(deliverable);
        }
    }
    public void doUpdate(POEntity poEntity,List<DeliverableEntity>deliverables){
        Date now=new Date();
        for(DeliverableEntity deliverable:deliverables){
            deliverable.setLastUpdate(now);
            deliverable.setPoEntity(poEntity);
            if(deliverable.getId()<0){
                deliverable.setId(null);
                deliverable.setStatus(StatusEnum.ENABLE);
                save(deliverable);
            }else{
                update(deliverable);
            }
        }

    }
    public List<DeliverableEntity> findDeliverableByPurchaseOrder(Long id){

        StringBuilder sb = new StringBuilder();
        sb.append(" SELECT d ");
        sb.append(" FROM DeliverableEntity d ");
        sb.append(" WHERE d.status = :ENABLE ");
        sb.append(" AND d.poEntity.id=:ID");
        Map<String,Object> params = new HashMap<>();
        params.put("ENABLE", StatusEnum.ENABLE);
        params.put("ID",id!=null?id:id);
        return super.findBy(sb.toString(),params);
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