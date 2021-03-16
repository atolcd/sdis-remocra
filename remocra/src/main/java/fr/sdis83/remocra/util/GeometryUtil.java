package fr.sdis83.remocra.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.geom.PrecisionModel;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;
import com.vividsolutions.jts.simplify.TopologyPreservingSimplifier;
import fr.sdis83.remocra.domain.remocra.Commune;
import fr.sdis83.remocra.exception.BusinessException;
import org.cts.CRSFactory;
import org.cts.IllegalCoordinateException;
import org.cts.crs.CRSException;
import org.cts.crs.CoordinateReferenceSystem;
import org.cts.crs.GeodeticCRS;
import org.cts.op.CoordinateOperation;
import org.cts.op.CoordinateOperationFactory;
import org.cts.registry.EPSGRegistry;
import org.jboss.logging.Logger;

public class GeometryUtil {

    private final static Logger logger = Logger.getLogger(Commune.class);

    private static CRSFactory cRSFactory = null;

    public static String wktFromBBox(String bbox) {
        if (bbox == null) {
            return null;
        }
        String[] coord = bbox.split(",");
        return MessageFormat.format("POLYGON(({0} {1},{0} {3},{2} {3},{2} {1},{0} {1}))", coord[0], coord[1], coord[2], coord[3]);
    }

    public static Geometry geometryFromBBox(String bbox) {
        String polygon = wktFromBBox(bbox);
        if (polygon == null) {
            return null;
        }
        WKTReader fromText = new WKTReader();
        Geometry geometry = null;
        try {
            geometry = fromText.read(polygon);
        } catch (ParseException e) {
            throw new RuntimeException("Not a WKT String:" + polygon);
        }
        geometry.setSRID(sridFromBBox(bbox));
        return geometry;
    }

    public static String bboxFromGeometry(Geometry geometry) {
        if (geometry == null) {
            return null;
        }
        Envelope env = geometry.getEnvelopeInternal();
        return String.format("EPSG:2154;%s,%s,%s,%s", env.getMinX(), env.getMinY(), env.getMaxX(), env.getMaxY());
    }

    public static Integer sridFromBBox(String bbox) {
        Integer srid = 2154;
        if (bbox != null) {
            String[] coord = bbox.split(",");
            if (coord.length == 5) {
                srid = sridFromEpsgCode(coord[4]);
            }
        }
        return srid;
    }

    public static Integer sridFromEpsgCode(String coupleOrCode) {
        return Integer.valueOf(coupleOrCode.split(":")[1]);
    }

    public static Integer sridFromGeom(String coupleOrCode) {
        return Integer.valueOf(coupleOrCode.split("=")[1]);
    }

    public static Geometry toGeometry(String wkt, Integer srid) {
        WKTReader fromText = new WKTReader();
        Geometry geometry = null;
        try {
            geometry = fromText.read(wkt);
            geometry.setSRID(srid);
        } catch (ParseException e) {
            throw new RuntimeException("Not a WKT string:" + wkt);
        }
        return geometry;
    }

    public static CRSFactory getCRSFactory() {
        if (cRSFactory == null) {
            cRSFactory = new CRSFactory();
            cRSFactory.getRegistryManager().addRegistry(new EPSGRegistry());
        }
        return cRSFactory;
    }

