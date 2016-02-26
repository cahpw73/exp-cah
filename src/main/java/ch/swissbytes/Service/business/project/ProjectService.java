package ch.swissbytes.Service.business.project;

import ch.swissbytes.Service.business.mainDocument.AttachmentMainDocumentService;
import ch.swissbytes.Service.business.mainDocument.MainDocumentService;
import ch.swissbytes.Service.business.projectCurrency.ProjectCurrencyService;
import ch.swissbytes.Service.business.projectDocument.ProjectDocumentService;
import ch.swissbytes.Service.business.projectTextSnippet.ProjectTextSnippetService;
import ch.swissbytes.Service.business.textSnippet.TextSnippetService;
import ch.swissbytes.domain.model.entities.*;
import ch.swissbytes.domain.types.StatusEnum;

import javax.inject.Inject;
import javax.transaction.Transactional;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

/**
 * Created by christian on 12/09/14.
 */
public class ProjectService implements Serializable {

    private static final Logger log = Logger.getLogger(ProjectService.class.getName());

    @Inject
    private ProjectDao projectDao;

    @Inject
    private ProjectCurrencyService projectCurrencyService;

    @Inject
    private ProjectTextSnippetService projectTextSnippetService;

    @Inject
    private TextSnippetService textSnippetService;

    @Inject
    private ProjectDocumentService projectDocumentService;

    @Inject
    private AttachmentMainDocumentService attachmentMainDocumentService;

    @Inject
    private MainDocumentService mainDocumentService;


    @Transactional
    public ProjectEntity doSave(ProjectEntity entity) {
        if (entity != null) {
            entity.setStatus(StatusEnum.ENABLE);
            entity.setLastUpdate(new Date());
            entity.setProjectNumber(entity.getProjectNumber().toUpperCase());
            entity.setTitle(entity.getTitle().toUpperCase());
            entity.setInvoiceTo(entity.getInvoiceTo().toUpperCase());
            projectDao.doSave(entity);
            doSaveProjectCurrency(entity);
            doSaveProjectTextSnippet(entity);
            doSaveProjectDocument(entity);
        }
        return entity;
    }

    private void doSaveProjectCurrency(ProjectEntity entity) {
        for (ProjectCurrencyEntity pc : entity.getCurrencies()) {
            if (pc.getId() < 0) {
                pc.setId(null);
            }
            pc.setProject(entity);
            projectCurrencyService.doSave(pc);
        }
    }

    private void doSaveProjectTextSnippet(ProjectEntity entity) {
        for (ProjectTextSnippetEntity pt : entity.getProjectTextSnippetList()) {
            pt.setProject(entity);
            projectTextSnippetService.doSave(pt);
        }
    }

    private void doSaveProjectDocument(ProjectEntity entity) {
        for (ProjectDocumentEntity pt : entity.getProjectDocumentList()) {
            pt.setProject(entity);
            pt.setLastUpdate(new Date());
            projectDocumentService.doSave(pt);
        }
    }

    @Transactional
    public ProjectEntity doUpdate(ProjectEntity entity) {
        if (entity != null) {
            entity.setLastUpdate(new Date());
            entity.setProjectNumber(entity.getProjectNumber().toUpperCase());
            entity.setTitle(entity.getTitle().toUpperCase());
            entity.setInvoiceTo(entity.getInvoiceTo().toUpperCase());
            projectDao.doUpdate(entity);
            doUpdateProjectCurrency(entity);
            doUpdateProjectTextSnippet(entity);
            doUpdateProjectDocument(entity);
            doUpdateMainDocument(entity);
        }
        return entity;
    }

    private void doUpdateProjectCurrency(ProjectEntity entity) {
        for (ProjectCurrencyEntity pc : entity.getCurrencies()) {
            if (pc.getId() < 0) {
                pc.setId(null);
                pc.setProject(entity);
            }
            projectCurrencyService.doUpdate(pc);
        }
    }

    private void doUpdateProjectTextSnippet(ProjectEntity entity) {
        for (ProjectTextSnippetEntity pt : entity.getProjectTextSnippetList()) {
            if (pt.getId() < 0L) {
                pt.setId(null);
                pt.setProject(entity);
            }
            if (pt.getTextSnippet() != null && pt.getTextSnippet().getId() != null && pt.getTextSnippet().getId().longValue() < 0L) {
                pt.setTextSnippet(null);
            }
            pt.setLastUpdate(new Date());
            projectTextSnippetService.doUpdate(pt);
        }
    }

