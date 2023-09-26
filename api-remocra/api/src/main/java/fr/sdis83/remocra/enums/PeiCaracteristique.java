package fr.sdis83.remocra.enums;

import java.util.Arrays;
import java.util.Optional;

public enum PeiCaracteristique {
  // Général
  TYPE_PEI("typePei", "Type du PEI"),
  NATURE_PEI("naturePei", "Nature du PEI"),
  AUTORITE_POLICE("autoritePolice", "Autorité de police DECI"),
  TYPE_DECI("typeDeci", "Type de DECI"),
  SERVICE_PUBLIC("servicePublic", "ServicePublic DECI"),
  MAINTENANCE_CTP("maintenanceCtp", "Maintenance et CTP"),
  // Localisation

  // Caractéristiques techniques
  DIAMETRE_NOMINAL("diametreNominal", "Diamètre nominal"),

  // TODO et bien plus encoooore
  COMPLEMENT("complement", "Complément d'adresse"),

  DATE_RECEPTION("dateReception", "Date de réception");
  ;

  private final String code;
  private final String libelle;

  PeiCaracteristique(String code, String libelle) {
    this.code = code;
    this.libelle = libelle;
  }

  public String getCode() {
    return code;
  }

  public String getLibelle() {
    return libelle;
  }

  public static PeiCaracteristique fromString(String stringValue) {
    Optional<PeiCaracteristique> opt =
        Arrays.stream(PeiCaracteristique.values())
            .filter(it -> it.code.equals(stringValue))
            .findFirst();
    if (opt.isPresent()) return opt.get();
    throw new IllegalArgumentException(
        "PeiCaracteristique : valeur '" + stringValue + "' non trouvée");
  }
}
