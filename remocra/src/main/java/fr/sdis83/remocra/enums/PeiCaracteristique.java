package fr.sdis83.remocra.enums;

import java.util.ArrayList;
import java.util.List;

// TODO Temps que nous n'avons pas de commons bien penser a répliquer dans l'api

/**
 * Cette énumération représente les caractéristiques associées à un PEI (Point d'Eau Incendie).
 * Chaque valeur est identifiée par un code et décrite par un libellé. Chaque valeur peut être
 * spécifique au PIBI ou au PENA ou pour les deux (GENERAL) (cas par défaut)
 */
public enum PeiCaracteristique {
  AUTORITE_POLICE("autoritePolice", "Autorité de police DECI"),
  DATE_RECEPTION("dateReception", "Date de réception"),
  MAINTENANCE_CTP("maintenanceCtp", "Maintenance et CTP"),
  NATURE_PEI("naturePei", "Nature du PEI"),
  SERVICE_PUBLIC("servicePublic", "ServicePublic DECI"),
  TYPE_DECI("typeDeci", "Type de DECI"),
  TYPE_PEI("typePei", "Type du PEI"),
  // Localisation
  COMPLEMENT("complement", "Complément d'adresse"),

  // Caractéristiques techniques
  // PIBI
  DEBIT("debit", "Débit", TypeCaracteristique.PIBI),
  DIAMETRE_NOMINAL("diametreNominal", "Diamètre nominal", TypeCaracteristique.PIBI),

  // PENA
  CAPACITE("capacite", "Capacité", TypeCaracteristique.PENA);

  private final String code;
  private final String libelle;
  private final TypeCaracteristique typeCaracteristique;

  /**
   * Constructeur de l'énumération PeiCaracteristique.
   *
   * @param code Le code de la caractéristique.
   * @param libelle Le libellé (description) de la caractéristique.
   */
  PeiCaracteristique(String code, String libelle) {
    this(code, libelle, TypeCaracteristique.GENERAL);
  }

  /**
   * @param code code utilisable par l'appli
   * @param libelle Texte intelligible utilisable par l'affichage
   * @param typeCaracteristique PENA, PIBI, GENERAL
   */
  PeiCaracteristique(String code, String libelle, TypeCaracteristique typeCaracteristique) {
    this.code = code;
    this.libelle = libelle;
    this.typeCaracteristique = typeCaracteristique;
  }

  /**
   * Obtient le code de la caractéristique.
   *
   * @return Le code de la caractéristique.
   */
  public String getCode() {
    return code;
  }

  /**
   * Obtient le libellé (description) de la caractéristique.
   *
   * @return Le libellé de la caractéristique.
   */
  public String getLibelle() {
    return libelle;
  }

  /**
   * Convertit une valeur de chaîne en une instance de l'énumération PeiCaracteristique.
   *
   * @param stringValue La valeur sous forme de chaîne à convertir en une instance d'énumération.
   * @return L'instance de PeiCaracteristique correspondant à la chaîne donnée.
   * @throws IllegalArgumentException Si aucune correspondance n'est trouvée pour la chaîne donnée.
   */
  public static PeiCaracteristique fromString(String stringValue) {

    for (PeiCaracteristique caracteristique : PeiCaracteristique.values()) {
      if (caracteristique.code.equalsIgnoreCase(stringValue)) {
        return caracteristique;
      }
    }

    throw new IllegalArgumentException(
        "PeiCaracteristique : valeur '" + stringValue + "' non trouvée");
  }

  public enum TypeCaracteristique {
    GENERAL,
    PIBI,
    PENA
  }

  public static List<PeiCaracteristique> getValuesByType(TypeCaracteristique typeCaracteristique) {

    List<PeiCaracteristique> values = new ArrayList<>();

    for (PeiCaracteristique peiCaracteristique : PeiCaracteristique.values()) {

      if (peiCaracteristique.typeCaracteristique.equals(typeCaracteristique)
          || TypeCaracteristique.GENERAL.equals(peiCaracteristique.typeCaracteristique)) {

        values.add(peiCaracteristique);
      }
    }

    return values;
  }
}
