/**
 * This class is generated by jOOQ
 */
package fr.sdis83.remocra.db.model.pdi.tables;


import fr.sdis83.remocra.db.model.pdi.Pdi;

import javax.annotation.Generated;

import org.jooq.Field;
import org.jooq.Record;
import org.jooq.Table;
import org.jooq.TableField;
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
public class VueModeleCourrierFicheTourneeTournee extends TableImpl<Record> {

	private static final long serialVersionUID = -1146542719;

	/**
	 * The reference instance of <code>pdi.vue_modele_courrier_fiche_tournee_tournee</code>
	 */
	public static final VueModeleCourrierFicheTourneeTournee VUE_MODELE_COURRIER_FICHE_TOURNEE_TOURNEE = new VueModeleCourrierFicheTourneeTournee();

	/**
	 * The class holding records for this type
	 */
	@Override
	public Class<Record> getRecordType() {
		return Record.class;
	}

	/**
	 * The column <code>pdi.vue_modele_courrier_fiche_tournee_tournee.id</code>.
	 */
	public final TableField<Record, Long> ID = createField("id", org.jooq.impl.SQLDataType.BIGINT, this, "");

	/**
	 * The column <code>pdi.vue_modele_courrier_fiche_tournee_tournee.libelle</code>.
	 */
	public final TableField<Record, String> LIBELLE = createField("libelle", org.jooq.impl.SQLDataType.VARCHAR, this, "");

	/**
	 * Create a <code>pdi.vue_modele_courrier_fiche_tournee_tournee</code> table reference
	 */
	public VueModeleCourrierFicheTourneeTournee() {
		this("vue_modele_courrier_fiche_tournee_tournee", null);
	}

	/**
	 * Create an aliased <code>pdi.vue_modele_courrier_fiche_tournee_tournee</code> table reference
	 */
	public VueModeleCourrierFicheTourneeTournee(String alias) {
		this(alias, VUE_MODELE_COURRIER_FICHE_TOURNEE_TOURNEE);
	}

	private VueModeleCourrierFicheTourneeTournee(String alias, Table<Record> aliased) {
		this(alias, aliased, null);
	}

	private VueModeleCourrierFicheTourneeTournee(String alias, Table<Record> aliased, Field<?>[] parameters) {
		super(alias, Pdi.PDI, aliased, parameters, "");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public VueModeleCourrierFicheTourneeTournee as(String alias) {
		return new VueModeleCourrierFicheTourneeTournee(alias, this);
	}

	/**
	 * Rename this table
	 */
	public VueModeleCourrierFicheTourneeTournee rename(String name) {
		return new VueModeleCourrierFicheTourneeTournee(name, null);
	}
}
