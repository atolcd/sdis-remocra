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
 * Détail des éléments des requêtes personnalisées REMOCRA
 */
@Generated(
	value = {
		"http://www.jooq.org",
		"jOOQ version:3.6.0"
	},
	comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class RequeteModeleSelectionDetail extends TableImpl<Record> {

	private static final long serialVersionUID = 338205253;

	/**
	 * The reference instance of <code>remocra.requete_modele_selection_detail</code>
	 */
	public static final RequeteModeleSelectionDetail REQUETE_MODELE_SELECTION_DETAIL = new RequeteModeleSelectionDetail();

	/**
	 * The class holding records for this type
	 */
	@Override
	public Class<Record> getRecordType() {
		return Record.class;
	}

	/**
	 * The column <code>remocra.requete_modele_selection_detail.id</code>. Identifiant interne auto-généré
	 */
	public final TableField<Record, Long> ID = createField("id", org.jooq.impl.SQLDataType.BIGINT.nullable(false).defaulted(true), this, "Identifiant interne auto-généré");

	/**
	 * The column <code>remocra.requete_modele_selection_detail.selection</code>. Sélection parente
	 */
	public final TableField<Record, Long> SELECTION = createField("selection", org.jooq.impl.SQLDataType.BIGINT.nullable(false), this, "Sélection parente");

	/**
	 * The column <code>remocra.requete_modele_selection_detail.geometrie</code>. Géométrie des éléments sélectionnés
	 */
	public final TableField<Record, Object> GEOMETRIE = createField("geometrie", org.jooq.impl.DefaultDataType.getDefaultDataType("USER-DEFINED"), this, "Géométrie des éléments sélectionnés");

	/**
	 * Create a <code>remocra.requete_modele_selection_detail</code> table reference
	 */
	public RequeteModeleSelectionDetail() {
		this("requete_modele_selection_detail", null);
	}

	/**
	 * Create an aliased <code>remocra.requete_modele_selection_detail</code> table reference
	 */
	public RequeteModeleSelectionDetail(String alias) {
		this(alias, REQUETE_MODELE_SELECTION_DETAIL);
	}

	private RequeteModeleSelectionDetail(String alias, Table<Record> aliased) {
		this(alias, aliased, null);
	}

	private RequeteModeleSelectionDetail(String alias, Table<Record> aliased, Field<?>[] parameters) {
		super(alias, Remocra.REMOCRA, aliased, parameters, "Détail des éléments des requêtes personnalisées REMOCRA");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Identity<Record, Long> getIdentity() {
		return Keys.IDENTITY_REQUETE_MODELE_SELECTION_DETAIL;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public UniqueKey<Record> getPrimaryKey() {
		return Keys.REQUETE_MODELE_SELECTION_DETAIL_PKEY;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<UniqueKey<Record>> getKeys() {
		return Arrays.<UniqueKey<Record>>asList(Keys.REQUETE_MODELE_SELECTION_DETAIL_PKEY);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<ForeignKey<Record, ?>> getReferences() {
		return Arrays.<ForeignKey<Record, ?>>asList(Keys.REQUETE_MODELE_SELECTION_DETAIL__REQUETE_MODELE_SELECTION);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public RequeteModeleSelectionDetail as(String alias) {
		return new RequeteModeleSelectionDetail(alias, this);
	}

	/**
	 * Rename this table
	 */
	public RequeteModeleSelectionDetail rename(String name) {
		return new RequeteModeleSelectionDetail(name, null);
	}
}