package ch.swissbytes.procurement.util;

import java.io.*;
import java.util.logging.Logger;

/**
 * Created by Christian on 21/05/2015.
 */
public class FileUtil implements Serializable {

    public static final Logger log = Logger.getLogger(FileUtil.class.getName());

    public FileUtil() {
    }

    public void saveFileTemporal(String content, String path, String fileName) throws IOException{
        log.info("saving file");
        File file = createDirectoryFiles(path);
        File newFile = new File(file.getAbsolutePath() + File.separator + fileName);
        BufferedWriter bw;
        if(!newFile.exists()){
            bw = new BufferedWriter(new FileWriter(newFile));
            bw.write(content);
            bw.close();
        }
        log.info("file saved !");
    }

    private File createDirectoryFiles(String path) {
        File theDir = new File(path);
        if (!theDir.exists()) {
            try {
                theDir.mkdirs();
            } catch (SecurityException se) {

            }
        }
        return theDir;
    }

    public void deleteFileTemporal(String filePath) {
        try {
            File file = new File(filePath);
            if (file.delete()) {
                log.info(file.getName() + " is deleted!");
            } else {
                log.info("Delete operation is failed.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getContentFileToString(String filePath) throws FileNotFoundException, IOException {
        log.info("getContentFileToString");
        String str;
        String content="";
        FileReader f = new FileReader(filePath);
        BufferedReader b = new BufferedReader(f);
        while((str = b.readLine())!=null) {
            content = content + str;
        }
        b.close();
        return content;
    }

}
