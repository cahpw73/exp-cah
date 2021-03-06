package ch.swissbytes.Service.business.supplierProc;

import ch.swissbytes.Service.business.category.CategoryDao;
import ch.swissbytes.Service.infrastructure.Filter;
import ch.swissbytes.Service.infrastructure.GenericDao;
import ch.swissbytes.domain.model.entities.CategoryEntity;
import ch.swissbytes.domain.model.entities.SupplierCategory;
import ch.swissbytes.domain.model.entities.SupplierProcEntity;
import ch.swissbytes.domain.types.StatusEnum;

import javax.inject.Inject;
import javax.persistence.Query;
import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by alvaro on 9/8/14.
 */


public class SupplierCategoryDao extends GenericDao<SupplierCategory> implements Serializable {

    @Inject
    private CategoryDao categoryDao;

    public void doSave(List<CategoryEntity> categories,SupplierProcEntity supplier) {
        Date now=new Date();
        for(CategoryEntity category:categories){
            SupplierCategory sc=new SupplierCategory();
            sc.setStatus(StatusEnum.ENABLE);
            sc.setLastUpdate(now);
            sc.setSupplier(supplier);
            sc.setCategory(categoryDao.findById(CategoryEntity.class,category.getId()).get(0));
            super.save(sc);
        }
    }

    public void doUpdate(List<CategoryEntity> categories,SupplierProcEntity supplier){
        List<CategoryEntity> oldCategories=findCategoriesBySupplier(supplier.getId());
        Date now=new Date();
        for(CategoryEntity category:oldCategories){
            if(categories.indexOf(category)<0){
                SupplierCategory sc=findByCategoryAndSupplier(category.getId(), supplier.getId());
                sc.setStatus(StatusEnum.DELETED);
                sc.setLastUpdate(now);
                super.update(sc);
            }
        }
        for(CategoryEntity category:categories){
            if(oldCategories.indexOf(category)<0) {
                SupplierCategory sc=new SupplierCategory();
                sc.setCategory(category);
                sc.setSupplier(supplier);
                sc.setStatus(StatusEnum.ENABLE);
                sc.setLastUpdate(now);
                super.save(sc);
            }
        }
    }
    public void  doDelete(Long id){
        StringBuilder sb = new StringBuilder();
        sb.append(" UPDATE SupplierCategory SET ");
        sb.append(" status=:DELETED , ");
        sb.append(" lastUpdate=:LAST_UPDATE ");
        sb.append(" WHERE status=:ENABLE ");
        sb.append(" AND supplier.id = :SUPPLIER_ID ");
        Map<String,Object> params = new HashMap<>();
        params.put("SUPPLIER_ID", id);
        params.put("LAST_UPDATE", new Date());
        params.put("ENABLE", StatusEnum.ENABLE);
        params.put("DELETED", StatusEnum.DELETED);
        super.executeUpdate(sb.toString(),params);
    }

    public void  doDeleteByCategoryId(Long id){
        StringBuilder sb = new StringBuilder();
        sb.append(" UPDATE SupplierCategory SET ");
        sb.append(" status=:DELETED , ");
        sb.append(" lastUpdate=:LAST_UPDATE ");
        sb.append(" WHERE status=:ENABLE ");
        sb.append(" AND category.id = :CATEGORY_ID ");
        Map<String,Object> params = new HashMap<>();
        params.put("CATEGORY_ID", id);
        params.put("LAST_UPDATE", new Date());
        params.put("ENABLE", StatusEnum.ENABLE);
        params.put("DELETED", StatusEnum.DELETED);
        super.executeUpdate(sb.toString(),params);
    }


    public List<CategoryEntity> findCategoriesBySupplier(final Long id){
        StringBuilder sb = new StringBuilder();
        sb.append(" SELECT sc.category ");
        sb.append(" FROM SupplierCategory sc ");
        sb.append(" WHERE sc.status=:ENABLE ");
        sb.append(" AND sc.supplier.id = :SUPPLIER_ID ");
        Map<String,Object> params = new HashMap<>();
        params.put("SUPPLIER_ID", id);
        params.put("ENABLE", StatusEnum.ENABLE);
        return super.findBy(sb.toString(), params);
    }
    public SupplierCategory findByCategoryAndSupplier(final Long categoryId,final Long supplierId){
        StringBuilder sb = new StringBuilder();
        sb.append(" SELECT sc ");
        sb.append(" FROM SupplierCategory sc ");
        sb.append(" WHERE sc.status=:ENABLE ");
        sb.append(" AND sc.category.id = :CATEGORY_ID ");
        sb.append(" AND sc.supplier.id = :SUPPLIER_ID ");
        Map<String,Object> params = new HashMap<>();
        params.put("SUPPLIER_ID", supplierId);
        params.put("CATEGORY_ID", categoryId);
        params.put("ENABLE", StatusEnum.ENABLE);
        List<SupplierCategory> sc =super.findBy(sb.toString(), params);
        return sc.isEmpty()?null:sc.get(0);
    }
    @Override
    protected void applyCriteriaValues(Query query, Filter filter) {

    }

    @Override
    protected String getEntity() {
        return null;
    }

    @Override
    protected String addCriteria(Filter filter) {
        return null;
    }
}
