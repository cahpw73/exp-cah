package ch.swissbytes.fqmes.control.purchase;

import ch.swissbytes.fqmes.control.Service;
import ch.swissbytes.fqmes.model.dao.*;
import ch.swissbytes.fqmes.model.entities.*;

import javax.inject.Inject;
import javax.transaction.Transactional;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * Created by alvaro on 9/15/14.
 */
public class PurchaseOrderService extends Service implements Serializable {

    @Inject
    private PurchaseOrderDao dao;

    @Inject
    private SupplierDao supplierDao;

    @Inject
    private CommentDao commentDao;

    @Inject
    private ScopeSupplyDao scopeSupplyDao;

    @Inject
    private AttachmentDao attachmentDao;

    public PurchaseOrderService() {
        super.initialize(dao);
    }


    public PurchaseOrderEntity load(Long id) {
        return dao.load(id);
    }



    @Transactional
    public void doSave(PurchaseOrderEntity newPurchaseOrder, List<CommentEntity> comments, List<ScopeSupplyEntity> scopeSupplies, SupplierEntity supplier,
                       List<AttachmentEntity> attachmentEntities) {
        dao.save(newPurchaseOrder);
        supplier.setPurchaseOrder(newPurchaseOrder);
        supplierDao.persist(supplier);
        commentDao.persist(comments, newPurchaseOrder);
        scopeSupplyDao.persist(scopeSupplies, newPurchaseOrder);
        attachmentDao.save(attachmentEntities, newPurchaseOrder);
    }

    @Transactional
    public PurchaseOrderEntity doUpdate(PurchaseOrderEntity por, SupplierEntity supplierEntity, List<CommentEntity> commentEntities, List<ScopeSupplyEntity> scopeSupplyEntities,
                                        List<AttachmentEntity> attachmentEntities) {
        PurchaseOrderEntity entity = dao.update(por);
        supplierDao.update(supplierEntity);
        commentDao.update(commentEntities, entity);
        scopeSupplyDao.update(scopeSupplyEntities, entity);
        attachmentDao.update(attachmentEntities, entity);
        return por;
    }


}
