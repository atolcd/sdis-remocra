/*
 * This file is generated by jOOQ.
 */
package fr.sdis83.remocra.db.model.incoming.tables;


import fr.sdis83.remocra.db.converter.TimestampToInstantConverter;
import fr.sdis83.remocra.db.model.incoming.Incoming;
import fr.sdis83.remocra.db.model.incoming.Indexes;
import fr.sdis83.remocra.db.model.incoming.Keys;
import fr.sdis83.remocra.db.model.remocra.tables.Hydrant;
import fr.sdis83.remocra.db.model.remocra.tables.TypeHydrantSaisie;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import javax.annotation.Generated;

import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.Index;
import org.jooq.Name;
import org.jooq.Record;
import org.jooq.Schema;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.UniqueKey;
import org.jooq.impl.DSL;
import org.jooq.impl.TableImpl;


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
public class HydrantVisite extends TableImpl<Record> {

    private static final long serialVersionUID = 1876080093;

    /**
     * The reference instance of <code>incoming.hydrant_visite</code>
     */
    public static final HydrantVisite HYDRANT_VISITE = new HydrantVisite();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<Record> getRecordType() {
        return Record.class;
    }

    /**
     * The column <code>incoming.hydrant_visite.id_hydrant_visite</code>.
     */
    public final TableField<Record, UUID> ID_HYDRANT_VISITE = createField("id_hydrant_visite", org.jooq.impl.SQLDataType.UUID.nullable(false), this, "");

    /**
     * The column <code>incoming.hydrant_visite.id_hydrant</code>.
     */
    public final TableField<Record, Long> ID_HYDRANT = createField("id_hydrant", org.jooq.impl.SQLDataType.BIGINT, this, "");

    /**
     * The column <code>incoming.hydrant_visite.date_hydrant_visite</code>.
     */
    public final TableField<Record, Instant> DATE_HYDRANT_VISITE = createField("date_hydrant_visite", org.jooq.impl.SQLDataType.TIMESTAMP.defaultValue(org.jooq.impl.DSL.field("now()", org.jooq.impl.SQLDataType.TIMESTAMP)), this, "", new TimestampToInstantConverter());

    /**
     * The column <code>incoming.hydrant_visite.id_type_hydrant_saisie</code>.
     */
    public final TableField<Record, Long> ID_TYPE_HYDRANT_SAISIE = createField("id_type_hydrant_saisie", org.jooq.impl.SQLDataType.BIGINT, this, "");

    /**
     * The column <code>incoming.hydrant_visite.ctrl_debit_pression</code>.
     */
    public final TableField<Record, Boolean> CTRL_DEBIT_PRESSION = createField("ctrl_debit_pression", org.jooq.impl.SQLDataType.BOOLEAN, this, "");

    /**
     * The column <code>incoming.hydrant_visite.agent1_hydrant_visite</code>.
     */
    public final TableField<Record, String> AGENT1_HYDRANT_VISITE = createField("agent1_hydrant_visite", org.jooq.impl.SQLDataType.VARCHAR, this, "");

    /**
     * The column <code>incoming.hydrant_visite.agent2_hydrant_visite</code>.
     */
    public final TableField<Record, String> AGENT2_HYDRANT_VISITE = createField("agent2_hydrant_visite", org.jooq.impl.SQLDataType.VARCHAR, this, "");

    /**
     * The column <code>incoming.hydrant_visite.debit_hydrant_visite</code>.
     */
    public final TableField<Record, Integer> DEBIT_HYDRANT_VISITE = createField("debit_hydrant_visite", org.jooq.impl.SQLDataType.INTEGER, this, "");

    /**
     * The column <code>incoming.hydrant_visite.pression_hydrant_visite</code>.
     */
    public final TableField<Record, Double> PRESSION_HYDRANT_VISITE = createField("pression_hydrant_visite", org.jooq.impl.SQLDataType.DOUBLE, this, "");

    /**
     * The column <code>incoming.hydrant_visite.pression_dyn_hydrant_visite</code>.
     */
    public final TableField<Record, Double> PRESSION_DYN_HYDRANT_VISITE = createField("pression_dyn_hydrant_visite", org.jooq.impl.SQLDataType.DOUBLE, this, "");

    /**
     * The column <code>incoming.hydrant_visite.observations_hydrant_visite</code>.
     */
    public final TableField<Record, String> OBSERVATIONS_HYDRANT_VISITE = createField("observations_hydrant_visite", org.jooq.impl.SQLDataType.VARCHAR, this, "");

    /**
     * The column <code>incoming.hydrant_visite.has_anomalie_changes</code>.
     */
    public final TableField<Record, Boolean> HAS_ANOMALIE_CHANGES = createField("has_anomalie_changes", org.jooq.impl.SQLDataType.BOOLEAN.nullable(false).defaultValue(org.jooq.impl.DSL.field("false", org.jooq.impl.SQLDataType.BOOLEAN)), this, "");

    /**
     * Create a <code>incoming.hydrant_visite</code> table reference
     */
    public HydrantVisite() {
        this(DSL.name("hydrant_visite"), null);
    }

    /**
     * Create an aliased <code>incoming.hydrant_visite</code> table reference
     */
    public HydrantVisite(String alias) {
        this(DSL.name(alias), HYDRANT_VISITE);
    }

    /**
     * Create an aliased <code>incoming.hydrant_visite</code> table reference
     */
    public HydrantVisite(Name alias) {
        this(alias, HYDRANT_VISITE);
    }

    private HydrantVisite(Name alias, Table<Record> aliased) {
        this(alias, aliased, null);
    }

    private HydrantVisite(Name alias, Table<Record> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment(""));
    }

    public <O extends Record> HydrantVisite(Table<O> child, ForeignKey<O, Record> key) {
        super(child, key, HYDRANT_VISITE);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Schema getSchema() {
        return Incoming.INCOMING;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Index> getIndexes() {
        return Arrays.<Index>asList(Indexes.HYDRANT_VISITE_PKEY);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UniqueKey<Record> getPrimaryKey() {
        return Keys.HYDRANT_VISITE_PKEY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<UniqueKey<Record>> getKeys() {
        return Arrays.<UniqueKey<Record>>asList(Keys.HYDRANT_VISITE_PKEY);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ForeignKey<Record, ?>> getReferences() {
        return Arrays.<ForeignKey<Record, ?>>asList(Keys.HYDRANT_VISITE__HYDRANT_VISITE_ID_HYDRANT_FKEY, Keys.HYDRANT_VISITE__HYDRANT_VISITE_ID_TYPE_HYDRANT_SAISIE_FKEY);
    }

    public Hydrant hydrant() {
        return new Hydrant(this, Keys.HYDRANT_VISITE__HYDRANT_VISITE_ID_HYDRANT_FKEY);
    }

    public TypeHydrantSaisie typeHydrantSaisie() {
        return new TypeHydrantSaisie(this, Keys.HYDRANT_VISITE__HYDRANT_VISITE_ID_TYPE_HYDRANT_SAISIE_FKEY);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public HydrantVisite as(String alias) {
        return new HydrantVisite(DSL.name(alias), this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public HydrantVisite as(Name alias) {
        return new HydrantVisite(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public HydrantVisite rename(String name) {
        return new HydrantVisite(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public HydrantVisite rename(Name name) {
        return new HydrantVisite(name, null);
    }
}
