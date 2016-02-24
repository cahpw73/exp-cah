package ch.swissbytes.Service.business.purchase;

import ch.swissbytes.Service.business.Service;
import ch.swissbytes.Service.business.cashflow.CashflowService;
import ch.swissbytes.Service.business.comment.CommentDao;
import ch.swissbytes.Service.business.contact.ContactService;
import ch.swissbytes.Service.business.deliverable.DeliverableDao;
import ch.swissbytes.Service.business.enumService.EnumService;
import ch.swissbytes.Service.business.item.ItemService;
import ch.swissbytes.Service.business.poDocument.PODocumentService;
import ch.swissbytes.Service.business.project.ProjectService;
import ch.swissbytes.Service.business.projectTextSnippet.ProjectTextSnippetService;
import ch.swissbytes.Service.business.requisition.RequisitionDao;
import ch.swissbytes.Service.business.scopesupply.ScopeSupplyDao;
import ch.swissbytes.Service.business.tdp.TransitDeliveryPointService;
import ch.swissbytes.Service.business.text.TextService;
import ch.swissbytes.domain.model.entities.*;
import ch.swissbytes.domain.types.IncoTermsEnum;
import ch.swissbytes.domain.types.ExpeditingStatusEnum;
import ch.swissbytes.domain.types.StatusEnum;
import ch.swissbytes.fqmes.util.Configuration;
import ch.swissbytes.fqmes.util.Purchase;
import ch.swissbytes.fqmes.util.Util;
import ch.swissbytes.procurement.boundary.purchaseOrder.FilterPO;
import ch.swissbytes.procurement.util.DateUtil;
import org.apache.commons.lang.StringUtils;

import javax.inject.Inject;
import javax.transaction.Transactional;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.*;
import java.math.RoundingMode;
import java.util.logging.Logger;

/**
 * Created by alvaro on 9/15/14.
 */
public class PurchaseOrderService extends Service implements Serializable {


    public static final Logger log = Logger.getLogger(PurchaseOrderService.class.getName());

    @Inject
    private PurchaseOrderDao dao;

    @Inject
    private CommentDao commentDao;

    @Inject
    private ScopeSupplyDao scopeSupplyDao;

    @Inject
    private EnumService enumService;

    @Inject
    private TransitDeliveryPointService transitDeliveryPointService;

    @Inject
    private ItemService itemService;

    @Inject
    private RequisitionDao requisitionDao;

    @Inject
    private DeliverableDao deliverableDao;

    @Inject
    private CashflowService cashflowService;

    @Inject
    private TextService textService;

    @Inject
    private ProjectService projectService;

    @Inject
    private ProjectTextSnippetService projectTextSnippetService;

    @Inject
    private ContactService contactService;

    @Inject
    private Configuration configuration;

    @Inject
    private PODocumentService poDocumentService;

    private final String PREFIX = "v";

    public PurchaseOrderService() {
        super.initialize(dao);
    }

    public PurchaseOrderEntity load(Long id) {
        PurchaseOrderEntity purchaseOrderEntity = dao.load(id);
        addPrefixToVariation(purchaseOrderEntity);
        if (purchaseOrderEntity.getPurchaseOrderProcurementEntity().getSupplier() != null) {
            purchaseOrderEntity.getPurchaseOrderProcurementEntity().getSupplier().getContacts().addAll(contactService.findByContactsBySupplier(purchaseOrderEntity.getPurchaseOrderProcurementEntity().getSupplier().getId()));
        }
        return purchaseOrderEntity;
    }


    @Transactional
    public void doSave(PurchaseOrderEntity newPurchaseOrder, List<CommentEntity> comments, List<ScopeSupplyEntity> scopeSupplies, SupplierEntity supplier) {
        if (newPurchaseOrder.getPurchaseOrderProcurementEntity().getId() == null) {
            newPurchaseOrder.setProject(newPurchaseOrder.getProjectEntity().getProjectNumber());
            dao.save(newPurchaseOrder.getPurchaseOrderProcurementEntity());
        }
        dao.save(newPurchaseOrder);
        removePrefixIfAny(newPurchaseOrder);
        supplier.setPurchaseOrder(newPurchaseOrder);
        // supplierDao.persist(supplier);
        commentDao.persist(comments, newPurchaseOrder);
        scopeSupplyDao.persist(scopeSupplies, newPurchaseOrder);
    }

    @Transactional
    public PurchaseOrderEntity doUpdate(PurchaseOrderEntity por, List<CommentEntity> commentEntities, List<ScopeSupplyEntity> scopeSupplyEntities) {
        PurchaseOrderEntity entity = dao.update(por);
        removePrefixIfAny(entity);
        dao.updatePOEntity(por.getPurchaseOrderProcurementEntity());
        commentDao.update(commentEntities, entity);
        scopeSupplyDao.update(scopeSupplyEntities, entity);
        return por;
    }