    public static Point createPoint(double longitude, double latitude, String projFrom, String projTo) throws CRSException, IllegalCoordinateException {
        double[] coord = transformCordinate(longitude, latitude, projFrom, projTo);
        GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 2154);
        return geometryFactory.createPoint(new Coordinate(coord[0], coord[1]));
    }

    public static double[] transformCordinate(double longitude, double latitude, String projFrom, String projTo) throws CRSException, IllegalCoordinateException {
        CoordinateReferenceSystem fromCoordRefSys = getCRSFactory().getCRS("EPSG:" + projFrom);
        CoordinateReferenceSystem toCoordRefSys = getCRSFactory().getCRS("EPSG:" + projTo);
        CoordinateOperation transformation = CoordinateOperationFactory.createCoordinateOperations((GeodeticCRS) fromCoordRefSys, (GeodeticCRS) toCoordRefSys).get(0);
        return transformation.transform(new double[] { longitude, latitude });
    }

    /**
     * Recherche des coordonnées DFCI. Réalisé en JDBC natif pour éviter une
     * gestion de transactions par Spring qui annule toutes les requêtes
     * suivantes dans le cas où la table n'existe pas (cas hors SDIS 83 par
     * exemple)
     * 
     * @param ds
     * @param geom
     * @return
     * @throws BusinessException
     */
    public static String findCoordDFCIFromGeom(DataSource ds, Geometry geom) throws BusinessException {
        Connection cx = null;
        Statement st = null;
        ResultSet rs = null;
        try {
            cx = ds.getConnection();
            st = cx.createStatement();
            st.execute(
                    "select coordonnee_complete from remocra_referentiel.carro_dfci where sous_type = 'CARRES INTRA 2x2 KM' and st_dwithin (geometrie, st_transform(st_geomfromtext('"
                            + geom.toText() + "', " + geom.getSRID() + "), 2154), 0) = true");
            rs = st.getResultSet();
            rs.next();
            return rs.getString("coordonnee_complete");
        } catch (Exception e) {
            //
        } finally {
            try {
                if (rs != null)
                    rs.close();
                if (st != null && !st.isClosed())
                    st.close();
                if (cx != null && !cx.isClosed())
                    cx.close();
            } catch (Exception e1) {
                //
            }
        }
        logger.info("Impossible de récupérer les coordonnées DFCI (table remocra_referentiel.carro_dfci)");
        throw new BusinessException("Impossible de récupérer les coordonnées DFCI");
    }

    public static Geometry getMultiGeometry(Geometry geometry) {
        GeometryFactory geometryFactory = new GeometryFactory();
        Geometry outputGeometry = null;

        if (geometry instanceof Point) {
            List<Point> pointList = new ArrayList<Point>();
            pointList.add((Point) geometry);
            outputGeometry = geometryFactory.createMultiPoint(GeometryFactory.toPointArray(pointList));
        } else if (geometry instanceof LineString) {
            List<LineString> lineStringList = new ArrayList<LineString>();
            lineStringList.add((LineString) geometry);
            outputGeometry = geometryFactory.createMultiLineString(GeometryFactory.toLineStringArray(lineStringList));
        } else if (geometry instanceof Polygon) {
            List<Polygon> polygonList = new ArrayList<Polygon>();
            polygonList.add((Polygon) geometry);
            outputGeometry = geometryFactory.createMultiPolygon(GeometryFactory.toPolygonArray(polygonList));
        } else {
            outputGeometry = geometry;
        }

        outputGeometry.setSRID(geometry.getSRID());
        return outputGeometry;
    }

    public static Geometry simplifyGeometry(Geometry input) {
        return simplifyGeometry(input, 1.0, null);
    }

    public static Geometry simplifyGeometry(Geometry input, Double tolerance) {
        return simplifyGeometry(input, tolerance, null);
    }
    public static Geometry simplifyGeometry(Geometry input, Double tolerance, Integer srid) {
        TopologyPreservingSimplifier simplifier = new TopologyPreservingSimplifier(input);
        simplifier.setDistanceTolerance(tolerance);
        Geometry returned = simplifier.getResultGeometry();
        returned.setSRID(srid!=null?srid:input.getSRID());
        return returned;
    }

    /**
     * Convertit une coordonnée WSG84 en degrés décimaux en
     * @param coordDegres
     * @return
     */
    public static String convertDegresDecimauxToSexagesimaux(double coordDegres){
        StringBuilder coords = new StringBuilder();
        BigDecimal bd = new BigDecimal(String.valueOf(coordDegres));
        coords.append(bd.intValue()+"°");
        bd = bd.remainder(BigDecimal.ONE);

        bd = bd.multiply(new BigDecimal("60"));
        coords.append(bd.intValue()+"'");
        bd = bd.remainder(BigDecimal.ONE);

        bd = bd.multiply(new BigDecimal("60"));
        bd = bd.setScale(4, RoundingMode.HALF_UP);
        coords.append(bd+"''");
        return coords.toString();
    }

    public static double convertDegresSexagesimauxToDecimaux(String coordSexa) {

        String[] parts = coordSexa.split("°");
        String[] parts2 = parts[1].split("'");

        BigDecimal minutes = new BigDecimal(parts2[0]);
        BigDecimal secondes = new BigDecimal(parts2[1]);

        BigDecimal coord = new BigDecimal(parts[0]);
        minutes = minutes.divide((new BigDecimal("60")), 14, RoundingMode.HALF_UP);
        secondes = secondes.divide((new BigDecimal("3600")), 14, RoundingMode.HALF_UP);
        coord = coord.add(minutes).add(secondes);

        return coord.doubleValue();
    }
}
