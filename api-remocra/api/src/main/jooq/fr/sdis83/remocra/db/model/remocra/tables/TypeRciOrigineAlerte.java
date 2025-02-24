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
public class TypeRciOrigineAlerte extends TableImpl<Record> {

    private static final long serialVersionUID = 1309993307;

    /**
     * The reference instance of <code>remocra.type_rci_origine_alerte</code>
     */
    public static final TypeRciOrigineAlerte TYPE_RCI_ORIGINE_ALERTE = new TypeRciOrigineAlerte();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<Record> getRecordType() {
        return Record.class;
    }

    /**
     * The column <code>remocra.type_rci_origine_alerte.id</code>.
     */
    public final TableField<Record, Long> ID = createField("id", org.jooq.impl.SQLDataType.BIGINT.nullable(false).defaultValue(org.jooq.impl.DSL.field("nextval('remocra.type_rci_origine_alerte_id_seq'::regclass)", org.jooq.impl.SQLDataType.BIGINT)), this, "");

    /**
     * The column <code>remocra.type_rci_origine_alerte.actif</code>.
     */
    public final TableField<Record, Boolean> ACTIF = createField("actif", org.jooq.impl.SQLDataType.BOOLEAN.nullable(false).defaultValue(org.jooq.impl.DSL.field("true", org.jooq.impl.SQLDataType.BOOLEAN)), this, "");

    /**
     * The column <code>remocra.type_rci_origine_alerte.code</code>.
     */
    public final TableField<Record, String> CODE = createField("code", org.jooq.impl.SQLDataType.VARCHAR.nullable(false), this, "");

    /**
     * The column <code>remocra.type_rci_origine_alerte.nom</code>.
     */
    public final TableField<Record, String> NOM = createField("nom", org.jooq.impl.SQLDataType.VARCHAR.nullable(false), this, "");

    /**
     * The column <code>remocra.type_rci_origine_alerte.version</code>.
     */
    public final TableField<Record, Integer> VERSION = createField("version", org.jooq.impl.SQLDataType.INTEGER.defaultValue(org.jooq.impl.DSL.field("1", org.jooq.impl.SQLDataType.INTEGER)), this, "");

    /**
     * Create a <code>remocra.type_rci_origine_alerte</code> table reference
     */
    public TypeRciOrigineAlerte() {
        this(DSL.name("type_rci_origine_alerte"), null);
    }

    /**
     * Create an aliased <code>remocra.type_rci_origine_alerte</code> table reference
     */
    public TypeRciOrigineAlerte(String alias) {
        this(DSL.name(alias), TYPE_RCI_ORIGINE_ALERTE);
    }

    /**
     * Create an aliased <code>remocra.type_rci_origine_alerte</code> table reference
     */
    public TypeRciOrigineAlerte(Name alias) {
        this(alias, TYPE_RCI_ORIGINE_ALERTE);
    }

    private TypeRciOrigineAlerte(Name alias, Table<Record> aliased) {
        this(alias, aliased, null);
    }

    private TypeRciOrigineAlerte(Name alias, Table<Record> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment(""));
    }

    public <O extends Record> TypeRciOrigineAlerte(Table<O> child, ForeignKey<O, Record> key) {
        super(child, key, TYPE_RCI_ORIGINE_ALERTE);
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
        return Arrays.<Index>asList(Indexes.TYPE_RCI_ORIGINE_ALERTE_PKEY);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Identity<Record, Long> getIdentity() {
        return Keys.IDENTITY_TYPE_RCI_ORIGINE_ALERTE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UniqueKey<Record> getPrimaryKey() {
        return Keys.TYPE_RCI_ORIGINE_ALERTE_PKEY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<UniqueKey<Record>> getKeys() {
        return Arrays.<UniqueKey<Record>>asList(Keys.TYPE_RCI_ORIGINE_ALERTE_PKEY);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TypeRciOrigineAlerte as(String alias) {
        return new TypeRciOrigineAlerte(DSL.name(alias), this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TypeRciOrigineAlerte as(Name alias) {
        return new TypeRciOrigineAlerte(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public TypeRciOrigineAlerte rename(String name) {
        return new TypeRciOrigineAlerte(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public TypeRciOrigineAlerte rename(Name name) {
        return new TypeRciOrigineAlerte(name, null);
    }
}
