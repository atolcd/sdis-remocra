/*
 * This file is generated by jOOQ.
 */
package fr.sdis83.remocra.db.model.tracabilite.tables;


import fr.sdis83.remocra.db.converter.OffsetDateTimeToInstantConverter;
import fr.sdis83.remocra.db.converter.TimestampToInstantConverter;
import fr.sdis83.remocra.db.model.remocra.tables.Organisme;
import fr.sdis83.remocra.db.model.remocra.tables.Utilisateur;
import fr.sdis83.remocra.db.model.tracabilite.Indexes;
import fr.sdis83.remocra.db.model.tracabilite.Keys;
import fr.sdis83.remocra.db.model.tracabilite.Tracabilite;

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
public class Hydrant extends TableImpl<Record> {

    private static final long serialVersionUID = -27579835;

    /**
     * The reference instance of <code>tracabilite.hydrant</code>
     */
    public static final Hydrant HYDRANT = new Hydrant();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<Record> getRecordType() {
        return Record.class;
    }

    /**
     * The column <code>tracabilite.hydrant.id</code>.
     */
    public final TableField<Record, Integer> ID = createField("id", org.jooq.impl.SQLDataType.INTEGER.nullable(false).defaultValue(org.jooq.impl.DSL.field("nextval('tracabilite.hydrant_id_seq'::regclass)", org.jooq.impl.SQLDataType.INTEGER)), this, "");

    /**
     * The column <code>tracabilite.hydrant.num_transac</code>.
     */
    public final TableField<Record, Long> NUM_TRANSAC = createField("num_transac", org.jooq.impl.SQLDataType.BIGINT, this, "");

    /**
     * The column <code>tracabilite.hydrant.nom_operation</code>.
     */
    public final TableField<Record, String> NOM_OPERATION = createField("nom_operation", org.jooq.impl.SQLDataType.VARCHAR, this, "");

    /**
     * The column <code>tracabilite.hydrant.date_operation</code>.
     */
    public final TableField<Record, Instant> DATE_OPERATION = createField("date_operation", org.jooq.impl.SQLDataType.TIMESTAMPWITHTIMEZONE, this, "", new OffsetDateTimeToInstantConverter());

    /**
     * The column <code>tracabilite.hydrant.id_hydrant</code>.
     */
    public final TableField<Record, Long> ID_HYDRANT = createField("id_hydrant", org.jooq.impl.SQLDataType.BIGINT, this, "");

    /**
     * The column <code>tracabilite.hydrant.numero</code>.
     */
    public final TableField<Record, String> NUMERO = createField("numero", org.jooq.impl.SQLDataType.VARCHAR, this, "");

    /**
     * @deprecated Unknown data type. Please define an explicit {@link org.jooq.Binding} to specify how this type should be handled. Deprecation can be turned off using {@literal <deprecationOnUnknownTypes/>} in your code generator configuration.
     */
    @java.lang.Deprecated
    public final TableField<Record, Object> GEOMETRIE = createField("geometrie", org.jooq.impl.DefaultDataType.getDefaultDataType("\"public\".\"geometry\""), this, "");

    /**
     * The column <code>tracabilite.hydrant.insee</code>.
     */
    public final TableField<Record, String> INSEE = createField("insee", org.jooq.impl.SQLDataType.VARCHAR, this, "");

    /**
     * The column <code>tracabilite.hydrant.commune</code>.
     */
    public final TableField<Record, String> COMMUNE = createField("commune", org.jooq.impl.SQLDataType.VARCHAR, this, "");

    /**
     * The column <code>tracabilite.hydrant.lieu_dit</code>.
     */
    public final TableField<Record, String> LIEU_DIT = createField("lieu_dit", org.jooq.impl.SQLDataType.VARCHAR, this, "");

    /**
     * The column <code>tracabilite.hydrant.voie</code>.
     */
    public final TableField<Record, String> VOIE = createField("voie", org.jooq.impl.SQLDataType.VARCHAR, this, "");

    /**
     * The column <code>tracabilite.hydrant.carrefour</code>.
     */
    public final TableField<Record, String> CARREFOUR = createField("carrefour", org.jooq.impl.SQLDataType.VARCHAR, this, "");

    /**
     * The column <code>tracabilite.hydrant.complement</code>.
     */
    public final TableField<Record, String> COMPLEMENT = createField("complement", org.jooq.impl.SQLDataType.VARCHAR, this, "");

    /**
     * The column <code>tracabilite.hydrant.agent1</code>.
     */
    public final TableField<Record, String> AGENT1 = createField("agent1", org.jooq.impl.SQLDataType.VARCHAR, this, "");

    /**
     * The column <code>tracabilite.hydrant.agent2</code>.
     */
    public final TableField<Record, String> AGENT2 = createField("agent2", org.jooq.impl.SQLDataType.VARCHAR, this, "");

