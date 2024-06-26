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
public class TypeHydrantAnomalie extends TableImpl<Record> {

    private static final long serialVersionUID = 1906405032;

    /**
     * The reference instance of <code>remocra.type_hydrant_anomalie</code>
     */
    public static final TypeHydrantAnomalie TYPE_HYDRANT_ANOMALIE = new TypeHydrantAnomalie();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<Record> getRecordType() {
        return Record.class;
    }

    /**
     * The column <code>remocra.type_hydrant_anomalie.id</code>.
     */
    public final TableField<Record, Long> ID = createField("id", org.jooq.impl.SQLDataType.BIGINT.nullable(false).defaultValue(org.jooq.impl.DSL.field("nextval('remocra.type_hydrant_anomalie_id_seq'::regclass)", org.jooq.impl.SQLDataType.BIGINT)), this, "");

    /**
     * The column <code>remocra.type_hydrant_anomalie.actif</code>.
     */
    public final TableField<Record, Boolean> ACTIF = createField("actif", org.jooq.impl.SQLDataType.BOOLEAN.defaultValue(org.jooq.impl.DSL.field("true", org.jooq.impl.SQLDataType.BOOLEAN)), this, "");

    /**
     * The column <code>remocra.type_hydrant_anomalie.code</code>.
     */
    public final TableField<Record, String> CODE = createField("code", org.jooq.impl.SQLDataType.VARCHAR.nullable(false), this, "");

    /**
     * The column <code>remocra.type_hydrant_anomalie.commentaire</code>.
     */
    public final TableField<Record, String> COMMENTAIRE = createField("commentaire", org.jooq.impl.SQLDataType.VARCHAR, this, "");

    /**
     * The column <code>remocra.type_hydrant_anomalie.nom</code>.
     */
    public final TableField<Record, String> NOM = createField("nom", org.jooq.impl.SQLDataType.VARCHAR.nullable(false), this, "");

    /**
     * The column <code>remocra.type_hydrant_anomalie.version</code>.
     */
    public final TableField<Record, Integer> VERSION = createField("version", org.jooq.impl.SQLDataType.INTEGER.defaultValue(org.jooq.impl.DSL.field("1", org.jooq.impl.SQLDataType.INTEGER)), this, "");

    /**
     * The column <code>remocra.type_hydrant_anomalie.critere</code>.
     */
    public final TableField<Record, Long> CRITERE = createField("critere", org.jooq.impl.SQLDataType.BIGINT, this, "");

    /**
     * Create a <code>remocra.type_hydrant_anomalie</code> table reference
     */
    public TypeHydrantAnomalie() {
        this(DSL.name("type_hydrant_anomalie"), null);
    }

    /**
     * Create an aliased <code>remocra.type_hydrant_anomalie</code> table reference
     */
    public TypeHydrantAnomalie(String alias) {
        this(DSL.name(alias), TYPE_HYDRANT_ANOMALIE);
    }

    /**
     * Create an aliased <code>remocra.type_hydrant_anomalie</code> table reference
     */
    public TypeHydrantAnomalie(Name alias) {
        this(alias, TYPE_HYDRANT_ANOMALIE);
    }

    private TypeHydrantAnomalie(Name alias, Table<Record> aliased) {
        this(alias, aliased, null);
    }

    private TypeHydrantAnomalie(Name alias, Table<Record> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment(""));
    }

    public <O extends Record> TypeHydrantAnomalie(Table<O> child, ForeignKey<O, Record> key) {
        super(child, key, TYPE_HYDRANT_ANOMALIE);
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
        return Arrays.<Index>asList(Indexes.TYPE_HYDRANT_ANOMALIE_CRITERE_IDX, Indexes.TYPE_HYDRANT_ANOMALIE_PKEY);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Identity<Record, Long> getIdentity() {
        return Keys.IDENTITY_TYPE_HYDRANT_ANOMALIE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UniqueKey<Record> getPrimaryKey() {
        return Keys.TYPE_HYDRANT_ANOMALIE_PKEY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<UniqueKey<Record>> getKeys() {
        return Arrays.<UniqueKey<Record>>asList(Keys.TYPE_HYDRANT_ANOMALIE_PKEY);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ForeignKey<Record, ?>> getReferences() {
        return Arrays.<ForeignKey<Record, ?>>asList(Keys.TYPE_HYDRANT_ANOMALIE__FKAAFAFC6EFD3AE2E2);
    }

    public TypeHydrantCritere typeHydrantCritere() {
        return new TypeHydrantCritere(this, Keys.TYPE_HYDRANT_ANOMALIE__FKAAFAFC6EFD3AE2E2);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TypeHydrantAnomalie as(String alias) {
        return new TypeHydrantAnomalie(DSL.name(alias), this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TypeHydrantAnomalie as(Name alias) {
        return new TypeHydrantAnomalie(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public TypeHydrantAnomalie rename(String name) {
        return new TypeHydrantAnomalie(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public TypeHydrantAnomalie rename(Name name) {
        return new TypeHydrantAnomalie(name, null);
    }
}