    /**
     * Delete the purchase order and its dependencies
     *
     * @param purchaseOrderId
     */
    @Transactional
    public void doDelete(final Long purchaseOrderId) {

        List<PurchaseOrderEntity> list = dao.findById(PurchaseOrderEntity.class, purchaseOrderId);
        if (!list.isEmpty()) {
            PurchaseOrderEntity entity = list.get(0);
            entity.setStatus(getStatusDelete());
            dao.update(entity);
            deleteComment(purchaseOrderId);
            deleteScopeSupply(purchaseOrderId);
            deleteItem(purchaseOrderId);
        }
    }


    private void deleteScopeSupply(final Long purchaseOrderId) {
        List<ScopeSupplyEntity> scopeSupplyList = scopeSupplyDao.findByPurchaseOrder(purchaseOrderId);
        if (!scopeSupplyList.isEmpty()) {
            ScopeSupplyEntity scopeSupply = scopeSupplyList.get(0);
            scopeSupply.setStatus(getStatusDelete());
            scopeSupplyDao.update(scopeSupply);
        }
    }

    private void deleteItem(final Long purchaseOrderId) {
        List<ItemEntity> scopeSupplyList = itemService.findByPoId(purchaseOrderId);
        if (!scopeSupplyList.isEmpty()) {
            ItemEntity scopeSupply = scopeSupplyList.get(0);
            scopeSupply.setStatus(getStatusDelete());
            itemService.delete(scopeSupply);
        }
    }

    private void deleteComment(final Long purchaseOrderId) {
        List<CommentEntity> commentList = commentDao.findByPurchaseOrder(purchaseOrderId);
        if (!commentList.isEmpty()) {
            CommentEntity comment = commentList.get(0);
            comment.setStatus(getStatusDelete());
            commentDao.update(comment);
        }
    }

    private StatusEntity getStatusDelete() {
        return dao.findById(StatusEntity.class, StatusEnum.DELETED.getId()).get(0);
    }

    public Integer getAbsoluteHashcode(final Long purchaseOrderId) {
        Integer hashCode = -1;
        PurchaseOrderEntity entity = dao.load(purchaseOrderId);
        if (entity != null) {
            hashCode = entity.hashCode();
           /* SupplierEntity supplierEntity = supplierDao.findByPurchaseOrder(purchaseOrderId);
            if (supplierEntity != null) {
                hashCode += supplierEntity.hashCode();
            }*/
            for (CommentEntity commentEntity : commentDao.findByPurchaseOrder(purchaseOrderId)) {
                hashCode += commentEntity.hashCode();
            }
            for (ScopeSupplyEntity scopeSupplyEntity : scopeSupplyDao.findByPurchaseOrder(purchaseOrderId)) {
                hashCode += scopeSupplyEntity.hashCode();
                for (TransitDeliveryPointEntity tdp : transitDeliveryPointService.findByScopeSupply(scopeSupplyEntity.getId())) {
                    hashCode += tdp.hashCode();
                }

            }
        }
        return hashCode;
    }

    public Integer getAbsoluteHashcode(final PurchaseOrderEntity purchaseOrderEntity, List<CommentEntity> commentEntityList, List<ScopeSupplyEntity> scopeSupplyEntityList, SupplierEntity supplierEntity) {
        Integer hashCode = -1;
        //PurchaseOrderEntity entity=dao.load(purchaseOrderEntity.getId());
        //if(entity!=null){
        hashCode = purchaseOrderEntity.hashCode();
        //SupplierEntity supplierEntity=supplierDao.findByPurchaseOrder(purchaseOrderId);
        if (supplierEntity != null) {
            hashCode += supplierEntity.hashCode();
        }
        for (CommentEntity commentEntity : commentEntityList) {
            hashCode += commentEntity.hashCode();
        }
        for (ScopeSupplyEntity scopeSupplyEntity : scopeSupplyEntityList) {
            hashCode += scopeSupplyEntity.hashCode();
            for (TransitDeliveryPointEntity tdp : scopeSupplyEntity.getTdpList()) {
                hashCode += tdp.hashCode();
            }
        }
        return hashCode;
    }


    public List<PurchaseOrderEntity> purchaseListByProject(final Long projectId) {
        return dao.findPOByProject(projectId);
    }

    public List<PurchaseOrderEntity> findPOMaxVariations(final Long projectId) {
        return dao.findPOMaxVariations(projectId);
    }

