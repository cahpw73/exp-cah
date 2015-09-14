package ch.swissbytes.procurement.report.dtos;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Created by Christian on 08/09/2015.
 */
public class PurchaseOrderSummaryDto implements Serializable {
    private String title;
    private String currencyCode1;
    private BigDecimal amount1;
    private String plus1;
    private String currencyCode2;
    private BigDecimal amount2;
    private String plus2;
    private String currencyCode3;
    private BigDecimal amount3;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCurrencyCode1() {
        return currencyCode1;
    }

    public void setCurrencyCode1(String currencyCode1) {
        this.currencyCode1 = currencyCode1;
    }

    public BigDecimal getAmount1() {
        return amount1;
    }

    public void setAmount1(BigDecimal amount1) {
        this.amount1 = amount1;
    }

    public String getPlus1() {
        return plus1;
    }

    public void setPlus1(String plus1) {
        this.plus1 = plus1;
    }

    public String getCurrencyCode2() {
        return currencyCode2;
    }

    public void setCurrencyCode2(String currencyCode2) {
        this.currencyCode2 = currencyCode2;
    }

    public BigDecimal getAmount2() {
        return amount2;
    }

    public void setAmount2(BigDecimal amount2) {
        this.amount2 = amount2;
    }

    public String getPlus2() {
        return plus2;
    }

    public void setPlus2(String plus2) {
        this.plus2 = plus2;
    }

    public String getCurrencyCode3() {
        return currencyCode3;
    }

    public void setCurrencyCode3(String currencyCode3) {
        this.currencyCode3 = currencyCode3;
    }

    public BigDecimal getAmount3() {
        return amount3;
    }

    public void setAmount3(BigDecimal amount3) {
        this.amount3 = amount3;
    }
}
