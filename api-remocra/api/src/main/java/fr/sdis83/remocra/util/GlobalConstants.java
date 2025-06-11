package fr.sdis83.remocra.util;

/** Constantes globales à l'API */
public class GlobalConstants {

  /** Type prédéfini pour un organisme de type "service des eaux" */
  public static final String SERVICE_EAUX = "SERVICEEAUX";

  /** Type prédéfini pour un organisme de type "prestataire technique" */
  public static final String PRESTATAIRE_TECHNIQUE = "PRESTATAIRE_TECHNIQUE";

  /** Type prédéfini pour un organisme de type "commune */
  public static final String COMMUNE = "COMMUNE";

  /** Type prédéfini pour un organisme de type "EPCI" */
  public static final String EPCI = "EPCI";

  /** Tag pour les endpoints de l'application mobile */
  public static final String REMOCRA_MOBILE_TAG = "REMOcRA Mobile";

  public enum TypeHydrant {
    PIBI("PIBI", "PIBI"),
    PENA("PENA", "PENA");

    private final String code;
    private final String libelle;

    TypeHydrant(String code, String libelle) {
      this.code = code;
      this.libelle = libelle;
    }

    public String getCode() {
      return code;
    }

    public String getLibelle() {
      return libelle;
    }
  }

  public enum AuteurModificationFlag {
    API("API"),
    MOBILE("MOBILE");

    private final String auteurModificationFlag;

    AuteurModificationFlag(String auteurModificationFlag) {
      this.auteurModificationFlag = auteurModificationFlag;
    }

    public String getAuteurModificationFlag() {
      return auteurModificationFlag;
    }
  }

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
  }

  public static Integer SRID_PARAM = 2154;
  public static final Integer SRID_4326 = 4326;

  public static final String DOSSIER_DOC_HYDRANT = "DOSSIER_DOC_HYDRANT";

  public enum TypeDocument {
    TYPE_DOCUMENT_HYDRANT("HYDRANT");
    private final String typeDocument;

    TypeDocument(String typeDocument) {
      this.typeDocument = typeDocument;
    }

    public String getTypeDocument() {
      return typeDocument;
    }
  }

  /** Clé de la table "Parametre" pour la gestion des agents dans l'application mobile */
  public static final String GESTION_AGENT = "GESTION_AGENT";

  /** Paramètres utilisés dans le cadre de l'appli mobile */
  public static final String PARAMETRE_CARACTERISTIQUE_PIBI = "CARACTERISTIQUE_PIBI";

  public static final String PARAMETRE_CARACTERISTIQUE_PENA = "CARACTERISTIQUE_PENA";
  public static final String PARAMETRE_DUREE_VALIDITE_TOKEN = "DUREE_VALIDITE_TOKEN";
  public static final String PARAMETRE_MODE_DECONNECTE = "MODE_DECONNECTE";
  public static final String PARAMETRE_MDP_ADMINISTRATEUR = "MDP_ADMINISTRATEUR";
  public static final String PARAMETRE_BRIDAGE_PHOTO = "BRIDAGE_PHOTO";

  // Clé du paramètre SRID stocké dans la table remocra.parametre
  public static final String CLE_SRID = "SRID";
}
