package fr.sdis83.remocra.web.mobile;

import com.fasterxml.jackson.core.JsonProcessingException;
import fr.sdis83.remocra.authn.AuthDevice;
import fr.sdis83.remocra.repository.CommunesRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/mobile/referentiel")
@Produces("application/json; charset=UTF-8")
@Consumes(MediaType.APPLICATION_JSON)
public class MobileReferentielEndpoint {

  @Inject
  CommunesRepository communeRepository;

  @GET
  @Path("/communes")
  @Operation(summary = "Retourne la liste des communes", tags = {"Référentiels communs"})
  @AuthDevice
  public Response getRefentielCommunes(
      final @Parameter(description = "Code INSEE de la commune") @QueryParam("code") String insee,
      final @Parameter(description = "Tout ou partie du nom de la commune") @QueryParam("commune") String commune,
      final @Parameter(description = "Nombre maximum de résultats à retourner") @QueryParam("limit") Integer limit,
      final @Parameter(description = "Retourne les informations à partir de la n-ième ligne") @QueryParam("start") Integer start
  ) throws JsonProcessingException {

    return Response.ok(communeRepository.getAll(insee, commune, start, limit)).build();
  }
}
