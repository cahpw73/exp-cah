package ch.swissbytes.procurement.report;


import ch.swissbytes.domain.model.entities.ProjectEntity;
import ch.swissbytes.fqmes.util.Configuration;

import javax.persistence.EntityManager;
import javax.sql.DataSource;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Created by christian on 11/06/14.
 */
public class ReportPosRegister extends ReportProject implements Serializable {

    private EntityManager entityManager;

    private Map<String, Boolean> filterMap;

    private List<Integer> classesId = new ArrayList<>();

    /**
     * @param filenameJasper   - fileName the reports to use
     * @param reportNameMsgKey - Message key for name the file to save
     * @param messages
     * @param locale           {@link java.util.Locale}
     */
    public ReportPosRegister(String filenameJasper, String reportNameMsgKey, Map<String, String> messages, Locale locale,
                             Configuration configuration, ProjectEntity project, Map<String, Boolean> sortMap, EntityManager entityManager,
                             Map<String, Boolean> filterMap,DataSource ds) {
        super(filenameJasper, reportNameMsgKey, messages, locale, configuration, project, sortMap,ds);
        this.entityManager = entityManager;
        this.filterMap = filterMap;
        loadAdditionalParameters1();
    }


    protected void loadAdditionalParameters1() {
        loadFilterToQuery();
    }

    public void loadFilterToQuery() {
        Boolean allPos = filterMap.get("allPos");
        Boolean contractOnly = filterMap.get("contractOnly");
        Boolean posOnly = filterMap.get("posOnly");
        if (allPos != null && allPos.booleanValue()) {
            classesId.add(0);
            classesId.add(1);
            classesId.add(2);
            classesId.add(3);
        }
        if (contractOnly != null && contractOnly.booleanValue()) {
            classesId.add(1);
            classesId.add(2);
            classesId.add(3);
        }
        if (posOnly != null && posOnly.booleanValue()) {
            classesId.add(0);
        }
        addParameters("classId", classesId);
    }


    protected String getStrSort() {
        Boolean poNo = sortMap.get("poNo");
        Boolean supplier = sortMap.get("supplier");
        Boolean title = sortMap.get("title");
        String strSort = "";
        if (!poNo && !supplier && title) {
            strSort = "po.po_title,po.orderedvariation, ";
            sortByName = "Title, Variation, ";
        } else if (poNo && !supplier && !title) {
            strSort = "po.po,po.orderedvariation, ";
            sortByName = "PO No, Variation, ";
        } else if (!poNo && supplier && !title) {
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

    @Override
    public void printDocument(Long documentId) {
        try {
            runReport();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
