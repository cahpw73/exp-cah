package ch.swissbytes.Service.business.purchase;

import ch.swissbytes.Service.infrastructure.Filter;
import ch.swissbytes.Service.infrastructure.GenericDao;
import ch.swissbytes.domain.model.entities.VPurchaseOrder;
import ch.swissbytes.domain.types.ExpeditingStatusEnum;
import ch.swissbytes.fqmes.boundary.purchase.SearchPurchase;
import ch.swissbytes.fqmes.util.Util;
import org.apache.commons.lang3.StringUtils;

import javax.persistence.Query;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

/**
 * Created by alvaro on 9/8/14.
 */

public class PurchaseOrderViewDao extends GenericDao<VPurchaseOrder> implements Serializable {

    private static final Logger log = Logger.getLogger(PurchaseOrderViewDao.class.getName());


    public VPurchaseOrder load(final Long id) {
        return super.load(VPurchaseOrder.class, id);
    }

    @Override
    protected void applyCriteriaValues(Query query, Filter f) {
        log.info("protected  void applyCriteriaValues(Query query,Filter f)");
        if (f != null) {
            log.info("filtering.....");
            SearchPurchase filter = (SearchPurchase) f;
            if (StringUtils.isNotBlank(filter.getProject()) && StringUtils.isNotEmpty(filter.getProject())) {
                query.setParameter("PROJECT", "%" + filter.getProject().trim() + "%");
            }
            if (StringUtils.isNotBlank(filter.getPo()) && StringUtils.isNotEmpty(filter.getPo())) {
                query.setParameter("PO", "%" + filter.getPo().trim() + "%");
            }
            if (StringUtils.isNotBlank(filter.getVariation()) && StringUtils.isNotEmpty(filter.getVariation())) {
                query.setParameter("VARIATION", "%" + filter.getVariation().trim() + "%");
            }
            if (StringUtils.isNotBlank(filter.getPoTitle()) && StringUtils.isNotEmpty(filter.getPoTitle())) {
                query.setParameter("PO_TITLE", "%" + filter.getPoTitle().trim() + "%");
            }
            if (StringUtils.isNotBlank(filter.getExpeditingTitle()) && StringUtils.isNotEmpty(filter.getExpeditingTitle())) {
                query.setParameter("EXPEDITING_TITLE", "%" + filter.getExpeditingTitle().trim() + "%");
            }
            if (StringUtils.isNotBlank(filter.getSupplier()) && StringUtils.isNotEmpty(filter.getSupplier())) {
                query.setParameter("SUPPLIER", "%" + filter.getSupplier().trim() + "%");
            }
            if (StringUtils.isNotBlank(filter.getResponsibleExpediting()) && StringUtils.isNotEmpty(filter.getResponsibleExpediting())) {
                query.setParameter("RESPONSIBLE_EXPEDITING", "%" + filter.getResponsibleExpediting().trim() + "%");
            }
            if (StringUtils.isNotEmpty(filter.getIncoTerm()) && StringUtils.isNotBlank(filter.getIncoTerm())) {
                query.setParameter("INCO_TERM", "%" + filter.getIncoTerm().trim() + "%");
            }
            if (!filter.getProjectsAssignedId().isEmpty()) {
                query.setParameter("PROJECT_ASSIGN_IDS", filter.getProjectsAssignedId());
            }
            if (filter.getTypeDateReport() != null) {
                if (filter.getStartDateReport() != null) {
                    switch (filter.getTypeDateReport()) {
                        case PO_DELIVERY_DATE:
                            query.setParameter("START_PO_DELIVERY_DATE", filter.getStartDateReport());
                            break;
                        case REQUIRED_ON_SITE_DATE:
                            query.setParameter("START_REQUIRED_ON_SITE_DATE", filter.getStartDateReport());
                            break;
                        case ACTUAL_ON_SITE_DATE:
                            query.setParameter("START_ACTUAL_ON_SITE_DATE", filter.getStartDateReport());
                            break;
                        case NEXT_KEY_DATE:
                            query.setParameter("START_NEXT_KEY_DATE", filter.getStartDateReport());
                            break;
                        default:
                            break;
                    }
                }
                if (filter.getEndDateReport() != null) {
                    switch (filter.getTypeDateReport()) {
                        case PO_DELIVERY_DATE:
                            query.setParameter("END_PO_DELIVERY_DATE", filter.getEndDateReport());
                            break;
                        case REQUIRED_ON_SITE_DATE:
                            query.setParameter("END_REQUIRED_ON_SITE_DATE", filter.getEndDateReport());
                            break;
                        case ACTUAL_ON_SITE_DATE:
                            query.setParameter("END_ACTUAL_ON_SITE_DATE", filter.getEndDateReport());
                            break;
                        case NEXT_KEY_DATE:
                            query.setParameter("END_NEXT_KEY_DATE", filter.getEndDateReport());
                            break;
                        default:
                            break;
                    }
                }
            }
            prepareValueExpeditingStatuses(query, filter);
            prepareValueSubquery(query, filter);
        }
    }

