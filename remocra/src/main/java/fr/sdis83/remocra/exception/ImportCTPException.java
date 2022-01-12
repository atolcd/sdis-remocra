package fr.sdis83.remocra.exception;

import com.fasterxml.jackson.databind.node.ObjectNode;

public class ImportCTPException extends Exception {

  private String codeErreur;

  private ObjectNode data;

  public ImportCTPException(String codeErreur, ObjectNode data) {
    this.codeErreur = codeErreur;
    this.data = data;
  }

  public String getCodeErreur() {
    return this.codeErreur;
  }

  public ObjectNode getData() {
    return this.data;
  }
}
