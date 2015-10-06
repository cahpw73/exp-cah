package ch.swissbytes.Service.business.scopesupply;

import ch.swissbytes.Service.business.AttachmentScopeSupply.AttachmentScopeSupplyService;
import ch.swissbytes.Service.business.Service;
import ch.swissbytes.Service.business.tdp.TransitDeliveryPointService;
import ch.swissbytes.domain.model.entities.*;
import ch.swissbytes.domain.types.TimeMeasurementEnum;
import org.apache.commons.lang3.time.DateUtils;

import javax.inject.Inject;
import javax.transaction.Transactional;
import java.io.Serializable;
import java.util.ArrayList;
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

    public List<ScopeSupplyEntity> scopeSupplyListByPOId(final Long purchaseOrderId){
        return dao.findByPurchaseOrder(purchaseOrderId);
    }

    public List<ScopeSupplyEntity> scopeSupplyListByPOOId(final Long purchaseOrderId){
        return dao.findByPOOId(purchaseOrderId);
    }

    public List<ProjectCurrencyEntity>findCurrenciesBy(final Long purchaseOrderId){
        List<ScopeSupplyEntity>list=scopeSupplyListByPOId(purchaseOrderId);
        List<ProjectCurrencyEntity>currencies=new ArrayList<>();
        for(ScopeSupplyEntity scopeSupplyEntity:list){
            if(!currencies.contains(scopeSupplyEntity.getProjectCurrency())) {
                currencies.add(scopeSupplyEntity.getProjectCurrency());
        }
        }
        return currencies;
    }

    @Transactional
    public void doUpdate(ScopeSupplyEntity scopeSupply){
        dao.doUpdate(scopeSupply);
    }
}
