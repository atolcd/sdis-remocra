package fr.sdis83.remocra.data;

/** TODO verrue pour gérer les appels au front via extjs */
public class ParamConfWithClDisplay {
  private String id;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  private String cle;
  private String description;
  private String valeur;
  private Integer version;
  private String nomgroupe;
  private String clDisplay;

  public String getCle() {
    return cle;
  }

  public void setCle(String cle) {
    this.cle = cle;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getValeur() {
    return valeur;
  }

  public void setValeur(String valeur) {
    this.valeur = valeur;
  }

  public Integer getVersion() {
    return version;
  }

  public void setVersion(Integer version) {
    this.version = version;
  }

  public String getNomgroupe() {
    return nomgroupe;
  }

  public void setNomgroupe(String nomgroupe) {
    this.nomgroupe = nomgroupe;
  }

  public String getClDisplay() {
    return clDisplay;
  }

  public void setClDisplay(String clDisplay) {
    this.clDisplay = clDisplay;
  }
}
