/**
 * This class is generated by jOOQ
 */
package fr.sdis83.remocra.db.model.tracabilite.tables;


import fr.sdis83.remocra.db.converter.InstantConverter;
import fr.sdis83.remocra.db.model.tracabilite.Keys;
import fr.sdis83.remocra.db.model.tracabilite.Tracabilite;

import javax.annotation.Generated;

import org.joda.time.Instant;
import org.jooq.Field;
import org.jooq.Identity;
import org.jooq.Record;
import org.jooq.Table;
import org.jooq.TableField;
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
public class HydrantVisite extends TableImpl<Record> {

	private static final long serialVersionUID = -15932137;

	/**
	 * The reference instance of <code>tracabilite.hydrant_visite</code>
	 */
	public static final HydrantVisite HYDRANT_VISITE = new HydrantVisite();

	/**
	 * The class holding records for this type
	 */
	@Override
	public Class<Record> getRecordType() {
		return Record.class;
	}

	/**
	 * The column <code>tracabilite.hydrant_visite.id</code>.
	 */
	public final TableField<Record, Long> ID = createField("id", org.jooq.impl.SQLDataType.BIGINT.nullable(false).defaulted(true), this, "");

	/**
	 * The column <code>tracabilite.hydrant_visite.num_transac</code>.
	 */
	public final TableField<Record, Long> NUM_TRANSAC = createField("num_transac", org.jooq.impl.SQLDataType.BIGINT, this, "");

	/**
	 * The column <code>tracabilite.hydrant_visite.nom_operation</code>.
	 */
	public final TableField<Record, String> NOM_OPERATION = createField("nom_operation", org.jooq.impl.SQLDataType.VARCHAR, this, "");

	/**
	 * The column <code>tracabilite.hydrant_visite.date_operation</code>.
	 */
	public final TableField<Record, Instant> DATE_OPERATION = createField("date_operation", org.jooq.impl.SQLDataType.TIMESTAMP, this, "", new InstantConverter());

	/**
	 * The column <code>tracabilite.hydrant_visite.id_visite</code>.
	 */
	public final TableField<Record, Long> ID_VISITE = createField("id_visite", org.jooq.impl.SQLDataType.BIGINT.nullable(false), this, "");

	/**
	 * The column <code>tracabilite.hydrant_visite.hydrant</code>.
	 */
	public final TableField<Record, Long> HYDRANT = createField("hydrant", org.jooq.impl.SQLDataType.BIGINT.nullable(false), this, "");

	/**
	 * The column <code>tracabilite.hydrant_visite.date</code>.
	 */
	public final TableField<Record, Instant> DATE = createField("date", org.jooq.impl.SQLDataType.TIMESTAMP.defaulted(true), this, "", new InstantConverter());

	/**
	 * The column <code>tracabilite.hydrant_visite.type</code>.
	 */
	public final TableField<Record, Long> TYPE = createField("type", org.jooq.impl.SQLDataType.BIGINT, this, "");

	/**
	 * The column <code>tracabilite.hydrant_visite.ctrl_debit_pression</code>.
	 */
	public final TableField<Record, Boolean> CTRL_DEBIT_PRESSION = createField("ctrl_debit_pression", org.jooq.impl.SQLDataType.BOOLEAN, this, "");

	/**
	 * The column <code>tracabilite.hydrant_visite.agent1</code>.
	 */
	public final TableField<Record, String> AGENT1 = createField("agent1", org.jooq.impl.SQLDataType.VARCHAR, this, "");

	/**
	 * The column <code>tracabilite.hydrant_visite.agent2</code>.
	 */
	public final TableField<Record, String> AGENT2 = createField("agent2", org.jooq.impl.SQLDataType.VARCHAR, this, "");

	/**
	 * The column <code>tracabilite.hydrant_visite.debit</code>.
	 */
	public final TableField<Record, Integer> DEBIT = createField("debit", org.jooq.impl.SQLDataType.INTEGER, this, "");

	/**
	 * The column <code>tracabilite.hydrant_visite.debit_max</code>.
	 */
	public final TableField<Record, Integer> DEBIT_MAX = createField("debit_max", org.jooq.impl.SQLDataType.INTEGER, this, "");

	/**
	 * The column <code>tracabilite.hydrant_visite.pression</code>.
	 */
	public final TableField<Record, Double> PRESSION = createField("pression", org.jooq.impl.SQLDataType.DOUBLE, this, "");

	/**
	 * The column <code>tracabilite.hydrant_visite.pression_dyn</code>.
	 */
	public final TableField<Record, Double> PRESSION_DYN = createField("pression_dyn", org.jooq.impl.SQLDataType.DOUBLE, this, "");

	/**
	 * The column <code>tracabilite.hydrant_visite.pression_dyn_deb</code>.
	 */
	public final TableField<Record, Double> PRESSION_DYN_DEB = createField("pression_dyn_deb", org.jooq.impl.SQLDataType.DOUBLE, this, "");

	/**
	 * The column <code>tracabilite.hydrant_visite.anomalies</code>.
	 */
	public final TableField<Record, String> ANOMALIES = createField("anomalies", org.jooq.impl.SQLDataType.VARCHAR, this, "");

	/**
	 * The column <code>tracabilite.hydrant_visite.observations</code>.
	 */
	public final TableField<Record, String> OBSERVATIONS = createField("observations", org.jooq.impl.SQLDataType.VARCHAR, this, "");

	/**
	 * The column <code>tracabilite.hydrant_visite.auteur_modification</code>.
	 */
	public final TableField<Record, String> AUTEUR_MODIFICATION = createField("auteur_modification", org.jooq.impl.SQLDataType.VARCHAR, this, "");

	/**
	 * The column <code>tracabilite.hydrant_visite.utilisateur_modification</code>.
	 */
	public final TableField<Record, Long> UTILISATEUR_MODIFICATION = createField("utilisateur_modification", org.jooq.impl.SQLDataType.BIGINT, this, "");

	/**
	 * The column <code>tracabilite.hydrant_visite.organisme</code>.
	 */
	public final TableField<Record, Long> ORGANISME = createField("organisme", org.jooq.impl.SQLDataType.BIGINT, this, "");

	/**
	 * The column <code>tracabilite.hydrant_visite.auteur_modification_flag</code>.
	 */
	public final TableField<Record, String> AUTEUR_MODIFICATION_FLAG = createField("auteur_modification_flag", org.jooq.impl.SQLDataType.VARCHAR, this, "");

	/**
	 * Create a <code>tracabilite.hydrant_visite</code> table reference
	 */
	public HydrantVisite() {
		this("hydrant_visite", null);
	}

	/**
	 * Create an aliased <code>tracabilite.hydrant_visite</code> table reference
	 */
	public HydrantVisite(String alias) {
		this(alias, HYDRANT_VISITE);
	}

	private HydrantVisite(String alias, Table<Record> aliased) {
		this(alias, aliased, null);
	}

	private HydrantVisite(String alias, Table<Record> aliased, Field<?>[] parameters) {
		super(alias, Tracabilite.TRACABILITE, aliased, parameters, "");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Identity<Record, Long> getIdentity() {
		return Keys.IDENTITY_HYDRANT_VISITE;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public HydrantVisite as(String alias) {
		return new HydrantVisite(alias, this);
	}

	/**
	 * Rename this table
	 */
	public HydrantVisite rename(String name) {
		return new HydrantVisite(name, null);
	}
}