    public PurchaseOrderEntity findPOWithMaxVariation(String project, String po) {
        Date date1 = new Date();
        List<PurchaseOrderEntity> list = dao.findPOWithMaxVariation(project, po);
        Date date2 = new Date();
        log.info("Time for max variation [" + (date2.getTime() - date1.getTime()) + "]ms");
        if (!list.isEmpty()) {
            return list.get(0);
        }
        return null;
    }

    public boolean findPOByOneVariation(String project, String po, String variation) {
        List<PurchaseOrderEntity> list = dao.findPOByOneVariation(project, po, variation);
        if (!list.isEmpty()) {
            return true;
        }
        return false;
    }

    public List<PurchaseOrderEntity> purchaseListByProjectIdAnPoNo(final Long projectId, final String poNo) {
        return dao.findPOByProjectIdAndPoNo(projectId, poNo);
    }


    @Transactional
    public PurchaseOrderEntity savePOOnProcurement(PurchaseOrderEntity purchaseOrderEntity) {
        removePrefixIfAny(purchaseOrderEntity);
        purchaseOrderEntity.getPurchaseOrderProcurementEntity().setContactExpediting(purchaseOrderEntity.getPurchaseOrderProcurementEntity().getContactEntity());
        PurchaseOrderProcurementEntity po = dao.savePOEntity(purchaseOrderEntity.getPurchaseOrderProcurementEntity());
        purchaseOrderEntity.setPurchaseOrderProcurementEntity(po);
        // purchaseOrderEntity.setPo(purchaseOrderEntity.getProjectEntity().getProjectNumber());
        purchaseOrderEntity.setLastUpdate(new Date());
        purchaseOrderEntity.setStatus(enumService.getStatusEnumEnable());
        purchaseOrderEntity.setPoExpeditingDeliveryDate(purchaseOrderEntity.getPoDeliveryDate());
        purchaseOrderEntity.setPurchaseOrderStatus(ExpeditingStatusEnum.ISSUED);
        purchaseOrderEntity.setPoTitle(purchaseOrderEntity.getPoTitle().trim().toUpperCase());
        purchaseOrderEntity.getPurchaseOrderProcurementEntity().setPoint(purchaseOrderEntity.getPurchaseOrderProcurementEntity().getPoint().toUpperCase());
        String incoTerms = getStrToIncoTerm(po.getPoint());
        String fullIncoTerms = po.getPoint();
        if (exitsDeliveryPointInIncoTerms(incoTerms)) {
            purchaseOrderEntity.setIncoTerm(incoTerms);
            purchaseOrderEntity.setFullIncoTerms(fullIncoTerms);
        } else {
            purchaseOrderEntity.setIncoTerm(null);
            purchaseOrderEntity.setFullIncoTerms(null);
        }
        verifyAndSetRequiredOnSideDate(purchaseOrderEntity, po.getRequisitions());
        dao.save(purchaseOrderEntity);
        doUpdateProjectTextEntity(purchaseOrderEntity);
        //requisition daos
        requisitionDao.doSave(purchaseOrderEntity.getPurchaseOrderProcurementEntity(), po.getRequisitions());
        //items
        itemService.doSave(po.getScopeSupplyList(), purchaseOrderEntity, incoTerms, fullIncoTerms);
        //deliverable
        deliverableDao.doSave(purchaseOrderEntity.getPurchaseOrderProcurementEntity(), po.getDeliverables());
        //CashFlow
        cashflowService.doSave(purchaseOrderEntity.getPurchaseOrderProcurementEntity().getCashflow(), po);
        //Text
        textService.doSave(purchaseOrderEntity.getPurchaseOrderProcurementEntity().getTextEntity(), po);
        //PO Document
        poDocumentService.doSave(purchaseOrderEntity.getPurchaseOrderProcurementEntity().getPoDocumentList(),po);

        return purchaseOrderEntity;
    }

    private void doUpdateProjectTextEntity(PurchaseOrderEntity po){
        List<ProjectTextSnippetEntity> projectTextSnippetEntities =  new ArrayList<>();
        projectTextSnippetEntities.addAll(po.getPurchaseOrderProcurementEntity().getProjectTextSnippetList());
        for (ProjectTextSnippetEntity pr : projectTextSnippetEntities){
            pr.setPurchaseOrder(po);
            projectTextSnippetService.doUpdate(pr);
        }
    }

    public boolean exitsDeliveryPointInIncoTerms(String point) {
        for (IncoTermsEnum i : IncoTermsEnum.values()) {
            if (i.name().equalsIgnoreCase(point)) {
                return true;
            }
        }
        return false;
    }

    public String getStrToIncoTerm(String point) {
        return point != null && point.length() > 2 ? point.substring(0, 3) : point;
    }

