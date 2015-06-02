package ch.swissbytes.procurement.boundary.purchaseOrder;

import ch.swissbytes.Service.business.project.ProjectService;
import ch.swissbytes.Service.business.purchase.PurchaseOrderService;
import ch.swissbytes.domain.model.entities.ItemEntity;
import ch.swissbytes.domain.model.entities.POEntity;
import ch.swissbytes.domain.model.entities.ProjectEntity;
import ch.swissbytes.domain.model.entities.PurchaseOrderEntity;
import ch.swissbytes.procurement.boundary.Bean;
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
public class ItemBean extends Bean implements Serializable {

    public static final Logger log = Logger.getLogger(ItemBean.class.getName());

    private List<ItemEntity> itemList;

    @Override
    public void create(){
        log.info("create itemBean");
        itemList = new ArrayList<>();
    }

    @Override
    public void destroy(){
        log.info("destroy itemBean");
    }

    public void addSupplier(){
        ItemEntity entity = new ItemEntity();
        itemList.add(entity);
    }

    public List<ItemEntity> getItemList() {
        return itemList;
    }

    public void onRowEdit(RowEditEvent event) {

    }

    public void onRowCancel(RowEditEvent event) {

    }

}
