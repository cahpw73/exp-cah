package ch.swissbytes.Service.business.purchase;

import ch.swissbytes.Service.business.Service;
import ch.swissbytes.Service.business.cashflow.CashflowService;
import ch.swissbytes.Service.business.comment.CommentDao;
import ch.swissbytes.Service.business.contact.ContactService;
import ch.swissbytes.Service.business.deliverable.DeliverableDao;
import ch.swissbytes.Service.business.enumService.EnumService;
import ch.swissbytes.Service.business.item.ItemService;
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
import ch.swissbytes.fqmes.util.Util;
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

    private final String PREFIX="v";

    public PurchaseOrderService() {
        super.initialize(dao);
    }

    public PurchaseOrderEntity load(Long id) {
        PurchaseOrderEntity purchaseOrderEntity = dao.load(id);
        addPrefixToVariation(purchaseOrderEntity);
        if (purchaseOrderEntity.getPoEntity().getSupplier() != null) {
            purchaseOrderEntity.getPoEntity().getSupplier().getContacts().addAll(contactService.findByContactsBySupplier(purchaseOrderEntity.getPoEntity().getSupplier().getId()));
        }
        return purchaseOrderEntity;
    }


    @Transactional
    public void doSave(PurchaseOrderEntity newPurchaseOrder, List<CommentEntity> comments, List<ScopeSupplyEntity> scopeSupplies, SupplierEntity supplier) {
        if(newPurchaseOrder.getPoEntity().getId() == null){
            newPurchaseOrder.setProject(newPurchaseOrder.getProjectEntity().getProjectNumber());
            dao.save(newPurchaseOrder.getPoEntity());
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
        dao.updatePOEntity(por.getPoEntity());
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
            //deleteSupplier(purchaseOrderId);
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

    public List<PurchaseOrderEntity> purchaseListByProjectIdAnPoNo(final Long projectId, final String poNo) {
        return dao.findPOByProjectIdAndPoNo(projectId, poNo);
    }


    @Transactional
    public PurchaseOrderEntity savePOOnProcurement(PurchaseOrderEntity purchaseOrderEntity) {
        removePrefixIfAny(purchaseOrderEntity);
        purchaseOrderEntity.getPoEntity().setContactExpediting(purchaseOrderEntity.getPoEntity().getContactEntity());
        POEntity po = dao.savePOEntity(purchaseOrderEntity.getPoEntity());
        purchaseOrderEntity.setPoEntity(po);
        // purchaseOrderEntity.setPo(purchaseOrderEntity.getProjectEntity().getProjectNumber());
        purchaseOrderEntity.setLastUpdate(new Date());
        purchaseOrderEntity.setStatus(enumService.getStatusEnumEnable());
        purchaseOrderEntity.setPurchaseOrderStatus(ExpeditingStatusEnum.ISSUED);
        String incoTerms = getStrToIncoTerm(po.getPoint());
        String fullIncoTerms = po.getPoint();
        if(exitsDeliveryPointInIncoTerms(incoTerms)){
            purchaseOrderEntity.setIncoTerm(incoTerms);
            purchaseOrderEntity.setFullIncoTerms(fullIncoTerms);
        }else{
            purchaseOrderEntity.setIncoTerm(null);
            purchaseOrderEntity.setFullIncoTerms(null);
        }
        dao.save(purchaseOrderEntity);
        //requisition daos
        requisitionDao.doSave(purchaseOrderEntity.getPoEntity(), po.getRequisitions());
        //items
        itemService.doSave(po.getScopeSupplyList(), purchaseOrderEntity,incoTerms,fullIncoTerms);
        //deliverable
        deliverableDao.doSave(purchaseOrderEntity.getPoEntity(), po.getDeliverables());
        //CashFlow
        cashflowService.doSave(purchaseOrderEntity.getPoEntity().getCashflow(), po);
        //Text
        textService.doSave(purchaseOrderEntity.getPoEntity().getTextEntity(), po);

        return purchaseOrderEntity;
    }

    public boolean exitsDeliveryPointInIncoTerms(String point){
        for(IncoTermsEnum i : IncoTermsEnum.values()){
            if(i.name().equalsIgnoreCase(point)){
                return true;
            }
        }
        return  false;
    }

    public String getStrToIncoTerm(String point){
        return point!=null&&point.length()>2? point.substring(0,3):point;
    }

    public void addPrefixToVariation(PurchaseOrderEntity purchaseOrderEntity) {
        if(purchaseOrderEntity!=null&&StringUtils.isNotEmpty(purchaseOrderEntity.getVariation())&&StringUtils.isNotBlank(purchaseOrderEntity.getVariation())) {
            purchaseOrderEntity.setVariation(PREFIX + purchaseOrderEntity.getVariation());
        }
    }
    public void removePrefixIfAny(PurchaseOrderEntity purchaseOrder){
        if(purchaseOrder!=null&&StringUtils.isNotEmpty(purchaseOrder.getVariation())&&StringUtils.isNotBlank(purchaseOrder.getVariation())){
            while(purchaseOrder.getVariation().toLowerCase().trim().startsWith(PREFIX)&&StringUtils.isNotBlank(purchaseOrder.getVariation())){
                purchaseOrder.setVariation(purchaseOrder.getVariation().substring(1,purchaseOrder.getVariation().length()));
            }
        }
    }

    @Transactional
    public PurchaseOrderEntity savePOOnProcurementNewVariation(PurchaseOrderEntity purchaseOrderEntity) {
        removePrefixIfAny(purchaseOrderEntity);
        POEntity po = dao.savePOEntity(purchaseOrderEntity.getPoEntity());
        purchaseOrderEntity.setPoEntity(po);
        purchaseOrderEntity.setLastUpdate(new Date());
        purchaseOrderEntity.setStatus(enumService.getStatusEnumEnable());
        purchaseOrderEntity.setPurchaseOrderStatus(ExpeditingStatusEnum.ISSUED);
        purchaseOrderEntity.setPoDeliveryDate(null);
        dao.save(purchaseOrderEntity);
        //requisitionDao.doSave(purchaseOrderEntity.getPoEntity(), po.getRequisitions());
        //deliverableDao.doSave(purchaseOrderEntity.getPoEntity(), po.getDeliverables());
        cashflowService.doSave(purchaseOrderEntity.getPoEntity().getCashflow(), po);

        return purchaseOrderEntity;
    }

    @Transactional
    public PurchaseOrderEntity updatePOOnProcurement(PurchaseOrderEntity purchaseOrderEntity) {
        removePrefixIfAny(purchaseOrderEntity);
        purchaseOrderEntity.getPoEntity().setContactExpediting(purchaseOrderEntity.getPoEntity().getContactEntity());
        POEntity po = dao.updatePOEntity(purchaseOrderEntity.getPoEntity());
        collectLists(po, purchaseOrderEntity);
        purchaseOrderEntity.setPoEntity(po);
        purchaseOrderEntity.setLastUpdate(new Date());
        purchaseOrderEntity.setVariation(purchaseOrderEntity.getVariation());
        String incoTerms = getStrToIncoTerm(po.getPoint());
        if(exitsDeliveryPointInIncoTerms(incoTerms)){
            purchaseOrderEntity.setIncoTerm(incoTerms);
            purchaseOrderEntity.setFullIncoTerms(po.getPoint());
        }else{
            purchaseOrderEntity.setIncoTerm(null);
            purchaseOrderEntity.setFullIncoTerms(null);
        }
        dao.update(purchaseOrderEntity);
        //requisition daos
        requisitionDao.doUpdate(purchaseOrderEntity.getPoEntity(), po.getRequisitions());
        //items
        itemService.doUpdate(po.getScopeSupplyList(), purchaseOrderEntity);
        //deliverable
        deliverableDao.doUpdate(purchaseOrderEntity.getPoEntity(), po.getDeliverables());
        //cashFlow
        cashflowService.doUpdate(purchaseOrderEntity.getPoEntity().getCashflow(), po);
        //Text
        textService.doUpdate(purchaseOrderEntity.getPoEntity().getTextEntity(), po);
        return purchaseOrderEntity;
    }


    @Transactional
    public PurchaseOrderEntity updateOnlyPOOnProcurement(PurchaseOrderEntity purchaseOrderEntity) {
        removePrefixIfAny(purchaseOrderEntity);
        String incoTerms = getStrToIncoTerm(purchaseOrderEntity.getPoEntity().getPoint());
        if(exitsDeliveryPointInIncoTerms(incoTerms)){
            purchaseOrderEntity.setIncoTerm(incoTerms);
            purchaseOrderEntity.setFullIncoTerms(purchaseOrderEntity.getPoEntity().getDeliveryInstruction());
        }else{
            purchaseOrderEntity.setIncoTerm(null);
            purchaseOrderEntity.setFullIncoTerms(null);
        }
        purchaseOrderEntity.getPoEntity().setContactExpediting(purchaseOrderEntity.getPoEntity().getContactEntity());
        dao.updatePOEntity(purchaseOrderEntity.getPoEntity());
        return purchaseOrderEntity;
    }

    private void collectLists(POEntity po, PurchaseOrderEntity poe) {
        /*po.getItemList().clear();
        po.getItemList().addAll(poe.getPoEntity().getItemList());*/
        po.getScopeSupplyList().clear();
        po.getScopeSupplyList().addAll(poe.getPoEntity().getScopeSupplyList());
        po.getDeliverables().clear();
        po.getDeliverables().addAll(poe.getPoEntity().getDeliverables());
        po.getRequisitions().clear();
        po.getRequisitions().addAll(poe.getPoEntity().getRequisitions());
        po.setCashflow(poe.getPoEntity().getCashflow());
        po.setTextEntity(poe.getPoEntity().getTextEntity());
    }

    public PurchaseOrderEntity findById(Long id) {
        List<PurchaseOrderEntity> list = dao.findById(PurchaseOrderEntity.class, id != null ? id : 0L);
        PurchaseOrderEntity po = list.isEmpty() ? null : list.get(0);

        if (po.getPoEntity().getSupplier() != null) {
            addPrefixToVariation(po);
            po.getPoEntity().getSupplier().getContacts().addAll(contactService.findByContactsBySupplier(po.getPoEntity().getSupplier().getId()));
        }
        if (po != null) {
            po.getProjectEntity().getCurrencies().addAll(projectService.findProjectCurrencyByProjectId(po.getProjectEntity().getId()));
            po.getPoEntity().getRequisitions().addAll(requisitionDao.findRequisitionByPurchaseOrder(po.getPoEntity().getId()));
            po.getPoEntity().getDeliverables().addAll(deliverableDao.findDeliverableByPurchaseOrder(po.getPoEntity().getId()));
            po.getPoEntity().getScopeSupplyList().addAll(itemService.findByPoId(po.getId()));
            //po.getPoEntity().getItemList().addAll(itemService.findByPoId(po.getPoEntity().getId()));
            List<CashflowEntity> cashflows = cashflowService.findByPoId(po.getPoEntity().getId());
            po.getPoEntity().setCashflow(!cashflows.isEmpty() ? cashflows.get(0) : null);
            if (po.getPoEntity().getCashflow() != null) {
                po.getPoEntity().getCashflow().getCashflowDetailList().addAll(cashflowService.findDetailByCashflowId(po.getPoEntity().getCashflow().getId()));
            }
            po.getPoEntity().setTextEntity(textService.findByPoId(po.getPoEntity().getId()));
            if (po.getPoEntity().getTextEntity() != null) {
                List<ClausesEntity> clausesEntities = textService.findClausesByTextId(po.getPoEntity().getTextEntity().getId());
                po.getPoEntity().getTextEntity().getClausesList().addAll(clausesEntities);
            }
        }
        return list.isEmpty() ? null : list.get(0);
    }

    public PurchaseOrderEntity findPOToCreateVariation(Long id) {
        List<PurchaseOrderEntity> list = dao.findById(PurchaseOrderEntity.class, id != null ? id : 0L);
        PurchaseOrderEntity po = list.isEmpty() ? null : list.get(0);

        if (po.getPoEntity().getSupplier() != null) {
            addPrefixToVariation(po);
            po.getPoEntity().getSupplier().getContacts().addAll(contactService.findByContactsBySupplier(po.getPoEntity().getSupplier().getId()));
        }
        if (po != null) {
            po.getProjectEntity().getCurrencies().addAll(projectService.findProjectCurrencyByProjectId(po.getProjectEntity().getId()));
            po.getPoEntity().getRequisitions().addAll(requisitionDao.findRequisitionByPurchaseOrder(po.getPoEntity().getId()));
            po.getPoEntity().getDeliverables().addAll(deliverableDao.findDeliverableByPurchaseOrder(po.getPoEntity().getId()));
            List<CashflowEntity> cashflows = cashflowService.findByPoId(po.getPoEntity().getId());
            if (!cashflows.isEmpty()) {
                po.getPoEntity().setCashflow(cashflows.get(0));
                po.getPoEntity().getCashflow().getCashflowDetailList().addAll(cashflowService.findDetailByCashflowId(po.getPoEntity().getCashflow().getId()));
            }

        }
        return list.isEmpty() ? null : list.get(0);
    }

    public List<PurchaseOrderEntity> findByProjectIdAndPo(final Long projectId, final String poNo) {
        return dao.findByProjectAndPo(projectId, poNo);
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


    public BigDecimal calculateProjectValue(List<ScopeSupplyEntity> items, ProjectCurrencyEntity currency) {
        BigDecimal poValue = new BigDecimal("0.00000").setScale(5, RoundingMode.CEILING);
        if (currency != null) {
            poValue = calculatePOValue(items, currency);
            poValue = poValue != null ? poValue.multiply(currency.getCurrencyFactor()) : null;
        }
        return poValue;
    }

    public BigDecimal calculatePOValue(List<ScopeSupplyEntity> list, ProjectCurrencyEntity currency) {
        BigDecimal poValue = new BigDecimal("0");
        if (currency == null) {
            return poValue;
        }
        for (ScopeSupplyEntity item : list) {
            BigDecimal amount = item.getCost() != null ? item.getCost() : null;
            BigDecimal exchangeRateSource = item.getProjectCurrency() != null ? item.getProjectCurrency().getExchangeRate() : null;
            BigDecimal exchangeRateTarget = currency.getExchangeRate();
            poValue = poValue.add(Util.currencyToCurrency(amount, exchangeRateSource, exchangeRateTarget));
        }
        return poValue;
    }
    private BigDecimal calculatePOValueByCurrency(List<ScopeSupplyEntity> list, ProjectCurrencyEntity currency){
        BigDecimal poValue = new BigDecimal("0");
        if (currency == null) {
            return poValue;
        }

        for (ScopeSupplyEntity item : list) {
            if(item.getProjectCurrency()!=null&&item.getProjectCurrency().getCurrency().getId().longValue()==currency.getCurrency().getId()) {
                BigDecimal amount = item.getCost() != null ? item.getCost() : new BigDecimal("0");
                BigDecimal quantity = new BigDecimal(item.getQuantity() != null ? item.getQuantity().toString() : "0");
                poValue = poValue.add(amount.multiply(quantity));
            }
        }
        return poValue;
    }

    public Map<ProjectCurrencyEntity, BigDecimal> getTotalValuesByCurrency(List<ScopeSupplyEntity> items) {
        Map<ProjectCurrencyEntity, BigDecimal> totals = new HashMap<>();
        List<ProjectCurrencyEntity> currencies = findAllCurrenciesOnPO(null, items);
        for (ProjectCurrencyEntity pce : currencies) {
            totals.put(pce, calculatePOValueByCurrency(items, pce));
        }
        return totals;
    }

    private List<ProjectCurrencyEntity> findAllCurrenciesOnPO(ProjectCurrencyEntity defaultCurrency, List<ScopeSupplyEntity> items) {
        List<ProjectCurrencyEntity> currencies = new ArrayList<>();
        if(defaultCurrency!=null) {
            currencies.add(defaultCurrency);
        }
        for (ScopeSupplyEntity item : items) {
            if (item.getProjectCurrency() != null && !hasCurrency(currencies, item.getProjectCurrency().getCurrency())) {
                currencies.add(item.getProjectCurrency());
            }
        }
        return currencies;
    }

    public Map<ProjectCurrencyEntity, BigDecimal> getBalanceByCurrency( List<ScopeSupplyEntity> items, List<CashflowDetailEntity> milestones) {
        Map<ProjectCurrencyEntity, BigDecimal> balance = new HashMap<>();
        Map<ProjectCurrencyEntity, BigDecimal> totals = getTotalValuesByCurrency( items);

        for(ProjectCurrencyEntity currencyEntity:totals.keySet()){
            BigDecimal totalPayment=cashflowService.getTotalMilestonePayments(milestones, currencyEntity);
            BigDecimal total=totals.get(currencyEntity);
            balance.put(currencyEntity,total.add(totalPayment.multiply(new BigDecimal(-1))));
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
    public List<PurchaseOrderEntity> findRelatives(Long purchaseOrderId){
        List<PurchaseOrderEntity> list=new ArrayList<>();
        PurchaseOrderEntity po=load(purchaseOrderId);
        if(po!=null){
            for (PurchaseOrderEntity poe:dao.findByProjectAndPo(po.getProjectEntity().getId(),po.getPo())){
                if(poe.getId().longValue()!=purchaseOrderId.longValue()){
                    list.add(poe);
                }
            }

        }
        return list;
    }
    public String joinRequisitionsNumbers(){
        return null;
    }

}
