package fr.sdis83.remocra.web.s;

import com.fasterxml.jackson.core.JsonProcessingException;
import fr.sdis83.remocra.repository.TypeHydrantDomaineRepository;
import fr.sdis83.remocra.repository.TypeHydrantNatureDeciRepository;
import fr.sdis83.remocra.repository.TypeHydrantNiveauRepository;
import fr.sdis83.remocra.repository.TypeHydrantSaisieRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;

import javax.annotation.security.PermitAll;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

@Path("/api/deci/referentiel")
@Produces("application/json; charset=UTF-8")
@Consumes(MediaType.APPLICATION_JSON)
public class ReferentielsDeciEndpoint {

  @Inject
  TypeHydrantNatureDeciRepository typeHydrantNatureDeci;

  @Inject
  TypeHydrantNiveauRepository typeHydrantNiveauRepository;

  @Inject
  TypeHydrantDomaineRepository typeHydrantDomaineRepository;

  @Inject
  TypeHydrantSaisieRepository typeHydrantSaisieRepository;

  @GET
  @Path("/naturesDECI")
  @Operation(summary = "Retourne la liste des natures DECI possibles pour un PEI", tags = {"DECI - Référentiels communs"})
  @PermitAll
  public String getRefentielNaturesDECI(
    final @Parameter(description = "Nombre maximum de résultats à retourner") @QueryParam("limit") Integer limit,
    final @Parameter(description = "Retourne les informations à partir de la n-ième ligne") @QueryParam("start") Integer start
  ) throws JsonProcessingException {

    return typeHydrantNatureDeci.getAll(start, limit);
  }

  @GET
  @Path("/niveaux")
  @Operation(summary = "Retourne la liste des natures DECI possibles pour un PEI", tags = {"DECI - Référentiels communs"})
  @PermitAll
  public String getRefentielNiveaux(
    final @Parameter(description = "Nombre maximum de résultats à retourner") @QueryParam("limit") Integer limit,
    final @Parameter(description = "Retourne les informations à partir de la n-ième ligne") @QueryParam("start") Integer start
  ) throws JsonProcessingException {

    return typeHydrantNiveauRepository.getAll(start, limit);
  }

  @GET
  @Path("/domaines")
  @Operation(summary = "Retourne la liste des domaines possibles pour un PEI", tags = {"DECI - Référentiels communs"})
  @PermitAll
  public String getRefentielDomaines(
    final @Parameter(description = "Nombre maximum de résultats à retourner") @QueryParam("limit") Integer limit,
    final @Parameter(description = "Retourne les informations à partir de la n-ième ligne") @QueryParam("start") Integer start
  ) throws JsonProcessingException {

    return typeHydrantDomaineRepository.getAll(start, limit);
  }

  @GET
  @Path("/naturesVisites")
  @Operation(summary = "Retourne la liste des types de visite disponibles", tags = {"DECI - Référentiels communs"})
  @PermitAll
  public String getRefentielVisites(
    final @Parameter(description = "Nombre maximum de résultats à retourner") @QueryParam("limit") Integer limit,
    final @Parameter(description = "Retourne les informations à partir de la n-ième ligne") @QueryParam("start") Integer start
  ) throws JsonProcessingException {

    return typeHydrantSaisieRepository.getAll(start, limit);
  }
}

