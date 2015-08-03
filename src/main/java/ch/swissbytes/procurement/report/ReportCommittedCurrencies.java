package ch.swissbytes.procurement.report;


import ch.swissbytes.domain.model.entities.ProjectEntity;
import ch.swissbytes.fqmes.util.Configuration;
import java.util.Locale;
import java.util.Map;

/**
 * Created by christian on 11/06/14.
 */
public class ReportCommittedCurrencies extends ReportProject {

    public ReportCommittedCurrencies(String filenameJasper, String reportNameMsgKey, Map<String, String> messages, Locale locale,
                                     Configuration configuration, ProjectEntity project, final Map<String, Boolean> sortMap) {
        super(filenameJasper, reportNameMsgKey, messages, locale,configuration,project,sortMap);
    }
    @Override
    protected void loadAdditionalParameters(){
        addParameters("PROJECT_ID", project.getId());
        addParameters("SUBREPORT_DIR", "reports/procurement/committedCurrenciesReport/");
    }

}
