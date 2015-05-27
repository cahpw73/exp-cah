package ch.swissbytes.Service.business.logo;


import ch.swissbytes.Service.business.Service;
import ch.swissbytes.domain.model.entities.LogoEntity;
import ch.swissbytes.domain.types.StatusEnum;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.transaction.Transactional;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

/**
 * Created by christian  on 11/05/15.
 */
public class LogoService extends Service<LogoEntity> implements Serializable{

    private static final Logger log = Logger.getLogger(LogoService.class.getName());

    @Inject
    private LogoDao dao;

    @PostConstruct
    public void create(){
        super.initialize(dao);
    }

    @Transactional
    public void delete(LogoEntity logo){
        logo.setStatus(StatusEnum.DELETED);
        logo.setFile(null);
        dao.update(logo);
    }
    @Transactional
    public LogoEntity save(LogoEntity logo){
        logo.setLastUpdate(new Date());
        logo.setStatus(StatusEnum.ENABLE);
        dao.save(logo);
        return logo;
    }

    public List<LogoEntity> getLogoList(){
        log.info("getLogoList");
        Date date =new Date();
        List<LogoEntity> list=dao.getLogoList();
        Date end=new Date();
        log.info(String.format("logo list takes %s ms ",Long.toString(end.getTime()-date.getTime())));
        return list;
    }


    public List<LogoEntity> findByFileName(final String fileName) {
        return dao.findByFileName(fileName);
    }

    public List<LogoEntity> findAll() {
        return dao.findAll();
    }
}
