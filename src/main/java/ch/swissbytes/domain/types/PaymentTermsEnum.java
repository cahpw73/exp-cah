package ch.swissbytes.domain.types;

import java.io.Serializable;
import java.util.Comparator;

/**
 * Created by alvaro on 9/30/14.
 */
public enum PaymentTermsEnum implements Serializable {


    NET_30(0, "Net 30 Days from receipt of invoice", 3),
    NET_14(1, "Net 15 Days from receipt of invoice", 4),
    NET_7(2, "Net 7 Days from receipt of invoice", 5),
    NET_CASH_DELIVERY(3, "Net Cash prior to delivery", 6),
    CASH(4, "Cash with Order", 7),
    NET_CASH_INVOICE(5, "Net Cash on receipt of invoice", 8),
    NET_45(0, "Net 45 Days from receipt of invoice", 2),
    NET_60(1, "Net 60 Days from receipt of invoice", 1);

    private Integer id;
    private String label;
    private Integer ordered;

    PaymentTermsEnum(Integer idS, String label, Integer ordered) {
        this.label = label;
        this.id = id;
        this.ordered = ordered;
    }

    public Integer getId() {
        return id;
    }

    public String getLabel() {
        return label;
    }

    public Integer getOrdered() {
        return ordered;
    }
}
