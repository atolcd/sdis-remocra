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
 * Visite de contrôle réalisée sur une parcelle soumise à une obligation légale 
 * de débroussaillement
 */
@Generated(
    value = {
        "http://www.jooq.org",
        "jOOQ version:3.11.11"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class OldebVisite extends TableImpl<Record> {

    private static final long serialVersionUID = 886827711;

    /**
     * The reference instance of <code>remocra.oldeb_visite</code>
     */
    public static final OldebVisite OLDEB_VISITE = new OldebVisite();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<Record> getRecordType() {
        return Record.class;
    }

    /**
     * The column <code>remocra.oldeb_visite.id</code>.
     */
    public final TableField<Record, Long> ID = createField("id", org.jooq.impl.SQLDataType.BIGINT.nullable(false).defaultValue(org.jooq.impl.DSL.field("nextval('remocra.oldeb_visite_id_seq'::regclass)", org.jooq.impl.SQLDataType.BIGINT)), this, "");

    /**
     * The column <code>remocra.oldeb_visite.code</code>.
     */
    public final TableField<Record, String> CODE = createField("code", org.jooq.impl.SQLDataType.VARCHAR.nullable(false).defaultValue(org.jooq.impl.DSL.field("md5(((now() || 'oldeb_visite_document'::text) || random()))", org.jooq.impl.SQLDataType.VARCHAR)), this, "");

    /**
     * The column <code>remocra.oldeb_visite.date_visite</code>.
     */
    public final TableField<Record, Instant> DATE_VISITE = createField("date_visite", org.jooq.impl.SQLDataType.TIMESTAMP, this, "", new TimestampToInstantConverter());

    /**
     * The column <code>remocra.oldeb_visite.agent</code>.
     */
    public final TableField<Record, String> AGENT = createField("agent", org.jooq.impl.SQLDataType.VARCHAR.nullable(false), this, "");

    /**
     * The column <code>remocra.oldeb_visite.observation</code>.
     */
    public final TableField<Record, String> OBSERVATION = createField("observation", org.jooq.impl.SQLDataType.VARCHAR, this, "");

    /**
     * The column <code>remocra.oldeb_visite.oldeb</code>.
     */
    public final TableField<Record, Long> OLDEB = createField("oldeb", org.jooq.impl.SQLDataType.BIGINT.nullable(false), this, "");

    /**
     * The column <code>remocra.oldeb_visite.utilisateur</code>.
     */
    public final TableField<Record, Long> UTILISATEUR = createField("utilisateur", org.jooq.impl.SQLDataType.BIGINT, this, "");

    /**
     * The column <code>remocra.oldeb_visite.debroussaillement_parcelle</code>.
     */
    public final TableField<Record, Long> DEBROUSSAILLEMENT_PARCELLE = createField("debroussaillement_parcelle", org.jooq.impl.SQLDataType.BIGINT.nullable(false), this, "");

    /**
     * The column <code>remocra.oldeb_visite.debroussaillement_acces</code>.
     */
    public final TableField<Record, Long> DEBROUSSAILLEMENT_ACCES = createField("debroussaillement_acces", org.jooq.impl.SQLDataType.BIGINT.nullable(false), this, "");

    /**
     * The column <code>remocra.oldeb_visite.avis</code>.
     */
    public final TableField<Record, Long> AVIS = createField("avis", org.jooq.impl.SQLDataType.BIGINT.nullable(false), this, "");

    /**
     * The column <code>remocra.oldeb_visite.action</code>.
     */
    public final TableField<Record, Long> ACTION = createField("action", org.jooq.impl.SQLDataType.BIGINT.nullable(false), this, "");

    /**
     * Create a <code>remocra.oldeb_visite</code> table reference
     */
    public OldebVisite() {
        this(DSL.name("oldeb_visite"), null);
    }

    /**
     * Create an aliased <code>remocra.oldeb_visite</code> table reference
     */
    public OldebVisite(String alias) {
        this(DSL.name(alias), OLDEB_VISITE);
    }

    /**
     * Create an aliased <code>remocra.oldeb_visite</code> table reference
     */
    public OldebVisite(Name alias) {
        this(alias, OLDEB_VISITE);
    }

    private OldebVisite(Name alias, Table<Record> aliased) {
        this(alias, aliased, null);
    }

    private OldebVisite(Name alias, Table<Record> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment("Visite de contrôle réalisée sur une parcelle soumise à une obligation légale de débroussaillement"));
    }

    public <O extends Record> OldebVisite(Table<O> child, ForeignKey<O, Record> key) {
        super(child, key, OLDEB_VISITE);
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
        return Arrays.<Index>asList(Indexes.OLDEB_VISITE_ACTION_IDX, Indexes.OLDEB_VISITE_AVIS_IDX, Indexes.OLDEB_VISITE_CODE_KEY, Indexes.OLDEB_VISITE_DEBROUSSAILLEMENT_ACCES_IDX, Indexes.OLDEB_VISITE_DEBROUSSAILLEMENT_PARCELLE_IDX, Indexes.OLDEB_VISITE_OLDEB_IDX, Indexes.OLDEB_VISITE_PKEY, Indexes.OLDEB_VISITE_UTILISATEUR_IDX);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Identity<Record, Long> getIdentity() {
        return Keys.IDENTITY_OLDEB_VISITE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UniqueKey<Record> getPrimaryKey() {
        return Keys.OLDEB_VISITE_PKEY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<UniqueKey<Record>> getKeys() {
        return Arrays.<UniqueKey<Record>>asList(Keys.OLDEB_VISITE_PKEY, Keys.OLDEB_VISITE_CODE_KEY);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ForeignKey<Record, ?>> getReferences() {
        return Arrays.<ForeignKey<Record, ?>>asList(Keys.OLDEB_VISITE__FK_OLDEB_VISITE_OLDEB, Keys.OLDEB_VISITE__FK_OLDEB_VISITE_UTILISATEUR, Keys.OLDEB_VISITE__FK_OLDEB_VISITE_DEBROUSSAILLEMENT_PARCELLE, Keys.OLDEB_VISITE__FK_OLDEB_VISITE_DEBROUSSAILLEMENT_ACCES, Keys.OLDEB_VISITE__FK_OLDEB_VISITE_AVIS, Keys.OLDEB_VISITE__FK_OLDEB_VISITE_ACTION);
    }

    public Oldeb oldeb() {
        return new Oldeb(this, Keys.OLDEB_VISITE__FK_OLDEB_VISITE_OLDEB);
    }

    public Utilisateur utilisateur() {
        return new Utilisateur(this, Keys.OLDEB_VISITE__FK_OLDEB_VISITE_UTILISATEUR);
    }

    public TypeOldebDebroussaillement oldebVisite_FkOldebVisiteDebroussaillementParcelle() {
        return new TypeOldebDebroussaillement(this, Keys.OLDEB_VISITE__FK_OLDEB_VISITE_DEBROUSSAILLEMENT_PARCELLE);
    }

    public TypeOldebDebroussaillement oldebVisite_FkOldebVisiteDebroussaillementAcces() {
        return new TypeOldebDebroussaillement(this, Keys.OLDEB_VISITE__FK_OLDEB_VISITE_DEBROUSSAILLEMENT_ACCES);
    }

    public TypeOldebAvis typeOldebAvis() {
        return new TypeOldebAvis(this, Keys.OLDEB_VISITE__FK_OLDEB_VISITE_AVIS);
    }

    public TypeOldebAction typeOldebAction() {
        return new TypeOldebAction(this, Keys.OLDEB_VISITE__FK_OLDEB_VISITE_ACTION);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public OldebVisite as(String alias) {
        return new OldebVisite(DSL.name(alias), this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public OldebVisite as(Name alias) {
        return new OldebVisite(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public OldebVisite rename(String name) {
        return new OldebVisite(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public OldebVisite rename(Name name) {
        return new OldebVisite(name, null);
    }
}