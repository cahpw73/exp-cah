package ch.swissbytes.domain.types;

import java.io.Serializable;

/**
 * Created by alvaro on 9/30/14.
 */
public enum RetentionFormEnum implements Serializable {


            Cash(0,"CASH"), BANK_GUARANTEE(1,"BANK GUARANTEE"), INSURANCE_BOND(2,"INSURANCE BOND"), STANDBY_LC(3,"STANDBY LC");

    private Integer id;
    private String label;

    RetentionFormEnum(Integer id, String label) {
        this.id = id;
        this.label=label;
    }


    public static RetentionFormEnum getEnum(Integer ordinal) {
        for (RetentionFormEnum poEnum : RetentionFormEnum.values()) {
            if (poEnum.ordinal() == ordinal.intValue()) {
                return poEnum;
            }
        }
        return null;
    }

    public String getLabel() {
        return label;
    }
}
