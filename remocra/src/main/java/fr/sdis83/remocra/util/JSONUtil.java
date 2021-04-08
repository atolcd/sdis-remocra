package fr.sdis83.remocra.util;

import fr.sdis83.remocra.db.converter.InstantConverter;
import org.joda.time.Instant;

import java.sql.Timestamp;
import java.util.Map;

public class JSONUtil {

  private static InstantConverter instantConverter = new InstantConverter();

  public static String getString(Map<String, Object> data, String key) {
    return (data.get(key) == null) ? null : data.get(key).toString();
  }

  public static Instant getInstant(Map<String, Object> data, String key) {
    return instantConverter.from(Timestamp.valueOf(data.get(key).toString()));
  }

  public static Long getLong(Map<String, Object> data, String key) {
    return (data.get(key) == null || data.get(key).toString().length() == 0) ? null : Long.valueOf(data.get(key).toString());
  }

  public static Integer getInteger(Map<String, Object> data, String key) {
    return (data.get(key) == null || data.get(key).toString().length() == 0) ? null : Integer.valueOf(data.get(key).toString());
  }

  public static Boolean getBoolean(Map<String, Object> data, String key) {
    return (data.get(key) == null || data.get(key).toString().length() == 0) ? null : Boolean.valueOf(data.get(key).toString());
  }

  public static Double getDouble(Map<String, Object> data, String key) {
    return (data.get(key) == null || data.get(key).toString().length() == 0) ? null : Double.valueOf(data.get(key).toString());
  }
}
