package ch.swissbytes.procurement.util;

import java.io.*;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
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
import com.sun.scenario.effect.impl.sw.sse.SSEBlend_SRC_OUTPeer;

/**
 * Created by Christian on 13/02/2016.
 */
public class XmlWorker {

    /**
     * The original PDF file.
     */
    public static final String COVER = "D:\\doc-html\\po-order.pdf";
    /**
     * The original PDF file.
     */
    public static final String SRC = "D:\\doc-html\\file1-1.pdf";

    /**
     * The resulting PDF file.
     */
    public static final String DEST = "D:\\doc-html\\cover_with_pages.pdf";

    public ByteArrayOutputStream manipulatePdf(byte[] coverBt,String src)
            throws IOException, DocumentException {
        System.out.println("manipulatePdf(byte[] coverBt,String src, String dest)");
        System.out.println("covertB size= "+coverBt.length);
        PdfReader cover = new PdfReader(coverBt);
        PdfReader reader = new PdfReader(src);
        Document document = new Document();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        System.out.println("size1= " + outputStream.size());
        PdfCopy copy = new PdfCopy(document, outputStream);

        document.open();
        copy.addDocument(cover);
        copy.addDocument(reader);
        System.out.println("size2= "+outputStream.size());
        document.close();

        cover.close();
        reader.close();
        return outputStream;
    }

    public ByteArrayOutputStream manipulatePdf(byte[] coverBt,byte[] src)
            throws IOException, DocumentException {
        System.out.println("manipulatePdf(byte[] coverBt,String src, String dest)");
        System.out.println("covertB size= "+coverBt.length);
        PdfReader cover = new PdfReader(coverBt);
        PdfReader reader = new PdfReader(src);
        Document document = new Document();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        System.out.println("size1= " + outputStream.size());
        PdfCopy copy = new PdfCopy(document, outputStream);

        document.open();
        copy.addDocument(cover);
        copy.addDocument(reader);
        System.out.println("size2= "+outputStream.size());
        document.close();

        cover.close();
        reader.close();
        return outputStream;
    }

    public ByteArrayOutputStream convertHtml(String content) throws FileNotFoundException, IOException, DocumentException {
        System.out.println("convert to HTML");
        Document document = new Document();
        ByteArrayOutputStream baos=new ByteArrayOutputStream();
        PdfWriter writer = PdfWriter.getInstance(document, baos);
        document.open();
        ByteArrayInputStream bis=new ByteArrayInputStream(content.getBytes());

        // convert the HTML with the built-in convenience method
        XMLWorkerHelper.getInstance().parseXHtml(writer, document, bis);
        System.out.println("outStream size= " + baos.size());
        document.close();
        return  baos;
    }

    public static void main(String[] args) throws FileNotFoundException, IOException, DocumentException, CssResolverException {
        String file = "D:\\doc-html\\file1.html";
        String name = "";
        if (file.endsWith(".html"))
            name = file.substring(0, file.length() - 5);
        else if (file.endsWith(".htm"))
            name = file.substring(0, file.length() - 4);
        else {
            System.out.println("Skipping " + file + ": only processing files with htm or html extension.");

        }
        String outfile1 = name + "-1.pdf";
        String outfile2 = name + "-2.pdf";
        System.out.println("Converting " + file + " to " + outfile1 + ", using XMLWorkerHelper");
        convert1(file, outfile1);
        System.out.println("Converting " + file + " to " + outfile2 + ", using custom pipeline");
        convert2(file, outfile2);
        //**********************************************************

        File fileA = new File(DEST);
        fileA.getParentFile().mkdirs();
        new XmlWorker().manipulatePdf(SRC, DEST);

    }

    public void manipulatePdf(String src, String dest)
            throws IOException, DocumentException {
        PdfReader cover = new PdfReader(COVER);
        PdfReader reader = new PdfReader(src);
        Document document = new Document();
        PdfCopy copy = new PdfCopy(document, new FileOutputStream(dest));
        document.open();
        copy.addDocument(cover);
        copy.addDocument(reader);
        document.close();
        cover.close();
        reader.close();
    }

    public static void convert1(String infile, String outfile) throws FileNotFoundException, IOException, DocumentException {
        Document document = new Document();
        PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(outfile));
        document.open();

        // convert the HTML with the built-in convenience method
        XMLWorkerHelper.getInstance().parseXHtml(writer, document, new FileInputStream(infile));

        document.close();
    }

    public static void convert2(String infile, String outfile)
            throws FileNotFoundException, IOException, DocumentException,
            CssResolverException {
        Document document = new Document();
        PdfWriter writer = PdfWriter.getInstance(document,
                new FileOutputStream(outfile));
        document.open();

        HtmlPipelineContext htmlContext = new HtmlPipelineContext(null);
        htmlContext.setTagFactory(Tags.getHtmlTagProcessorFactory());
        CSSResolver cssResolver = XMLWorkerHelper.getInstance()
                .getDefaultCssResolver(true);

        Pipeline<?> pipeline = new CssResolverPipeline(cssResolver,
                new HtmlPipeline(htmlContext, new PdfWriterPipeline(document,
                        writer)));
        XMLWorker worker = new XMLWorker(pipeline, true);
        XMLParser p = new XMLParser(worker);
        File input = new File(infile);
        p.parse(new InputStreamReader(new FileInputStream(input), "UTF-8"));

        document.close();

    }
}
