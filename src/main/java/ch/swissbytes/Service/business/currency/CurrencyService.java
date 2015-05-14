package ch.swissbytes.Service.business.currency;


import ch.swissbytes.Service.business.Service;
import ch.swissbytes.domain.model.entities.CurrencyEntity;
import ch.swissbytes.domain.types.StatusEnum;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.transaction.Transactional;
import java.io.Serializable;
import java.util.Currency;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

/**
 * Created by christian  on 11/05/15.
 */
public class CurrencyService extends Service<CurrencyEntity> implements Serializable{

    private static final Logger log = Logger.getLogger(CurrencyService.class.getName());

    @Inject
    private CurrencyDao dao;

    @PostConstruct
    public void create(){
        super.initialize(dao);
    }

    @Transactional
    public void delete(CurrencyEntity currency){
        currency.setStatus(StatusEnum.DELETED);
        currency.setLastUpdate(new Date());
        dao.update(currency);
    }

    public CurrencyEntity findById(Long id){
        List<CurrencyEntity> list=dao.findById(CurrencyEntity.class,id);
        return !list.isEmpty()?list.get(0):null;
    }

    public List<CurrencyEntity> getCurrencyList(){
        log.info("getCurrencyList");
        return dao.getLogoList();
    }


}
