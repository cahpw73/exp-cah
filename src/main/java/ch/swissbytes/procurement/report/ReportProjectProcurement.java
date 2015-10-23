package ch.swissbytes.procurement.report;


import ch.swissbytes.domain.model.entities.ProjectCurrencyEntity;
import ch.swissbytes.domain.model.entities.ProjectEntity;
import ch.swissbytes.domain.model.entities.PurchaseOrderEntity;
import ch.swissbytes.fqmes.report.util.ReportView;
import ch.swissbytes.fqmes.util.Configuration;
import ch.swissbytes.fqmes.util.LookupValueFactory;
import ch.swissbytes.fqmes.util.Util;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.util.*;
import java.util.logging.Logger;

/**
 * Created by christian on 11/06/14.
 */
public class ReportProjectProcurement extends ReportProject implements Serializable {

    private EntityManager entityManager;

    /**
     * @param filenameJasper   - fileName the reports to use
     * @param reportNameMsgKey - Message key for name the file to save
     * @param messages
     * @param locale           {@link java.util.Locale}
     */
    public ReportProjectProcurement(String filenameJasper, String reportNameMsgKey, Map<String, String> messages, Locale locale,
                                    Configuration configuration, ProjectEntity project, Map<String, Boolean> sortMap, EntityManager entityManager) {
        super(filenameJasper, reportNameMsgKey, messages, locale,configuration,project,sortMap);
        this.entityManager = entityManager;
    }

    @Override
    protected void loadAdditionalParameters() {
        //ProjectCurrencyEntity projectCurrency = getProjectCurrencyDefaultByProjectId();
        addParameters("SUBREPORT_DIR", "reports/procurement/uncommittedDataReport/");
        /*if(projectCurrency.getCurrency()!=null){
            addParameters("currencyCode",projectCurrency.getCurrency().getCode());
        }
*/
    }

    private ProjectCurrencyEntity getProjectCurrencyDefaultByProjectId(){
        StringBuilder sb = new StringBuilder();
        sb.append(" SELECT p ");
        sb.append(" FROM ProjectCurrencyEntity pc ");
        sb.append(" WHERE pc.project.id = :PROJECT_ID ");
        sb.append(" AND pc.projectDefault = true ");
        Query query = entityManager.createQuery(sb.toString());
        query.setParameter("PROJECT_ID",project.getId());
        List<ProjectCurrencyEntity> list = query.getResultList();
        ProjectCurrencyEntity entity = new ProjectCurrencyEntity();
        if(!list.isEmpty()){
            entity = list.get(0);
        }
        return entity;
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
