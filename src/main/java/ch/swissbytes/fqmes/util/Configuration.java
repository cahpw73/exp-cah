package ch.swissbytes.fqmes.util;

import ch.swissbytes.domain.types.ProcurementStatus;
import ch.swissbytes.domain.types.ExpeditingStatusEnum;
import ch.swissbytes.domain.types.TimeMeasurementEnum;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTimeZone;
import org.picketlink.common.util.StringUtil;

import javax.inject.Inject;
import javax.inject.Named;
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

    private int []sizes={5,10,25,50,100};

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

    public String getPOStatus(ExpeditingStatusEnum status){
        return status!=null? bundle.getString("postatus."+status.name().toUpperCase()):"";
    }

    public String getPOProcStatus(ProcurementStatus status){
        return status!=null? bundle.getString("popstatus."+status.name().toUpperCase()):"";
    }

    public Long getTimeOutConversation(){
        return 3600000L;
    }

    public String getPatternDecimal(){
        return "#,##0.00";
    }

    public String getPatternDecimalWithoutComma(){
        return "##0.00";
    }

    public String getPatternIntegerDecimal(){
        return "###.##";
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
        String string = laguangeDefault;
        if(languagePreference!=null) {
            string = StringUtils.isNotEmpty(languagePreference.getLanguage()) ? languagePreference.getLanguage() : laguangeDefault;
        }
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

    public String getFormatDatePlaceHolder(){
        String string = laguangeDefault;
        if(languagePreference!=null) {
            string = StringUtils.isNotEmpty(languagePreference.getLanguage()) ? languagePreference.getLanguage() : laguangeDefault;
        }
        String[] parts = string.split("-");
        Locale locale;
        if(parts.length>1){
            locale=new Locale(parts[0],parts[1]);
        }else{
            locale=new Locale(parts[0],"");
        }
        DateFormat formatter = DateFormat.getDateInstance(DateFormat.MEDIUM, locale);
        String localPattern  = ((SimpleDateFormat)formatter).toPattern();
        return localPattern.toUpperCase();
    }

    public String getHardFormatDate(){
        return "dd MMM yyyy";
    }

    public String getHardFormatDateExportFile(){
        return "dd-MMM-yy";
    }

    public String getHardFormatDateJDECscFile(){
        return "dd/MM/yyyy";
    }

    public String convertDateToExportFile(final Date date) {
        String converted = "";
        if (date != null) {
            DateTimeZone dtz = org.joda.time.DateTimeZone.forID(getTimeZone());
            long utc = dtz.convertUTCToLocal(date.getTime());
            date.setTime(utc);
            converted = new java.text.SimpleDateFormat(getHardFormatDateExportFile(), new Locale("en")).format(date).toUpperCase();
        }
        return converted;
    }

    public String convertDateToExportFileCsv(Date date) {
        String converted = "";
        if (date != null) {
            DateTimeZone dtz = org.joda.time.DateTimeZone.forID(getTimeZone());
            long utc = dtz.convertUTCToLocal(date.getTime());
            date.setTime(utc);
            converted = new java.text.SimpleDateFormat(getHardFormatDateJDECscFile(), new Locale("en")).format(date).toUpperCase();
        }
        return converted;
    }

    public String getFormatDateTime(){
        log.log(Level.FINE,"format date time");
        String string = StringUtils.isNotEmpty(languagePreference.getLanguage())?languagePreference.getLanguage():laguangeDefault;
        String[] parts = string.split("-");
        Locale locale;
        if(parts.length>1){
            locale=new Locale(parts[0],parts[1]);
        }else{
            locale=new Locale(parts[0],"");
        }
        DateFormat formatter = DateFormat.getTimeInstance(DateFormat.DATE_FIELD, locale);
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
        return false;
    }

    public String getTimeZone(){
        if(languagePreference!=null) {
            return StringUtil.isNotNull(languagePreference.getTimeZone()) ? languagePreference.getTimeZone() : TimeZone.getDefault().getID();
        }else{
            return TimeZone.getDefault().getID();
        }
    }

    public TimeZone getTimeZone2(){
        DateTimeZone zone = DateTimeZone.forID(getTimeZone());
        return zone!=null?zone.toTimeZone():TimeZone.getDefault();
    }

    public String getMessage(final String key){
        return  bundle.getString(key);
    }

    public Long getMaxFileSize(){
        return 104857600L;
    }

    public Integer getMaxQuantityFile(){
        return 100;
    }
    public List<Integer> getAvailableWeeks(){
        List<Integer> weeks=new ArrayList<Integer>();
        for(int i=0;i<21;i++){
            weeks.add(i);
        }
        return weeks;
    }
    public List<String>getVarianceOptions(){
        List<String>variances=new ArrayList<>();
        variances.add("all");
        variances.add("+ve");
        variances.add("-ve");
        return variances;
    }

    public String getTitle(){
        return System.getProperties().getProperty("fqm-title-page")==null?bundle.getString("main.title"):System.getProperties().getProperty("fqm-title-page");
    }
    public String getProcurementTitle(){
        return System.getProperties().getProperty("procurement-title-page")==null?bundle.getString("main.title.procurement"):System.getProperties().getProperty("procurement-title-page");
    }
    public String format(String pattern,BigDecimal value){
        return value!=null?new java.text.DecimalFormat(pattern, new java.text.DecimalFormatSymbols()).format(value):"";
    }

    public static void main(String[] args) {
        DecimalFormat format = new DecimalFormat("###.##");

        double[] doubles = {123.45, 99.0, 34343434333323.258, 45.0};
        for(int i=0;i<doubles.length;i++){
            System.out.println(format.format(doubles[i]));
        }
    }
}
