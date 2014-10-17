package ch.swissbytes.fqmes.util;

import ch.swissbytes.fqmes.types.TimeMeasurementEnum;
import sun.util.logging.resources.logging;

import javax.inject.Named;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by alvaro on 9/26/14.
 */
@Named
public class Configuration {

    private ResourceBundle bundle = ResourceBundle.getBundle("messages_en");

    private static final Logger log = Logger.getLogger(Configuration.class.getName());

    private int []sizes={5,10,50,100};

    public String getFormatDate(){
        log.log(Level.FINE,"format date");
        return "dd/MM/yyyy";
    }

    public String getPagesSize(){
        return getPagesSize(0L);
    }

    public String getPagesSize(Long total){
        log.info(String.format("total [%s]",total));
        String sizes="";
        for(int i=0;i<this.sizes.length;i++){
            sizes +=this.sizes[i]+" ";
        }
        if(total>this.sizes[this.sizes.length-1]){
            sizes +=Long.toString(total);
        }
        return sizes.trim();
    }
    public String getDefaultPageSize(){
        return "5";
    }

    public String getTimeMeasurement(TimeMeasurementEnum time){
        log.info(String.format("time measurement [%s]",time));
        return time!=null? bundle.getString("measurement.time."+time.name().toLowerCase()):"";
    }


}
