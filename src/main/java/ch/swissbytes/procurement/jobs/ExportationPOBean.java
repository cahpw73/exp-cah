package ch.swissbytes.procurement.jobs;

import ch.swissbytes.Service.business.Spreadsheet.SpreadsheetJDEService;
import ch.swissbytes.Service.business.Spreadsheet.SpreadsheetService;
import ch.swissbytes.Service.business.cashflow.CashflowService;
import ch.swissbytes.Service.business.project.ProjectService;
import ch.swissbytes.Service.business.purchase.PurchaseOrderService;
import ch.swissbytes.domain.model.entities.CashflowEntity;
import ch.swissbytes.domain.model.entities.ProjectEntity;
import ch.swissbytes.domain.model.entities.PurchaseOrderEntity;
import org.apache.commons.lang.StringUtils;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.Stateless;
import javax.inject.Inject;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
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
    private ProjectService projectService;

    @Inject
    private PurchaseOrderService poService;

    @Inject
    private CashflowService cashflowService;


    @PostConstruct
    public void create() {
        log.info("Created ExportationPOBean");
    }

    @PreDestroy
    public void destroy() {
        log.info("Destroyed ExportationPOBean");
    }

    public void dailyExportation() {
        List<ProjectEntity> projectEntities = projectService.findAllProjects();
        List<PurchaseOrderEntity> poListCMS = new ArrayList<>();
        List<PurchaseOrderEntity> poListJDE = new ArrayList<>();
        for (ProjectEntity p : projectEntities) {
            poListCMS = poService.findPOListWithoutExportCMS(p.getId());
            poListJDE = poService.findPOListWithoutExportJDE(p.getId());
            try {
                if (!poListCMS.isEmpty()) {
                    exportCMS(poListCMS, p);
                }
               /* if(!poListJDE.isEmpty()){
                    for(PurchaseOrderEntity po : poListJDE){
                        List<CashflowEntity> cashflows = cashflowService.findByPoId(po.getPurchaseOrderProcurementEntity().getId());
                        po.getPurchaseOrderProcurementEntity().setCashflow((!cashflows.isEmpty() && cashflows.size() > 0)? cashflows.get(0) : null);
                    }
                    exportJDE(poListJDE, p);
                }*/
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    public void exportCMS(List<PurchaseOrderEntity> list, ProjectEntity project) throws FileNotFoundException {
        log.info("exportCMS");
        String fName = StringUtils.isNotEmpty(project.getFolderName()) ? project.getFolderName() : project.getProjectNumber() + " " + project.getTitle();
        exporter.generateWorkbookToExport(list, fName);
        /*for (PurchaseOrderEntity po : list) {
            poService.markCMSAsExported(po);
        }*/
    }

    public void exportJDE(List<PurchaseOrderEntity> list, ProjectEntity project) throws FileNotFoundException {
        log.info("exportJDE");
        String fName = StringUtils.isNotEmpty(project.getFolderName()) ? project.getFolderName() : project.getProjectNumber() + " " + project.getTitle();
        exporterToJDE.generateWorkbookToExport(list, fName);
        /*for(PurchaseOrderEntity po : list) {
            poService.markJDEAsExported(po);
        }*/
    }
}
