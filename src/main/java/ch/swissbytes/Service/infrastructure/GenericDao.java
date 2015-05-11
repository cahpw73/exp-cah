package ch.swissbytes.Service.infrastructure;

import ch.swissbytes.Service.business.Specification;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public abstract class GenericDao<T> {

    private static final Logger LOG = Logger.getLogger(GenericDao.class.getName());

    @PersistenceContext(unitName = "fqmPU")
    protected EntityManager entityManager;

    /**
     * Returns entities matching with the given classType
     * and the given ID, as all IDs are number-based they are all mapped
     * as Long, so there's no need to handle them as objects.
     * <p/>
     * <pre>
     * Usage:
     *  List&lt;TUser&gt; users = model.findById( TUser.class , 1L );
     *
     * </pre>
     *
     * @param clazz entity target class
     * @param id    not-null Long number
     * @return a list of elements matching the ID-equals criteria
     */
    public <T> List<T> findById(final Class<T> clazz, final Long id) {
        return findEquals(clazz, "id", id);
    }

    public <T> List<T> findById(final Class<T> clazz, final Integer id) {
        return findEquals(clazz, "id", id);
    }

    /**
     * Return a single entities matching with the given classType
     * and the given ID, as all IDs are number-based they are all mapped
     * as Long, so there's no need to handle them as objects.
     * <p/>
     * <pre>
     * Usage:
     * Optional&lt;TUser&gt; result = model.getById( TUser.class, 1L);
     * if( result.isPresent() ){
     *   TUser user = result.get();
     * }
     * </pre>
     *
     * @param clazz entity target class
     * @param id    not-null Long number
     * @return Optional element of the result, {@link Optional#ABSENT} if there
     *         are not results
     */
    public <T> Optional<T> getById(final Class<T> clazz, final Long id) {
        return Optional.firstElement(findById(clazz, id));
    }

    public <T> Optional<T> getById(final Class<T> clazz, final Integer id) {
        return Optional.firstElement(findById(clazz, id));
    }

    /**
     * Returns a single entity matching with the given classType
     * and the property equality.
     *
     * @param clazz        entity target class
     * @param propertyName property/field name to compare
     * @param value        value of the property to compare with
     * @return Optional element of the result, {@link Optional#ABSENT} if there
     *         are not results
     */
    public <T> Optional<T> getBy(final Class<T> clazz, final String propertyName, final Object value) {
        return Optional.firstElement(findEquals(clazz, propertyName, value));
    }

    /**
     * Returns entities matching with the given classType
     * and the given property.
     * <p/>
     * <pre>
     * Usage:
     *  List&lt;TUser&gt; users = model.findById( TUser.class , "login" , "admin" );
     *
     * </pre>
     *
     * @param clazz        entity target class
     * @param propertyName
     * @param value
     * @return a list of elements matching the property-equals criteria
     * @author timoteo
     */
    public <T> List<T> findEquals(final Class<T> clazz, final String propertyName, final Object value) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<T> query = cb.createQuery(clazz);
        Root<T> root = query.from(clazz);
        query.where(cb.equal(root.get(propertyName), value));
        return entityManager.createQuery(query).getResultList();
    }


    public <T> Number countEquals(final Class<T> clazz, final String propertyName, final Object value) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> query = cb.createQuery(Long.class);
        Root<T> root = query.from(clazz);
        query.select(cb.count(query.from(clazz)));
        query.where(cb.equal(root.get(propertyName), value));
        return entityManager.createQuery(query).getSingleResult();
    }

    /**
     * @param clazz
     * @param propertyName
     * @param value
     * @return
     */
    public <T> boolean exists(final Class<T> clazz, final String propertyName, final Object value) {
        return countEquals(clazz, propertyName, value).intValue() > 0;
    }

    /**
     * @param clazz
     * @return
     */
    public <T> List<T> findAll(final Class<T> clazz) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<T> query = cb.createQuery(clazz);
        Root<T> root = query.from(clazz);
        return entityManager.createQuery(query).getResultList();
    }

    /**
     * @param entity
     */
    public <T> void save(final T entity) {
        entityManager.persist(entity);
    }

    public <T> void saveAll(final Collection<T> entities) {
        for (T item : entities)
            entityManager.persist(item);
    }

    public <T> void saveAndFlush(final T entity) {
        entityManager.persist(entity);
        entityManager.flush();
    }

    public <T> void saveAndFlushAll(final Collection<T> entities) {
        for (T item : entities) {
            entityManager.persist(item);
        }
        entityManager.flush();
    }

    /**
     * @param entity
     * @return
     */

    public <T> T merge(final T entity) {
        return entityManager.merge(entity);
    }

    /**
     * @param entity
     */
    public <T> void delete(final T... entity) {
        for (T item : entity) {
            entityManager.remove(item);
        }
    }

    /**
     * Performs a specification-based criteria selection, it delegates the
     * filtering to the given specification and return the results.
     * <p/>
     * <pre>
     * Usage:
     * List&lt;TUSer&gt; users = model.find( UserSpec.of("admin") );
     * </pre>
     *
     * @param specification specification to delegate filtering
     * @return list of matching elements
     * @author timoteo
     */
    public <T> List<T> find(final Specification<T> specification) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        return entityManager.createQuery(specification.buildQuery(cb)).getResultList();
    }

    protected <T> List<T> findBy(final String queryStr, Map<String, Object> values) {
        Query query = entityManager.createQuery(queryStr);
        for (Map.Entry<String, Object> entry : values.entrySet()) {
            query.setParameter(entry.getKey(), entry.getValue());
        }
        return query.getResultList();
    }


    /**
     * Performs a specification-based criteria unique element, it delegates the
     * filtering to the given specification and return the results.
     * <p/>
     * <pre>
     * Usage:
     * Optional&lt;TUSer&gt; user = model.get( UserSpec.of("admin") );
     * </pre>
     *
     * @param specification specification to delegate filtering
     * @return optional of matching element
     * @author timoteo
     */
    public <T> Optional<T> get(final Specification<T> specification) {
        return Optional.firstElement(find(specification));
    }

    /**
     * Evaluates if a given specification is satisfied by checking the amount of
     * results,
     * if there are results present the specification is satisfied.
     *
     * @param specification specification to delegate filtering
     * @return evaluation of the specification results, returning TRUE if it has
     *         any
     * @author timoteo
     *
    public <T> boolean isSatisfied(final Specification<T> specification) {
    return countOfSpec(specification) > 0;
    }
    /*
     @Override public <T> Optional<T> getBy(final Class<T> targetClass, final Map<String, Object> properties) {
     return Optional.firstElement(findBy(targetClass, properties));
     }

     @Override public <T> long countOfSpec(final Specification<T> spec) {
     CriteriaBuilder cb = entityManager.getCriteriaBuilder();
     CriteriaQuery<T> query = specification.buildQuery(cb);
     query.select(cb.count(query.from(spec.targetClass())));
     return (Number) entityManager.createQuery(query).getSingleResult();
     }

     @Override public <T extends EntityTbl> Collection<T> findByIdIn(final Class<T> targetClass, final List<Long> iDs) {
     Criteria criteria = sessionFactory.getCurrentSession().createCriteria(targetClass);
     criteria.add(Restrictions.in("id", iDs));
     return criteria.list();
     }
     */
    public <T> List<T> findByPage(int startAt, int maxPerPage,final Filter filter ) {
        Query query = entityManager.createQuery(createQuery(false,filter));
        query.setMaxResults(maxPerPage);
        query.setFirstResult(startAt);
        applyCriteriaValues(query,filter);
        return query.getResultList();
    }
    protected String createQuery(final boolean count,Filter filter){
        StringBuilder query=new StringBuilder();
        query.append(select(count));
        query.append(from());
        query.append(" WHERE 1=1 ");
        String prepositions=addCriteria(filter);
        if(prepositions!=null&&!prepositions.isEmpty()){
            //query.append(" AND ");
            query.append(prepositions);
        }
        if(!count){
            query.append(orderBy());
        }
        return query.toString();
    }
    protected String select(boolean count){
        String select;
        if(count){
            select="SELECT COUNT(x) ";
        }else{
            select="SELECT x ";
        }
        return select;
    }
    protected String from(){
        StringBuilder sb=new StringBuilder();
        sb.append(" FROM ");
        sb.append(getEntity());
        sb.append(" x ");
        return sb.toString();
    }

    protected String createQuery(final boolean count,Filter filter,String sortBy,boolean ascending){
        StringBuilder query=new StringBuilder();
        query.append(select(count));
        query.append(from());
        query.append(" WHERE 1=1 ");
        String prepositions=addCriteria(filter);
        if(prepositions!=null&&!prepositions.isEmpty()){
            //query.append(" AND ");
            query.append(prepositions);
        }
        if(!count){
            query.append(orderBy(sortBy,ascending));
        }
        return query.toString();
    }


    protected abstract void applyCriteriaValues(Query query,Filter filter);

    protected abstract String getEntity();

    public String orderBy(){
        return " ORDER BY id DESC ";
    }
    public String orderBy(String field,boolean ascending){
        return "";
    }

    /**
     * Adds preposition for pagination.
     * @author Alvaro Cardozo.
     * */
    protected abstract String addCriteria(Filter filter);

    public Long findTotal(Filter filter) {
        Query query = entityManager.createQuery(createQuery(true,filter));
        applyCriteriaValues(query,filter);
        return Long.parseLong(query.getSingleResult().toString());
    }
     public T update(final T entity) {
        T entityManaged=entityManager.merge(entity);
        entityManager.persist(entityManaged);
        return entityManaged;
    }

    public T load(final Class<T>clazz,final Long id){
        List<T>list=findById(clazz,id);
        return list.isEmpty()?null:list.get(0);
    }
    public <T> List<T> findByPage(int startAt, int maxPerPage,final Filter filter,String sort,boolean ascending ) {
        Query query = entityManager.createQuery(createQuery(false,filter,sort,ascending));
        query.setMaxResults(maxPerPage);
        query.setFirstResult(startAt);
        applyCriteriaValues(query,filter);
        return query.getResultList();
    }

}
