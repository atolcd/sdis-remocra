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
 * Réseau d'alimentation des hydrants
 */
@Generated(
    value = {
        "http://www.jooq.org",
        "jOOQ version:3.11.11"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class TypeReseauAlimentation extends TableImpl<Record> {

    private static final long serialVersionUID = -812241119;

    /**
     * The reference instance of <code>remocra.type_reseau_alimentation</code>
     */
    public static final TypeReseauAlimentation TYPE_RESEAU_ALIMENTATION = new TypeReseauAlimentation();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<Record> getRecordType() {
        return Record.class;
    }

    /**
     * The column <code>remocra.type_reseau_alimentation.id</code>. Identifiant interne
     */
    public final TableField<Record, Long> ID = createField("id", org.jooq.impl.SQLDataType.BIGINT.nullable(false).defaultValue(org.jooq.impl.DSL.field("nextval('remocra.type_reseau_alimentation_id_seq'::regclass)", org.jooq.impl.SQLDataType.BIGINT)), this, "Identifiant interne");

    /**
     * The column <code>remocra.type_reseau_alimentation.actif</code>. Sélectionnable dans l'interface
     */
    public final TableField<Record, Boolean> ACTIF = createField("actif", org.jooq.impl.SQLDataType.BOOLEAN.nullable(false).defaultValue(org.jooq.impl.DSL.field("true", org.jooq.impl.SQLDataType.BOOLEAN)), this, "Sélectionnable dans l'interface");

    /**
     * The column <code>remocra.type_reseau_alimentation.code</code>. Code du statut. Facilite les échanges de données
     */
    public final TableField<Record, String> CODE = createField("code", org.jooq.impl.SQLDataType.VARCHAR.nullable(false), this, "Code du statut. Facilite les échanges de données");

    /**
     * The column <code>remocra.type_reseau_alimentation.nom</code>. Libellé du réseau d'alimentation
     */
    public final TableField<Record, String> NOM = createField("nom", org.jooq.impl.SQLDataType.VARCHAR.nullable(false), this, "Libellé du réseau d'alimentation");

    /**
     * The column <code>remocra.type_reseau_alimentation.version</code>.
     */
    public final TableField<Record, Integer> VERSION = createField("version", org.jooq.impl.SQLDataType.INTEGER.defaultValue(org.jooq.impl.DSL.field("1", org.jooq.impl.SQLDataType.INTEGER)), this, "");

    /**
     * Create a <code>remocra.type_reseau_alimentation</code> table reference
     */
    public TypeReseauAlimentation() {
        this(DSL.name("type_reseau_alimentation"), null);
    }

    /**
     * Create an aliased <code>remocra.type_reseau_alimentation</code> table reference
     */
    public TypeReseauAlimentation(String alias) {
        this(DSL.name(alias), TYPE_RESEAU_ALIMENTATION);
    }

    /**
     * Create an aliased <code>remocra.type_reseau_alimentation</code> table reference
     */
    public TypeReseauAlimentation(Name alias) {
        this(alias, TYPE_RESEAU_ALIMENTATION);
    }

    private TypeReseauAlimentation(Name alias, Table<Record> aliased) {
        this(alias, aliased, null);
    }

    private TypeReseauAlimentation(Name alias, Table<Record> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment("Réseau d'alimentation des hydrants"));
    }

    public <O extends Record> TypeReseauAlimentation(Table<O> child, ForeignKey<O, Record> key) {
        super(child, key, TYPE_RESEAU_ALIMENTATION);
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
        return Arrays.<Index>asList(Indexes.TYPE_RESEAU_ALIMENTATION_PKEY);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Identity<Record, Long> getIdentity() {
        return Keys.IDENTITY_TYPE_RESEAU_ALIMENTATION;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UniqueKey<Record> getPrimaryKey() {
        return Keys.TYPE_RESEAU_ALIMENTATION_PKEY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<UniqueKey<Record>> getKeys() {
        return Arrays.<UniqueKey<Record>>asList(Keys.TYPE_RESEAU_ALIMENTATION_PKEY);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TypeReseauAlimentation as(String alias) {
        return new TypeReseauAlimentation(DSL.name(alias), this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TypeReseauAlimentation as(Name alias) {
        return new TypeReseauAlimentation(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public TypeReseauAlimentation rename(String name) {
        return new TypeReseauAlimentation(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public TypeReseauAlimentation rename(Name name) {
        return new TypeReseauAlimentation(name, null);
    }
}