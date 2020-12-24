package fr.sdis83.remocra.web.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

import java.util.Optional;

@Value.Immutable
@JsonDeserialize(as = ImmutableExceptionResponse.class)
public interface ExceptionResponse {

@Value.Parameter
@JsonProperty
Optional<String> message();
}
