package ch.swissbytes.domain.types;

import java.io.Serializable;

/**
 * Created by christian on 16/09/14.
 */
public enum PurchaseOrderNumberOption implements Serializable{

    P0000("0000","0999"),
    P1000("1000","1999"),
    P2000("2000","2999"),
    P3000("3000","3999"),
    P4000("4000","4999"),
    P5000("5000","5999"),
    P6000("6000","6999"),
    P7000("7000","7999"),
    P8000("8000","8999"),
    P9000("9000","9999");

    private String beginning;
    private String ending;

    PurchaseOrderNumberOption(String beginning, String ending) {
        this.beginning=beginning;
        this.ending=ending;
    }

    public String getBeginning() {
        return beginning;
    }

    public String getEnding() {
        return ending;
    }

}
