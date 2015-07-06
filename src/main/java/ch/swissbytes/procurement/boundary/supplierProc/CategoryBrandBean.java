package ch.swissbytes.procurement.boundary.supplierProc;

import ch.swissbytes.Service.business.brand.BrandService;
import ch.swissbytes.Service.business.category.CategoryService;
import ch.swissbytes.domain.model.entities.BrandEntity;
import ch.swissbytes.domain.model.entities.CategoryEntity;
import org.primefaces.model.DualListModel;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.ArrayList;
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


    @PostConstruct
    public void create() {
        log.info("CategoryBrandBean bean created");

    }


    public void restart(){
        //this.categories = new DualListModel<CategoryEntity>(categoryService.getCategoryList(),new ArrayList<>());
        //this.brands = new DualListModel<BrandEntity>(diffBrandList(brandService.getBrandList(),lb), lb);
    }
    public void addListLoaded(List<CategoryEntity> categories, List<BrandEntity> brands) {
        List<CategoryEntity>lc= categories != null ? categories : new ArrayList<CategoryEntity>();
        List<BrandEntity>lb= brands != null ? brands : new ArrayList<BrandEntity>();
        this.categories = new DualListModel<CategoryEntity>(diffCategoryList(categoryService.getCategoryList(), lc),lc);
        this.brands = new DualListModel<BrandEntity>(diffBrandList(brandService.getBrandList(),lb), lb);
    }

    private List<CategoryEntity> diffCategoryList(List<CategoryEntity> original, List<CategoryEntity> destiny) {
        for (CategoryEntity ce : destiny) {
            int index=original.indexOf(ce);
            if(index>=0){
                original.remove(index);
            }
        }
        return original;
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
}
