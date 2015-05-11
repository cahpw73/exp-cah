package ch.swissbytes.Service.business.tdp;

import ch.swissbytes.Service.infrastructure.GenericDao;
import ch.swissbytes.Service.infrastructure.Filter;
import ch.swissbytes.domain.model.entities.TransitDeliveryPointEntity;
import ch.swissbytes.domain.types.StatusEnum;
import ch.swissbytes.fqmes.util.Util;

import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.util.List;

/**
 * Created by Alvaro on 11/10/14.
 */
public class TransitDeliveryPointDao extends GenericDao<TransitDeliveryPointEntity> {

    public List<TransitDeliveryPointEntity> findByScopeSupply(final Long scopeSupplyId){
        String hql = "SELECT tdp FROM TransitDeliveryPointEntity tdp where tdp.scopeSupply.id=:scope_supply_id AND  tdp.status.id<>:DELETED ORDER BY tdp.id" ;
        TypedQuery<TransitDeliveryPointEntity> query = this.entityManager.createQuery(
                hql, TransitDeliveryPointEntity.class);
        query.setParameter("scope_supply_id", scopeSupplyId);
        query.setParameter("DELETED", StatusEnum.DELETED.getId());
        return query.getResultList();

    }


  @Override
  public <TransitDeliveryPointEntity>  void save(final TransitDeliveryPointEntity entity) {
      ch.swissbytes.domain.model.entities.TransitDeliveryPointEntity transitDeliveryPointEntity = (ch.swissbytes.domain.model.entities.TransitDeliveryPointEntity) entity;
      transitDeliveryPointEntity.setLocation(Util.cutIfAny(transitDeliveryPointEntity.getLocation(),50));
      transitDeliveryPointEntity.setComment(Util.cutIfAny(transitDeliveryPointEntity.getLocation(),1000));
      if(transitDeliveryPointEntity.getLeadTime() == null){
          transitDeliveryPointEntity.setLeadTime(0);
      }
      super.save(entity);
  }
    @Override
    public TransitDeliveryPointEntity update(final TransitDeliveryPointEntity entity) {
        entity.setLocation(Util.cutIfAny(entity.getLocation(),50));
        entity.setComment(Util.cutIfAny(entity.getComment(), 1000));
        //if(entity.getActualDeliveryDate().ge)
        if(entity.getLeadTime() == null){
            entity.setLeadTime(0);
        }
        return super.update(entity);
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
