/*
 * This file is generated by jOOQ.
 */
package fr.sdis83.remocra.db.model.tables.pojos;


import java.io.Serializable;
import java.time.Instant;

import javax.annotation.Generated;


/**
 * Indisponibilié temporaire d'hydrant
 */
@Generated(
    value = {
        "http://www.jooq.org",
        "jOOQ version:3.11.11"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class HydrantIndispoTemporaire implements Serializable {

    private static final long serialVersionUID = -1071552536;

    private Long    id;
    private Instant dateDebut;
    private Instant dateFin;
    private String  motif;
    private Instant dateRappelDebut;
    private Instant dateRappelFin;
    private Long    statut;
    private Integer totalHydrants;
    private Boolean basculeAutoIndispo;
    private Boolean basculeAutoDispo;
    private Boolean melAvantIndispo;
    private Boolean melAvantDispo;

    public HydrantIndispoTemporaire() {}

    public HydrantIndispoTemporaire(HydrantIndispoTemporaire value) {
        this.id = value.id;
        this.dateDebut = value.dateDebut;
        this.dateFin = value.dateFin;
        this.motif = value.motif;
        this.dateRappelDebut = value.dateRappelDebut;
        this.dateRappelFin = value.dateRappelFin;
        this.statut = value.statut;
        this.totalHydrants = value.totalHydrants;
        this.basculeAutoIndispo = value.basculeAutoIndispo;
        this.basculeAutoDispo = value.basculeAutoDispo;
        this.melAvantIndispo = value.melAvantIndispo;
        this.melAvantDispo = value.melAvantDispo;
    }

    public HydrantIndispoTemporaire(
        Long    id,
        Instant dateDebut,
        Instant dateFin,
        String  motif,
        Instant dateRappelDebut,
        Instant dateRappelFin,
        Long    statut,
        Integer totalHydrants,
        Boolean basculeAutoIndispo,
        Boolean basculeAutoDispo,
        Boolean melAvantIndispo,
        Boolean melAvantDispo
    ) {
        this.id = id;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.motif = motif;
        this.dateRappelDebut = dateRappelDebut;
        this.dateRappelFin = dateRappelFin;
        this.statut = statut;
        this.totalHydrants = totalHydrants;
        this.basculeAutoIndispo = basculeAutoIndispo;
        this.basculeAutoDispo = basculeAutoDispo;
        this.melAvantIndispo = melAvantIndispo;
        this.melAvantDispo = melAvantDispo;
    }

    public Long getId() {
        return this.id;
    }

    public HydrantIndispoTemporaire setId(Long id) {
        this.id = id;
        return this;
    }

    public Instant getDateDebut() {
        return this.dateDebut;
    }

    public HydrantIndispoTemporaire setDateDebut(Instant dateDebut) {
        this.dateDebut = dateDebut;
        return this;
    }

    public Instant getDateFin() {
        return this.dateFin;
    }

    public HydrantIndispoTemporaire setDateFin(Instant dateFin) {
        this.dateFin = dateFin;
        return this;
    }

    public String getMotif() {
        return this.motif;
    }

    public HydrantIndispoTemporaire setMotif(String motif) {
        this.motif = motif;
        return this;
    }

    public Instant getDateRappelDebut() {
        return this.dateRappelDebut;
    }

    public HydrantIndispoTemporaire setDateRappelDebut(Instant dateRappelDebut) {
        this.dateRappelDebut = dateRappelDebut;
        return this;
    }

    public Instant getDateRappelFin() {
        return this.dateRappelFin;
    }

    public HydrantIndispoTemporaire setDateRappelFin(Instant dateRappelFin) {
        this.dateRappelFin = dateRappelFin;
        return this;
    }

    public Long getStatut() {
        return this.statut;
    }

    public HydrantIndispoTemporaire setStatut(Long statut) {
        this.statut = statut;
        return this;
    }

    public Integer getTotalHydrants() {
        return this.totalHydrants;
    }

    public HydrantIndispoTemporaire setTotalHydrants(Integer totalHydrants) {
        this.totalHydrants = totalHydrants;
        return this;
    }

    public Boolean getBasculeAutoIndispo() {
        return this.basculeAutoIndispo;
    }

    public HydrantIndispoTemporaire setBasculeAutoIndispo(Boolean basculeAutoIndispo) {
        this.basculeAutoIndispo = basculeAutoIndispo;
        return this;
    }

    public Boolean getBasculeAutoDispo() {
        return this.basculeAutoDispo;
    }

    public HydrantIndispoTemporaire setBasculeAutoDispo(Boolean basculeAutoDispo) {
        this.basculeAutoDispo = basculeAutoDispo;
        return this;
    }

    public Boolean getMelAvantIndispo() {
        return this.melAvantIndispo;
    }

    public HydrantIndispoTemporaire setMelAvantIndispo(Boolean melAvantIndispo) {
        this.melAvantIndispo = melAvantIndispo;
        return this;
    }

    public Boolean getMelAvantDispo() {
        return this.melAvantDispo;
    }

    public HydrantIndispoTemporaire setMelAvantDispo(Boolean melAvantDispo) {
        this.melAvantDispo = melAvantDispo;
        return this;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("HydrantIndispoTemporaire (");

        sb.append(id);
        sb.append(", ").append(dateDebut);
        sb.append(", ").append(dateFin);
        sb.append(", ").append(motif);
        sb.append(", ").append(dateRappelDebut);
        sb.append(", ").append(dateRappelFin);
        sb.append(", ").append(statut);
        sb.append(", ").append(totalHydrants);
        sb.append(", ").append(basculeAutoIndispo);
        sb.append(", ").append(basculeAutoDispo);
        sb.append(", ").append(melAvantIndispo);
        sb.append(", ").append(melAvantDispo);

        sb.append(")");
        return sb.toString();
    }
}