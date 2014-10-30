package ch.swissbytes.fqmes.model.dao;

import ch.swissbytes.fqmes.model.Filter;
import ch.swissbytes.fqmes.model.entities.ScopeSupplyEntity;
import ch.swissbytes.fqmes.model.entities.TransitDeliveryPointEntity;
import ch.swissbytes.fqmes.types.StatusEnum;

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

  /*  @Override
    public void save(TransitDeliveryPointEntity entity){
        if(entity.getLeadTime() == null){
            entity.setLeadTime(0);
        }
        super.save(entity);
    }*/
  @Override
  public <TransitDeliveryPointEntity>  void save(final TransitDeliveryPointEntity entity) {
      ch.swissbytes.fqmes.model.entities.TransitDeliveryPointEntity transitDeliveryPointEntity = (ch.swissbytes.fqmes.model.entities.TransitDeliveryPointEntity) entity;
      if(transitDeliveryPointEntity.getLeadTime() == null){
          transitDeliveryPointEntity.setLeadTime(0);
      }
      super.save(entity);
  }
    @Override
    public TransitDeliveryPointEntity update(final TransitDeliveryPointEntity entity) {
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
