package fr.sdis83.remocra.web.s;

import fr.sdis83.remocra.authn.UserRoles;
import fr.sdis83.remocra.usecase.indispotemporaire.IndispoTemporaireUseCase;
import fr.sdis83.remocra.web.exceptions.ResponseException;
import fr.sdis83.remocra.web.model.deci.pei.HydrantVisiteSpecifiqueForm;
import fr.sdis83.remocra.web.model.indispotemporaire.IndispoTemporaireForm;
import fr.sdis83.remocra.web.model.indispotemporaire.IndispoTemporaireSpecifiqueForm;
import fr.sdis83.remocra.web.model.pei.PeiForm;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;

@Path("/api/deci/indispoTemporaire")
@Produces("application/json; charset=UTF-8")
@Consumes(MediaType.APPLICATION_JSON)
public class DeciHydrantIndispoTemporaireEndpoint {

  @Inject
  IndispoTemporaireUseCase indispoTemporaireUseCase;

  @GET
  @Path("")
  @Operation(summary = "Retourne la liste des indisponibilités temporaires de Remocra", tags = {"DECI - Indispo temporaire"})
  @RolesAllowed({UserRoles.RoleTypes.RECEVOIR})
  public Response getIndispoTemporaire(
    final @Parameter(description = "Code de l'organisme API étant à l'origine de l'indisponibilité temporaire") @QueryParam("organismeApi") String organismeAPI,
    final @Parameter(description = "Ne renvoie que les indisponibilités temporaires liées à cet hydrant") @QueryParam("numeroHydrant") String hydrant,
    final @Parameter(description = "Code du statut de l'indisponibilité temporaire") @QueryParam("statut") String statut,
    final @Parameter(description = "Nombre maximum de résultats à retourner") @QueryParam("limit") Integer limit,
    final @Parameter(description = "Retourne les informations à partir de la n-ième ligne") @QueryParam("start") Integer start
  ) throws IOException {
    return Response.ok(this.indispoTemporaireUseCase.getAll(organismeAPI, hydrant, statut, limit, start)).build();
  }

  @POST
  @Path("")
  @Operation(summary = "Ajoute une nouvelle indisponibilité temporaire", tags = {"DECI - Indispo temporaire"})
  @RolesAllowed({UserRoles.RoleTypes.TRANSMETTRE})
  public Response addIndispoTemporaire(
    @NotNull @Parameter(description = "Informations de l'indisponibilité temporaire") IndispoTemporaireForm indispoForm
  ) {
    try {
      return Response.ok(indispoTemporaireUseCase.addIndispoTemporaire(indispoForm), MediaType.APPLICATION_JSON).build();
    } catch (ResponseException e) {
      return Response.status(e.getStatusCode()).entity(e.getMessage()).build();
    }
  }

  @PUT
  @Path("/{idIndisponibiliteTemporaire}")
  @Operation(summary = "Modifie les informations relatives à une indisponibilité temporaire", tags = {"DECI - Indispo temporaire"})
  @ApiResponse(responseCode = "200", description = "Indisponibilité temporaire modifiée avec succès")
  @ApiResponse(responseCode = "400", description = "Erreur à la saisie")
  @RolesAllowed({UserRoles.RoleTypes.TRANSMETTRE})
  public Response editIndispoTemporaire(
    final @Parameter(description = "Identifiant de l'indisponibilité temporaire)") @PathParam("idIndisponibiliteTemporaire") String idIndispo,
    @Parameter(description = "Informations d'indisponibilite temporaire", required = true) @NotNull IndispoTemporaireSpecifiqueForm form
  ) throws ResponseException {
    try {
      this.indispoTemporaireUseCase.editIndispoTemporaire(idIndispo, form);
      return Response.ok("Indisponibilité temporaire modifiée avec succès").build();
    } catch(ResponseException e) {
      return Response.status(e.getStatusCode()).entity(e.getMessage()).build();
    }
  }
}
