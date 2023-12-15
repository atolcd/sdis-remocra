package fr.sdis83.remocra.data;

public class CleValeurClasseData {
  private final String cle;
  private final Object valeur;
  private Class<?> clazz = null;

  public String getCle() {
    return cle;
  }

  public Object getValeur() {
    return valeur;
  }

  public Class<?> getClazz() {
    return clazz;
  }

  public CleValeurClasseData(String cle, Object valeur, Class<?> clazz) {
    this.cle = cle;
    this.valeur = valeur;
    this.clazz = clazz;
  }

  public CleValeurClasseData(String cle, Object valeur) {
    this.cle = cle;
    this.valeur = valeur;
  }

  public void setClazz(Class<?> clazz) {
    this.clazz = clazz;
  }
}