    /**
     * The column <code>tracabilite.hydrant.date_recep</code>.
     */
    public final TableField<Record, Instant> DATE_RECEP = createField("date_recep", org.jooq.impl.SQLDataType.TIMESTAMP, this, "", new TimestampToInstantConverter());

    /**
     * The column <code>tracabilite.hydrant.date_reco</code>.
     */
    public final TableField<Record, Instant> DATE_RECO = createField("date_reco", org.jooq.impl.SQLDataType.TIMESTAMP, this, "", new TimestampToInstantConverter());

    /**
     * The column <code>tracabilite.hydrant.date_contr</code>.
     */
    public final TableField<Record, Instant> DATE_CONTR = createField("date_contr", org.jooq.impl.SQLDataType.TIMESTAMP, this, "", new TimestampToInstantConverter());

    /**
     * The column <code>tracabilite.hydrant.date_verif</code>.
     */
    public final TableField<Record, Instant> DATE_VERIF = createField("date_verif", org.jooq.impl.SQLDataType.TIMESTAMP, this, "", new TimestampToInstantConverter());

    /**
     * The column <code>tracabilite.hydrant.dispo_terrestre</code>.
     */
    public final TableField<Record, String> DISPO_TERRESTRE = createField("dispo_terrestre", org.jooq.impl.SQLDataType.VARCHAR, this, "");

    /**
     * The column <code>tracabilite.hydrant.dispo_hbe</code>.
     */
    public final TableField<Record, String> DISPO_HBE = createField("dispo_hbe", org.jooq.impl.SQLDataType.VARCHAR, this, "");

    /**
     * The column <code>tracabilite.hydrant.nature</code>.
     */
    public final TableField<Record, String> NATURE = createField("nature", org.jooq.impl.SQLDataType.VARCHAR, this, "");

    /**
     * The column <code>tracabilite.hydrant.type_hydrant</code>.
     */
    public final TableField<Record, String> TYPE_HYDRANT = createField("type_hydrant", org.jooq.impl.SQLDataType.VARCHAR, this, "");

    /**
     * The column <code>tracabilite.hydrant.anomalies</code>.
     */
    public final TableField<Record, String[]> ANOMALIES = createField("anomalies", org.jooq.impl.SQLDataType.VARCHAR.getArrayDataType(), this, "");

    /**
     * The column <code>tracabilite.hydrant.observation</code>.
     */
    public final TableField<Record, String> OBSERVATION = createField("observation", org.jooq.impl.SQLDataType.VARCHAR, this, "");

    /**
     * The column <code>tracabilite.hydrant.auteur_modification</code>.
     */
    public final TableField<Record, String> AUTEUR_MODIFICATION = createField("auteur_modification", org.jooq.impl.SQLDataType.VARCHAR, this, "");

    /**
     * The column <code>tracabilite.hydrant.hbe</code>.
     */
    public final TableField<Record, Boolean> HBE = createField("hbe", org.jooq.impl.SQLDataType.BOOLEAN, this, "");

    /**
     * The column <code>tracabilite.hydrant.positionnement</code>.
     */
    public final TableField<Record, String> POSITIONNEMENT = createField("positionnement", org.jooq.impl.SQLDataType.VARCHAR, this, "");

    /**
     * The column <code>tracabilite.hydrant.materiau</code>.
     */
    public final TableField<Record, String> MATERIAU = createField("materiau", org.jooq.impl.SQLDataType.VARCHAR, this, "");

    /**
     * The column <code>tracabilite.hydrant.vol_constate</code>.
     */
    public final TableField<Record, String> VOL_CONSTATE = createField("vol_constate", org.jooq.impl.SQLDataType.VARCHAR, this, "");

    /**
     * The column <code>tracabilite.hydrant.diametre</code>.
     */
    public final TableField<Record, String> DIAMETRE = createField("diametre", org.jooq.impl.SQLDataType.VARCHAR, this, "");

    /**
     * The column <code>tracabilite.hydrant.debit</code>.
     */
    public final TableField<Record, Integer> DEBIT = createField("debit", org.jooq.impl.SQLDataType.INTEGER, this, "");

    /**
     * The column <code>tracabilite.hydrant.debit_max</code>.
     */
    public final TableField<Record, Integer> DEBIT_MAX = createField("debit_max", org.jooq.impl.SQLDataType.INTEGER, this, "");

    /**
     * The column <code>tracabilite.hydrant.pression</code>.
     */
    public final TableField<Record, Double> PRESSION = createField("pression", org.jooq.impl.SQLDataType.DOUBLE, this, "");

