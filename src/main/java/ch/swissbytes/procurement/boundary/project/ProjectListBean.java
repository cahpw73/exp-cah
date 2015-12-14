package ch.swissbytes.procurement.boundary.project;

import ch.swissbytes.Service.business.project.ProjectService;
import ch.swissbytes.domain.model.entities.*;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
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

    @PostConstruct
    public void init (){
        log.info("ProjectsBean was created");
        projectList = new ArrayList<>();
        loadProjects();
    }

    @PreDestroy
    public void destroy(){
        log.info("ProjectsBean destroying");
    }

    public void loadProjects(){
        projectList = projectService.findAllProjects();
    }

    public void doSearch(){
        projectList.clear();
        projectList = projectService.doSearch(searchTerm);
    }

    public void doClean(){
        projectList.clear();
        loadProjects();
        searchTerm = "";
    }

    public void listFolders(){
        String pathMain = System.getProperty("fqmes.path.export.root");
        File f = new File(pathMain);
        System.out.println("PATH "+pathMain);
        System.out.println("File name "+ f.getName());
        System.out.println("Absolute path "+f.getAbsolutePath());
        System.out.println("Another path "+f.getPath());
        File[] ficheros = f.listFiles();
        if(ficheros!=null) {
            for (int x = 0; x < ficheros.length; x++) {
                System.out.println("1: " + ficheros[x].getName());
                String subPath = pathMain + File.separator + ficheros[x].getName();
                File subFile = new File(subPath);
                File[] subficheros = subFile.listFiles();
                for (int i = 0; i < subficheros.length; i++) {
                    System.out.println("2: " + subficheros[i].getName());
                    String subPath2 = pathMain + File.separator + ficheros[x].getName() + File.separator + subficheros[i].getName();
                    File subFile2 = new File(subPath2);
                    File[] subficheros2 = subFile2.listFiles();
                    for (int j = 0; j < subficheros2.length; j++) {
                        System.out.println("3: " + subficheros2[j].getName());
                        String subPath3 = pathMain + File.separator + ficheros[x].getName() + File.separator + subficheros[i].getName() + File.separator + subficheros2[j].getName();
                        File subFile3 = new File(subPath3);
                        File[] subficheros3 = subFile3.listFiles();
                        for (int k = 0; k < subficheros3.length; k++) {
                            System.out.println("4." + k + ": " + subficheros3[k].getName());
                        }
                    }
                }
            }
        }else{
            System.out.println("Something is happening, it is no capable of retrieving anything");
        }
    }

    public void deleteFile(){
        String pathCMS = System.getProperty("fqmes.path.export.cms");
        pathCMS = pathCMS.replace("{project_field}", "test");
        File fichero = new File(pathCMS+File.separator+generateFileName());
        if (fichero.delete())
            System.out.println("File was deleted successfully");
        else
            System.out.println("File can't be deleted");

        File directorio = new File(pathCMS);
        if (directorio.delete())
            System.out.println("File was deleted successfully");
        else
            System.out.println("File can't be deleted");

        File directorio1 = new File(pathCMS.replace("\\Procurement Commitments",""));
        if (directorio1.delete())
            System.out.println("File was deleted successfully");
        else
            System.out.println("File can't be deleted");

        File directorio2 = new File(pathCMS.replace("\\300 Project Controls\\Procurement Commitments",""));
        if (directorio2.delete())
            System.out.println("File was deleted successfully");
        else
            System.out.println("File can't be deleted");

    }

    public void createFile(){
        String pathCMS = System.getProperty("fqmes.path.export.cms");
        pathCMS = pathCMS.replace("{project_field}", "test");
        FileOutputStream out = null;
        File file = createDirectoryFiles(pathCMS);
        File newFile = new File(file.getAbsolutePath()+File.separator+generateFileName());
        try {
            if(newFile.createNewFile()){
                System.out.println("created file");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            out = new FileOutputStream(newFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try {
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String generateFileName() {
        SimpleDateFormat format = new SimpleDateFormat("dd MMM yy");
        String dateStr = format.format(new Date());
        String fileName = dateStr.toUpperCase() + " - " + "TESTCommitments.xlsx";
        return fileName;
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
