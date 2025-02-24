/**
 * This class is generated by jOOQ
 */
package fr.sdis83.remocra.db.model.remocra.tables;


import fr.sdis83.remocra.db.model.remocra.Keys;
import fr.sdis83.remocra.db.model.remocra.Remocra;

import java.util.Arrays;
import java.util.List;

import javax.annotation.Generated;

import org.jooq.Field;
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
public class Thematique extends TableImpl<Record> {

	private static final long serialVersionUID = -1181960592;

	/**
	 * The reference instance of <code>remocra.thematique</code>
	 */
	public static final Thematique THEMATIQUE = new Thematique();

	/**
	 * The class holding records for this type
	 */
	@Override
	public Class<Record> getRecordType() {
		return Record.class;
	}

	/**
	 * The column <code>remocra.thematique.id</code>.
	 */
	public final TableField<Record, Long> ID = createField("id", org.jooq.impl.SQLDataType.BIGINT.nullable(false).defaulted(true), this, "");

	/**
	 * The column <code>remocra.thematique.nom</code>.
	 */
	public final TableField<Record, String> NOM = createField("nom", org.jooq.impl.SQLDataType.VARCHAR, this, "");

	/**
	 * The column <code>remocra.thematique.version</code>.
	 */
	public final TableField<Record, Integer> VERSION = createField("version", org.jooq.impl.SQLDataType.INTEGER, this, "");

	/**
	 * The column <code>remocra.thematique.actif</code>.
	 */
	public final TableField<Record, Boolean> ACTIF = createField("actif", org.jooq.impl.SQLDataType.BOOLEAN.nullable(false).defaulted(true), this, "");

	/**
	 * The column <code>remocra.thematique.code</code>.
	 */
	public final TableField<Record, String> CODE = createField("code", org.jooq.impl.SQLDataType.VARCHAR.nullable(false), this, "");

	/**
	 * Create a <code>remocra.thematique</code> table reference
	 */
	public Thematique() {
		this("thematique", null);
	}

	/**
	 * Create an aliased <code>remocra.thematique</code> table reference
	 */
	public Thematique(String alias) {
		this(alias, THEMATIQUE);
	}

	private Thematique(String alias, Table<Record> aliased) {
		this(alias, aliased, null);
	}

	private Thematique(String alias, Table<Record> aliased, Field<?>[] parameters) {
		super(alias, Remocra.REMOCRA, aliased, parameters, "");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Identity<Record, Long> getIdentity() {
		return Keys.IDENTITY_THEMATIQUE;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public UniqueKey<Record> getPrimaryKey() {
		return Keys.THEMATIQUE_PKEY;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<UniqueKey<Record>> getKeys() {
		return Arrays.<UniqueKey<Record>>asList(Keys.THEMATIQUE_PKEY, Keys.THEMATIQUE_NOM_KEY, Keys.THEMATIQUE_CODE_KEY);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Thematique as(String alias) {
		return new Thematique(alias, this);
	}

	/**
	 * Rename this table
	 */
	public Thematique rename(String name) {
		return new Thematique(name, null);
	}
}