    /**
     * The column <code>tracabilite.hydrant.pression_dyn</code>.
     */
    public final TableField<Record, Double> PRESSION_DYN = createField("pression_dyn", org.jooq.impl.SQLDataType.DOUBLE, this, "");

    /**
     * The column <code>tracabilite.hydrant.marque</code>.
     */
    public final TableField<Record, String> MARQUE = createField("marque", org.jooq.impl.SQLDataType.VARCHAR, this, "");

    /**
     * The column <code>tracabilite.hydrant.modele</code>.
     */
    public final TableField<Record, String> MODELE = createField("modele", org.jooq.impl.SQLDataType.VARCHAR, this, "");

    /**
     * The column <code>tracabilite.hydrant.pression_dyn_deb</code>.
     */
    public final TableField<Record, Double> PRESSION_DYN_DEB = createField("pression_dyn_deb", org.jooq.impl.SQLDataType.DOUBLE, this, "");

    /**
     * The column <code>tracabilite.hydrant.domaine</code>.
     */
    public final TableField<Record, String> DOMAINE = createField("domaine", org.jooq.impl.SQLDataType.VARCHAR, this, "");

    /**
     * The column <code>tracabilite.hydrant.capacite</code>.
     */
    public final TableField<Record, String> CAPACITE = createField("capacite", org.jooq.impl.SQLDataType.VARCHAR, this, "");

    /**
     * The column <code>tracabilite.hydrant.nature_deci</code>.
     */
    public final TableField<Record, String> NATURE_DECI = createField("nature_deci", org.jooq.impl.SQLDataType.VARCHAR, this, "");

    /**
     * The column <code>tracabilite.hydrant.numero_voie</code>.
     */
    public final TableField<Record, Integer> NUMERO_VOIE = createField("numero_voie", org.jooq.impl.SQLDataType.INTEGER, this, "");

    /**
     * The column <code>tracabilite.hydrant.suffixe_voie</code>.
     */
    public final TableField<Record, String> SUFFIXE_VOIE = createField("suffixe_voie", org.jooq.impl.SQLDataType.VARCHAR, this, "");

    /**
     * The column <code>tracabilite.hydrant.niveau</code>.
     */
    public final TableField<Record, String> NIVEAU = createField("niveau", org.jooq.impl.SQLDataType.VARCHAR, this, "");

    /**
     * The column <code>tracabilite.hydrant.gestionnaire</code>.
     */
    public final TableField<Record, String> GESTIONNAIRE = createField("gestionnaire", org.jooq.impl.SQLDataType.VARCHAR, this, "");

    /**
     * The column <code>tracabilite.hydrant.gestionnaire_site</code>.
     */
    public final TableField<Record, String> GESTIONNAIRE_SITE = createField("gestionnaire_site", org.jooq.impl.SQLDataType.VARCHAR, this, "");

    /**
     * The column <code>tracabilite.hydrant.autorite_deci</code>.
     */
    public final TableField<Record, String> AUTORITE_DECI = createField("autorite_deci", org.jooq.impl.SQLDataType.VARCHAR, this, "");

    /**
     * The column <code>tracabilite.hydrant.jumele</code>.
     */
    public final TableField<Record, String> JUMELE = createField("jumele", org.jooq.impl.SQLDataType.VARCHAR, this, "");

    /**
     * The column <code>tracabilite.hydrant.dispositif_inviolabilite</code>.
     */
    public final TableField<Record, Boolean> DISPOSITIF_INVIOLABILITE = createField("dispositif_inviolabilite", org.jooq.impl.SQLDataType.BOOLEAN, this, "");

    /**
     * The column <code>tracabilite.hydrant.reservoir</code>.
     */
    public final TableField<Record, String> RESERVOIR = createField("reservoir", org.jooq.impl.SQLDataType.VARCHAR, this, "");

    /**
     * The column <code>tracabilite.hydrant.service_eaux</code>.
     */
    public final TableField<Record, String> SERVICE_EAUX = createField("service_eaux", org.jooq.impl.SQLDataType.VARCHAR, this, "");

    /**
     * The column <code>tracabilite.hydrant.debit_renforce</code>.
     */
    public final TableField<Record, Boolean> DEBIT_RENFORCE = createField("debit_renforce", org.jooq.impl.SQLDataType.BOOLEAN, this, "");

    /**
     * The column <code>tracabilite.hydrant.type_reseau_canalisation</code>.
     */
    public final TableField<Record, String> TYPE_RESEAU_CANALISATION = createField("type_reseau_canalisation", org.jooq.impl.SQLDataType.VARCHAR, this, "");

    /**
     * The column <code>tracabilite.hydrant.type_reseau_alimentation</code>.
     */
    public final TableField<Record, String> TYPE_RESEAU_ALIMENTATION = createField("type_reseau_alimentation", org.jooq.impl.SQLDataType.VARCHAR, this, "");

