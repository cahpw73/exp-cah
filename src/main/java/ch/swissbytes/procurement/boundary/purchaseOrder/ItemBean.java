package ch.swissbytes.procurement.boundary.purchaseOrder;

import ch.swissbytes.Service.business.enumService.EnumService;
import ch.swissbytes.Service.business.item.ItemService;
import ch.swissbytes.domain.interfaces.RecordEditable;
import ch.swissbytes.domain.model.entities.ItemEntity;
import ch.swissbytes.domain.model.entities.ScopeSupplyEntity;
import ch.swissbytes.domain.types.StatusEnum;
import ch.swissbytes.fqmes.util.SortBean;
import ch.swissbytes.procurement.boundary.BeanEditableList;
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
        }
    }

    public void loadItemList(final Long poEntityId) {
        log.info("loading item list to edit");
        scopeSupplyList = itemService.findByPoId(poEntityId);
        sortBean.sortScopeSupplyEntity(scopeSupplyList);
    }

    public void copyDateToItemList(Date orderDate) {
        if (orderDate != null) {
            for(ScopeSupplyEntity s : scopeSupplyList){
                if(s.getIsEditable()) {
                    s.setPoDeliveryDate(orderDate);
                }
            }
        }
    }

    public void confirmItem(ScopeSupplyEntity entity) throws Exception {
        log.info("confirm item");
        int index=scopeSupplyList.indexOf(entity);
        ScopeSupplyEntity ss=index>=0?scopeSupplyList.get(index):null;
         if(ss==null){
             throw new Exception("Invalid item");
         }
        if(validateItems(ss,true)){
            ss.stopEditing();
        }
    }



    public void deleteItem(ScopeSupplyEntity entity) {
        log.info("delete item");
        if(entity.getId()< 0L){
            scopeSupplyList.remove(entity);
        }else{
            entity.setStatus(enumService.getStatusEnumDeleted());
        }
        sortBean.sortScopeSupplyEntity(scopeSupplyList);
    }

    public void editItem(ScopeSupplyEntity entity) {
        log.info("edit item");
        if(canEdit()) {
            entity.startEditing();
            entity.storeOldValue(entity);
            //sortBean.sortScopeSupplyEntity(scopeSupplyList);
        }
    }

    private boolean canEdit(){
        return rowsBeingEdited()==0;
    }

    private Integer rowsBeingEdited() {
        Integer rows = 0;
        for (ScopeSupplyEntity r : scopeSupplyList) {
            if (r.getIsEditable()) {
                rows++;
            }
        }
        return rows;
    }

    public void cancelEditionItem(ScopeSupplyEntity entity) {
        log.info("cancel item");
        if(!itemNoIsNotEmpty(entity)){
            scopeSupplyList.remove(entity);
        }else{
            entity.stopEditing();
            entity = entity.getValueCloned();
        }
        sortBean.sortScopeSupplyEntity(scopeSupplyList);
    }
    private boolean validateItems(ScopeSupplyEntity scopeSupply,boolean showMessage){
        boolean validated=true;
        if(StringUtils.isEmpty(scopeSupply.getCode())&&StringUtils.isBlank(scopeSupply.getCode())){
            if(showMessage) {
                Messages.addGlobalError("Enter Item Code");
            }
            validated=false;
        }
        if(StringUtils.isEmpty(scopeSupply.getUnit())&&StringUtils.isBlank(scopeSupply.getUnit())){
            if(showMessage) {
                Messages.addGlobalError("Enter Unit");
            }
            validated=false;
        }
        if(scopeSupply.getQuantity()==null){
            if(showMessage) {
                Messages.addGlobalError("Enter a valid Quantity");
            }
            validated=false;
        }
        if(StringUtils.isEmpty(scopeSupply.getDescription())&&StringUtils.isBlank(scopeSupply.getDescription())){
            if(showMessage) {
                Messages.addGlobalError("Enter description");
            }
            validated=false;
        }
        if(scopeSupply.getProjectCurrency()==null){
            if(showMessage) {
                Messages.addGlobalError("Enter a valid currency");
            }
            validated=false;
        }
        if(scopeSupply.getCost()==null){
            if(showMessage) {
                Messages.addGlobalError("Enter the cost");
            }
            validated=false;
        }
        if(scopeSupply.getPoDeliveryDate()==null){
            if(showMessage) {
                Messages.addGlobalError("Enter the delivery date");
            }
            validated=false;
        }
        return validated;
    }
    public boolean hasNotStatusDeleted(ScopeSupplyEntity entity) {
        if (entity != null && entity.getStatus() != null)
            return entity.getStatus().getId().intValue() != StatusEnum.DELETED.getId().intValue();
        else
            return true;
    }

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

    private boolean lastItemNoIsNotEmpty() {
        int index = scopeSupplyList.size();
        if (index > 0) {
            ScopeSupplyEntity lastItem = scopeSupplyList.get(index - 1);
            return itemNoIsNotEmpty(lastItem);
        }
        return true;
    }
    private boolean itemNoIsNotEmpty(ScopeSupplyEntity entity) {
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

    public List<ScopeSupplyEntity> getScopeSupplyList() {
        return scopeSupplyList;
    }
}
