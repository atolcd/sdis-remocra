/**
 * This class is generated by jOOQ
 */
package fr.sdis83.remocra.db.model.couverture_hydraulique;


import fr.sdis83.remocra.db.model.couverture_hydraulique.tables.Batiments;
import fr.sdis83.remocra.db.model.couverture_hydraulique.tables.CouvertureHydrauliquePei;
import fr.sdis83.remocra.db.model.couverture_hydraulique.tables.CouvertureHydrauliqueZonage;
import fr.sdis83.remocra.db.model.couverture_hydraulique.tables.Pei;
import fr.sdis83.remocra.db.model.couverture_hydraulique.tables.Reseau;
import fr.sdis83.remocra.db.model.couverture_hydraulique.tables.Sommet;
import fr.sdis83.remocra.db.model.couverture_hydraulique.tables.TempDistances;
import fr.sdis83.remocra.db.model.couverture_hydraulique.tables.Voieslaterales;

import javax.annotation.Generated;


/**
 * Convenience access to all tables in couverture_hydraulique
 */
@Generated(
	value = {
		"http://www.jooq.org",
		"jOOQ version:3.6.0"
	},
	comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Tables {

	/**
	 * The table couverture_hydraulique.batiments
	 */
	public static final Batiments BATIMENTS = fr.sdis83.remocra.db.model.couverture_hydraulique.tables.Batiments.BATIMENTS;

	/**
	 * The table couverture_hydraulique.couverture_hydraulique_pei
	 */
	public static final CouvertureHydrauliquePei COUVERTURE_HYDRAULIQUE_PEI = fr.sdis83.remocra.db.model.couverture_hydraulique.tables.CouvertureHydrauliquePei.COUVERTURE_HYDRAULIQUE_PEI;

	/**
	 * The table couverture_hydraulique.couverture_hydraulique_zonage
	 */
	public static final CouvertureHydrauliqueZonage COUVERTURE_HYDRAULIQUE_ZONAGE = fr.sdis83.remocra.db.model.couverture_hydraulique.tables.CouvertureHydrauliqueZonage.COUVERTURE_HYDRAULIQUE_ZONAGE;

	/**
	 * The table couverture_hydraulique.pei
	 */
	public static final Pei PEI = fr.sdis83.remocra.db.model.couverture_hydraulique.tables.Pei.PEI;

	/**
	 * The table couverture_hydraulique.reseau
	 */
	public static final Reseau RESEAU = fr.sdis83.remocra.db.model.couverture_hydraulique.tables.Reseau.RESEAU;

	/**
	 * The table couverture_hydraulique.sommet
	 */
	public static final Sommet SOMMET = fr.sdis83.remocra.db.model.couverture_hydraulique.tables.Sommet.SOMMET;

	/**
	 * The table couverture_hydraulique.temp_distances
	 */
	public static final TempDistances TEMP_DISTANCES = fr.sdis83.remocra.db.model.couverture_hydraulique.tables.TempDistances.TEMP_DISTANCES;

	/**
	 * The table couverture_hydraulique.voieslaterales
	 */
	public static final Voieslaterales VOIESLATERALES = fr.sdis83.remocra.db.model.couverture_hydraulique.tables.Voieslaterales.VOIESLATERALES;
}