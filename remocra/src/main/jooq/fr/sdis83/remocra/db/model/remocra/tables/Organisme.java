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
public class Organisme extends TableImpl<Record> {

	private static final long serialVersionUID = 1849168879;

	/**
	 * The reference instance of <code>remocra.organisme</code>
	 */
	public static final Organisme ORGANISME = new Organisme();

	/**
	 * The class holding records for this type
	 */
	@Override
	public Class<Record> getRecordType() {
		return Record.class;
	}

	/**
	 * The column <code>remocra.organisme.id</code>.
	 */
	public final TableField<Record, Long> ID = createField("id", org.jooq.impl.SQLDataType.BIGINT.nullable(false).defaulted(true), this, "");

	/**
	 * The column <code>remocra.organisme.actif</code>.
	 */
	public final TableField<Record, Boolean> ACTIF = createField("actif", org.jooq.impl.SQLDataType.BOOLEAN.nullable(false).defaulted(true), this, "");

	/**
	 * The column <code>remocra.organisme.code</code>.
	 */
	public final TableField<Record, String> CODE = createField("code", org.jooq.impl.SQLDataType.VARCHAR.nullable(false), this, "");

	/**
	 * The column <code>remocra.organisme.email_contact</code>.
	 */
	public final TableField<Record, String> EMAIL_CONTACT = createField("email_contact", org.jooq.impl.SQLDataType.VARCHAR, this, "");

	/**
	 * The column <code>remocra.organisme.nom</code>.
	 */
	public final TableField<Record, String> NOM = createField("nom", org.jooq.impl.SQLDataType.VARCHAR, this, "");

	/**
	 * The column <code>remocra.organisme.version</code>.
	 */
	public final TableField<Record, Integer> VERSION = createField("version", org.jooq.impl.SQLDataType.INTEGER.defaulted(true), this, "");

	/**
	 * The column <code>remocra.organisme.profil_organisme</code>.
	 */
	public final TableField<Record, Long> PROFIL_ORGANISME = createField("profil_organisme", org.jooq.impl.SQLDataType.BIGINT.nullable(false), this, "");

	/**
	 * The column <code>remocra.organisme.type_organisme</code>.
	 */
	public final TableField<Record, Long> TYPE_ORGANISME = createField("type_organisme", org.jooq.impl.SQLDataType.BIGINT.nullable(false), this, "");

	/**
	 * The column <code>remocra.organisme.zone_competence</code>.
	 */
	public final TableField<Record, Long> ZONE_COMPETENCE = createField("zone_competence", org.jooq.impl.SQLDataType.BIGINT.nullable(false), this, "");

	/**
	 * The column <code>remocra.organisme.organisme_parent</code>.
	 */
	public final TableField<Record, Long> ORGANISME_PARENT = createField("organisme_parent", org.jooq.impl.SQLDataType.BIGINT, this, "");

	/**
	 * The column <code>remocra.organisme.password</code>.
	 */
	public final TableField<Record, String> PASSWORD = createField("password", org.jooq.impl.SQLDataType.VARCHAR, this, "");

	/**
	 * The column <code>remocra.organisme.salt</code>.
	 */
	public final TableField<Record, String> SALT = createField("salt", org.jooq.impl.SQLDataType.VARCHAR, this, "");

	/**
	 * Create a <code>remocra.organisme</code> table reference
	 */
	public Organisme() {
		this("organisme", null);
	}

	/**
	 * Create an aliased <code>remocra.organisme</code> table reference
	 */
	public Organisme(String alias) {
		this(alias, ORGANISME);
	}

	private Organisme(String alias, Table<Record> aliased) {
		this(alias, aliased, null);
	}

	private Organisme(String alias, Table<Record> aliased, Field<?>[] parameters) {
		super(alias, Remocra.REMOCRA, aliased, parameters, "");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Identity<Record, Long> getIdentity() {
		return Keys.IDENTITY_ORGANISME;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public UniqueKey<Record> getPrimaryKey() {
		return Keys.ORGANISME_PKEY;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<UniqueKey<Record>> getKeys() {
		return Arrays.<UniqueKey<Record>>asList(Keys.ORGANISME_PKEY, Keys.ORGANISME_CODE_KEY);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<ForeignKey<Record, ?>> getReferences() {
		return Arrays.<ForeignKey<Record, ?>>asList(Keys.ORGANISME__FK805999D374EBAF33, Keys.ORGANISME__FK805999D3F5378273, Keys.ORGANISME__FK805999D39B5C78A5, Keys.ORGANISME__FK_ORGANISME_PARENT);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Organisme as(String alias) {
		return new Organisme(alias, this);
	}

	/**
	 * Rename this table
	 */
	public Organisme rename(String name) {
		return new Organisme(name, null);
	}
}
