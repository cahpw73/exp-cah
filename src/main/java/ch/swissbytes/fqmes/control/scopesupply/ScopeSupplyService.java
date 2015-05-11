package ch.swissbytes.fqmes.control.scopesupply;

import ch.swissbytes.fqmes.control.Service;
import ch.swissbytes.fqmes.control.tdp.TransitDeliveryPointService;
import ch.swissbytes.fqmes.model.dao.ScopeSupplyDao;
import ch.swissbytes.domain.repository.entities.*;
import ch.swissbytes.domain.repository.types.TimeMeasurementEnum;
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

    @Inject
    private AttachmentScopeSupplyService attachmentService;

    private static final Logger log = Logger.getLogger(ScopeSupplyService.class.getName());

    public ScopeSupplyService() {
        super.initialize(dao);
    }

    public ScopeSupplyEntity load(Long id) {
        return dao.load(id);
    }

    public List<ScopeSupplyEntity> findByPurchaseOrder(final Long purchaseOrderId){
        List<ScopeSupplyEntity> list=dao.findByPurchaseOrder(purchaseOrderId);
        for(ScopeSupplyEntity sse:list){
           sse.getTdpList().addAll(tdpService.findByScopeSupply(sse.getId()));
            sse.getAttachments().addAll(attachmentService.findByScopeSupplyLazy(sse.getId()));
        }
        return list;
    }

    public Date calculateForecastSiteDate(ScopeSupplyEntity scopeSupplyEntity) {
        log.info("calculateForecastSiteDate");
        Date computedDate = null;
        if (scopeSupplyEntity != null) {
            if(tdpService.getActives(scopeSupplyEntity.getTdpList()).size()==0){//there is no any tdp added.
                if(scopeSupplyEntity.getPoDeliveryDate()!=null&&scopeSupplyEntity.getDeliveryLeadTimeMs()!=null&&scopeSupplyEntity.getDeliveryLeadTimeQt()!=null){
                    if(scopeSupplyEntity.getActualExWorkDate()!=null){
                        computedDate= calculateDate(scopeSupplyEntity.getDeliveryLeadTimeMs(),scopeSupplyEntity.getDeliveryLeadTimeQt(),scopeSupplyEntity.getActualExWorkDate());
                    }else if (scopeSupplyEntity.getForecastExWorkDate() != null) {
                        computedDate= calculateDate(scopeSupplyEntity.getDeliveryLeadTimeMs(),scopeSupplyEntity.getDeliveryLeadTimeQt(),scopeSupplyEntity.getForecastExWorkDate());
                    }else if(scopeSupplyEntity.getPoDeliveryDate()!=null){
                        computedDate=calculateDate(scopeSupplyEntity.getDeliveryLeadTimeMs(),scopeSupplyEntity.getDeliveryLeadTimeQt(),scopeSupplyEntity.getPoDeliveryDate());
                    }
                }
            }else {
                //upgrading tdps
                List<TransitDeliveryPointEntity>list=tdpService.getActives(scopeSupplyEntity.getTdpList());
                Date date=calculateForecastDeliveryDateForTdp(scopeSupplyEntity,true, null, list.get(0));
                if(!list.get(0).getIsForecastSiteDateManual()){
                    list.get(0).setForecastDeliveryDate(date);
                }

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
                if(ss.getPoDeliveryDate()!=null){
                    if(ss.getActualExWorkDate()!=null){//delivery date !=null
                        date=calculateDate(tdpCurrent.getMeasurementTime(),tdpCurrent.getLeadTime(),ss.getActualExWorkDate());
                    }else if(ss.getForecastExWorkDate()!=null){//delivery date !=null
                        date=calculateDate(tdpCurrent.getMeasurementTime(),tdpCurrent.getLeadTime(),ss.getForecastExWorkDate());
                    }else if(ss.getPoDeliveryDate()!=null){
                        date=calculateDate(tdpCurrent.getMeasurementTime(),tdpCurrent.getLeadTime(),ss.getPoDeliveryDate());
                    }
                }
            }else{
                if(tdpPrevious.getActualDeliveryDate()!=null){
                    date=calculateDate(tdpCurrent.getMeasurementTime(),tdpCurrent.getLeadTime(),tdpPrevious.getActualDeliveryDate());
                }else if(tdpPrevious.getForecastDeliveryDate()!=null){
                    date=calculateDate(tdpCurrent.getMeasurementTime(),tdpCurrent.getLeadTime(),tdpPrevious.getForecastDeliveryDate());
                }
            }
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
            int index=tdpService.getIndexById(tdp.getId(),tdpService.getActives(ss.getTdpList()));
            for(int i=index+1;i<ss.getTdpList().size();i++){
                TransitDeliveryPointEntity current=tdpService.clone(ss.getTdpList().get(i));
                if(!current.getIsForecastSiteDateManual()){
                    Date d=calculateDate(previous,current);
                    ss.getTdpList().get(i).setForecastDeliveryDate(d);
                }
                previous=tdpService.clone( ss.getTdpList().get(i));
            }
    }
}
