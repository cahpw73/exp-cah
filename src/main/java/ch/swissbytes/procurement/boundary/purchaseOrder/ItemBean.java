package ch.swissbytes.procurement.boundary.purchaseOrder;

import ch.swissbytes.Service.business.enumService.EnumService;
import ch.swissbytes.Service.business.item.ItemService;
import ch.swissbytes.domain.model.entities.ItemEntity;
import ch.swissbytes.domain.types.StatusEnum;
import ch.swissbytes.fqmes.util.SortBean;
import org.apache.commons.lang.StringUtils;
import org.omnifaces.util.Messages;

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

    @Inject
    private PoBean poBean;


        private List<ItemEntity> itemList;

    private List<ItemEntity> scopeSupplyList;

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
            ItemEntity supply = new ItemEntity();
            supply.setId(preId);
            supply.startEditing();
            if(!scopeSupplyList.isEmpty()){
                ItemEntity firstItem = scopeSupplyList.get(0);
                supply.setCostCode(firstItem.getCostCode());
            }
            supply.setPoDeliveryDate(poBean.getPurchaseOrder().getPoDeliveryDate());
            scopeSupplyList.add(supply);
            preId--;
            sortBean.sortItemEntity(scopeSupplyList);
        }
    }

    public void loadItemList(final Long poEntityId) {
        log.info("loading item list to edit");
        scopeSupplyList = itemService.findByPoId(poEntityId);
        sortBean.sortItemEntity(scopeSupplyList);
    }

    public void copyDateToItemList(Date orderDate) {
        if (orderDate != null) {
            for (ItemEntity s : scopeSupplyList) {
                if (s.getIsEditable()) {
                    s.setPoDeliveryDate(orderDate);
                }
            }
        }
    }

    public void confirmItem(ItemEntity entity) throws Exception {
        log.info("confirm item");
        int index = scopeSupplyList.indexOf(entity);
        ItemEntity ss = index >= 0 ? scopeSupplyList.get(index) : null;
        if (ss == null) {
            throw new Exception("Invalid item");
        }
        if (validateItems(ss, true)) {
            ss.stopEditing();
        }
    }


    public void deleteItem(ItemEntity entity) {
        log.info("delete item");
        if (entity.getId() < 0L) {
            scopeSupplyList.remove(entity);
        } else {
            entity.setStatus(enumService.getStatusEnumDeleted());
        }
        sortBean.sortItemEntity(scopeSupplyList);
    }

    public void editItem(ItemEntity entity) {
        log.info("edit item");
        if (canEdit()) {
            entity.startEditing();
            entity.storeOldValue(entity);
            //sortBean.sortScopeSupplyEntity(scopeSupplyList);
        }
    }

    private boolean canEdit() {
        return rowsBeingEdited() == 0;
    }

    private Integer rowsBeingEdited() {
        Integer rows = 0;
        for (ItemEntity r : scopeSupplyList) {
            if (r.getIsEditable()) {
                rows++;
            }
        }
        return rows;
    }

    public void cancelEditionItem(ItemEntity entity) {
        log.info("cancel item");
        if (!itemNoIsNotEmpty(entity)) {
            scopeSupplyList.remove(entity);
        } else {
            entity.stopEditing();
            int index = scopeSupplyList.indexOf(entity);
            if (index >= 0) {
                scopeSupplyList.set(index, entity.getValueCloned());
                scopeSupplyList.get(index).stopEditing();
            }
        }
    }

    private boolean validateItems(ItemEntity scopeSupply, boolean showMessage) {
        boolean validated = true;
        if (StringUtils.isEmpty(scopeSupply.getCode()) && StringUtils.isBlank(scopeSupply.getCode())) {
            if (showMessage) {
                Messages.addGlobalError("Enter Item Code");
            }
            validated = false;
        }
        if (StringUtils.isEmpty(scopeSupply.getUnit()) && StringUtils.isBlank(scopeSupply.getUnit())) {
            if (showMessage) {
                Messages.addGlobalError("Enter Unit");
            }
            validated = false;
        }
        if (scopeSupply.getQuantity() == null) {
            if (showMessage) {
                Messages.addGlobalError("Enter a valid Quantity");
            }
            validated = false;
        }
        if (StringUtils.isEmpty(scopeSupply.getDescription()) && StringUtils.isBlank(scopeSupply.getDescription())) {
            if (showMessage) {
                Messages.addGlobalError("Enter description");
            }
            validated = false;
        }
        if (scopeSupply.getProjectCurrency() == null) {
            if (showMessage) {
                Messages.addGlobalError("Enter a valid currency");
            }
            validated = false;
        }
        if (scopeSupply.getCost() == null) {
            if (showMessage) {
                Messages.addGlobalError("Enter the cost");
            }
            validated = false;
        }
        if (scopeSupply.getPoDeliveryDate() == null) {
            if (showMessage) {
                Messages.addGlobalError("Enter the delivery date");
            }
            validated = false;
        }
        return validated;
    }

    public boolean hasNotStatusDeleted(ItemEntity entity) {
        if (entity != null && entity.getStatus() != null)
            return entity.getStatus().getId().intValue() != StatusEnum.DELETED.getId().intValue();
        else
            return true;
    }

  /*  private boolean noHasData(ItemEntity entity) {
        if (entity.getCostCode() == null && entity.getPoDeliveryDate() == null
                && (entity.getQuantity() == null)
                && (StringUtils.isEmpty(entity.getCode()) && StringUtils.isBlank(entity.getCode()))
                && (StringUtils.isEmpty(entity.getUnit()) && StringUtils.isBlank(entity.getUnit()))
                && (StringUtils.isEmpty(entity.getDescription()) && StringUtils.isBlank(entity.getDescription()))
                && entity.getTotalCost() == null && entity.getCost() == null
                && entity.getProjectCurrency() == null) {
            return true;
        }
        return false;
    }*/

    private boolean lastItemNoIsNotEmpty() {
        int index = scopeSupplyList.size();
        if (index > 0) {
            ItemEntity lastItem = scopeSupplyList.get(index - 1);
            return itemNoIsNotEmpty(lastItem);
        }
        return true;
    }

    private boolean itemNoIsNotEmpty(ItemEntity entity) {
        log.info("item is not empty");
        return StringUtils.isNotEmpty(entity.getCode()) && StringUtils.isNotBlank(entity.getCode());
    }

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

    public List<ItemEntity> getScopeSupplyList() {
        return scopeSupplyList;
    }

    public boolean isNotEmptyScopeSupplyList(){
        return !scopeSupplyList.isEmpty();
    }
}
