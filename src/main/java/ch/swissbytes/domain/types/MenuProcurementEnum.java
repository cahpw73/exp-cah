package ch.swissbytes.domain.types;

import java.io.Serializable;

/**
 * Created by alvaro on 9/30/14.
 */
public enum MenuProcurementEnum implements Serializable {

    PROJECT,REPORT,ADMIN,PROFILE;

    public static MenuProcurementEnum getEnum(Integer value){
        for(MenuProcurementEnum menuProcurementEnum:MenuProcurementEnum.values()){
            if(menuProcurementEnum.ordinal()==value.intValue()){
                return menuProcurementEnum;
            }
        }
        throw new IllegalArgumentException("invalid menu");
    }

}
