package ch.swissbytes.procurement.boundary.purchaseOrder;

import ch.swissbytes.Service.infrastructure.Filter;

/**
 * Created by alvaro on 8/4/2015.
 */
public class FilterPO extends Filter {

    private Long projectId;

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public void clear(){
        super.clean();;
        projectId=null;
    }
}
