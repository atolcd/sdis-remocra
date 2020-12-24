package fr.sdis83.remocra.db.converter;

import org.jooq.Converter;

import java.sql.Time;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;

public class LocalTimeConverter implements Converter<Time, LocalTime> {

/**
 *
 */
private static final long serialVersionUID = 7591707199038774610L;

@Override
public LocalTime from(Time databaseObject) {
        if (databaseObject == null) {
                return null;
        }
        return databaseObject.toLocalTime();
}

@Override
public Time to(LocalTime userObject) {
        if (userObject == null) {
                return null;
        }
        return Time.valueOf(userObject.truncatedTo(ChronoUnit.SECONDS));
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
