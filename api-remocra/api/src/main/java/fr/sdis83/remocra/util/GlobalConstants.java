package fr.sdis83.remocra.util;

/**
 * Constantes globales à l'API
 */
public class GlobalConstants {

  /**
   * Type prédéfini pour un organisme de type "service des eaux"
   */
  public static final String SERVICE_EAUX = "SERVICEEAUX";
  /**
   * Type prédéfini pour un organisme de type "prestataire technique"
   */
  public static final String PRESTATAIRE_TECHNIQUE = "PRESTATAIRE_TECHNIQUE";

  /**
   * Type prédéfini pour un organisme de type "commune
   */
  public static final String COMMUNE = "COMMUNE";
  /**
   * Type prédéfini pour un organisme de type "EPCI"
   */
  public static final String EPCI = "EPCI";

  /**
   * Type d'organisme "admin" ayant la main sur tous les PEI sans distinction
   */
  public static final String API_ADMIN = "API_ADMIN";

  /**
   * Enumération des types de visite, avec
   * <ul>
   *   <li>Le code tel que représenté en BDD</li>
   *   <li>Le libellé à titre informatif</li>
   * </ul>
   */
  public enum TypeVisite {
    CREATION("CREA", "Création"),
    RECEPTION("RECEP", "Réception"),
    RECONNAISSANCE("RECO",  "Reconnaissance"),
    CONTROLE("CTRL", "Contrôle"),
    NON_PROGRAMMEE("NP", "Non programmée");

    private final String codeTypeVisite;
    private final String libelleTypeVisite;

    TypeVisite(String code, String libelle) {
      codeTypeVisite = code;
      libelleTypeVisite = libelle;
    }
    public String getCode() { return codeTypeVisite; }
    public String getLibelle() { return libelleTypeVisite; }
  }

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


}