    private void prepareValueExpeditingStatuses(Query query, SearchPurchase filter) {
        if (StringUtils.isNotEmpty(filter.getStatuses()) && StringUtils.isNotBlank(filter.getStatuses())) {
            String[] statuses = filter.getStatuses().split(",");
            List<ExpeditingStatusEnum> list = new ArrayList<>();
            for (String status : statuses) {
                try {
                    list.add(ExpeditingStatusEnum.getEnum(Integer.parseInt(status)));
                } catch (NumberFormatException nfe) {

                }
            }
            query.setParameter("PURCHASE_ORDER_STATUS_LIST", list);
        }
    }

    private void prepareValueSubquery(Query query, SearchPurchase filter) {
        if (filter.getTypeDateReport() != null) {
            if (filter.getStartDateReport() != null) {
                switch (filter.getTypeDateReport()) {
                    case FORECAST_EX_WORKS_DATE:
                        query.setParameter("START_FORECAST_EX_WORKS_DATE", filter.getStartDateReport());
                        break;
                    case ACTUAL_EX_WORKS_DATE:
                        query.setParameter("START_ACTUAL_EX_WORKS_DATE", filter.getStartDateReport());
                        break;
                    case FORECAST_SITE_DATE:
                        query.setParameter("START_FORECAST_SITE_DATE", filter.getStartDateReport());
                        break;
                    case ACTUAL_SITE_DATE:
                        query.setParameter("START_ACTUAL_SITE_DATE", filter.getStartDateReport());
                        break;
                    case REQUIRED_SITE_DATE:
                        query.setParameter("START_REQUIRED_SITE_DATE", filter.getStartDateReport());
                        break;
                    default:
                        break;
                }
            }
            if (filter.getEndDateReport() != null) {
                switch (filter.getTypeDateReport()) {
                    case FORECAST_EX_WORKS_DATE:
                        query.setParameter("END_FORECAST_EX_WORKS_DATE", filter.getEndDateReport());
                        break;
                    case ACTUAL_EX_WORKS_DATE:
                        query.setParameter("END_ACTUAL_EX_WORKS_DATE", filter.getEndDateReport());
                        break;
                    case FORECAST_SITE_DATE:
                        query.setParameter("END_FORECAST_SITE_DATE", filter.getEndDateReport());
                        break;
                    case ACTUAL_SITE_DATE:
                        query.setParameter("END_ACTUAL_SITE_DATE", filter.getEndDateReport());
                        break;
                    case REQUIRED_SITE_DATE:
                        query.setParameter("END_REQUIRED_SITE_DATE", filter.getEndDateReport());
                        break;
                    default:
                        break;
                }
            }
        }

    }

    protected String getEntity() {
        return VPurchaseOrder.class.getSimpleName();
    }

