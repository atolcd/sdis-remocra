package fr.sdis83.remocra;

import java.util.Objects;

/** Classe permettant de définir des constantes globales */
public class GlobalConstants {
  public static final String SRID_4326 = "4326";
  public static final String SRID_3857 = "3857";
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

  // Clé du paramètre BUFFER_CARTE stocké dans la table remocra.parametre
  public static final String CLE_BUFFER_CARTE = "BUFFER_CARTE";

  // Clé du paramètre AFFICHAGE_INDISPO stocké dans la table remocra.parametre
  public static final String AFFICHAGE_INDISPO = "AFFICHAGE_INDISPO";

  // Clé du paramètre MDP_ADMINISTRATEUR stocké dans la table remocra.parametre
  public static final String MDP_ADMINISTRATEUR = "MDP_ADMINISTRATEUR";

  // Clé du paramètre DUREE_VALIDITE_TOKEN stocké dans la table remocra.parametre
  public static final String DUREE_VALIDITE_TOKEN = "DUREE_VALIDITE_TOKEN";

  /**
   * Enumération des types de visite, avec
   *
   * <ul>
   *   <li>Le code tel que représenté en BDD
   *   <li>Le libellé à titre informatif
   * </ul>
   */
  public enum TypeVisite {
    LECTURE("LECT", "Lecture"),
    CREATION("CREA", "Création"),
    RECEPTION("RECEP", "Réception"),
    RECONNAISSANCE("RECO", "Reconnaissance"),
    CONTROLE("CTRL", "Contrôle"),
    NON_PROGRAMMEE("NP", "Non programmée");

    private final String codeTypeVisite;
    private final String libelleTypeVisite;

    TypeVisite(String code, String libelle) {
      codeTypeVisite = code;
      libelleTypeVisite = libelle;
    }

    public String getCode() {
      return codeTypeVisite;
    }

    public String getLibelle() {
      return libelleTypeVisite;
    }

    public static TypeVisite fromCode(String codeTypeVisite) {
      for (TypeVisite typeVisite : values()) {
        if (Objects.equals(typeVisite.codeTypeVisite, codeTypeVisite)) {
          return typeVisite;
        }
      }
      throw new IllegalArgumentException("TypeVisite non reconnu : " + codeTypeVisite);
    }
  }
}
