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
 * Service des eaux
 */
@Generated(
	value = {
		"http://www.jooq.org",
		"jOOQ version:3.6.0"
	},
	comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class ServiceEaux extends TableImpl<Record> {

	private static final long serialVersionUID = -1144254351;

	/**
	 * The reference instance of <code>remocra.service_eaux</code>
	 */
	public static final ServiceEaux SERVICE_EAUX = new ServiceEaux();

	/**
	 * The class holding records for this type
	 */
	@Override
	public Class<Record> getRecordType() {
		return Record.class;
	}

	/**
	 * The column <code>remocra.service_eaux.id</code>. Identifiant interne
	 */
	public final TableField<Record, Long> ID = createField("id", org.jooq.impl.SQLDataType.BIGINT.nullable(false).defaulted(true), this, "Identifiant interne");

	/**
	 * The column <code>remocra.service_eaux.actif</code>. Sélectionnable dans l'interface
	 */
	public final TableField<Record, Boolean> ACTIF = createField("actif", org.jooq.impl.SQLDataType.BOOLEAN.nullable(false).defaulted(true), this, "Sélectionnable dans l'interface");

	/**
	 * The column <code>remocra.service_eaux.code</code>. Code du statut. Facilite les échanges de données
	 */
	public final TableField<Record, String> CODE = createField("code", org.jooq.impl.SQLDataType.VARCHAR.nullable(false), this, "Code du statut. Facilite les échanges de données");

	/**
	 * The column <code>remocra.service_eaux.nom</code>. Libellé du service des eaux
	 */
	public final TableField<Record, String> NOM = createField("nom", org.jooq.impl.SQLDataType.VARCHAR.nullable(false), this, "Libellé du service des eaux");

	/**
	 * The column <code>remocra.service_eaux.version</code>.
	 */
	public final TableField<Record, Integer> VERSION = createField("version", org.jooq.impl.SQLDataType.INTEGER.defaulted(true), this, "");

	/**
	 * Create a <code>remocra.service_eaux</code> table reference
	 */
	public ServiceEaux() {
		this("service_eaux", null);
	}

	/**
	 * Create an aliased <code>remocra.service_eaux</code> table reference
	 */
	public ServiceEaux(String alias) {
		this(alias, SERVICE_EAUX);
	}

	private ServiceEaux(String alias, Table<Record> aliased) {
		this(alias, aliased, null);
	}

	private ServiceEaux(String alias, Table<Record> aliased, Field<?>[] parameters) {
		super(alias, Remocra.REMOCRA, aliased, parameters, "Service des eaux");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Identity<Record, Long> getIdentity() {
		return Keys.IDENTITY_SERVICE_EAUX;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public UniqueKey<Record> getPrimaryKey() {
		return Keys.SERVICE_EAUX_PKEY;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<UniqueKey<Record>> getKeys() {
		return Arrays.<UniqueKey<Record>>asList(Keys.SERVICE_EAUX_PKEY);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ServiceEaux as(String alias) {
		return new ServiceEaux(alias, this);
	}

	/**
	 * Rename this table
	 */
	public ServiceEaux rename(String name) {
		return new ServiceEaux(name, null);
	}
}