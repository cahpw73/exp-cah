package ch.swissbytes.fqmes.control.scopesupply;

import ch.swissbytes.fqmes.control.Service;
import ch.swissbytes.fqmes.model.dao.ScopeSupplyDao;
import ch.swissbytes.fqmes.model.entities.*;
import ch.swissbytes.fqmes.types.TimeMeasurementEnum;
import org.apache.commons.lang3.time.DateUtils;

import javax.inject.Inject;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

/**
 * Created by alvaro on 9/22/14.
 */
public class ScopeSupplyService extends Service<ScopeSupplyEntity> implements Serializable{

    @Inject
     private ScopeSupplyDao dao;

    private static final Logger log = Logger.getLogger(ScopeSupplyService.class.getName());

    public ScopeSupplyService() {
        super.initialize(dao);
    }


    public ScopeSupplyEntity load(Long id) {
        return dao.load(id);
    }

    public List<ScopeSupplyEntity> findByPurchaseOrder(final Long purchaseOrderId){
        return dao.findByPurchaseOrder(purchaseOrderId);
    }

    public Date calculateForecastSiteDate(ScopeSupplyEntity scopeSupplyEntity) {
        log.info("calculateForecastSiteDate");
        Date computedDate = null;
        if (scopeSupplyEntity != null) {
            if(scopeSupplyEntity.getActualExWorkDate()!=null){
                computedDate= calculateDate(scopeSupplyEntity.getDeliveryLeadTimeMs(),scopeSupplyEntity.getDeliveryLeadTimeQt(),scopeSupplyEntity.getActualExWorkDate());
            }else if (scopeSupplyEntity.getExWorkDate() != null) {
                computedDate= calculateDate(scopeSupplyEntity.getDeliveryLeadTimeMs(),scopeSupplyEntity.getDeliveryLeadTimeQt(),scopeSupplyEntity.getExWorkDate());
            }else if(scopeSupplyEntity.getDeliveryDate()!=null){
                computedDate=calculateDate(scopeSupplyEntity.getDeliveryLeadTimeMs(),scopeSupplyEntity.getDeliveryLeadTimeQt(),scopeSupplyEntity.getDeliveryDate());
            }
        }
        return computedDate;
    }
    private Date calculateDate(TimeMeasurementEnum measurementEnum,Integer quantity,Date date){
        log.info("calculateDate");
        Date computedDate=null;
        if(measurementEnum!=null&& quantity!=null){
            switch (measurementEnum){
                case DAY:
                    computedDate= DateUtils.addDays(date,quantity);
                    break;
                case WEEK:
                    computedDate=DateUtils.addDays(date,quantity*7);
                    break;
                case MONTH:
                    computedDate=DateUtils.addMonths(date,quantity);
                    break;
                case YEAR:
                    computedDate=DateUtils.addYears(date,quantity);
                    break;
            }
        }
        return computedDate;
    }
}
