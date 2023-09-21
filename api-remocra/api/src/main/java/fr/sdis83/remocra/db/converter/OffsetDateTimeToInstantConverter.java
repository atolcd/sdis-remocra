package fr.sdis83.remocra.db.converter;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import org.jooq.Converter;

public class OffsetDateTimeToInstantConverter implements Converter<OffsetDateTime, Instant> {

  /** */
  private static final long serialVersionUID = -4750143605865916519L;

  @Override
  public Instant from(OffsetDateTime databaseObject) {
    if (databaseObject == null) {
      return null;
    }
    return databaseObject.toInstant();
  }

  @Override
  public OffsetDateTime to(Instant userObject) {
    if (userObject == null) {
      return null;
    }
    return OffsetDateTime.ofInstant(userObject, ZoneId.systemDefault());
  }

  @Override
  public Class<OffsetDateTime> fromType() {
    return OffsetDateTime.class;
  }

  @Override
  public Class<Instant> toType() {
    return Instant.class;
  }
}
