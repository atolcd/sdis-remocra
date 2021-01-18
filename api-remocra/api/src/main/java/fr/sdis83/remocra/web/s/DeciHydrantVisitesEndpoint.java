package fr.sdis83.remocra.web.s;

import fr.sdis83.remocra.repository.HydrantVisitesRepository;
import fr.sdis83.remocra.web.exceptions.ResponseException;
import fr.sdis83.remocra.web.model.deci.pei.HydrantVisiteForm;
import fr.sdis83.remocra.web.model.deci.pei.HydrantVisiteSpecifiqueForm;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.HttpStatus;

import javax.annotation.security.PermitAll;
import javax.inject.Inject;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;

@Path("/api/deci/pei/{numero}")
@Produces("application/json; charset=UTF-8")
@Consumes(MediaType.APPLICATION_JSON)
public class DeciHydrantVisitesEndpoint {

  @Inject
  HydrantVisitesRepository hydrantVisitesRepository;

  @GET
  @Path("/visites")
  @Operation(summary = "Retourne les visites d'un PEI", tags = {"DECI - Hydrant - Visites"})
  @PermitAll
  public Response getHydrantVisites(
    final @Parameter(description = "Numéro du PEI") @PathParam("numero") String numero,
    final @Parameter(description = "Code du contexte de visite") @QueryParam("contexte") String contexte,
    final @Parameter(description = "Date à partir de laquelle retourner les résultats, format YYYY-MM-DD hh:mm") @QueryParam("date") String date,
    final @Parameter(description = "Renvoyer uniquement la dernière visite") @QueryParam("derniereOnly") Boolean derniereOnly,
    final @Parameter(description = "Nombre maximum de résultats à retourner") @QueryParam("limit") Integer limit,
    final @Parameter(description = "Retourne les informations à partir de la n-ième ligne") @QueryParam("start") Integer start
  ) throws IOException {

    try {
      return Response.ok(hydrantVisitesRepository.getAll(numero, contexte, date, derniereOnly, start, limit), MediaType.APPLICATION_JSON).build();
    } catch(ResponseException e) {
      return Response.status(e.getStatusCode()).entity(e.getMessage()).build();
    }
  }

  @POST
  @Path("/visites")
  @Operation(summary = "Ajoute une visite à un hydrant", tags = {"DECI - Hydrant - Visites"})
  @ApiResponse(responseCode = "201", description = "Visite créée avec succès")
  @ApiResponse(responseCode = "400", description = "Erreur à la saisie")
  @PermitAll
  public Response addVisite(
    @Parameter(description = "Informations de la visite", required = true) @NotNull HydrantVisiteForm form
  ) throws ResponseException {
    try {
      hydrantVisitesRepository.addVisite(form);
      return Response.status(HttpStatus.CREATED.value()).build();
    } catch(ResponseException e) {
      return Response.status(e.getStatusCode()).entity(e.getMessage()).build();
    }
  }

  @GET
  @Path("/visites/{idVisite}")
  @Operation(summary = "Retourne les détails d'une visite", tags = {"DECI - Hydrant - Visites"})
  @PermitAll
  public Response getHydrantVisiteSpecifique(
    final @Parameter(description = "Numéro du PEI") @PathParam("numero") String numero,
    final @Parameter(description = "Identifiant de la visite") @PathParam("idVisite") String idVisite
  ) throws IOException {

    return Response.ok(hydrantVisitesRepository.getHydrantVisiteSpecifique(numero, idVisite), MediaType.APPLICATION_JSON).build();
  }

  @POST
  @Path("/visites/{idVisite}")
  @Operation(summary = "Modifie une visite spécifique", tags = {"DECI - Hydrant - Visites"})
  @ApiResponse(responseCode = "200", description = "Visite modifiée avec succès")
  @ApiResponse(responseCode = "400", description = "Erreur à la saisie")
  @PermitAll
  public Response editVisite(
    final @Parameter(description = "Numéro du PEI") @PathParam("numero") String numero,
    final @Parameter(description = "Identifiant de la visite") @PathParam("idVisite") String idVisite,
    @Parameter(description = "Informations de la visite", required = true) @NotNull HydrantVisiteSpecifiqueForm form
  ) throws ResponseException {
    try {
      hydrantVisitesRepository.editVisite(numero, idVisite, form);
      return Response.ok().build();
    } catch(ResponseException e) {
      return Response.status(e.getStatusCode()).entity(e.getMessage()).build();
    }
  }

  @DELETE
  @Path("/visites/{idVisite}")
  @Operation(summary = "Supprime une visite spécifique", tags = {"DECI - Hydrant - Visites"})
  @ApiResponse(responseCode = "200", description = "Visite supprimée avec succès")
  @ApiResponse(responseCode = "400", description = "Erreur à la saisie")
  @PermitAll
  public Response deleteVisite(
    final @Parameter(description = "Numéro du PEI") @PathParam("numero") String numero,
    final @Parameter(description = "Identifiant de la visite") @PathParam("idVisite") String idVisite
  ) throws ResponseException {
    try {
      hydrantVisitesRepository.deleteVisite(numero, idVisite);
      return Response.ok().build();
    } catch(ResponseException e) {
      return Response.status(e.getStatusCode()).entity(e.getMessage()).build();
    }
  }

}
