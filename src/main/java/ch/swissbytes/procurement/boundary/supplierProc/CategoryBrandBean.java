package ch.swissbytes.procurement.boundary.supplierProc;

import ch.swissbytes.Service.business.brand.BrandService;
import ch.swissbytes.Service.business.category.CategoryService;
import ch.swissbytes.domain.model.entities.BrandEntity;
import ch.swissbytes.domain.model.entities.CategoryEntity;
import ch.swissbytes.domain.types.StatusEnum;
import org.apache.commons.lang.StringUtils;
import org.omnifaces.util.Messages;
import org.primefaces.context.RequestContext;
import org.primefaces.event.TransferEvent;
import org.primefaces.model.DualListModel;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

/**
 * Created by alvaro on 5/19/2015.
 */
@Named
@ViewScoped
public class CategoryBrandBean implements Serializable {

    private static final Logger log = Logger.getLogger(CategoryBrandBean.class.getName());

    @Inject
    private CategoryService categoryService;

    @Inject
    private BrandService brandService;

    private DualListModel<CategoryEntity> categories;

    private DualListModel<BrandEntity> brands;

    private CategoryEntity categoryEntity;

    private BrandEntity brandEntity;
    private List<BrandEntity>brandList;
    private List<CategoryEntity>categoryList;


    @PostConstruct
    public void create() {
        log.info("CategoryBrandBean bean created");
        categoryEntity = new CategoryEntity();
        brandEntity = new BrandEntity();
        categoryList=categoryService.getCategoryList();
        brandList=brandService.getBrandList();
    }

    public CategoryEntity findCategoryByName(String name){
        CategoryEntity target=null;
        for(CategoryEntity categoryEntity:categoryList){
            if(name.equalsIgnoreCase("AACategory")) {
                if (categoryEntity.getName().equalsIgnoreCase(name)) {
                    target = categoryEntity;
                    break;
                }
            }
        }
        return target;
    }


    public BrandEntity findBrandByName(String name){
        BrandEntity target=null;
        for(BrandEntity brandEntity:brandList){
            if(brandEntity.getName().equalsIgnoreCase(name)){
                target=brandEntity;
                break;
            }
        }
        return target;
    }

    public void sortCategoryList(TransferEvent transferEvent){
        if(transferEvent.isRemove()){
            Collections.sort(categories.getSource());
        }
    }
    public void sortBrandList(TransferEvent transferEvent){
        if(transferEvent.isRemove()){
            Collections.sort(brands.getSource());
        }
    }

    public void restart(){
        this.categories = new DualListModel<CategoryEntity>(categoryList,new ArrayList<CategoryEntity>());
        this.brands = new DualListModel<BrandEntity>(brandList,new ArrayList<BrandEntity>());
    }
    public void addListLoaded(List<CategoryEntity> categories, List<BrandEntity> brands) {
        List<CategoryEntity>lc= categories != null ? categories : new ArrayList<CategoryEntity>();
        List<BrandEntity>lb= brands != null ? brands : new ArrayList<BrandEntity>();
        this.categories = new DualListModel<CategoryEntity>(diffCategoryList(categoryList, lc),lc);
        this.brands = new DualListModel<BrandEntity>(diffBrandList(brandList,lb), lb);
    }

    public void doSaveBrand(){
        if(StringUtils.isNotEmpty(brandEntity.getName())){
            if(isValidBrand(brandEntity.getName())) {
                brandEntity.setLastUpdate(new Date());
                brandEntity.setStatus(StatusEnum.ENABLE);
                brandService.doSave(brandEntity);
                addNewBrandPicketList(brandEntity);
                RequestContext context = RequestContext.getCurrentInstance();
                context.execute("PF('brandModal').hide();");
            }else{
                Messages.addFlashError("nameBrandId","Brand name already exists");
            }
        }else{
            Messages.addFlashError("nameBrandId","Enter a valid Brand");
        }
    }
    private boolean isValidBrand(String brandName) {
        List<BrandEntity> brandList = brandService.findByName(brandName);
        return brandList.isEmpty();
    }

    public void doSaveCategory(){
        if(StringUtils.isNotEmpty(categoryEntity.getName())){
            if(isValidCategory(categoryEntity.getName())) {
                categoryEntity.setLastUpdate(new Date());
                categoryEntity.setStatus(StatusEnum.ENABLE);
                categoryService.doSave(categoryEntity);
                addNewCategoryToPicketList(categoryEntity);
                RequestContext context = RequestContext.getCurrentInstance();
                context.execute("PF('categoryModal').hide();");
            }else{
                Messages.addFlashError("nameCategoryId","Category name already exists");
            }
        }else{
            Messages.addFlashError("nameCategory","Enter a valid Category");
        }
    }

    private boolean isValidCategory(String categoryName) {
        List<CategoryEntity> categoryList = categoryService.findByName(categoryName);
        return categoryList.isEmpty();
    }

    public void resetCategory(){
        categoryEntity = new CategoryEntity();
    }

    public void resetBrand(){
        brandEntity = new BrandEntity();
    }

    private void addNewBrandPicketList(final BrandEntity brand){
        brands.getTarget().add(brand);
    }

    private void addNewCategoryToPicketList(final CategoryEntity category){
        categories.getTarget().add(category);
    }

    private List<CategoryEntity> diffCategoryList(List<CategoryEntity> source, List<CategoryEntity> target) {
        for (CategoryEntity ce : target) {
            int index=source.indexOf(ce);
            if(index>=0){
                source.remove(index);
            }
        }
        return source;
    }
    private List<BrandEntity> diffBrandList(List<BrandEntity> original, List<BrandEntity> destiny) {
        for (BrandEntity be : destiny) {
            int index=original.indexOf(be);
            if(index>=0){
                original.remove(index);
            }
        }
        return original;
    }
    @PreDestroy
    public void destroy() {
        log.info("CategoryBrandBean bean destroyed");
    }

    public DualListModel<CategoryEntity> getCategories() {
        return categories;
    }

    public DualListModel<BrandEntity> getBrands() {
        return brands;
    }

    public void setBrands(DualListModel<BrandEntity> brands) {
        this.brands = brands;
    }

    public void setCategories(DualListModel<CategoryEntity> categories) {
        this.categories = categories;
    }

    public CategoryEntity getCategoryEntity() {
        return categoryEntity;
    }

    public void setCategoryEntity(CategoryEntity categoryEntity) {
        this.categoryEntity = categoryEntity;
    }

    public BrandEntity getBrandEntity() {
        return brandEntity;
    }

    public void setBrandEntity(BrandEntity brandEntity) {
        this.brandEntity = brandEntity;
    }

    public List<BrandEntity> getBrandList() {
        return brandList;
    }

    public void setBrandList(List<BrandEntity> brandList) {
        this.brandList = brandList;
    }

    public List<CategoryEntity> getCategoryList() {
        return categoryList;
    }

    public void setCategoryList(List<CategoryEntity> categoryList) {
        this.categoryList = categoryList;
    }
}
