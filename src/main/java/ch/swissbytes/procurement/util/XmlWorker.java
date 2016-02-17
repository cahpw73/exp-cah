package ch.swissbytes.procurement.util;

import java.io.*;
import java.util.*;
import java.util.List;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import com.itextpdf.text.pdf.codec.Base64;
import com.itextpdf.text.pdf.qrcode.ByteArray;
import com.itextpdf.tool.xml.Pipeline;
import com.itextpdf.tool.xml.XMLWorker;
import com.itextpdf.tool.xml.XMLWorkerHelper;
import com.itextpdf.tool.xml.exceptions.CssResolverException;
import com.itextpdf.tool.xml.html.Tags;
import com.itextpdf.tool.xml.parser.XMLParser;
import com.itextpdf.tool.xml.pipeline.css.CSSResolver;
import com.itextpdf.tool.xml.pipeline.css.CssResolverPipeline;
import com.itextpdf.tool.xml.pipeline.end.PdfWriterPipeline;
import com.itextpdf.tool.xml.pipeline.html.HtmlPipeline;
import com.itextpdf.tool.xml.pipeline.html.HtmlPipelineContext;
import com.lowagie.text.HeaderFooter;
import com.lowagie.text.Phrase;
import com.sun.scenario.effect.impl.sw.sse.SSEBlend_SRC_OUTPeer;

/**
 * Created by Christian on 13/02/2016.
 */
public class XmlWorker {


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
        return outputStream;
    }

    public ByteArrayOutputStream convertHtml(String content,String titleHeader) throws FileNotFoundException, IOException, DocumentException {
        Document document = new Document(PageSize.A4,55F, 27f, 89f, 27f);
        ByteArrayOutputStream baos=new ByteArrayOutputStream();
        PdfWriter writer = PdfWriter.getInstance(document, baos);
        HeaderFooterPageEvent event = new HeaderFooterPageEvent(titleHeader);
        writer.setPageEvent(event);
        document.open();
        ByteArrayInputStream bis=new ByteArrayInputStream(content.getBytes());

        // convert the HTML with the built-in convenience method
        XMLWorkerHelper.getInstance().parseXHtml(writer, document, bis);
        document.close();
        return  baos;
    }
}
