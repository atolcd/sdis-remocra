package fr.sdis83.remocra.web.s;

import fr.sdis83.remocra.repository.HydrantVisitesRepository;
import fr.sdis83.remocra.web.exceptions.ResponseException;
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

}
