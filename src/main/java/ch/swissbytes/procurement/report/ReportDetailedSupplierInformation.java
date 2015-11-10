package ch.swissbytes.procurement.report;


import ch.swissbytes.domain.model.entities.ProjectEntity;
import ch.swissbytes.fqmes.util.Configuration;

import java.util.Locale;
import java.util.Map;

/**
 * Created by christian on 11/06/14.
 */
public class ReportDetailedSupplierInformation extends ReportProject {

    public ReportDetailedSupplierInformation(String filenameJasper, String reportNameMsgKey, Map<String, String> messages, Locale locale,
                                             Configuration configuration, ProjectEntity project, final Map<String, Boolean> sortMap) {
        super(filenameJasper, reportNameMsgKey, messages, locale, configuration, project, sortMap);
    }

    protected void loadAdditionalParameters() {
        addParameters("PROJECT_ID", project.getId());
        addParameters("SUBREPORT_DIR", "reports/procurement/DetailedSupplierInformation/");
        addParameters("sortBy", getStrSort());
    }

    @Override
    protected String getStrSort() {
        String str = super.getStrSort();
        if (!str.isEmpty()) {
            //remove this after fixing original method on super class.
            int index = str.indexOf("po.orderedvariation");
            str = str.substring(0, index);
            if (str.trim().length() > 0) {
                str = str + ",";
            }
        }
        str = str + "sp.id";
        return str;
    }
}
