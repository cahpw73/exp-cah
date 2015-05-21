package ch.swissbytes.Service.business.supplierProc;

import ch.swissbytes.Service.business.Service;
import ch.swissbytes.Service.business.contact.ContactDao;
import ch.swissbytes.domain.model.entities.BrandEntity;
import ch.swissbytes.domain.model.entities.CategoryEntity;
import ch.swissbytes.domain.model.entities.ContactEntity;
import ch.swissbytes.domain.model.entities.SupplierProcEntity;
import org.apache.commons.lang.ArrayUtils;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by alvaro on 9/22/14.
 */
public class SupplierProcService extends Service<SupplierProcEntity> implements Serializable{

    @Inject
     private SupplierProcDao dao;

    @Inject
    private SupplierCategoryDao supplierCategoryDao;

    @Inject
    private SupplierBrandDao supplierBrandDao;

    @Inject
    private ContactDao contactDao;

    @PostConstruct
    public void create() {
        super.initialize(dao);
    }

    public SupplierProcEntity findById(Long id){
        List<SupplierProcEntity>list=dao.findById(SupplierProcEntity.class,id);
        return list.isEmpty()?list.get(0):null;
    }

    public List<CategoryEntity> getCategories(Long id){
        return id!=null&&id>0?supplierCategoryDao.findCategoriesBySupplier(id): new ArrayList<CategoryEntity>();
    }
    public List<BrandEntity> getBrands(Long id){
        return id!=null&&id>0?supplierBrandDao.findBrandsBySupplier(id) : new ArrayList<BrandEntity>();
    }

    public List<ContactEntity> getContacts(Long id){
        return id!=null&&id>0?contactDao.findByContactsBySupplier(id) : new ArrayList<ContactEntity>();
    }

}
