package ch.swissbytes.domain.types;

import java.io.Serializable;

/**
 * Created by alvaro on 9/30/14.
 */
public enum PaymentTermsEnum implements Serializable {


    NET_30(0,"Net 30 Days from receipt of invoice"),
    NET_14(1,"Net 15 Days from receipt of invoice"),
    NET_7(2,"Net 7 Days from receipt of invoice"),
    NET_CASH_DELIVERY(3,"Net Cash prior to delivery"),
    CASH(4,"Cash with Order"),
    NET_CASH_INVOICE(5,"Net Cash on receipt of invoice");

    private Integer id;
    private String label;

    PaymentTermsEnum(Integer idS,String label){
        this.label = label;
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public String getLabel() {
        return label;
    }
}
