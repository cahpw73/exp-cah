package ch.swissbytes.fqmes.types;

import java.io.Serializable;

/**
 * Created by alvaro on 9/30/14.
 */
public enum PurchaseOrderStatusEnum implements Serializable {


    ISSUED, CANCELLED, DUE, OVERDUE, UNDER_SUPPLY, COMPLETED, INCOMPLETE, OVER_SUPPLY, STORAGE, ON_HOLD, MMR_REQUIRED,MISSING;

        public static PurchaseOrderStatusEnum getEnum(Integer ordinal){
            for(PurchaseOrderStatusEnum poEnum:PurchaseOrderStatusEnum.values()){
                if(poEnum.ordinal()==ordinal.intValue()){
                    return poEnum;
                }
            }
            return null;
        }
    }
