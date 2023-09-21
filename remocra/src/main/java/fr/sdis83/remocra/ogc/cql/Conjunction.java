package fr.sdis83.remocra.ogc.cql;

public enum Conjunction {
  AND("AND"),
  OR("OR");

  final String value;

  Conjunction(String value) {
    this.value = value;
  }

  public String toString() {
    return this.value;
  }
}
