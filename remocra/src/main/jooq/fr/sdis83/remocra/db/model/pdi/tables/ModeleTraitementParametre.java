/**
 * This class is generated by jOOQ
 */
package fr.sdis83.remocra.db.model.pdi.tables;


import fr.sdis83.remocra.db.model.pdi.Keys;
import fr.sdis83.remocra.db.model.pdi.Pdi;

import java.util.Arrays;
import java.util.List;

import javax.annotation.Generated;

import org.jooq.Field;
import org.jooq.ForeignKey;
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
public class ModeleTraitementParametre extends TableImpl<Record> {

	private static final long serialVersionUID = -557777041;

	/**
	 * The reference instance of <code>pdi.modele_traitement_parametre</code>
	 */
	public static final ModeleTraitementParametre MODELE_TRAITEMENT_PARAMETRE = new ModeleTraitementParametre();

	/**
	 * The class holding records for this type
	 */
	@Override
	public Class<Record> getRecordType() {
		return Record.class;
	}

	/**
	 * The column <code>pdi.modele_traitement_parametre.idparametre</code>.
	 */
	public final TableField<Record, Integer> IDPARAMETRE = createField("idparametre", org.jooq.impl.SQLDataType.INTEGER.nullable(false), this, "");

	/**
	 * The column <code>pdi.modele_traitement_parametre.form_etiquette</code>.
	 */
	public final TableField<Record, String> FORM_ETIQUETTE = createField("form_etiquette", org.jooq.impl.SQLDataType.VARCHAR.nullable(false), this, "");

	/**
	 * The column <code>pdi.modele_traitement_parametre.form_num_ordre</code>.
	 */
	public final TableField<Record, Integer> FORM_NUM_ORDRE = createField("form_num_ordre", org.jooq.impl.SQLDataType.INTEGER.nullable(false), this, "");

	/**
	 * The column <code>pdi.modele_traitement_parametre.form_obligatoire</code>.
	 */
	public final TableField<Record, Boolean> FORM_OBLIGATOIRE = createField("form_obligatoire", org.jooq.impl.SQLDataType.BOOLEAN.nullable(false), this, "");

	/**
	 * The column <code>pdi.modele_traitement_parametre.form_source_donnee</code>.
	 */
	public final TableField<Record, String> FORM_SOURCE_DONNEE = createField("form_source_donnee", org.jooq.impl.SQLDataType.VARCHAR, this, "");

	/**
	 * The column <code>pdi.modele_traitement_parametre.form_type_valeur</code>.
	 */
	public final TableField<Record, String> FORM_TYPE_VALEUR = createField("form_type_valeur", org.jooq.impl.SQLDataType.VARCHAR.nullable(false), this, "");

	/**
	 * The column <code>pdi.modele_traitement_parametre.form_valeur_defaut</code>.
	 */
	public final TableField<Record, String> FORM_VALEUR_DEFAUT = createField("form_valeur_defaut", org.jooq.impl.SQLDataType.VARCHAR, this, "");

	/**
	 * The column <code>pdi.modele_traitement_parametre.nom</code>.
	 */
	public final TableField<Record, String> NOM = createField("nom", org.jooq.impl.SQLDataType.VARCHAR.nullable(false), this, "");

	/**
	 * The column <code>pdi.modele_traitement_parametre.idmodele</code>.
	 */
	public final TableField<Record, Integer> IDMODELE = createField("idmodele", org.jooq.impl.SQLDataType.INTEGER.nullable(false), this, "");

	/**
	 * Create a <code>pdi.modele_traitement_parametre</code> table reference
	 */
	public ModeleTraitementParametre() {
		this("modele_traitement_parametre", null);
	}

	/**
	 * Create an aliased <code>pdi.modele_traitement_parametre</code> table reference
	 */
	public ModeleTraitementParametre(String alias) {
		this(alias, MODELE_TRAITEMENT_PARAMETRE);
	}

	private ModeleTraitementParametre(String alias, Table<Record> aliased) {
		this(alias, aliased, null);
	}

	private ModeleTraitementParametre(String alias, Table<Record> aliased, Field<?>[] parameters) {
		super(alias, Pdi.PDI, aliased, parameters, "");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public UniqueKey<Record> getPrimaryKey() {
		return Keys.MODELE_TRAITEMENT_PARAMETRE_PKEY;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<UniqueKey<Record>> getKeys() {
		return Arrays.<UniqueKey<Record>>asList(Keys.MODELE_TRAITEMENT_PARAMETRE_PKEY);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<ForeignKey<Record, ?>> getReferences() {
		return Arrays.<ForeignKey<Record, ?>>asList(Keys.MODELE_TRAITEMENT_PARAMETRE__FK3D5405684675AFA4);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ModeleTraitementParametre as(String alias) {
		return new ModeleTraitementParametre(alias, this);
	}

	/**
	 * Rename this table
	 */
	public ModeleTraitementParametre rename(String name) {
		return new ModeleTraitementParametre(name, null);
	}
}
