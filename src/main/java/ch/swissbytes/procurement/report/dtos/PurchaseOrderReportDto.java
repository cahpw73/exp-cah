package ch.swissbytes.procurement.report.dtos;

import ch.swissbytes.domain.model.entities.PurchaseOrderEntity;
import ch.swissbytes.domain.model.entities.ScopeSupplyEntity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by Christian on 23/06/2015.
 */
public class PurchaseOrderReportDto implements Serializable {

    private String code;
    private Integer quantity;
    private String unit;
    private String description;
    private BigDecimal cost;
    private BigDecimal totalCost;
    private String poTitle;
    private String preamble;

    public PurchaseOrderReportDto(){

    }

    public PurchaseOrderReportDto(ScopeSupplyEntity scopeSupply){
        this.code = scopeSupply.getCode();
        this.quantity = scopeSupply.getQuantity();
        this.unit = scopeSupply.getUnit();
        this.description = scopeSupply.getDescription();
        this.cost = scopeSupply.getCost();
        this.totalCost = scopeSupply.getTotalCost();
        this.poTitle = null;
        this.preamble = null;
    }
    public PurchaseOrderReportDto(PurchaseOrderEntity po, String preamble){
        this.code = null;
        this.quantity = null;
        this.unit = null;
        this.description = null;
        this.cost = null;
        this.totalCost = null;
        this.poTitle = po.getPoEntity().getOrderTitle();
        this.preamble = preamble;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getCost() {
        return cost;
    }

    public void setCost(BigDecimal cost) {
        this.cost = cost;
    }

    public BigDecimal getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(BigDecimal totalCost) {
        this.totalCost = totalCost;
    }

    public String getPoTitle() {
        return poTitle;
    }

    public void setPoTitle(String poTitle) {
        this.poTitle = poTitle;
    }

    public String getPreamble() {
        return preamble;
    }

    public void setPreamble(String preamble) {
        this.preamble = preamble;
    }
}
