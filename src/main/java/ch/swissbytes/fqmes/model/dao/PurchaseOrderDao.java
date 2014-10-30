package ch.swissbytes.fqmes.model.dao;

import ch.swissbytes.fqmes.boundary.purchase.SearchPurchase;
import ch.swissbytes.fqmes.model.Filter;
import ch.swissbytes.fqmes.model.entities.PurchaseOrderEntity;
import ch.swissbytes.fqmes.types.StatusEnum;
import org.apache.commons.lang3.StringUtils;

import javax.persistence.Query;
import java.io.Serializable;
import java.util.logging.Logger;

/**
 * Created by alvaro on 9/8/14.
 */

public class PurchaseOrderDao extends GenericDao<PurchaseOrderEntity> implements Serializable{

    private static final Logger log = Logger.getLogger(PurchaseOrderDao.class.getName());


    public PurchaseOrderEntity load(final Long id){
        return super.load(PurchaseOrderEntity.class,id);
    }
    @Override
    protected  void applyCriteriaValues(Query query,Filter f){
        log.info("protected  void applyCriteriaValues(Query query,Filter f)");
        query.setParameter("DELETED", StatusEnum.DELETED.getId());
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
        }else{
            log.info("filter is null");
        }
    }

    protected String getEntity(){
        return PurchaseOrderEntity.class.getSimpleName();
    }

    @Override
    protected  String addCriteria(Filter f){
        log.info("protected  String addCriteria(Filter f)");
        StringBuilder sb=new StringBuilder();
        sb.append(" AND x.status.id<>:DELETED ");
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
        }else{
            log.info("filter is null");
        }
        return sb.toString();
    }

    @Override
    public String orderBy(String field,boolean ascending){
        return "ORDER BY "+field;
    }

}
