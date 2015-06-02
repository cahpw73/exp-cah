package ch.swissbytes.domain.types;

import java.io.Serializable;

/**
 * Created by alvaro on 9/30/14.
 */
public enum POStatusEnum implements Serializable {


    COMMITED(0,"COMMITED"), FINAL(1,"FINAL"), READY(2,"READY"), ON_HOLD(3,"ON_HOLD");

    private Integer id;
    private String label;

    POStatusEnum(Integer id, String label) {
        this.id = id;
        this.label=label;
    }


    public static POStatusEnum getEnum(Integer ordinal) {
        for (POStatusEnum poEnum : POStatusEnum.values()) {
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
