package ch.swissbytes.domain.types;

import java.io.Serializable;

/**
 * Created by alvaro on 9/30/14.
 */
public enum ExpeditingStatusEnum implements Serializable {


    ISSUED(0,"issued"), CANCELLED(1,"cancelled"), DUE(2,"due"), OVERDUE(3,"overdue"), UNDER_SUPPLY(4,"under supply"), COMPLETED(5,"completed"),
    INCOMPLETE(6,"incomplete"), OVER_SUPPLY(7,"over supply"), STORAGE(8,"storage"), ON_HOLD(9,"on hold"),MMR_REQUIRED(10,"mmr required"),
    MISSING(11,"missing"),DELETED(12,"deleted"), SHIPPED(13,"shipped"),BLANK(14,"");

    private Integer id;
    private String label;

    ExpeditingStatusEnum(Integer id, String label) {
        this.id = id;
        this.label=label;
    }


    public static ExpeditingStatusEnum getEnum(Integer ordinal) {
        for (ExpeditingStatusEnum poEnum : ExpeditingStatusEnum.values()) {
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
