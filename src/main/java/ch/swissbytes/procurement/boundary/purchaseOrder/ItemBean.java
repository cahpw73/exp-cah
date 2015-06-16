package ch.swissbytes.procurement.boundary.purchaseOrder;

import ch.swissbytes.Service.business.enumService.EnumService;
import ch.swissbytes.Service.business.item.ItemService;
import ch.swissbytes.domain.model.entities.ItemEntity;
import ch.swissbytes.domain.model.entities.ScopeSupplyEntity;
import ch.swissbytes.domain.model.entities.StatusEntity;
import ch.swissbytes.domain.types.StatusEnum;
import ch.swissbytes.fqmes.util.SortBean;
import org.apache.commons.lang.StringUtils;
import org.primefaces.event.ReorderEvent;

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
public class ItemBean implements Serializable {

    public static final Logger log = Logger.getLogger(ItemBean.class.getName());

    @Inject
    private ItemService itemService;

    @Inject
    private SortBean sortBean;

    @Inject
    private EnumService enumService;

    private List<ItemEntity> itemList;

    private List<ScopeSupplyEntity> scopeSupplyList;

    private Long preId = -1L;

    @PostConstruct
    public void create() {
        log.info("create itemBean");
        itemList = new ArrayList<>();
        scopeSupplyList = new ArrayList<>();
    }

    @PreDestroy
    public void destroy() {
        log.info("destroy itemBean");
    }

    public void addItem() {
        log.info("add Item");
        if (lastItemNoIsNotEmpty()) {
            ScopeSupplyEntity supply = new ScopeSupplyEntity();
            supply.setId(preId);
            supply.startEditing();
            scopeSupplyList.add(supply);
            preId--;
            sortBean.sortScopeSupplyEntity(scopeSupplyList);

            /*ItemEntity entity = new ItemEntity();
            entity.setId(preId);
            entity.startEditing();
            itemList.add(entity);
            preId--;
            sortBean.sortItemEntity(itemList);*/
        }
    }

    public void loadItemList(final Long poEntityId) {
        log.info("loading item list to edit");
        scopeSupplyList = itemService.findByPoId(poEntityId);
        //itemList = itemService.findByPoId(poEntityId);
        sortBean.sortScopeSupplyEntity(scopeSupplyList);
    }

    public void copyDateToItemList(Date orderDate) {
        if (orderDate != null) {
            for(ScopeSupplyEntity s : scopeSupplyList){
                s.setPoDeliveryDate(orderDate);
            }
            /*for (ItemEntity i : itemList) {
                i.setDeliveryDate(orderDate);
            }*/
        }
    }

    public void confirmItem(ScopeSupplyEntity entity) {
        log.info("confirm item");
        if(itemNoIsNotEmpty(entity)){
            int index = scopeSupplyList.indexOf(entity);
            scopeSupplyList.set(index,entity);
            entity.stopEditing();
        }
        /*if (itemNoIsNotEmpty(itemEntity)) {
            int index = itemList.indexOf(itemEntity);
            itemList.set(index, itemEntity);
            itemEntity.stopEditing();
        }*/
    }

    public void deleteItem(ScopeSupplyEntity entity) {
        log.info("delete item");
        if(entity.getId()< 0L){
            scopeSupplyList.remove(entity);
        }else{
            entity.setStatus(enumService.getStatusEnumDeleted());
        }
        sortBean.sortScopeSupplyEntity(scopeSupplyList);
        /*if (itemEntity.getId() < 0L) {
            itemList.remove(itemEntity);
        } else {
            itemEntity.setStatusEnum(StatusEnum.DELETED);
        }
        sortBean.sortItemEntity(itemList);*/
    }

    public void editItem(ScopeSupplyEntity entity) {
        log.info("edit item");
        entity.startEditing();
        entity.storeOldValue(entity);
        sortBean.sortScopeSupplyEntity(scopeSupplyList);
        /*itemEntity.startEditing();
        itemEntity.storeOldValue(itemEntity);
        sortBean.sortItemEntity(itemList);*/
    }

