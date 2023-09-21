package fr.sdis83.remocra.web.s;

import fr.sdis83.remocra.authn.UserRoles;
import fr.sdis83.remocra.usecase.visites.HydrantVisitesUseCase;
import fr.sdis83.remocra.web.exceptions.ResponseException;
import fr.sdis83.remocra.web.model.deci.pei.HydrantVisiteForm;
import fr.sdis83.remocra.web.model.deci.pei.HydrantVisiteSpecifiqueForm;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import java.io.IOException;
import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/api/deci/pei/{numero}/visites")
@Produces("application/json; charset=UTF-8")
@Consumes(MediaType.APPLICATION_JSON)
public class DeciHydrantVisitesEndpoint {

  @Inject HydrantVisitesUseCase hydrantVisitesUseCase;

  @GET
  @Path("")
  @Operation(
      summary =
          "Retourne les visites associées à un PEI. Une visite correspond à une séance terrain auprès d'un PEI à "
              + "une date donnée dans un contexte périodique (contrôle technique périodique, reconnaissance opérationnelle périodique) ou "
              + "non (reconnaissance initiale, intervention). Chaque visite permet, selon son type de récolter de l'information sur le PEI "
              + "(mesure de débit et de pression, présence d'anomalies empêchant le fonctionnement,etc) qui conditionnent la disponibilité "
              + "du PEI",
      tags = {"DECI - Visites"})
  @RolesAllowed({UserRoles.RoleTypes.RECEVOIR})
  public Response getHydrantVisites(
      final @Parameter(description = "Numéro du PEI") @PathParam("numero") String numero,
      final @Parameter(description = "Code du contexte de visite") @QueryParam("contexte") String
              contexte,
      final @Parameter(
              description =
                  "Date à partir de laquelle retourner les résultats, format YYYY-MM-DD hh:mm")
          @QueryParam("date") String date,
      final @Parameter(description = "Renvoyer uniquement la dernière visite") @QueryParam(
              "derniereOnly") Boolean derniereOnly,
      final @Parameter(description = "Nombre maximum de résultats à retourner") @QueryParam("limit")
          Integer limit,
      final @Parameter(description = "Retourne les informations à partir de la n-ième ligne")
          @QueryParam("start") Integer start)
      throws IOException {
    try {
      return Response.ok(
              this.hydrantVisitesUseCase.getAll(numero, contexte, date, derniereOnly, start, limit))
          .build();
    } catch (ResponseException e) {
      return Response.status(e.getStatusCode()).entity(e.getMessage()).build();
    }
  }

  @POST
  @Path("")
  @Operation(
      summary =
          "Ajoute une visite à un PEI en précisant notamment le contexte, une liste de points contrôlés et une "
              + "liste d'anomalies éventuellement constatées. Des mesures de débit et de pression peuvent également être transmises dans le"
              + " cas d'un contexte \"Contrôle Technique Périodique\"",
      tags = {"DECI - Visites"})
  @ApiResponse(responseCode = "201", description = "Visite créée avec succès")
  @ApiResponse(responseCode = "400", description = "Erreur à la saisie")
  @RolesAllowed({UserRoles.RoleTypes.TRANSMETTRE})
  public Response addVisite(
      final @Parameter(description = "Numéro du PEI") @PathParam("numero") String numero,
      @Parameter(description = "Informations de la visite", required = true) @NotNull
          HydrantVisiteForm form)
      throws ResponseException {
    try {
      this.hydrantVisitesUseCase.addVisite(numero, form);
      return Response.status(Response.Status.CREATED).build();
    } catch (ResponseException e) {
      return Response.status(e.getStatusCode()).entity(e.getMessage()).build();
    }
  }

  @GET
  @Path("/{idVisite}")
  @Operation(
      summary =
          "Retourne l'information détaillée d'une visite spécifique dont les éventuelles informations de débit et pressions",
      tags = {"DECI - Visites"})
  @RolesAllowed({UserRoles.RoleTypes.RECEVOIR})
  public Response getHydrantVisiteSpecifique(
      final @Parameter(description = "Numéro du PEI") @PathParam("numero") String numero,
      final @Parameter(description = "Identifiant de la visite") @PathParam("idVisite") String
              idVisite)
      throws IOException {
    try {
      return Response.ok(
              this.hydrantVisitesUseCase.getHydrantVisiteSpecifique(numero, idVisite),
              MediaType.APPLICATION_JSON)
          .build();
    } catch (ResponseException e) {
      return Response.status(e.getStatusCode()).entity(e.getMessage()).build();
    }
  }

  @PUT
  @Path("/{idVisite}")
  @Operation(
      summary =
          "Modifie les informations relatives à une visite spécifique dont les éventuelles informations de débit et pressions dans le cas d’un CTP",
      tags = {"DECI - Visites"})
  @ApiResponse(responseCode = "200", description = "Visite modifiée avec succès")
  @ApiResponse(responseCode = "400", description = "Erreur à la saisie")
  @RolesAllowed({UserRoles.RoleTypes.TRANSMETTRE})
  public Response editVisite(
      final @Parameter(description = "Numéro du PEI") @PathParam("numero") String numero,
      final @Parameter(description = "Identifiant de la visite") @PathParam("idVisite") String
              idVisite,
      @Parameter(description = "Informations de la visite", required = true) @NotNull
          HydrantVisiteSpecifiqueForm form)
      throws ResponseException {
    try {
      this.hydrantVisitesUseCase.editVisite(numero, idVisite, form);
      return Response.ok("Visite modifiée avec succès").build();
    } catch (ResponseException e) {
      return Response.status(e.getStatusCode()).entity(e.getMessage()).build();
    }
  }

  @DELETE
  @Path("/{idVisite}")
  @Operation(
      summary = "Supprime une visite spécifique",
      tags = {"DECI - Visites"})
  @ApiResponse(responseCode = "200", description = "Visite supprimée avec succès")
  @ApiResponse(responseCode = "400", description = "Erreur à la saisie")
  @RolesAllowed({UserRoles.RoleTypes.TRANSMETTRE})
  public Response deleteVisite(
      final @Parameter(description = "Numéro du PEI") @PathParam("numero") String numero,
      final @Parameter(description = "Identifiant de la visite") @PathParam("idVisite") String
              idVisite) {
    try {
      this.hydrantVisitesUseCase.deleteVisite(numero, idVisite);
      return Response.ok().build();
    } catch (ResponseException e) {
      return Response.status(e.getStatusCode()).entity(e.getMessage()).build();
    }
  }
}
