package ch.swissbytes.fqmes.model.dao;

import ch.swissbytes.fqmes.boundary.purchase.SearchPurchase;
import ch.swissbytes.fqmes.model.Filter;
import ch.swissbytes.fqmes.model.entities.VPurchaseOrder;
import ch.swissbytes.fqmes.util.Util;
import org.apache.commons.lang3.StringUtils;

import javax.persistence.Query;
import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Logger;

/**
 * Created by alvaro on 9/8/14.
 */

public class PurchaseOrderViewDao extends GenericDao<VPurchaseOrder> implements Serializable{

    private static final Logger log = Logger.getLogger(PurchaseOrderViewDao.class.getName());


    public VPurchaseOrder load(final Long id){
        return super.load(VPurchaseOrder.class,id);
    }
    @Override
    protected  void applyCriteriaValues(Query query,Filter f){
        log.info("protected  void applyCriteriaValues(Query query,Filter f)");
        if(f!=null){
            log.info("filtering.....");
            SearchPurchase filter=(SearchPurchase)f;
            if(StringUtils.isNotBlank(filter.getProject())&&StringUtils.isNotEmpty(filter.getProject())){
                query.setParameter("PROJECT","%"+filter.getProject().trim()+"%");
            }
            if(StringUtils.isNotBlank(filter.getPo())&&StringUtils.isNotEmpty(filter.getPo())){
                query.setParameter("PO","%"+filter.getPo().trim()+"%");
            }
            if(StringUtils.isNotBlank(filter.getVariation())&&StringUtils.isNotEmpty(filter.getVariation())){
                query.setParameter("VARIATION","%"+filter.getVariation().trim()+"%");
            }
            if(StringUtils.isNotBlank(filter.getPoTitle())&&StringUtils.isNotEmpty(filter.getPoTitle())){
                query.setParameter("PO_TITLE","%"+filter.getPoTitle().trim()+"%");
            }
            if(StringUtils.isNotBlank(filter.getSupplier())&&StringUtils.isNotEmpty(filter.getSupplier())){
                query.setParameter("SUPPLIER","%"+filter.getSupplier().trim()+"%");
            }
            if(StringUtils.isNotBlank(filter.getResponsibleExpediting())&&StringUtils.isNotEmpty(filter.getResponsibleExpediting())){
                query.setParameter("RESPONSIBLE_EXPEDITING","%"+filter.getResponsibleExpediting().trim()+"%");
            }
            if(StringUtils.isNotEmpty(filter.getIncoTerm())&&StringUtils.isNotBlank(filter.getIncoTerm())){
                query.setParameter("INCO_TERM","%"+filter.getIncoTerm().trim()+"%");
            }
            if(filter.getDeliveryDateStart()!=null){
                query.setParameter("START_DELIVERY_DATE",filter.getDeliveryDateStart());
            }
            if(filter.getDeliveryDateEnd()!=null){
                query.setParameter("END_DELIVERY_DATE",filter.getDeliveryDateEnd());
            }
            if(filter.getDueIn()!=null&&filter.getDueIn().intValue()>=0){
                Date startDueInDate=new Date();
                query.setParameter("START_DUE_DATE_IN",startDueInDate);
                query.setParameter("END_DUE_DATE_IN",new Util().addDays(startDueInDate,filter.getDueIn()*7));
            }
            prepareValueSubquery(query,filter);
        }else{
            log.info("filter is null");
        }
    }

    protected String getEntity(){
        return VPurchaseOrder.class.getSimpleName();
    }

    @Override
    protected  String addCriteria(Filter f){
        log.info("protected  String addCriteria(Filter f)");
        StringBuilder sb=new StringBuilder();
        if(f!=null){
            log.info("filtering.....");
            SearchPurchase filter=(SearchPurchase)f;
            if(StringUtils.isNotBlank(filter.getProject())&&StringUtils.isNotEmpty(filter.getProject())){
                sb.append(" AND lower(x.project) like lower(:PROJECT)");
            }
            if(StringUtils.isNotBlank(filter.getPo())&&StringUtils.isNotEmpty(filter.getPo())){
                sb.append(" AND lower(x.po) like lower(:PO)");
            }
            if(StringUtils.isNotBlank(filter.getPoTitle())&&StringUtils.isNotEmpty(filter.getPoTitle())){
                sb.append(" AND lower(x.poTitle) like lower(:PO_TITLE)");
            }
            if(StringUtils.isNotBlank(filter.getVariation())&&StringUtils.isNotEmpty(filter.getVariation())){
                sb.append(" AND lower(x.variation) like lower(:VARIATION)");
            }
            if(StringUtils.isNotBlank(filter.getSupplier())&&StringUtils.isNotEmpty(filter.getSupplier())){
                sb.append(" AND lower(x.supplier) like lower(:SUPPLIER)");
            }
            if(StringUtils.isNotBlank(filter.getResponsibleExpediting())&&StringUtils.isNotEmpty(filter.getResponsibleExpediting())){
                sb.append(" AND lower(x.responsibleExpediting) like lower(:RESPONSIBLE_EXPEDITING)");
            }
            if(StringUtils.isNotEmpty(filter.getIncoTerm())&&StringUtils.isNotBlank(filter.getIncoTerm())){
                sb.append(" AND lower(x.incoTerm) like lower(:INCO_TERM)");
            }
            if(filter.getDeliveryDateStart()!=null){
                sb.append(" AND x.deliveryDate>=:START_DELIVERY_DATE");
            }
            if(filter.getDeliveryDateEnd()!=null){
                sb.append(" AND x.deliveryDate<=:END_DELIVERY_DATE ");
            }
            if(filter.getDueIn()!=null){
                sb.append(" AND (x.requiredDate>=:START_DUE_DATE_IN AND x.requiredDate<=:END_DUE_DATE_IN)");
            }
           sb.append(prepareSubquery(filter));
        }else{
            log.info("filter is null");
        }
        System.out.println("query ============================ ["+sb.toString()+"]");
        return sb.toString();
    }
    private String prepareSubquery(SearchPurchase filter){
        StringBuilder subQuery=new StringBuilder();
        if(filter.hasAnyValueForScopeSupplyActive()){
            subQuery.append(" AND x.poId IN ( ");
            subQuery.append(" SELECT ss.purchaseOrder.id ");
            subQuery.append(" FROM VScopeSupply ss ");
            subQuery.append(" WHERE 1=1 ");
            if(filter.getLeadTime()!=null &&filter.getLeadTime().intValue()>=0 && filter.getLeadTime().intValue()<=20){
                subQuery.append(" AND (ss.leadTimeDays<=:LEAD_TIME)");
            }
            if(filter.getVariance()!=null){
                subQuery.append(" AND ss.variance=:VARIANCE");
            }
            subQuery.append(" ) ");
        }
        return subQuery.toString();
    }

    private void prepareValueSubquery(Query query, SearchPurchase filter){
        if(filter.getLeadTime()!=null){
            //System.out.println("LEAD_TIME  "+filter.getLeadTime()*7);
            query.setParameter("LEAD_TIME",filter.getLeadTime()*7);
        }
        if(filter.getVariance()!=null){
            query.setParameter("VARIANCE",filter.getVariance());
        }

    }

    @Override
    public String orderBy(){
        return "";
    }

    @Override
    public String orderBy(String field,boolean ascending){
        return ascending?" ORDER BY "+field +" ASC ":" ORDER BY "+field+" DESC";
    }

}