    public void addPrefixToVariation(PurchaseOrderEntity purchaseOrderEntity) {
        if (purchaseOrderEntity != null && StringUtils.isNotEmpty(purchaseOrderEntity.getVariation()) && StringUtils.isNotBlank(purchaseOrderEntity.getVariation())) {
            purchaseOrderEntity.setVariation(PREFIX + purchaseOrderEntity.getVariation());
        }
    }

    public void removePrefixIfAny(PurchaseOrderEntity purchaseOrder) {
        if (purchaseOrder != null && StringUtils.isNotEmpty(purchaseOrder.getVariation()) && StringUtils.isNotBlank(purchaseOrder.getVariation())) {
            while (purchaseOrder.getVariation().toLowerCase().trim().startsWith(PREFIX) && StringUtils.isNotBlank(purchaseOrder.getVariation())) {
                purchaseOrder.setVariation(purchaseOrder.getVariation().substring(1, purchaseOrder.getVariation().length()));
            }
        }
    }

    @Transactional
    public PurchaseOrderEntity savePOOnProcurementNewVariation(PurchaseOrderEntity purchaseOrderEntity) {
        removePrefixIfAny(purchaseOrderEntity);
        purchaseOrderEntity.getPurchaseOrderProcurementEntity().setOrderDate(Util.convertUTC(new Date(), configuration.getTimeZone()));
        PurchaseOrderProcurementEntity po = dao.savePOEntity(purchaseOrderEntity.getPurchaseOrderProcurementEntity());
        purchaseOrderEntity.setPurchaseOrderProcurementEntity(po);
        purchaseOrderEntity.setLastUpdate(new Date());
        purchaseOrderEntity.setStatus(enumService.getStatusEnumEnable());
        purchaseOrderEntity.setPurchaseOrderStatus(ExpeditingStatusEnum.ISSUED);
        purchaseOrderEntity.setPoDeliveryDate(null);
        purchaseOrderEntity.setIntroEmail(null);
        purchaseOrderEntity.setIntroEmailSentComment(null);
        purchaseOrderEntity.setRequiredDate(null);
        purchaseOrderEntity.setRequiredSiteDateComment(null);
        purchaseOrderEntity.setActualDate(null);
        purchaseOrderEntity.setActualSiteDateComment(null);
        purchaseOrderEntity.getPurchaseOrderProcurementEntity().setCmsExported(false);
        purchaseOrderEntity.getPurchaseOrderProcurementEntity().setJdeExported(false);
        dao.save(purchaseOrderEntity);
        purchaseOrderEntity.getPurchaseOrderProcurementEntity().getCashflow().getCashflowDetailList().clear();
        cashflowService.doSave(purchaseOrderEntity.getPurchaseOrderProcurementEntity().getCashflow(), po);

        return purchaseOrderEntity;
    }

    @Transactional
    public PurchaseOrderEntity updatePOOnProcurement(PurchaseOrderEntity purchaseOrderEntity) {
        removePrefixIfAny(purchaseOrderEntity);
        purchaseOrderEntity.getPurchaseOrderProcurementEntity().setContactExpediting(purchaseOrderEntity.getPurchaseOrderProcurementEntity().getContactEntity());
        PurchaseOrderProcurementEntity po = dao.updatePOEntity(purchaseOrderEntity.getPurchaseOrderProcurementEntity());
        collectLists(po, purchaseOrderEntity);
        purchaseOrderEntity.setPurchaseOrderProcurementEntity(po);
        purchaseOrderEntity.setLastUpdate(new Date());
        purchaseOrderEntity.setPoExpeditingDeliveryDate(purchaseOrderEntity.getPoDeliveryDate());
        purchaseOrderEntity.setVariation(purchaseOrderEntity.getVariation());
        purchaseOrderEntity.setPoTitle(purchaseOrderEntity.getPoTitle().trim().toUpperCase());
        purchaseOrderEntity.getPurchaseOrderProcurementEntity().setPoint(purchaseOrderEntity.getPurchaseOrderProcurementEntity().getPoint().toUpperCase());
        String incoTerms = getStrToIncoTerm(po.getPoint());
        if (exitsDeliveryPointInIncoTerms(incoTerms)) {
            purchaseOrderEntity.setIncoTerm(incoTerms);
            purchaseOrderEntity.setFullIncoTerms(po.getPoint());
        } else {
            purchaseOrderEntity.setIncoTerm(null);
            purchaseOrderEntity.setFullIncoTerms(null);
        }
        verifyAndSetRequiredOnSideDate(purchaseOrderEntity, po.getRequisitions());
        dao.update(purchaseOrderEntity);
        doUpdateProjectTextEntity(purchaseOrderEntity);
        //requisition daos
        requisitionDao.doUpdate(purchaseOrderEntity.getPurchaseOrderProcurementEntity(), po.getRequisitions());
        //items
        itemService.doUpdate(po.getScopeSupplyList(), purchaseOrderEntity);
        //deliverable
        deliverableDao.doUpdate(purchaseOrderEntity.getPurchaseOrderProcurementEntity(), po.getDeliverables());
        //cashFlow
        cashflowService.doUpdate(purchaseOrderEntity.getPurchaseOrderProcurementEntity().getCashflow(), po);
        //Text
        textService.doUpdate(purchaseOrderEntity.getPurchaseOrderProcurementEntity().getTextEntity(), po);
        //PO Document
        List<PODocumentEntity> poDocList = purchaseOrderEntity.getPurchaseOrderProcurementEntity().getPoDocumentList();
        List<ProjectDocumentEntity> projectDocList = purchaseOrderEntity.getPurchaseOrderProcurementEntity().getProjectDocList();
        poDocumentService.doUpdate(poDocList,projectDocList,po);
        return purchaseOrderEntity;
    }

