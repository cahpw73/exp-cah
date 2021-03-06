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
import java.util.logging.Logger;

/**
 * Created by Christian on 25/02/2016.
 */
@WebServlet(urlPatterns = "/document/html/pdf", asyncSupported = true)
public class PdfDocumentFromHtmlServlet extends HttpServlet implements Serializable {


    public static final Logger log = Logger.getLogger(PdfDocumentFromHtmlServlet.class.getName());

    @Inject
    private MainDocumentService service;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        log.info("PdfDocumentFromHtmlServlet");
        String pathfile = request.getParameter("pathfile");

        FileUtil file = new FileUtil();
        String description =  file.getContentFileToString(pathfile);

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
        log.info("deleting file");
        file.deleteFileTemporal(pathfile);
        log.info("deleted file");
    }

    private ByteArrayOutputStream getReportFromHtml(final String contentPdf) throws IOException, DocumentException {
        XmlWorker xmlWorker = new XmlWorker();
        return xmlWorker.convertHtml(contentPdf);
    }
}
