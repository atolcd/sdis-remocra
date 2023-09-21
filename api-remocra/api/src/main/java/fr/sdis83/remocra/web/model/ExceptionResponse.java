package fr.sdis83.remocra.web.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.Optional;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableExceptionResponse.class)
public interface ExceptionResponse {

  @Value.Parameter
  @JsonProperty
  Optional<String> message();
}
