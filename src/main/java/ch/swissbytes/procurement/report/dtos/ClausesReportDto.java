package ch.swissbytes.procurement.report.dtos;

import ch.swissbytes.domain.model.entities.ClausesEntity;

/**
 * Created by Christian on 11/11/2015.
 */
public class ClausesReportDto {
    private String numberClause;
    private String clause;

    public ClausesReportDto() {
    }

    public ClausesReportDto(ClausesEntity entity) {
        this.numberClause = entity.getNumberClause();
        this.clause = entity.getClauses();
    }

    public String getNumberClause() {
        return numberClause;
    }

    public void setNumberClause(String numberClause) {
        this.numberClause = numberClause;
    }

    public String getClause() {
        return clause;
    }

    public void setClause(String clause) {
        this.clause = clause;
    }
}
