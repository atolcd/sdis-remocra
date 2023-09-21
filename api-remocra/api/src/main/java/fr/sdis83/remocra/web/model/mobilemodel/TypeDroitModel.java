package fr.sdis83.remocra.web.model.mobilemodel;

public class TypeDroitModel {
  String id;

  String code;

  String categorie;

  public TypeDroitModel(String id, String code, String categorie) {
    this.id = id;
    this.code = code;
    this.categorie = categorie;
  }

  public String getId() {
    return id;
  }

  public String getCode() {
    return code;
  }

  public String getCategorie() {
    return categorie;
  }
}
