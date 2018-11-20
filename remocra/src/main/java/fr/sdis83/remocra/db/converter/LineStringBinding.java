package fr.sdis83.remocra.db.converter;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.LineString;

/**
 * Binding Jooq pour les géométries de type LineString.
 */
public class LineStringBinding extends AbstractGeometryBinding<LineString> {

  /**
   * Constructeur par défaut.
   */
  public LineStringBinding() {
    super(LineString.class);
  }

  @Override protected LineString cast(Geometry geometry) {
    return (LineString) geometry;
  }
}