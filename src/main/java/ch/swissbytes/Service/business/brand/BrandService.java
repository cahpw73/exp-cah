package ch.swissbytes.Service.business.brand;


import ch.swissbytes.Service.business.Service;
import ch.swissbytes.domain.model.entities.BrandEntity;

import javax.inject.Inject;
import javax.transaction.Transactional;
import java.io.Serializable;
import java.util.List;
import java.util.logging.Logger;

/**
 * Created by christian  on 11/05/15.
 */
public class BrandService  extends Service<BrandEntity> implements Serializable{

    private static final Logger log = Logger.getLogger(BrandService.class.getName());

    @Inject
    private BrandDao brandDao;



    @Transactional
    public void doSave(BrandEntity brandEntity){
        log.info("doSave");
        brandDao.doSave(brandEntity);
    }

    @Transactional
    public void doUpdate(BrandEntity detachedEntity){
        log.info("doUpdate");
        brandDao.doUpdate(detachedEntity);
    }

    public List<BrandEntity> getBrandList(){
        log.info("getBrandList");
        return brandDao.getBrandList();
    }

    public List<BrandEntity> findByName(final String name){
        log.info("findByName");
        String brandName = name != null ? name : "";
        return brandDao.findByName(brandName);
    }

    public List<BrandEntity> findByLikeName(final String name){
        log.info("findByLikeName");
        String brandName = name != null ? name : "";
        return brandDao.findByLikeName(brandName);
    }

}
