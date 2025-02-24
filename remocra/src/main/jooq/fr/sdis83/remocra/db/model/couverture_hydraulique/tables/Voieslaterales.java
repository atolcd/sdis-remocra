/**
 * This class is generated by jOOQ
 */
package fr.sdis83.remocra.db.model.couverture_hydraulique.tables;


import fr.sdis83.remocra.db.model.couverture_hydraulique.CouvertureHydraulique;

import javax.annotation.Generated;

import org.jooq.Field;
import org.jooq.Record;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.impl.TableImpl;


/**
 * Résultante de la fonction couverture_hydraulique.voiesLaterales()
 */
@Generated(
	value = {
		"http://www.jooq.org",
		"jOOQ version:3.6.0"
	},
	comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Voieslaterales extends TableImpl<Record> {

	private static final long serialVersionUID = -102357520;

	/**
	 * The reference instance of <code>couverture_hydraulique.voieslaterales</code>
	 */
	public static final Voieslaterales VOIESLATERALES = new Voieslaterales();

	/**
	 * The class holding records for this type
	 */
	@Override
	public Class<Record> getRecordType() {
		return Record.class;
	}

	/**
	 * The column <code>couverture_hydraulique.voieslaterales.voie</code>. Voie voisine de la voie actuelle
	 */
	public final TableField<Record, Integer> VOIE = createField("voie", org.jooq.impl.SQLDataType.INTEGER, this, "Voie voisine de la voie actuelle");

	/**
	 * The column <code>couverture_hydraulique.voieslaterales.angle</code>. Angle que forme la voie avec la voie actuelle
	 */
	public final TableField<Record, Double> ANGLE = createField("angle", org.jooq.impl.SQLDataType.DOUBLE, this, "Angle que forme la voie avec la voie actuelle");

	/**
	 * The column <code>couverture_hydraulique.voieslaterales.gauche</code>. Indique si la voie est celle se situant le plus à gauche
	 */
	public final TableField<Record, Boolean> GAUCHE = createField("gauche", org.jooq.impl.SQLDataType.BOOLEAN, this, "Indique si la voie est celle se situant le plus à gauche");

	/**
	 * The column <code>couverture_hydraulique.voieslaterales.droite</code>. Indique si la voie est celle se situant le plus à droite
	 */
	public final TableField<Record, Boolean> DROITE = createField("droite", org.jooq.impl.SQLDataType.BOOLEAN, this, "Indique si la voie est celle se situant le plus à droite");

	/**
	 * The column <code>couverture_hydraulique.voieslaterales.traversable</code>. Indique si la voie est traversable
	 */
	public final TableField<Record, Boolean> TRAVERSABLE = createField("traversable", org.jooq.impl.SQLDataType.BOOLEAN, this, "Indique si la voie est traversable");

	/**
	 * The column <code>couverture_hydraulique.voieslaterales.accessible</code>. Voie accessible depuis le point de jonction (non accessible si entre 
les voies gauche et droite si ces dernières sont non traversables)
	 */
	public final TableField<Record, Boolean> ACCESSIBLE = createField("accessible", org.jooq.impl.SQLDataType.BOOLEAN.defaulted(true), this, "Voie accessible depuis le point de jonction (non accessible si entre \nles voies gauche et droite si ces dernières sont non traversables)");

	/**
	 * Create a <code>couverture_hydraulique.voieslaterales</code> table reference
	 */
	public Voieslaterales() {
		this("voieslaterales", null);
	}

	/**
	 * Create an aliased <code>couverture_hydraulique.voieslaterales</code> table reference
	 */
	public Voieslaterales(String alias) {
		this(alias, VOIESLATERALES);
	}

	private Voieslaterales(String alias, Table<Record> aliased) {
		this(alias, aliased, null);
	}

	private Voieslaterales(String alias, Table<Record> aliased, Field<?>[] parameters) {
		super(alias, CouvertureHydraulique.COUVERTURE_HYDRAULIQUE, aliased, parameters, "Résultante de la fonction couverture_hydraulique.voiesLaterales()");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Voieslaterales as(String alias) {
		return new Voieslaterales(alias, this);
	}

	/**
	 * Rename this table
	 */
	public Voieslaterales rename(String name) {
		return new Voieslaterales(name, null);
	}
}
