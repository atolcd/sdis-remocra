/*
 * This file is generated by jOOQ.
 */
package fr.sdis83.remocra.db.model.remocra.tables;


import fr.sdis83.remocra.db.converter.TimestampToInstantConverter;
import fr.sdis83.remocra.db.model.remocra.Indexes;
import fr.sdis83.remocra.db.model.remocra.Keys;
import fr.sdis83.remocra.db.model.remocra.Remocra;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Generated;

import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.Identity;
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
public class Synchronisation extends TableImpl<Record> {

    private static final long serialVersionUID = -1872907890;

    /**
     * The reference instance of <code>remocra.synchronisation</code>
     */
    public static final Synchronisation SYNCHRONISATION = new Synchronisation();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<Record> getRecordType() {
        return Record.class;
    }

    /**
     * The column <code>remocra.synchronisation.id</code>.
     */
    public final TableField<Record, Long> ID = createField("id", org.jooq.impl.SQLDataType.BIGINT.nullable(false).defaultValue(org.jooq.impl.DSL.field("nextval('remocra.synchronisation_id_seq'::regclass)", org.jooq.impl.SQLDataType.BIGINT)), this, "");

    /**
     * The column <code>remocra.synchronisation.date_synchro</code>.
     */
    public final TableField<Record, Instant> DATE_SYNCHRO = createField("date_synchro", org.jooq.impl.SQLDataType.TIMESTAMP.nullable(false), this, "", new TimestampToInstantConverter());

    /**
     * The column <code>remocra.synchronisation.succes</code>.
     */
    public final TableField<Record, Boolean> SUCCES = createField("succes", org.jooq.impl.SQLDataType.BOOLEAN.nullable(false), this, "");

    /**
     * The column <code>remocra.synchronisation.version</code>.
     */
    public final TableField<Record, Integer> VERSION = createField("version", org.jooq.impl.SQLDataType.INTEGER, this, "");

    /**
     * The column <code>remocra.synchronisation.thematique</code>.
     */
    public final TableField<Record, Long> THEMATIQUE = createField("thematique", org.jooq.impl.SQLDataType.BIGINT.nullable(false), this, "");

    /**
     * Create a <code>remocra.synchronisation</code> table reference
     */
    public Synchronisation() {
        this(DSL.name("synchronisation"), null);
    }

    /**
     * Create an aliased <code>remocra.synchronisation</code> table reference
     */
    public Synchronisation(String alias) {
        this(DSL.name(alias), SYNCHRONISATION);
    }

    /**
     * Create an aliased <code>remocra.synchronisation</code> table reference
     */
    public Synchronisation(Name alias) {
        this(alias, SYNCHRONISATION);
    }

    private Synchronisation(Name alias, Table<Record> aliased) {
        this(alias, aliased, null);
    }

    private Synchronisation(Name alias, Table<Record> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment(""));
    }

    public <O extends Record> Synchronisation(Table<O> child, ForeignKey<O, Record> key) {
        super(child, key, SYNCHRONISATION);
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
        return Arrays.<Index>asList(Indexes.SYNCHRONISATION_PKEY, Indexes.SYNCHRONISATION_THEMATIQUE_IDX);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Identity<Record, Long> getIdentity() {
        return Keys.IDENTITY_SYNCHRONISATION;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UniqueKey<Record> getPrimaryKey() {
        return Keys.SYNCHRONISATION_PKEY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<UniqueKey<Record>> getKeys() {
        return Arrays.<UniqueKey<Record>>asList(Keys.SYNCHRONISATION_PKEY);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ForeignKey<Record, ?>> getReferences() {
        return Arrays.<ForeignKey<Record, ?>>asList(Keys.SYNCHRONISATION__FK43A80607D27676E2);
    }

    public Thematique thematique() {
        return new Thematique(this, Keys.SYNCHRONISATION__FK43A80607D27676E2);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Synchronisation as(String alias) {
        return new Synchronisation(DSL.name(alias), this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Synchronisation as(Name alias) {
        return new Synchronisation(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public Synchronisation rename(String name) {
        return new Synchronisation(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public Synchronisation rename(Name name) {
        return new Synchronisation(name, null);
    }
}
