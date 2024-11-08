package fr.sdis83.remocra.enums;

public enum TypeErreurImportCtp {
  ERR_FICHIER_INNAC("ERR_FICHIER_INNAC"),
  ERR_MAUVAIS_EXT("ERR_MAUVAIS_EXT"),
  ERR_ONGLET_ABS("ERR_ONGLET_ABS"),
  ERR_MAUVAIS_NUM_PEI("ERR_MAUVAIS_NUM_PEI"),
  INFO_IGNORE("INFO_IGNORE"),
  ERR_DEHORS_ZC("ERR_DEHORS_ZC"),
  WARN_DEPLACEMENT("WARN_DEPLACEMENT"),
  ERR_COORD_GPS("ERR_COORD_GPS"),
  WARN_DATE_ANTE("WARN_DATE_ANTE"),
  ERR_DATE_POST("ERR_DATE_POST"),
  ERR_FORMAT_DATE("ERR_FORMAT_DATE"),
  ERR_DATE_MANQ("ERR_DATE_MANQ"),
  ERR_AGENT1_ABS("ERR_AGENT1_ABS"),
  WARN_PRESS_VIDE("WARN_PRESS_VIDE"),
  ERR_PRESS_ELEVEE("ERR_PRESS_ELEVEE"),
  ERR_FORMAT_PRESS("ERR_FORMAT_PRESS"),
  WARN_DEBIT_VIDE("WARN_DEBIT_VIDE"),
  ERR_FORMAT_DEBIT("ERR_FORMAT_DEBIT"),
  WARN_DEB_PRESS_VIDE("WARN_DEB_PRESS_VIDE"),
  INFO_TRONC_DEBIT("INFO_TRONC_DEBIT"),
  ERR_ANO_INCONNU("ERR_ANO_INCONNU"),
  ERR_VISITES_MANQUANTES("ERR_VISITES_MANQUANTES"),
  ERR_VISITE_EXISTANTE("ERR_VISITE_EXISTANTE");

  private final String code;

  TypeErreurImportCtp(String code) {
    this.code = code;
  }

  public String getCode() {
    return code;
  }
}
