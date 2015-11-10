package ch.swissbytes.procurement.report;


import ch.swissbytes.domain.model.entities.ProjectEntity;
import ch.swissbytes.fqmes.util.Configuration;
import java.util.Locale;
import java.util.Map;

/**
 * Created by christian on 11/06/14.
 */
public class ReportMaterialRequisition extends ReportProject {

    public ReportMaterialRequisition(String filenameJasper, String reportNameMsgKey, Map<String, String> messages, Locale locale,
                                     Configuration configuration, ProjectEntity project, final Map<String, Boolean> sortMap) {
        super(filenameJasper, reportNameMsgKey, messages, locale,configuration,project,sortMap);
    }

    protected void loadAdditionalParameters() {
        addParameters("PROJECT_ID", project.getId());
        addParameters("SUBREPORT_DIR","reports/procurement/MaterialRequisitions/");
    }

    protected String getStrSort() {
        Boolean poNo = sortMap.get("poNo");
        Boolean supplier = sortMap.get("supplier");
        Boolean deliveryDate = sortMap.get("deliveryDate");
        Boolean mrNo = sortMap.get("mrNo");
        String strSort = "";
        if (!poNo && !supplier && deliveryDate && !mrNo) {
            strSort = "po.po_delivery_date,po.orderedvariation, ";
            sortByName = "Delivery Date, Variation, ";
        } else if (poNo && !supplier && !deliveryDate && !mrNo) {
            strSort = "po.po,po.orderedvariation, ";
            sortByName = "PO No, Variation, ";
        } else if (!poNo && supplier && !deliveryDate && !mrNo) {
            strSort = "sp.company,po.orderedvariation, ";
            sortByName = "Supplier, Variation, ";
        } else if (!poNo && !supplier && !deliveryDate && mrNo){
            strSort = "sp.company,po.orderedvariation, ";
            sortByName = "Supplier, Variation, ";
        }

        if (strSort.length() > 1) {
            sortByName = sortByName.substring(0, sortByName.length() - 2);
            strSort = strSort.substring(0, strSort.length() - 2);
        } else {
            sortByName = "Variation";
            strSort = "po.orderedvariation";
        }
        return strSort;
    }


}
