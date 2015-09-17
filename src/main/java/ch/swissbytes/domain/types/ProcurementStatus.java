package ch.swissbytes.domain.types;

import java.io.Serializable;

/**
 * Created by alvaro on 9/30/14.
 */
public enum ProcurementStatus implements Serializable {


    COMMITTED(0,"Committed"), FINAL(1,"Final"), READY(2,"Ready"), ON_HOLD(3,"On hold"), INCOMPLETE(4,"Incomplete");

    private Integer id;
    private String label;

    ProcurementStatus(Integer id, String label) {
        this.id = id;
        this.label=label;
    }


    public static ProcurementStatus getEnum(Integer ordinal) {
        for (ProcurementStatus poEnum : ProcurementStatus.values()) {
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
