package fr.sdis83.remocra.authn;

import java.util.Collections;
import java.util.List;

/**
 * Classe utilitaire permettant de vérifier qu'une version de l'application mobile est bien
 * autorisée à discuter avec une version du serveur. A chaque nouvelle version compatible de l'appli
 * mobile, il conviendra d'ajouter une entrée dans [MobileVersion]
 */
public class CompatibleVersions {

  // Versions autorisées pour l'appli mobile
  private final List<String> mobileCompatibleVersions =
      Collections.singletonList(MobileVersion.M2_0_beta2.version);

  public CompatibleVersions() {}

  /**
   * Permet de savoir si la version de l'application mobile est compatible avec celle du serveur
   *
   * @param mobileVersionString : la version de la tablette
   */
  public void checkCompat(String mobileVersionString) {
    if (mobileVersionString == null || mobileVersionString.isEmpty()) {
      throw new IllegalArgumentException("MobileVersion nulle");
    }

    if (!mobileCompatibleVersions.contains(mobileVersionString)) {
      throw new IllegalArgumentException("Version non compatible : " + mobileVersionString);
    }
  }

  enum MobileVersion {
    M2_0_beta2("2.0-beta2");

    public final String version;

    MobileVersion(String s) {
      this.version = s;
    }
  }
}
