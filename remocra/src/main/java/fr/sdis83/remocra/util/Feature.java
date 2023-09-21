package fr.sdis83.remocra.util;

import java.util.HashMap;
import java.util.Map;

public class Feature {

  private Long id;
  private Map<String, Object> properties;
  private String geometry;

  public Feature(Long id, String geometry) {
    this.properties = new HashMap<String, Object>();
    this.id = id;
    this.geometry = geometry;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getType() {
    return "Feature";
  }

  public Map<String, Object> getProperties() {
    return this.properties;
  }

  public void addProperty(String key, Object value) {
    this.properties.put(key, value);
  }

  public String getGeometry() {
    return geometry;
  }
}
