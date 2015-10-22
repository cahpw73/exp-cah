package ch.swissbytes.procurement.report.dtos;

import ch.swissbytes.domain.model.entities.*;

import org.apache.commons.lang.StringUtils;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by Christian on 23/06/2015.
 */
public class PurchaseOrderReportDto implements Serializable {

    private String code;
    private BigDecimal quantity;
    private String unit;
    private String description;
    private BigDecimal cost;
    private BigDecimal totalCost;
    private String poTitle;
    private String preamble;
    private String strTotalCost;
    private Date deliveryDate;

    private BigDecimal totalAmount;

    private String currency;

    public PurchaseOrderReportDto(){

    }

    public PurchaseOrderReportDto(ItemEntity itemEntity){
        this.code = itemEntity.getCode();
        this.quantity = itemEntity.getQuantity();
        this.unit = itemEntity.getUnit();
        this.description = itemEntity.getDescription();
        this.cost = itemEntity.getCost();
        this.totalCost = itemEntity.getTotalCost();
        this.deliveryDate = itemEntity.getPoDeliveryDate();
        this.poTitle = null;
        this.preamble = null;
        this.strTotalCost = null;
        this.totalAmount = null;
        String symbol="";
        if(itemEntity.getProjectCurrency()!=null&&itemEntity.getProjectCurrency().getCurrency()!=null){
            if(StringUtils.isNotEmpty(itemEntity.getProjectCurrency().getCurrency().getSymbol())&&StringUtils.isNotBlank(itemEntity.getProjectCurrency().getCurrency().getSymbol())){
                symbol=itemEntity.getProjectCurrency().getCurrency().getSymbol();
            }else{
                symbol=itemEntity.getProjectCurrency().getCurrency().getCode();
            }
        }
        this.currency=symbol;
    }
    public PurchaseOrderReportDto(String description){
        this.poTitle=description;
    }
    public PurchaseOrderReportDto(PurchaseOrderEntity po, String preamble){
        this.code = null;
        this.quantity = null;
        this.unit = null;
        this.description = null;
        this.cost = null;
        this.totalCost = null;
        this.deliveryDate = null;
        if(po!=null) {
            this.poTitle = po.getPoTitle();
        }
        this.preamble = preamble;
        this.strTotalCost = null;
        this.totalAmount = null;
    }
    public PurchaseOrderReportDto(ClausesEntity clauses){
        this.code = null;
        this.quantity = null;
        this.unit = null;
        this.description = null;
        this.cost = null;
        this.totalCost = null;
        this.poTitle = null;
        this.preamble = clauses.getClauses();
        this.strTotalCost = null;
        this.totalAmount = null;
    }

    public PurchaseOrderReportDto loadTotalCost(String currencyCode,BigDecimal totalAmount){
        PurchaseOrderReportDto dto = new PurchaseOrderReportDto();
        dto.setCode(null);
        dto.setQuantity(null);
        dto.setUnit(null);
        dto.setDescription(null);
        dto.setCost(null);
        dto.setTotalCost(null);
        dto.setPoTitle(null);
        dto.setPreamble(null);
        dto.setDeliveryDate(null);
        dto.setStrTotalCost("TOTAL "+ currencyCode);
        dto.setTotalAmount(totalAmount);
        return dto;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
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

    public String getStrTotalCost() {
        return strTotalCost;
    }

    public void setStrTotalCost(String strTotalCost) {
        this.strTotalCost = strTotalCost;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public Date getDeliveryDate() {
        return deliveryDate;
    }

    public void setDeliveryDate(Date deliveryDate) {
        this.deliveryDate = deliveryDate;
    }
}
