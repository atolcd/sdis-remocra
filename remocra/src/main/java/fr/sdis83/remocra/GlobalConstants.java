package fr.sdis83.remocra;

/** Classe permettant de définir des constantes globales */
public class GlobalConstants {

  public static Integer SRID_PARAM = 2154;
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

  /** Champ appartenance dans la table contact */
  public static final String GESTIONNAIRE = "GESTIONNAIRE";

  public static final String ORGANISME = "ORGANISME";

  //////// Parametre
  public static final String PARAMETRE_AGENTS = "GESTION_AGENT";

  // Paramètre requête Zone de compétence
  public static final String ZONE_COMPETENCE_ID = "ZONE_COMPETENCE_ID";

  // Clé du paramètre SRID stocké dans la table remocra.parametre
  public static final String CLE_SRID = "SRID";
  public static final String GENERIQUE_MESSAGE_ERROR =
      "Une erreur est survenue lors de l'éxécution de la requête. Un log a été écrit, veuillez contacter votre référent technique pour qu'il puisse le consulter.";
}
