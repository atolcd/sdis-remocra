package fr.sdis83.remocra.usecase.utils;

import fr.sdis83.remocra.util.GlobalConstants;
import java.util.stream.Stream;

/** Classe regroupant les utilitaires ayant attrait aux cas d'utilisation des PEI */
public class UseCaseUtils {
  /**
   * Vérifie si l'organisme connecté est le profil API_ADMIN TODO implémenter
   *
   * @param organisme OrganismeIdType
   * @return boolean
   */
  public static boolean isApiAdmin(OrganismeIdType organisme) {
    // Not yet implemented
    return false;
  }

  public static boolean isServicePublicDECI(OrganismeIdType organisme) {
    return GlobalConstants.COMMUNE.equalsIgnoreCase(organisme.typeOrganisme)
        || GlobalConstants.EPCI.equalsIgnoreCase(organisme.typeOrganisme);
  }

  public static boolean isMaintenanceDECI(OrganismeIdType organisme) {
    return Stream.of(
            GlobalConstants.SERVICE_EAUX,
            GlobalConstants.PRESTATAIRE_TECHNIQUE,
            GlobalConstants.COMMUNE,
            GlobalConstants.EPCI)
        .anyMatch(it -> it.equalsIgnoreCase(organisme.typeOrganisme));
  }

  /**
   * Fonction utilitaire permettant de savoir si l'organisme est la maintenance DECI du PEI
   *
   * @param hydrantMaintenanceDeciId Id de l'organisme reponsable de la "maintenance DECI"
   * @param organisme OrganismeIdType
   * @return boolean
   */
  public static boolean isMaintenanceDECI(
      Long hydrantMaintenanceDeciId, OrganismeIdType organisme) {
    return hydrantMaintenanceDeciId != null
        && hydrantMaintenanceDeciId.equals(organisme.getIdOrganisme())
        && isMaintenanceDECI(organisme);
  }

  /**
   * Fonction utilitaire permettant de savoir si l'organisme est le service public DECI du PEI
   *
   * @param hydrantServicePublicDeciId Id de l'organisme public marqué comme "service public DECI"
   * @param organisme OrganismeIdType
   * @return boolean
   */
  public static boolean isServicePublicDECI(
      Long hydrantServicePublicDeciId, OrganismeIdType organisme) {
    return hydrantServicePublicDeciId != null
        && hydrantServicePublicDeciId.equals(organisme.idOrganisme)
        && (isServicePublicDECI(organisme));
  }

  /**
   * Fonction utilitaire permettant de savoir si l'organisme est le service des eaux du PEI
   *
   * @param hydrantServiceEauxId Id du service des eaux du PEI
   * @param organisme OrganismeIdType
   * @return boolean
   */
  public static boolean isServiceEaux(Long hydrantServiceEauxId, OrganismeIdType organisme) {
    return hydrantServiceEauxId != null
        && hydrantServiceEauxId.equals(organisme.idOrganisme)
        && GlobalConstants.SERVICE_EAUX.equalsIgnoreCase(organisme.typeOrganisme);
  }

  /** Classe de représentation des propriétés "utiles" d'un organisme utilisateur de l'API */
  public static class OrganismeIdType {
    private final Long idOrganisme;
    private final String typeOrganisme;

    public OrganismeIdType(Long idOrganisme, String typeOrganisme) {
      this.idOrganisme = idOrganisme;
      this.typeOrganisme = typeOrganisme;
    }

    public Long getIdOrganisme() {
      return idOrganisme;
    }

    public String getTypeOrganisme() {
      return typeOrganisme;
    }
  }
}
