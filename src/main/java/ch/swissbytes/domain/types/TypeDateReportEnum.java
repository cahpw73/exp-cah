package ch.swissbytes.domain.types;

import java.io.Serializable;

/**
 * Created by alvaro on 9/30/14.
 */
public enum TypeDateReportEnum implements Serializable {


    PO_DELIVERY_DATE(0,"PO Delivery Date"), FORECAST_EX_WORKS_DATE(1,"Forecast Ex Works Date"),
    ACTUAL_EX_WORKS_DATE(2,"Actual Ex Works Date"), FORECAST_SITE_DATE(3,"Forecast Site Date"),
    ACTUAL_SITE_DATE(4,"Actual Site Date"), REQUIRED_SITE_DATE(5,"Required Site Date"),
    REQUIRED_ON_SITE_DATE(6,"Required On Site Date"), ACTUAL_ON_SITE_DATE(7,"Actual On Site Date"),
    NEXT_KEY_DATE(8,"Next Key Date");

    private Integer id;
    private String label;

    TypeDateReportEnum(Integer id, String label) {
        this.id = id;
        this.label=label;
    }


    public static TypeDateReportEnum getEnum(Integer ordinal) {
        for (TypeDateReportEnum poEnum : TypeDateReportEnum.values()) {
            if (poEnum.ordinal() == ordinal.intValue()) {
                return poEnum;
            }
        }
        return null;
    }

    public static TypeDateReportEnum valueOf(Integer id) {
        if (id!=null) {
            for (TypeDateReportEnum item : values()) {
                if (item.id.equals(id)) {
                    return item;
                }
            }
        }
        throw new IllegalArgumentException("User Role Id invalid : [" + id + "]");
    }

    public String getLabel() {
        return label;
    }
    public Integer getId() {
        return id;
    }
}
