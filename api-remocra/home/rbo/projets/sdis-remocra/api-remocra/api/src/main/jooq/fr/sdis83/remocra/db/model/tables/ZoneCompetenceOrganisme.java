/*
 * This file is generated by jOOQ.
 */
package fr.sdis83.remocra.db.model.tables;


import fr.sdis83.remocra.db.model.Indexes;
import fr.sdis83.remocra.db.model.Remocra;

import java.util.Arrays;
import java.util.List;

import javax.annotation.Generated;

import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.Index;
import org.jooq.Name;
import org.jooq.Record;
import org.jooq.Schema;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.impl.DSL;
import org.jooq.impl.TableImpl;


/**
 * Vue des zones de compétence des organismes
 */
@Generated(
    value = {
        "http://www.jooq.org",
        "jOOQ version:3.11.11"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class ZoneCompetenceOrganisme extends TableImpl<Record> {

    private static final long serialVersionUID = 812055120;

    /**
     * The reference instance of <code>remocra.zone_competence_organisme</code>
     */
    public static final ZoneCompetenceOrganisme ZONE_COMPETENCE_ORGANISME = new ZoneCompetenceOrganisme();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<Record> getRecordType() {
        return Record.class;
    }

    /**
     * The column <code>remocra.zone_competence_organisme.organisme_id</code>. Organisme principal
     */
    public final TableField<Record, Long> ORGANISME_ID = createField("organisme_id", org.jooq.impl.SQLDataType.BIGINT, this, "Organisme principal");

    /**
     * The column <code>remocra.zone_competence_organisme.organisme_contenu_id</code>. Organisme dont la zone de compétence est contenue ou
superposée avec la zone de compétence de l'organisme princial
     */
    public final TableField<Record, Long> ORGANISME_CONTENU_ID = createField("organisme_contenu_id", org.jooq.impl.SQLDataType.BIGINT, this, "Organisme dont la zone de compétence est contenue ou\nsuperposée avec la zone de compétence de l'organisme princial");

    /**
     * Create a <code>remocra.zone_competence_organisme</code> table reference
     */
    public ZoneCompetenceOrganisme() {
        this(DSL.name("zone_competence_organisme"), null);
    }

    /**
     * Create an aliased <code>remocra.zone_competence_organisme</code> table reference
     */
    public ZoneCompetenceOrganisme(String alias) {
        this(DSL.name(alias), ZONE_COMPETENCE_ORGANISME);
    }

    /**
     * Create an aliased <code>remocra.zone_competence_organisme</code> table reference
     */
    public ZoneCompetenceOrganisme(Name alias) {
        this(alias, ZONE_COMPETENCE_ORGANISME);
    }

    private ZoneCompetenceOrganisme(Name alias, Table<Record> aliased) {
        this(alias, aliased, null);
    }

    private ZoneCompetenceOrganisme(Name alias, Table<Record> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment("Vue des zones de compétence des organismes"));
    }

    public <O extends Record> ZoneCompetenceOrganisme(Table<O> child, ForeignKey<O, Record> key) {
        super(child, key, ZONE_COMPETENCE_ORGANISME);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Schema getSchema() {
        return Remocra.REMOCRA;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Index> getIndexes() {
        return Arrays.<Index>asList(Indexes.ZONE_COMPETENCE_ORGANISME_ORGANISME_CONTENU_IDX, Indexes.ZONE_COMPETENCE_ORGANISME_ORGANISME_IDX);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ZoneCompetenceOrganisme as(String alias) {
        return new ZoneCompetenceOrganisme(DSL.name(alias), this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ZoneCompetenceOrganisme as(Name alias) {
        return new ZoneCompetenceOrganisme(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public ZoneCompetenceOrganisme rename(String name) {
        return new ZoneCompetenceOrganisme(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public ZoneCompetenceOrganisme rename(Name name) {
        return new ZoneCompetenceOrganisme(name, null);
    }
}