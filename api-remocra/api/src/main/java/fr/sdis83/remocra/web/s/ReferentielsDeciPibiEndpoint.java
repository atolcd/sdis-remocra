package fr.sdis83.remocra.web.s;

import com.fasterxml.jackson.core.JsonProcessingException;
import fr.sdis83.remocra.repository.TypeHydrantDiametreRepository;
import fr.sdis83.remocra.repository.TypeHydrantMarqueRepository;
import fr.sdis83.remocra.repository.TypeHydrantModeleRepository;
import fr.sdis83.remocra.repository.TypeHydrantNatureAnomalieRepository;
import fr.sdis83.remocra.repository.TypeHydrantNatureRepository;
import fr.sdis83.remocra.repository.TypeReseauAlimentationRepository;
import fr.sdis83.remocra.repository.TypeReseauCanalisationRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;

import javax.annotation.security.PermitAll;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

@Path("/api/deci/referentiel/pibi")
@Produces("application/json; charset=UTF-8")
@Consumes(MediaType.APPLICATION_JSON)
public class ReferentielsDeciPibiEndpoint {

  @Inject
  TypeHydrantNatureRepository typeHydrantNatureRepository;

  @Inject
  TypeHydrantDiametreRepository typeHydrantDiametreRepository;

  @Inject
  TypeHydrantNatureAnomalieRepository typeHydrantNatureAnomalieRepository;

  @Inject
  TypeHydrantMarqueRepository typeHydrantMarqueRepository;

  @Inject
  TypeHydrantModeleRepository typeHydrantModeleRepository;

  @Inject
  TypeReseauAlimentationRepository typeReseauAlimentationRepository;

  @Inject
  TypeReseauCanalisationRepository typeReseauCanalisationRepository;

  @GET
  @Path("/naturesPEI")
  @Operation(summary = "Retourne la liste des natures possibles pour un PIBI", tags = {"DECI - PIBI"})
  @PermitAll
  public String getRefentielNaturesPEI(
    final @Parameter(description = "Nombre maximum de résultats à retourner") @QueryParam("limit") Integer limit,
    final @Parameter(description = "Retourne les informations à partir de la n-ième ligne") @QueryParam("start") Integer start
  ) throws JsonProcessingException {

    return typeHydrantNatureRepository.getAll("PIBI", start, limit);
  }

  @GET
  @Path("/diametres/{codeNature}")
  @Operation(summary = "Retourne la liste des diamètres possibles pour une nature de PIBI", tags = {"DECI - PIBI"})
  @PermitAll
  public String getRefentielDiametres(
    final @Parameter(description = "Code de nature PIBI") @PathParam("codeNature") String codeNature,
    final @Parameter(description = "Nombre maximum de résultats à retourner") @QueryParam("limit") Integer limit,
    final @Parameter(description = "Retourne les informations à partir de la n-ième ligne") @QueryParam("start") Integer start
  ) throws JsonProcessingException {

    return typeHydrantDiametreRepository.getAll(codeNature, start, limit);
  }

  @GET
  @Path("{codeNature}/naturesAnomalies")
  @Operation(summary = "Retourne la liste des anomalies pour un PIBI", tags = {"DECI - PIBI"})
  @PermitAll
  public String getRefentielNaturesAnomalies(
    final @Parameter(description = "Nature du PENA") @PathParam("codeNature") String codeNature,
    final @Parameter(description = "Contexte (code) de la visite") @QueryParam("contexteVisite") String contexteVisite,
    final @Parameter(description = "Nombre maximum de résultats à retourner") @QueryParam("limit") Integer limit,
    final @Parameter(description = "Retourne les informations à partir de la n-ième ligne") @QueryParam("start") Integer start
  ) throws JsonProcessingException {

    return typeHydrantNatureAnomalieRepository.getAll(codeNature, contexteVisite, "PIBI", start, limit);
  }

  @GET
  @Path("/marques")
  @Operation(summary = "Retourne la liste des marques possibles pour un PIBI", tags = {"DECI - PIBI"})
  @PermitAll
  public String getRefentielMarques(
    final @Parameter(description = "Nombre maximum de résultats à retourner") @QueryParam("limit") Integer limit,
    final @Parameter(description = "Retourne les informations à partir de la n-ième ligne") @QueryParam("start") Integer start
  ) throws JsonProcessingException {

    return typeHydrantMarqueRepository.getAll(start, limit);
  }

  @GET
  @Path("/modeles")
  @Operation(summary = "Retourne la liste des modèles possibles pour un PIBI", tags = {"DECI - PIBI"})
  @PermitAll
  public String getRefentielModeles(
    final @Parameter(description = "Code de la marque") @QueryParam("codeMarque") String codeMarque,
    final @Parameter(description = "Nombre maximum de résultats à retourner") @QueryParam("limit") Integer limit,
    final @Parameter(description = "Retourne les informations à partir de la n-ième ligne") @QueryParam("start") Integer start
  ) throws JsonProcessingException {

    return typeHydrantModeleRepository.getAll(codeMarque, start, limit);
  }

  @GET
  @Path("/naturesReseau")
  @Operation(summary = "Retourne la liste des natures de réseau d'alimentation", tags = {"DECI - PIBI"})
  @PermitAll
  public String getRefentielNaturesReseau(
    final @Parameter(description = "Nombre maximum de résultats à retourner") @QueryParam("limit") Integer limit,
    final @Parameter(description = "Retourne les informations à partir de la n-ième ligne") @QueryParam("start") Integer start
  ) throws JsonProcessingException {

    return typeReseauAlimentationRepository.getAll(start, limit);
  }

  @GET
  @Path("/naturesCanalisation")
  @Operation(summary = "Retourne la liste des natures de réseau de canalisation", tags = {"DECI - PIBI"})
  @PermitAll
  public String getRefentielNaturesCanalisation(
    final @Parameter(description = "Nombre maximum de résultats à retourner") @QueryParam("limit") Integer limit,
    final @Parameter(description = "Retourne les informations à partir de la n-ième ligne") @QueryParam("start") Integer start
  ) throws JsonProcessingException {

    return typeReseauCanalisationRepository.getAll(start, limit);
  }
}
