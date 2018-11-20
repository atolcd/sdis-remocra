package fr.sdis83.remocra.db.converter;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.Point;

/**
 * Binding Jooq pour les géométries de type LineString.
 */
public class PointBinding extends AbstractGeometryBinding<Point> {

  /**
   * Constructeur par défaut.
   */
  public PointBinding() {
    super(Point.class);
  }

  @Override protected Point cast(Geometry geometry) {
    return (Point) geometry;
  }
}