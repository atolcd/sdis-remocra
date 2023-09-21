package fr.sdis83.remocra.db.converter;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Polygon;

/** Binding Jooq pour les géométries de type LineString. */
public class PolygonBinding extends AbstractGeometryBinding<Polygon> {

  /** Constructeur par défaut. */
  public PolygonBinding() {
    super(Polygon.class);
  }

  @Override
  protected Polygon cast(Geometry geometry) {
    return (Polygon) geometry;
  }
}
