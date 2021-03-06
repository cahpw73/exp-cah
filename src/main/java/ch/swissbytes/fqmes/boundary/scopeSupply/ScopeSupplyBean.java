package ch.swissbytes.fqmes.boundary.scopeSupply;


import ch.swissbytes.Service.business.enumService.EnumService;
import ch.swissbytes.Service.business.scopesupply.ScopeSupplyService;
import ch.swissbytes.Service.business.tdp.TransitDeliveryPointService;
import ch.swissbytes.domain.model.entities.*;
import ch.swissbytes.domain.types.TimeMeasurementEnum;
import ch.swissbytes.fqmes.util.Purchase;
import org.omnifaces.util.Messages;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by alvaro on 10/7/14.
 */
@Named
@ViewScoped
public class ScopeSupplyBean implements Serializable {

    @Inject
    private EnumService enumService;

    @Inject
    private ScopeSupplyService scopeSupplyService;

    private ScopeSupplyEntity newScopeSupply;

    private TransitDeliveryPointEntity editTdp;

    @Inject
    private List<ScopeSupplyEntity>scopeSupplies;


    @Inject
    private TransitDeliveryPointService tdpService;

    private ScopeSupplyEntity selectedScopeSupply;

    private ScopeSupplyEntity editScopeSupply;

    private Integer indexScopeSupplyEditing=-1;


    @Inject
    @Purchase
    private PurchaseOrderEntity purchaseOrder;

    private TransitDeliveryPointEntity tdp;
    private int indexEditingTdp=-1;
    private int temporaryIdForTdp =-1;

    private static final Logger log = Logger.getLogger(ScopeSupplyBean.class.getName());


    public void initialize(){
        log.info("initializing...");
        if(indexScopeSupplyEditing!=null&&indexScopeSupplyEditing>=0&&indexScopeSupplyEditing<scopeSupplies.size()){
            editScopeSupply=scopeSupplyService.clone(scopeSupplies.get(indexScopeSupplyEditing));
            editScopeSupply.getTdpList().clear();
            editScopeSupply.getTdpList().addAll(scopeSupplies.get(indexScopeSupplyEditing).getTdpList());
            editScopeSupply.getAttachments().clear();
            editScopeSupply.getAttachments().addAll(scopeSupplies.get(indexScopeSupplyEditing).getAttachments());
        }else{
            log.info("index invalid");
        }
    }


    public String doUpdateScopeSupply(){
        if(isValidDataUpdate()){
            if(indexScopeSupplyEditing>=0){
                ScopeSupplyEntity ss=scopeSupplyService.clone(editScopeSupply);
                ss.getTdpList().clear();
                ss.getTdpList().addAll(editScopeSupply.getTdpList());
                ss.getAttachments().clear();
                ss.getAttachments().addAll(editScopeSupply.getAttachments());
                scopeSupplies.set(indexScopeSupplyEditing,ss);
            }
            return "/purchase/create?faces-redirect=true";
        }
        return "";
    }

    private boolean isValidData() {
        log.info("boolean isValidData()");
        boolean isValid = false;
        int inc = 0;
        BigDecimal quantity=newScopeSupply.getQuantity()!=null?newScopeSupply.getQuantity():new BigDecimal("0");
        Double cost=newScopeSupply.getCost()!=null?newScopeSupply.getCost().doubleValue():0D;
        if(quantity.doubleValue() >= 0){
            inc++;
        }else{
            Messages.addGlobalError("Quantity has a invalid data");
        }
        if(cost >= 0){
            inc++;
        }else{
            Messages.addGlobalError("Unit Price has a invalid data");
        }
        if(inc == 2){
            isValid = true;
        }
        return isValid;
    }

    @PostConstruct
    public void create(){
        log.log(Level.INFO,String.format("creating bean [%s]",this.getClass().toString()));
        newScopeSupply=new ScopeSupplyEntity();
        tdp=new TransitDeliveryPointEntity();
        tdp.setIsForecastSiteDateManual(true);
        newScopeSupply.setIsForecastSiteDateManual(false);
        newScopeSupply.setDeliveryLeadTimeMs(TimeMeasurementEnum.DAY);
        newScopeSupply.setResponsibleExpediting(purchaseOrder !=null&&purchaseOrder.getResponsibleExpediting()!=null? purchaseOrder.getResponsibleExpediting().toString():null);
        newScopeSupply.setRequiredSiteDate(purchaseOrder.getRequiredDate());
        newScopeSupply.setPoDeliveryDate(purchaseOrder.getPoDeliveryDate());
    }

    public void setDeliveryDate(Date date){
        newScopeSupply.setPoDeliveryDate(date);
    }

    public void cleanTransitDeliveryPoint(){
        tdp=new TransitDeliveryPointEntity();
        tdp.setIsForecastSiteDateManual(false);
        tdp.setStatus(enumService.getStatusEnumEnable());
        tdp.setLastUpdate(new Date());
        tdp.setMeasurementTime(TimeMeasurementEnum.DAY);
        indexEditingTdp=-1;
    }

