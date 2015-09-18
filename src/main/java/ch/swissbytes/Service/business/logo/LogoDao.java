package ch.swissbytes.Service.business.logo;

import ch.swissbytes.Service.infrastructure.Filter;
import ch.swissbytes.Service.infrastructure.GenericDao;
import ch.swissbytes.domain.model.entities.*;
import ch.swissbytes.domain.types.StatusEnum;

import javax.persistence.Query;
import java.io.Serializable;
import java.util.*;
import java.util.logging.Logger;

/**
 * Created by alvaro on 9/10/14.
 */

public class LogoDao extends GenericDao<LogoEntity> implements Serializable {

    private static final Logger log = Logger.getLogger(LogoDao.class.getName());


    public void doSave(LogoEntity brandEntity) {
        super.save(brandEntity);
    }

    public void doUpdate(LogoEntity detachedEntity) {
        super.update(detachedEntity);
    }

    public List<LogoEntity> getLogoList() {
        return getLightList();
    }


    private List<LogoEntity> getLightList() {
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT x.id,x.fileName,x.mimeType,x.path,x.description ");
        sb.append("FROM LogoEntity x ");
        sb.append("WHERE x.status=:ENABLED ");
        sb.append("ORDER BY x.description ");
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("ENABLED", StatusEnum.ENABLE);
        List<Object> list = super.findBy(sb.toString(), map);
        List<LogoEntity> logos = new ArrayList<>();
        for (Object object : list) {
            Object[] record = (Object[]) object;
            LogoEntity logo = new LogoEntity();
            logo.setId(Long.parseLong(record[0].toString()));
            logo.setFileName(record[1].toString());
            logo.setMimeType(record[2].toString());
            logo.setPath(record[3] != null ? record[3].toString() : null);
            logo.setDescription(record[4] != null ? record[4].toString() : null);
            logos.add(logo);
        }
        return logos;
    }


    @Override
    protected void applyCriteriaValues(Query query, Filter filter) {

    }

    @Override
    protected String getEntity() {
        return null;
    }

    @Override
    protected String addCriteria(Filter filter) {
        return null;
    }


    public List<LogoEntity> findByFileName(String fileName) {
        StringBuilder sb = new StringBuilder();
        sb.append(" SELECT l ");
        sb.append(" FROM LogoEntity l ");
        sb.append(" WHERE l.status = :ENABLE ");
        sb.append(" AND LOWER(l.fileName) = :FILE_NAME ");
        Map<String, Object> params = new HashMap<>();
        params.put("ENABLE", StatusEnum.ENABLE);
        params.put("FILE_NAME", fileName.trim().toLowerCase());
        return super.findBy(sb.toString(), params);
    }

    public List<LogoEntity> findAll() {
        StringBuilder sb = new StringBuilder();
        sb.append(" SELECT l ");
        sb.append(" FROM LogoEntity l ");
        sb.append(" WHERE l.status = :ENABLE ");
        sb.append("ORDER BY l.description ");
        Map<String, Object> params = new HashMap<>();
        params.put("ENABLE", StatusEnum.ENABLE);
        return super.findBy(sb.toString(), params);
    }

    public List<LogoEntity> existsLogoDescription(final String description, final Long id) {
        StringBuilder sb = new StringBuilder();
        sb.append(" SELECT l ");
        sb.append(" FROM LogoEntity l ");
        sb.append(" WHERE l.status = :ENABLE ");
        sb.append(" AND LOWER(l.description) = :DESCRIPTION ");
        if(id!=null){
            sb.append(" AND NOT l.id = :ID ");
        }
        Map<String, Object> params = new HashMap<>();
        params.put("ENABLE", StatusEnum.ENABLE);
        params.put("DESCRIPTION",description.toLowerCase());
        if(id!=null) {
            params.put("ID", id);
        }
        return super.findBy(sb.toString(), params);
    }
}
