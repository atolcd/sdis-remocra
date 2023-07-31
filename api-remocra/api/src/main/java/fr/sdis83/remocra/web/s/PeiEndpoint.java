package fr.sdis83.remocra.web.s;

import com.fasterxml.jackson.core.JsonProcessingException;
import fr.sdis83.remocra.authn.CurrentUser;
import fr.sdis83.remocra.authn.UserInfo;
import fr.sdis83.remocra.authn.UserRoles;
import fr.sdis83.remocra.usecase.pei.PeiUseCase;
import fr.sdis83.remocra.web.exceptions.ResponseException;
import fr.sdis83.remocra.web.model.pei.PeiForm;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.inject.Provider;
import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/api/deci/pei")
@Produces("application/json; charset=UTF-8")
@Consumes(MediaType.APPLICATION_JSON)
public class PeiEndpoint {

    @Inject @CurrentUser
    Provider<UserInfo> currentUser;

    @Inject
    PeiUseCase peiUseCase;

    @GET
    @Path("")
    @Operation(summary = "Retourne la liste des PEI en lien avec l'organisme associé à la clef d'accès d’API. Le lien est " +
      "établ au sein de l'application REMOCRA par association entre un point d'eau et les types d’organismes suivants :\n" +
      "*Autorité de police* \n" +
      "*Service Public DECI* \n" +
      "*Prestataire Technique pour le compte du Service Public DECI* \n" +
      "*Service des eaux en charge de la distribution de l'eau pour les équipements sous pression (BI et PI)* \n" +
      "Les PEI se répartissent selon leur type (PIBI pour les équipements reliés à réseau d'eau sous pression, PENA pour les " +
      "autres ressources), leur nature (PIBI : Poteau (PI) ou borne (BI). PENA : Point d'aspiration , citerne, etc.) et le type " +
      "de DECI (publique, privée, privée sous convention). Les caractéristiques techniques et les procédures applicables varient" +
      " en fonction de ces critères de répartition.\n" +
      "La structure de donnée retournée est conforme au modèle de donnée minimal défini par l'AFIGEO\n", tags = {"DECI - Points d'Eau Incendie"})
    @RolesAllowed({UserRoles.RoleTypes.RECEVOIR})
    public Response getPei(
      final @Parameter(description = "Nombre maximum de résultats à retourner (maximum fixé à 200 résultats)")
          @QueryParam("limit") @Max(value=200) @DefaultValue("200") Integer limit,
      final @Parameter(description = "Retourne les informations à partir de la n-ième ligne") @QueryParam("start") Integer start,
      final @Parameter(description = "Numéro INSEE de la commune où se trouve le PEI") @QueryParam("insee") String insee,
      final @Parameter(description = "Type du PEI : PIBI ou PENA") @QueryParam("type") String type,
      final @Parameter(description = "Nature de l'hydrant") @QueryParam("nature") String nature,
      final @Parameter(description = "Nature DECI de l'hydrant") @QueryParam("natureDECI") String natureDECI
    ) {
      return Response.ok(this.peiUseCase.getAll(insee, type, nature, natureDECI, limit, start)).build();
    }

    @GET
    @Path("/{numero}")
    @Operation(summary = "Retourne les informations communes à tout type de PEI d'un PEI spécifique", tags = {"DECI - Points d'Eau Incendie"})
    @RolesAllowed({UserRoles.RoleTypes.RECEVOIR})
    public Response getPeiSpecifique(
      final @Parameter(description = "Numéro du PEI") @PathParam("numero") String numero
    ) throws JsonProcessingException {
      try {
        return Response.ok(peiUseCase.getPeiSpecifique(numero)).build();
      } catch (ResponseException e){
        return Response.status(e.getStatusCode()).entity(e.getMessage()).build();
      }
    }

    @GET
    @Path("/{numero}/caracteristiques")
    @Operation(summary = "Retourne les caractéristiques techniques propres au PEI et à son type (PIBI ou PENA)", tags = {"DECI - Points d'Eau Incendie"})
    @RolesAllowed({UserRoles.RoleTypes.RECEVOIR})
    public Response getPeiCaracteristiques(
      final @Parameter(description = "Numéro du PEI") @PathParam("numero") String numero
    ) throws JsonProcessingException {
      try {
        return Response.ok(peiUseCase.getPeiCaracteristiques(numero), MediaType.APPLICATION_JSON).build();
      } catch (ResponseException e) {
        return Response.status(e.getStatusCode()).entity(e.getMessage()).build();
      }
    }

    @PUT
    @Path("/{numero}/caracteristiques")
    @Operation(summary = "Modifie les caractéristiques techniques propres au PEI et à son type (PIBI ou PENA)", tags = {"DECI - Points d'Eau Incendie"})
    @RolesAllowed({UserRoles.RoleTypes.TRANSMETTRE})
    public Response updatePeiCaracteristiques(
      @Parameter(description = "Numéro du PEI") @PathParam("numero") String numero,
      @NotNull @Parameter(description = "Informations du PEI") PeiForm peiForm
    ) {
      try {
        return Response.ok(peiUseCase.updatePeiCaracteristiques(numero, peiForm), MediaType.APPLICATION_JSON).build();
      } catch (ResponseException e) {
        return Response.status(e.getStatusCode()).entity(e.getMessage()).build();
      }
    }

    @GET
    @Path("/diff")
    @Operation(summary = "Liste des PEI ayant subit une modification (ajout, modification ou suppression) postérieure à la date passée en paramètre", tags = {"DECI - Points d'Eau Incendie"})
    @RolesAllowed({UserRoles.RoleTypes.RECEVOIR})
    public Response diff(
      final @Parameter(description = "Date à partir de laquelle retourner les résultats, format YYYY-MM-DD hh:mm:ss", required = true) @QueryParam("date") String date
    ) {
      try {
        return Response.ok(peiUseCase.diff(date), MediaType.APPLICATION_JSON).build();
      } catch (ResponseException e) {
        return Response.status(e.getStatusCode()).entity(e.getMessage()).build();
      }
    }

}
