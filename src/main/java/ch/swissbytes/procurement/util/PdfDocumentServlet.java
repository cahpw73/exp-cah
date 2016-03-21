package ch.swissbytes.procurement.util;

import ch.swissbytes.Service.business.mainDocument.MainDocumentService;
import ch.swissbytes.domain.model.entities.AttachmentMainDocumentEntity;
import org.apache.commons.lang.StringUtils;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Serializable;

/**
 * Created by Christian on 25/02/2016.
 */
@WebServlet(urlPatterns = "/document/pdf", asyncSupported = true)
public class PdfDocumentServlet extends HttpServlet implements Serializable {

    @Inject
    private MainDocumentService service;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String attachmentMainDocId = request.getParameter("pdfId");
        if(StringUtils.isNotEmpty(attachmentMainDocId)){
            AttachmentMainDocumentEntity attachmentPdf = service.findAttachmentMainDocument(Long.parseLong(attachmentMainDocId));
            if(attachmentPdf!=null) {
                byte[] content = attachmentPdf.getFile();
                response.setContentType("application/pdf");
                response.setContentLength(content.length);
                response.getOutputStream().write(content);
            }
        }
    }
}
