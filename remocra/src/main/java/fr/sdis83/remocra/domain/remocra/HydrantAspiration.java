package fr.sdis83.remocra.domain.remocra;

import com.vividsolutions.jts.geom.Point;
import fr.sdis83.remocra.GlobalConstants;
import fr.sdis83.remocra.usecase.parametre.ParametreDataProvider;
import fr.sdis83.remocra.util.GeometryUtil;
import java.math.BigDecimal;
import java.math.RoundingMode;
import javax.inject.Inject;
import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;
import org.apache.log4j.Logger;
import org.cts.IllegalCoordinateException;
import org.cts.crs.CRSException;
import org.hibernate.annotations.Type;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.tostring.RooToString;

@RooJavaBean
@RooToString
public class HydrantAspiration {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;

  @Column private String numero;

  @Column private Boolean normalise;

  @Column private Boolean hauteur;

  @ManyToOne private TypeHydrantAspiration typeAspiration;

  @Column private Boolean deporte;

  @Column
  @Type(type = "org.hibernate.spatial.GeometryType")
  private Point geometrie;

  @ManyToOne @NotNull private HydrantPena pena;

  @Version
  @Column(name = "version", columnDefinition = "INTEGER default 1")
  private Integer version;

  @Transient @Inject ParametreDataProvider parametreDataProvider;

  @Transient private static final int LATITUDE = 1;
  @Transient private static final int LONGITUDE = 0;
  @Transient private final Logger logger = Logger.getLogger(getClass());

  public Double getLongitude() {
    return getLatOrLong(LONGITUDE);
  }

  public Double getLatitude() {

    return getLatOrLong(LATITUDE);
  }

  private Double getLatOrLong(int param) {

    if (this.getGeometrie() == null) {
      logger.info("Géométrie null pour l'aire d'aspiration :" + this.getNumero());
      return null;
    }
    ;
    try {
      Point p = this.getGeometrie();
      double[] coordonneConvert =
          GeometryUtil.transformCordinate(
              p.getX(),
              p.getY(),
              parametreDataProvider.get().getSridString(),
              GlobalConstants.SRID_4326);

      return BigDecimal.valueOf(coordonneConvert[param])
          .setScale(5, RoundingMode.HALF_UP)
          .doubleValue();

    } catch (CRSException | IllegalCoordinateException exception) {
      logger.error(exception.getMessage());
      return null;
    }
  }
}