    public void addTransitDeliveryPoint(){
        log.info("public void addTransitDeliveryPoint()");
        tdp.setId(Long.parseLong(Integer.toString(temporaryIdForTdp)));
        temporaryIdForTdp--;
        newScopeSupply.getTdpList().add(tdpService.clone(tdp));
        calculateDate();
    }
    public void addTransitDeliveryPointOnEdition(){
        log.info("public void addTransitDeliveryPoint()");
        tdp.setId(Long.parseLong(Integer.toString(temporaryIdForTdp)));
        temporaryIdForTdp--;
        editScopeSupply.getTdpList().add(tdpService.clone(tdp));
        calculateDate();
    }
    public void switchModeForecastSiteDate() {
        log.info("public void switchModeForecastSiteDate()");
        if (indexScopeSupplyEditing==null||indexScopeSupplyEditing < 0) {
            if (!newScopeSupply.getIsForecastSiteDateManual()) {
                calculateDate();
            }else{
                newScopeSupply.setForecastSiteDate(null);
            }
        } else if (!editScopeSupply.getIsForecastSiteDateManual()) {
            calculateDate();
        }else{
            editScopeSupply.setForecastSiteDate(null);
        }
    }
    public void switchModeForecastSiteDateForTdp(boolean editing){
        if(editing){
            if(editTdp.getIsForecastSiteDateManual()){
                editTdp.setForecastDeliveryDate(null);
            }else{
                calulateForecasteDateForTdpEdition();
            }
        }else if(tdp.getIsForecastSiteDateManual()){
            tdp.setForecastDeliveryDate(null);
        }else {
            calulateForecasteDateForTdpCreation();
        }
    }
    public String addScopeSupply(){
        log.info("public String addScopeSupply()");
        if(isValidData()){
            registerScopeSupply();
            return "/purchase/create?faces-redirect=true";
        }
        return "";
    }

    private boolean isValidDataUpdate() {
        log.info("boolean isValidDataUpdate()");
        boolean isValid = false;
        int inc = 0;
        BigDecimal quantity=editScopeSupply.getQuantity()!=null?editScopeSupply.getQuantity():new BigDecimal("0");
        Double cost=editScopeSupply.getCost()!=null?editScopeSupply.getCost().doubleValue():0D;
        if(quantity.doubleValue() >= 0){
            inc++;
        }else{
            Messages.addGlobalError("Quantity has a invalid data");
        }
        if(cost >= 0){
            inc++;
        }else{
            Messages.addGlobalError("Unit Price has a invalid data");
        }
        if(inc == 2){
            isValid = true;
        }
        return isValid;
    }

    public String cancel(){
        return "/purchase/create?faces-redirect=true";
    }

    public String addScopeSupplyAndAdd(){
        if(isValidData()){
            registerScopeSupply();
            cleanScopeSupply();
        }
        return "";
    }
    private void registerScopeSupply(){
        log.log(Level.INFO, "SCOPE CODE " + newScopeSupply.getCode());
        ScopeSupplyEntity scopeSupplyEntity=new ScopeSupplyEntity();
        try{
            scopeSupplyEntity=(ScopeSupplyEntity)org.apache.commons.beanutils.BeanUtils.cloneBean(newScopeSupply);
            scopeSupplyEntity.getTdpList().clear();
            scopeSupplyEntity.getTdpList().addAll(newScopeSupply.getTdpList());
        }catch (Exception ex){
            ex.printStackTrace();
        }
        scopeSupplies.add(scopeSupplyEntity);
    }
    public void deleteTransitDeliveryPoint (Integer index){
        log.info(String.format("deleteTransitDeliveryPoint index [%s]",index));
        if(index>=0&&index< newScopeSupply.getTdpList().size()){
            log.info("removing.....");
            newScopeSupply.getTdpList().remove(index.intValue());
            calculateDate();
        }
    }
    public void deleteTransitDeliveryPoint2(Integer index){
        log.info(String.format("deleteTransitDeliveryPoint index [%s]",index));
        if(index>=0&&index< editScopeSupply.getTdpList().size()){
            log.info("removing.....");
            editScopeSupply.getTdpList().remove(index.intValue());
            calculateDate();
        }
    }
    @PreDestroy
    public void destroy(){
        log.log(Level.INFO, String.format("bean destroyed [%s]", this.getClass().toString()));
    }

