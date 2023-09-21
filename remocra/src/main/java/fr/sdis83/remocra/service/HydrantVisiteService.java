package fr.sdis83.remocra.service;

import com.vividsolutions.jts.geom.Geometry;
import flexjson.JSONDeserializer;
import fr.sdis83.remocra.domain.remocra.HydrantVisite;
import fr.sdis83.remocra.domain.utils.JSONMap;
import fr.sdis83.remocra.domain.utils.RemocraDateHourTransformer;
import fr.sdis83.remocra.web.deserialize.GeometryFactory;
import fr.sdis83.remocra.web.deserialize.RemocraBeanObjectFactory;
import fr.sdis83.remocra.web.message.ItemFilter;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import org.apache.log4j.Logger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

@Configuration
public class HydrantVisiteService extends AbstractService<HydrantVisite> {

  private final Logger logger = Logger.getLogger(getClass());

  public HydrantVisiteService() {
    super(HydrantVisite.class);
  }

  @PersistenceContext protected EntityManager entityManager;

  @Bean
  public HydrantVisiteService hydrantVisiteService() {
    return new HydrantVisiteService();
  }

  @Override
  protected Predicate processFilterItem(
      CriteriaQuery<?> itemQuery,
      Map<String, Object> parameters,
      Root<HydrantVisite> from,
      ItemFilter itemFilter) {
    CriteriaBuilder cBuilder = this.getCriteriaBuilder();
    Predicate predicat = null;
    if ("id".equals(itemFilter.getFieldName())) {
      Expression<String> cpPath = from.get("id");
      predicat = cBuilder.equal(cpPath, itemFilter.getValue());
    } else if ("hydrant".equals(itemFilter.getFieldName())) {
      Expression<String> cpPath = from.join("hydrant").get("id");
      predicat = cBuilder.equal(cpPath, itemFilter.getValue());
    } else {
      logger.info(
          "processFilterItem non traité "
              + itemFilter.getFieldName()
              + " ("
              + itemFilter.getValue()
              + ")");
    }
    return predicat;
  }

  /**
   * Permet de mettre à jour plusieurs instances de HydrantVisite
   *
   * @param json Un tableau JSON contenant les informations de l'HydrantVisite
   */
  @Transactional
  public void updateMany(String json) throws Exception {
    ArrayList<HashMap<String, Object>> liste =
        new JSONDeserializer<ArrayList<HashMap<String, Object>>>().deserialize(json);
    for (HashMap<String, Object> obj : liste) {
      // ID existant -> mise à jour
      if (obj.get("id") != null) {

        HydrantVisite visite =
            this.entityManager.find(this.cls, Long.valueOf(obj.get("id").toString()));

        if (obj.get("date") != null) {
          Date date =
              new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(String.valueOf(obj.get("date")));
          obj.remove("date");
          visite.setDate(date);
        }
        if (obj.get("anomalies") != null) {
          visite.setAnomalies(String.valueOf(obj.get("anomalies")));
          obj.remove("anomalies");
        }

        JSONDeserializer<HydrantVisite> deserializer = new JSONDeserializer<HydrantVisite>();
        deserializer
            .use(null, this.cls)
            .use(Date.class, RemocraDateHourTransformer.getInstance())
            .use(Geometry.class, new GeometryFactory())
            .use(Object.class, new RemocraBeanObjectFactory(this.entityManager));
        deserializer.deserializeInto(JSONMap.fromMap(obj).toString(), visite);

        /*
         * "hack" pour gérer les valeurs "null" dans le json. Flexjson les
         * ignore, et ce bug connu sera corriger dans la v3.0 cf :
         * http://sourceforge.net/p/flexjson/bugs/32/
         */
        JSONDeserializer<Map<String, Object>> deserializer2 =
            new JSONDeserializer<Map<String, Object>>();
        Map<String, Object> data = deserializer2.deserialize(JSONMap.fromMap(obj).toString());
        Object nullObject = null;
        if (data != null && data.size() > 0) {
          for (String key : data.keySet()) {
            Object value = data.get(key);
            if (value == null) {
              Method method = findSetter(visite, key);
              if (method != null) {
                method.invoke(visite, nullObject);
              }
            }
          }
        }
        // Fin "hack"

        this.setUpInformation(visite, null);
        this.entityManager.merge(visite);
        this.entityManager.flush();
      } else {

        // ID inexistant: création
        this.create(obj, null);
      }
    }
  }

  private Method findSetter(Object obj, String fieldName) {
    String methodName = "set" + StringUtils.capitalize(fieldName);
    Method[] methods = obj.getClass().getMethods();
    for (int i = 0; i < methods.length; i++) {
      if (methodName.equals(methods[i].getName())) {
        return methods[i];
      }
    }
    return null;
  }

  @Transactional
  public HydrantVisite create(HashMap<String, Object> obj, Map<String, MultipartFile> files)
      throws Exception {
    JSONDeserializer<HydrantVisite> deserializer = new JSONDeserializer<HydrantVisite>();
    deserializer
        .use(null, this.cls)
        .use(Date.class, RemocraDateHourTransformer.getInstance())
        .use(Geometry.class, new GeometryFactory())
        .use(Object.class, new RemocraBeanObjectFactory(this.entityManager));

    HydrantVisite attached = new HydrantVisite();
    if (obj.get("date") != null) {
      Date date =
          new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(String.valueOf(obj.get("date")));
      obj.remove("date");
      attached.setDate(date);
    }
    if (obj.get("anomalies") != null) {
      attached.setAnomalies(String.valueOf(obj.get("anomalies")));
      obj.remove("anomalies");
    }

    deserializer.deserializeInto(JSONMap.fromMap(obj).toString(), attached);

    /*
     * "hack" pour gérer les valeurs "null" dans le json. Flexjson les
     * ignore, et ce bug connu sera corriger dans la v3.0 cf :
     * http://sourceforge.net/p/flexjson/bugs/32/
     */
    JSONDeserializer<Map<String, Object>> deserializer2 =
        new JSONDeserializer<Map<String, Object>>();
    Map<String, Object> data = deserializer2.deserialize(JSONMap.fromMap(obj).toString());
    Object nullObject = null;
    if (data != null && data.size() > 0) {
      for (String key : data.keySet()) {
        Object value = data.get(key);
        if (value == null) {
          Method method = findSetter(attached, key);
          if (method != null) {
            method.invoke(attached, nullObject);
          }
        }
      }
    }
    // Fin "hack"

    this.setUpInformation(attached, files);
    this.entityManager.persist(attached);
    return attached;
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
