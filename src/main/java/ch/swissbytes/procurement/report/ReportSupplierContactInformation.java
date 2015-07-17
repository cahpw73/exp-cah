package ch.swissbytes.procurement.report;



import ch.swissbytes.domain.model.entities.ProjectEntity;
import ch.swissbytes.fqmes.util.Configuration;
import java.util.Locale;
import java.util.Map;

/**
 * Created by christian on 11/06/14.
 */
public class ReportSupplierContactInformation extends ReportProject {
    public ReportSupplierContactInformation(String filenameJasper, String reportNameMsgKey, Map<String, String> messages, Locale locale,
                                            Configuration configuration, ProjectEntity project, final Map<String, Boolean> sortMap) {
        super(filenameJasper, reportNameMsgKey, messages, locale,configuration,project,sortMap);

    }

    protected void loadAdditionalParameters() {
        addParameters("SUBREPORT_DIR","reports/procurement/supplierContactInformation/");
        addParameters("PROJECT_ID", project.getId());
    }

    protected String getStrSort(){
        Boolean poNo = sortMap.get("poNo");
        Boolean supplier = sortMap.get("supplier");
        String strSort = "";
        if(poNo){
            strSort = strSort+"po,";
        }
        if (supplier){
            strSort = strSort+"company,";
        }
        if(strSort.length()>1){
            strSort = strSort.substring(0,strSort.length() - 1);
        }
        if(strSort.isEmpty()){
            strSort ="id";
        }
        return strSort;
    }
}
