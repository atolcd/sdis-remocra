/*
 * This file is generated by jOOQ.
 */
package fr.sdis83.remocra.db.model.remocra.tables;


import fr.sdis83.remocra.db.model.remocra.Indexes;
import fr.sdis83.remocra.db.model.remocra.Keys;
import fr.sdis83.remocra.db.model.remocra.Remocra;

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
public class AlerteElt extends TableImpl<Record> {

    private static final long serialVersionUID = -626331752;

    /**
     * The reference instance of <code>remocra.alerte_elt</code>
     */
    public static final AlerteElt ALERTE_ELT = new AlerteElt();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<Record> getRecordType() {
        return Record.class;
    }

    /**
     * The column <code>remocra.alerte_elt.id</code>.
     */
    public final TableField<Record, Long> ID = createField("id", org.jooq.impl.SQLDataType.BIGINT.nullable(false).defaultValue(org.jooq.impl.DSL.field("nextval('remocra.alerte_elt_id_seq'::regclass)", org.jooq.impl.SQLDataType.BIGINT)), this, "");

    /**
     * The column <code>remocra.alerte_elt.commentaire</code>.
     */
    public final TableField<Record, String> COMMENTAIRE = createField("commentaire", org.jooq.impl.SQLDataType.VARCHAR, this, "");

    /**
     * @deprecated Unknown data type. Please define an explicit {@link org.jooq.Binding} to specify how this type should be handled. Deprecation can be turned off using {@literal <deprecationOnUnknownTypes/>} in your code generator configuration.
     */
    @java.lang.Deprecated
    public final TableField<Record, Object> GEOMETRIE = createField("geometrie", org.jooq.impl.DefaultDataType.getDefaultDataType("\"public\".\"geometry\"").nullable(false), this, "");

    /**
     * The column <code>remocra.alerte_elt.version</code>.
     */
    public final TableField<Record, Integer> VERSION = createField("version", org.jooq.impl.SQLDataType.INTEGER.defaultValue(org.jooq.impl.DSL.field("1", org.jooq.impl.SQLDataType.INTEGER)), this, "");

    /**
     * The column <code>remocra.alerte_elt.alerte</code>.
     */
    public final TableField<Record, Long> ALERTE = createField("alerte", org.jooq.impl.SQLDataType.BIGINT.nullable(false), this, "");

    /**
     * The column <code>remocra.alerte_elt.sous_type_alerte_elt</code>.
     */
    public final TableField<Record, Long> SOUS_TYPE_ALERTE_ELT = createField("sous_type_alerte_elt", org.jooq.impl.SQLDataType.BIGINT.nullable(false), this, "");

    /**
     * Create a <code>remocra.alerte_elt</code> table reference
     */
    public AlerteElt() {
        this(DSL.name("alerte_elt"), null);
    }

    /**
     * Create an aliased <code>remocra.alerte_elt</code> table reference
     */
    public AlerteElt(String alias) {
        this(DSL.name(alias), ALERTE_ELT);
    }

    /**
     * Create an aliased <code>remocra.alerte_elt</code> table reference
     */
    public AlerteElt(Name alias) {
        this(alias, ALERTE_ELT);
    }

    private AlerteElt(Name alias, Table<Record> aliased) {
        this(alias, aliased, null);
    }

    private AlerteElt(Name alias, Table<Record> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment(""));
    }

    public <O extends Record> AlerteElt(Table<O> child, ForeignKey<O, Record> key) {
        super(child, key, ALERTE_ELT);
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
        return Arrays.<Index>asList(Indexes.ALERTE_ELT_ALERTE_IDX, Indexes.ALERTE_ELT_GEOMETRIE_IDX, Indexes.ALERTE_ELT_PKEY, Indexes.ALERTE_ELT_SOUS_TYPE_ALERTE_ELT_IDX);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Identity<Record, Long> getIdentity() {
        return Keys.IDENTITY_ALERTE_ELT;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UniqueKey<Record> getPrimaryKey() {
        return Keys.ALERTE_ELT_PKEY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<UniqueKey<Record>> getKeys() {
        return Arrays.<UniqueKey<Record>>asList(Keys.ALERTE_ELT_PKEY);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ForeignKey<Record, ?>> getReferences() {
        return Arrays.<ForeignKey<Record, ?>>asList(Keys.ALERTE_ELT__FK72BDEDF7D653DAE6, Keys.ALERTE_ELT__FK72BDEDF72A91E8F3);
    }

    public Alerte alerte() {
        return new Alerte(this, Keys.ALERTE_ELT__FK72BDEDF7D653DAE6);
    }

    public SousTypeAlerteElt sousTypeAlerteElt() {
        return new SousTypeAlerteElt(this, Keys.ALERTE_ELT__FK72BDEDF72A91E8F3);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AlerteElt as(String alias) {
        return new AlerteElt(DSL.name(alias), this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AlerteElt as(Name alias) {
        return new AlerteElt(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public AlerteElt rename(String name) {
        return new AlerteElt(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public AlerteElt rename(Name name) {
        return new AlerteElt(name, null);
    }
}
