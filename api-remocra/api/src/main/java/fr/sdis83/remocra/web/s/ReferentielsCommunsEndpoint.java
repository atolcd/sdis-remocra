package fr.sdis83.remocra.web.s;

import com.fasterxml.jackson.core.JsonProcessingException;
import fr.sdis83.remocra.repository.CommunesRepository;
import fr.sdis83.remocra.repository.OrganismesRepository;
import fr.sdis83.remocra.repository.TypeOrganismesRepository;
import fr.sdis83.remocra.repository.VoiesRepository;
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
  @Operation(summary = "Retourne la liste des natures d'organismes", tags = {"Référentiels communs"})
  @PermitAll
  public String getRefentielNatureOrganismes(
    final @Parameter(description = "Nombre maximum de résultats à retourner") @QueryParam("limit") Integer limit,
    final @Parameter(description = "Retourne les informations à partir de la n-ième ligne") @QueryParam("start") Integer start
  ) throws JsonProcessingException {

    return typeOrganismesRepository.getAll(start, limit);
  }

  @GET
  @Path("/communes")
  @Operation(summary = "Retourne la liste des communes", tags = {"Référentiels communs"})
  @PermitAll
  public String getRefentielCommunes(
    final @Parameter(description = "Code INSEE de la commune") @QueryParam("code") String insee,
    final @Parameter(description = "Tout ou partie du nom de la commune") @QueryParam("commune") String commune,
    final @Parameter(description = "Nombre maximum de résultats à retourner") @QueryParam("limit") Integer limit,
    final @Parameter(description = "Retourne les informations à partir de la n-ième ligne") @QueryParam("start") Integer start
  ) throws JsonProcessingException {

    return communeRepository.getAll(insee, commune, start, limit);
  }

  @GET
  @Path("/voies/{insee}")
  @Operation(summary = "Retourne les voies d'une commune donnée", tags = {"Référentiels communs"})
  @PermitAll
  public String getRefentielVoies(
    final @Parameter(description = "Code INSEE de la commune", required = true) @PathParam("insee") String insee,
    final @Parameter(description = "Tout ou partie du nom de la voie") @QueryParam("nom") String nom,
    final @Parameter(description = "Nombre maximum de résultats à retourner") @QueryParam("limit") Integer limit,
    final @Parameter(description = "Retourne les informations à partir de la n-ième ligne") @QueryParam("start") Integer start
  ) throws JsonProcessingException {

    return voiesRepository.getAll(insee, nom, start, limit);
  }

  @GET
  @Path("/organismes")
  @Operation(summary = "Organismes", tags = {"Référentiels communs"})
  @PermitAll
  public String getRefentielOrganismes(
    final @Parameter(description = "Code de la nature de l'organisme") @QueryParam("codeNature") String codeNature,
    final @Parameter(description = "Nombre maximum de résultats à retourner") @QueryParam("limit") Integer limit,
    final @Parameter(description = "Retourne les informations à partir de la n-ième ligne") @QueryParam("start") Integer start
  ) throws JsonProcessingException {

    return organismesRepository.getAll(codeNature, start, limit);
  }
}
