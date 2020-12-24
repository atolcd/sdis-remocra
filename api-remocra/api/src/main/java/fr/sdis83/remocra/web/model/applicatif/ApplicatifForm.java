package fr.sdis83.remocra.web.model.applicatif;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableApplicatifForm.class)
public interface ApplicatifForm {

  @JsonProperty(required = true)
  String detectionDoublon();

  @JsonProperty(required = true)
  String millesimeMarche();

  @JsonProperty(required = true)
  String codeMarche();

  @JsonProperty(required = true)
  String codeTrancheMarche();

  @JsonProperty(required = true)
  String tauxTva();

  @JsonProperty(required = true)
  String millesimeEJ();

  @JsonProperty(required = true)
  String exerciceBudgetaire();

}






