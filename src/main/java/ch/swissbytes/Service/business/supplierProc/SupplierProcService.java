package ch.swissbytes.Service.business.supplierProc;

import ch.swissbytes.Service.business.Service;
import ch.swissbytes.domain.model.entities.SupplierProcEntity;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.io.Serializable;
import java.util.List;

/**
 * Created by alvaro on 9/22/14.
 */
public class SupplierProcService extends Service<SupplierProcEntity> implements Serializable{

    @Inject
     private SupplierProcDao dao;

    @PostConstruct
    public void create() {
        super.initialize(dao);
    }

    public SupplierProcEntity findById(Long id){
        List<SupplierProcEntity>list=dao.findById(SupplierProcEntity.class,id);
        return list.isEmpty()?list.get(0):null;
    }

}
