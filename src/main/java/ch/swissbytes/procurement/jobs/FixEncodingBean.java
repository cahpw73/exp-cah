package ch.swissbytes.procurement.jobs;

import ch.swissbytes.Service.business.Spreadsheet.SpreadsheetJDECsvService;
import ch.swissbytes.Service.business.Spreadsheet.SpreadsheetJDEService;
import ch.swissbytes.Service.business.Spreadsheet.SpreadsheetService;
import ch.swissbytes.Service.business.cashflow.CashflowService;
import ch.swissbytes.Service.business.mainDocument.MainDocumentService;
import ch.swissbytes.Service.business.poDocument.PODocumentService;
import ch.swissbytes.Service.business.project.ProjectService;
import ch.swissbytes.Service.business.projectDocument.ProjectDocumentService;
import ch.swissbytes.Service.business.projectTextSnippet.ProjectTextSnippetService;
import ch.swissbytes.Service.business.purchase.PurchaseOrderService;
import ch.swissbytes.Service.business.text.TextService;
import ch.swissbytes.Service.business.textSnippet.TextSnippetService;
import ch.swissbytes.domain.model.entities.*;
import ch.swissbytes.fqmes.util.CreateEmailSender;
import org.apache.commons.lang.StringUtils;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.inject.Named;
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
@Named
public class FixEncodingBean implements Serializable {

    private static final Logger log = Logger.getLogger(FixEncodingBean.class.getName());

    @Inject
    private TextSnippetService mainTextSnippetService;

    @Inject
    private TextService poTextSnippetService;

    @Inject
    private ProjectTextSnippetService projectTextSnippetService;

    @Inject
    private MainDocumentService mainDocumentService;

    @Inject
    private ProjectDocumentService projectDocumentService;

    @Inject
    private PODocumentService poDocumentService;



    @PostConstruct
    public void create() {
        log.info("Created ExportationPOBean");
    }

    @PreDestroy
    public void destroy() {
        log.info("Destroyed ExportationPOBean");
    }

    @Transactional(value = Transactional.TxType.REQUIRED, rollbackOn = { IOException.class })
    public void fixTextEncode() {
        log.info("running process to fixTextEncode.");

        /*List<TextSnippetEntity> textSnippetList = mainTextSnippetService.getAllTextSnippetList();
        for(TextSnippetEntity t : textSnippetList){
            t.setDescriptionSnippet(t.getTextSnippet());
            mainTextSnippetService.doUpdate(t);
        }

        List<ProjectTextSnippetEntity> projectTextList =  projectTextSnippetService.getAllProjectText();
        for(ProjectTextSnippetEntity pt : projectTextList){
            pt.setDescriptionSnippet(pt.getDescription());
            projectTextSnippetService.doUpdate(pt);
        }

        List<ClausesEntity> clausesList = poTextSnippetService.getAll();
        for(ClausesEntity c : clausesList){
            c.setDescriptionSnippet(c.getClauses());
            poTextSnippetService.doUpdateClauses(c);
        }

        List<MainDocumentEntity> mainDocumentList = mainDocumentService.getAll();
        for(MainDocumentEntity m : mainDocumentList){
            m.setDescriptionDocument(m.getDescription());
            mainDocumentService.doUpdate(m);
        }

        List<ProjectDocumentEntity> projectDocumentList = projectDocumentService.getAll();
        for(ProjectDocumentEntity p : projectDocumentList){
            p.setDescriptionDocument(p.getDescription());
            projectDocumentService.doUpdate(p);
        }

        List<PODocumentEntity> poDocumentList = poDocumentService.getAll();
        for(PODocumentEntity p : poDocumentList){
            p.setDescriptionDocument(p.getDescription());
            poDocumentService.doUpdate(p);
        }*/


        log.info("finalize job.");
    }

}
