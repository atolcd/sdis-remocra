package fr.sdis83.remocra.db.converter;

import org.jooq.Converter;

import java.sql.Timestamp;
import org.joda.time.Instant;
import sun.security.jca.GetInstance;

public class InstantConverter implements Converter<Timestamp, Instant> {

    /**
     *
     */
    private static final long serialVersionUID = -4750143605865916519L;

    @Override
    public Instant from(Timestamp databaseObject) {
        if (databaseObject == null) {
            return null;
        }
        return new Instant(databaseObject.getTime());
    }

    @Override
    public Timestamp to(Instant userObject) {
        if (userObject == null) {
            return null;
        }
        return new Timestamp(userObject.getMillis());
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
