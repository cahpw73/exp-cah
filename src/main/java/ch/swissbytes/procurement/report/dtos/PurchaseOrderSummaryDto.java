package ch.swissbytes.procurement.report.dtos;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Created by Christian on 08/09/2015.
 */
public class PurchaseOrderSummaryDto implements Serializable {
    public String title;
    public String currencyCode1;
    public BigDecimal amount1;
    public String currencyCode2;
    public BigDecimal amount2;
    public String currencyCode3;
    public BigDecimal amount3;


}
