package ch.swissbytes.procurement.boundary.project;

import ch.swissbytes.Service.business.project.ProjectService;
import ch.swissbytes.domain.model.entities.*;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Logger;

/**
 * Created by Christian on 25/05/2015.
 */
@Named
@ViewScoped
public class ProjectListBean implements Serializable {

    public static final Logger log = Logger.getLogger(ProjectListBean.class.getName());

    @Inject
    private ProjectService projectService;

    private List<ProjectEntity> projectList;

    private String searchTerm;

    private boolean wasCreatedDirectories;

    @PostConstruct
    public void init() {
        log.info("ProjectsBean was created");
        projectList = new ArrayList<>();
        loadProjects();
    }

    @PreDestroy
    public void destroy() {
        log.info("ProjectsBean destroying");
    }

    public void loadProjects() {
        projectList = projectService.findAllProjects();
    }

    public void doSearch() {
        projectList.clear();
        projectList = projectService.doSearch(searchTerm);
    }

    public void doClean() {
        projectList.clear();
        loadProjects();
        searchTerm = "";
    }

    public void calculateFreeSpace() {
        log.info("calculating free space");
        File file = new File(System.getProperty("fqmes.path.export.main.root"));
        long totalSpace = file.getTotalSpace(); //total disk space in bytes.
        long usableSpace = file.getUsableSpace(); ///unallocated / free disk space in bytes.
        long freeSpace = file.getFreeSpace(); //unallocated / free disk space in bytes.

        System.out.println(" === Partition Detail ===");

        System.out.println(" === bytes ===");
        System.out.println("Total size : " + totalSpace + " bytes");
        System.out.println("Space free : " + usableSpace + " bytes");
        System.out.println("Space free : " + freeSpace + " bytes");

        System.out.println(" === mega bytes ===");
        System.out.println("Total size : " + totalSpace / 1024 / 1024 + " mb");
        System.out.println("Space free : " + usableSpace / 1024 / 1024 + " mb");
        System.out.println("Space free : " + freeSpace / 1024 / 1024 + " mb");
    }

    public void listFolderFQM() {
        String path1824_1 = "D:\\PERDevl\\1824 - cobre panama\\300 Project Controls\\Procurement Commitments";
        String path1824_2 = "D:\\PERDevl\\1824 - cobre panama\\500 Procurement\\Commitments to JDE";
        String pathFQMA_1 = "D:\\PERDevl\\AFCE\\FQMEA\\300 Project Controls\\Procurement Commitments";
        String pathFQMA_2 = "D:\\PERDevl\\AFCE\\FQMEA\\500 Procurement\\Commitments to JDE";
        String pathPJO006_1 = "D:\\PERDevl\\PJO - Projects\\PJO006 Sentinel Trolley Assist\\300 Project Controls\\Procurement Commitments";
        String pathPJO006_2 = "D:\\PERDevl\\PJO - Projects\\PJO006 Sentinel Trolley Assist\\500 Procurement\\Commitments to JDE";
        Map<String, String> map = new HashMap<>();
        map.put("path1824_1", path1824_1);
        map.put("path1824_2", path1824_2);
        map.put("pathFQMA_1", pathFQMA_1);
        map.put("pathFQMA_2", pathFQMA_2);
        map.put("pathPJO006_1", pathPJO006_1);
        map.put("pathPJO006_2", pathPJO006_2);
        for (Object value : map.values()) {
            log.info("Path project to list: " + (String)value);
            log.info(" ");
            listFolders((String) value);
            log.info(" ");
        }
    }

