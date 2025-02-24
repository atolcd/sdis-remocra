/*
 * This file is generated by jOOQ.
 */
package fr.sdis83.remocra.db.model.remocra.tables.pojos;


import java.io.Serializable;

import javax.annotation.Generated;


/**
 * Table de liaison entre les crises et les interventions
 */
@Generated(
    value = {
        "http://www.jooq.org",
        "jOOQ version:3.11.11"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class CriseIntervention implements Serializable {

    private static final long serialVersionUID = -2119097635;

    private Long crise;
    private Long intervention;

    public CriseIntervention() {}

    public CriseIntervention(CriseIntervention value) {
        this.crise = value.crise;
        this.intervention = value.intervention;
    }

    public CriseIntervention(
        Long crise,
        Long intervention
    ) {
        this.crise = crise;
        this.intervention = intervention;
    }

    public Long getCrise() {
        return this.crise;
    }

    public CriseIntervention setCrise(Long crise) {
        this.crise = crise;
        return this;
    }

    public Long getIntervention() {
        return this.intervention;
    }

    public CriseIntervention setIntervention(Long intervention) {
        this.intervention = intervention;
        return this;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("CriseIntervention (");

        sb.append(crise);
        sb.append(", ").append(intervention);

        sb.append(")");
        return sb.toString();
    }
}
