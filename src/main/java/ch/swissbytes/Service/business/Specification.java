package ch.swissbytes.Service.business;


import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;

/**
 * A specification is a selection criteria that defines the filtering
 * logic to select one or more data.
 *
 * @author Timoteo Ponce
 * @param <T>
 */
public interface Specification<T> {

    /**
     * @param cb
     * @author "Timoteo Ponce <timoteo.ponce@swissbytes.ch>"
     */
    CriteriaQuery<T> buildQuery(CriteriaBuilder cb);

    /**
     * @return
     * @author "Timoteo Ponce <timoteo.ponce@swissbytes.ch>"
     */
    Class<T> targetClass();
}
