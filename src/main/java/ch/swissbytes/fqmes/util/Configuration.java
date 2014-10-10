package ch.swissbytes.fqmes.util;

import ch.swissbytes.fqmes.types.TimeMeasurementEnum;

import javax.inject.Named;
import java.util.ResourceBundle;

/**
 * Created by alvaro on 9/26/14.
 */
@Named
public class Configuration {

    ResourceBundle bundle = ResourceBundle.getBundle("messages_en");

    public String getFormatDate(){
        return "dd/MM/yyyy";
    }

    public String getTimeMeasurement(TimeMeasurementEnum time){
        return bundle.getString("measurement.time."+time.name().toLowerCase());
    }


}
