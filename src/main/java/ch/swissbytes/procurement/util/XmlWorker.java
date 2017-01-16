package ch.swissbytes.procurement.util;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.pdf.PdfCopy;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.tool.xml.XMLWorkerHelper;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

/**
 * Created by Christian on 13/02/2016.
 */
public class XmlWorker {

    private static final Logger log = Logger.getLogger(XmlWorker.class.getName());

    public ByteArrayOutputStream manipulatePdf(byte[] coverBt,byte[] src)
            throws IOException, DocumentException {
        PdfReader cover = new PdfReader(coverBt);
        PdfReader reader = new PdfReader(src);
        Document document = new Document();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PdfCopy copy = new PdfCopy(document, outputStream);

        document.open();
        copy.addDocument(cover);
        copy.addDocument(reader);
        document.close();

        cover.close();
        reader.close();
        return outputStream;
    }

    public ByteArrayOutputStream manipulatePdf(byte[] poReportBt, java.util.List<ByteArrayOutputStream> otherDocList)
            throws IOException, DocumentException {
        Date startManipulatePdf = new Date();
        PdfReader poReport = new PdfReader(poReportBt);
        List<PdfReader> pdfDocs = new ArrayList<>();

        for(ByteArrayOutputStream baos : otherDocList){
            PdfReader pdfDoc = new PdfReader(baos.toByteArray());
            pdfDocs.add(pdfDoc);
        }
        Document document = new Document();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PdfCopy copy = new PdfCopy(document, outputStream);

        document.open();
        copy.addDocument(poReport);
        for(PdfReader pdf : pdfDocs){
            copy.addDocument(pdf);
        }
        document.close();
        poReport.close();
        Date endManipulatePdf = new Date();
        log.info("endManipulatePdf time - startManipulatePdf time = " + (endManipulatePdf.getTime()-startManipulatePdf.getTime())+"ms");
        return outputStream;
    }

    public ByteArrayOutputStream convertHtml(String content,String titleHeader) throws FileNotFoundException, IOException, DocumentException {
        log.info("convertHtml");
        Document document = new Document(PageSize.A4,55F, 27f, 89f, 27f);
        ByteArrayOutputStream baos=new ByteArrayOutputStream();
        PdfWriter writer = PdfWriter.getInstance(document, baos);
        HeaderFooterPageEvent event = new HeaderFooterPageEvent(titleHeader);
        writer.setPageEvent(event);
        document.open();
        ByteArrayInputStream bis=new ByteArrayInputStream(content.getBytes());

        // convert the HTML with the built-in convenience method
        Date startConvertHtml = new Date();
        XMLWorkerHelper.getInstance().parseXHtml(writer, document, bis);
        Date endConvertHtml = new Date();
        log.info("endConvertHtml time - startConvertHtml time = "+(endConvertHtml.getTime()-startConvertHtml.getTime())+"ms");
        document.close();
        return  baos;
    }

    public ByteArrayOutputStream convertHtml(String content) throws FileNotFoundException, IOException, DocumentException {
        log.info("convertHtml");
        Document document = new Document(PageSize.A4,55F, 27f, 89f, 27f);
        ByteArrayOutputStream baos=new ByteArrayOutputStream();
        PdfWriter writer = PdfWriter.getInstance(document, baos);
        //HeaderFooterPageEvent event = new HeaderFooterPageEvent(titleHeader);
        //writer.setPageEvent(event);
        document.open();
        ByteArrayInputStream bis=new ByteArrayInputStream(content.getBytes());

        // convert the HTML with the built-in convenience method
        Date startConvertHtml = new Date();
        XMLWorkerHelper.getInstance().parseXHtml(writer, document, bis);
        Date endConvertHtml = new Date();
        log.info("endConvertHtml time - startConvertHtml time = "+(endConvertHtml.getTime()-startConvertHtml.getTime())+"ms");
        document.close();
        return  baos;
    }
}
