package ch.swissbytes.procurement.report.dtos;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by Christian on 23/06/2015.
 */
public class ProjectProcurementDto implements Serializable {
    public String po;
    public String variation;
    public Date orderDate;
    public String company;
    public String title;
    public String currency;
    public Date poDeliveryDate;
    public String poStatus;
}
