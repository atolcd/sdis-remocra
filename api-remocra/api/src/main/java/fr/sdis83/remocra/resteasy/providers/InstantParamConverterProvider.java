package fr.sdis83.remocra.resteasy.providers;

import com.google.common.base.Strings;

import javax.ws.rs.ext.ParamConverter;
import javax.ws.rs.ext.ParamConverterProvider;
import javax.ws.rs.ext.Provider;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.TimeZone;

@Provider
public class InstantParamConverterProvider implements ParamConverterProvider {

@Override
@SuppressWarnings("unchecked")
public <T> ParamConverter<T> getConverter(Class<T> rawType, Type genericType, Annotation[] annotations) {
        if (Instant.class.equals(rawType)) {
                return (ParamConverter<T>) new InstantParamConverter();
        }
        return null;
}

private static class InstantParamConverter implements ParamConverter<Instant> {
@Override
public Instant fromString(String value) {
        if (Strings.isNullOrEmpty(value)) {
                return null;
        }
        // DateTimeFormatter.ISO_INSTANT.parse(value) pour de l'UTC
        return Instant.from(
                DateTimeFormatter.ISO_LOCAL_DATE_TIME.withZone(TimeZone.getDefault().toZoneId()).parse(value));
}

@Override
public String toString(Instant value) {
        if (value == null) {
                return null;
        }
        // DateTimeFormatter.ISO_INSTANT.format(value); pour de l'UTC
        return DateTimeFormatter.ISO_LOCAL_DATE_TIME.withZone(TimeZone.getDefault().toZoneId()).format(value);
}
}
}