    public void verifyAndSetRequiredOnSideDate(PurchaseOrderEntity purchaseOrderEntity, List<RequisitionEntity> requisitions) {
        if (hasRequisitionListDeleted(requisitions)) {
            purchaseOrderEntity.setRequiredDate(null);
        } else if (requisitions.size() == 1 && requisitions.get(0).getStatusEnum().ordinal() == StatusEnum.ENABLE.ordinal()) {
            purchaseOrderEntity.setRequiredDate(requisitions.get(0).getRequiredOnSiteDate());
        } else if (requisitions.size() == 1 && requisitions.get(0).getStatusEnum().ordinal() != StatusEnum.ENABLE.ordinal()){
            purchaseOrderEntity.setRequiredDate(null);
        } else if (requisitions.size() > 1) {
            RequisitionEntity hasLessDateReq = requisitions.get(0);
            for (RequisitionEntity r : requisitions) {
                if(hasLessDateReq.getRequiredOnSiteDate() != null && hasLessDateReq.getStatusEnum().ordinal() == StatusEnum.ENABLE.ordinal()){
                    if(r.getStatusEnum().ordinal() == StatusEnum.ENABLE.ordinal()) {
                        if(r.getRequiredOnSiteDate()!=null) {
                            if (r.getRequiredOnSiteDate().before(hasLessDateReq.getRequiredOnSiteDate())) {
                                hasLessDateReq = r;
                            }
                        }
                    }
                }else{
                    hasLessDateReq = r;
                }
            }
            if(hasLessDateReq.getStatusEnum().ordinal() == StatusEnum.ENABLE.ordinal()){
                purchaseOrderEntity.setRequiredDate(hasLessDateReq.getRequiredOnSiteDate());
            }else{
                purchaseOrderEntity.setRequiredDate(null);
            }
        }

    }

    private boolean hasRequisitionListDeleted(List<RequisitionEntity> requisitions) {
        if (requisitions.isEmpty()) {
            return true;
        } else {
            boolean wasDeleted = true;
            for (RequisitionEntity r : requisitions) {
                if (r.getStatusEnum().ordinal() == StatusEnum.ENABLE.ordinal()) {
                    wasDeleted = false;
                    break;
                }
            }
            return wasDeleted;
        }
    }

    @Transactional
    public void markCMSAsExported(PurchaseOrderEntity purchaseOrderEntity) {
        purchaseOrderEntity.getPurchaseOrderProcurementEntity().setCmsExported(true);
        dao.updatePOEntity(purchaseOrderEntity.getPurchaseOrderProcurementEntity());
    }

    @Transactional
    public void markJDEAsExported(PurchaseOrderEntity purchaseOrderEntity) {
        purchaseOrderEntity.getPurchaseOrderProcurementEntity().setJdeExported(true);
        dao.updatePOEntity(purchaseOrderEntity.getPurchaseOrderProcurementEntity());
    }


    @Transactional
    public PurchaseOrderEntity updateOnlyPOOnProcurement(PurchaseOrderEntity purchaseOrderEntity) {
        removePrefixIfAny(purchaseOrderEntity);
        purchaseOrderEntity.getPurchaseOrderProcurementEntity().setContactExpediting(purchaseOrderEntity.getPurchaseOrderProcurementEntity().getContactEntity());
        dao.updatePOEntity(purchaseOrderEntity.getPurchaseOrderProcurementEntity());
        return purchaseOrderEntity;
    }

