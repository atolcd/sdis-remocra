package fr.sdis83.remocra.web.s;

import com.fasterxml.jackson.core.JsonProcessingException;
import fr.sdis83.remocra.repository.PeiRepository;
import fr.sdis83.remocra.web.exceptions.ResponseException;
import fr.sdis83.remocra.web.model.pei.PeiForm;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;

import javax.annotation.security.PermitAll;
import javax.inject.Inject;
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

    @Inject
    PeiRepository peiRepository;

    @GET
    @Path("")
    @Operation(summary = "Retourne la liste des pei", tags = {"Pei"})
    @PermitAll
    public String getPei(
            final @Parameter(description = "Nombre maximum de résultats à retourner (maximum fixé à 500 résultats)")
                @QueryParam("limit") @Max(value=200) @DefaultValue("200") Integer limit,
            final @Parameter(description = "Retourne les informations à partir de la n-ième ligne") @QueryParam("start") Integer start,
            final @Parameter(description = "Numéro INSEE de la commune où se trouve le pei") @QueryParam("insee") String insee,
            final @Parameter(description = "Type du pei : PIBI ou PENA") @QueryParam("type") String type,
            final @Parameter(description = "Nature de l'hydrant") @QueryParam("nature") String nature,
            final @Parameter(description = "Nature DECI de l'hydrant") @QueryParam("natureDECI") String natureDECI
    ) throws JsonProcessingException {
        return peiRepository.getAll(insee, type, nature, natureDECI, limit, start);
    }

    @GET
    @Path("/{numero}")
    @Operation(summary = "Retourne les informations d'un pei", tags = {"Pei"})
    @PermitAll
    public Response getPeiSpecifique(
            final @Parameter(description = "Numéro du pei") @PathParam("numero") String numero
    ) throws JsonProcessingException {
        try {
            return Response.ok(peiRepository.getPeiSpecifique(numero)).build();
        } catch (ResponseException e){
            return Response.status(e.getStatusCode()).entity(e.getMessage()).build();
        }
    }

    @GET
    @Path("/{numero}/caracteristiques")
    @Operation(summary = "Retourne les informations d'un pei", tags = {"Pei"})
    @PermitAll
    public Response getPeiCaracteristiques(
            final @Parameter(description = "Numéro du pei") @PathParam("numero") String numero
    ) throws JsonProcessingException {
        try {
            return Response.ok(peiRepository.getPeiCaracteristiques(numero), MediaType.APPLICATION_JSON).build();
        } catch (ResponseException e){
            return Response.status(e.getStatusCode()).entity(e.getMessage()).build();
        }
    }

    @PUT
    @Path("/{numero}/caracteristiques")
    @Operation(summary = "Met à jour les informations d'un pei", tags = {"Pei"})
    @PermitAll
    public Response updatePeiCaracteristiques(
            @Parameter(description = "Numéro du pei") @PathParam("numero") String numero,
            @NotNull @Parameter(description = "Informations du pei") PeiForm peiForm
    ) {
        try{
            return Response.ok(peiRepository.updatePeiCaracteristiques(numero, peiForm)).build();
        } catch (ResponseException e){
            return Response.status(e.getStatusCode()).entity(e.getMessage()).build();
        }
    }

}
