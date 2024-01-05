package fr.sdis83.remocra.usecase.importctp;

import java.util.Collection;

/** Permet de stocker les infos relatives à un import CTP */
public class ImportCtpData {
  Collection<LigneImportCtpData> bilanVerifications;

  public Collection<LigneImportCtpData> getBilanVerifications() {
    return bilanVerifications;
  }

  public void setBilanVerifications(Collection<LigneImportCtpData> bilanVerifications) {
    this.bilanVerifications = bilanVerifications;
  }
}
