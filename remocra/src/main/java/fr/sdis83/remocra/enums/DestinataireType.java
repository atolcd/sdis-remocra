package fr.sdis83.remocra.enums;

public enum DestinataireType {
  UTILISATEUR("Utilisateur"),
  ORGANISME("Organisme"),
  CONTACT_GESTIONNAIRE("Contact Gestionnaire"),
  CONTACT_ORGANISME("Contact Organisme");

  private final String type;

  DestinataireType(String type) {
    this.type = type;
  }

  public String getType() {
    return type;
  }
}
