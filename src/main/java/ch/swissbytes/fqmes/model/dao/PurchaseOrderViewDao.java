package ch.swissbytes.fqmes.model.dao;

import ch.swissbytes.fqmes.boundary.purchase.SearchPurchase;
import ch.swissbytes.fqmes.model.Filter;
import ch.swissbytes.fqmes.model.entities.PurchaseOrderEntity;
import ch.swissbytes.fqmes.model.entities.VPurchaseOrder;
import ch.swissbytes.fqmes.types.StatusEnum;
import org.apache.commons.lang3.StringUtils;

import javax.persistence.Query;
import java.io.Serializable;
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
            System.out.println("INCOTERM 1 ["+filter.getIncoTerm()+"]");
            if(StringUtils.isNotEmpty(filter.getIncoTerm())&&StringUtils.isNotBlank(filter.getIncoTerm())){
                System.out.println("entering 1");
                query.setParameter("INCO_TERM","%"+filter.getIncoTerm().trim()+"%");
            }
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
            System.out.println("INCOTERM  2 ["+filter.getIncoTerm()+"]");
            System.out.println("IS NOT EMPTY  2 ["+StringUtils.isNotEmpty(filter.getIncoTerm())+"]");
            System.out.println("IS NOT BLANK  2 ["+StringUtils.isNotBlank(filter.getIncoTerm())+"]");
            if(StringUtils.isNotEmpty(filter.getIncoTerm())&&StringUtils.isNotBlank(filter.getIncoTerm())){
                System.out.println("entering 2...");
                sb.append(" AND lower(x.incoTerm) like lower(:INCO_TERM)");
            }
        }else{
            log.info("filter is null");
        }
        return sb.toString();
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
