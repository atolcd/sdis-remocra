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
public class CourrierParametre extends TableImpl<Record> {

	private static final long serialVersionUID = -119421448;

	/**
	 * The reference instance of <code>remocra.courrier_parametre</code>
	 */
	public static final CourrierParametre COURRIER_PARAMETRE = new CourrierParametre();

	/**
	 * The class holding records for this type
	 */
	@Override
	public Class<Record> getRecordType() {
		return Record.class;
	}

	/**
	 * The column <code>remocra.courrier_parametre.id</code>.
	 */
	public final TableField<Record, Long> ID = createField("id", org.jooq.impl.SQLDataType.BIGINT.nullable(false).defaulted(true), this, "");

	/**
	 * The column <code>remocra.courrier_parametre.nom</code>.
	 */
	public final TableField<Record, String> NOM = createField("nom", org.jooq.impl.SQLDataType.VARCHAR.nullable(false), this, "");

	/**
	 * The column <code>remocra.courrier_parametre.type_valeur</code>.
	 */
	public final TableField<Record, String> TYPE_VALEUR = createField("type_valeur", org.jooq.impl.SQLDataType.VARCHAR.nullable(false), this, "");

	/**
	 * The column <code>remocra.courrier_parametre.obligatoire</code>.
	 */
	public final TableField<Record, Boolean> OBLIGATOIRE = createField("obligatoire", org.jooq.impl.SQLDataType.BOOLEAN.nullable(false), this, "");

	/**
	 * The column <code>remocra.courrier_parametre.source_sql</code>.
	 */
	public final TableField<Record, String> SOURCE_SQL = createField("source_sql", org.jooq.impl.SQLDataType.VARCHAR, this, "");

	/**
	 * The column <code>remocra.courrier_parametre.source_sql_valeur</code>.
	 */
	public final TableField<Record, String> SOURCE_SQL_VALEUR = createField("source_sql_valeur", org.jooq.impl.SQLDataType.VARCHAR, this, "");

	/**
	 * The column <code>remocra.courrier_parametre.source_sql_libelle</code>.
	 */
	public final TableField<Record, String> SOURCE_SQL_LIBELLE = createField("source_sql_libelle", org.jooq.impl.SQLDataType.VARCHAR, this, "");

	/**
	 * The column <code>remocra.courrier_parametre.formulaire_etiquette</code>.
	 */
	public final TableField<Record, String> FORMULAIRE_ETIQUETTE = createField("formulaire_etiquette", org.jooq.impl.SQLDataType.VARCHAR.nullable(false), this, "");

	/**
	 * The column <code>remocra.courrier_parametre.formulaire_type_controle</code>.
	 */
	public final TableField<Record, String> FORMULAIRE_TYPE_CONTROLE = createField("formulaire_type_controle", org.jooq.impl.SQLDataType.VARCHAR.nullable(false), this, "");

	/**
	 * The column <code>remocra.courrier_parametre.formulaire_num_ordre</code>.
	 */
	public final TableField<Record, Long> FORMULAIRE_NUM_ORDRE = createField("formulaire_num_ordre", org.jooq.impl.SQLDataType.BIGINT.nullable(false), this, "");

	/**
	 * The column <code>remocra.courrier_parametre.formulaire_valeur_defaut</code>.
	 */
	public final TableField<Record, String> FORMULAIRE_VALEUR_DEFAUT = createField("formulaire_valeur_defaut", org.jooq.impl.SQLDataType.VARCHAR, this, "");

	/**
	 * The column <code>remocra.courrier_parametre.modele</code>.
	 */
	public final TableField<Record, Long> MODELE = createField("modele", org.jooq.impl.SQLDataType.BIGINT.nullable(false), this, "");

	/**
	 * Create a <code>remocra.courrier_parametre</code> table reference
	 */
	public CourrierParametre() {
		this("courrier_parametre", null);
	}

	/**
	 * Create an aliased <code>remocra.courrier_parametre</code> table reference
	 */
	public CourrierParametre(String alias) {
		this(alias, COURRIER_PARAMETRE);
	}

	private CourrierParametre(String alias, Table<Record> aliased) {
		this(alias, aliased, null);
	}

	private CourrierParametre(String alias, Table<Record> aliased, Field<?>[] parameters) {
		super(alias, Remocra.REMOCRA, aliased, parameters, "");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Identity<Record, Long> getIdentity() {
		return Keys.IDENTITY_COURRIER_PARAMETRE;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public UniqueKey<Record> getPrimaryKey() {
		return Keys.COURRIER_PARAMETRE_PKEY;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<UniqueKey<Record>> getKeys() {
		return Arrays.<UniqueKey<Record>>asList(Keys.COURRIER_PARAMETRE_PKEY);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<ForeignKey<Record, ?>> getReferences() {
		return Arrays.<ForeignKey<Record, ?>>asList(Keys.COURRIER_PARAMETRE__COURRIER_PARAMETRE_MODELE);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public CourrierParametre as(String alias) {
		return new CourrierParametre(alias, this);
	}

	/**
	 * Rename this table
	 */
	public CourrierParametre rename(String name) {
		return new CourrierParametre(name, null);
	}
}