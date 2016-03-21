package ch.swissbytes.procurement.util;

import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfWriter;

/**
 * Created by Christian on 15/02/2016.
 */
public class HeaderFooterPageEvent extends PdfPageEventHelper{

    private String titleHeader;

    public HeaderFooterPageEvent(){

    }
    public  HeaderFooterPageEvent(String title){
        titleHeader = title;
    }

    public void onStartPage(PdfWriter writer, Document document) {
        Font font = new Font(Font.getFamily("ARIAL"), 10, Font.BOLDITALIC);
        if(titleHeader.toLowerCase().contains("purchase order")){
            ColumnText.showTextAligned(writer.getDirectContent(), Element.ALIGN_CENTER, new Phrase(titleHeader,font), 136, 770, 0);
        }else{
            ColumnText.showTextAligned(writer.getDirectContent(), Element.ALIGN_CENTER, new Phrase(titleHeader,font), 116, 770, 0);
        }
    }
}
