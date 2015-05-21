package ch.swissbytes.Service.business.supplierProc;

import ch.swissbytes.Service.infrastructure.Filter;
import ch.swissbytes.Service.infrastructure.GenericDao;
import ch.swissbytes.domain.model.entities.*;
import ch.swissbytes.domain.types.StatusEnum;

import javax.persistence.Query;
import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by alvaro on 9/8/14.
 */


public class SupplierBrandDao extends GenericDao<SupplierBrand> implements Serializable {

    public void doSave(List<BrandEntity> brands,SupplierProcEntity supplier) {
        Date now=new Date();
        for(BrandEntity brand:brands){
            SupplierBrand sc=new SupplierBrand();
            sc.setStatus(StatusEnum.ENABLE);
            sc.setLastUpdate(now);
            sc.setSupplier(supplier);
            sc.setBrand(brand);
            super.save(brand);
        }
    }

    public void doUpdate(List<BrandEntity> brands,SupplierProcEntity supplier){
        List<BrandEntity> oldBrands=findBrandsBySupplier(supplier.getId());
        Date now=new Date();
        for(BrandEntity brand:oldBrands){
            if(brands.indexOf(brand)<0){
                SupplierBrand supplierBrand=findByBrandAndSupplier(brand.getId(), supplier.getId());
                supplierBrand.setStatus(StatusEnum.DELETED);
                supplierBrand.setLastUpdate(now);
                super.update(supplierBrand);
            }
        }
        for(BrandEntity brand:brands){
            if(oldBrands.indexOf(brand)<0) {
                SupplierBrand supplierBrand=new SupplierBrand();
                supplierBrand.setBrand(brand);
                supplierBrand.setSupplier(supplier);
                supplierBrand.setStatus(StatusEnum.ENABLE);
                supplierBrand.setLastUpdate(now);
                super.save(supplierBrand);
            }
        }
    }



    public List<BrandEntity> findBrandsBySupplier(final Long id){
        StringBuilder sb = new StringBuilder();
        sb.append(" SELECT sb.brand ");
        sb.append(" FROM SupplierBrand sb ");
        sb.append(" WHERE sb.status=:ENABLE ");
        sb.append(" AND sb.supplier.id = :SUPPLIER_ID ");
        Map<String,Object> params = new HashMap<>();
        params.put("SUPPLIER_ID", id);
        params.put("ENABLE", StatusEnum.ENABLE);
        return super.findBy(sb.toString(), params);
    }
    public SupplierBrand findByBrandAndSupplier(final Long brandId,final Long supplierId){
        StringBuilder sb = new StringBuilder();
        sb.append(" SELECT sb ");
        sb.append(" FROM SupplierBrand sb ");
        sb.append(" WHERE sb.status=:ENABLE ");
        sb.append(" AND sb.brand.id = :BRAND_ID ");
        sb.append(" AND sb.supplier.id = :SUPPLIER_ID ");
        Map<String,Object> params = new HashMap<>();
        params.put("SUPPLIER_ID", supplierId);
        params.put("BRAND_ID", brandId);
        params.put("ENABLE", StatusEnum.ENABLE);
        List<SupplierBrand> supplierBrands =super.findBy(sb.toString(), params);
        return supplierBrands.isEmpty()?null:supplierBrands.get(0);
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
