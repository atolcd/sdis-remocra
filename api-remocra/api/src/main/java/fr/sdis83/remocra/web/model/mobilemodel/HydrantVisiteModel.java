package fr.sdis83.remocra.web.model.mobilemodel;


import java.time.ZonedDateTime;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;
import javax.annotation.Nullable;
import java.util.UUID;

@Value.Immutable
@JsonSerialize(as = ImmutableHydrantVisiteModel.class)
@JsonDeserialize(as = ImmutableHydrantVisiteModel.class)
public interface HydrantVisiteModel {
    UUID idHydrantVisite();
    Long idHydrant();
    ZonedDateTime date();
    Long idTypeVisite();
    boolean ctrDebitPression();
    @Nullable
    String agent1();
    @Nullable
    String agent2();
    @Nullable
    Integer debit();
    @Nullable
    Double pression();
    @Nullable
    Double pressionDyn();
    @Nullable
    String observations();
}