package ch.swissbytes.procurement.boundary.category;

import ch.swissbytes.Service.business.category.CategoryService;
import ch.swissbytes.domain.model.entities.BrandEntity;
import ch.swissbytes.domain.model.entities.CategoryEntity;
import ch.swissbytes.domain.types.StatusEnum;
import org.apache.commons.lang.StringUtils;
import org.omnifaces.util.Messages;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

/**
 * Created by alvaro on 9/15/14.
 */
@Named
@ViewScoped
public class CategoryBean implements Serializable {

    private static final Logger log = Logger.getLogger(CategoryBean.class.getName());

    @Inject
    private CategoryService categoryService;

    private CategoryEntity selectedCategory;

    private String categoryName;

    private String searchNameCategory;

    private List<CategoryEntity> categoryList;


    @PostConstruct
    public void create(){
        log.info("created CategoryBean");
        loadCategories();
    }

    @PreDestroy
    public void destroy(){
        log.info("destroying CategoryBean");
    }

    public void loadCategories(){
        categoryList = categoryService.getCategoryList();
    }

    public void doSaveCategory(){
        if(StringUtils.isNotEmpty(categoryName) && StringUtils.isNotBlank(categoryName)){
            if(isValidCategory(categoryName)) {
                CategoryEntity currentCategory = new CategoryEntity();
                currentCategory.setName(categoryName);
                currentCategory.setLastUpdate(new Date());
                currentCategory.setStatus(StatusEnum.ENABLE);
                categoryService.doSave(currentCategory);
                loadCategories();
                categoryName = "";
            }else{
                Messages.addFlashError("nameCategory","Category name already exists");
            }
        }else{
            Messages.addFlashError("nameCategory","Enter a valid Category");
        }
    }

    private boolean isValidCategory(String categoryName) {
        List<CategoryEntity> categoryList = categoryService.findByName(categoryName);
        return categoryList.isEmpty();
    }

    public void doDeleteCategory(){
        if(selectedCategory != null) {
            selectedCategory.setStatus(StatusEnum.DELETED);
            selectedCategory.setLastUpdate(new Date());
            categoryService.doUpdate(selectedCategory);
            loadCategories();
            selectedCategory = null;
        }else{
            Messages.addFlashError("categoryList","Select a category first");
        }

    }

    public void searchCategory(){
        log.info("searchCategory : " + searchNameCategory);
        if(StringUtils.isNotEmpty(searchNameCategory) && StringUtils.isNotBlank(searchNameCategory)){
            categoryList.clear();
            categoryList = categoryService.findByLikeName(searchNameCategory);
            if (categoryList.size() == 1 ){
                selectedCategory = categoryList.get(0);
            }
        }else{
            categoryList = categoryService.getCategoryList();
        }
    }

    public CategoryEntity getSelectedCategory() {
        return selectedCategory;
    }

    public void setSelectedCategory(CategoryEntity selectedCategory) {
        this.selectedCategory = selectedCategory;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getSearchNameCategory() {
        return searchNameCategory;
    }

    public void setSearchNameCategory(String searchNameCategory) {
        this.searchNameCategory = searchNameCategory;
    }

    public List<CategoryEntity> getCategoryList() {
        return categoryList;
    }


}
