package ch.swissbytes.procurement.report;


import ch.swissbytes.domain.model.entities.ProjectEntity;
import ch.swissbytes.fqmes.util.Configuration;
import java.util.Locale;
import java.util.Map;

/**
 * Created by christian on 11/06/14.
 */
public class ReportCommittedCurrencies extends ReportProject {

    private boolean showOriginal;
    private String strSortCurrency;

    public ReportCommittedCurrencies(String filenameJasper, String reportNameMsgKey, Map<String, String> messages, Locale locale,
                                     Configuration configuration, ProjectEntity project, final Map<String, Boolean> sortMap) {
        super(filenameJasper, reportNameMsgKey, messages, locale,configuration,project,sortMap);
    }
    @Override
    protected void loadAdditionalParameters(){
        showOriginal = true;
        strSortCurrency = "";
        addParameters("PROJECT_ID", project.getId());
        addParameters("SUBREPORT_DIR", "reports/procurement/committedCurrenciesReport/");
        if(!getStrSortCurrency()){
            addParameters("sortBy", strSortCurrency);
            addParameters("sortByName", sortByName);
        }
        addParameters("showOriginal",showOriginal);
    }

    private boolean getStrSortCurrency(){
        boolean currency = sortMap.get("currency");
        if (currency){
            strSortCurrency = "cu.code,po.orderedvariation, ";
            sortByName = "Currency, Variation, ";
            showOriginal = false;
        }
        if (strSortCurrency.length() > 1) {
            strSortCurrency = strSortCurrency.substring(0, strSortCurrency.length() - 2);
            sortByName = sortByName.substring(0, sortByName.length() - 2);
        } else {
            strSortCurrency = "po.po,po.orderedvariation";
        }
        return showOriginal;
    }

}
