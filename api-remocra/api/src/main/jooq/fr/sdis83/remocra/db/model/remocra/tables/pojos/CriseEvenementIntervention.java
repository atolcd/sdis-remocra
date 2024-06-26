/*
 * This file is generated by jOOQ.
 */
package fr.sdis83.remocra.db.model.remocra.tables.pojos;


import java.io.Serializable;

import javax.annotation.Generated;


/**
 * Table de liaison entre des évènement d'une crises et des interventions
 */
@Generated(
    value = {
        "http://www.jooq.org",
        "jOOQ version:3.11.11"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class CriseEvenementIntervention implements Serializable {

    private static final long serialVersionUID = 638544443;

    private Long criseEvenement;
    private Long intervention;

    public CriseEvenementIntervention() {}

    public CriseEvenementIntervention(CriseEvenementIntervention value) {
        this.criseEvenement = value.criseEvenement;
        this.intervention = value.intervention;
    }

    public CriseEvenementIntervention(
        Long criseEvenement,
        Long intervention
    ) {
        this.criseEvenement = criseEvenement;
        this.intervention = intervention;
    }

    public Long getCriseEvenement() {
        return this.criseEvenement;
    }

    public CriseEvenementIntervention setCriseEvenement(Long criseEvenement) {
        this.criseEvenement = criseEvenement;
        return this;
    }

    public Long getIntervention() {
        return this.intervention;
    }

    public CriseEvenementIntervention setIntervention(Long intervention) {
        this.intervention = intervention;
        return this;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("CriseEvenementIntervention (");

        sb.append(criseEvenement);
        sb.append(", ").append(intervention);

        sb.append(")");
        return sb.toString();
    }
}