    private void collectLists(PurchaseOrderProcurementEntity po, PurchaseOrderEntity poe) {
        po.getScopeSupplyList().clear();
        po.getScopeSupplyList().addAll(poe.getPurchaseOrderProcurementEntity().getScopeSupplyList());
        po.getDeliverables().clear();
        po.getDeliverables().addAll(poe.getPurchaseOrderProcurementEntity().getDeliverables());
        po.getRequisitions().clear();
        po.getRequisitions().addAll(poe.getPurchaseOrderProcurementEntity().getRequisitions());
        po.setCashflow(poe.getPurchaseOrderProcurementEntity().getCashflow());
        po.setTextEntity(poe.getPurchaseOrderProcurementEntity().getTextEntity());
        po.getPoDocumentList().addAll(poe.getPurchaseOrderProcurementEntity().getPoDocumentList());
        po.getProjectDocList().addAll(poe.getPurchaseOrderProcurementEntity().getProjectDocList());
        po.getProjectTextSnippetList().addAll(poe.getPurchaseOrderProcurementEntity().getProjectTextSnippetList());
    }

    public PurchaseOrderEntity findById(Long id) {
        List<PurchaseOrderEntity> list = dao.findById(PurchaseOrderEntity.class, id != null ? id : 0L);
        PurchaseOrderEntity po = list.isEmpty() ? null : list.get(0);

        if (po.getPurchaseOrderProcurementEntity().getSupplier() != null) {
            //addPrefixToVariation(po);
            po.getPurchaseOrderProcurementEntity().getSupplier().getContacts().addAll(contactService.findByContactsBySupplier(po.getPurchaseOrderProcurementEntity().getSupplier().getId()));
        }
        if (po != null) {
            po.getProjectEntity().getCurrencies().addAll(projectService.findProjectCurrencyByProjectId(po.getProjectEntity().getId()));
            po.getPurchaseOrderProcurementEntity().getRequisitions().addAll(requisitionDao.findRequisitionByPurchaseOrder(po.getPurchaseOrderProcurementEntity().getId()));
            po.getPurchaseOrderProcurementEntity().getDeliverables().addAll(deliverableDao.findDeliverableByPurchaseOrder(po.getPurchaseOrderProcurementEntity().getId()));
            po.getPurchaseOrderProcurementEntity().getScopeSupplyList().addAll(itemService.findByPoId(po.getId()));
            List<CashflowEntity> cashflows = cashflowService.findByPoId(po.getPurchaseOrderProcurementEntity().getId());
            po.getPurchaseOrderProcurementEntity().setCashflow(!cashflows.isEmpty() ? cashflows.get(0) : null);
            if (po.getPurchaseOrderProcurementEntity().getCashflow() != null) {
                po.getPurchaseOrderProcurementEntity().getCashflow().getCashflowDetailList().addAll(cashflowService.findDetailByCashflowId(po.getPurchaseOrderProcurementEntity().getCashflow().getId()));
            }
            po.getPurchaseOrderProcurementEntity().setTextEntity(textService.findByPoId(po.getPurchaseOrderProcurementEntity().getId()));
            if (po.getPurchaseOrderProcurementEntity().getTextEntity() != null) {
                List<ClausesEntity> clausesEntities = textService.findClausesByTextId(po.getPurchaseOrderProcurementEntity().getTextEntity().getId());
                po.getPurchaseOrderProcurementEntity().getTextEntity().getClausesList().addAll(clausesEntities);
            }
        }
        return list.isEmpty() ? null : list.get(0);
    }

    public PurchaseOrderEntity findPOToCreateVariation(Long id) {
        List<PurchaseOrderEntity> list = dao.findById(PurchaseOrderEntity.class, id != null ? id : 0L);
        PurchaseOrderEntity po = list.isEmpty() ? null : list.get(0);
        if (po.getPurchaseOrderProcurementEntity().getSupplier() != null) {
            addPrefixToVariation(po);
            po.getPurchaseOrderProcurementEntity().getSupplier().getContacts().addAll(contactService.findByContactsBySupplier(po.getPurchaseOrderProcurementEntity().getSupplier().getId()));
        }
        if (po != null) {
            po.getProjectEntity().getCurrencies().addAll(projectService.findProjectCurrencyByProjectId(po.getProjectEntity().getId()));
            po.getPurchaseOrderProcurementEntity().getRequisitions().addAll(requisitionDao.findRequisitionByPurchaseOrder(po.getPurchaseOrderProcurementEntity().getId()));
            po.getPurchaseOrderProcurementEntity().getDeliverables().addAll(deliverableDao.findDeliverableByPurchaseOrder(po.getPurchaseOrderProcurementEntity().getId()));
            List<CashflowEntity> cashflows = cashflowService.findByPoId(po.getPurchaseOrderProcurementEntity().getId());
            if (!cashflows.isEmpty()) {
                po.getPurchaseOrderProcurementEntity().setCashflow(cashflows.get(0));
                po.getPurchaseOrderProcurementEntity().getCashflow().getCashflowDetailList().addAll(cashflowService.findDetailByCashflowId(po.getPurchaseOrderProcurementEntity().getCashflow().getId()));
            }
        }
        return list.isEmpty() ? null : list.get(0);
    }

