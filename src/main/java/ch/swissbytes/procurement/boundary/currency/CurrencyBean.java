package ch.swissbytes.procurement.boundary.currency;


import ch.swissbytes.Service.business.currency.CurrencyService;
import ch.swissbytes.domain.model.entities.CurrencyEntity;
import ch.swissbytes.domain.types.ModeOperationEnum;
import ch.swissbytes.domain.types.StatusEnum;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

/**
 * Created by alvaro on 9/15/14.
 */
@Named
@ViewScoped
public class CurrencyBean implements Serializable {

    private static final Logger log = Logger.getLogger(CurrencyBean.class.getName());

    @Inject
    private CurrencyService service;
    private List<CurrencyEntity> list;
    private CurrencyEntity currency;
    private ModeOperationEnum mode;

    @PostConstruct
    public void create(){
        log.info("creating currency bean");
        loadList();
        mode=ModeOperationEnum.NEW;
        currency=new CurrencyEntity();
        currency.setStatus(StatusEnum.ENABLE);
    }
    public void edit(Long currencyId){
        currency=service.findById(currencyId);
        mode=ModeOperationEnum.UPDATE;
    }
    public String doSave(){
        currency.setLastUpdate(new Date());
        service.doSave(currency);
        return "currency?faces-redirect=true";
    }

    public String doUpdate(){
        currency.setLastUpdate(new Date());
        service.doUpdate(currency);
        return "currency?faces-redirect=true";
    }

    public String doDelete(Long id){
        currency=service.findById(id);
        service.delete(currency);
        return "currency?faces-redirect=true";
    }

    private void loadList(){
        list=service.getCurrencyList();
    }

    public List<CurrencyEntity> getList() {
        return list;
    }

    public CurrencyEntity getCurrency() {
        return currency;
    }

    public void setCurrency(CurrencyEntity currency) {
        this.currency = currency;
    }

    @PreDestroy
    public void destroy(){
        log.info("destroying currency bean");
    }

    public boolean isEditing(){
     return mode.ordinal()==ModeOperationEnum.UPDATE.ordinal();
    }
    public boolean isCreating(){
        return mode.ordinal()==ModeOperationEnum.NEW.ordinal();
    }

}
