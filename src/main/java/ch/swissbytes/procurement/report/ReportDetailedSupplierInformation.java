package ch.swissbytes.procurement.report;


import ch.swissbytes.domain.model.entities.ProjectEntity;
import ch.swissbytes.fqmes.util.Configuration;

import javax.sql.DataSource;
import java.util.Locale;
import java.util.Map;

/**
 * Created by christian on 11/06/14.
 */
public class ReportDetailedSupplierInformation extends ReportProject {

    public ReportDetailedSupplierInformation(String filenameJasper, String reportNameMsgKey, Map<String, String> messages, Locale locale,
                                             Configuration configuration, ProjectEntity project, final Map<String, Boolean> sortMap,DataSource ds) {
        super(filenameJasper, reportNameMsgKey, messages, locale, configuration, project, sortMap,ds);
    }

    protected void loadAdditionalParameters() {
        addParameters("PROJECT_ID", project.getId());
        addParameters("SUBREPORT_DIR", "reports/procurement/DetailedSupplierInformation/");
        addParameters("sortBy", getStrSort());
        if(project.getId().longValue()<0){
            //addParameters("projectCode", "All Projects");
            addParameters("projectName", "All Projects");
        }
    }

    @Override
    protected String getStrSort() {
        boolean supplier = sortMap.get("supplier");
        boolean code = sortMap.get("code");
        boolean country = sortMap.get("country");
        sortByName="";
        String str="";
        if(supplier){
            sortByName="Supplier";
            str=str+"sp.company,";
        }else if(code){
            sortByName="Code";
            str=str+"sp.supplier_id,";
        }else if(country){
            sortByName="Country";
            str=str+"sp.country,";
        }
        str = str + "sp.id";
        return str;
    }
}
