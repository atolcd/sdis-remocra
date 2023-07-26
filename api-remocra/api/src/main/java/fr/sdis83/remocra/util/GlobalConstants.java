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

        AuteurModificationFlag(String auteurModificationFlag ) {
            this.auteurModificationFlag = auteurModificationFlag;
        }

        public String getAuteurModificationFlag() {
            return auteurModificationFlag;
        }
    }
}