    public void cancelEditionItem(ScopeSupplyEntity entity) {
        log.info("cancel item");
        if(true){
            scopeSupplyList.remove(entity);
        }else{
            entity.stopEditing();
            entity = entity.getValueCloned();
        }
        sortBean.sortScopeSupplyEntity(scopeSupplyList);
        /*if (noHasData(itemEntity)) {
            itemList.remove(itemEntity);
        } else {
            itemEntity.stopEditing();
            itemEntity = (ItemEntity) itemEntity.getValueCloned();
        }
        sortBean.sortItemEntity(itemList);*/
    }

    public boolean hasNotStatusDeleted(ScopeSupplyEntity entity) {
        if (entity != null && entity.getStatus() != null)
            return entity.getStatus().getId().intValue() != StatusEnum.DELETED.getId().intValue();
        else
            return true;
    }

    /*public boolean hasNotStatusDeleted(ItemEntity itemEntity) {
        if (itemEntity != null && itemEntity.getStatusEnum() != null)
            return StatusEnum.DELETED.getId().intValue() != itemEntity.getStatusEnum().getId().intValue();
        else
            return true;
    }*/

    private boolean noHasData(ScopeSupplyEntity entity) {
        if (entity.getCostCode() == null && entity.getPoDeliveryDate() == null
                && (entity.getQuantity()  == null)
                && (StringUtils.isEmpty(entity.getCode()) && StringUtils.isBlank(entity.getCode()))
                && (StringUtils.isEmpty(entity.getUnit()) && StringUtils.isBlank(entity.getUnit()))
                && (StringUtils.isEmpty(entity.getDescription()) && StringUtils.isBlank(entity.getDescription()))
                && entity.getTotalCost() == null && entity.getCost() == null
                && entity.getProjectCurrency() == null) {
            return true;
        }
        return false;
    }

    /*private boolean noHasData(ItemEntity itemEntity) {
        if (itemEntity.getCostCode() == null && itemEntity.getDeliveryDate() == null
                && (StringUtils.isEmpty(itemEntity.getItemNo()) && StringUtils.isBlank(itemEntity.getItemNo()))
                && (StringUtils.isEmpty(itemEntity.getQty()) && StringUtils.isBlank(itemEntity.getQty()))
                && (StringUtils.isEmpty(itemEntity.getUnit()) && StringUtils.isBlank(itemEntity.getUnit()))
                && (StringUtils.isEmpty(itemEntity.getDescription()) && StringUtils.isBlank(itemEntity.getDescription()))
                && itemEntity.getTotalCost() == null && itemEntity.getUnitCost() == null
                && itemEntity.getProjectCurrency() == null) {
            return true;
        }
        return false;
    }*/

    private boolean lastItemNoIsNotEmpty() {
        int index = scopeSupplyList.size();
        if (index > 0) {
            ScopeSupplyEntity lastItem = scopeSupplyList.get(index - 1);
            return itemNoIsNotEmpty(lastItem);
            /*ItemEntity lastItem = itemList.get(index - 1);
            return itemNoIsNotEmpty(lastItem);*/
        }
        return true;
    }

    /*private boolean lastItemNoIsNotEmpty() {
        int index = itemList.size();
        if (index > 0) {
            ItemEntity lastItem = itemList.get(index - 1);
            return itemNoIsNotEmpty(lastItem);
        }
        return true;
    }*/

    private boolean itemNoIsNotEmpty(ScopeSupplyEntity entity) {
        log.info("item is not empty");
        return StringUtils.isNotEmpty(entity.getCode()) && StringUtils.isNotBlank(entity.getCode());
        //return StringUtils.isNotEmpty(entity.getItemNo()) && StringUtils.isNotBlank(entity.getItemNo());
    }

    /*private boolean itemNoIsNotEmpty(ItemEntity entity) {
        log.info("item is not empty");
        return StringUtils.isNotEmpty(entity.getItemNo()) && StringUtils.isNotBlank(entity.getItemNo());
    }*/

    public List<ItemEntity> getItemList() {
        return itemList;
    }

    public List<String> items() {
        List<String> list = new ArrayList<>();
        for (ItemEntity item : itemList) {
            if (StringUtils.isNotEmpty(item.getDescription()) && StringUtils.isNotBlank(item.getDescription())) {
                list.add(item.getDescription().trim());
            }
        }
        Collections.sort(list);
        return list;
    }

    public List<ScopeSupplyEntity> getScopeSupplyList() {
        return scopeSupplyList;
    }
}
