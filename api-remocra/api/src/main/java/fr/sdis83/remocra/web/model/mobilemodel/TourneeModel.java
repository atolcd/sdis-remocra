package fr.sdis83.remocra.web.model.mobilemodel;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.util.List;
import javax.annotation.Nullable;
import org.immutables.value.Value;

@Value.Immutable
@JsonSerialize(as = ImmutableTourneeModel.class)
@JsonDeserialize(as = ImmutableTourneeModel.class)
public interface TourneeModel {

  Long idRemocra();

  Long affectation();

  String nom();

  @Nullable
  Long reservation();

  @Nullable
  List<Long> listeHydrant();
}
