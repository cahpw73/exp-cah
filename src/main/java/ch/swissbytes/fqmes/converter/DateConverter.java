package ch.swissbytes.fqmes.converter;

import ch.swissbytes.fqmes.util.Configuration;
import ch.swissbytes.fqmes.util.Util;
import org.joda.time.DateTimeZone;

import javax.faces.component.FacesComponent;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.DateTimeConverter;
import javax.inject.Inject;
import javax.inject.Named;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.logging.Logger;

/**
 * Created by Alvaro on 3/11/14.
 */
@Named("dateConverter")
public class DateConverter extends DateTimeConverter {

    private static final Logger log = Logger.getLogger(DateConverter.class.getName());

    @Inject
    private Configuration configuration;

    @Inject
    private Util util;

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        log.info(String.format("getAsObject value [%s]",value));
        SimpleDateFormat sdf=new SimpleDateFormat(configuration.getFormatDate(),new Locale("en"));
        Date date=null;
        try{
            date=sdf.parse(value);
        }catch(ParseException pe){
            pe.printStackTrace();
            return null;
        }
        return Util.convertUTC(date,configuration.getTimeZone());
    }

    public String getAsString(FacesContext context, UIComponent component, Object value) {
        log.info(String.format("getAsString value[%s]",value));
        Date date=(Date)value;
        return util.toLocal(date);
    }
}
