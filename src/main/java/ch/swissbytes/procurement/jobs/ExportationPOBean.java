package ch.swissbytes.procurement.jobs;

import ch.swissbytes.Service.business.Spreadsheet.SpreadsheetJDECsvService;
import ch.swissbytes.Service.business.Spreadsheet.SpreadsheetJDEService;
import ch.swissbytes.Service.business.Spreadsheet.SpreadsheetService;
import ch.swissbytes.Service.business.cashflow.CashflowService;
import ch.swissbytes.Service.business.project.ProjectService;
import ch.swissbytes.Service.business.purchase.PurchaseOrderService;
import ch.swissbytes.domain.model.entities.CashflowEntity;
import ch.swissbytes.domain.model.entities.ProjectEntity;
import ch.swissbytes.domain.model.entities.PurchaseOrderEntity;
import ch.swissbytes.fqmes.util.CreateEmailSender;
import org.apache.commons.lang.StringUtils;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.transaction.Transactional;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by Christian on 16/11/2015.
 */
@Stateless
public class ExportationPOBean implements Serializable {

    private static final Logger log = Logger.getLogger(ExportationPOBean.class.getName());

    @Inject
    private SpreadsheetService exporter;

    @Inject
    private SpreadsheetJDEService exporterToJDE;

    @Inject
    private SpreadsheetJDECsvService exporterToJDECsv;

    @Inject
    private ProjectService projectService;

    @Inject
    private PurchaseOrderService poService;

    @Inject
    private CashflowService cashflowService;

    private String pathAbsolute;

    @PostConstruct
    public void create() {
        log.info("Created ExportationPOBean");
    }

    @PreDestroy
    public void destroy() {
        log.info("Destroyed ExportationPOBean");
    }

    @Transactional(value = Transactional.TxType.REQUIRED, rollbackOn = { IOException.class })
    public void dailyExportation(CreateEmailSender createEmailSender) {
        log.info("running process to export.");
        String projectName = "";
        List<ProjectEntity> projectEntities = projectService.findAllProjects();
        List<PurchaseOrderEntity> poListCMS = new ArrayList<>();
        List<PurchaseOrderEntity> poListJDE = new ArrayList<>();
        for (ProjectEntity p : projectEntities) {
            try {
                projectName = p.getProjectNumber();
                log.info("processing PO's from Project[" + p.getProjectNumber() + "]");
                poListCMS = poService.findPOListWithoutExportCMS(p.getId());
                poListJDE = poService.findPOListWithoutExportJDE(p.getId());
                if (!poListCMS.isEmpty()) {
                    exportCMS(poListCMS, p);
                }
                if (!poListJDE.isEmpty()) {
                    for (PurchaseOrderEntity po : poListJDE) {
                        List<CashflowEntity> cashflows = cashflowService.findByPoId(po.getPurchaseOrderProcurementEntity().getId());
                        po.getPurchaseOrderProcurementEntity().setCashflow((!cashflows.isEmpty() && cashflows.size() > 0) ? cashflows.get(0) : null);
                    }
                    exportJDE(poListJDE, p);
                    exportJDECsv(poListJDE, p);
                }
            }catch (IOException e){
                String messageError = "Error Trying to export POs under project: " + projectName;
                String exportationPath="Exportation Path: "+pathAbsolute;
                log.info(messageError);
                log.log(Level.SEVERE, e.getMessage());
                StringWriter errors = new StringWriter();
                e.printStackTrace(new PrintWriter(errors));
                createEmailSender.createEmailToInfoErrorExportCmsOrJde(errors.toString(),messageError,exportationPath);
                e.printStackTrace();
            }
        }
    }

    public void exportCMS(List<PurchaseOrderEntity> list, ProjectEntity project) throws IOException {
        log.info("exportCMS");
        String pathCMS = System.getProperty("fqmes.path.export.cms");
        String fName = StringUtils.isNotEmpty(project.getFolderName()) ? project.getFolderName() : project.getProjectNumber() + " " + project.getTitle();
        pathCMS = pathCMS.replace("{project_field}", fName);
        pathAbsolute = pathCMS;
        exporter.generateWorkbookToExport(list, fName);
        for (PurchaseOrderEntity po : list) {
            poService.markCMSAsExported(po);
        }
    }

    public void exportJDE(List<PurchaseOrderEntity> list, ProjectEntity project) throws IOException {
        log.info("exportJDE");
        String pathJDE = System.getProperty("fqmes.path.export.jde");
        String fName = StringUtils.isNotEmpty(project.getFolderName()) ? project.getFolderName() : project.getProjectNumber() + " " + project.getTitle();
        pathJDE = pathJDE.replace("{project_field}", fName);
        pathAbsolute = pathJDE;
        exporterToJDE.generateWorkbookToExport(list, fName);
        for(PurchaseOrderEntity po : list) {
            poService.markJDEAsExported(po);
        }
    }

    public void exportJDECsv(List<PurchaseOrderEntity> list, ProjectEntity project) throws IOException {
        log.info("exportJDECsv");
        String fName = StringUtils.isNotEmpty(project.getFolderName()) ? project.getFolderName() : project.getProjectNumber() + " " + project.getTitle();
        exporterToJDECsv.generateWorkbookToExport(list, fName);
    }

}
