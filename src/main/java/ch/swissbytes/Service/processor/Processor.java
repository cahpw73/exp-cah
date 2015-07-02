package ch.swissbytes.Service.processor;

import java.util.Stack;

/**
 * Created by alvaro on 7/2/2015.
 */
public class Processor {

    private Stack<TagHTML> tags;

    public Processor(){
        tags=new Stack<>();
    }


    public String htmlToJasperPdfStyle(String html){
        StringBuilder pdfJasperStyle=new StringBuilder();
        String copy=html;
        while(copy.length()>0){
            Integer index= stackFirstElementFound(copy);
            copy= moveOn(index, copy);
            TagHTML tag=tags.peek();
            String textInBetween=getTextInBetween(tag);
            int index2=copy.toLowerCase().indexOf(textInBetween.toLowerCase());
            //processing text inBetween;


        }
        return pdfJasperStyle.toString();
    }
    private String moveOn(Integer index, String html){
        return index<0?"":html.substring(index+tags.peek().open.length(),html.length());
    }
    private String moveOn(String text, String html,TagHTML tag){
        return "";
        //return index<0?"":html.substring(index+tags.peek().open.length(),html.length());
    }

    private String getTextInBetween(TagHTML tag){
        return "";
    }

    private Integer stackFirstElementFound(String source){
        int index=-1;
        for(TagHTML tag:TagHTML.values()){
            index=source.toLowerCase().indexOf(tag.open.toLowerCase());
            if(index>=0){
                tags.push(tag);
                break;
            }
        }
        return index;
    }


    public static void main(String[] args) {
        String s="algo algo <b>xxxxx</b>";
        Processor processor=new Processor();
        System.out.println("========================= PROCESSEED ");

        System.out.println(processor.htmlToJasperPdfStyle(s));
    }
}
