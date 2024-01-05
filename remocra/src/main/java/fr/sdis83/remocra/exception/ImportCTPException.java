package fr.sdis83.remocra.exception;

import fr.sdis83.remocra.enums.TypeErreurImportCtp;
import fr.sdis83.remocra.usecase.importctp.LigneImportCtpData;

public class ImportCTPException extends Exception {

  private TypeErreurImportCtp typeErreur;

  private LigneImportCtpData data;

  public ImportCTPException(TypeErreurImportCtp typeErreur, LigneImportCtpData data) {
    this.typeErreur = typeErreur;
    this.data = data;
  }

  public TypeErreurImportCtp getTypeErreur() {
    return typeErreur;
  }

  public LigneImportCtpData getData() {
    return this.data;
  }
}
