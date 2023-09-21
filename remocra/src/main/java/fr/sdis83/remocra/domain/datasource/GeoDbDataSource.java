package fr.sdis83.remocra.domain.datasource;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Point;
import geodb.GeoDB;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import javax.annotation.PostConstruct;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;

public class GeoDbDataSource extends SimpleDriverDataSource {

  @PostConstruct
  public void initGeoDb() {

    Connection connection = null;
    try {
      connection = getConnection();
      GeoDB.InitGeoDB(connection);

      // Ajout des fonctions manquantes à GeoDB
      this.addMissingFunctions(connection);

    } catch (SQLException e) {
      throw new RuntimeException("SQL exception occurred acquiring connection", e);
    } finally {
      if (connection != null) {
        try {
          connection.close();
        } catch (SQLException e) {
        }
      }
    }
  }

  /**
   * Ajoute les fonctions nécessaires au bon fonctionnement de l'application avec GeoDB
   *
   * @param cx
   * @throws SQLException
   */
  void addMissingFunctions(Connection cx) throws SQLException {
    try {
      Statement st = cx.createStatement();
      try {
        try {
          st.execute(
              "CREATE ALIAS ST_Distance_Sphere FOR \"fr.sdis83.remocra.domain.datasource.GeoDbDataSource.ST_Distance_Sphere\"");
        } catch (SQLException e) {
          // tant pis !
        }
      } finally {
        st.close();
      }
    } catch (Exception e) {
      throw (SQLException)
          new SQLException("Pb GeoDB : ajout des fonctions manquantes").initCause(e);
    }
  }

  /**
   * Retourne la distance en mètres entre deux géométries.
   *
   * @param wkb1 Première géométrie
   * @param wkb2 Seconde géométrie
   * @return distance en mètres
   */
  public static double ST_Distance_Sphere(byte[] wkb1, byte[] wkb2) {
    if (wkb1 == null || wkb2 == null) {
      return -1;
    }

    Geometry g1 = GeoDB.gFromWKB(wkb1);
    Geometry g2 = GeoDB.gFromWKB(wkb2);

    if (g1 instanceof Point && g2 instanceof Point) {
      // Calcul amélioré
      return ST_Distance_SphereInternal((Point) g1, (Point) g2);
    }
    // TODO éventuellement, utiliser le Centroid ?
    double distDegrees = g1.distance(g2);
    double distMeters = distDegrees * (Math.PI / 180d) * 6378137d;
    return distMeters;
  }

  public static double ST_Distance_SphereInternal(Point p1, Point p2) {
    return ST_Distance_SphereInternal(p1.getX(), p1.getY(), p2.getX(), p2.getY());
  }

  public static double ST_Distance_SphereInternal(
      double lon1, double lat1, double lon2, double lat2) {
    return ST_Distance_SphereInternal(lon1, lat1, lon2, lat2, EARTH_RADIUS, METERCONVERSION);
  }

  static final double EARTH_RADIUS = 3958.75d;
  static final double METERCONVERSION = 1609.344d;

  public static double ST_Distance_SphereInternal(
      double lon1,
      double lat1,
      double lon2,
      double lat2,
      double earthRadius,
      double meterConversion) {

    double dLat = Math.toRadians(lat2 - lat1);
    double dLng = Math.toRadians(lon2 - lon1);
    double a =
        Math.sin(dLat / 2) * Math.sin(dLat / 2)
            + Math.cos(Math.toRadians(lat1))
                * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLng / 2)
                * Math.sin(dLng / 2);
    double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    double dist = earthRadius * c;
    return dist * meterConversion;
  }
}
