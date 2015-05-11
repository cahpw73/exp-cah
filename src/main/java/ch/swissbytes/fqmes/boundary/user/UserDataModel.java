package ch.swissbytes.fqmes.boundary.user;

import ch.swissbytes.Service.business.user.UserDao;
import ch.swissbytes.domain.model.entities.UserEntity;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Created by alvaro on 9/8/14.
 */
public class UserDataModel extends LazyDataModel<UserEntity> {

    private static final Logger log = Logger.getLogger(UserDataModel.class.getName());

    //@Inject
    private UserDao dao;

    public UserDataModel(UserDao dao){
        log.info("creating tbl ");
        log.info((dao!=null?"dao has value":"dao doesnt have value"));
        this.dao=dao;
    }


    @Override
    public List<UserEntity> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
        List<UserEntity> userList= dao.findByPage(first, pageSize, null);
        if(super.getRowCount()<=0){
            Long total= dao.findTotal(null);
            this.setRowCount(total.intValue());
        }
        this.setPageSize(pageSize);
        return userList;
    }
}
