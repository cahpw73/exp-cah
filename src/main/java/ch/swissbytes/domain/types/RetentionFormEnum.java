package ch.swissbytes.domain.types;

import java.io.Serializable;

/**
 * Created by alvaro on 9/30/14.
 */
public enum RetentionFormEnum implements Serializable {


    Cash(0,"Cash"), BANK_GUARANTEE(1,"Bank Guarantee"), INSURANCE_BOND(2,"Insurance Bond"), STANDBY_LC(3,"Standby LC");

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
