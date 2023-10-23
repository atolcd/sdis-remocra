package fr.sdis83.remocra;

/** Classe permettant de définir des constantes globales */
public class GlobalConstants {
  public static final Integer SRID_2154 = 2154;
  public static final String LDAP_PASSWORD = "LDAP";

  /**
   * Nom de la catégorie stockée dans les ProcessusEtlModele pour les traitements de la couverture
   * hydraulique
   */
  public static final String CATEGORIE_COUVERTURE_HYDRAULIQUE = "COUVERTURE_HYDRAULIQUE";
  /**
   * Nom de la catégorie stockée dans les ProcessusEtlModele pour les traitements du module de
   * gestion de crise
   */
  public static final String CATEGORIE_CRISE = "GESTION_CRISE";

  //////// Couverture hydraulique
  /** Clé du param conf correspondant à la profondeur de la couverture hydraulique */
  public static final String PROFONDEUR_COUVERTURE = "PROFONDEUR_COUVERTURE";

  /**
   * Clé du param conf correspondant à la distance maximal de parcours de la couverture hydraulique
   */
  public static final String DECI_DISTANCE_MAX_PARCOURS = "DECI_DISTANCE_MAX_PARCOURS";

  /** Clé du param conf correspondant aux distances à parcourir de la couverture hydraulique */
  public static final String DECI_ISODISTANCES = "DECI_ISODISTANCES";
}
