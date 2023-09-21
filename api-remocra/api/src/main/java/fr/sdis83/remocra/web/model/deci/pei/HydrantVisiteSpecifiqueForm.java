package fr.sdis83.remocra.web.model.deci.pei;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import javax.annotation.Nullable;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableHydrantVisiteSpecifiqueForm.class)
public interface HydrantVisiteSpecifiqueForm {

  @JsonProperty
  @Nullable
  String agent1();

  @JsonProperty
  @Nullable
  String agent2();

  @JsonProperty
  String[] anomaliesControlees();

  @JsonProperty
  String[] anomaliesConstatees();

  @JsonProperty
  @Nullable
  Integer debit();

  @JsonProperty
  @Nullable
  Integer debitMax();

  @JsonProperty
  @Nullable
  Double pression();

  @JsonProperty
  @Nullable
  Double pressionDynamique();

  @JsonProperty
  @Nullable
  Double pressionDynamiqueDebitMax();

  @JsonProperty
  @Nullable
  String observations();
}
