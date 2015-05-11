package ch.swissbytes.fqmes.util;

import ch.swissbytes.domain.types.IncoTermsEnum;
import ch.swissbytes.domain.types.PurchaseOrderStatusEnum;
import ch.swissbytes.domain.types.TimeMeasurementEnum;
import ch.swissbytes.domain.types.UoMEnum;

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
    public Map<String,PurchaseOrderStatusEnum> purchaseOrderStatuses(){
        Map<String,PurchaseOrderStatusEnum> map=new TreeMap<>();
        for(PurchaseOrderStatusEnum poEnum:PurchaseOrderStatusEnum.values()){
            map.put(bundle.getString("postatus." + poEnum.name()), poEnum);
        }
        return map;
    }

}