    public void listFolders(String pathDir) {
        log.info("listing folders....");
        String pathMain = pathDir;
        File f = new File(pathMain);
        log.info("PATH " + pathMain);
        log.info("File name " + f.getName());
        log.info("Absolute path " + f.getAbsolutePath());
        log.info("Another path " + f.getPath());
        log.info("Size listFiles: " + f.listFiles().length);
        File[] ficheros = f.listFiles();
        if (ficheros != null) {
            log.info("Files[] size: " + ficheros.length);
            log.info("files is not null");
            for (int x = 0; x < ficheros.length; x++) {
                log.info("1: " + ficheros[x].getName());
                String subPath = pathMain + File.separator + ficheros[x].getName();
                log.info("subPath1: " + subPath);
                File subFile = new File(subPath);
                log.info("create var subFile1: " + subFile.getAbsolutePath());
                File[] subficheros = subFile.listFiles();
                if (subficheros != null) {
                    log.info("subFiles[] size: " + subficheros.length);
                    for (int i = 0; i < subficheros.length; i++) {
                        log.info("2: " + subficheros[i].getName());
                        String subPath2 = pathMain + File.separator + ficheros[x].getName() + File.separator + subficheros[i].getName();
                        log.info("subPath2: " + subPath2);
                        File subFile2 = new File(subPath2);
                        log.info("create var subFile2: " + subFile2.getAbsolutePath());
                        File[] subficheros2 = subFile2.listFiles();
                        if (subficheros2 != null) {
                            log.info("subFiles2[] size: " + subficheros2.length);
                            for (int j = 0; j < subficheros2.length; j++) {
                                log.info("3: " + subficheros2[j].getName());
                                String subPath3 = pathMain + File.separator + ficheros[x].getName() + File.separator + subficheros[i].getName() + File.separator + subficheros2[j].getName();
                                log.info("subPath3 : " + subPath3);
                                File subFile3 = new File(subPath3);
                                log.info("create var subFile3 " + subFile3.getAbsolutePath());
                                File[] subficheros3 = subFile3.listFiles();
                                if (subficheros3 != null) {
                                    log.info("subfiles3[] size: " + subficheros3.length);
                                    for (int k = 0; k < subficheros3.length; k++) {
                                        log.info("4." + k + ": " + subficheros3[k].getName() + ",   file size: " + subficheros3[k].length());
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } else {
            System.out.println("Something is happening, it is no capable of retrieving anything");
        }
    }

    public void delete1824_1(){
        String path1824_1 = "D:\\PERDevl\\1824 - cobre panama\\300 Project Controls\\Procurement Commitments";
        String path1824_2 = "D:\\PERDevl\\1824 - cobre panama\\500 Procurement\\Commitments to JDE";
        String pathFQMA_1 = "D:\\PERDevl\\AFCE\\FQMEA\\300 Project Controls\\Procurement Commitments";
        String pathFQMA_2 = "D:\\PERDevl\\AFCE\\FQMEA\\500 Procurement\\Commitments to JDE";
        String pathPJO006_1 = "D:\\PERDevl\\PJO - Projects\\PJO006 Sentinel Trolley Assist\\300 Project Controls\\Procurement Commitments";
        String pathPJO006_2 = "D:\\PERDevl\\PJO - Projects\\PJO006 Sentinel Trolley Assist\\500 Procurement\\Commitments to JDE";
        Map<String, String> map = new HashMap<>();
        map.put("path1824_1", path1824_1);
        map.put("path1824_2", path1824_2);
        map.put("pathFQMA_1", pathFQMA_1);
        map.put("pathFQMA_2", pathFQMA_2);
        map.put("pathPJO006_1", pathPJO006_1);
        map.put("pathPJO006_2", pathPJO006_2);
        for (Object value : map.values()) {
            log.info("Path project to list: " + (String)value);
            log.info(" ");
            deleteFile1824_1((String) value);
            log.info(" ");
        }
    }

    public void deleteFile1824_1(String pathDir) {
        log.info("deleting file to testing....");
        String pathCMS = pathDir;
        log.info("path absolute to delete file: " + pathCMS);
        File file = new File(pathCMS + File.separator + generateFileName());
        log.info("deleting file");
        if (file.delete())
            log.info("File was deleted successfully");
        else
            log.info("File can't be deleted");

        File directorio = new File(pathCMS);
        if (directorio.delete())
            log.info("File was deleted successfully");
        else
            log.info("File can't be deleted");

        File directorio1 = new File(pathCMS.replace("\\Procurement Commitments", ""));
        if (directorio1.delete())
            log.info("File was deleted successfully");
        else
            log.info("File can't be deleted");

        File directorio2 = new File(pathCMS.replace("\\300 Project Controls\\Procurement Commitments", ""));
        if (directorio2.delete())
            log.info("File was deleted successfully");
        else
            log.info("File can't be deleted");

        File directorio3 = new File(pathCMS.replace("\\Commitments to JDE", ""));
        if (directorio3.delete())
            log.info("File was deleted successfully");
        else
            log.info("File can't be deleted");

        File directorio4 = new File(pathCMS.replace("\\500 Procurement\\Procurement Commitments", ""));
        if (directorio4.delete())
            log.info("File was deleted successfully");
        else
            log.info("File can't be deleted");

    }

    public void delete1824_2(){
        String path1824_2 = "D:\\PERDevl\\1824 - cobre panama\\500 Procurement\\Commitments to JDE";
    }

    public void createFQM(){
        String path1824_1 = "D:\\PERDevl\\1824 - cobre panama\\300 Project Controls\\Procurement Commitments";
        String path1824_2 = "D:\\PERDevl\\1824 - cobre panama\\500 Procurement\\Commitments to JDE";
        String pathFQMA_1 = "D:\\PERDevl\\AFCE\\FQMEA\\300 Project Controls\\Procurement Commitments";
        String pathFQMA_2 = "D:\\PERDevl\\AFCE\\FQMEA\\500 Procurement\\Commitments to JDE";
        String pathPJO006_1 = "D:\\PERDevl\\PJO - Projects\\PJO006 Sentinel Trolley Assist\\300 Project Controls\\Procurement Commitments";
        String pathPJO006_2 = "D:\\PERDevl\\PJO - Projects\\PJO006 Sentinel Trolley Assist\\500 Procurement\\Commitments to JDE";
        Map<String, String> map = new HashMap<>();
        map.put("path1824_1", path1824_1);
        map.put("path1824_2", path1824_2);
        map.put("pathFQMA_1", pathFQMA_1);
        map.put("pathFQMA_2", pathFQMA_2);
        map.put("pathPJO006_1", pathPJO006_1);
        map.put("pathPJO006_2", pathPJO006_2);
        for (Object value : map.values()) {
            log.info("Path project to list: " + (String)value);
            log.info(" ");
            createFile((String) value);
            log.info(" ");
        }
    }

    public void createFile(String pathDir) {
        log.info("creatin file to testing.....");

        String pathCMS = pathDir;
        File testFile = new File(pathCMS);
        if (testFile.canRead()) {
            log.info("read can into root");
        } else {
            log.info("read can't into root");
        }
        if (testFile.canWrite()) {
            log.info("write can into root");
        } else {
            log.info("write can't into root");
        }
        if (testFile.canExecute()) {
            log.info("execute can int root");
        } else {
            log.info("execute can't int root");
        }

        log.info("path to save file: " + pathCMS);
        FileOutputStream out = null;
        File folders = createDirectoryFiles(pathCMS);
        if (wasCreatedDirectories) {
            if (folders.isDirectory() && folders.exists()) {
                log.info("directory exists");
            } else {
                log.info("directory NOT exists");
            }
            log.info("folder + file path: " + folders.getAbsolutePath() + File.separator + generateFileName());
            File file = new File(folders.getAbsolutePath() + File.separator + generateFileName());
            log.info("create var File: " + file.getAbsolutePath());
            try {
                if (file.createNewFile()) {
                    out = new FileOutputStream(file);
                    log.info("created file");
                    List<String> lines = Arrays.asList("The first line", "The second line");
                    Path file12 = Paths.get(file.getAbsolutePath());
                    Files.write(file12, lines, Charset.forName("UTF-8"));
                } else {
                    log.info("File can't be create");
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    log.info("closing fileOutputStream out");
                    if (out != null) {
                        out.close();
                        log.info("closed out");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private String generateFileName() {
        SimpleDateFormat format = new SimpleDateFormat("dd MMM yy");
        String dateStr = format.format(new Date());
        String fileName = dateStr.toUpperCase() + " - " + "TESTCommitments.txt";
        return fileName;
    }

    private File createDirectoryFiles(String path) {
        log.info("creating directories files");
        log.info("directories path: " + path);
        File theDir = new File(path);
        if (!theDir.exists()) {
            log.info("directories not exists");
            try {
                wasCreatedDirectories = theDir.mkdirs();
                log.info("created directories = " + wasCreatedDirectories);
            } catch (SecurityException se) {
                se.printStackTrace();
            }
        }
        return theDir;
    }

    private File createDirectoryFile(String path) {
        log.info("creating directories file");
        log.info("directories path: " + path);
        File theDir = new File(path);
        if (!theDir.exists()) {
            log.info("directories not exists");
            try {
                wasCreatedDirectories = theDir.mkdir();
                log.info("created directories = " + wasCreatedDirectories);
            } catch (SecurityException se) {
                se.printStackTrace();
            }
        }
        return theDir;
    }

    public List<ProjectEntity> getProjectList() {
        return projectList;
    }

    public String getSearchTerm() {
        return searchTerm;
    }

    public void setSearchTerm(String searchTerm) {
        this.searchTerm = searchTerm;
    }

}
