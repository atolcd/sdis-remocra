/*
 * This file is generated by jOOQ.
 */
package fr.sdis83.remocra.db.model.tables;


import fr.sdis83.remocra.db.converter.TimestampToInstantConverter;
import fr.sdis83.remocra.db.model.Indexes;
import fr.sdis83.remocra.db.model.Keys;
import fr.sdis83.remocra.db.model.Remocra;

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
public class Tournee extends TableImpl<Record> {

    private static final long serialVersionUID = 714450009;

    /**
     * The reference instance of <code>remocra.tournee</code>
     */
    public static final Tournee TOURNEE = new Tournee();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<Record> getRecordType() {
        return Record.class;
    }

    /**
     * The column <code>remocra.tournee.id</code>.
     */
    public final TableField<Record, Long> ID = createField("id", org.jooq.impl.SQLDataType.BIGINT.nullable(false).defaultValue(org.jooq.impl.DSL.field("nextval('remocra.tournee_id_seq'::regclass)", org.jooq.impl.SQLDataType.BIGINT)), this, "");

    /**
     * The column <code>remocra.tournee.deb_sync</code>.
     */
    public final TableField<Record, Instant> DEB_SYNC = createField("deb_sync", org.jooq.impl.SQLDataType.TIMESTAMP, this, "", new TimestampToInstantConverter());

    /**
     * The column <code>remocra.tournee.last_sync</code>.
     */
    public final TableField<Record, Instant> LAST_SYNC = createField("last_sync", org.jooq.impl.SQLDataType.TIMESTAMP, this, "", new TimestampToInstantConverter());

    /**
     * The column <code>remocra.tournee.version</code>.
     */
    public final TableField<Record, Integer> VERSION = createField("version", org.jooq.impl.SQLDataType.INTEGER.defaultValue(org.jooq.impl.DSL.field("1", org.jooq.impl.SQLDataType.INTEGER)), this, "");

    /**
     * The column <code>remocra.tournee.affectation</code>.
     */
    public final TableField<Record, Long> AFFECTATION = createField("affectation", org.jooq.impl.SQLDataType.BIGINT.nullable(false), this, "");

    /**
     * The column <code>remocra.tournee.reservation</code>.
     */
    public final TableField<Record, Long> RESERVATION = createField("reservation", org.jooq.impl.SQLDataType.BIGINT, this, "");

    /**
     * The column <code>remocra.tournee.etat</code>.
     */
    public final TableField<Record, Integer> ETAT = createField("etat", org.jooq.impl.SQLDataType.INTEGER, this, "");

    /**
     * The column <code>remocra.tournee.hydrant_count</code>.
     */
    public final TableField<Record, Integer> HYDRANT_COUNT = createField("hydrant_count", org.jooq.impl.SQLDataType.INTEGER, this, "");

    /**
     * The column <code>remocra.tournee.nom</code>.
     */
    public final TableField<Record, String> NOM = createField("nom", org.jooq.impl.SQLDataType.VARCHAR.nullable(false), this, "");

    /**
     * Create a <code>remocra.tournee</code> table reference
     */
    public Tournee() {
        this(DSL.name("tournee"), null);
    }

    /**
     * Create an aliased <code>remocra.tournee</code> table reference
     */
    public Tournee(String alias) {
        this(DSL.name(alias), TOURNEE);
    }

    /**
     * Create an aliased <code>remocra.tournee</code> table reference
     */
    public Tournee(Name alias) {
        this(alias, TOURNEE);
    }

    private Tournee(Name alias, Table<Record> aliased) {
        this(alias, aliased, null);
    }

    private Tournee(Name alias, Table<Record> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment(""));
    }

    public <O extends Record> Tournee(Table<O> child, ForeignKey<O, Record> key) {
        super(child, key, TOURNEE);
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
        return Arrays.<Index>asList(Indexes.NOM_AFFECTATION, Indexes.TOURNEE_AFFECTATION_IDX, Indexes.TOURNEE_PKEY, Indexes.TOURNEE_RESERVATION_IDX);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Identity<Record, Long> getIdentity() {
        return Keys.IDENTITY_TOURNEE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UniqueKey<Record> getPrimaryKey() {
        return Keys.TOURNEE_PKEY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<UniqueKey<Record>> getKeys() {
        return Arrays.<UniqueKey<Record>>asList(Keys.TOURNEE_PKEY, Keys.NOM_AFFECTATION);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ForeignKey<Record, ?>> getReferences() {
        return Arrays.<ForeignKey<Record, ?>>asList(Keys.TOURNEE__FKBC630036DBF82B2F, Keys.TOURNEE__FKBC6300366F3F65FB);
    }

    public Organisme organisme() {
        return new Organisme(this, Keys.TOURNEE__FKBC630036DBF82B2F);
    }

    public Utilisateur utilisateur() {
        return new Utilisateur(this, Keys.TOURNEE__FKBC6300366F3F65FB);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Tournee as(String alias) {
        return new Tournee(DSL.name(alias), this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Tournee as(Name alias) {
        return new Tournee(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public Tournee rename(String name) {
        return new Tournee(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public Tournee rename(Name name) {
        return new Tournee(name, null);
    }
}