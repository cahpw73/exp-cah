package ch.swissbytes.procurement.boundary.purchaseOrder;

import ch.swissbytes.Service.business.project.ProjectService;
import ch.swissbytes.Service.business.purchase.PurchaseOrderService;
import ch.swissbytes.domain.model.entities.ItemEntity;
import ch.swissbytes.domain.model.entities.POEntity;
import ch.swissbytes.domain.model.entities.ProjectEntity;
import ch.swissbytes.domain.model.entities.PurchaseOrderEntity;
import ch.swissbytes.procurement.boundary.Bean;
import org.apache.commons.beanutils.BeanUtils;
import org.primefaces.event.RowEditEvent;

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
 * Created by Christian 02/06/2015
 */

@Named
@ViewScoped
public class ItemBean  implements Serializable {

    public static final Logger log = Logger.getLogger(ItemBean.class.getName());

    private List<ItemEntity> itemList;

    @PostConstruct
    public void create(){
        log.info("create itemBean");
        itemList = new ArrayList<>();
    }

    @PreDestroy
    public void destroy(){
        log.info("destroy itemBean");
    }

    public void addItem(){
        log.info("add Item");
        ItemEntity entity = new ItemEntity();
        entity.startEditing();
        itemList.add(entity);
    }

    public void confirmItem(ItemEntity itemEntity){
        log.info("confirm item");
        itemEntity.stopEditing();
    }

    public void deleteItem(ItemEntity itemEntity){
        log.info("delete item");
        itemList.remove(itemEntity);
    }

    public void editItem(ItemEntity itemEntity){
        log.info("edit item");
        itemEntity.startEditing();
        itemEntity.storeOldValue(itemEntity);
    }

    public void cancelEditionItem(ItemEntity itemEntity){
        log.info("cancel item");
        if(noHasData(itemEntity)){
            itemList.remove(itemEntity);
        }else{
            itemEntity.stopEditing();
            itemEntity = (ItemEntity) itemEntity.getValueCloned();
        }

    }

    private boolean noHasData(ItemEntity itemEntity) {
        if(itemEntity.getCostCode() == null && itemEntity.getDeliveryDate() == null
                && itemEntity.getDescription() == null && itemEntity.getItemNo() == null && itemEntity.getQty() == null
                && itemEntity.getTotalCost() == null && itemEntity.getUnit() == null && itemEntity.getUnitCost() == null
                && itemEntity.getProjectCurrency() == null ){
            return true;
        }
        return false;
    }

    public List<ItemEntity> getItemList() {
        return itemList;
    }

}
