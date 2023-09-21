package fr.sdis83.remocra.ogc.cql;

public abstract class Statement {
  boolean not = false;

  public String toString() {
    return (not ? "NOT " : "") + toCQL();
  }

  public abstract String toCQL();
}
