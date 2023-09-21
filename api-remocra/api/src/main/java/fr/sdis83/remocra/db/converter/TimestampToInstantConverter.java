package fr.sdis83.remocra.db.converter;

import java.sql.Timestamp;
import java.time.Instant;
import org.jooq.Converter;

public class TimestampToInstantConverter implements Converter<Timestamp, Instant> {

  /** */
  private static final long serialVersionUID = -4750143605865916519L;

  @Override
  public Instant from(Timestamp databaseObject) {
    if (databaseObject == null) {
      return null;
    }
    return databaseObject.toInstant();
  }

  @Override
  public Timestamp to(Instant userObject) {
    if (userObject == null) {
      return null;
    }
    return Timestamp.from(userObject);
  }

  @Override
  public Class<Timestamp> fromType() {
    return Timestamp.class;
  }

  @Override
  public Class<Instant> toType() {
    return Instant.class;
  }
}
