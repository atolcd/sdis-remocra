/**
 * This class is generated by jOOQ
 */
package fr.sdis83.remocra.db.model.remocra.tables;


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
public class Tournee extends TableImpl<Record> {

	private static final long serialVersionUID = 1865946723;

	/**
	 * The reference instance of <code>remocra.tournee</code>
	 */
	public static final Tournee TOURNEE = new Tournee();

	/**
	 * The class holding records for this type
	 */
	@Override
	public Class<Record> getRecordType() {
		return Record.class;
	}

	/**
	 * The column <code>remocra.tournee.id</code>.
	 */
	public final TableField<Record, Long> ID = createField("id", org.jooq.impl.SQLDataType.BIGINT.nullable(false).defaulted(true), this, "");

	/**
	 * The column <code>remocra.tournee.deb_sync</code>.
	 */
	public final TableField<Record, Instant> DEB_SYNC = createField("deb_sync", org.jooq.impl.SQLDataType.TIMESTAMP, this, "", new InstantConverter());

	/**
	 * The column <code>remocra.tournee.last_sync</code>.
	 */
	public final TableField<Record, Instant> LAST_SYNC = createField("last_sync", org.jooq.impl.SQLDataType.TIMESTAMP, this, "", new InstantConverter());

	/**
	 * The column <code>remocra.tournee.version</code>.
	 */
	public final TableField<Record, Integer> VERSION = createField("version", org.jooq.impl.SQLDataType.INTEGER.defaulted(true), this, "");

	/**
	 * The column <code>remocra.tournee.affectation</code>.
	 */
	public final TableField<Record, Long> AFFECTATION = createField("affectation", org.jooq.impl.SQLDataType.BIGINT.nullable(false), this, "");

	/**
	 * The column <code>remocra.tournee.reservation</code>.
	 */
	public final TableField<Record, Long> RESERVATION = createField("reservation", org.jooq.impl.SQLDataType.BIGINT, this, "");

	/**
	 * The column <code>remocra.tournee.etat</code>.
	 */
	public final TableField<Record, Integer> ETAT = createField("etat", org.jooq.impl.SQLDataType.INTEGER, this, "");

	/**
	 * The column <code>remocra.tournee.hydrant_count</code>.
	 */
	public final TableField<Record, Integer> HYDRANT_COUNT = createField("hydrant_count", org.jooq.impl.SQLDataType.INTEGER, this, "");

	/**
	 * The column <code>remocra.tournee.nom</code>.
	 */
	public final TableField<Record, String> NOM = createField("nom", org.jooq.impl.SQLDataType.VARCHAR.nullable(false), this, "");

	/**
	 * Create a <code>remocra.tournee</code> table reference
	 */
	public Tournee() {
		this("tournee", null);
	}

	/**
	 * Create an aliased <code>remocra.tournee</code> table reference
	 */
	public Tournee(String alias) {
		this(alias, TOURNEE);
	}

	private Tournee(String alias, Table<Record> aliased) {
		this(alias, aliased, null);
	}

	private Tournee(String alias, Table<Record> aliased, Field<?>[] parameters) {
		super(alias, Remocra.REMOCRA, aliased, parameters, "");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Identity<Record, Long> getIdentity() {
		return Keys.IDENTITY_TOURNEE;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public UniqueKey<Record> getPrimaryKey() {
		return Keys.TOURNEE_PKEY;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<UniqueKey<Record>> getKeys() {
		return Arrays.<UniqueKey<Record>>asList(Keys.TOURNEE_PKEY, Keys.NOM_AFFECTATION);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<ForeignKey<Record, ?>> getReferences() {
		return Arrays.<ForeignKey<Record, ?>>asList(Keys.TOURNEE__FKBC630036DBF82B2F, Keys.TOURNEE__FKBC6300366F3F65FB);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Tournee as(String alias) {
		return new Tournee(alias, this);
	}

	/**
	 * Rename this table
	 */
	public Tournee rename(String name) {
		return new Tournee(name, null);
	}
}
