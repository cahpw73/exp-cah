package ch.swissbytes.Service.business.category;


import ch.swissbytes.Service.business.Service;
import ch.swissbytes.Service.business.brand.BrandDao;
import ch.swissbytes.Service.business.supplierProc.SupplierCategoryDao;
import ch.swissbytes.domain.model.entities.BrandEntity;
import ch.swissbytes.domain.model.entities.CategoryEntity;
import ch.swissbytes.domain.types.StatusEnum;

import javax.inject.Inject;
import javax.transaction.Transactional;
import java.io.Serializable;
import java.util.List;
import java.util.logging.Logger;

/**
 * Created by christian  on 11/05/15.
 */
public class CategoryService extends Service<CategoryEntity> implements Serializable{

    private static final Logger log = Logger.getLogger(CategoryService.class.getName());

    @Inject
    private CategoryDao categoryDao;

    @Inject
    private SupplierCategoryDao supplierCategoryDao;



    @Transactional
    public void doSave(CategoryEntity categoryEntity){
        log.info("doSave");
        categoryDao.doSave(categoryEntity);
    }

    @Transactional
    public void doUpdate(CategoryEntity detachedEntity){
        log.info("doUpdate");
        categoryDao.doUpdate(detachedEntity);
        if(detachedEntity.getStatus().ordinal() == StatusEnum.DELETED.ordinal()){
            supplierCategoryDao.doDeleteByCategoryId(detachedEntity.getId());
        }
    }

    public List<CategoryEntity> getCategoryList(){
        log.info("getCategoryList");
        return categoryDao.getCategoryList();
    }

    public List<CategoryEntity> findByName(final String name){
        log.info("findByName");
        String categoryName = name != null ? name : "";
        return categoryDao.findByName(categoryName);
    }

    public List<CategoryEntity> findByLikeName(final String name){
        log.info("findByLikeName");
        String categoryName = name != null ? name : "";
        return categoryDao.findByLikeName(categoryName);
    }

}
