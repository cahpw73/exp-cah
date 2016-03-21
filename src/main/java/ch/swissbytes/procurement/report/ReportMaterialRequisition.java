package ch.swissbytes.procurement.report;


import ch.swissbytes.domain.model.entities.ProjectEntity;
import ch.swissbytes.fqmes.util.Configuration;

import javax.sql.DataSource;
import java.util.Locale;
import java.util.Map;

/**
 * Created by christian on 11/06/14.
 */
public class ReportMaterialRequisition extends ReportProject {

    private boolean showMaterialOriginal;

    public ReportMaterialRequisition(String filenameJasper, String reportNameMsgKey, Map<String, String> messages, Locale locale,
                                     Configuration configuration, ProjectEntity project, final Map<String, Boolean> sortMap,DataSource ds) {
        super(filenameJasper, reportNameMsgKey, messages, locale,configuration,project,sortMap,ds);
    }

    protected void loadAdditionalParameters() {
        showMaterialOriginal = true;
        addParameters("PROJECT_ID", project.getId());
        addParameters("SUBREPORT_DIR","reports/procurement/MaterialRequisitions/");
        addParameters("sortMrNo",getStrSortMrNo());
        addParameters("materialRequisitionOriginal",showMaterialOriginal);
        addParameters("sortByName", sortByName);
    }

    private String getStrSortMrNo(){
        boolean mrNo = sortMap.get("mrNo");
        boolean rtfNo = sortMap.get("rtfNo");
        boolean originator = sortMap.get("originator");
        String strSort = "";
        if (mrNo){
            strSort = "requisition_number,po,orderedvariation, ";
            sortByName = "Mr No, Variation, ";
            showMaterialOriginal = false;
        }else if (rtfNo){
            strSort = "rtf_no,po,orderedvariation, ";
            sortByName = "Rtf No, Variation, ";
            showMaterialOriginal = false;
        }else if (originator){
            strSort = "originator,po,orderedvariation, ";
            sortByName = "Originator, Variation, ";
            showMaterialOriginal = false;
        }
        if (strSort.length() > 1) {
            strSort = strSort.substring(0, strSort.length() - 2);
            sortByName = sortByName.substring(0, sortByName.length() - 2);
        } else {
            strSort = "id";
        }
        return strSort;
    }
}
