package fr.sdis83.remocra.repository;

import static fr.sdis83.remocra.db.model.remocra.Tables.HYDRANT_ASPIRATION;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.PrecisionModel;
import fr.sdis83.remocra.GlobalConstants;
import fr.sdis83.remocra.db.model.remocra.tables.pojos.HydrantAspiration;
import fr.sdis83.remocra.usecase.parametre.ParametreDataProvider;
import fr.sdis83.remocra.util.GeometryUtil;
import fr.sdis83.remocra.util.JSONUtil;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.cts.IllegalCoordinateException;
import org.cts.crs.CRSException;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HydrantAspirationRepository {

  @Autowired DSLContext context;

  @Autowired protected ParametreDataProvider parametreProvider;

  private ObjectMapper objectMapper = new ObjectMapper();

  public HydrantAspirationRepository() {}

  @Bean
  public HydrantAspirationRepository hydrantAspirationRepository(DSLContext context) {
    return new HydrantAspirationRepository(context);
  }

  HydrantAspirationRepository(DSLContext context) {
    this.context = context;
  }

  /**
   * Ajoute une ou plusieurs aspirations à un PENA
   *
   * @param id L'identifiant du PENA
   * @param data Les données JSON des aspirations
   */
  public void addHydrantAspirationFromFiche(Long id, String data)
      throws IOException, CRSException, IllegalCoordinateException {

    if (data != null && !"null".equals(data)) {
      List<Map<String, Object>> listeAspirations =
          objectMapper.readValue(data, new TypeReference<List<Map<String, Object>>>() {});
      for (Map<String, Object> a : listeAspirations) {

        HydrantAspiration aspiration = new HydrantAspiration();
        aspiration.setId(JSONUtil.getLong(a, "id"));
        aspiration.setNumero(JSONUtil.getString(a, "numero"));
        aspiration.setNormalise(JSONUtil.getBoolean(a, "normalise"));
        aspiration.setTypeAspiration(JSONUtil.getLong(a, "typeAspiration"));
        aspiration.setDeporte(JSONUtil.getBoolean(a, "deporte"));
        aspiration.setHauteur(JSONUtil.getBoolean(a, "hauteur"));

        Double longitude = JSONUtil.getDouble(a, "longitude");
        Double latitude = JSONUtil.getDouble(a, "latitude");

        if (longitude != null && latitude != null) {
          double[] coordonneConvert =
              GeometryUtil.transformCordinate(
                  longitude,
                  latitude,
                  GlobalConstants.SRID_4326,
                  parametreProvider.get().getSridString());
          int lon =
              BigDecimal.valueOf(coordonneConvert[0]).setScale(0, RoundingMode.HALF_UP).intValue();
          int lat =
              BigDecimal.valueOf(coordonneConvert[1]).setScale(0, RoundingMode.HALF_UP).intValue();

          GeometryFactory geometryFactory =
              new GeometryFactory(new PrecisionModel(), parametreProvider.get().getSridInt());
          Point p = geometryFactory.createPoint(new Coordinate(lon, lat));
          aspiration.setGeometrie(p);
        } else {
          aspiration.setGeometrie(null);
        }

        if (aspiration.getId() != null) {
          this.updateHydrantAspiration(aspiration);
        } else {
          this.createHydrantAspiration(id, aspiration);
        }
      }
    }
  }

  /**
   * Supprime une ou plusieurs aspirations d'un PENA
   *
   * @param id L'identifiant du PENA
   * @param data Un array JSON contenant les identifiants des aspirations à supprimer
   */
  public void deleteHydrantAspirationsFromFiche(Long id, String data) throws IOException {
    if (data != null && !"null".equals(data)) {
      ArrayList<Long> listeAspi =
          objectMapper.readValue(data.toString(), new TypeReference<ArrayList<Long>>() {});

      for (Long idAspi : listeAspi) {
        this.deleteHydrantAspiration(idAspi);
      }
    }
  }

  /**
   * Ajoute une aspiration en base La fonction updatehydrantAspiration est ensuite appelée pour lui
   * transmettre les données
   *
   * @param idPena Identifiant du PENA
   * @param aspiration Les données de l'aspiration
   * @return L'aspiration créée et complétée
   */
  private HydrantAspiration createHydrantAspiration(Long idPena, HydrantAspiration aspiration) {
    Long id =
        context
            .insertInto(HYDRANT_ASPIRATION)
            .set(HYDRANT_ASPIRATION.PENA, idPena)
            .returning(HYDRANT_ASPIRATION.ID)
            .fetchOne()
            .getValue(HYDRANT_ASPIRATION.ID);
    aspiration.setId(id);
    return this.updateHydrantAspiration(aspiration);
  }

  /**
   * Met à jour une aspiration en base
   *
   * @param aspiration Les données de l'aspiration
   * @return L'aspiration contenant ses nouvelles données
   */
  private HydrantAspiration updateHydrantAspiration(HydrantAspiration aspiration) {
    context
        .update(HYDRANT_ASPIRATION)
        .set(HYDRANT_ASPIRATION.DEPORTE, aspiration.getDeporte())
        .set(HYDRANT_ASPIRATION.GEOMETRIE, aspiration.getGeometrie())
        .set(HYDRANT_ASPIRATION.HAUTEUR, aspiration.getHauteur())
        .set(HYDRANT_ASPIRATION.NORMALISE, aspiration.getNormalise())
        .set(HYDRANT_ASPIRATION.NUMERO, aspiration.getNumero())
        .set(HYDRANT_ASPIRATION.TYPE_ASPIRATION, aspiration.getTypeAspiration())
        .where(HYDRANT_ASPIRATION.ID.eq(aspiration.getId()))
        .execute();

    return context
        .selectFrom(HYDRANT_ASPIRATION)
        .where(HYDRANT_ASPIRATION.ID.eq(aspiration.getId()))
        .fetchOneInto(HydrantAspiration.class);
  }

  /**
   * Supprime une aspiration en base
   *
   * @param id L'identifiant de l'aspiration
   */
  private void deleteHydrantAspiration(Long id) {
    context.deleteFrom(HYDRANT_ASPIRATION).where(HYDRANT_ASPIRATION.ID.eq(id)).execute();
  }
}
