package ch.swissbytes.fqmes.boundary.projectUser;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import java.io.Serializable;
import java.util.logging.Logger;

/**
 * Created by Christian on 02/03/2016.
 */
@Named
@ViewScoped
public class ProjectUserBean implements Serializable {

    private static final Logger log = Logger.getLogger(ProjectUserBean.class.getName());

    private Long userId;

    @PostConstruct
    public void init() {
        log.info("create ProjectUserBean");
    }

    @PreDestroy
    public void destroy() {
        log.info("destroy ProjectUserBean");
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
