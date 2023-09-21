package fr.sdis83.remocra.web.model.indispotemporaire;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.List;
import javax.annotation.Nullable;
import javax.ws.rs.DefaultValue;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableIndispoTemporaireForm.class)
public interface IndispoTemporaireForm {

  @JsonProperty
  @Nullable
  String date_debut();

  @JsonProperty
  @Nullable
  String date_fin();

  @JsonProperty
  @Nullable
  String motif();

  @JsonProperty
  String statut();

  @JsonProperty
  @DefaultValue("true")
  Boolean bascule_auto_indispo();

  @JsonProperty
  @DefaultValue("true")
  Boolean bascule_auto_dispo();

  @JsonProperty
  @DefaultValue("true")
  Boolean mel_avant_indispo();

  @JsonProperty
  @DefaultValue("true")
  Boolean mel_avant_dispo();

  @JsonProperty
  @DefaultValue("true")
  List<String> hydrants();
}
