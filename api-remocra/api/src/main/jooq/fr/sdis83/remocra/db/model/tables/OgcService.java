/*
 * This file is generated by jOOQ.
 */
package fr.sdis83.remocra.db.model.tables;


import fr.sdis83.remocra.db.model.Indexes;
import fr.sdis83.remocra.db.model.Keys;
import fr.sdis83.remocra.db.model.Remocra;

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
 * Service de données mobilisable sur un serveur OGC
 */
@Generated(
    value = {
        "http://www.jooq.org",
        "jOOQ version:3.11.11"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class OgcService extends TableImpl<Record> {

    private static final long serialVersionUID = 1455995155;

    /**
     * The reference instance of <code>remocra.ogc_service</code>
     */
    public static final OgcService OGC_SERVICE = new OgcService();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<Record> getRecordType() {
        return Record.class;
    }

    /**
     * The column <code>remocra.ogc_service.id</code>. Identifiant interne
     */
    public final TableField<Record, Long> ID = createField("id", org.jooq.impl.SQLDataType.BIGINT.nullable(false).defaultValue(org.jooq.impl.DSL.field("nextval('remocra.ogc_service_id_seq'::regclass)", org.jooq.impl.SQLDataType.BIGINT)), this, "Identifiant interne");

    /**
     * The column <code>remocra.ogc_service.type_service</code>. Type de service OGC proposé par le serveur OGC
     */
    public final TableField<Record, String> TYPE_SERVICE = createField("type_service", org.jooq.impl.SQLDataType.VARCHAR.nullable(false), this, "Type de service OGC proposé par le serveur OGC");

    /**
     * The column <code>remocra.ogc_service.nom</code>. Titre associé aux métadonnées du service
     */
    public final TableField<Record, String> NOM = createField("nom", org.jooq.impl.SQLDataType.VARCHAR.nullable(false), this, "Titre associé aux métadonnées du service");

    /**
     * The column <code>remocra.ogc_service.description</code>. Résumé associé aux métadonnées du service
     */
    public final TableField<Record, String> DESCRIPTION = createField("description", org.jooq.impl.SQLDataType.VARCHAR, this, "Résumé associé aux métadonnées du service");

    /**
     * The column <code>remocra.ogc_service.ogc_source</code>. Serveur OGC associé
     */
    public final TableField<Record, Long> OGC_SOURCE = createField("ogc_source", org.jooq.impl.SQLDataType.BIGINT.nullable(false), this, "Serveur OGC associé");

    /**
     * Create a <code>remocra.ogc_service</code> table reference
     */
    public OgcService() {
        this(DSL.name("ogc_service"), null);
    }

    /**
     * Create an aliased <code>remocra.ogc_service</code> table reference
     */
    public OgcService(String alias) {
        this(DSL.name(alias), OGC_SERVICE);
    }

    /**
     * Create an aliased <code>remocra.ogc_service</code> table reference
     */
    public OgcService(Name alias) {
        this(alias, OGC_SERVICE);
    }

    private OgcService(Name alias, Table<Record> aliased) {
        this(alias, aliased, null);
    }

    private OgcService(Name alias, Table<Record> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment("Service de données mobilisable sur un serveur OGC"));
    }

    public <O extends Record> OgcService(Table<O> child, ForeignKey<O, Record> key) {
        super(child, key, OGC_SERVICE);
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
        return Arrays.<Index>asList(Indexes.OGC_SERVICE_OGC_SOURCE_IDX, Indexes.OGC_SERVICE_PKEY);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Identity<Record, Long> getIdentity() {
        return Keys.IDENTITY_OGC_SERVICE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UniqueKey<Record> getPrimaryKey() {
        return Keys.OGC_SERVICE_PKEY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<UniqueKey<Record>> getKeys() {
        return Arrays.<UniqueKey<Record>>asList(Keys.OGC_SERVICE_PKEY);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ForeignKey<Record, ?>> getReferences() {
        return Arrays.<ForeignKey<Record, ?>>asList(Keys.OGC_SERVICE__OGC_SOURCE_FK);
    }

    public OgcSource ogcSource() {
        return new OgcSource(this, Keys.OGC_SERVICE__OGC_SOURCE_FK);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public OgcService as(String alias) {
        return new OgcService(DSL.name(alias), this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public OgcService as(Name alias) {
        return new OgcService(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public OgcService rename(String name) {
        return new OgcService(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public OgcService rename(Name name) {
        return new OgcService(name, null);
    }
}