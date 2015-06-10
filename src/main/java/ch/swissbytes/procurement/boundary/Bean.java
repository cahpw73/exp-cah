package ch.swissbytes.procurement.boundary;

import ch.swissbytes.domain.model.entities.ContactEntity;
import ch.swissbytes.domain.types.ModeOperationEnum;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.logging.Logger;

/**
 * Created by alvaro on 30-05-15.
 */
public class Bean implements Serializable{

    protected ModeOperationEnum  modeOperationEnum;

    private static final Logger log = Logger.getLogger(Bean.class.getName());

    @PostConstruct
    public void create() {
        log.info(this.getClass().getName()+ "  has been created");
        initialize();
    }

    @PreDestroy
    public void destroy() {
        ending();
        log.info(this.getClass().getName()+ "  has been destroyed!");

    }

    protected void ending(){
        //sub class should implement something
    }

    protected  void initialize(){
        //sub class should implement something
    }

    public void putModeCreation(){
        modeOperationEnum=ModeOperationEnum.NEW;
    }
    public void putModeEdition(){
        modeOperationEnum=ModeOperationEnum.UPDATE;
    }
    public void putModeView(){
        modeOperationEnum=ModeOperationEnum.VIEW;
    }
    public void putModeDeletion(){
        modeOperationEnum=ModeOperationEnum.DELETE;
    }

    public boolean isBeingCreated(){
        return modeOperationEnum!=null?modeOperationEnum.ordinal()==ModeOperationEnum.NEW.ordinal():false;
    }

    public boolean isBeingUpdated(){
        return modeOperationEnum!=null?modeOperationEnum.ordinal()==ModeOperationEnum.UPDATE.ordinal():false;
    }

    public boolean isBeingViewed(){
        return modeOperationEnum!=null?modeOperationEnum.ordinal()==ModeOperationEnum.VIEW.ordinal():false;
    }
    public boolean isBeingDeleted(){
        return modeOperationEnum!=null?modeOperationEnum.ordinal()==ModeOperationEnum.DELETE.ordinal():false;
    }

    public boolean isBeingEdited(){
        return modeOperationEnum!=null?(modeOperationEnum.ordinal()==ModeOperationEnum.NEW.ordinal() || modeOperationEnum.ordinal()==ModeOperationEnum.UPDATE.ordinal()):false;
    }

}
