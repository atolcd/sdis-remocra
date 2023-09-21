package fr.sdis83.remocra.web.s;

import com.fasterxml.jackson.core.JsonProcessingException;
import fr.sdis83.remocra.authn.UserRoles;
import fr.sdis83.remocra.repository.TypeHydrantDiametreRepository;
import fr.sdis83.remocra.repository.TypeHydrantMarqueRepository;
import fr.sdis83.remocra.repository.TypeHydrantModeleRepository;
import fr.sdis83.remocra.repository.TypeHydrantNatureAnomalieRepository;
import fr.sdis83.remocra.repository.TypeHydrantNatureRepository;
import fr.sdis83.remocra.repository.TypeReseauAlimentationRepository;
import fr.sdis83.remocra.repository.TypeReseauCanalisationRepository;
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

@Path("/api/deci/referentiel/pibi")
@Produces("application/json; charset=UTF-8")
@Consumes(MediaType.APPLICATION_JSON)
public class ReferentielsDeciPibiEndpoint {

  @Inject TypeHydrantNatureRepository typeHydrantNatureRepository;

  @Inject TypeHydrantDiametreRepository typeHydrantDiametreRepository;

  @Inject TypeHydrantNatureAnomalieRepository typeHydrantNatureAnomalieRepository;

  @Inject TypeHydrantMarqueRepository typeHydrantMarqueRepository;

  @Inject TypeHydrantModeleRepository typeHydrantModeleRepository;

  @Inject TypeReseauAlimentationRepository typeReseauAlimentationRepository;

  @Inject TypeReseauCanalisationRepository typeReseauCanalisationRepository;

  @GET
  @Path("/naturesPEI")
  @Operation(
      summary = "Retourne les natures de PEI possibles pour les PEI de type PIBI",
      tags = {"DECI - Référentiels PIBI"})
  @RolesAllowed({UserRoles.RoleTypes.RECEVOIR})
  public Response getRefentielNaturesPEI(
      final @Parameter(description = "Nombre maximum de résultats à retourner") @QueryParam("limit")
          Integer limit,
      final @Parameter(description = "Retourne les informations à partir de la n-ième ligne")
          @QueryParam("start") Integer start)
      throws JsonProcessingException {

    return Response.ok(typeHydrantNatureRepository.getAll("PIBI", start, limit)).build();
  }

  @GET
  @Path("/diametres/{codeNature}")
  @Operation(
      summary = "Retourne les diamètres de demi-raccord possibles une nature de PIBI",
      tags = {"DECI - Référentiels PIBI"})
  @RolesAllowed({UserRoles.RoleTypes.RECEVOIR})
  public Response getRefentielDiametres(
      final @Parameter(description = "Code de nature PIBI") @PathParam("codeNature") String
              codeNature,
      final @Parameter(description = "Nombre maximum de résultats à retourner") @QueryParam("limit")
          Integer limit,
      final @Parameter(description = "Retourne les informations à partir de la n-ième ligne")
          @QueryParam("start") Integer start)
      throws JsonProcessingException {

    return Response.ok(typeHydrantDiametreRepository.getAll(codeNature, start, limit)).build();
  }

  @GET
  @Path("{codeNature}/naturesAnomalies")
  @Operation(
      summary =
          "Retourne les types d'anomalies pouvant être constatées pour une nature de PIBI et un contexte "
              + "(type de visite) spécifiques",
      tags = {"DECI - Référentiels PIBI"})
  @RolesAllowed({UserRoles.RoleTypes.RECEVOIR})
  public Response getRefentielNaturesAnomalies(
      final @Parameter(description = "Nature du PIBI") @PathParam("codeNature") String codeNature,
      final @Parameter(description = "Contexte (code) de la visite") @QueryParam("contexteVisite")
          String contexteVisite,
      final @Parameter(description = "Nombre maximum de résultats à retourner") @QueryParam("limit")
          Integer limit,
      final @Parameter(description = "Retourne les informations à partir de la n-ième ligne")
          @QueryParam("start") Integer start)
      throws JsonProcessingException {

    return Response.ok(
            typeHydrantNatureAnomalieRepository.getAll(
                codeNature, contexteVisite, "PIBI", start, limit))
        .build();
  }

  @GET
  @Path("/marques")
  @Operation(
      summary = "Retourne les marques susceptibles d'équiper le parc de PIBI",
      tags = {"DECI - Référentiels PIBI"})
  @RolesAllowed({UserRoles.RoleTypes.RECEVOIR})
  public Response getRefentielMarques(
      final @Parameter(description = "Nombre maximum de résultats à retourner") @QueryParam("limit")
          Integer limit,
      final @Parameter(description = "Retourne les informations à partir de la n-ième ligne")
          @QueryParam("start") Integer start)
      throws JsonProcessingException {

    return Response.ok(typeHydrantMarqueRepository.getAll(start, limit)).build();
  }

  @GET
  @Path("/modeles")
  @Operation(
      summary = "Retourne les modèles susceptibles d'équiper le parc de PIBI",
      tags = {"DECI - Référentiels PIBI"})
  @RolesAllowed({UserRoles.RoleTypes.RECEVOIR})
  public Response getRefentielModeles(
      final @Parameter(description = "Code de la marque") @QueryParam("codeMarque") String
              codeMarque,
      final @Parameter(description = "Nombre maximum de résultats à retourner") @QueryParam("limit")
          Integer limit,
      final @Parameter(description = "Retourne les informations à partir de la n-ième ligne")
          @QueryParam("start") Integer start)
      throws JsonProcessingException {

    return Response.ok(typeHydrantModeleRepository.getAll(codeMarque, start, limit)).build();
  }

  @GET
  @Path("/naturesReseau")
  @Operation(
      summary = "Retourne les types de réseau d'alimentation en eau pouvant alimenter les PIBI",
      tags = {"DECI - Référentiels PIBI"})
  @RolesAllowed({UserRoles.RoleTypes.RECEVOIR})
  public Response getRefentielNaturesReseau(
      final @Parameter(description = "Nombre maximum de résultats à retourner") @QueryParam("limit")
          Integer limit,
      final @Parameter(description = "Retourne les informations à partir de la n-ième ligne")
          @QueryParam("start") Integer start)
      throws JsonProcessingException {

    return Response.ok(typeReseauAlimentationRepository.getAll(start, limit)).build();
  }

  @GET
  @Path("/naturesCanalisation")
  @Operation(
      summary = "Retourne les matériaux pouvant constituer les canalisations",
      tags = {"DECI - Référentiels PIBI"})
  @RolesAllowed({UserRoles.RoleTypes.RECEVOIR})
  public Response getRefentielNaturesCanalisation(
      final @Parameter(description = "Nombre maximum de résultats à retourner") @QueryParam("limit")
          Integer limit,
      final @Parameter(description = "Retourne les informations à partir de la n-ième ligne")
          @QueryParam("start") Integer start)
      throws JsonProcessingException {

    return Response.ok(typeReseauCanalisationRepository.getAll(start, limit)).build();
  }
}
