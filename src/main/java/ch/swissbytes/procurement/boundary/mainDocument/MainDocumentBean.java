package ch.swissbytes.procurement.boundary.mainDocument;


import ch.swissbytes.Service.business.mainDocument.MainDocumentService;
import ch.swissbytes.Service.business.poDocument.PODocumentService;
import ch.swissbytes.Service.business.projectDocument.ProjectDocumentService;
import ch.swissbytes.domain.model.entities.AttachmentMainDocumentEntity;
import ch.swissbytes.domain.model.entities.MainDocumentEntity;
import ch.swissbytes.domain.types.ModeOperationEnum;
import ch.swissbytes.procurement.util.FileUtil;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.omnifaces.util.Messages;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.UploadedFile;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.bean.ApplicationScoped;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.*;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by alvaro on 9/15/14.
 */
@Named
@ViewScoped
public class MainDocumentBean implements Serializable {

    private static final Logger log = Logger.getLogger(MainDocumentBean.class.getName());

    @Inject
    private MainDocumentService service;

    @Inject
    private ProjectDocumentService projectDocumentService;

    @Inject
    private PODocumentService poDocumentService;

    private List<MainDocumentEntity> list;

    private MainDocumentEntity mainDocument;

    private MainDocumentEntity selected;

    private ModeOperationEnum mode;

    private String criteria;

    private AttachmentMainDocumentEntity attachmentMainDocument;

    private String tempFilePdfPath = "";

    @PostConstruct
    public void create() {
        log.info("creating currency bean");
        loadList();
        mode = ModeOperationEnum.NEW;
        mainDocument = new MainDocumentEntity();
        attachmentMainDocument = new AttachmentMainDocumentEntity();
    }

    @PreDestroy
    public void destroy() {
        log.info("destroying currency bean");
    }

    public void edit(Long currencyId) {
        mainDocument = service.findById(currencyId);
        mode = ModeOperationEnum.UPDATE;
    }

    public String doSave() {
        if (!validate(mainDocument)) {
            return "";
        }
        service.doSave(mainDocument);
        return "main-document?faces-redirect=true";
    }

    public String doSavePdf(){
        if(!validateCodeDlg(mainDocument)){
            return "";
        }
        service.doSaveWithPdf(mainDocument, attachmentMainDocument);
        return "main-document?faces-redirect=true";
    }

    public void clear(){
        mainDocument=new MainDocumentEntity();
    }


    private boolean validate(MainDocumentEntity entity) {
        boolean valid = true;
        if(StringUtils.isEmpty(entity.getCode())){
            Messages.addFlashGlobalError("Enter valid code");
            valid = false;
        }
        if(StringUtils.isEmpty(entity.getDescription())){
            Messages.addFlashGlobalError("Enter valid text");
            valid = false;
        }
        if (service.isCodeDuplicated(entity.getId(), entity.getCode())) {
            Messages.addFlashError("codeTS", String.format("Code [%s] is duplicated ", entity.getCode()));
            valid = false;
        }
        return valid;
    }

    private boolean validateCodeDlg(MainDocumentEntity entity){
        boolean valid = true;
        if (service.isCodeDuplicated(entity.getId(), entity.getCode())) {
            Messages.addFlashError("codeDlg", String.format("Code [%s] is duplicated ", entity.getCode()));
            valid = false;
        }
        return valid;
    }

    public boolean addProject(boolean hideModal){
        if(!validate(mainDocument)){
            return false;
        }
        if(hideModal) {
            RequestContext context = RequestContext.getCurrentInstance();
            RequestContext.getCurrentInstance().update("pickSystemFormId");
            context.execute("PF('textSnippetModal').hide();");
        }
        return true;
    }

    public String doUpdate() {
        if (!validate(selected)) {
            return "";
        }
        selected.setLastUpdate(new Date());
        selected.setCode(selected.getCode().toUpperCase());
        service.doUpdate(selected);
        poDocumentService.doBulkUpdateCode(projectDocumentService.doBulkUpdateCode(selected));
        return "main-document?faces-redirect=true";
    }

    public String doDelete() {

        service.delete(selected);
        return "main-document?faces-redirect=true";
    }

    public void search() {
        log.info("criteria : " + criteria);
        list.clear();
        list = service.findByText(criteria);
        if (list.size() == 1) {
            selected = list.get(0);
        }

    }

