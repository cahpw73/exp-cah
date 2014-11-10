package ch.swissbytes.fqmes.util;

import ch.swissbytes.fqmes.types.TimeMeasurementEnum;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTimeZone;
import org.picketlink.common.util.StringUtil;
import sun.util.logging.resources.logging;

import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.math.BigDecimal;
import java.text.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by alvaro on 9/26/14.
 */
@Named
public class Configuration implements Serializable {

    @Inject
    private LanguagePreference languagePreference;

    private ResourceBundle bundle = ResourceBundle.getBundle("messages_en");

    private static final Logger log = Logger.getLogger(Configuration.class.getName());

    private int []sizes={5,10,50,100};

    private final String laguangeDefault = "en-AU";

    public String getPagesSize(){
        return getPagesSize(0L);
    }


    public String getPagesSize(Long total){
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
        return time!=null? bundle.getString("measurement.time."+time.name().toLowerCase()):"";
    }

    public Long getTimeOutConversation(){
        return 3600000L;
    }

    public String getPatternDecimal(){
        return "#,##0.00";
    }

    public String getLocale(){
        return "en_AU";
    }

    public String getLangLocalDecimal() {
        return StringUtils.isNotEmpty(languagePreference.getLanguage())?languagePreference.getLanguage():laguangeDefault;
    }

    public String getLangLocalCalendar(){
        String pattern = laguangeDefault;
        if(StringUtils.isNotEmpty(languagePreference.getLanguage()) && StringUtils.isNotBlank(languagePreference.getLanguage())){
            String string = StringUtils.isNotEmpty(languagePreference.getLanguage())?languagePreference.getLanguage():laguangeDefault;;
            String[] parts = string.split("-");
            return parts.length > 1 ? parts[0] : languagePreference.getLanguage();
        }
       return pattern;
    }

    public String getFormatDate(){
        log.log(Level.FINE,"format date");
        String string = StringUtils.isNotEmpty(languagePreference.getLanguage())?languagePreference.getLanguage():laguangeDefault;;
        String[] parts = string.split("-");
        Locale locale;
        if(parts.length>1){
            locale=new Locale(parts[0],parts[1]);
        }else{
            locale=new Locale(parts[0],"");
        }
        DateFormat formatter = DateFormat.getDateInstance(DateFormat.MEDIUM, locale);
        String localPattern  = ((SimpleDateFormat)formatter).toPattern();
        return localPattern;
    }

    public String getLanguage(){
        String string = StringUtils.isNotEmpty(languagePreference.getLanguage())?languagePreference.getLanguage():laguangeDefault;;
        String[] parts = string.split("-");
        return parts!=null&&parts.length>0?parts[0]:"en";
    }
    public String getCountry(){
        String string = StringUtils.isNotEmpty(languagePreference.getLanguage())?languagePreference.getLanguage():laguangeDefault;;
        String[] parts = string.split("-");
        return parts!=null&&parts.length>1?parts[1]:"AU";
    }

    public Boolean getMask(){
        FacesContext facesContext = FacesContext.getCurrentInstance();
        HttpServletRequest request=(HttpServletRequest)facesContext.getExternalContext().getRequest();

        return false;
    }

    public String getTimeZone(){
        return StringUtil.isNotNull(languagePreference.getTimeZone())?languagePreference.getTimeZone(): TimeZone.getDefault().getID();
    }

    public TimeZone getTimeZone2(){
        DateTimeZone zone = DateTimeZone.forID(getTimeZone());
        return zone!=null?zone.toTimeZone():TimeZone.getDefault();
    }

    public String getMessage(final String key){
        return  bundle.getString(key);
    }

}
