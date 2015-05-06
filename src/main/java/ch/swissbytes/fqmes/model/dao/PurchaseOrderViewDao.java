package ch.swissbytes.fqmes.model.dao;

import ch.swissbytes.fqmes.boundary.purchase.SearchPurchase;
import ch.swissbytes.fqmes.model.Filter;
import ch.swissbytes.fqmes.model.entities.VPurchaseOrder;
import ch.swissbytes.fqmes.types.PurchaseOrderStatusEnum;
import ch.swissbytes.fqmes.util.Util;
import org.apache.commons.lang3.StringUtils;

import javax.persistence.Query;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
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


            if(StringUtils.isNotEmpty(filter.getStatuses())&&StringUtils.isNotBlank(filter.getStatuses())){
                String []statuses=filter.getStatuses().split(",");
                List<PurchaseOrderStatusEnum> list=new ArrayList<PurchaseOrderStatusEnum>();
                for(String status:statuses){
                    try{
                        list.add(PurchaseOrderStatusEnum.getEnum(Integer.parseInt(status)));
                    }catch(NumberFormatException nfe){

                    }
                }
                query.setParameter("PURCHASE_ORDER_STATUS_LIST",list);
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
            if(StringUtils.isNotEmpty(filter.getStatuses())&&StringUtils.isNotBlank(filter.getStatuses())){
                sb.append(" AND  x.purchaseOrderStatus IN(:PURCHASE_ORDER_STATUS_LIST)");
            }
           sb.append(prepareSubquery(filter));
        }else{
            log.info("filter is null");
        }
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
            if(filter.getVariance()!=null&&!filter.getVariance().equals("all")){
                if(filter.getVariance().equalsIgnoreCase("+ve")){
                    subQuery.append(" AND ss.variance>=0");
                }else if(filter.getVariance().equalsIgnoreCase("-ve")){
                    subQuery.append(" AND ss.variance<0");
                }
            }
            if(filter.getForecastDueDate()!=null&&filter.getForecastDueDate().intValue()>=0){
                subQuery.append(" AND (ss.forecastExWorkDate>=:START_FORECAST_DUE_DATE_IN AND ss.forecastExWorkDate<=:END_FORECAST_DUE_DATE_IN)");
            }
            if(filter.getDueIn()!=null){
                subQuery.append(" AND (ss.poDeliveryDate>=:START_DUE_DATE_IN AND ss.poDeliveryDate<=:END_DUE_DATE_IN)");
            }
            if(filter.getDeliveryDateStart()!=null){
                subQuery.append(" AND ss.forecastExWorkDate>=:START_DELIVERY_DATE");
            }
            if(filter.getDeliveryDateEnd()!=null){
                subQuery.append(" AND ss.forecastExWorkDate<=:END_DELIVERY_DATE ");
            }
            subQuery.append(" ) ");
        }
        return subQuery.toString();
    }

    private void prepareValueSubquery(Query query, SearchPurchase filter){
        if(filter.getLeadTime()!=null){
            query.setParameter("LEAD_TIME",filter.getLeadTime()*7);
        }
        if(filter.getForecastDueDate()!=null&&filter.getForecastDueDate().intValue()>=0){
            Date startForecastDueDate=new Date();
            query.setParameter("START_FORECAST_DUE_DATE_IN",startForecastDueDate);
            query.setParameter("END_FORECAST_DUE_DATE_IN",new Util().addDays(startForecastDueDate,filter.getForecastDueDate()*7));
        }
        if(filter.getDueIn()!=null&&filter.getDueIn().intValue()>=0){
            Date startDueInDate=new Date();
            query.setParameter("START_DUE_DATE_IN",startDueInDate);
            query.setParameter("END_DUE_DATE_IN",new Util().addDays(startDueInDate,filter.getDueIn()*7));
        }
        if(filter.getDeliveryDateStart()!=null){
            query.setParameter("START_DELIVERY_DATE",filter.getDeliveryDateStart());
        }
        if(filter.getDeliveryDateEnd()!=null){
            query.setParameter("END_DELIVERY_DATE",filter.getDeliveryDateEnd());
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
