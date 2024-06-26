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
 * Gestion de crise : Type de contexte dans lequel est gérée la crise. Deux 
 * contextes cohabitent simultanément pour chaque épisode : "Opérationnel" 
 * et "Anticipation". Les données cartographiques associées peuvent varier 
 * en fonction du contexte de même que les types d'évenements à renseigner
 */
@Generated(
	value = {
		"http://www.jooq.org",
		"jOOQ version:3.6.0"
	},
	comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class TypeCriseContexte extends TableImpl<Record> {

	private static final long serialVersionUID = -642707217;

	/**
	 * The reference instance of <code>remocra.type_crise_contexte</code>
	 */
	public static final TypeCriseContexte TYPE_CRISE_CONTEXTE = new TypeCriseContexte();

	/**
	 * The class holding records for this type
	 */
	@Override
	public Class<Record> getRecordType() {
		return Record.class;
	}

	/**
	 * The column <code>remocra.type_crise_contexte.id</code>. Identifiant interne
	 */
	public final TableField<Record, Long> ID = createField("id", org.jooq.impl.SQLDataType.BIGINT.nullable(false).defaulted(true), this, "Identifiant interne");

	/**
	 * The column <code>remocra.type_crise_contexte.actif</code>. Sélectionnable dans l'interface
	 */
	public final TableField<Record, Boolean> ACTIF = createField("actif", org.jooq.impl.SQLDataType.BOOLEAN.defaulted(true), this, "Sélectionnable dans l'interface");

	/**
	 * The column <code>remocra.type_crise_contexte.code</code>. Code du contexte. Facilite les échanges de données
	 */
	public final TableField<Record, String> CODE = createField("code", org.jooq.impl.SQLDataType.VARCHAR.nullable(false), this, "Code du contexte. Facilite les échanges de données");

	/**
	 * The column <code>remocra.type_crise_contexte.nom</code>. Libellé du contexte
	 */
	public final TableField<Record, String> NOM = createField("nom", org.jooq.impl.SQLDataType.VARCHAR.nullable(false), this, "Libellé du contexte");

	/**
	 * Create a <code>remocra.type_crise_contexte</code> table reference
	 */
	public TypeCriseContexte() {
		this("type_crise_contexte", null);
	}

	/**
	 * Create an aliased <code>remocra.type_crise_contexte</code> table reference
	 */
	public TypeCriseContexte(String alias) {
		this(alias, TYPE_CRISE_CONTEXTE);
	}

	private TypeCriseContexte(String alias, Table<Record> aliased) {
		this(alias, aliased, null);
	}

	private TypeCriseContexte(String alias, Table<Record> aliased, Field<?>[] parameters) {
		super(alias, Remocra.REMOCRA, aliased, parameters, "Gestion de crise : Type de contexte dans lequel est gérée la crise. Deux contextes cohabitent simultanément pour chaque épisode : \"Opérationnel\" et \"Anticipation\". Les données cartographiques associées peuvent varier en fonction du contexte de même que les types d'évenements à renseigner");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Identity<Record, Long> getIdentity() {
		return Keys.IDENTITY_TYPE_CRISE_CONTEXTE;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public UniqueKey<Record> getPrimaryKey() {
		return Keys.TYPE_CRISE_CONTEXTE_PKEY;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<UniqueKey<Record>> getKeys() {
		return Arrays.<UniqueKey<Record>>asList(Keys.TYPE_CRISE_CONTEXTE_PKEY, Keys.TYPE_CRISE_CONTEXTE_CODE_KEY, Keys.TYPE_CRISE_CONTEXTE_NOM_KEY);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public TypeCriseContexte as(String alias) {
		return new TypeCriseContexte(alias, this);
	}

	/**
	 * Rename this table
	 */
	public TypeCriseContexte rename(String name) {
		return new TypeCriseContexte(name, null);
	}
}
