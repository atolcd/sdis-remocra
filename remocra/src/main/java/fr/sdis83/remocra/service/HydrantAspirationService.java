package fr.sdis83.remocra.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.PrecisionModel;
import flexjson.JSONDeserializer;
import fr.sdis83.remocra.GlobalConstants;
import fr.sdis83.remocra.domain.remocra.HydrantAspiration;
import fr.sdis83.remocra.util.GeometryUtil;
import fr.sdis83.remocra.web.message.ItemFilter;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.Transactional;

@Configuration
public class HydrantAspirationService extends AbstractService<HydrantAspiration> {

  public HydrantAspirationService() {
    super(HydrantAspiration.class);
  }

  @Override
  protected Predicate processFilterItem(
      CriteriaQuery<?> itemQuery,
      Map<String, Object> parameters,
      Root<HydrantAspiration> from,
      ItemFilter itemFilter) {
    CriteriaBuilder cBuilder = this.getCriteriaBuilder();
    Predicate predicat = null;
    if ("id".equals(itemFilter.getFieldName())) {
      Expression<String> cpPath = from.get("id");
      predicat = cBuilder.equal(cpPath, itemFilter.getValue());
    } else if ("pena".equals(itemFilter.getFieldName())) {
      Expression<String> cpPath = from.get("pena").get("id");
      predicat = cBuilder.equal(cpPath, itemFilter.getValue());
    }
    return predicat;
  }

  /**
   * Permet de mettre à jour plusieurs instances de HydrantAspiration
   *
   * @param json Un tableau JSON contenant les informations de l'HydrantAspiration
   */
  public void updateMany(String json) throws Exception {
    ArrayList<HashMap<String, Object>> liste =
        new JSONDeserializer<ArrayList<HashMap<String, Object>>>().deserialize(json);
    for (HashMap<String, Object> obj : liste) {

      // Si on a indiqué des coordonnées
      if (obj.get("longitude") != null && obj.get("latitude") != null) {
        double longitude = Double.parseDouble(obj.get("longitude").toString());
        double latitude = Double.parseDouble(obj.get("latitude").toString());
        obj.remove("longitude");
        obj.remove("latitude");

        double[] coordonneConvert =
            GeometryUtil.transformCordinate(
                longitude, latitude, "4326", GlobalConstants.SRID_PARAM.toString());
        longitude =
            BigDecimal.valueOf(coordonneConvert[0]).setScale(0, RoundingMode.HALF_UP).intValue();
        latitude =
            BigDecimal.valueOf(coordonneConvert[1]).setScale(0, RoundingMode.HALF_UP).intValue();

        GeometryFactory geometryFactory =
            new GeometryFactory(new PrecisionModel(), GlobalConstants.SRID_PARAM);
        Point p = geometryFactory.createPoint(new Coordinate(longitude, latitude));
        obj.put("geometrie", p);
      } else {
        obj.put("geometrie", null);
      }
      ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
      String aspi = ow.writeValueAsString(obj);
      if (obj.get("id") == null) { // Pas d'ID: nouvelle aspiration
        super.create(aspi, null);
      } else { // ID présent: modification dans la BDD
        super.update(Long.parseLong(obj.get("id").toString()), aspi, null);
      }
    }
  }

  @Transactional
  public boolean delete(String json) throws Exception {
    ArrayList<Integer> liste = new JSONDeserializer<ArrayList<Integer>>().deserialize(json);
    for (Integer id : liste) {
      super.delete(Long.valueOf(id));
    }
    return true;
  }
}
