/*
 * This file is generated by jOOQ.
 */
package fr.sdis83.remocra.db.model.tables;


import fr.sdis83.remocra.db.converter.OffsetDateTimeToInstantConverter;
import fr.sdis83.remocra.db.model.Indexes;
import fr.sdis83.remocra.db.model.Keys;
import fr.sdis83.remocra.db.model.Remocra;

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
 * Evènement associé à une crise. Route inondée sur la RD 84 à la position...
 */
@Generated(
    value = {
        "http://www.jooq.org",
        "jOOQ version:3.11.11"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class CriseEvenement extends TableImpl<Record> {

    private static final long serialVersionUID = 1662523075;

    /**
     * The reference instance of <code>remocra.crise_evenement</code>
     */
    public static final CriseEvenement CRISE_EVENEMENT = new CriseEvenement();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<Record> getRecordType() {
        return Record.class;
    }

    /**
     * The column <code>remocra.crise_evenement.id</code>. Identifiant interne
     */
    public final TableField<Record, Long> ID = createField("id", org.jooq.impl.SQLDataType.BIGINT.nullable(false).defaultValue(org.jooq.impl.DSL.field("nextval('remocra.crise_evenement_id_seq'::regclass)", org.jooq.impl.SQLDataType.BIGINT)), this, "Identifiant interne");

    /**
     * @deprecated Unknown data type. Please define an explicit {@link org.jooq.Binding} to specify how this type should be handled. Deprecation can be turned off using {@literal <deprecationOnUnknownTypes/>} in your code generator configuration.
     */
    @java.lang.Deprecated
    public final TableField<Record, Object> GEOMETRIE = createField("geometrie", org.jooq.impl.DefaultDataType.getDefaultDataType("\"public\".\"geometry\""), this, "");

    /**
     * The column <code>remocra.crise_evenement.nom</code>. Titre de l'évènement
     */
    public final TableField<Record, String> NOM = createField("nom", org.jooq.impl.SQLDataType.VARCHAR.nullable(false), this, "Titre de l'évènement");

    /**
     * The column <code>remocra.crise_evenement.description</code>. Information générale sur l'évènement (cause, autre information utile, etc.)
     */
    public final TableField<Record, String> DESCRIPTION = createField("description", org.jooq.impl.SQLDataType.VARCHAR, this, "Information générale sur l'évènement (cause, autre information utile, etc.)");

    /**
     * The column <code>remocra.crise_evenement.constat</code>. Date et heure de constat du phénomène ou de l'action
     */
    public final TableField<Record, Instant> CONSTAT = createField("constat", org.jooq.impl.SQLDataType.TIMESTAMPWITHTIMEZONE.nullable(false), this, "Date et heure de constat du phénomène ou de l'action", new OffsetDateTimeToInstantConverter());

    /**
     * The column <code>remocra.crise_evenement.redefinition</code>. Date et heure de modification de l'évènement
     */
    public final TableField<Record, Instant> REDEFINITION = createField("redefinition", org.jooq.impl.SQLDataType.TIMESTAMPWITHTIMEZONE, this, "Date et heure de modification de l'évènement", new OffsetDateTimeToInstantConverter());

    /**
     * The column <code>remocra.crise_evenement.cloture</code>. Date et heure de fin de vie de l'évènement
     */
    public final TableField<Record, Instant> CLOTURE = createField("cloture", org.jooq.impl.SQLDataType.TIMESTAMPWITHTIMEZONE, this, "Date et heure de fin de vie de l'évènement", new OffsetDateTimeToInstantConverter());

    /**
     * The column <code>remocra.crise_evenement.origine</code>. Origine de l'information : habitant, service de gendarmerie, twitter, etc.
     */
    public final TableField<Record, String> ORIGINE = createField("origine", org.jooq.impl.SQLDataType.VARCHAR, this, "Origine de l'information : habitant, service de gendarmerie, twitter, etc.");

    /**
     * The column <code>remocra.crise_evenement.importance</code>. Niveau d'importance permettant de filtrer facilement
     */
    public final TableField<Record, Integer> IMPORTANCE = createField("importance", org.jooq.impl.SQLDataType.INTEGER, this, "Niveau d'importance permettant de filtrer facilement");

    /**
     * The column <code>remocra.crise_evenement.tags</code>. Tags permettant qualifier facilement un évènement
     */
    public final TableField<Record, String> TAGS = createField("tags", org.jooq.impl.SQLDataType.VARCHAR, this, "Tags permettant qualifier facilement un évènement");

    /**
     * The column <code>remocra.crise_evenement.crise</code>. Crise associée
     */
    public final TableField<Record, Long> CRISE = createField("crise", org.jooq.impl.SQLDataType.BIGINT.nullable(false), this, "Crise associée");

    /**
     * The column <code>remocra.crise_evenement.nature_evenement</code>. Nature d'évènement associée
     */
    public final TableField<Record, Long> NATURE_EVENEMENT = createField("nature_evenement", org.jooq.impl.SQLDataType.BIGINT.nullable(false), this, "Nature d'évènement associée");

    /**
     * The column <code>remocra.crise_evenement.auteur_evenement</code>.
     */
    public final TableField<Record, Long> AUTEUR_EVENEMENT = createField("auteur_evenement", org.jooq.impl.SQLDataType.BIGINT, this, "");

    /**
     * The column <code>remocra.crise_evenement.contexte</code>.
     */
    public final TableField<Record, String> CONTEXTE = createField("contexte", org.jooq.impl.SQLDataType.VARCHAR.nullable(false).defaultValue(org.jooq.impl.DSL.field("'ANTICIPATION'::character varying", org.jooq.impl.SQLDataType.VARCHAR)), this, "");

    /**
     * Create a <code>remocra.crise_evenement</code> table reference
     */
    public CriseEvenement() {
        this(DSL.name("crise_evenement"), null);
    }

    /**
     * Create an aliased <code>remocra.crise_evenement</code> table reference
     */
    public CriseEvenement(String alias) {
        this(DSL.name(alias), CRISE_EVENEMENT);
    }

    /**
     * Create an aliased <code>remocra.crise_evenement</code> table reference
     */
    public CriseEvenement(Name alias) {
        this(alias, CRISE_EVENEMENT);
    }

    private CriseEvenement(Name alias, Table<Record> aliased) {
        this(alias, aliased, null);
    }

    private CriseEvenement(Name alias, Table<Record> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment("Evènement associé à une crise. Route inondée sur la RD 84 à la position..."));
    }

    public <O extends Record> CriseEvenement(Table<O> child, ForeignKey<O, Record> key) {
        super(child, key, CRISE_EVENEMENT);
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
        return Arrays.<Index>asList(Indexes.CRISE_EVENEMENT_CRISE_IDX, Indexes.CRISE_EVENEMENT_GEOMETRIE_IDX, Indexes.CRISE_EVENEMENT_NATURE_EVENEMENT_IDX, Indexes.CRISE_EVENEMENT_PKEY);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Identity<Record, Long> getIdentity() {
        return Keys.IDENTITY_CRISE_EVENEMENT;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UniqueKey<Record> getPrimaryKey() {
        return Keys.CRISE_EVENEMENT_PKEY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<UniqueKey<Record>> getKeys() {
        return Arrays.<UniqueKey<Record>>asList(Keys.CRISE_EVENEMENT_PKEY);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ForeignKey<Record, ?>> getReferences() {
        return Arrays.<ForeignKey<Record, ?>>asList(Keys.CRISE_EVENEMENT__CRISE_EVENEMENT_CRISE_FK, Keys.CRISE_EVENEMENT__CRISE_EVENEMENT_NATURE_EVENEMENT_FK, Keys.CRISE_EVENEMENT__CRISE_EVENEMENT_UTILISATEUR);
    }

    public Crise crise() {
        return new Crise(this, Keys.CRISE_EVENEMENT__CRISE_EVENEMENT_CRISE_FK);
    }

    public TypeCriseNatureEvenement typeCriseNatureEvenement() {
        return new TypeCriseNatureEvenement(this, Keys.CRISE_EVENEMENT__CRISE_EVENEMENT_NATURE_EVENEMENT_FK);
    }

    public Utilisateur utilisateur() {
        return new Utilisateur(this, Keys.CRISE_EVENEMENT__CRISE_EVENEMENT_UTILISATEUR);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CriseEvenement as(String alias) {
        return new CriseEvenement(DSL.name(alias), this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CriseEvenement as(Name alias) {
        return new CriseEvenement(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public CriseEvenement rename(String name) {
        return new CriseEvenement(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public CriseEvenement rename(Name name) {
        return new CriseEvenement(name, null);
    }
}