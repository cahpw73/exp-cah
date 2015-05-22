package ch.swissbytes.Service.business.tdp;

import ch.swissbytes.Service.business.Service;
import ch.swissbytes.domain.model.entities.TransitDeliveryPointEntity;

import javax.inject.Inject;
import java.io.Serializable;
import java.util.List;

/**
 * Created by alvaro on 10/9/14.
 */
public class TransitDeliveryPointService extends Service<TransitDeliveryPointEntity> implements Serializable {

    @Inject
    private TransitDeliveryPointDao dao;

    public List<TransitDeliveryPointEntity> findByScopeSupply(final Long id){
        return dao.findByScopeSupply(id);
    }

    public TransitDeliveryPointEntity save(TransitDeliveryPointEntity entity){
        dao.save(entity);
        return entity;
    }

    public TransitDeliveryPointEntity update(TransitDeliveryPointEntity entity){
        dao.update(entity);
        return entity;
    }

}
