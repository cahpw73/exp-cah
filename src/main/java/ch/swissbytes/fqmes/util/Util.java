package ch.swissbytes.fqmes.util;

import ch.swissbytes.fqmes.model.entities.ScopeSupplyEntity;
import ch.swissbytes.fqmes.model.interfaces.ManageFile;
import org.apache.commons.io.IOUtils;
import org.joda.time.DateTimeZone;
import org.primefaces.model.UploadedFile;

import javax.inject.Inject;
import javax.inject.Named;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by alvaro on 10/31/14.
 */
@Named
public class Util {

    @Inject
    private Configuration configuration;

    private static final Logger log = Logger.getLogger(Util.class.getName());

    public static String cutIfAny(String str, Integer length){
        String target=str;
        if(str!=null){
            target=target.length()>length.intValue()?target.substring(0,length):target;
        }
        return target;
    }

    public static Date convertUTC(Date date, String timeZone){
        DateTimeZone zone = DateTimeZone.forID(timeZone);
        long utc = zone.convertLocalToUTC(date.getTime(), false);
        return new Date(utc);
    }

    public String toLocal(Date date){
        if(date!=null){
            DateTimeZone zone = DateTimeZone.forID(configuration.getTimeZone());
            long local = zone.convertUTCToLocal(date.getTime());
            SimpleDateFormat sdf=new SimpleDateFormat(configuration.getFormatDate(),new Locale("en"));
            return sdf.format(local);
        }
        return null;
    }
    public Date toLocalDate(Date date){
        if(date!=null){
            DateTimeZone zone = DateTimeZone.forID(configuration.getTimeZone());
            long local = zone.convertUTCToLocal(date.getTime());
            return new Date(local);
        }
        return null;
    }

    public String toLocalDateGMT(Date date){
        DateTimeZone dtz= DateTimeZone.getDefault();
        Date utcDate=convertUTC(date,dtz.toTimeZone().getID());
        if(utcDate!=null){
            DateTimeZone zone = DateTimeZone.forID(configuration.getTimeZone());
            long local = zone.convertUTCToLocal(utcDate.getTime());
            //return new Date(local);
            SimpleDateFormat sdf=new SimpleDateFormat(configuration.getFormatDate(),new Locale("en"));
            return sdf.format(new Date(local));
        }
        return "";
    }
    public static String toLocal(Date date,String timeZone,String formatDate){
        DateTimeZone dtz= DateTimeZone.getDefault();
        Date utcDate=convertUTC(date,dtz.toTimeZone().getID());
        if(utcDate!=null){
            DateTimeZone zone = DateTimeZone.forID(timeZone);
            long local = zone.convertUTCToLocal(utcDate.getTime());
            //return new Date(local);
            SimpleDateFormat sdf=new SimpleDateFormat(formatDate,new Locale("en"));
            return sdf.format(new Date(local));
        }
        return "";
    }
    public static String removeSpecialCharactersForJasperReport(String target){
        String value=target!=null?target:"";
        value=value.replaceAll("&\\s+", "&amp; ");
        return value;
    }

    public String calculateVariance(ScopeSupplyEntity scopeSupply){
        if(scopeSupply!=null&&scopeSupply.getRequiredSiteDate()!=null&&scopeSupply.getSiteDate()!=null){
            Calendar with = Calendar.getInstance();
            with.setTime(scopeSupply.getSiteDate());
            Calendar to = Calendar.getInstance();
            to.setTime(scopeSupply.getRequiredSiteDate());
            to.set(Calendar.YEAR, with.get(Calendar.YEAR));
            int withDAY = with.get(Calendar.DAY_OF_YEAR);
            int toDAY = to.get(Calendar.DAY_OF_YEAR);
            int diffDay =  toDAY  - withDAY;
            return Integer.toString(diffDay);
        }
        return "";
    }
    public Integer calculateDifference(Date dateA,Date dateB){
        Integer difference=null;
        if(dateA!=null&&dateB!=null){
            Calendar with = Calendar.getInstance();
            with.setTime(dateB);
            Calendar to = Calendar.getInstance();
            to.setTime(dateA);
            to.set(Calendar.YEAR, with.get(Calendar.YEAR));
            int withDAY = with.get(Calendar.DAY_OF_YEAR);
            int toDAY = to.get(Calendar.DAY_OF_YEAR);
            difference =toDAY  - withDAY;
        }
        return difference;
    }

    public Date addDays(Date date, Integer days){
        Date newDate=null;
        if(date!=null&& days!=null){
            Calendar calendar=Calendar.getInstance();
            calendar.setTime(date);
            calendar.add(Calendar.DAY_OF_YEAR, days);
            newDate=calendar.getTime();
        }
        return newDate;
    }

    public void enterFile(UploadedFile uf,ManageFile entity){
        try{
            entity.setFile(IOUtils.toByteArray(uf.getInputstream()));
            entity.setMimeType(uf.getContentType());
            entity.setFileName(uf.getFileName());
        }catch (IOException ioe){
            log.log(Level.SEVERE,String.format("problems with file ["+uf.getFileName()+"]"));
            log.log(Level.SEVERE,ioe.getMessage());
        }catch (Exception ex){
            log.log(Level.SEVERE,String.format("problems with file ["+uf.getFileName()+"]"));
            log.log(Level.SEVERE,ex.getMessage());
        }finally {
            try{
                uf.getInputstream().close();
            }catch (IOException ioe){
                log.log(Level.SEVERE,ioe.getMessage());
            }
        }
    }

}
