/*
 * This file is generated by jOOQ.
 */
package fr.sdis83.remocra.db.model.incoming.tables.pojos;


import java.io.Serializable;
import java.util.UUID;

import javax.annotation.Generated;


/**
 * This class is generated by jOOQ.
 */
@Generated(
    value = {
        "http://www.jooq.org",
        "jOOQ version:3.11.11"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class HydrantVisiteAnomalie implements Serializable {

    private static final long serialVersionUID = 453434715;

    private UUID idHydrantVisite;
    private Long idAnomalie;

    public HydrantVisiteAnomalie() {}

    public HydrantVisiteAnomalie(HydrantVisiteAnomalie value) {
        this.idHydrantVisite = value.idHydrantVisite;
        this.idAnomalie = value.idAnomalie;
    }

    public HydrantVisiteAnomalie(
        UUID idHydrantVisite,
        Long idAnomalie
    ) {
        this.idHydrantVisite = idHydrantVisite;
        this.idAnomalie = idAnomalie;
    }

    public UUID getIdHydrantVisite() {
        return this.idHydrantVisite;
    }

    public HydrantVisiteAnomalie setIdHydrantVisite(UUID idHydrantVisite) {
        this.idHydrantVisite = idHydrantVisite;
        return this;
    }

    public Long getIdAnomalie() {
        return this.idAnomalie;
    }

    public HydrantVisiteAnomalie setIdAnomalie(Long idAnomalie) {
        this.idAnomalie = idAnomalie;
        return this;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("HydrantVisiteAnomalie (");

        sb.append(idHydrantVisite);
        sb.append(", ").append(idAnomalie);

        sb.append(")");
        return sb.toString();
    }
}