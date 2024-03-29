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
public class SousTypeAlerteElt extends TableImpl<Record> {

	private static final long serialVersionUID = -2045347503;

	/**
	 * The reference instance of <code>remocra.sous_type_alerte_elt</code>
	 */
	public static final SousTypeAlerteElt SOUS_TYPE_ALERTE_ELT = new SousTypeAlerteElt();

	/**
	 * The class holding records for this type
	 */
	@Override
	public Class<Record> getRecordType() {
		return Record.class;
	}

	/**
	 * The column <code>remocra.sous_type_alerte_elt.id</code>.
	 */
	public final TableField<Record, Long> ID = createField("id", org.jooq.impl.SQLDataType.BIGINT.nullable(false).defaulted(true), this, "");

	/**
	 * The column <code>remocra.sous_type_alerte_elt.actif</code>.
	 */
	public final TableField<Record, Boolean> ACTIF = createField("actif", org.jooq.impl.SQLDataType.BOOLEAN.nullable(false).defaulted(true), this, "");

	/**
	 * The column <code>remocra.sous_type_alerte_elt.code</code>.
	 */
	public final TableField<Record, String> CODE = createField("code", org.jooq.impl.SQLDataType.VARCHAR.nullable(false), this, "");

	/**
	 * The column <code>remocra.sous_type_alerte_elt.nom</code>.
	 */
	public final TableField<Record, String> NOM = createField("nom", org.jooq.impl.SQLDataType.VARCHAR.nullable(false), this, "");

	/**
	 * The column <code>remocra.sous_type_alerte_elt.type_geom</code>.
	 */
	public final TableField<Record, Integer> TYPE_GEOM = createField("type_geom", org.jooq.impl.SQLDataType.INTEGER.nullable(false), this, "");

	/**
	 * The column <code>remocra.sous_type_alerte_elt.type_alerte_elt</code>.
	 */
	public final TableField<Record, Long> TYPE_ALERTE_ELT = createField("type_alerte_elt", org.jooq.impl.SQLDataType.BIGINT.nullable(false), this, "");

	/**
	 * Create a <code>remocra.sous_type_alerte_elt</code> table reference
	 */
	public SousTypeAlerteElt() {
		this("sous_type_alerte_elt", null);
	}

	/**
	 * Create an aliased <code>remocra.sous_type_alerte_elt</code> table reference
	 */
	public SousTypeAlerteElt(String alias) {
		this(alias, SOUS_TYPE_ALERTE_ELT);
	}

	private SousTypeAlerteElt(String alias, Table<Record> aliased) {
		this(alias, aliased, null);
	}

	private SousTypeAlerteElt(String alias, Table<Record> aliased, Field<?>[] parameters) {
		super(alias, Remocra.REMOCRA, aliased, parameters, "");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Identity<Record, Long> getIdentity() {
		return Keys.IDENTITY_SOUS_TYPE_ALERTE_ELT;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public UniqueKey<Record> getPrimaryKey() {
		return Keys.SOUS_TYPE_ALERTE_ELT_PKEY;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<UniqueKey<Record>> getKeys() {
		return Arrays.<UniqueKey<Record>>asList(Keys.SOUS_TYPE_ALERTE_ELT_PKEY);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<ForeignKey<Record, ?>> getReferences() {
		return Arrays.<ForeignKey<Record, ?>>asList(Keys.SOUS_TYPE_ALERTE_ELT__FK91F5E3D755054712);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public SousTypeAlerteElt as(String alias) {
		return new SousTypeAlerteElt(alias, this);
	}

	/**
	 * Rename this table
	 */
	public SousTypeAlerteElt rename(String name) {
		return new SousTypeAlerteElt(name, null);
	}
}
