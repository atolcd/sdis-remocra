package fr.sdis83.remocra.web.model.pei;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

import javax.annotation.Nullable;

@Value.Immutable
@JsonDeserialize(as = ImmutablePeiForm.class)
public interface PeiForm {

    /* PENA */
    @JsonProperty
    @Nullable
    Boolean capaciteIllimitee();

    @JsonProperty
    @Nullable
    Boolean ressourceIncertaine();

    @JsonProperty
    @Nullable
    String capacite();

    @JsonProperty
    @Nullable
    Double debitAppoint();

    @JsonProperty
    @Nullable
    String codeMateriau();

    @JsonProperty
    @Nullable
    Boolean equipeHBE();


    /* PIBI */

    @JsonProperty
    @Nullable
    String codeDiametre();

    @JsonProperty
    @Nullable
    Integer diametreCanalisation();

    @JsonProperty
    @Nullable
    String peiJumele();

    @JsonProperty
    @Nullable
    Boolean inviolabilite();

    @JsonProperty
    @Nullable
    Boolean renversable();

    @JsonProperty
    @Nullable
    String codeMarque();

    @JsonProperty
    @Nullable
    String codeModele();

    @JsonProperty
    @Nullable
    Integer anneeFabrication();

    @JsonProperty
    @Nullable
    String codeNatureReseau();

    @JsonProperty
    @Nullable
    String codeNatureCanalisation();

    @JsonProperty
    @Nullable
    Boolean reseauSurpresse();

    @JsonProperty
    @Nullable
    Boolean reseauAdditive();
}
