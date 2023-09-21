package fr.sdis83.remocra.web.deserialize;

import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;
import flexjson.ObjectBinder;
import flexjson.ObjectFactory;
import java.lang.reflect.Type;
import org.apache.log4j.Logger;

public class GeometryFactory implements ObjectFactory {

  private static final Logger logger = Logger.getLogger(GeometryFactory.class);
  private static final WKTReader reader = new WKTReader();

  @SuppressWarnings("rawtypes")
  @Override
  public Object instantiate(
      ObjectBinder context, Object value, Type targetType, Class targetClass) {
    try {
      return reader.read((String) value);
    } catch (ParseException e) {
      logger.error("Erreur lors de la lecture du WKT : " + value, e);
    }
    return null;
  }
}
