package ch.swissbytes.Service.business.supplierProc;

import ch.swissbytes.Service.business.Service;
import ch.swissbytes.Service.business.contact.ContactService;
import ch.swissbytes.domain.model.entities.BrandEntity;
import ch.swissbytes.domain.model.entities.CategoryEntity;
import ch.swissbytes.domain.model.entities.ContactEntity;
import ch.swissbytes.domain.model.entities.SupplierProcEntity;
import ch.swissbytes.domain.types.StatusEnum;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.transaction.Transactional;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by alvaro on 9/22/14.
 */
public class SupplierProcService extends Service<SupplierProcEntity> implements Serializable {

    @Inject
    private SupplierProcDao dao;

    @Inject
    private SupplierCategoryDao supplierCategoryDao;

    @Inject
    private SupplierBrandDao supplierBrandDao;

    @Inject
    private ContactService contactService;

    @PostConstruct
    public void create() {
        super.initialize(dao);
    }


    @Override
    @Transactional
    public SupplierProcEntity save(SupplierProcEntity supplier) {
        supplier.setLastUpdate(new Date());
        supplier.setStatus(StatusEnum.ENABLE);
        supplier = super.save(supplier);
        supplierBrandDao.doSave(supplier.getBrands(), supplier);
        supplierCategoryDao.doSave(supplier.getCategories(), supplier);
        contactService.doSave(supplier.getContacts(), supplier);
        return supplier;
    }

    @Override
    @Transactional
    public SupplierProcEntity update(SupplierProcEntity supplier) {
        supplier.setLastUpdate(new Date());
        supplier = super.update(supplier);
        supplierBrandDao.doUpdate(supplier.getBrands(), supplier);
        supplierCategoryDao.doUpdate(supplier.getCategories(), supplier);
        contactService.doUpdate(supplier.getContacts(), supplier);
        return supplier;
    }

    public boolean isAlreadyBeingUsed(final String supplierId, Long id) {
        SupplierProcEntity supplier = dao.findSupplierBySupplierIdAndDistinct(supplierId, id);
        return supplier != null;
    }

    @Transactional
    public SupplierProcEntity doDelete(SupplierProcEntity supplier) {
        supplier = super.update(supplier);
        supplier.setStatus(StatusEnum.DELETED);
        supplierBrandDao.doDelete(supplier.getId());
        supplierCategoryDao.doDelete(supplier.getId());
        return super.update(supplier);
    }


    public SupplierProcEntity findById(Long id) {
        List<SupplierProcEntity> list = dao.findById(SupplierProcEntity.class, id);
        return !list.isEmpty() ? list.get(0) : null;
    }

    public List<CategoryEntity> getCategories(Long id) {
        return id != null && id > 0 ? supplierCategoryDao.findCategoriesBySupplier(id) : new ArrayList<CategoryEntity>();
    }

    public List<BrandEntity> getBrands(Long id) {
        return id != null && id > 0 ? supplierBrandDao.findBrandsBySupplier(id) : new ArrayList<BrandEntity>();
    }

    public List<ContactEntity> getContacts(Long id) {
        return id != null && id > 0 ? contactService.findByContactsBySupplier(id) : new ArrayList<ContactEntity>();
    }

    public List<SupplierProcEntity> findByCompany(final String company) {
        return dao.findByCompany(company);
    }

    public List<SupplierProcEntity> findAll() {
        List<SupplierProcEntity>suppliers=dao.findAll();
      /*  for(SupplierProcEntity supplier:suppliers){
            supplier.getContacts().addAll(contactService.findByContactsBySupplier(supplier.getId()));
        }*/
        return suppliers;
    }

    public List<String> findCountriesByCategory(final Long id){
        return dao.findCountriesByCategory(id);
    }
    public List<SupplierProcEntity> findSupplierByCountriesAndCategory(final Long id,final List<String> countries){
        return dao.findSupplierByCountriesAndCategory(id, countries);
    }
}
