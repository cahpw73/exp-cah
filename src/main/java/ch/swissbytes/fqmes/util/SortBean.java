package ch.swissbytes.fqmes.util;

import ch.swissbytes.fqmes.model.entities.ScopeSupplyEntity;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by christian on 31/10/14.
 */
@Named
@RequestScoped
public class SortBean implements Serializable {

    @Inject
    private Util util;

    public void sortScopeSupplyEntity(List<ScopeSupplyEntity> list) {
        Comparator<ScopeSupplyEntity> comparator = new Comparator<ScopeSupplyEntity>() {
            @Override
            public int compare(ScopeSupplyEntity ssp1, ScopeSupplyEntity ssp2) {
                return sortItemNumber(ssp1.getCode(), ssp2.getCode());
            }
        };
        Collections.sort(list, comparator);
    }

    public boolean matchRegex(String code) {
        Pattern pattern = Pattern.compile("^[0-9]+(\\.[0-9]+)*$");
        Matcher matcher = pattern.matcher(code);
        boolean matchFound = matcher.find();
        return matchFound;
    }

    public boolean matchRegexAtLeastOne(String code1, String code2) {
        return matchRegex(code1) || matchRegex(code2);
    }



    public int sortItemNumber(Object val1, Object val2) {
        if (val1.toString().equals("") || val2.toString().equals("")) {
            if (val1.toString().equals("")) {
                return 1;
            } else if (val2.toString().equals("")) {
                return -1;
            }
            return 0;
        } else {
            if (matchRegex(val1.toString()) && matchRegex(val2.toString())) { //Case 1 valid match regex
                String codeA = val1.toString();
                String codeB = val2.toString();
                return isLessThan(codeA, codeB) ? -1 : 1;
            } else if (matchRegexAtLeastOne(val1.toString(), val2.toString())) { // Case 2 valid only one regex
                int i = 0;
                if (matchRegex(val1.toString())) {
                    i = -1;
                } else if (matchRegex(val2.toString())) {
                    i = 1;
                }
                return i;
            } else { //Case 3 none valid regex
                return val1.toString().compareTo(val2.toString());
            }
        }
    }

    public int sortPoNumber(Object val1, Object val2){
            boolean targetAIsNumber= NumberUtils.isNumber(val1 != null ? val1.toString() : "");
            boolean targetBIsNumber=NumberUtils.isNumber(val2 != null ? val2.toString().trim() : "");
            if(targetAIsNumber&&targetBIsNumber){
                return Double.parseDouble(val1.toString().trim())>Double.parseDouble(val2.toString().trim())?1:-1;
            }else{
                if((!targetAIsNumber&&!targetBIsNumber)){
                    return val1.toString().trim().toLowerCase().compareTo(val2.toString().trim().toLowerCase());
                }else{
                    return targetAIsNumber?-1:1;
                }
            }
    }

    public int sortIntegers(Object val1, Object val2) {
        Integer quantity = Integer.parseInt(val1.toString());
        Integer quantity2 = Integer.parseInt(val2.toString());
        return quantity.intValue() > quantity2.intValue() ? 1 : -1;
    }


    public int sortStrings(Object val1, Object val2) {
        String desc1 = val1.toString();
        String desc2 = val2.toString();
        return desc1.toLowerCase().compareTo(desc2.toLowerCase());
    }
    public int sortDate(Object val1, Object val2) {

        Date dateA = val1!=null?util.toLocalDate((Date) val1):null;
        Date dateB = val2!=null?util.toLocalDate((Date)val2):null;

        return dateA.compareTo(dateB);
    }
    public int sortLeadTime(Object val1, Object val2) {
        String []leadA = val1.toString().split("-");
        String []leadB = val2.toString().split("-");
        int daysA=Integer.parseInt(leadA[0]);
        int daysB=Integer.parseInt(leadB[0]);
        switch (Integer.parseInt(leadB[1])){
            case 1:
                daysB=daysB*7;
                break;
            case 2:
                daysB=daysB*30;
                break;
            case 3:
                daysB=daysB*365;
                break;
        }
        switch (Integer.parseInt(leadA[1])){
            case 1:
                daysA=daysA*7;
                break;
            case 2:
                daysA=daysA*30;
                break;
            case 3:
                daysA=daysA*365;
                break;
        }
        return daysA<daysB?-1:1;

    }

    private boolean isLessThan(String valueA, String valueB) {
        if (org.apache.commons.lang3.StringUtils.isNotEmpty(valueA) && org.apache.commons.lang3.StringUtils.isNotEmpty(valueB)) {
            String[] levelA = valueA.split("\\.");
            String[] levelB = valueB.split("\\.");

            if (levelA != null && levelA.length > 0 && levelB != null && levelB.length > 0) {
                if (Integer.parseInt(levelA[0].toString()) == Integer.parseInt(levelB[0].toString())) {
                    //StringUtils.isEmpty(StringUtils.indexOf("."));
                    int indexA = valueA.indexOf(".");
                    int indexB = valueB.indexOf(".");
                    String newValueA = "";
                    String newValueB = "";
                    if (indexA >= 0) {
                        newValueA = valueA.substring(indexA + 1, valueA.length());
                    }
                    if (indexB >= 0) {
                        newValueB = valueB.substring(indexB + 1, valueB.length());
                    }
                    return isLessThan(newValueA, newValueB);
                } else {
                    return Integer.parseInt(levelA[0].toString()) < Integer.parseInt(levelB[0].toString());
                }
            }
        }
        return StringUtils.isEmpty(valueA) ? true : false;
    }

}