    public Date calculateDate() {
        log.info("calculateDate");
        Date date=null;
            if(indexScopeSupplyEditing>=0){
                if(!editScopeSupply.getIsForecastSiteDateManual()){
                    date=scopeSupplyService.calculateForecastSiteDate(editScopeSupply);
                    editScopeSupply.setForecastSiteDate(date);
                }
            }else{
                if(!newScopeSupply.getIsForecastSiteDateManual()){
                    date=scopeSupplyService.calculateForecastSiteDate(newScopeSupply);
                    newScopeSupply.setForecastSiteDate(date);
                }
            }

        log.info("date calculated "+date);
        return date;
    }
    public void calulateForecasteDateForTdpCreation(){
        log.info("calulateForecasteDateForTdpCreation....");
        if(!tdp.getIsForecastSiteDateManual()){
            if(indexScopeSupplyEditing<0){
                List<TransitDeliveryPointEntity>list=newScopeSupply.getTdpList();
                TransitDeliveryPointEntity tdpPrevious=list.size()>0?list.get(list.size()-1):null;

                tdp.setForecastDeliveryDate(scopeSupplyService.calculateForecastDeliveryDateForTdp(newScopeSupply, newScopeSupply.getTdpList().size()==0,tdpPrevious, tdp));
            }else{
                List<TransitDeliveryPointEntity>list=editScopeSupply.getTdpList();
                TransitDeliveryPointEntity tdpPrevious=list.size()>0?list.get(list.size()-1):null;
                tdp.setForecastDeliveryDate(scopeSupplyService.calculateForecastDeliveryDateForTdp(editScopeSupply, editScopeSupply.getTdpList().size()==0,tdpPrevious, tdp));
            }
        }
    }

    public void calulateForecasteDateForTdpEdition(){
        log.info("calulateForecasteDateForTdpEdition....");
        if(!tdp.getIsForecastSiteDateManual()){
            log.info("calculating...");;
            if(indexScopeSupplyEditing<0){
                List<TransitDeliveryPointEntity>list=newScopeSupply.getTdpList();
                TransitDeliveryPointEntity tdpPrevious=list.size()>1&&indexEditingTdp>0?list.get(indexEditingTdp-1):null;
                editTdp.setForecastDeliveryDate(scopeSupplyService.calculateForecastDeliveryDateForTdp(newScopeSupply, indexEditingTdp==0,tdpPrevious, editTdp));
            }else{
                List<TransitDeliveryPointEntity>list=editScopeSupply.getTdpList();
                TransitDeliveryPointEntity tdpPrevious=list.size()>1&&indexEditingTdp>0?list.get(indexEditingTdp-1):null;
                editTdp.setForecastDeliveryDate(scopeSupplyService.calculateForecastDeliveryDateForTdp(editScopeSupply, indexEditingTdp==0,tdpPrevious, editTdp));
            }
        }else{
            log.info("calculated");
        }
    }
    public void cleanScopeSupply(){
        log.info("cleaning scope supply");
        indexScopeSupplyEditing=-1;
        newScopeSupply=new ScopeSupplyEntity();
        newScopeSupply.setIsForecastSiteDateManual(false);

    }

    public void selectForEditingScopeSuppply(Integer index){
        indexScopeSupplyEditing=-1;
        if(index>=0&&index <=scopeSupplies.size()){
            indexScopeSupplyEditing=index;
            editScopeSupply= scopeSupplyService.clone(scopeSupplies.get(index));
        }
    }

    public ScopeSupplyEntity getEditScopeSupply() {
        return editScopeSupply;
    }

    public ScopeSupplyEntity getNewScopeSupply() {
        return newScopeSupply;
    }

    public Integer getIndexScopeSupplyEditing() {
        return indexScopeSupplyEditing;
    }

    public void setIndexScopeSupplyEditing(Integer indexScopeSupplyEditing) {
        this.indexScopeSupplyEditing = indexScopeSupplyEditing;
    }

    public void selectTransitDeliveryPoint(final int index){
        if(index>=0 && index<newScopeSupply.getTdpList().size()){
           editTdp= tdpService.clone(newScopeSupply.getTdpList().get(index));
            indexEditingTdp=index;
        }

    }
    public void selectTransitDeliveryPointOnEdition(final int index){
        if(index>=0 && index<editScopeSupply.getTdpList().size()){
            editTdp= tdpService.clone(editScopeSupply.getTdpList().get(index));
            indexEditingTdp=index;
        }

    }



    public TransitDeliveryPointEntity getTdp() {
        return tdp;
    }

    public void setTdp(TransitDeliveryPointEntity tdp) {
        this.tdp = tdp;
    }

    public TransitDeliveryPointEntity getEditTdp() {
        return editTdp;
    }

    public void setEditTdp(TransitDeliveryPointEntity editTdp) {
        this.editTdp = editTdp;
    }

    public void doUpdateTdp(){
        if(indexEditingTdp>=0&&indexEditingTdp<newScopeSupply.getTdpList().size()){
            editTdp.setLastUpdate(new Date());
            newScopeSupply.getTdpList().set(indexEditingTdp,tdpService.clone(editTdp));
            calculateDate();
        }
    }
    public void doUpdateTdpOnEdition(){
        if(indexEditingTdp>=0&&indexEditingTdp<editScopeSupply.getTdpList().size()){
            editTdp.setLastUpdate(new Date());
            editScopeSupply.getTdpList().set(indexEditingTdp,tdpService.clone(editTdp));
            calculateDate();
        }
    }
}


