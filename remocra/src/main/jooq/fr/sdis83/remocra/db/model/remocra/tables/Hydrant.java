/**
 * This class is generated by jOOQ
 */
package fr.sdis83.remocra.db.model.remocra.tables;


import com.vividsolutions.jts.geom.Geometry;

import fr.sdis83.remocra.db.converter.GeometryBinding;
import fr.sdis83.remocra.db.converter.InstantConverter;
import fr.sdis83.remocra.db.model.remocra.Keys;
import fr.sdis83.remocra.db.model.remocra.Remocra;

import java.util.Arrays;
import java.util.List;

import javax.annotation.Generated;

import org.joda.time.Instant;
import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.Identity;
import org.jooq.Record;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.UniqueKey;
import org.jooq.impl.TableImpl;


/**
 * This class is generated by jOOQ.
 */
@Generated(
	value = {
		"http://www.jooq.org",
		"jOOQ version:3.6.0"
	},
	comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Hydrant extends TableImpl<Record> {

	private static final long serialVersionUID = -1416424292;

	/**
	 * The reference instance of <code>remocra.hydrant</code>
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
	 * The column <code>remocra.hydrant.id</code>.
	 */
	public final TableField<Record, Long> ID = createField("id", org.jooq.impl.SQLDataType.BIGINT.nullable(false).defaulted(true), this, "");

	/**
	 * The column <code>remocra.hydrant.agent1</code>.
	 */
	public final TableField<Record, String> AGENT1 = createField("agent1", org.jooq.impl.SQLDataType.VARCHAR, this, "");

	/**
	 * The column <code>remocra.hydrant.agent2</code>.
	 */
	public final TableField<Record, String> AGENT2 = createField("agent2", org.jooq.impl.SQLDataType.VARCHAR, this, "");

	/**
	 * The column <code>remocra.hydrant.annee_fabrication</code>.
	 */
	public final TableField<Record, Integer> ANNEE_FABRICATION = createField("annee_fabrication", org.jooq.impl.SQLDataType.INTEGER, this, "");

	/**
	 * The column <code>remocra.hydrant.code</code>.
	 */
	public final TableField<Record, String> CODE = createField("code", org.jooq.impl.SQLDataType.VARCHAR, this, "");

	/**
	 * The column <code>remocra.hydrant.complement</code>.
	 */
	public final TableField<Record, String> COMPLEMENT = createField("complement", org.jooq.impl.SQLDataType.VARCHAR, this, "");

	/**
	 * The column <code>remocra.hydrant.date_contr</code>.
	 */
	public final TableField<Record, Instant> DATE_CONTR = createField("date_contr", org.jooq.impl.SQLDataType.TIMESTAMP, this, "", new InstantConverter());

	/**
	 * The column <code>remocra.hydrant.date_recep</code>.
	 */
	public final TableField<Record, Instant> DATE_RECEP = createField("date_recep", org.jooq.impl.SQLDataType.TIMESTAMP, this, "", new InstantConverter());

	/**
	 * The column <code>remocra.hydrant.date_reco</code>.
	 */
	public final TableField<Record, Instant> DATE_RECO = createField("date_reco", org.jooq.impl.SQLDataType.TIMESTAMP, this, "", new InstantConverter());

	/**
	 * The column <code>remocra.hydrant.date_verif</code>.
	 */
	public final TableField<Record, Instant> DATE_VERIF = createField("date_verif", org.jooq.impl.SQLDataType.TIMESTAMP, this, "", new InstantConverter());

	/**
	 * The column <code>remocra.hydrant.dispo_admin</code>.
	 */
	public final TableField<Record, String> DISPO_ADMIN = createField("dispo_admin", org.jooq.impl.SQLDataType.VARCHAR, this, "");

	/**
	 * The column <code>remocra.hydrant.dispo_hbe</code>.
	 */
	public final TableField<Record, String> DISPO_HBE = createField("dispo_hbe", org.jooq.impl.SQLDataType.VARCHAR, this, "");

	/**
	 * The column <code>remocra.hydrant.dispo_terrestre</code>.
	 */
	public final TableField<Record, String> DISPO_TERRESTRE = createField("dispo_terrestre", org.jooq.impl.SQLDataType.VARCHAR, this, "");

	/**
	 * The column <code>remocra.hydrant.geometrie</code>.
	 */
	public final TableField<Record, Geometry> GEOMETRIE = createField("geometrie", org.jooq.impl.DefaultDataType.getDefaultDataType("USER-DEFINED"), this, "", new GeometryBinding());

	/**
	 * The column <code>remocra.hydrant.lieu_dit</code>.
	 */
	public final TableField<Record, String> LIEU_DIT = createField("lieu_dit", org.jooq.impl.SQLDataType.VARCHAR, this, "");

	/**
	 * The column <code>remocra.hydrant.numero</code>.
	 */
	public final TableField<Record, String> NUMERO = createField("numero", org.jooq.impl.SQLDataType.VARCHAR, this, "");

	/**
	 * The column <code>remocra.hydrant.numero_interne</code>.
	 */
	public final TableField<Record, Integer> NUMERO_INTERNE = createField("numero_interne", org.jooq.impl.SQLDataType.INTEGER, this, "");

	/**
	 * The column <code>remocra.hydrant.observation</code>.
	 */
	public final TableField<Record, String> OBSERVATION = createField("observation", org.jooq.impl.SQLDataType.VARCHAR, this, "");

	/**
	 * The column <code>remocra.hydrant.version</code>.
	 */
	public final TableField<Record, Integer> VERSION = createField("version", org.jooq.impl.SQLDataType.INTEGER.defaulted(true), this, "");

	/**
	 * The column <code>remocra.hydrant.voie</code>.
	 */
	public final TableField<Record, String> VOIE = createField("voie", org.jooq.impl.SQLDataType.VARCHAR, this, "");

	/**
	 * The column <code>remocra.hydrant.voie2</code>.
	 */
	public final TableField<Record, String> VOIE2 = createField("voie2", org.jooq.impl.SQLDataType.VARCHAR, this, "");

	/**
	 * The column <code>remocra.hydrant.commune</code>.
	 */
	public final TableField<Record, Long> COMMUNE = createField("commune", org.jooq.impl.SQLDataType.BIGINT, this, "");

	/**
	 * The column <code>remocra.hydrant.domaine</code>.
	 */
	public final TableField<Record, Long> DOMAINE = createField("domaine", org.jooq.impl.SQLDataType.BIGINT, this, "");

	/**
	 * The column <code>remocra.hydrant.nature</code>.
	 */
	public final TableField<Record, Long> NATURE = createField("nature", org.jooq.impl.SQLDataType.BIGINT, this, "");

	/**
	 * The column <code>remocra.hydrant.date_modification</code>.
	 */
	public final TableField<Record, Instant> DATE_MODIFICATION = createField("date_modification", org.jooq.impl.SQLDataType.TIMESTAMP, this, "", new InstantConverter());

	/**
	 * The column <code>remocra.hydrant.courrier</code>.
	 */
	public final TableField<Record, String> COURRIER = createField("courrier", org.jooq.impl.SQLDataType.VARCHAR, this, "");

	/**
	 * The column <code>remocra.hydrant.gest_point_eau</code>.
	 */
	public final TableField<Record, String> GEST_POINT_EAU = createField("gest_point_eau", org.jooq.impl.SQLDataType.VARCHAR, this, "");

	/**
	 * The column <code>remocra.hydrant.organisme</code>.
	 */
	public final TableField<Record, Long> ORGANISME = createField("organisme", org.jooq.impl.SQLDataType.BIGINT, this, "");

	/**
	 * The column <code>remocra.hydrant.date_gps</code>.
	 */
	public final TableField<Record, Instant> DATE_GPS = createField("date_gps", org.jooq.impl.SQLDataType.TIMESTAMP, this, "", new InstantConverter());

	/**
	 * The column <code>remocra.hydrant.zone_speciale</code>.
	 */
	public final TableField<Record, Long> ZONE_SPECIALE = createField("zone_speciale", org.jooq.impl.SQLDataType.BIGINT, this, "");

	/**
	 * The column <code>remocra.hydrant.date_attestation</code>.
	 */
	public final TableField<Record, Instant> DATE_ATTESTATION = createField("date_attestation", org.jooq.impl.SQLDataType.TIMESTAMP, this, "", new InstantConverter());

	/**
	 * The column <code>remocra.hydrant.utilisateur_modification</code>.
	 */
	public final TableField<Record, Long> UTILISATEUR_MODIFICATION = createField("utilisateur_modification", org.jooq.impl.SQLDataType.BIGINT, this, "");

	/**
	 * The column <code>remocra.hydrant.nature_deci</code>.
	 */
	public final TableField<Record, Long> NATURE_DECI = createField("nature_deci", org.jooq.impl.SQLDataType.BIGINT.defaulted(true), this, "");

	/**
	 * The column <code>remocra.hydrant.numero_voie</code>. Numéro de voie (adresse)
	 */
	public final TableField<Record, Integer> NUMERO_VOIE = createField("numero_voie", org.jooq.impl.SQLDataType.INTEGER, this, "Numéro de voie (adresse)");

	/**
	 * The column <code>remocra.hydrant.suffixe_voie</code>. Suffixe de voie de l'adresse (bis, ter, etc)
	 */
	public final TableField<Record, String> SUFFIXE_VOIE = createField("suffixe_voie", org.jooq.impl.SQLDataType.VARCHAR, this, "Suffixe de voie de l'adresse (bis, ter, etc)");

	/**
	 * The column <code>remocra.hydrant.niveau</code>. Identifiant du type de niveau de l'hydrant
	 */
	public final TableField<Record, Long> NIVEAU = createField("niveau", org.jooq.impl.SQLDataType.BIGINT, this, "Identifiant du type de niveau de l'hydrant");

	/**
	 * The column <code>remocra.hydrant.gestionnaire</code>. Identifiant du gestionnaire de l'hydrant
	 */
	public final TableField<Record, Long> GESTIONNAIRE = createField("gestionnaire", org.jooq.impl.SQLDataType.BIGINT, this, "Identifiant du gestionnaire de l'hydrant");

	/**
	 * The column <code>remocra.hydrant.gestionnaire_site</code>. Identifiant du site auquel appartient l'hydrant
	 */
	public final TableField<Record, Long> GESTIONNAIRE_SITE = createField("gestionnaire_site", org.jooq.impl.SQLDataType.BIGINT, this, "Identifiant du site auquel appartient l'hydrant");

	/**
	 * The column <code>remocra.hydrant.autorite_deci</code>. Identifiant de l'autorité de police DECI
	 */
	public final TableField<Record, Long> AUTORITE_DECI = createField("autorite_deci", org.jooq.impl.SQLDataType.BIGINT, this, "Identifiant de l'autorité de police DECI");

	/**
	 * The column <code>remocra.hydrant.en_face</code>.
	 */
	public final TableField<Record, Boolean> EN_FACE = createField("en_face", org.jooq.impl.SQLDataType.BOOLEAN.defaulted(true), this, "");

	/**
	 * The column <code>remocra.hydrant.date_crea</code>.
	 */
	public final TableField<Record, Instant> DATE_CREA = createField("date_crea", org.jooq.impl.SQLDataType.TIMESTAMP, this, "", new InstantConverter());

	/**
	 * The column <code>remocra.hydrant.sp_deci</code>. Identifiant du service publique DECI
	 */
	public final TableField<Record, Long> SP_DECI = createField("sp_deci", org.jooq.impl.SQLDataType.BIGINT, this, "Identifiant du service publique DECI");

	/**
	 * The column <code>remocra.hydrant.maintenance_deci</code>.
	 */
	public final TableField<Record, Long> MAINTENANCE_DECI = createField("maintenance_deci", org.jooq.impl.SQLDataType.BIGINT, this, "");

	/**
	 * The column <code>remocra.hydrant.auteur_modification_flag</code>.
	 */
	public final TableField<Record, String> AUTEUR_MODIFICATION_FLAG = createField("auteur_modification_flag", org.jooq.impl.SQLDataType.VARCHAR, this, "");

	/**
	 * The column <code>remocra.hydrant.date_changement_dispo_terrestre</code>. Date du dernier changement d'état
	 */
	public final TableField<Record, Instant> DATE_CHANGEMENT_DISPO_TERRESTRE = createField("date_changement_dispo_terrestre", org.jooq.impl.SQLDataType.TIMESTAMP, this, "Date du dernier changement d'état", new InstantConverter());

	/**
	 * Create a <code>remocra.hydrant</code> table reference
	 */
	public Hydrant() {
		this("hydrant", null);
	}

	/**
	 * Create an aliased <code>remocra.hydrant</code> table reference
	 */
	public Hydrant(String alias) {
		this(alias, HYDRANT);
	}

	private Hydrant(String alias, Table<Record> aliased) {
		this(alias, aliased, null);
	}

	private Hydrant(String alias, Table<Record> aliased, Field<?>[] parameters) {
		super(alias, Remocra.REMOCRA, aliased, parameters, "");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Identity<Record, Long> getIdentity() {
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
		return Arrays.<UniqueKey<Record>>asList(Keys.HYDRANT_PKEY, Keys.HYDRANT_NUMERO_KEY);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<ForeignKey<Record, ?>> getReferences() {
		return Arrays.<ForeignKey<Record, ?>>asList(Keys.HYDRANT__FK51B8F028D2DA796C, Keys.HYDRANT__FK51B8F0285D29D8A8, Keys.HYDRANT__FK51B8F028D10A0428, Keys.HYDRANT__FK51B8F028374ADD52, Keys.HYDRANT__FK_ZONE_SPECIALE, Keys.HYDRANT__FK_HYDRANT_UTILISATEUR_MODIFICATION, Keys.HYDRANT__FK_NATURE_DECI, Keys.HYDRANT__FK_NIVEAU, Keys.HYDRANT__FK_GESTIONNAIRE, Keys.HYDRANT__FK_GESTIONNAIRE_SITE, Keys.HYDRANT__FK_AUTORITE_DECI, Keys.HYDRANT__FK_SP_DECI, Keys.HYDRANT__FK_MAINTENANCE_DECI_ORGANISME);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Hydrant as(String alias) {
		return new Hydrant(alias, this);
	}

	/**
	 * Rename this table
	 */
	public Hydrant rename(String name) {
		return new Hydrant(name, null);
	}
}
