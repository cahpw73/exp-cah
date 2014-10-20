package ch.swissbytes.fqmes.boundary.scopeSupply;


import ch.swissbytes.fqmes.control.enumService.EnumService;
import ch.swissbytes.fqmes.control.scopesupply.ScopeSupplyService;
import ch.swissbytes.fqmes.control.tdp.TransitDeliveryPointService;
import ch.swissbytes.fqmes.model.entities.*;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
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
    private Date deliveryDate;

    @Inject
    private TransitDeliveryPointService tdpService;

    private ScopeSupplyEntity selectedScopeSupply;

    private ScopeSupplyEntity editScopeSupply;

    private Integer indexScopeSupplyEditing;

    private TransitDeliveryPointEntity tdp;
    private int indexEditingTdp=-1;

    private static final Logger log = Logger.getLogger(ScopeSupplyBean.class.getName());


    public void initialize(){
        log.info("initializing...");
        if(indexScopeSupplyEditing!=null&&indexScopeSupplyEditing>=0&&indexScopeSupplyEditing<scopeSupplies.size()){
            editScopeSupply=scopeSupplyService.clone(scopeSupplies.get(indexScopeSupplyEditing));
            editScopeSupply.getTdpList().clear();
            editScopeSupply.getTdpList().addAll(scopeSupplies.get(indexScopeSupplyEditing).getTdpList());
        }else{
            log.info("index invalid");
        }
    }

    public String doUpdateScopeSupply(){
        if(indexScopeSupplyEditing>=0){
            ScopeSupplyEntity ss=scopeSupplyService.clone(editScopeSupply);
            ss.getTdpList().clear();
            ss.getTdpList().addAll(editScopeSupply.getTdpList());
            scopeSupplies.set(indexScopeSupplyEditing,ss);
        }
        return "/purchase/create?faces-redirect=true";
    }

    @PostConstruct
    public void create(){
        log.log(Level.INFO,String.format("creating bean [%s]",this.getClass().toString()));
        newScopeSupply=new ScopeSupplyEntity();
        tdp=new TransitDeliveryPointEntity();
        tdp.setIsForecastSiteDateCalculated(true);
        newScopeSupply.setIsForecastSiteDateCalculated(true);
        if(deliveryDate.getYear()>0){
            newScopeSupply.setDeliveryDate(deliveryDate);
        }
    }

    public void setDeliveryDate(Date date){
        newScopeSupply.setDeliveryDate(date);
    }

    public void cleanTransitDeliveryPoint(){
        tdp=new TransitDeliveryPointEntity();
        tdp.setIsForecastSiteDateCalculated(true);
        tdp.setStatus(enumService.getStatusEnumEnable());
        tdp.setLastUpdate(new Date());
    }

    public void addTransitDeliveryPoint(){
        log.info("public void addTransitDeliveryPoint()");
        newScopeSupply.getTdpList().add(tdpService.clone(tdp));
        calculateDate();
    }
    public void addTransitDeliveryPointOnEdition(){
        log.info("public void addTransitDeliveryPoint()");
        editScopeSupply.getTdpList().add(tdpService.clone(tdp));
        calculateDate();
    }
    public void switchModeForecastSiteDate() {
        log.info("public void switchModeForecastSiteDate()");
        if (indexScopeSupplyEditing==null||indexScopeSupplyEditing < 0) {
            if (newScopeSupply.getIsForecastSiteDateCalculated()) {
                calculateDate();
            }else{
                newScopeSupply.setSiteDate(null);
            }
        } else if (editScopeSupply.getIsForecastSiteDateCalculated()) {
            editScopeSupply.setSiteDate(null);
        }else{
            calculateDate();
        }
    }
    public void switchModeForecastSiteDateForTdp(boolean editing){
        if(editing){
            if(editTdp.getIsForecastSiteDateCalculated()){
                calulateForecasteDateForTdpEdition();
            }else{
                editTdp.setForecastDeliveryDate(null);
            }
        }else if(tdp.getIsForecastSiteDateCalculated()){
            calulateForecasteDateForTdpCreation();
        }else {
            tdp.setForecastDeliveryDate(null);
        }
    }
    public String addScopeSupply(){
        log.info("public String addScopeSupply()");
        registerScopeSupply();
        return "/purchase/create?faces-redirect=true";
    }

    public String cancel(){
        return "/purchase/create?faces-redirect=true";
    }

    public String addScopeSupplyAndAdd(){
        registerScopeSupply();
        cleanScopeSupply();
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
        if(index>=0&&index< newScopeSupply.getTdpList().size()){
            newScopeSupply.getTdpList().remove(index.intValue());
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
            if(indexScopeSupplyEditing!=null&&indexScopeSupplyEditing>=0){
                if(editScopeSupply.getIsForecastSiteDateCalculated()){
                    date=scopeSupplyService.calculateForecastSiteDate(editScopeSupply);
                    editScopeSupply.setSiteDate(date);
                }
            }else{
                if(newScopeSupply.getIsForecastSiteDateCalculated()){
                    date=scopeSupplyService.calculateForecastSiteDate(newScopeSupply);
                    newScopeSupply.setSiteDate(date);
                }
            }

        log.info("date calculated "+date);
        return date;
    }
    public void calulateForecasteDateForTdpCreation(){
        log.info("calulateForecasteDateForTdpCreation....");
        if(tdp.getIsForecastSiteDateCalculated()){
            List<TransitDeliveryPointEntity>list=newScopeSupply.getTdpList();
            TransitDeliveryPointEntity tdpPrevious=list.size()>0?list.get(list.size()-1):null;
            tdp.setForecastDeliveryDate(scopeSupplyService.calculateForecastDeliveryDateForTdp(newScopeSupply, newScopeSupply.getTdpList().size()==0,tdpPrevious, tdp));
        }
    }

    public void calulateForecasteDateForTdpEdition(){
        log.info("calulateForecasteDateForTdpEdition....");
        if(tdp.getIsForecastSiteDateCalculated()){
            if(editScopeSupply!=null){
            List<TransitDeliveryPointEntity>list=editScopeSupply.getTdpList();
            TransitDeliveryPointEntity tdpPrevious=list.size()>=0?list.get(list.size()-1):null;
            tdp.setForecastDeliveryDate(scopeSupplyService.calculateForecastDeliveryDateForTdp(editScopeSupply, editScopeSupply.getTdpList().size() == 0, tdpPrevious, tdp));
            }else{
                if(newScopeSupply!=null){
                    /*List<TransitDeliveryPointEntity>list=newScopeSupply.getTdpList();
                    TransitDeliveryPointEntity tdpPrevious=list.size()>=0?list.get(list.size()-1):null;
                    tdp.setForecastDeliveryDate(scopeSupplyService.calculateForecastDeliveryDateForTdp(newScopeSupply, newScopeSupply.getTdpList().size() == 0, tdpPrevious, tdp));*/
                }
            }
        }
    }
  /*  public void calulateForecasteDateForTdpEdition2(){
        log.info("calulateForecasteDateForTdpEdition2....");
        if(tdp.getIsForecastSiteDateCalculated()){
            List<TransitDeliveryPointEntity>list=newScopeSupply.getTdpList();
            TransitDeliveryPointEntity tdpPrevious=list.size()>=0?list.get(list.size()-1):null;
            tdp.setForecastDeliveryDate(scopeSupplyService.calculateForecastDeliveryDateForTdp(newScopeSupply, newScopeSupply.getTdpList().size() == 0, tdpPrevious, tdp));
        }
    }*/
    public void cleanScopeSupply(){
        log.info("cleaning scope supply");
        indexScopeSupplyEditing=-1;
        newScopeSupply=new ScopeSupplyEntity();
        newScopeSupply.setIsForecastSiteDateCalculated(true);

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


