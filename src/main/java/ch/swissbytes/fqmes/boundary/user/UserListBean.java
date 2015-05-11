package ch.swissbytes.fqmes.boundary.user;

import ch.swissbytes.Service.business.user.UserDao;
import ch.swissbytes.domain.model.entities.UserEntity;
import org.primefaces.model.LazyDataModel;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by Christian on 9/12/14.
 */
@Named
@ViewScoped
public class UserListBean implements Serializable {

    public static final Logger log = Logger.getLogger(UserListBean.class.getName());



    @Inject
    private UserDao dao;
    private UserDataModel tbl;


    @PostConstruct
    public void create(){
        log.info("creating bean purchase list");
        log.log(Level.FINER,"FINER log");
        tbl=new UserDataModel(dao);
    }

    @PreDestroy
    public void destroy(){
        log.info("destroying bean");
    }

    public LazyDataModel<UserEntity> getList(){
        return tbl;
    }

    public String goToEditUser(final Long userId){
        return "/user/edit?faces-redirect=true&userId=" + userId ;
    }

}
