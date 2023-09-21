package fr.sdis83.remocra.util;

public class Layer {

  public static enum AccessLevel {
    OPEN(3),
    AUTH_ALL(2),
    AUTH_ZONE(1),
    NONE(0);

    private final int level;

    AccessLevel(int level) {
      this.level = level;
    }

    public int getLevel() {
      return level;
    }

    public static AccessLevel fromLevel(int level) {
      if (level >= AccessLevel.OPEN.getLevel()) {
        return AccessLevel.OPEN;
      } else if (level >= AccessLevel.AUTH_ALL.getLevel()) {
        return AccessLevel.AUTH_ALL;
      } else if (level >= AccessLevel.AUTH_ZONE.getLevel()) {
        return AccessLevel.AUTH_ZONE;
      } else {
        return AccessLevel.NONE;
      }
    }

    public static AccessLevel getMostRestricted(AccessLevel al1, AccessLevel al2) {
      return AccessLevel.fromLevel(Math.min(al1.getLevel(), al2.getLevel()));
    }

    public static AccessLevel getMostRestricted(AccessLevel... accessLevels) {
      AccessLevel returned = AccessLevel.OPEN;
      for (AccessLevel al : accessLevels) {
        returned = getMostRestricted(returned, al);
      }
      return returned;
    }
  }

  private String name = null;
  private boolean open = false;
  private String[] profils = null;
  private String[] profilslimites = null;

  public Layer() {}

  public Layer(String name, boolean open, String[] profils, String[] profilsLimites) {
    setName(name);
    setOpen(open);
    setProfils(profils);
    setProfilslimites(profilsLimites);
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public boolean isOpen() {
    return open;
  }

  public void setOpen(boolean open) {
    this.open = open;
  }

  public String[] getProfils() {
    return profils;
  }

  public void setProfils(String[] profils) {
    this.profils = profils;
  }

  public String[] getProfilslimites() {
    return profilslimites;
  }

  public void setProfilslimites(String[] profilslimites) {
    this.profilslimites = profilslimites;
  }

  public AccessLevel getAccessLevelProfil(String profil) {
    if (isOpen()) {
      return AccessLevel.OPEN;
    }
    if (hasProfil(profil)) {
      return AccessLevel.AUTH_ALL;
    }
    if (hasProfillimite(profil)) {
      return AccessLevel.AUTH_ZONE;
    }
    return AccessLevel.NONE;
  }

  public boolean hasProfil(String profil) {
    return hasProfil(profils, "*") || hasProfil(profils, profil);
  }

  public boolean hasProfillimite(String profil) {
    return hasProfil(profilslimites, "*") || hasProfil(profilslimites, profil);
  }

  protected boolean hasProfil(String[] a, String o) {
    if (a == null) {
      return false;
    }
    for (String p : a) {
      if (p.equals(o)) {
        return true;
      }
    }
    return false;
  }
}
