package ch.swissbytes.fqmes.util;

import org.joda.time.DateTimeZone;

import javax.inject.Inject;
import javax.inject.Named;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by alvaro on 10/31/14.
 */
@Named
public class Util {

    @Inject
    private Configuration configuration;

    public static String cutIfAny(String str, Integer length){
        String target=str;
        if(str!=null){
            target=target.length()>length.intValue()?target.substring(0,length):target;
        }
        return target;
    }

    public static Date convertUTC(Date date, String timeZone){
        System.out.println("convertUTC(Date date="+date+", String timeZone="+timeZone+")");
        DateTimeZone zone = DateTimeZone.forID(timeZone);
        long utc = zone.convertLocalToUTC(date.getTime(), false);
        System.out.println("converted "+new Date(utc));
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





}
