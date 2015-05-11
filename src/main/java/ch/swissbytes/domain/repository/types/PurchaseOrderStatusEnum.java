package ch.swissbytes.domain.repository.types;

import java.io.Serializable;

/**
 * Created by alvaro on 9/30/14.
 */
public enum PurchaseOrderStatusEnum implements Serializable {


    ISSUED, CANCELLED, DUE, OVERDUE, UNDER_SUPPLY, COMPLETED, INCOMPLETE, OVER_SUPPLY, STORAGE, ON_HOLD, MMR_REQUIRED,MISSING,DELETED,SHIPPED,BLANK;

        public static PurchaseOrderStatusEnum getEnum(Integer ordinal){
            for(PurchaseOrderStatusEnum poEnum:PurchaseOrderStatusEnum.values()){
                if(poEnum.ordinal()==ordinal.intValue()){
                    return poEnum;
                }
            }
            return null;
        }
    }