    public void handleUpload(FileUploadEvent event) {
        UploadedFile uf = event.getFile();
        enterFile(uf);
    }

    public void enterFile(UploadedFile uf) {
        try {
            InputStream is = uf.getInputstream();
            attachmentMainDocument.setFile(IOUtils.toByteArray(is));
            attachmentMainDocument.setMimeType(uf.getContentType());
            attachmentMainDocument.setFileName(uf.getFileName());
            is.close();
        } catch (IOException ioe) {
            log.log(Level.SEVERE, String.format("problems with file [" + uf.getFileName() + "]"));
            log.log(Level.SEVERE, ioe.getMessage());
        } catch (Exception ex) {
            log.log(Level.SEVERE, String.format("problems with file [" + uf.getFileName() + "]"));
            log.log(Level.SEVERE, ex.getMessage());
        }
    }

    public byte[] getPdf() throws IOException {
            if(selected!=null) {
                AttachmentMainDocumentEntity attachmentPdf = service.findAttachmentMainDocument(selected.getAttachmentMainDocument().getId());
                if(attachmentPdf != null){
                    return  attachmentPdf.getFile();
                }else{
                    return new byte[0];
                }

            }else{
                return new byte[0];
            }
    }

    public String getPdfId() throws IOException {
        if(selected!=null && selected.getAttachmentMainDocument() != null) {
            AttachmentMainDocumentEntity attachmentPdf = service.findAttachmentMainDocument(selected.getAttachmentMainDocument().getId());
            if(attachmentPdf != null){
                return  attachmentPdf.getId().toString();
            }else{
                return "";
            }

        }else{
            return "";
        }
    }

    public void createMainDocPdfFile(){
        log.info("createMainDocPdfFile()");
        if(StringUtils.isNotEmpty(mainDocument.getCode())){
            log.info("the code is not null");
            String pathTempPdf = System.getProperty("fqmes.path.preview.pdf");
            String fileName = new Date().getTime()+"";
            FileUtil fileUtil = new FileUtil();
            try {
                log.info("creating file on path = " + pathTempPdf);
                fileUtil.saveFileTemporal(mainDocument.getDescription(),pathTempPdf,fileName);
                tempFilePdfPath = pathTempPdf + File.separator + fileName;
                log.info("tempFilePdfPath = "  + tempFilePdfPath);
            } catch (IOException e) {
                e.printStackTrace();
            }
            RequestContext context = RequestContext.getCurrentInstance();
            context.execute("PF('previewPDFProjectHtml1').show();");
        }else{
            log.info("the code is null");
            Messages.addFlashGlobalError("Enter valid code");
        }

    }

    public void previewMainDocPdfSelect(){
        log.info("createMainDocPdfFile()");
        String pathTempPdf = System.getProperty("fqmes.path.preview.pdf");
        String fileName = new Date().getTime()+"";
        FileUtil fileUtil = new FileUtil();
        try {
            log.info("creating file on path = " + pathTempPdf);
            fileUtil.saveFileTemporal(selected.getDescription(),pathTempPdf,fileName);
            tempFilePdfPath = pathTempPdf + File.separator + fileName;
            log.info("tempFilePdfPath = "  + tempFilePdfPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public String getCriteria() {
        return criteria;
    }

    public void setCriteria(String criteria) {
        this.criteria = criteria;
    }

    private void loadList() {
        list = service.getMainDocumentList();
    }

    public List<MainDocumentEntity> getList() {
        return list;
    }

    public MainDocumentEntity getMainDocument() {
        return mainDocument;
    }

    public void setMainDocument(MainDocumentEntity mainDocument) {
        this.mainDocument = mainDocument;
    }

    public MainDocumentEntity getSelected() {
        return selected;
    }

    public void setSelected(MainDocumentEntity selected) {
        this.selected = selected;
    }

    public AttachmentMainDocumentEntity getAttachmentMainDocument() {
        return attachmentMainDocument;
    }

    public void setAttachmentMainDocument(AttachmentMainDocumentEntity attachmentMainDocument) {
        this.attachmentMainDocument = attachmentMainDocument;
    }

    public String getTempFilePdfPath() {
        return tempFilePdfPath;
    }

    public void setTempFilePdfPath(String tempFilePdfPath) {
        this.tempFilePdfPath = tempFilePdfPath;
    }
}
