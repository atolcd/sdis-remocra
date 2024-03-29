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
 * Gestion de crise : répertoire de lieux mobilisable dans le cadre d'une 
 * action "Zoomer sur..". Associé à un épisode de crise
 */
@Generated(
    value = {
        "http://www.jooq.org",
        "jOOQ version:3.11.11"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class CriseRepertoireLieu extends TableImpl<Record> {

    private static final long serialVersionUID = -1067369350;

    /**
     * The reference instance of <code>remocra.crise_repertoire_lieu</code>
     */
    public static final CriseRepertoireLieu CRISE_REPERTOIRE_LIEU = new CriseRepertoireLieu();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<Record> getRecordType() {
        return Record.class;
    }

    /**
     * The column <code>remocra.crise_repertoire_lieu.crise</code>. Identifiant de la crise associée
     */
    public final TableField<Record, Long> CRISE = createField("crise", org.jooq.impl.SQLDataType.BIGINT.nullable(false), this, "Identifiant de la crise associée");

    /**
     * The column <code>remocra.crise_repertoire_lieu.repertoire_lieu</code>. Identifiant du répertoire de lieu associé
     */
    public final TableField<Record, Long> REPERTOIRE_LIEU = createField("repertoire_lieu", org.jooq.impl.SQLDataType.BIGINT.nullable(false), this, "Identifiant du répertoire de lieu associé");

    /**
     * Create a <code>remocra.crise_repertoire_lieu</code> table reference
     */
    public CriseRepertoireLieu() {
        this(DSL.name("crise_repertoire_lieu"), null);
    }

    /**
     * Create an aliased <code>remocra.crise_repertoire_lieu</code> table reference
     */
    public CriseRepertoireLieu(String alias) {
        this(DSL.name(alias), CRISE_REPERTOIRE_LIEU);
    }

    /**
     * Create an aliased <code>remocra.crise_repertoire_lieu</code> table reference
     */
    public CriseRepertoireLieu(Name alias) {
        this(alias, CRISE_REPERTOIRE_LIEU);
    }

    private CriseRepertoireLieu(Name alias, Table<Record> aliased) {
        this(alias, aliased, null);
    }

    private CriseRepertoireLieu(Name alias, Table<Record> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment("Gestion de crise : répertoire de lieux mobilisable dans le cadre d'une action \"Zoomer sur..\". Associé à un épisode de crise"));
    }

    public <O extends Record> CriseRepertoireLieu(Table<O> child, ForeignKey<O, Record> key) {
        super(child, key, CRISE_REPERTOIRE_LIEU);
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
        return Arrays.<Index>asList(Indexes.CRISE_REPERTOIRE_LIEU_PKEY);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UniqueKey<Record> getPrimaryKey() {
        return Keys.CRISE_REPERTOIRE_LIEU_PKEY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<UniqueKey<Record>> getKeys() {
        return Arrays.<UniqueKey<Record>>asList(Keys.CRISE_REPERTOIRE_LIEU_PKEY);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ForeignKey<Record, ?>> getReferences() {
        return Arrays.<ForeignKey<Record, ?>>asList(Keys.CRISE_REPERTOIRE_LIEU__CRISE_REPERTOIRE_LIEU_CRISE_FK, Keys.CRISE_REPERTOIRE_LIEU__CRISE_REPERTOIRE_LIEU_REPERTOIRE_LIEU_FK);
    }

    public Crise crise() {
        return new Crise(this, Keys.CRISE_REPERTOIRE_LIEU__CRISE_REPERTOIRE_LIEU_CRISE_FK);
    }

    public RepertoireLieu repertoireLieu() {
        return new RepertoireLieu(this, Keys.CRISE_REPERTOIRE_LIEU__CRISE_REPERTOIRE_LIEU_REPERTOIRE_LIEU_FK);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CriseRepertoireLieu as(String alias) {
        return new CriseRepertoireLieu(DSL.name(alias), this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CriseRepertoireLieu as(Name alias) {
        return new CriseRepertoireLieu(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public CriseRepertoireLieu rename(String name) {
        return new CriseRepertoireLieu(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public CriseRepertoireLieu rename(Name name) {
        return new CriseRepertoireLieu(name, null);
    }
}