    public List<PurchaseOrderEntity> findByProjectIdAndPo(final Long projectId, final String poNo) {
        return dao.findByProjectAndPo(projectId, poNo);
    }

    public List<PurchaseOrderEntity> findPOListWithoutExportCMS(final Long projectId) {
        return dao.findPOListWithoutExportCMS(projectId);
    }

    public List<PurchaseOrderEntity> findPOListWithoutExportJDE(final Long projectId) {
        return dao.findPOListWithoutExportJDE(projectId);
    }

    public List<PurchaseOrderEntity> findByProjectIdCustomizedSort(final Long projectId, Map<String, Boolean> sortByMap) {
        return dao.findByProjectCustomizedSort(projectId, sortByMap);
    }

    public boolean isVarNumberUsed(PurchaseOrderEntity purchaseOrder) {
        List<PurchaseOrderEntity> list = dao.findByVariation(purchaseOrder);
        return !list.isEmpty();
    }

    @Transactional
    public void doUpdatePurchaseOrder(PurchaseOrderEntity entity) {
        dao.update(entity);
    }

    public VPurchaseOrder findVPOById(Long id) {
        return dao.findVPOById(id);
    }


    public BigDecimal calculateProjectValue(List<ItemEntity> items, ProjectCurrencyEntity currency) {
        BigDecimal poValue = new BigDecimal("0.00000").setScale(5, RoundingMode.CEILING);
        if (currency != null) {
            poValue = calculatePOValue(items, currency);
            poValue = poValue != null ? poValue.multiply(currency.getCurrencyFactor()) : null;
        }
        return poValue;
    }

    public BigDecimal calculatePOValue(List<ItemEntity> list, ProjectCurrencyEntity currency) {
        BigDecimal poValue = new BigDecimal("0");
        if (currency == null) {
            return poValue;
        }
        for (ItemEntity item : list) {
            BigDecimal amount = item.getCost() != null ? item.getCost() : null;
            BigDecimal exchangeRateSource = item.getProjectCurrency() != null ? item.getProjectCurrency().getExchangeRate() : null;
            BigDecimal exchangeRateTarget = currency.getExchangeRate();
            poValue = poValue.add(Util.currencyToCurrency(amount, exchangeRateSource, exchangeRateTarget));
        }
        return poValue;
    }

    private BigDecimal calculatePOValueByCurrency(List<ItemEntity> list, ProjectCurrencyEntity currency) {
        BigDecimal poValue = new BigDecimal("0");
        if (currency == null) {
            return poValue;
        }

        for (ItemEntity item : list) {
            if (item.getProjectCurrency() != null && item.getProjectCurrency().getCurrency().getId().longValue() == currency.getCurrency().getId()) {
                BigDecimal amount = item.getCost() != null ? item.getCost() : new BigDecimal("0");
                BigDecimal quantity = new BigDecimal(item.getQuantity() != null ? item.getQuantity().toString() : "0");
                poValue = poValue.add(amount.multiply(quantity));
            }
        }
        return poValue;
    }

    public Map<ProjectCurrencyEntity, BigDecimal> getTotalValuesByCurrency(List<ItemEntity> items) {
        Map<ProjectCurrencyEntity, BigDecimal> totals = new HashMap<>();
        List<ProjectCurrencyEntity> currencies = findAllCurrenciesOnPO(null, items);
        for (ProjectCurrencyEntity pce : currencies) {
            totals.put(pce, calculatePOValueByCurrency(items, pce));
        }
        return totals;
    }

    private List<ProjectCurrencyEntity> findAllCurrenciesOnPO(ProjectCurrencyEntity defaultCurrency, List<ItemEntity> items) {
        List<ProjectCurrencyEntity> currencies = new ArrayList<>();
        if (defaultCurrency != null) {
            currencies.add(defaultCurrency);
        }
        for (ItemEntity item : items) {
            if (item.getProjectCurrency() != null && !hasCurrency(currencies, item.getProjectCurrency().getCurrency())) {
                currencies.add(item.getProjectCurrency());
            }
        }
        return currencies;
    }

    public Map<ProjectCurrencyEntity, BigDecimal> getBalanceByCurrency(List<ItemEntity> items, List<CashflowDetailEntity> milestones) {
        Map<ProjectCurrencyEntity, BigDecimal> balance = new HashMap<>();
        Map<ProjectCurrencyEntity, BigDecimal> totals = getTotalValuesByCurrency(items);

        for (ProjectCurrencyEntity currencyEntity : totals.keySet()) {
            BigDecimal totalPayment = cashflowService.getTotalMilestonePayments(milestones, currencyEntity);
            BigDecimal total = totals.get(currencyEntity);
            balance.put(currencyEntity, total.add(totalPayment.multiply(new BigDecimal(-1))));
        }
        return balance;
    }


