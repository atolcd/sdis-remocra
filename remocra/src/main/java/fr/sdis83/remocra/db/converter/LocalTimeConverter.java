package fr.sdis83.remocra.db.converter;

import java.sql.Time;
import org.joda.time.LocalTime;
import org.jooq.Converter;

public class LocalTimeConverter implements Converter<Time, LocalTime> {

  /** */
  private static final long serialVersionUID = 7591707199038774610L;

  @Override
  public LocalTime from(Time databaseObject) {
    if (databaseObject == null) {
      return null;
    }
    return new LocalTime(databaseObject);
  }

  @Override
  public Time to(LocalTime userObject) {
    if (userObject == null) {
      return null;
    }
    return new Time(userObject.toDateTimeToday().getMillis());
  }

  @Override
  public Class<Time> fromType() {
    return Time.class;
  }

  @Override
  public Class<LocalTime> toType() {
    return LocalTime.class;
  }
}
