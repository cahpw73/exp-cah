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
        addParameters("sortMrNo",getStrSortMrNo());
        addParameters("sortByName", sortByName);
    }

    private String getStrSortMrNo(){
        Boolean mrNo = sortMap.get("mrNo");
        String strSort = "";
        if (mrNo){
            strSort = "requisition_number, ";
            sortByName = "Mr No, Variation, ";
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