    private boolean hasCurrency(List<ProjectCurrencyEntity> list, CurrencyEntity currencyEntity) {
        boolean has = false;
        for (ProjectCurrencyEntity pce : list) {
            if (pce.getCurrency().getId().longValue() == currencyEntity.getId().longValue()) {
                has = true;
                break;
            }
        }
        return has;
    }

    /*
    It finds all purchase orders that haave the same Po Number and are under the same
    Project.
    * */
    public List<PurchaseOrderEntity> findRelatives(Long purchaseOrderId) {
        List<PurchaseOrderEntity> list = new ArrayList<>();
        PurchaseOrderEntity po = load(purchaseOrderId);
        if (po != null) {
            for (PurchaseOrderEntity poe : dao.findByProjectAndPo(po.getProjectEntity().getId(), po.getPo())) {
                if (poe.getId().longValue() != purchaseOrderId.longValue()) {
                    list.add(poe);
                }
            }

        }
        return list;
    }

    public String joinRequisitionsNumbers() {
        return null;
    }


    public String generateName(PurchaseOrderEntity po) {
        String fileName = po.getProjectEntity().getProjectNumber() != null ? po.getProjectEntity().getProjectNumber() + "-" : "";
        fileName = fileName + (po.getPo() != null ? po.getPo() + " " : "");
        if (po.getVariationNumber() != null && !po.getVariationNumber().equalsIgnoreCase("v0") && !po.getVariationNumber().equalsIgnoreCase("0")) {
            fileName = fileName + po.getVariationNumber().toUpperCase() + " ";
        }
        return fileName + (po.getPoTitle() != null ? po.getPoTitle() : "");
    }


    public List<PurchaseOrderEntity> findPosBy(FilterPO filter) {
        return dao.findPOsBy(filter);
    }

    public List<PurchaseOrderEntity> findAllPOs(final Long projectId) {
        return dao.findAllPOs(projectId);
    }

    public int getTotalNumberOfPOs(final Long projectId){
        return dao.getTotalNumberOfPOs(projectId);
    }

    public int getNumberOfCompletedPOs(final Long projectId){
        return  dao.getNumberOfCompletedPOs(projectId);
    }

    public int getNumberOfOpenPOs(final Long projectId){
        return dao.getNumberOfOpenPOs(projectId);
    }

    public int getNumberDeliveryNextMoth(final Long projectId,final Date nextMothIni, final Date nextMothEnd) {
        return dao.getNumberDeliveryNextMoth(projectId, nextMothIni, nextMothEnd);
    }

    public int getNumberMrrOutstanding(final Long projectId){
        return dao.getNumberMrrOutstanding(projectId);
    }

    @Transactional
    public void resetActivity(PurchaseOrderEntity purchaseOrderEntity) {
        dao.resetActivity(purchaseOrderEntity);
    }

    public boolean canEdit(PurchaseOrderEntity po, UserEntity user) {
        PurchaseOrderEntity purchaseOrderEntity = findById(po.getId());
        boolean canEdit = true;
        if (purchaseOrderEntity.isLocked() != null && purchaseOrderEntity.isLocked()) {
            if (purchaseOrderEntity.getLockedBy().getId().longValue() != user.getId().longValue()) {
                Date date = new Date();
                long difference = date.getTime() - purchaseOrderEntity.getLastActivityUpdate().getTime();
                if (difference <= 1800000) {
                    canEdit = false;
                }
            }
        }
        return canEdit;
    }

    @Transactional
    public void lock(PurchaseOrderEntity purchaseOrderEntity, UserEntity userEntity) {
        dao.lockPO(purchaseOrderEntity, userEntity);

    }

    @Transactional
    public void unlock(PurchaseOrderEntity po) {
        dao.unLockPO(po);
    }

    public boolean canUnlock(UserEntity user, PurchaseOrderEntity po) {
        boolean canUnlock = false;
        PurchaseOrderEntity poEntity = findById(po.getId());
        if (poEntity.isLocked() != null && poEntity.isLocked()) {
            UserEntity locker = poEntity.getLockedBy();
            if (locker != null && locker.getId().longValue() == user.getId().longValue()) {
                canUnlock = true;
            }
        }
        return canUnlock;
    }

    public PurchaseOrderEntity findFirstPO(final PurchaseOrderEntity purchaseOrder) {
        List<PurchaseOrderEntity> list = dao.findFirstPO(purchaseOrder);
        if(!list.isEmpty()){
           return list.get(0);
        }
        return null;
    }

    public PurchaseOrderProcurementEntity findPOEById(final Long poeId){
        return dao.findPOEntityById(poeId);
    }


}