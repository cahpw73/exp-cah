package ch.swissbytes.fqmes.control.scopesupply;

import ch.swissbytes.fqmes.control.Service;
import ch.swissbytes.fqmes.control.tdp.TransitDeliveryPointService;
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

    @Inject
    private TransitDeliveryPointService tdpService;

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
            if(tdpService.getActives(scopeSupplyEntity.getTdpList()).size()==0){//there is no any tdp added.
                if(scopeSupplyEntity.getDeliveryDate()!=null&&scopeSupplyEntity.getDeliveryLeadTimeMs()!=null&&scopeSupplyEntity.getDeliveryLeadTimeQt()!=null){
                    if(scopeSupplyEntity.getActualExWorkDate()!=null){
                        computedDate= calculateDate(scopeSupplyEntity.getDeliveryLeadTimeMs(),scopeSupplyEntity.getDeliveryLeadTimeQt(),scopeSupplyEntity.getActualExWorkDate());
                    }else if (scopeSupplyEntity.getExWorkDate() != null) {
                        computedDate= calculateDate(scopeSupplyEntity.getDeliveryLeadTimeMs(),scopeSupplyEntity.getDeliveryLeadTimeQt(),scopeSupplyEntity.getExWorkDate());
                    }else if(scopeSupplyEntity.getDeliveryDate()!=null){
                        computedDate=calculateDate(scopeSupplyEntity.getDeliveryLeadTimeMs(),scopeSupplyEntity.getDeliveryLeadTimeQt(),scopeSupplyEntity.getDeliveryDate());
                    }
                }
            }else {
                List<TransitDeliveryPointEntity>tdpList=tdpService.getActives(scopeSupplyEntity.getTdpList());
                TransitDeliveryPointEntity lastTdp=tdpList.get(tdpList.size()-1);
                if(lastTdp.getActualDeliveryDate()!=null){
                    computedDate=calculateDate(scopeSupplyEntity.getDeliveryLeadTimeMs(),scopeSupplyEntity.getDeliveryLeadTimeQt(),lastTdp.getActualDeliveryDate());
                }else if(lastTdp.getForecastDeliveryDate()!=null){
                    computedDate=calculateDate(scopeSupplyEntity.getDeliveryLeadTimeMs(),scopeSupplyEntity.getDeliveryLeadTimeQt(),lastTdp.getForecastDeliveryDate());
                }
            }
        }
        log.info("computeDate "+computedDate);
        return computedDate;
    }
    private Date calculateDate(TimeMeasurementEnum measurementEnum,Integer quantity,Date date){
        log.info("calculateDate(TimeMeasurementEnum measurementEnum="+measurementEnum+",Integer quantity="+quantity+",Date date="+date+")");
        Date computedDate=null;
        if(measurementEnum!=null&&quantity!=null){
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
        }
        return computedDate;
    }

    public Date calculateForecastDeliveryDateForTdp(ScopeSupplyEntity ss, boolean isFirstTdp, TransitDeliveryPointEntity tdpPrevious, TransitDeliveryPointEntity tdpCurrent){
        log.info("public Date calculateForecastDeliveryDateForTdp(ScopeSupplyEntity )");
        Date date=null;
            if(isFirstTdp){
                log.info("calculating for first tdp");
                if(ss.getDeliveryDate()!=null){
                    if(ss.getActualExWorkDate()!=null){//delivery date !=null
                        date=calculateDate(tdpCurrent.getMeasurementTime(),tdpCurrent.getLeadTime(),ss.getActualExWorkDate());
                    }else if(ss.getExWorkDate()!=null){//delivery date !=null
                        date=calculateDate(tdpCurrent.getMeasurementTime(),tdpCurrent.getLeadTime(),ss.getExWorkDate());
                    }else if(ss.getDeliveryDate()!=null){
                        date=calculateDate(tdpCurrent.getMeasurementTime(),tdpCurrent.getLeadTime(),ss.getDeliveryDate());
                    }
                }
            }else{
                if(tdpPrevious.getActualDeliveryDate()!=null){
                    date=calculateDate(tdpCurrent.getMeasurementTime(),tdpCurrent.getLeadTime(),tdpPrevious.getActualDeliveryDate());
                }else if(tdpPrevious.getForecastDeliveryDate()!=null){
                    date=calculateDate(tdpCurrent.getMeasurementTime(),tdpCurrent.getLeadTime(),tdpPrevious.getForecastDeliveryDate());
                }
            }
        TransitDeliveryPointEntity tdpe= tdpService.clone(tdpCurrent);
        tdpe.setForecastDeliveryDate(date);
        doUpdateTdpPost(ss,tdpCurrent,tdpe);
        log.info(String.format("Date calculated [%s]",date));
        return date;
    }

    private Date calculateDate(TransitDeliveryPointEntity tdpPrevious, TransitDeliveryPointEntity tdpCurrent){
        Date date=null;
        if(tdpPrevious.getActualDeliveryDate()!=null){
            date=calculateDate(tdpCurrent.getMeasurementTime(),tdpCurrent.getLeadTime(),tdpPrevious.getActualDeliveryDate());
        }else if(tdpPrevious.getForecastDeliveryDate()!=null){
            date=calculateDate(tdpCurrent.getMeasurementTime(),tdpCurrent.getLeadTime(),tdpPrevious.getForecastDeliveryDate());
        }
        return date;
    }
    private void doUpdateTdpPost(ScopeSupplyEntity ss, TransitDeliveryPointEntity tdp,TransitDeliveryPointEntity previous){
        if(tdp.getId()!=null){
            int index=tdpService.getIndexById(tdp.getId(),tdpService.getActives(ss.getTdpList()));
            for(int i=index+1;i<ss.getTdpList().size();i++){
                TransitDeliveryPointEntity tdp1=tdpService.clone(ss.getTdpList().get(i));
                if(ss.getTdpList().get(i).getIsForecastSiteDateCalculated()){
                    ss.getTdpList().get(i).setForecastDeliveryDate(calculateDate(previous,ss.getTdpList().get(i)));
                }
                previous=tdp1;
            }
        }
    }
}
