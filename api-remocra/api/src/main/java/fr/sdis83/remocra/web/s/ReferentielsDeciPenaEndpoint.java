package fr.sdis83.remocra.web.s;

import com.fasterxml.jackson.core.JsonProcessingException;
import fr.sdis83.remocra.authn.UserRoles;
import fr.sdis83.remocra.repository.TypeHydrantAspirationRepository;
import fr.sdis83.remocra.repository.TypeHydrantMateriauRepository;
import fr.sdis83.remocra.repository.TypeHydrantNatureAnomalieRepository;
import fr.sdis83.remocra.repository.TypeHydrantNatureRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

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
  @Operation(summary = "Retourne les natures de PEI possibles pour les PEI de type PENA", tags = {"DECI - Référentiels PENA"})
  @RolesAllowed({UserRoles.RoleTypes.RECEVOIR})
  public Response getRefentielNaturesPEI(
    final @Parameter(description = "Nombre maximum de résultats à retourner") @QueryParam("limit") Integer limit,
    final @Parameter(description = "Retourne les informations à partir de la n-ième ligne") @QueryParam("start") Integer start
  ) throws JsonProcessingException {

    return Response.ok(typeHydrantNatureRepository.getAll("PENA", start, limit)).build();
  }

  @GET
  @Path("/naturesMateriau")
  @Operation(summary = "Retourne les matériaux possibles pour les PEI de type PENA", tags = {"DECI - Référentiels PENA"})
  @RolesAllowed({UserRoles.RoleTypes.RECEVOIR})
  public Response getRefentielNaturesMateriau(
    final @Parameter(description = "Nombre maximum de résultats à retourner") @QueryParam("limit") Integer limit,
    final @Parameter(description = "Retourne les informations à partir de la n-ième ligne") @QueryParam("start") Integer start
  ) throws JsonProcessingException {

    return Response.ok(typeHydrantMateriauRepository.getAll(start, limit)).build();
  }

  /*@GET
  @Path("/naturesAspiration")
  @Operation(summary = "Retourne les types de dispositif d'aspiration possibles pour les aires d'aspiration associées aux PENA", tags = {"DECI - Référentiels PENA"})
  @RolesAllowed({UserRoles.RoleTypes.RECEVOIR})
  public Response getRefentielNaturesAspiration(
    final @Parameter(description = "Nombre maximum de résultats à retourner") @QueryParam("limit") Integer limit,
    final @Parameter(description = "Retourne les informations à partir de la n-ième ligne") @QueryParam("start") Integer start
  ) throws JsonProcessingException {

    return Response.ok(typeHydrantAspirationRepository.getAll(start, limit)).build();
  }*/

  @GET
  @Path("{codeNature}/naturesAnomalies")
  @Operation(summary = "Retourne les types d'anomalies pouvant être constatées pour une nature de PENA (ex : Envasement " +
    "excessif peut s'appliquer à un point d'eau naturel mais pas à une citerne) et à un contexte (type de visite) " +
    "spécifiques", tags = {"DECI - Référentiels PENA"})
  @RolesAllowed({UserRoles.RoleTypes.RECEVOIR})
  public Response getRefentielNaturesAnomalies(
    final @Parameter(description = "Nature du PENA") @PathParam("codeNature") String codeNature,
    final @Parameter(description = "Contexte (code) de la visite") @QueryParam("contexteVisite") String contexteVisite,
    final @Parameter(description = "Nombre maximum de résultats à retourner") @QueryParam("limit") Integer limit,
    final @Parameter(description = "Retourne les informations à partir de la n-ième ligne") @QueryParam("start") Integer start
  ) throws JsonProcessingException {

    return Response.ok(typeHydrantNatureAnomalieRepository.getAll(codeNature, contexteVisite, "PENA", start, limit)).build();
  }
}
