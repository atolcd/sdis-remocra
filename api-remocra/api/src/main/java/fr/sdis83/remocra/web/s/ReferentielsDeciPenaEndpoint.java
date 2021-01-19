package fr.sdis83.remocra.web.s;

import com.fasterxml.jackson.core.JsonProcessingException;
import fr.sdis83.remocra.db.model.tables.TypeHydrantMateriau;
import fr.sdis83.remocra.repository.TypeHydrantAspirationRepository;
import fr.sdis83.remocra.repository.TypeHydrantMateriauRepository;
import fr.sdis83.remocra.repository.TypeHydrantNatureAnomalieRepository;
import fr.sdis83.remocra.repository.TypeHydrantNatureRepository;
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

@Path("/api/deci/referentiel/pena")
@Produces("application/json; charset=UTF-8")
@Consumes(MediaType.APPLICATION_JSON)
public class ReferentielsDeciPenaEndpoint {

  @Inject
  TypeHydrantNatureRepository typeHydrantNatureRepository;

  @Inject
  TypeHydrantMateriauRepository typeHydrantMateriauRepository;

  @Inject
  TypeHydrantAspirationRepository typeHydrantAspirationRepository;

  @Inject
  TypeHydrantNatureAnomalieRepository typeHydrantNatureAnomalieRepository;

  @GET
  @Path("/naturesPEI")
  @Operation(summary = "Retourne la liste des natures possibles pour un PENA", tags = {"DECI - PENA"})
  @PermitAll
  public String getRefentielNaturesPEI(
    final @Parameter(description = "Nombre maximum de résultats à retourner") @QueryParam("limit") Integer limit,
    final @Parameter(description = "Retourne les informations à partir de la n-ième ligne") @QueryParam("start") Integer start
  ) throws JsonProcessingException {

    return typeHydrantNatureRepository.getAll("PENA", start, limit);
  }

  @GET
  @Path("/naturesMateriau")
  @Operation(summary = "Retourne la liste des matériaux pour un PENA", tags = {"DECI - PENA"})
  @PermitAll
  public String getRefentielNaturesMateriau(
    final @Parameter(description = "Nombre maximum de résultats à retourner") @QueryParam("limit") Integer limit,
    final @Parameter(description = "Retourne les informations à partir de la n-ième ligne") @QueryParam("start") Integer start
  ) throws JsonProcessingException {

    return typeHydrantMateriauRepository.getAll(start, limit);
  }

  @GET
  @Path("/naturesAspiration")
  @Operation(summary = "Retourne la liste des aspirations possibles pour un PENA", tags = {"DECI - PENA"})
  @PermitAll
  public String getRefentielNaturesAspiration(
    final @Parameter(description = "Nombre maximum de résultats à retourner") @QueryParam("limit") Integer limit,
    final @Parameter(description = "Retourne les informations à partir de la n-ième ligne") @QueryParam("start") Integer start
  ) throws JsonProcessingException {

    return typeHydrantAspirationRepository.getAll(start, limit);
  }

  @GET
  @Path("{codeNature}/naturesAnomalies")
  @Operation(summary = "Retourne la liste des anomalies pour un PENA", tags = {"DECI - PENA"})
  @PermitAll
  public String getRefentielNaturesAnomalies(
    final @Parameter(description = "Nature du PENA") @PathParam("codeNature") String codeNature,
    final @Parameter(description = "Contexte (code) de la visite") @QueryParam("contexteVisite") String contexteVisite,
    final @Parameter(description = "Nombre maximum de résultats à retourner") @QueryParam("limit") Integer limit,
    final @Parameter(description = "Retourne les informations à partir de la n-ième ligne") @QueryParam("start") Integer start
  ) throws JsonProcessingException {

    return typeHydrantNatureAnomalieRepository.getAll(codeNature, contexteVisite, "PENA", start, limit);
  }
}
