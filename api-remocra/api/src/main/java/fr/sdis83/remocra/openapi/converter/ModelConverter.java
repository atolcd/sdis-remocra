package fr.sdis83.remocra.openapi.converter;

import com.fasterxml.jackson.databind.JavaType;
import io.swagger.v3.core.converter.AnnotatedType;
import io.swagger.v3.core.converter.ModelConverterContext;
import io.swagger.v3.core.util.Json;
import io.swagger.v3.oas.models.media.DateSchema;
import io.swagger.v3.oas.models.media.DateTimeSchema;
import io.swagger.v3.oas.models.media.ObjectSchema;
import io.swagger.v3.oas.models.media.Schema;
import java.time.Instant;
import java.util.Iterator;

@SuppressWarnings("unused")
public class ModelConverter implements io.swagger.v3.core.converter.ModelConverter {

  @Override
  public Schema resolve(
      AnnotatedType type,
      ModelConverterContext context,
      Iterator<io.swagger.v3.core.converter.ModelConverter> chain) {
    JavaType _type = Json.mapper().constructType(type.getType());
    if (_type != null) {
      Class<?> cls = _type.getRawClass();
      if (Instant.class.isAssignableFrom(cls)) {
        return new DateTimeSchema();
      } else if (java.sql.Date.class.isAssignableFrom(cls)) {
        return new DateSchema();
      } else if (com.fasterxml.jackson.databind.JsonNode.class.isAssignableFrom(cls)) {
        return new ObjectSchema();
      }
    }
    if (chain.hasNext()) {
      return chain.next().resolve(type, context, chain);
    } else {
      return null;
    }
  }
}
