package ch.swissbytes.fqmes.util;

import ch.swissbytes.fqmes.types.TimeMeasurementEnum;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.context.SessionScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Named;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * Created by alvaro on 9/30/14.
 */
public class LookupValueFactory implements Serializable {


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



}
