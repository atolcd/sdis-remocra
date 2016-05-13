package fr.sdis83.remocra.web.deserialize;

import java.lang.reflect.Type;

import org.apache.log4j.Logger;

import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;

import flexjson.ObjectBinder;
import flexjson.ObjectFactory;

public class GeometryFactory implements ObjectFactory {

    private final static Logger logger = Logger.getLogger(GeometryFactory.class);
    private final static WKTReader reader = new WKTReader();

    @SuppressWarnings("rawtypes")
    @Override
    public Object instantiate(ObjectBinder context, Object value, Type targetType, Class targetClass) {
        try {
            return reader.read((String) value);
        } catch (ParseException e) {
            logger.error("Erreur lors de la lecture du WKT : " + value, e);
        }
        return null;
    }

}
