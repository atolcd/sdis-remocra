package fr.sdis83.remocra.web.s;

import com.fasterxml.jackson.core.JsonProcessingException;
import fr.sdis83.remocra.authn.UserRoles;
import fr.sdis83.remocra.repository.CommunesRepository;
import fr.sdis83.remocra.repository.OrganismesRepository;
import fr.sdis83.remocra.repository.TypeOrganismesRepository;
import fr.sdis83.remocra.repository.VoiesRepository;
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

@Path("/api/referentiel")
@Produces("application/json; charset=UTF-8")
@Consumes(MediaType.APPLICATION_JSON)
public class ReferentielsCommunsEndpoint {

  @Inject
  TypeOrganismesRepository typeOrganismesRepository;

  @Inject
  CommunesRepository communeRepository;

  @Inject
  VoiesRepository voiesRepository;

  @Inject
  OrganismesRepository organismesRepository;

  @GET
  @Path("/naturesOrganismes")
  @Operation(summary = "Retourne les types d'organismes susceptibles d'exploiter REMOCRA", tags = {"Référentiels communs"})
  @RolesAllowed({UserRoles.RoleTypes.RECEVOIR})
  public Response getRefentielNatureOrganismes(
    final @Parameter(description = "Nombre maximum de résultats à retourner") @QueryParam("limit") Integer limit,
    final @Parameter(description = "Retourne les informations à partir de la n-ième ligne") @QueryParam("start") Integer start
  ) throws JsonProcessingException {

    return Response.ok(typeOrganismesRepository.getAll(start, limit)).build();
  }

  @GET
  @Path("/communes")
  @Operation(summary = "Retourne la liste des communes", tags = {"Référentiels communs"})
  @RolesAllowed({UserRoles.RoleTypes.RECEVOIR})
  public Response getRefentielCommunes(
    final @Parameter(description = "Code INSEE de la commune") @QueryParam("code") String insee,
    final @Parameter(description = "Tout ou partie du nom de la commune") @QueryParam("commune") String commune,
    final @Parameter(description = "Nombre maximum de résultats à retourner") @QueryParam("limit") Integer limit,
    final @Parameter(description = "Retourne les informations à partir de la n-ième ligne") @QueryParam("start") Integer start
  ) throws JsonProcessingException {

    return Response.ok(communeRepository.getAll(insee, commune, start, limit)).build();
  }

  @GET
  @Path("/voies/{insee}")
  @Operation(summary = "Retourne les voies d'une commune donnée", tags = {"Référentiels communs"})
  @RolesAllowed({UserRoles.RoleTypes.RECEVOIR})
  public Response getRefentielVoies(
    final @Parameter(description = "Code INSEE de la commune", required = true) @PathParam("insee") String insee,
    final @Parameter(description = "Tout ou partie du nom de la voie") @QueryParam("nom") String nom,
    final @Parameter(description = "Nombre maximum de résultats à retourner") @QueryParam("limit") Integer limit,
    final @Parameter(description = "Retourne les informations à partir de la n-ième ligne") @QueryParam("start") Integer start
  ) throws JsonProcessingException {

    return Response.ok(voiesRepository.getAll(insee, nom, start, limit)).build();
  }

  @GET
  @Path("/organismes")
  @Operation(summary = "Retourne les organismes susceptibles d'exploiter REMOCRA (utilisateurs nommés avec accès à l'interface applicative ou exploitation de l'API)", tags = {"Référentiels communs"})
  @RolesAllowed({UserRoles.RoleTypes.RECEVOIR})
  public Response getRefentielOrganismes(
    final @Parameter(description = "Code de la nature de l'organisme") @QueryParam("codeNature") String codeNature,
    final @Parameter(description = "Nombre maximum de résultats à retourner") @QueryParam("limit") Integer limit,
    final @Parameter(description = "Retourne les informations à partir de la n-ième ligne") @QueryParam("start") Integer start
  ) throws JsonProcessingException {

    return Response.ok(organismesRepository.getAll(codeNature, start, limit)).build();
  }
}
