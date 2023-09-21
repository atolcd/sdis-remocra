package fr.sdis83.remocra.web.model;

import fr.sdis83.remocra.db.model.remocra.tables.pojos.OgcCouche;
import java.util.List;

public class OgcService {

  private Long id;

  private Long typeService;

  private String nom;

  private String description;

  private Long ogcSource;

  private List<OgcCouche> ogcCouches;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Long getTypeService() {
    return typeService;
  }

  public void setTypeService(Long typeService) {
    this.typeService = typeService;
  }

  public String getNom() {
    return nom;
  }

  public void setNom(String nom) {
    this.nom = nom;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public Long getOgcSource() {
    return ogcSource;
  }

  public void setOgcSource(Long ogcSource) {
    this.ogcSource = ogcSource;
  }

  public List<OgcCouche> getOgcCouches() {
    return ogcCouches;
  }

  public void setOgcCouches(List<OgcCouche> ogcCouches) {
    this.ogcCouches = ogcCouches;
  }
}