    /**
     * The column <code>tracabilite.hydrant.diametre_canalisation</code>.
     */
    public final TableField<Record, Integer> DIAMETRE_CANALISATION = createField("diametre_canalisation", org.jooq.impl.SQLDataType.INTEGER, this, "");

    /**
     * The column <code>tracabilite.hydrant.surpresse</code>.
     */
    public final TableField<Record, Boolean> SURPRESSE = createField("surpresse", org.jooq.impl.SQLDataType.BOOLEAN, this, "");

    /**
     * The column <code>tracabilite.hydrant.additive</code>.
     */
    public final TableField<Record, Boolean> ADDITIVE = createField("additive", org.jooq.impl.SQLDataType.BOOLEAN, this, "");

    /**
     * The column <code>tracabilite.hydrant.illimitee</code>.
     */
    public final TableField<Record, Boolean> ILLIMITEE = createField("illimitee", org.jooq.impl.SQLDataType.BOOLEAN, this, "");

    /**
     * The column <code>tracabilite.hydrant.incertaine</code>.
     */
    public final TableField<Record, Boolean> INCERTAINE = createField("incertaine", org.jooq.impl.SQLDataType.BOOLEAN, this, "");

    /**
     * The column <code>tracabilite.hydrant.en_face</code>.
     */
    public final TableField<Record, Boolean> EN_FACE = createField("en_face", org.jooq.impl.SQLDataType.BOOLEAN, this, "");

    /**
     * The column <code>tracabilite.hydrant.sp_deci</code>. Identifiant service publique deci
     */
    public final TableField<Record, String> SP_DECI = createField("sp_deci", org.jooq.impl.SQLDataType.VARCHAR, this, "Identifiant service publique deci");

    /**
     * The column <code>tracabilite.hydrant.utilisateur_modification</code>.
     */
    public final TableField<Record, Long> UTILISATEUR_MODIFICATION = createField("utilisateur_modification", org.jooq.impl.SQLDataType.BIGINT, this, "");

    /**
     * The column <code>tracabilite.hydrant.organisme</code>.
     */
    public final TableField<Record, Long> ORGANISME = createField("organisme", org.jooq.impl.SQLDataType.BIGINT, this, "");

    /**
     * The column <code>tracabilite.hydrant.auteur_modification_flag</code>.
     */
    public final TableField<Record, String> AUTEUR_MODIFICATION_FLAG = createField("auteur_modification_flag", org.jooq.impl.SQLDataType.VARCHAR, this, "");

    /**
     * Create a <code>tracabilite.hydrant</code> table reference
     */
    public Hydrant() {
        this(DSL.name("hydrant"), null);
    }

    /**
     * Create an aliased <code>tracabilite.hydrant</code> table reference
     */
    public Hydrant(String alias) {
        this(DSL.name(alias), HYDRANT);
    }

    /**
     * Create an aliased <code>tracabilite.hydrant</code> table reference
     */
    public Hydrant(Name alias) {
        this(alias, HYDRANT);
    }

    private Hydrant(Name alias, Table<Record> aliased) {
        this(alias, aliased, null);
    }

    private Hydrant(Name alias, Table<Record> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment(""));
    }

    public <O extends Record> Hydrant(Table<O> child, ForeignKey<O, Record> key) {
        super(child, key, HYDRANT);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Schema getSchema() {
        return Tracabilite.TRACABILITE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Index> getIndexes() {
        return Arrays.<Index>asList(Indexes.HYDRANT_DISPO_HBE_IDX, Indexes.HYDRANT_DISPO_TERRESTRE_IDX, Indexes.HYDRANT_PKEY);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Identity<Record, Integer> getIdentity() {
        return Keys.IDENTITY_HYDRANT;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UniqueKey<Record> getPrimaryKey() {
        return Keys.HYDRANT_PKEY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<UniqueKey<Record>> getKeys() {
        return Arrays.<UniqueKey<Record>>asList(Keys.HYDRANT_PKEY);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ForeignKey<Record, ?>> getReferences() {
        return Arrays.<ForeignKey<Record, ?>>asList(Keys.HYDRANT__FK_UTILISATEUR, Keys.HYDRANT__FK_ORGANISME);
    }

    public Utilisateur utilisateur() {
        return new Utilisateur(this, Keys.HYDRANT__FK_UTILISATEUR);
    }

    public Organisme organisme() {
        return new Organisme(this, Keys.HYDRANT__FK_ORGANISME);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Hydrant as(String alias) {
        return new Hydrant(DSL.name(alias), this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Hydrant as(Name alias) {
        return new Hydrant(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public Hydrant rename(String name) {
        return new Hydrant(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public Hydrant rename(Name name) {
        return new Hydrant(name, null);
    }
}
