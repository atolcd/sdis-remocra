package fr.sdis83.remocra.authn;

import org.jooq.EnumType;

public enum UserRoles {
  RECEVOIR(RoleTypes.RECEVOIR),
  TRANSMETTRE(RoleTypes.TRANSMETTRE),
  ADMINISTRER(RoleTypes.ADMINISTRER);

  public class RoleTypes{
    public static final String RECEVOIR = "RECEVOIR";
    public static final String TRANSMETTRE = "TRANSMETTRE";
    public static final String ADMINISTRER = "ADMINISTRER";
  }

  private final String label;

  private UserRoles(String label) {
    this.label = label;
  }

  public String toString() {
    return this.label;
  }
}
