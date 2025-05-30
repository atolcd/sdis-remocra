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
 * Propriété complémentaire pouvant être renseignée lors de la création ou 
 * de la modification d'un évènement
 */
@Generated(
	value = {
		"http://www.jooq.org",
		"jOOQ version:3.6.0"
	},
	comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class TypeCriseProprieteEvenement extends TableImpl<Record> {

	private static final long serialVersionUID = 256538455;

	/**
	 * The reference instance of <code>remocra.type_crise_propriete_evenement</code>
	 */
	public static final TypeCriseProprieteEvenement TYPE_CRISE_PROPRIETE_EVENEMENT = new TypeCriseProprieteEvenement();

	/**
	 * The class holding records for this type
	 */
	@Override
	public Class<Record> getRecordType() {
		return Record.class;
	}

	/**
	 * The column <code>remocra.type_crise_propriete_evenement.id</code>. Identifiant interne
	 */
	public final TableField<Record, Long> ID = createField("id", org.jooq.impl.SQLDataType.BIGINT.nullable(false).defaulted(true), this, "Identifiant interne");

	/**
	 * The column <code>remocra.type_crise_propriete_evenement.nom</code>. Nom de la propriété. Sans espace ni caractère spécial
	 */
	public final TableField<Record, String> NOM = createField("nom", org.jooq.impl.SQLDataType.VARCHAR.nullable(false), this, "Nom de la propriété. Sans espace ni caractère spécial");

	/**
	 * The column <code>remocra.type_crise_propriete_evenement.type_valeur</code>. Type de valeur attendue
	 */
	public final TableField<Record, String> TYPE_VALEUR = createField("type_valeur", org.jooq.impl.SQLDataType.VARCHAR.nullable(false), this, "Type de valeur attendue");

	/**
	 * The column <code>remocra.type_crise_propriete_evenement.obligatoire</code>. La saisie d'une valeur pour le paramètre est obligatoire
	 */
	public final TableField<Record, Boolean> OBLIGATOIRE = createField("obligatoire", org.jooq.impl.SQLDataType.BOOLEAN.nullable(false), this, "La saisie d'une valeur pour le paramètre est obligatoire");

	/**
	 * The column <code>remocra.type_crise_propriete_evenement.source_sql</code>. Définition de la requête SQL éventuelle à exécuter sur la base de données REMOCRA pour fournir les informations facilitant ou limitant la saisie de valeurs pour l'utilisateur
	 */
	public final TableField<Record, String> SOURCE_SQL = createField("source_sql", org.jooq.impl.SQLDataType.VARCHAR, this, "Définition de la requête SQL éventuelle à exécuter sur la base de données REMOCRA pour fournir les informations facilitant ou limitant la saisie de valeurs pour l'utilisateur");

	/**
	 * The column <code>remocra.type_crise_propriete_evenement.source_sql_valeur</code>. Colonne de la requête SQL éventuelle contenant la valeur du paramètre
	 */
	public final TableField<Record, String> SOURCE_SQL_VALEUR = createField("source_sql_valeur", org.jooq.impl.SQLDataType.VARCHAR, this, "Colonne de la requête SQL éventuelle contenant la valeur du paramètre");

	/**
	 * The column <code>remocra.type_crise_propriete_evenement.source_sql_libelle</code>. Colonne de la requête SQL éventuelle contenant le libellé associé à la valeur du paramètre
	 */
	public final TableField<Record, String> SOURCE_SQL_LIBELLE = createField("source_sql_libelle", org.jooq.impl.SQLDataType.VARCHAR, this, "Colonne de la requête SQL éventuelle contenant le libellé associé à la valeur du paramètre");

	/**
	 * The column <code>remocra.type_crise_propriete_evenement.formulaire_etiquette</code>. Etiquette associée au champ de saisie
	 */
	public final TableField<Record, String> FORMULAIRE_ETIQUETTE = createField("formulaire_etiquette", org.jooq.impl.SQLDataType.VARCHAR.nullable(false), this, "Etiquette associée au champ de saisie");

	/**
	 * The column <code>remocra.type_crise_propriete_evenement.formulaire_type_controle</code>. Type de contrôle associé au champ de saisie
	 */
	public final TableField<Record, String> FORMULAIRE_TYPE_CONTROLE = createField("formulaire_type_controle", org.jooq.impl.SQLDataType.VARCHAR.nullable(false), this, "Type de contrôle associé au champ de saisie");

	/**
	 * The column <code>remocra.type_crise_propriete_evenement.formulaire_num_ordre</code>. Position d'affichage du champ de saisie dans le formulaire
	 */
	public final TableField<Record, Long> FORMULAIRE_NUM_ORDRE = createField("formulaire_num_ordre", org.jooq.impl.SQLDataType.BIGINT.nullable(false), this, "Position d'affichage du champ de saisie dans le formulaire");

	/**
	 * The column <code>remocra.type_crise_propriete_evenement.formulaire_valeur_defaut</code>. Valeur par défaut proposée dans le champ de saisie
	 */
	public final TableField<Record, String> FORMULAIRE_VALEUR_DEFAUT = createField("formulaire_valeur_defaut", org.jooq.impl.SQLDataType.VARCHAR, this, "Valeur par défaut proposée dans le champ de saisie");

	/**
	 * The column <code>remocra.type_crise_propriete_evenement.nature_evenement</code>. Nature d'évènement associée
	 */
	public final TableField<Record, Long> NATURE_EVENEMENT = createField("nature_evenement", org.jooq.impl.SQLDataType.BIGINT.nullable(false), this, "Nature d'évènement associée");

	/**
	 * Create a <code>remocra.type_crise_propriete_evenement</code> table reference
	 */
	public TypeCriseProprieteEvenement() {
		this("type_crise_propriete_evenement", null);
	}

	/**
	 * Create an aliased <code>remocra.type_crise_propriete_evenement</code> table reference
	 */
	public TypeCriseProprieteEvenement(String alias) {
		this(alias, TYPE_CRISE_PROPRIETE_EVENEMENT);
	}

	private TypeCriseProprieteEvenement(String alias, Table<Record> aliased) {
		this(alias, aliased, null);
	}

	private TypeCriseProprieteEvenement(String alias, Table<Record> aliased, Field<?>[] parameters) {
		super(alias, Remocra.REMOCRA, aliased, parameters, "Propriété complémentaire pouvant être renseignée lors de la création ou de la modification d'un évènement");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Identity<Record, Long> getIdentity() {
		return Keys.IDENTITY_TYPE_CRISE_PROPRIETE_EVENEMENT;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public UniqueKey<Record> getPrimaryKey() {
		return Keys.TYPE_CRISE_PROPRIETE_EVENEMENT_PKEY;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<UniqueKey<Record>> getKeys() {
		return Arrays.<UniqueKey<Record>>asList(Keys.TYPE_CRISE_PROPRIETE_EVENEMENT_PKEY);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<ForeignKey<Record, ?>> getReferences() {
		return Arrays.<ForeignKey<Record, ?>>asList(Keys.TYPE_CRISE_PROPRIETE_EVENEMENT__TYPE_CRISE_PROPRIETE_EVENEMENT_NATURE_EVENEMENT_FK);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public TypeCriseProprieteEvenement as(String alias) {
		return new TypeCriseProprieteEvenement(alias, this);
	}

	/**
	 * Rename this table
	 */
	public TypeCriseProprieteEvenement rename(String name) {
		return new TypeCriseProprieteEvenement(name, null);
	}
}
