package fr.sdis83.remocra.resteasy.providers;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.core.util.VersionUtil;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.deser.Deserializers;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.ser.Serializers;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.fasterxml.jackson.datatype.guava.GuavaModule;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;

import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.time.Instant;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.TimeZone;

@Provider
@Produces({"application/*+json", "text/json"})
@Consumes({"application/*+json", "text/json"})
public class JacksonJsonProvider extends com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider {

static final InstantDeserialiser INSTANT_DESERIALIZER_INSTANCE = new InstantDeserialiser();
static final InstantSerialiser INSTANT_SERIALIZER_INSTANCE = new InstantSerialiser();
static final LocalTimeDeserialiser LOCALTIME_DESERIALIZER_INSTANCE = new LocalTimeDeserialiser();
static final LocalTimeSerialiser LOCALTIME_SERIALIZER_INSTANCE = new LocalTimeSerialiser();
public final static Version VERSION = VersionUtil.parseVersion("0.1", "fr.sdis83.remocra.resteasy.providers",
                                                               "jackson-json-provider");

public static final ObjectMapper OBJECT_MAPPER = new ObjectMapper().registerModule(new Module() {
                @Override
                public String getModuleName() {
                        return "InstantFromInteger";
                }

                @Override
                public Version version() {
                        return VERSION;
                }

                @Override
                public void setupModule(SetupContext setupContext) {
                        setupContext.addDeserializers(new Deserialisers());
                        setupContext.addSerializers(new Serialisers());
                }
        }).registerModule(new GuavaModule()).registerModule(new Jdk8Module())
                                                 .setSerializationInclusion(JsonInclude.Include.NON_ABSENT)
                                                 .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

public JacksonJsonProvider() {
        super(OBJECT_MAPPER);
}

// INSTANT
private static class InstantDeserialiser extends StdDeserializer<Instant> {
/**
 *
 */
private static final long serialVersionUID = -4711550196917083587L;

public InstantDeserialiser() {
        super(Instant.class);
}

@Override
public Instant deserialize(JsonParser p, DeserializationContext ctxt)
throws IOException, JsonProcessingException {
        // DateTimeFormatter.ISO_INSTANT.format(value) pour de l'UTC
        return Instant.from(DateTimeFormatter.ISO_LOCAL_DATE_TIME.withZone(TimeZone.getDefault().toZoneId())
                            .parse(p.getValueAsString()));
}
}

private static class InstantSerialiser extends StdSerializer<Instant> {
/**
 *
 */
private static final long serialVersionUID = 3542435533325555921L;

public InstantSerialiser() {
        super(Instant.class);
}

@Override
public void serialize(Instant value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        // DateTimeFormatter.ISO_INSTANT.format(value) pour de l'UTC
        gen.writeString(
                DateTimeFormatter.ISO_LOCAL_DATE_TIME.withZone(TimeZone.getDefault().toZoneId()).format(value));
}
}

// LOCALTIME
private static class LocalTimeDeserialiser extends StdDeserializer<LocalTime> {
/**
 *
 */
private static final long serialVersionUID = 6596102026257829146L;

public LocalTimeDeserialiser() {
        super(Instant.class);
}

@Override
public LocalTime deserialize(JsonParser p, DeserializationContext ctxt)
throws IOException, JsonProcessingException {
        return LocalTime.from(DateTimeFormatter.ISO_LOCAL_TIME.parse(p.getValueAsString()));
}
}

private static class LocalTimeSerialiser extends StdSerializer<LocalTime> {
/**
 *
 */
private static final long serialVersionUID = -5015216369337509481L;

public LocalTimeSerialiser() {
        super(LocalTime.class);
}

@Override
public void serialize(LocalTime value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeString(DateTimeFormatter.ISO_LOCAL_TIME.format(value));
}
}

private static class Deserialisers extends Deserializers.Base {
@Override
public JsonDeserializer<?> findBeanDeserializer(JavaType type, DeserializationConfig config,
                                                BeanDescription beanDesc) throws JsonMappingException {
        final Class<?> raw = type.getRawClass();
        if (raw == Instant.class) {
                return INSTANT_DESERIALIZER_INSTANCE;
        }
        if (raw == LocalTime.class) {
                return LOCALTIME_DESERIALIZER_INSTANCE;
        }
        return super.findBeanDeserializer(type, config, beanDesc);
}
}

private static class Serialisers extends Serializers.Base {
@Override
public JsonSerializer<?> findSerializer(SerializationConfig config, JavaType type, BeanDescription beanDesc) {
        final Class<?> raw = type.getRawClass();
        if (raw == Instant.class) {
                return INSTANT_SERIALIZER_INSTANCE;
        }
        if (raw == LocalTime.class) {
                return LOCALTIME_SERIALIZER_INSTANCE;
        }
        return super.findSerializer(config, type, beanDesc);
}
}
}
