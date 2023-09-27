package fr.sdis83.remocra.enums;

import java.util.ArrayList;
import java.util.List;

/**
 * Cette énumération représente les caractéristiques associées à un PEI (Point d'Eau Incendie).
 * Chaque valeur est identifiée par un code et décrite par un libellé.
 */
public enum PeiCaracteristique {
  TYPE_PEI("typePei", "Type du PEI"),
  NATURE_PEI("naturePei", "Nature du PEI"),
  AUTORITE_POLICE("autoritePolice", "Autorité de police DECI"),
  TYPE_DECI("typeDeci", "Type de DECI"),
  SERVICE_PUBLIC("servicePublic", "ServicePublic DECI"),
  MAINTENANCE_CTP("maintenanceCtp", "Maintenance et CTP"),
  // Localisation

  // Caractéristiques techniques
  DIAMETRE_NOMINAL("diametreNominal", "Diamètre nominal", TypeCaracteristique.PIBI),

  // TODO et bien plus encoooore
  COMPLEMENT("complement", "Complément d'adresse"),

  DATE_RECEPTION("dateReception", "Date de réception");

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
   * @param code
   * @param libelle
   * @param typeCaracteristique
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
