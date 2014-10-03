package ch.swissbytes.fqmes.util;

import javax.inject.Named;

/**
 * Created by alvaro on 9/26/14.
 */
@Named
public class Configuration {

    public String getFormatDate(){
        return "dd/MM/yyyy";
    }

}
