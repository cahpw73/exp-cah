package ch.swissbytes.procurement.boundary.User;

import ch.swissbytes.Service.business.enumService.EnumService;
import ch.swissbytes.Service.business.moduleGrantedAccess.ModuleGrantedAccessService;
import ch.swissbytes.Service.business.user.UserService;
import ch.swissbytes.Service.business.userRole.UserRoleService;
import ch.swissbytes.domain.model.entities.ModuleGrantedAccessEntity;
import ch.swissbytes.domain.model.entities.StatusEntity;
import ch.swissbytes.domain.model.entities.UserEntity;
import ch.swissbytes.domain.model.entities.UserRoleEntity;
import ch.swissbytes.domain.types.ModuleSystemEnum;
import ch.swissbytes.domain.types.RoleEnum;
import ch.swissbytes.domain.types.StatusEnum;
import ch.swissbytes.fqmes.boundary.user.VerificationTokenBean;
import org.omnifaces.util.Messages;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Created by Christian on 19/05/2015.
 */
@Named
@ViewScoped
public class UsersBean implements Serializable {

    public static final Logger log = Logger.getLogger(UsersBean.class.getName());

    @Inject
    private UserService userService;

    @Inject
    private UserRoleService userRoleService;

    @Inject
    private ModuleGrantedAccessService moduleGrantedAccessService;

    @Inject
    private EnumService enumService;

    @Inject
    private VerificationTokenBean verificationTokenBean;

    private List<UserEntity> userList;//expediting

    private List<UserEntity> userProcurementList;

    private String searchTerm;

    private String searchProcurementTerm;

    private boolean searchActiveUsers;

    private boolean searchProcurementActiveUsers;

    private UserEntity userSelected;

    private String roleName;

    private String statusName;

    @PostConstruct
    public void init (){
        log.info("UserListBean was created");
        userList = new ArrayList<>();
        userProcurementList = new ArrayList<>();
        loadUsers();
        searchActiveUsers = false;
        searchProcurementActiveUsers = false;
    }

    @PreDestroy
    public void destroy(){
        log.info("UserListBean destroying");
    }

    public void loadUsers(){
        userList = userService.findAllUser();
        userProcurementList = userService.findAllUser();
    }

    public void doSearch(){//expediting
        StatusEnum userStatus = null;
        userList.clear();
        if(searchActiveUsers){
            userStatus = StatusEnum.ENABLE;
        }
        userList = userService.doSearch(searchTerm,userStatus);
    }
    public void doSearchProcurement(){//procurement
        StatusEnum userStatus = null;
        userProcurementList.clear();
        if(searchProcurementActiveUsers){
            userStatus = StatusEnum.ENABLE;
        }
        userProcurementList = userService.doSearch(searchProcurementTerm,userStatus);
    }

    public void doClean(){
        userList.clear();
        loadUsers();
        searchTerm = "";
        searchActiveUsers = false;
    }

    public void doProcurementClean(){
        userList.clear();
        loadUsers();
        searchProcurementTerm = "";
        searchActiveUsers = false;
    }

    public void resetPassword(final String email){
        verificationTokenBean.sendLostPasswordToken(email);
    }

    public boolean getActiveUser(final StatusEntity status){
        return (StatusEnum.ENABLE.getId().intValue() == status.getId().intValue());
    }

    public boolean getAccessModuleProcurement(final Long userId){
        ModuleGrantedAccessEntity moduleProcurement = moduleGrantedAccessService.findByUserIdAndModuleSystem(userId, ModuleSystemEnum.PROCUREMENT);
        boolean hasAccess = false;
        if(moduleProcurement != null && moduleProcurement.getModuleAccess() != null){
            hasAccess = moduleProcurement.getModuleAccess();
        }
        return hasAccess;
    }

    public boolean getAccessModuleExpediting(final Long userId){
        ModuleGrantedAccessEntity moduleProcurement = moduleGrantedAccessService.findByUserIdAndModuleSystem(userId, ModuleSystemEnum.EXPEDITING);
        boolean hasAccess = false;
        if(moduleProcurement != null && moduleProcurement.getModuleAccess() != null){
            hasAccess = moduleProcurement.getModuleAccess();
        }
        return hasAccess;
    }

    public String getAccessLevelProcurement(final Long userId){
        UserRoleEntity userRole = userRoleService.findByUserIdAndModuleSystem(userId, ModuleSystemEnum.PROCUREMENT);
        if(userRole!=null&&userRole.getRole() != null) {
            RoleEnum roleEnum = RoleEnum.of(userRole.getRole());
            return roleEnum.getLabel();
        }else{
            return "";
        }
    }

    public String getAccessLevelExpediting(final Long userId){
        UserRoleEntity userRole = userRoleService.findByUserIdAndModuleSystem(userId, ModuleSystemEnum.EXPEDITING);
        if(userRole!=null&&userRole.getRole() != null) {
            RoleEnum roleEnum = RoleEnum.of(userRole.getRole());
            return roleEnum.getLabel();
        }else{
            return "";
        }
    }

    public void loadCurrentUserToProcurement(){
        statusName = StatusEnum.valueOf(userSelected.getStatus().getId()).getLabel();
        roleName = getAccessLevelProcurement(userSelected.getId());
    }
    public void loadCurrentUserToExpediting(){
        statusName = StatusEnum.valueOf(userSelected.getStatus().getId()).getLabel();
        roleName = getAccessLevelExpediting(userSelected.getId());
    }


    public String doDelete() {
        log.info("trying to delete user");
        StatusEntity statusEntity = enumService.getFindRoleByStatusEnumId(StatusEnum.DELETED.getId());
        if (statusEntity != null) {
            userSelected.setStatus(statusEntity);
            userService.doUpdate(userSelected);
            Messages.addFlashGlobalInfo("User was delete!");
            return "list?faces-redirect=true";
        } else {
            Messages.addGlobalError("There are data invalid! ");
            return "";
        }
    }

    public List<UserEntity> getUserList() {
        return userList;
    }

    public String getSearchTerm() {
        return searchTerm;
    }

    public void setSearchTerm(String searchTerm) {
        this.searchTerm = searchTerm;
    }

    public boolean isSearchActiveUsers() {
        return searchActiveUsers;
    }

    public void setSearchActiveUsers(boolean searchActiveUsers) {
        this.searchActiveUsers = searchActiveUsers;
    }

    public List<UserEntity> getUserProcurementList() {
        return userProcurementList;
    }


    public String getSearchProcurementTerm() {
        return searchProcurementTerm;
    }

    public void setSearchProcurementTerm(String searchProcurementTerm) {
        this.searchProcurementTerm = searchProcurementTerm;
    }

    public boolean isSearchProcurementActiveUsers() {
        return searchProcurementActiveUsers;
    }

    public void setSearchProcurementActiveUsers(boolean searchProcurementActiveUsers) {
        this.searchProcurementActiveUsers = searchProcurementActiveUsers;
    }

    public UserEntity getUserSelected() {
        return userSelected;
    }

    public void setUserSelected(UserEntity userSelected) {
        this.userSelected = userSelected;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public String getStatusName() {
        return statusName;
    }

    public void setStatusName(String statusName) {
        this.statusName = statusName;
    }
}
