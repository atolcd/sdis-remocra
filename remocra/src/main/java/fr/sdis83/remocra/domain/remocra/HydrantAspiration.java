package fr.sdis83.remocra.domain.remocra;

import java.math.BigDecimal;
import java.math.RoundingMode;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;

import fr.sdis83.remocra.GlobalConstants;
import fr.sdis83.remocra.util.Feature;
import fr.sdis83.remocra.util.GeometryUtil;
import org.cts.IllegalCoordinateException;
import org.cts.crs.CRSException;
import org.hibernate.annotations.Type;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.tostring.RooToString;

import com.vividsolutions.jts.geom.Point;

@RooJavaBean
@RooToString
public class HydrantAspiration {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column
    private String numero;

    @Column
    private Boolean normalise;

    @Column
    private Boolean hauteur;

    @ManyToOne
    private TypeHydrantAspiration typeAspiration;

    @Column
    private Boolean deporte;

    @Column
    @Type(type = "org.hibernate.spatial.GeometryType")
    private Point geometrie;

    @ManyToOne
    @NotNull
    private HydrantPena pena;

    @Version
    @Column(name = "version", columnDefinition = "INTEGER default 1")
    private Integer version;

    public Feature toFeature() throws CRSException, IllegalCoordinateException {
        Feature feature = new Feature(this.id, null);
        feature.addProperty("longitude", this.getLongitude());
        feature.addProperty("latitude", this.getLatitude());
        return feature;
    }

    public Double getLongitude() throws CRSException, IllegalCoordinateException {
        if(this.getGeometrie() == null)
            return null;
        Point p = this.getGeometrie();
        double[] coordonneConvert = GeometryUtil.transformCordinate(p.getX(), p.getY(), GlobalConstants.SRID_2154.toString(), "4326");
        double longitude = BigDecimal.valueOf(coordonneConvert[0]).setScale(5, RoundingMode.HALF_UP).doubleValue();
        return longitude;
    }

    public Double getLatitude() throws CRSException, IllegalCoordinateException {
        if(this.getGeometrie() == null)
            return null;
        Point p = this.getGeometrie();
        double[] coordonneConvert = GeometryUtil.transformCordinate(p.getX(), p.getY(), GlobalConstants.SRID_2154.toString(), "4326");
        double latitude = BigDecimal.valueOf(coordonneConvert[1]).setScale(5, RoundingMode.HALF_UP).doubleValue();
        return latitude;
    }




}
