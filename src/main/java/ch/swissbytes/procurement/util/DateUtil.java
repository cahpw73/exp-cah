package ch.swissbytes.procurement.util;

import org.joda.time.DateTime;
import org.joda.time.Days;

import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * An utility class that provides methods for {@link java.util.Date} handling
 *
 * @author rory.sandoval
 */
public class DateUtil {

    /**
     * A {@link java.text.SimpleDateFormat} with the pattern <b>dd/MM/yyyy</b>. Example: "22/01/1982"
     */
    public static final SimpleDateFormat SDF = new SimpleDateFormat("dd/MM/yyyy");

    /**
     * A {@link java.text.SimpleDateFormat} with the pattern <b>dd/MM/yyyy HH:mm</b>. Example: "22/01/1982 11:25"
     */
    public static final SimpleDateFormat SDF_DETAIL = new SimpleDateFormat("dd/MM/yyyy HH:mm");

    /**
     * A {@link java.text.SimpleDateFormat} with the pattern <b>dd MMM yyyy</b>. Example: "22 Ene 1982"
     */
    public static final SimpleDateFormat SDF_DESC = new SimpleDateFormat("dd MMM yyyy");

    /**
     * A {@link java.text.SimpleDateFormat} with the pattern <b>yyyyMMdd</b>. Example: "19820122"
     */
    public static final SimpleDateFormat SDF_INTRA = new SimpleDateFormat("yyyyMMdd");

    /**
     * A {@link java.text.SimpleDateFormat} with the pattern <b>yyyyMMddHHmmss</b>. Example: "19820122112530"
     */
    public static final SimpleDateFormat SDF_INTRA_LONG = new SimpleDateFormat("yyyyMMddHHmmss");

    public static final int MAX_HOURS_IN_DAY = 23;

    public static final int MAX_MINUTES_IN_HOUR = 59;

    public static final int MAX_SECONDS_IN_MINUTE = 59;

    public static final int MAX_MILLIS_IN_SECOND = 999;

    /**
     *
     */
    public DateUtil() {
    }


    /**
     * @param dateString
     * @param pattern
     * @return
     * @author Timoteo Ponce
     */
    public static Date parseDate(final String dateString, final String pattern) {
        Date myDate = null;
        if (org.apache.commons.lang.StringUtils.isNotEmpty(dateString)) {
            try {
                myDate = org.apache.commons.lang.time.DateUtils.parseDate(dateString, new String[] { pattern });
            } catch (final ParseException e) {
               /// Logger.getLogger(DateUtil.class).warn("Invalid Date...! " + dateString + " with format " + pattern);
            }
        }
        return myDate;
    }

    /**
     * Returns first date of current week, but not lower than current month's first date
     *
     * @return first date of current week in current month
     */
    public static Date getFirstDayOfCurrentWeek() {
        Calendar c = Calendar.getInstance();
        int today = c.get(Calendar.DAY_OF_MONTH);

        c.setFirstDayOfWeek(Calendar.MONDAY);
        c.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);

