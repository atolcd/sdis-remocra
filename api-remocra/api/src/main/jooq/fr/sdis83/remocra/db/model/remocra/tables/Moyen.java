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
 * Table de synchronisation des moyens pour utilisation dans REMOcRA en lien 
 * avec les interventions
 */
@Generated(
    value = {
        "http://www.jooq.org",
        "jOOQ version:3.11.11"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Moyen extends TableImpl<Record> {

    private static final long serialVersionUID = 562548399;

    /**
     * The reference instance of <code>remocra.moyen</code>
     */
    public static final Moyen MOYEN = new Moyen();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<Record> getRecordType() {
        return Record.class;
    }

    /**
     * The column <code>remocra.moyen.id</code>. Identifiant autogénéré
     */
    public final TableField<Record, Long> ID = createField("id", org.jooq.impl.SQLDataType.BIGINT.nullable(false).defaultValue(org.jooq.impl.DSL.field("nextval('remocra.moyen_id_seq'::regclass)", org.jooq.impl.SQLDataType.BIGINT)), this, "Identifiant autogénéré");

    /**
     * The column <code>remocra.moyen.type</code>.
     */
    public final TableField<Record, Long> TYPE = createField("type", org.jooq.impl.SQLDataType.BIGINT, this, "");

    /**
     * The column <code>remocra.moyen.nom</code>. Nom du moyen
     */
    public final TableField<Record, String> NOM = createField("nom", org.jooq.impl.SQLDataType.VARCHAR, this, "Nom du moyen");

    /**
     * The column <code>remocra.moyen.intervention</code>. Intervention à laquelle est rattachée le moyen
     */
    public final TableField<Record, Long> INTERVENTION = createField("intervention", org.jooq.impl.SQLDataType.BIGINT, this, "Intervention à laquelle est rattachée le moyen");

    /**
     * @deprecated Unknown data type. Please define an explicit {@link org.jooq.Binding} to specify how this type should be handled. Deprecation can be turned off using {@literal <deprecationOnUnknownTypes/>} in your code generator configuration.
     */
    @java.lang.Deprecated
    public final TableField<Record, Object> GEOMETRIE = createField("geometrie", org.jooq.impl.DefaultDataType.getDefaultDataType("\"public\".\"geometry\""), this, "Dernier emplacement connu du moyen");

    /**
     * Create a <code>remocra.moyen</code> table reference
     */
    public Moyen() {
        this(DSL.name("moyen"), null);
    }

    /**
     * Create an aliased <code>remocra.moyen</code> table reference
     */
    public Moyen(String alias) {
        this(DSL.name(alias), MOYEN);
    }

    /**
     * Create an aliased <code>remocra.moyen</code> table reference
     */
    public Moyen(Name alias) {
        this(alias, MOYEN);
    }

    private Moyen(Name alias, Table<Record> aliased) {
        this(alias, aliased, null);
    }

    private Moyen(Name alias, Table<Record> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment("Table de synchronisation des moyens pour utilisation dans REMOcRA en lien avec les interventions"));
    }

    public <O extends Record> Moyen(Table<O> child, ForeignKey<O, Record> key) {
        super(child, key, MOYEN);
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
        return Arrays.<Index>asList(Indexes.ENGIN_PKEY, Indexes.MOYEN_GEOMETRIE_IDX);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Identity<Record, Long> getIdentity() {
        return Keys.IDENTITY_MOYEN;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UniqueKey<Record> getPrimaryKey() {
        return Keys.ENGIN_PKEY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<UniqueKey<Record>> getKeys() {
        return Arrays.<UniqueKey<Record>>asList(Keys.ENGIN_PKEY);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ForeignKey<Record, ?>> getReferences() {
        return Arrays.<ForeignKey<Record, ?>>asList(Keys.MOYEN__FK_MOYEN_TYPE_MOYEN, Keys.MOYEN__FK_MOYEN_INTERVENTION);
    }

    public TypeMoyen typeMoyen() {
        return new TypeMoyen(this, Keys.MOYEN__FK_MOYEN_TYPE_MOYEN);
    }

    public Intervention intervention() {
        return new Intervention(this, Keys.MOYEN__FK_MOYEN_INTERVENTION);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Moyen as(String alias) {
        return new Moyen(DSL.name(alias), this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Moyen as(Name alias) {
        return new Moyen(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public Moyen rename(String name) {
        return new Moyen(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public Moyen rename(Name name) {
        return new Moyen(name, null);
    }
}