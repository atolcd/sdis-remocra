package fr.sdis83.remocra.web.s;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import fr.sdis83.remocra.app.ApplicationTitle;
import fr.sdis83.remocra.app.ApplicationVersion;
import io.swagger.v3.oas.annotations.Operation;
import org.immutables.value.Value;

import javax.annotation.security.PermitAll;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.time.Instant;

@Path("/application")
@Produces("application/json; charset=UTF-8")
@Consumes(MediaType.APPLICATION_JSON)
public class ApplicationEndpoint {

@Inject
@ApplicationVersion
String appVersion;

@Inject
@ApplicationTitle
String appTitle;

@GET
@Path("/info")
@Operation(
        summary = "Retourne les informations de l'application",
        tags = {"Application"}
        )
@PermitAll
public ImmutableAppInfo info() {
        return ImmutableAppInfo.of(appTitle.toString(), appVersion.toString(), System.getProperty("java.version"));
}

@Value.Immutable
@JsonDeserialize(as = ImmutableAppInfo.class)
public interface AppInfo {
@Value.Parameter
@JsonProperty
String title();

@Value.Parameter
@JsonProperty
String version();

@Value.Parameter
@JsonProperty
String java();
}

@GET
@Path("/now")
@Operation(
        summary = "Retourne la date et l'heure",
        tags = {"Application"}
        )
@PermitAll
public Instant now() {
        return Instant.now();
}
}