    @Override
    protected String addCriteria(Filter f) {
        log.info("protected  String addCriteria(Filter f)");
        StringBuilder sb = new StringBuilder();
        if (f != null) {
            log.info("filtering.....");
            SearchPurchase filter = (SearchPurchase) f;
            if (StringUtils.isNotBlank(filter.getProject()) && StringUtils.isNotEmpty(filter.getProject())) {
                sb.append(" AND lower(x.project) like lower(:PROJECT)");
            }
            if (StringUtils.isNotBlank(filter.getPo()) && StringUtils.isNotEmpty(filter.getPo())) {
                sb.append(" AND lower(x.po) like lower(:PO)");
            }
            if (StringUtils.isNotBlank(filter.getPoTitle()) && StringUtils.isNotEmpty(filter.getPoTitle())) {
                sb.append(" AND lower(x.poTitle) like lower(:PO_TITLE)");
            }
            if (StringUtils.isNotBlank(filter.getExpeditingTitle()) && StringUtils.isNotEmpty(filter.getExpeditingTitle())) {
                sb.append(" AND lower(x.expeditingTitle) like lower(:EXPEDITING_TITLE)");
            }
            if (StringUtils.isNotBlank(filter.getVariation()) && StringUtils.isNotEmpty(filter.getVariation())) {
                sb.append(" AND lower(x.variation) like lower(:VARIATION)");
            }
            if (StringUtils.isNotBlank(filter.getSupplier()) && StringUtils.isNotEmpty(filter.getSupplier())) {
                sb.append(" AND lower(x.supplier) like lower(:SUPPLIER)");
            }
            if (StringUtils.isNotBlank(filter.getResponsibleExpediting()) && StringUtils.isNotEmpty(filter.getResponsibleExpediting())) {
                sb.append(" AND lower(x.responsibleExpediting) like lower(:RESPONSIBLE_EXPEDITING)");
            }
            if (StringUtils.isNotEmpty(filter.getIncoTerm()) && StringUtils.isNotBlank(filter.getIncoTerm())) {
                sb.append(" AND lower(x.incoTerm) like lower(:INCO_TERM)");
            }
            if (!filter.getProjectsAssignedId().isEmpty()) {
                sb.append(" AND x.projectId IN(:PROJECT_ASSIGN_IDS)");
            } else {
                sb.append(" AND 3=2");
            }
            if (filter.getTypeDateReport() != null) {
                if (filter.getStartDateReport() != null) {
                    switch (filter.getTypeDateReport()) {
                        case PO_DELIVERY_DATE:
                            sb.append(" AND x.poDeliveryDate>=:START_PO_DELIVERY_DATE ");
                            break;
                        case REQUIRED_ON_SITE_DATE:
                            sb.append(" AND x.requiredDate>=:START_REQUIRED_ON_SITE_DATE ");
                            break;
                        case ACTUAL_ON_SITE_DATE:
                            sb.append(" AND x.actualOnSiteDate>=:START_ACTUAL_ON_SITE_DATE ");
                            break;
                        case NEXT_KEY_DATE:
                            sb.append(" AND x.nextKeyDate>=:START_NEXT_KEY_DATE ");
                            break;
                        default:
                            break;
                    }
                }
                if (filter.getEndDateReport() != null) {
                    switch (filter.getTypeDateReport()) {
                        case PO_DELIVERY_DATE:
                            sb.append(" AND x.poDeliveryDate<=:END_PO_DELIVERY_DATE ");
                            break;
                        case REQUIRED_ON_SITE_DATE:
                            sb.append(" AND x.requiredDate<=:END_REQUIRED_ON_SITE_DATE ");
                            break;
                        case ACTUAL_ON_SITE_DATE:
                            sb.append(" AND x.actualOnSiteDate<=:END_ACTUAL_ON_SITE_DATE ");
                            break;
                        case NEXT_KEY_DATE:
                            sb.append(" AND x.nextKeyDate<=:END_NEXT_KEY_DATE ");
                            break;
                        default:
                            break;
                    }
                }
            }
            sb.append(prepareSubQueryExpeditingStatuses(filter));
            sb.append(prepareSubquery(filter));
        } else {
            log.info("filter is null");
        }
        return sb.toString();
    }

