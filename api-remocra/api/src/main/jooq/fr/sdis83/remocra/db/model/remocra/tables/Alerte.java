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
public class Alerte extends TableImpl<Record> {

    private static final long serialVersionUID = 1221582831;

    /**
     * The reference instance of <code>remocra.alerte</code>
     */
    public static final Alerte ALERTE = new Alerte();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<Record> getRecordType() {
        return Record.class;
    }

    /**
     * The column <code>remocra.alerte.id</code>.
     */
    public final TableField<Record, Long> ID = createField("id", org.jooq.impl.SQLDataType.BIGINT.nullable(false).defaultValue(org.jooq.impl.DSL.field("nextval('remocra.alerte_id_seq'::regclass)", org.jooq.impl.SQLDataType.BIGINT)), this, "");

    /**
     * The column <code>remocra.alerte.commentaire</code>.
     */
    public final TableField<Record, String> COMMENTAIRE = createField("commentaire", org.jooq.impl.SQLDataType.VARCHAR.nullable(false), this, "");

    /**
     * The column <code>remocra.alerte.date_constat</code>.
     */
    public final TableField<Record, Instant> DATE_CONSTAT = createField("date_constat", org.jooq.impl.SQLDataType.TIMESTAMP.nullable(false), this, "", new TimestampToInstantConverter());

    /**
     * The column <code>remocra.alerte.date_modification</code>.
     */
    public final TableField<Record, Instant> DATE_MODIFICATION = createField("date_modification", org.jooq.impl.SQLDataType.TIMESTAMP.nullable(false), this, "", new TimestampToInstantConverter());

    /**
     * The column <code>remocra.alerte.etat</code>.
     */
    public final TableField<Record, Boolean> ETAT = createField("etat", org.jooq.impl.SQLDataType.BOOLEAN, this, "");

    /**
     * @deprecated Unknown data type. Please define an explicit {@link org.jooq.Binding} to specify how this type should be handled. Deprecation can be turned off using {@literal <deprecationOnUnknownTypes/>} in your code generator configuration.
     */
    @java.lang.Deprecated
    public final TableField<Record, Object> GEOMETRIE = createField("geometrie", org.jooq.impl.DefaultDataType.getDefaultDataType("\"public\".\"geometry\"").nullable(false), this, "");

    /**
     * The column <code>remocra.alerte.version</code>.
     */
    public final TableField<Record, Integer> VERSION = createField("version", org.jooq.impl.SQLDataType.INTEGER.defaultValue(org.jooq.impl.DSL.field("1", org.jooq.impl.SQLDataType.INTEGER)), this, "");

    /**
     * The column <code>remocra.alerte.rapporteur</code>.
     */
    public final TableField<Record, Long> RAPPORTEUR = createField("rapporteur", org.jooq.impl.SQLDataType.BIGINT.nullable(false), this, "");

    /**
     * Create a <code>remocra.alerte</code> table reference
     */
    public Alerte() {
        this(DSL.name("alerte"), null);
    }

    /**
     * Create an aliased <code>remocra.alerte</code> table reference
     */
    public Alerte(String alias) {
        this(DSL.name(alias), ALERTE);
    }

    /**
     * Create an aliased <code>remocra.alerte</code> table reference
     */
    public Alerte(Name alias) {
        this(alias, ALERTE);
    }

    private Alerte(Name alias, Table<Record> aliased) {
        this(alias, aliased, null);
    }

    private Alerte(Name alias, Table<Record> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment(""));
    }

    public <O extends Record> Alerte(Table<O> child, ForeignKey<O, Record> key) {
        super(child, key, ALERTE);
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
        return Arrays.<Index>asList(Indexes.ALERTE_GEOMETRIE_IDX, Indexes.ALERTE_PKEY, Indexes.ALERTE_RAPPORTEUR_IDX);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Identity<Record, Long> getIdentity() {
        return Keys.IDENTITY_ALERTE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UniqueKey<Record> getPrimaryKey() {
        return Keys.ALERTE_PKEY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<UniqueKey<Record>> getKeys() {
        return Arrays.<UniqueKey<Record>>asList(Keys.ALERTE_PKEY);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ForeignKey<Record, ?>> getReferences() {
        return Arrays.<ForeignKey<Record, ?>>asList(Keys.ALERTE__FKABA7A2891E30C88F);
    }

    public Utilisateur utilisateur() {
        return new Utilisateur(this, Keys.ALERTE__FKABA7A2891E30C88F);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Alerte as(String alias) {
        return new Alerte(DSL.name(alias), this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Alerte as(Name alias) {
        return new Alerte(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public Alerte rename(String name) {
        return new Alerte(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public Alerte rename(Name name) {
        return new Alerte(name, null);
    }
}