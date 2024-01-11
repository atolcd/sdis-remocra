package fr.sdis83.remocra.usecase.etude;

public class DocumentEtudeData {
  private Long idDocument;
  private String codeDocument;
  private String fichierDocument;
  private String nomEtudeDocument;

  public Long getIdDocument() {
    return idDocument;
  }

  public void setIdDocument(Long idDocument) {
    this.idDocument = idDocument;
  }

  public String getCodeDocument() {
    return codeDocument;
  }

  public void setCodeDocument(String codeDocument) {
    this.codeDocument = codeDocument;
  }

  public String getFichierDocument() {
    return fichierDocument;
  }

  public void setFichierDocument(String fichierDocument) {
    this.fichierDocument = fichierDocument;
  }

  public String getNomEtudeDocument() {
    return nomEtudeDocument;
  }

  public void setNomEtudeDocument(String nomEtudeDocument) {
    this.nomEtudeDocument = nomEtudeDocument;
  }
}