        if (c.get(Calendar.DAY_OF_MONTH) > today) {
            return getFirstDayOfCurrentMonth();
        } else {
            return c.getTime();
        }
    }

    /**
     * Returns last date of current week, but not greater than current month's last date
     *
     * @return last date of current week in current month
     */
    public static Date getLastDayOfCurrentWeek() {
        Calendar c = Calendar.getInstance();
        int today = c.get(Calendar.DAY_OF_MONTH);

        c.setFirstDayOfWeek(Calendar.MONDAY);
        c.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);

        if (c.get(Calendar.DAY_OF_MONTH) < today) {
            return getLastDayOfCurrentMonth();
        } else {
            return c.getTime();
        }
    }

    /**
     * @return first date of current month
     */
    public static Date getFirstDayOfCurrentMonth() {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.DAY_OF_MONTH, 1);
        c.setFirstDayOfWeek(Calendar.MONDAY);
        return c.getTime();
    }

    /**
     * @return last date of current month
     */
    public static Date getLastDayOfCurrentMonth() {
        Calendar c = Calendar.getInstance();
        c.setFirstDayOfWeek(Calendar.MONDAY);
        c.set(Calendar.DAY_OF_MONTH, c.getActualMaximum(Calendar.DAY_OF_MONTH));
        return c.getTime();
    }

    /**
     * Sets zero hour to a date. It is useful when you need to set the start of a date range.
     *
     * @param aDate
     * @return aDate with time 00:00:00-000
     */
    public static Date setZeroHour (Date aDate) {
        Calendar c = Calendar.getInstance();
        c.setTime(aDate);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        return c.getTime();
    }

    /**
     * Sets 24 hour to a date. It is useful when you need to set the end of a date range.
     *
     * @param aDate
     * @return aDate with time 23:59:59-999
     */
    public static Date set24Hour (Date aDate) {
        Calendar c = Calendar.getInstance();
        c.setTime(aDate);
        c.set(Calendar.HOUR_OF_DAY, 23);
        c.set(Calendar.MINUTE, 59);
        c.set(Calendar.SECOND, 59);
        c.set(Calendar.MILLISECOND, 999);
        return c.getTime();
    }

    /**
     * @param year  running year of requested month
     * @param month requested month, 0 based
     * @return first date of given month
     */
    public static Date getFirstDayOfAMonth(int year, int month) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month);
        c.set(Calendar.DAY_OF_MONTH, 1);
        c.setFirstDayOfWeek(Calendar.MONDAY);
        return c.getTime();
    }

    /**
     * @param year  running year of requested month
     * @param month requested month, 0 based
     * @return last date of given month
     */
    public static Date getLastDayOfAMonth(int year, int month) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month);
        c.setFirstDayOfWeek(Calendar.MONDAY);
        c.set(Calendar.DAY_OF_MONTH, c.getActualMaximum(Calendar.DAY_OF_MONTH));
        return c.getTime();
    }

    /**
     * @param aDate
     * @return Integer representing a date in format yyyyMMdd
     */
    public static Integer getIntraplatinumFormat(Date aDate) {
        System.out.println("getIntraplatinumFormat - aDate: " + aDate);
        String formatted = SDF_INTRA.format(aDate);
        System.out.println("getIntraplatinumFormat - formatted: " + formatted);
        return Integer.valueOf(formatted);
    }

    /**
     * Converts intraplatinum formatted date to Date object
     *
     * @param dateAsLong, an int date in format yyyyMMddHHmmss
     * @return
     */
    public static Date parseIntraLongToDate(Long dateAsLong) {
        System.out.println("parseIntraLongToDate - dateAsLong: " + dateAsLong);

        try {
            return SDF_INTRA_LONG.parse(dateAsLong.toString());
        } catch (ParseException e) {
           // LOGGER.error(e.getMessage(), e);
            return null;
        }
    }

    /**
     * Converts intraplatinum formatted date to Date object
     *
     * @param intDate, an int date in format yyyyMMdd
     * @return
     */
    public static Date parseIntraToDate(Integer intDate) {
//        System.out.println("parseIntraToDate - intDate: " + intDate);

        try {
            return SDF_INTRA.parse(intDate.toString());
        } catch (ParseException e) {
            //LOGGER.error(e.getMessage(), e);
            return null;
        }
    }


    /**
     * @param date date with we will work
     * @param days number of days that we want to add
     * @return date added days
     */
    public static Date getDateAddDays(Date date, int days) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.DATE, days);
        date = c.getTime();
        return date;
    }

    public static Date getFirstDayOfPreviousMonth() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MONTH,-1);
        cal.set(cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.getActualMinimum(Calendar.DAY_OF_MONTH),
                cal.getMinimum(Calendar.HOUR_OF_DAY),
                cal.getMinimum(Calendar.MINUTE),
                cal.getMinimum(Calendar.SECOND));
        return cal.getTime();
    }

    public static Date getLastDayOfPreviousMonth() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MONTH,-1);
        cal.set(cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.getActualMaximum(Calendar.DAY_OF_MONTH),
                cal.getMaximum(Calendar.HOUR_OF_DAY),
                cal.getMaximum(Calendar.MINUTE),
                cal.getMaximum(Calendar.SECOND));
        return cal.getTime();
    }

    public static Date getLastDayOfPrevious2Month() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MONTH,-2);
        cal.set(cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.getActualMinimum(Calendar.DAY_OF_MONTH),
                cal.getMinimum(Calendar.HOUR_OF_DAY),
                cal.getMinimum(Calendar.MINUTE),
                cal.getMinimum(Calendar.SECOND));
        return cal.getTime();
    }

    public static Date getPreviousDay(Integer previousDay) {
        Date date = null;
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -1 * previousDay);
        date = calendar.getTime();
        Date result = null;
        if (date != null) {
            DateTime dateTime = new DateTime(date);
            dateTime = dateTime.withTime(0, 0, 0, 0);
            result = dateTime.toDate();
        }
        return result;
    }

    public static Date getMinHourOfPreviousDay() {
        Date date = null;
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -1);
        date = calendar.getTime();
        Date result = null;
        if (date != null) {
            DateTime dateTime = new DateTime(date);
            dateTime = dateTime.withTime(0, 0, 0, 0);
            result = dateTime.toDate();
        }
        return result;
    }

    public static Date getMaxHourOfPreviousDay() {
        Date date = null;
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -1);
        date = calendar.getTime();
        Date result = null;
        if (date != null) {
            DateTime dateTime = new DateTime(date);
            dateTime = dateTime.withTime(MAX_HOURS_IN_DAY, MAX_MINUTES_IN_HOUR, MAX_SECONDS_IN_MINUTE, MAX_MILLIS_IN_SECOND);

            result = dateTime.toDate();
        }
        return result;
    }

    public static Date getFirstDayOfPreviousWeek(){
        Date date = null;
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -7);
        date = calendar.getTime();
        Date result = null;
        if (date != null) {
            DateTime dateTime = new DateTime(date);
            dateTime = dateTime.withTime(0, 0, 0, 0);
            result = dateTime.toDate();
        }
        return result;
    }

    public static Date getLastDayOfPreviousWeek(){
        Date date = null;
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -1);
        date = calendar.getTime();
        Date result = null;
        if (date != null) {
            DateTime dateTime = new DateTime(date);
            dateTime = dateTime.withTime(MAX_HOURS_IN_DAY, MAX_MINUTES_IN_HOUR, MAX_SECONDS_IN_MINUTE, MAX_MILLIS_IN_SECOND);
            result = dateTime.toDate();
        }
        return result;
    }

    public static Date getFirstDateDefault(){
        Date  fromDate = new Date();
        Calendar c = Calendar.getInstance();
        c.setTime(fromDate);
        c.set(Calendar.MONTH,0);
        c.set(Calendar.DAY_OF_MONTH,1);
        c.set(Calendar.YEAR, 1900);
        fromDate = c.getTime();
        Date result = null;
        if (fromDate != null) {
            DateTime dateTime = new DateTime(fromDate);
            dateTime = dateTime.withTime(0, 0, 0, 0);
            result = dateTime.toDate();
        }
        return result;
    }

    public static Date getLastDateDefault(){
        Date  fromDate = new Date();
        Calendar c = Calendar.getInstance();
        c.setTime(fromDate);
        c.set(Calendar.MONTH,11);
        c.set(Calendar.DAY_OF_MONTH,30);
        c.set(Calendar.YEAR, 2200);
        fromDate = c.getTime();
        Date result = null;
        if(fromDate != null){
            DateTime dateTime = new DateTime(fromDate);
            dateTime = dateTime.withTime(MAX_HOURS_IN_DAY, MAX_MINUTES_IN_HOUR, MAX_SECONDS_IN_MINUTE, MAX_MILLIS_IN_SECOND);
            result = dateTime.toDate();
        }
        return result;
    }

    public static Date getDateMaxHour(Date date){
        Date result = null;
        if(date != null){
            DateTime dateTime = new DateTime(date);
            dateTime = dateTime.withTime(MAX_HOURS_IN_DAY, MAX_MINUTES_IN_HOUR, MAX_SECONDS_IN_MINUTE, MAX_MILLIS_IN_SECOND);
            result = dateTime.toDate();
        }
        return result;
    }

    public static Date getDateMinHour(Date date){
        Date result = null;
        if(date != null){
            DateTime dateTime = new DateTime(date);
            dateTime = dateTime.withTime(0, 0, 0, 0);
            result = dateTime.toDate();
        }
        return result;
    }

    public static String getDateIsoFormat(Date date){
        TimeZone tz = TimeZone.getTimeZone("UTC");
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'");
        df.setTimeZone(tz);
        String dateAsISO = df.format(date);
        return  dateAsISO;
    }

    public static Date getStringDateIsoToDate(String dateIso){
        try {
            TimeZone tz = TimeZone.getTimeZone("UTC");
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'");

            df.setTimeZone(tz);
            Date date = df.parse(dateIso);

            return date;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Format use dd/MM/yyyy
     * @param date
     * @return
     */
    public static String getStringToDateFormatted(Date date){
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        String dateConverted = dateFormat.format(date);
        return dateConverted;
    }

    public static Date sumNDays(Date date, int n){
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.DAY_OF_YEAR, n);
        return c.getTime();
    }


    /**************************/

    public static Date getNextNMoth(int moths) {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MONTH,moths);
        cal.set(cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.getActualMinimum(Calendar.DAY_OF_MONTH),
                cal.getMinimum(Calendar.HOUR_OF_DAY),
                cal.getMinimum(Calendar.MINUTE),
                cal.getMinimum(Calendar.SECOND));
        return cal.getTime();
    }

    public static Date getLastDayOfMoth(Date date){
        Date  fromDate = date;
        Calendar c = Calendar.getInstance();
        c.setTime(fromDate);
        c.setFirstDayOfWeek(Calendar.MONDAY);
        c.set(Calendar.DAY_OF_MONTH, c.getActualMaximum(Calendar.DAY_OF_MONTH));
        fromDate = c.getTime();
        Date result = null;
        if(fromDate != null){
            DateTime dateTime = new DateTime(fromDate);
            dateTime = dateTime.withTime(MAX_HOURS_IN_DAY, MAX_MINUTES_IN_HOUR, MAX_SECONDS_IN_MINUTE, MAX_MILLIS_IN_SECOND);
            result = dateTime.toDate();
        }

        return result;
    }

    public static Date getNextNDay(int days) {
        Date currentDay = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(currentDay);
        cal.add(Calendar.DATE,days);
        return cal.getTime();
    }

    public static Date getLastDayOfTheFollowingMoth(Date currentDate,int days){
        Calendar cal = Calendar.getInstance();
        cal.setTime(currentDate);
        cal.add(Calendar.DATE,days);
        return cal.getTime();
    }

    public static int numberOfDaysBetween(Date startDate, Date endDate){
        DateTime startDateJd = new DateTime(startDate);
        DateTime endDateJd = new DateTime(endDate);
        return Days.daysBetween(startDateJd, endDateJd).getDays();
    }

    public static void main(String[] args) {
        System.out.println("//**********************");
        System.out.println("getNextNDay: " + getNextNDay(1));
        System.out.println("min hour: " + getDateMinHour(getNextNDay(1)));
        System.out.println("max hour: " + getDateMaxHour(getNextNDay(1)));
        System.out.println(" ");
        System.out.println("getLastDayOfTheFollowingMoth: " + getLastDayOfTheFollowingMoth(getNextNDay(1),30));
        System.out.println("max hour: " + getDateMaxHour(getLastDayOfTheFollowingMoth(getNextNDay(1),30)));
        System.out.println(" ");
        System.out.println("getLastDayOfTheFollowingMoth: " + getLastDayOfTheFollowingMoth(getNextNDay(1),90));
        System.out.println("max hour: " + getDateMaxHour(getLastDayOfTheFollowingMoth(getNextNDay(1),90)));


    }
}
