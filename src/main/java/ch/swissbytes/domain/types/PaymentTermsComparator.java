package ch.swissbytes.domain.types;

import java.util.Comparator;

/**
 * Created by Christian on 04/11/2015.
 */
public class PaymentTermsComparator implements Comparator<PaymentTermsEnum> {
    public int compare(PaymentTermsEnum o1, PaymentTermsEnum o2) {
        return o1.getOrdered() > o2.getOrdered() ? 1 : -1;
    }
}
