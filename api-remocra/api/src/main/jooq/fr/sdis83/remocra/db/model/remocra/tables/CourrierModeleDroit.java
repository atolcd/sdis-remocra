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
 * Profil de droit autorisé pour générer un courrier
 */
@Generated(
    value = {
        "http://www.jooq.org",
        "jOOQ version:3.11.11"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class CourrierModeleDroit extends TableImpl<Record> {

    private static final long serialVersionUID = -1408055328;

    /**
     * The reference instance of <code>remocra.courrier_modele_droit</code>
     */
    public static final CourrierModeleDroit COURRIER_MODELE_DROIT = new CourrierModeleDroit();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<Record> getRecordType() {
        return Record.class;
    }

    /**
     * The column <code>remocra.courrier_modele_droit.modele</code>. Identifiant du modèle de courrier
     */
    public final TableField<Record, Long> MODELE = createField("modele", org.jooq.impl.SQLDataType.BIGINT.nullable(false), this, "Identifiant du modèle de courrier");

    /**
     * The column <code>remocra.courrier_modele_droit.profil_droit</code>. Identifiant du profil de droit autorisé
     */
    public final TableField<Record, Long> PROFIL_DROIT = createField("profil_droit", org.jooq.impl.SQLDataType.BIGINT.nullable(false), this, "Identifiant du profil de droit autorisé");

    /**
     * Create a <code>remocra.courrier_modele_droit</code> table reference
     */
    public CourrierModeleDroit() {
        this(DSL.name("courrier_modele_droit"), null);
    }

    /**
     * Create an aliased <code>remocra.courrier_modele_droit</code> table reference
     */
    public CourrierModeleDroit(String alias) {
        this(DSL.name(alias), COURRIER_MODELE_DROIT);
    }

    /**
     * Create an aliased <code>remocra.courrier_modele_droit</code> table reference
     */
    public CourrierModeleDroit(Name alias) {
        this(alias, COURRIER_MODELE_DROIT);
    }

    private CourrierModeleDroit(Name alias, Table<Record> aliased) {
        this(alias, aliased, null);
    }

    private CourrierModeleDroit(Name alias, Table<Record> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment("Profil de droit autorisé pour générer un courrier"));
    }

    public <O extends Record> CourrierModeleDroit(Table<O> child, ForeignKey<O, Record> key) {
        super(child, key, COURRIER_MODELE_DROIT);
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
        return Arrays.<Index>asList(Indexes.COURRIER_MODELE_DROIT_PKEY);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UniqueKey<Record> getPrimaryKey() {
        return Keys.COURRIER_MODELE_DROIT_PKEY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<UniqueKey<Record>> getKeys() {
        return Arrays.<UniqueKey<Record>>asList(Keys.COURRIER_MODELE_DROIT_PKEY);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ForeignKey<Record, ?>> getReferences() {
        return Arrays.<ForeignKey<Record, ?>>asList(Keys.COURRIER_MODELE_DROIT__COURRIER_MODELE_DROIT_MODELE, Keys.COURRIER_MODELE_DROIT__COURRIER_MODELE_DROIT_PROFIL_DROIT);
    }

    public CourrierModele courrierModele() {
        return new CourrierModele(this, Keys.COURRIER_MODELE_DROIT__COURRIER_MODELE_DROIT_MODELE);
    }

    public ProfilDroit profilDroit() {
        return new ProfilDroit(this, Keys.COURRIER_MODELE_DROIT__COURRIER_MODELE_DROIT_PROFIL_DROIT);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CourrierModeleDroit as(String alias) {
        return new CourrierModeleDroit(DSL.name(alias), this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CourrierModeleDroit as(Name alias) {
        return new CourrierModeleDroit(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public CourrierModeleDroit rename(String name) {
        return new CourrierModeleDroit(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public CourrierModeleDroit rename(Name name) {
        return new CourrierModeleDroit(name, null);
    }
}
