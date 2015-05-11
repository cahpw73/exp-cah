package ch.swissbytes.fqmes.control;

import ch.swissbytes.domain.repository.GenericDao;
import ch.swissbytes.domain.repository.entities.EntityTbl;
import ch.swissbytes.domain.repository.types.StatusEnum;
import org.apache.commons.beanutils.BeanUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by alvaro on 9/22/14.
 */
public class Service<T> implements Serializable {

  private GenericDao<T> dao;


  public void initialize(GenericDao dao){
      this.dao=dao;
  }

    public void doSave(T entity){
        dao.save(entity);
    }

    public void doUpdate(T entity){
        dao.update(entity);
    }

    public List<T> getActives(List<T> entities){
        List<T>list=new ArrayList<>();
        if(entities!=null){
            for(T object:entities){
                if(object instanceof EntityTbl){
                    EntityTbl entity=(EntityTbl)object;
                    if(StatusEnum.ENABLE.equalsTo(entity.getStatus().getId())){
                        list.add((T)entity);
                    }
                }
            }
        }
        return list;
    }

    public Integer getIndexById(final Long id,final List list){
        int index=-1;
        int counter=0;
        for(final Object object: list){
            EntityTbl entity=(EntityTbl)object;
            if(entity.getId().intValue()==id.intValue()){
                index=counter;
                break;
            }
            counter++;
        }
        return index;
    }
    public T clone(T target){
        T entityCloned=null;
        try{
            entityCloned=(T)BeanUtils.cloneBean(target);
        }catch (Exception ex){

        }
        return entityCloned;
    }
}
