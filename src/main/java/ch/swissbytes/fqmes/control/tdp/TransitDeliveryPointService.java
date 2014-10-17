package ch.swissbytes.fqmes.control.tdp;

import ch.swissbytes.fqmes.control.Service;
import ch.swissbytes.fqmes.model.dao.TransitDeliveryPointDao;
import ch.swissbytes.fqmes.model.entities.TransitDeliveryPointEntity;

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

    public void save(TransitDeliveryPointEntity entity){
        dao.save(entity);
    }

    public void update(TransitDeliveryPointEntity entity){
        dao.update(entity);
    }

}
