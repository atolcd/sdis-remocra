package fr.sdis83.remocra.db.converter;


import com.vividsolutions.jts.geom.Geometry;

/**
 * Binding Jooq pour les géométries.
 */
public class GeometryBinding extends AbstractGeometryBinding<Geometry> {

  /**
   * Constructeur par défaut.
   */
  public GeometryBinding() {
    super(Geometry.class);
  }

  @Override protected Geometry cast(Geometry geometry) {
    return geometry;
  }
}