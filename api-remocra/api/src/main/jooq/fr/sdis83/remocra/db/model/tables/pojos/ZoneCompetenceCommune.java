/*
 * This file is generated by jOOQ.
 */
package fr.sdis83.remocra.db.model.tables.pojos;


import java.io.Serializable;

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
public class ZoneCompetenceCommune implements Serializable {

    private static final long serialVersionUID = 782923391;

    private Long zoneCompetenceId;
    private Long communeId;

    public ZoneCompetenceCommune() {}

    public ZoneCompetenceCommune(ZoneCompetenceCommune value) {
        this.zoneCompetenceId = value.zoneCompetenceId;
        this.communeId = value.communeId;
    }

    public ZoneCompetenceCommune(
        Long zoneCompetenceId,
        Long communeId
    ) {
        this.zoneCompetenceId = zoneCompetenceId;
        this.communeId = communeId;
    }

    public Long getZoneCompetenceId() {
        return this.zoneCompetenceId;
    }

    public ZoneCompetenceCommune setZoneCompetenceId(Long zoneCompetenceId) {
        this.zoneCompetenceId = zoneCompetenceId;
        return this;
    }

    public Long getCommuneId() {
        return this.communeId;
    }

    public ZoneCompetenceCommune setCommuneId(Long communeId) {
        this.communeId = communeId;
        return this;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("ZoneCompetenceCommune (");

        sb.append(zoneCompetenceId);
        sb.append(", ").append(communeId);

        sb.append(")");
        return sb.toString();
    }
}