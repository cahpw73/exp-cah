package ch.swissbytes.procurement.boundary.purchaseOrder;

import ch.swissbytes.Service.business.item.ItemService;
import ch.swissbytes.domain.model.entities.ItemEntity;
import org.apache.commons.lang.StringUtils;

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
 * Created by Christian 02/06/2015
 */

@Named
@ViewScoped
public class ItemBean  implements Serializable {

    public static final Logger log = Logger.getLogger(ItemBean.class.getName());

    @Inject
    private ItemService itemService;

    private List<ItemEntity> itemList;

    private Long preId = -1L;

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
        if(lastItemNoIsNotEmpty()){
            ItemEntity entity = new ItemEntity();
            entity.setId(preId);
            entity.startEditing();
            itemList.add(entity);
            preId--;
        }
        log.info("item list size : " + itemList.size());
    }

    public void loadItemList(final Long poEntityId){
        log.info("loading item list to edit");
       itemList =  itemService.findByPoId(poEntityId);
    }

    private boolean lastItemNoIsNotEmpty() {
        int index = itemList.size();
        if(index > 0) {
            ItemEntity lastItem = itemList.get(index - 1);
            return itemNoIsNotEmpty(lastItem);
        }
        return true;
    }
    private  boolean itemNoIsNotEmpty(ItemEntity entity){
        log.info("item is not empty");
        return StringUtils.isNotEmpty(entity.getItemNo()) && StringUtils.isNotBlank(entity.getItemNo());
    }

    public void copyDateToItemList(Date orderDate){
        if(orderDate != null){
            for (ItemEntity i : itemList){
                i.setDeliveryDate(orderDate);
            }
        }
    }

    public void confirmItem(ItemEntity itemEntity){
        log.info("confirm item");
        if(itemNoIsNotEmpty(itemEntity)){
            int index=itemList.indexOf(itemEntity);
            itemList.set(index,itemEntity);
            itemEntity.stopEditing();
        }

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

    public List<String> items(){
        List<String>list=new ArrayList<>();
        for(ItemEntity item:itemList){
            if(StringUtils.isNotEmpty(item.getDescription())&&StringUtils.isNotBlank(item.getDescription())) {
                list.add(item.getDescription().trim());
            }
        }
        Collections.sort(list);
        return list;
    }

}
