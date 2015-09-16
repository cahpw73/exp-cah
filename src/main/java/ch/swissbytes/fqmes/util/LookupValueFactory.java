package ch.swissbytes.fqmes.util;

import ch.swissbytes.domain.types.*;

import javax.enterprise.context.SessionScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Named;
import java.io.Serializable;
import java.util.*;
import java.util.logging.Logger;

/**
 * Created by alvaro on 9/30/14.
 */
public class LookupValueFactory implements Serializable {

    private static final Logger log= Logger.getLogger(LookupValueFactory.class.getName());

    ResourceBundle bundle = ResourceBundle.getBundle("messages_en");

    @Produces
    @SessionScoped
    @Named("allTimeMeasurement")
    public Map<String,TimeMeasurementEnum> getAllTimeMeasurement(){
        Map<String,TimeMeasurementEnum> result = new HashMap<>();
        for(TimeMeasurementEnum measurement : TimeMeasurementEnum.values()){
            result.put(bundle.getString("measurement.time."+measurement.name().toLowerCase()),measurement);
        }
        return result;
    }


    public Map<Integer,String> geTimesMeasurement(){
        Map<Integer,String> result = new HashMap<>();
        for(TimeMeasurementEnum measurement : TimeMeasurementEnum.values()){
            result.put(measurement.ordinal(),bundle.getString("measurement.time."+measurement.name().toLowerCase()));
        }
        return result;
    }

    @Produces
    @SessionScoped
    @Named("unitOfMeasurement")
    public Map<String,UoMEnum> getUnitOfMeasurement(){
        Map<String,UoMEnum> result = new HashMap<>();
        for(UoMEnum measurement : UoMEnum.values()){
            result.put(bundle.getString("uom."+measurement.name().toLowerCase()),measurement);
        }
        return result;
    }

    @Produces
    @SessionScoped
    @Named("incoTerms")
    public Map<String,IncoTermsEnum> getIncoTerms(){
        Map<String,IncoTermsEnum> result = new HashMap<>();
        for(IncoTermsEnum incoTerms : IncoTermsEnum.values()){
            result.put(incoTerms.name(),incoTerms);
        }
        return result;
    }

    @Produces
    @SessionScoped
    @Named("poStatuses")
    public Map<String,ExpeditingStatusEnum> purchaseOrderStatuses(){
        Map<String,ExpeditingStatusEnum> map=new TreeMap<>();
        for(ExpeditingStatusEnum poEnum: ExpeditingStatusEnum.values()){
            map.put(bundle.getString("postatus." + poEnum.name()), poEnum);
        }
        return map;
    }

    @Produces
    @SessionScoped
    @Named("procPoStatuses")
    public Map<String,ProcurementStatus> poStatuses(){
        Map<String,ProcurementStatus> map=new TreeMap<>();
        for(ProcurementStatus poEnum: ProcurementStatus.values()){
            map.put(bundle.getString("popstatus." + poEnum.name()), poEnum);
        }
        return map;
    }

    @Produces
    @SessionScoped
    @Named("classes")
    public Map<String,ClassEnum> classes(){
        Map<String,ClassEnum> map=new TreeMap<>();
        for(ClassEnum poEnum:ClassEnum.values()){
            map.put(bundle.getString("class." + poEnum.name()), poEnum);
        }
        return map;
    }

    public Map<Integer,String> getStatusPOProcurement(){
        Map<Integer,String> result = new HashMap<>();
        for(ProcurementStatus procurementStatus : ProcurementStatus.values()){
            result.put(procurementStatus.ordinal(),bundle.getString("popstatus." + procurementStatus.name().toUpperCase()));
        }
        return result;
    }

}
