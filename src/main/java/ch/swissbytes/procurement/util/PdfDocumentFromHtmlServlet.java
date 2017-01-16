package ch.swissbytes.procurement.util;

import ch.swissbytes.Service.business.mainDocument.MainDocumentService;
import ch.swissbytes.domain.model.entities.AttachmentMainDocumentEntity;
import ch.swissbytes.domain.model.entities.MainDocumentEntity;
import ch.swissbytes.domain.model.entities.PODocumentEntity;
import ch.swissbytes.domain.model.entities.ProjectDocumentEntity;
import ch.swissbytes.domain.types.ClassEnum;
import com.itextpdf.text.DocumentException;
import org.apache.commons.lang.StringUtils;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;

/**
 * Created by Christian on 25/02/2016.
 */
@WebServlet(urlPatterns = "/document/html/pdf", asyncSupported = true)
public class PdfDocumentFromHtmlServlet extends HttpServlet implements Serializable {

    @Inject
    private MainDocumentService service;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("PdfDocumentFromHtmlServlet");
        String attachmentMainDocId = request.getParameter("pdfMainId");
        String pdfType = request.getParameter("pdfType");
        if(StringUtils.isNotEmpty(attachmentMainDocId)){
            System.out.println("attachmentMainDocId = " +attachmentMainDocId);
            String description = "";
            if(pdfType.equals("1")) {
                System.out.println("pdfType = " +1);
                MainDocumentEntity attachmentPdf = service.findById(Long.parseLong(attachmentMainDocId));
                description = attachmentPdf!=null? attachmentPdf.getDescription():"";
            }else if(pdfType.equals("2")){
                System.out.println("pdfType = " +2);
                ProjectDocumentEntity attachmentPdf = service.findProjectDocumentEntityById(Long.parseLong(attachmentMainDocId));
                description = attachmentPdf!=null? attachmentPdf.getDescription():"";
            }else if (pdfType.equals("3")){
                System.out.println("pdfType = " +3);
                PODocumentEntity attachmentPdf = service.findPODocumentEntityById(Long.parseLong(attachmentMainDocId));
                description = attachmentPdf!=null? attachmentPdf.getDescription():"";
            }

            if(StringUtils.isNotEmpty(description)) {
                try {
                    byte[] content = getReportFromHtml(description).toByteArray();
                    response.setContentType("application/pdf");
                    response.setContentLength(content.length);
                    response.getOutputStream().write(content);
                } catch (DocumentException e) {
                    e.printStackTrace();
                }

            }
        }
    }

    private ByteArrayOutputStream getReportFromHtml(final String contentPdf) throws IOException, DocumentException {
        XmlWorker xmlWorker = new XmlWorker();
        return xmlWorker.convertHtml(contentPdf);
    }
}
