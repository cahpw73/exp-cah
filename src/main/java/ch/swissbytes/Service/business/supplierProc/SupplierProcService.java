package ch.swissbytes.Service.business.supplierProc;

import ch.swissbytes.Service.business.Service;
import ch.swissbytes.domain.model.entities.SupplierProcEntity;

import javax.inject.Inject;
import java.io.Serializable;

/**
 * Created by alvaro on 9/22/14.
 */
public class SupplierProcService extends Service<SupplierProcEntity> implements Serializable{

    @Inject
     private SupplierProcDao dao;



}
