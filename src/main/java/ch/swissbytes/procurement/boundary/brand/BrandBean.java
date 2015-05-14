package ch.swissbytes.procurement.boundary.brand;

import ch.swissbytes.Service.business.brand.BrandService;
import ch.swissbytes.domain.model.entities.BrandEntity;
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
public class BrandBean implements Serializable {

    private static final Logger log = Logger.getLogger(BrandBean.class.getName());

    @Inject
    private BrandService brandService;

    private BrandEntity selectedBrand;

    private String brandName;

    private String searchNameBrand;

    private List<BrandEntity> brandList;


    @PostConstruct
    public void create(){
        log.info("created BrandBean");
        selectedBrand = new BrandEntity();
        loadBrands();
    }

    @PreDestroy
    public void destroy(){
        log.info("destroying BrandBean");
    }

    public void loadBrands(){
        brandList = brandService.getBrandList();
    }

    public void doSaveBrand(){
        if(StringUtils.isNotEmpty(brandName) && StringUtils.isNotBlank(brandName)){
            if(isValidBrand(brandName)) {
                BrandEntity currentBrand = new BrandEntity();
                currentBrand.setName(brandName);
                currentBrand.setLastUpdate(new Date());
                currentBrand.setStatus(StatusEnum.ENABLE);
                brandService.doSave(currentBrand);
                loadBrands();
                brandName = "";
            }else{
                Messages.addFlashError("nameBrand","Brand name already exists");
            }
        }else{
            Messages.addFlashError("nameBrand","Enter a valid Brand");
        }
    }

    private boolean isValidBrand(String brandName) {
        List<BrandEntity> brandList = brandService.findByName(brandName);
        return brandList.isEmpty();
    }

    public void doDeleteBrand(){
        selectedBrand.setStatus(StatusEnum.DELETED);
        selectedBrand.setLastUpdate(new Date());
        brandService.doUpdate(selectedBrand);
        loadBrands();
    }

    public void searchBrand(){
        log.info("searchBrand : " + searchNameBrand);
        if(StringUtils.isNotEmpty(searchNameBrand) && StringUtils.isNotBlank(searchNameBrand)){
            brandList.clear();
            brandList = brandService.findByLikeName(searchNameBrand);
            if (brandList.size() == 1 ){
                selectedBrand = brandList.get(0);
            }
        }else{
            brandList = brandService.getBrandList();
        }
    }

    public List<BrandEntity> getBrandList() {
        return brandList;
    }

    public String getBrandName() {
        return brandName;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }

    public BrandEntity getSelectedBrand() {
        return selectedBrand;
    }

    public void setSelectedBrand(BrandEntity selectedBrand) {
        this.selectedBrand = selectedBrand;
    }

    public String getSearchNameBrand() {
        return searchNameBrand;
    }

    public void setSearchNameBrand(String searchNameBrand) {
        this.searchNameBrand = searchNameBrand;
    }
}
