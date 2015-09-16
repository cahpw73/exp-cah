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

    private String titleTotal;
    private String currencyCodeTotal1;
    private BigDecimal amountTotal1;
    private String plusTotal1;
    private String currencyCodeTotal2;
    private BigDecimal amountTotal2;
    private String plusTotal2;
    private String currencyCodeTotal3;
    private BigDecimal amountTotal3;




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

    public String getTitleTotal() {
        return titleTotal;
    }

    public void setTitleTotal(String titleTotal) {
        this.titleTotal = titleTotal;
    }

    public String getCurrencyCodeTotal1() {
        return currencyCodeTotal1;
    }

    public void setCurrencyCodeTotal1(String currencyCodeTotal1) {
        this.currencyCodeTotal1 = currencyCodeTotal1;
    }

    public BigDecimal getAmountTotal1() {
        return amountTotal1;
    }

    public void setAmountTotal1(BigDecimal amountTotal1) {
        this.amountTotal1 = amountTotal1;
    }

    public String getPlusTotal1() {
        return plusTotal1;
    }

    public void setPlusTotal1(String plusTotal1) {
        this.plusTotal1 = plusTotal1;
    }

    public String getCurrencyCodeTotal2() {
        return currencyCodeTotal2;
    }

    public void setCurrencyCodeTotal2(String currencyCodeTotal2) {
        this.currencyCodeTotal2 = currencyCodeTotal2;
    }

    public BigDecimal getAmountTotal2() {
        return amountTotal2;
    }

    public void setAmountTotal2(BigDecimal amountTotal2) {
        this.amountTotal2 = amountTotal2;
    }

    public String getPlusTotal2() {
        return plusTotal2;
    }

    public void setPlusTotal2(String plusTotal2) {
        this.plusTotal2 = plusTotal2;
    }

    public String getCurrencyCodeTotal3() {
        return currencyCodeTotal3;
    }

    public void setCurrencyCodeTotal3(String currencyCodeTotal3) {
        this.currencyCodeTotal3 = currencyCodeTotal3;
    }

    public BigDecimal getAmountTotal3() {
        return amountTotal3;
    }

    public void setAmountTotal3(BigDecimal amountTotal3) {
        this.amountTotal3 = amountTotal3;
    }
}