    private String prepareSubQueryExpeditingStatuses(SearchPurchase filter) {
        StringBuilder sb = new StringBuilder();
        if (StringUtils.isNotEmpty(filter.getStatuses()) && StringUtils.isNotBlank(filter.getStatuses())) {
            sb.append(" AND x.poId IN ( ");
            sb.append(" SELECT es.purchaseOrderEntity.id ");
            sb.append(" FROM  ExpeditingStatusEntity es ");
            sb.append(" WHERE es.purchaseOrderStatus IN (:PURCHASE_ORDER_STATUS_LIST) ");
            sb.append(" )");
        }
        return sb.toString();
    }

    private String prepareSubquery(SearchPurchase filter) {
        StringBuilder subQuery = new StringBuilder();
        if (filter.getTypeDateReport() != null) {
            subQuery.append(" AND x.poId IN ( ");
            subQuery.append(" SELECT ss.purchaseOrder.id ");
            subQuery.append(" FROM VScopeSupply ss ");
            subQuery.append(" WHERE 1=1 ");

            if (filter.getStartDateReport() != null) {
                switch (filter.getTypeDateReport()) {
                    case FORECAST_EX_WORKS_DATE:
                        subQuery.append(" AND ss.forecastExWorkDate>=:START_FORECAST_EX_WORKS_DATE ");
                        break;
                    case ACTUAL_EX_WORKS_DATE:
                        subQuery.append(" AND ss.actualExWorkDate>=:START_ACTUAL_EX_WORKS_DATE ");
                        break;
                    case FORECAST_SITE_DATE:
                        subQuery.append(" AND ss.forecastSiteDate>=:START_FORECAST_SITE_DATE ");
                        break;
                    case ACTUAL_SITE_DATE:
                        subQuery.append(" AND ss.actualSiteDate>=:START_ACTUAL_SITE_DATE ");
                        break;
                    case REQUIRED_SITE_DATE:
                        subQuery.append(" AND ss.requiredSiteDate>=:START_REQUIRED_SITE_DATE ");
                        break;
                    default:
                        break;
                }
            }
            if (filter.getEndDateReport() != null) {
                switch (filter.getTypeDateReport()) {
                    case FORECAST_EX_WORKS_DATE:
                        subQuery.append(" AND ss.forecastExWorkDate<=:END_FORECAST_EX_WORKS_DATE ");
                        break;
                    case ACTUAL_EX_WORKS_DATE:
                        subQuery.append(" AND ss.actualExWorkDate<=:END_ACTUAL_EX_WORKS_DATE ");
                        break;
                    case FORECAST_SITE_DATE:
                        subQuery.append(" AND ss.forecastSiteDate<=:END_FORECAST_SITE_DATE ");
                        break;
                    case ACTUAL_SITE_DATE:
                        subQuery.append(" AND ss.actualSiteDate<=:END_ACTUAL_SITE_DATE ");
                        break;
                    case REQUIRED_SITE_DATE:
                        subQuery.append(" AND ss.requiredSiteDate<=:END_REQUIRED_SITE_DATE ");
                        break;
                    default:
                        break;
                }
            }
            subQuery.append(" ) ");
        }
        return subQuery.toString();
    }

    @Override
    public String orderBy() {
        log.info("orderBy empty");
        return "ORDER BY x.po,x.orderedVariation";
    }

    @Override
    public String orderBy(String field, boolean ascending) {
        log.info("orderBy with sorting");
        String myCustomField = field;
        if (field.equalsIgnoreCase("purchaseOrderStatus")) {
            myCustomField = generateClauseOrderByForPurchaseOrderStatus();
        }
        return " ORDER BY " + myCustomField + (ascending ? " ASC " : " DESC");
    }

    private String generateClauseOrderByForPurchaseOrderStatus() {
        StringBuilder sb = new StringBuilder();
        sb.append(" CASE ");
        for (ExpeditingStatusEnum status : ExpeditingStatusEnum.values()) {
            sb.append(" WHEN x.purchaseOrderStatus=" + status.ordinal());
            sb.append(" THEN ");
            sb.append("'" + status.getLabel() + "'");
        }
        sb.append(" END ");
        return sb.toString();
    }

}
