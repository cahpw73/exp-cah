package ch.swissbytes.fqmes.converter;

import ch.swissbytes.fqmes.util.Configuration;
import ch.swissbytes.fqmes.util.Util;
import org.apache.commons.lang3.StringUtils;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.ConverterException;
import javax.faces.convert.DateTimeConverter;
import javax.inject.Inject;
import javax.inject.Named;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.logging.Level;
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
        //log.info(String.format("getAsObject value [%s]",value));
        if (StringUtils.isNotEmpty(value) && StringUtils.isNotBlank(value)) {
            Date date = parse(value);
            if (date == null) {
                FacesMessage msg =
                        new FacesMessage("Error parsing value");
                msg.setSeverity(FacesMessage.SEVERITY_ERROR);
                throw new ConverterException(msg);
            }
            return Util.convertUTC(date, configuration.getTimeZone());
        }
        return null;
    }

    private Date parse(String value) {
        Date date = null;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(configuration.getFormatDate(), new Locale("en"));
            date = sdf.parse(value);
        } catch (ParseException pe) {
            log.log(Level.SEVERE,"error parsing ["+value+"]");
           // pe.printStackTrace();

        }
        return date;
    }

    public String getAsString(FacesContext context, UIComponent component, Object value) {
        //log.info(String.format("getAsString value[%s]",value));
        Date date = (Date) value;
        return util.toLocal(date);
    }
}
