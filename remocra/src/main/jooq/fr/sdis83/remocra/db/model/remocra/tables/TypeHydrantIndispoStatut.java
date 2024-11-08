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
 * Statut de l'indisponibilité
 */
@Generated(
	value = {
		"http://www.jooq.org",
		"jOOQ version:3.6.0"
	},
	comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class TypeHydrantIndispoStatut extends TableImpl<Record> {

	private static final long serialVersionUID = 951905897;

	/**
	 * The reference instance of <code>remocra.type_hydrant_indispo_statut</code>
	 */
	public static final TypeHydrantIndispoStatut TYPE_HYDRANT_INDISPO_STATUT = new TypeHydrantIndispoStatut();

	/**
	 * The class holding records for this type
	 */
	@Override
	public Class<Record> getRecordType() {
		return Record.class;
	}

	/**
	 * The column <code>remocra.type_hydrant_indispo_statut.id</code>.
	 */
	public final TableField<Record, Long> ID = createField("id", org.jooq.impl.SQLDataType.BIGINT.nullable(false).defaulted(true), this, "");

	/**
	 * The column <code>remocra.type_hydrant_indispo_statut.actif</code>.
	 */
	public final TableField<Record, Boolean> ACTIF = createField("actif", org.jooq.impl.SQLDataType.BOOLEAN.defaulted(true), this, "");

	/**
	 * The column <code>remocra.type_hydrant_indispo_statut.code</code>.
	 */
	public final TableField<Record, String> CODE = createField("code", org.jooq.impl.SQLDataType.VARCHAR.nullable(false), this, "");

	/**
	 * The column <code>remocra.type_hydrant_indispo_statut.nom</code>.
	 */
	public final TableField<Record, String> NOM = createField("nom", org.jooq.impl.SQLDataType.VARCHAR.nullable(false), this, "");

	/**
	 * Create a <code>remocra.type_hydrant_indispo_statut</code> table reference
	 */
	public TypeHydrantIndispoStatut() {
		this("type_hydrant_indispo_statut", null);
	}

	/**
	 * Create an aliased <code>remocra.type_hydrant_indispo_statut</code> table reference
	 */
	public TypeHydrantIndispoStatut(String alias) {
		this(alias, TYPE_HYDRANT_INDISPO_STATUT);
	}

	private TypeHydrantIndispoStatut(String alias, Table<Record> aliased) {
		this(alias, aliased, null);
	}

	private TypeHydrantIndispoStatut(String alias, Table<Record> aliased, Field<?>[] parameters) {
		super(alias, Remocra.REMOCRA, aliased, parameters, "Statut de l'indisponibilité");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Identity<Record, Long> getIdentity() {
		return Keys.IDENTITY_TYPE_HYDRANT_INDISPO_STATUT;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public UniqueKey<Record> getPrimaryKey() {
		return Keys.TYPE_HYDRANT_INDISPO_STATUT_PKEY;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<UniqueKey<Record>> getKeys() {
		return Arrays.<UniqueKey<Record>>asList(Keys.TYPE_HYDRANT_INDISPO_STATUT_PKEY);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public TypeHydrantIndispoStatut as(String alias) {
		return new TypeHydrantIndispoStatut(alias, this);
	}

	/**
	 * Rename this table
	 */
	public TypeHydrantIndispoStatut rename(String name) {
		return new TypeHydrantIndispoStatut(name, null);
	}
}