    private void doUpdateProjectDocument(ProjectEntity entity) {
        for (ProjectDocumentEntity pt : entity.getProjectDocumentList()) {
            pt.setProject(entity);
            pt.setLastUpdate(new Date());
            if (pt.getId() < 0) {
                pt.setId(null);
                pt.setProject(entity);
                if (pt.getMainDocumentEntity().getId() < 0) {
                    pt.setMainDocumentEntity(null);
                }
            }
            projectDocumentService.doUpdate(pt);
        }
    }

    private void doUpdateMainDocument(ProjectEntity entity){
        for(MainDocumentEntity md : entity.getMainDocumentList()){
            if(md.getId()<0){
                md.setId(null);
                md.setLastUpdate(new Date());
                md.setStatus(StatusEnum.ENABLE);
                mainDocumentService.doSave(md);
            }
        }
    }

    @Transactional
    public ProjectTextSnippetEntity addNewTextSnippet(ProjectEntity projectEntity, TextSnippetEntity textSnippetEntity) {
        ProjectTextSnippetEntity projectTextSnippetEntity = new ProjectTextSnippetEntity();
        projectTextSnippetEntity.setLastUpdate(new Date());
        projectTextSnippetEntity.setProject(projectEntity);
        projectTextSnippetEntity.setStatus(StatusEnum.ENABLE);
        projectTextSnippetEntity.setCode(textSnippetEntity.getCode());
        projectTextSnippetEntity.setDescription(textSnippetEntity.getTextSnippet());
        projectTextSnippetService.doSave(projectTextSnippetEntity);
        return projectTextSnippetEntity;

    }

    @Transactional
    public ClausesEntity addNewClausesSnippet(ProjectTextSnippetEntity projectTextSnippetEntity) {
        ClausesEntity entity = new ClausesEntity();
        entity.setId(null);
        entity.setLastUpdate(new Date());
        entity.setStatus(StatusEnum.ENABLE);
        entity.setProjectTextSnippet(projectTextSnippetEntity);
        entity.setClauses(projectTextSnippetEntity.getDescription());
        entity.setCode(projectTextSnippetEntity.getCode());
        return entity;
    }


    public ProjectEntity findProjectById(Long projectId) {
        List<ProjectEntity> list = projectDao.findById(ProjectEntity.class, projectId);
        ProjectEntity entity = null;
        if (!list.isEmpty()) {
            entity = list.get(0);
            entity.getCurrencies().addAll(findProjectCurrencyByProjectId(projectId));
        }

        return entity;
    }

    public ProjectEntity findById(final Long projectId) {
        ProjectEntity entity = projectDao.findById(ProjectEntity.class, projectId).get(0);
        return entity != null ? entity : null;
    }

    public List<ProjectEntity> findAllProjects() {
        return projectDao.getProjectList();
    }

    public List<ProjectEntity> doSearch(final String searchTerm) {
        return projectDao.findBySearchTerm(searchTerm);
    }

    public ProjectEntity findByProjectNumber(String projectNumber) {
        List<ProjectEntity> list = projectDao.findByProjectNumber(projectNumber);
        ProjectEntity entity = null;
        if (!list.isEmpty()) {
            entity = list.get(0);
        }
        return entity;
    }

    public boolean existsProjectNumber(String projectNumber) {
        ProjectEntity project = findByProjectNumber(projectNumber);
        return project != null ? true : false;
    }

    public boolean validateDuplicityProjectNumber(final String projectNumber, final Long id) {
        List<ProjectEntity> list = projectDao.findDuplicityProjectNumber(projectNumber, id);
        boolean result = false;
        if (!list.isEmpty()) {
            result = true;
        }
        return result;
    }

    public List<ProjectCurrencyEntity> findProjectCurrencyByProjectId(final Long id) {
        return projectCurrencyService.findByProjectId(id);
    }

    @Transactional
    public List<ProjectTextSnippetEntity> findProjectTextSnippetByProjectId(Long projectId) {
        return projectTextSnippetService.findByProjectId(projectId);
    }

    @Transactional
    public List<ProjectTextSnippetEntity> findProjectTextSnippetByProjectIdOnlyProject(Long projectId) {
        return projectTextSnippetService.findByProjectIdCreatePO(projectId);
    }

    public List<ProjectEntity> findByLogoId(final Long logoId) {
        return projectDao.findByLogoId(logoId);
    }

    public boolean isClientBeingUsed(final Long clientId) {

        return !projectDao.findByClient(clientId).isEmpty();
    }

}
