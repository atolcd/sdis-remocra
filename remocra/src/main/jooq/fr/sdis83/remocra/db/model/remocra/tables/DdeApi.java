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
public class DdeApi extends TableImpl<Record> {

	private static final long serialVersionUID = 1903580857;

	/**
	 * The reference instance of <code>remocra.dde_api</code>
	 */
	public static final DdeApi DDE_API = new DdeApi();

	/**
	 * The class holding records for this type
	 */
	@Override
	public Class<Record> getRecordType() {
		return Record.class;
	}

	/**
	 * The column <code>remocra.dde_api.id</code>.
	 */
	public final TableField<Record, Integer> ID = createField("id", org.jooq.impl.SQLDataType.INTEGER.nullable(false).defaulted(true), this, "");

	/**
	 * The column <code>remocra.dde_api.organisme</code>.
	 */
	public final TableField<Record, Long> ORGANISME = createField("organisme", org.jooq.impl.SQLDataType.BIGINT, this, "");

	/**
	 * The column <code>remocra.dde_api.code</code>.
	 */
	public final TableField<Record, String> CODE = createField("code", org.jooq.impl.SQLDataType.VARCHAR, this, "");

	/**
	 * The column <code>remocra.dde_api.date_demande</code>.
	 */
	public final TableField<Record, Instant> DATE_DEMANDE = createField("date_demande", org.jooq.impl.SQLDataType.TIMESTAMP, this, "", new InstantConverter());

	/**
	 * The column <code>remocra.dde_api.utilise</code>.
	 */
	public final TableField<Record, Boolean> UTILISE = createField("utilise", org.jooq.impl.SQLDataType.BOOLEAN.defaulted(true), this, "");

	/**
	 * Create a <code>remocra.dde_api</code> table reference
	 */
	public DdeApi() {
		this("dde_api", null);
	}

	/**
	 * Create an aliased <code>remocra.dde_api</code> table reference
	 */
	public DdeApi(String alias) {
		this(alias, DDE_API);
	}

	private DdeApi(String alias, Table<Record> aliased) {
		this(alias, aliased, null);
	}

	private DdeApi(String alias, Table<Record> aliased, Field<?>[] parameters) {
		super(alias, Remocra.REMOCRA, aliased, parameters, "");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Identity<Record, Integer> getIdentity() {
		return Keys.IDENTITY_DDE_API;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public UniqueKey<Record> getPrimaryKey() {
		return Keys.DDE_API_PKEY;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<UniqueKey<Record>> getKeys() {
		return Arrays.<UniqueKey<Record>>asList(Keys.DDE_API_PKEY);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<ForeignKey<Record, ?>> getReferences() {
		return Arrays.<ForeignKey<Record, ?>>asList(Keys.DDE_API__FK_ORGANISME_DDE_API);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public DdeApi as(String alias) {
		return new DdeApi(alias, this);
	}

	/**
	 * Rename this table
	 */
	public DdeApi rename(String name) {
		return new DdeApi(name, null);
	}
}
