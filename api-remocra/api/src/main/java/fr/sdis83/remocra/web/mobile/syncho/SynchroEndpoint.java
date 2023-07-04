package fr.sdis83.remocra.web.mobile.syncho;

import fr.sdis83.remocra.authn.AuthDevice;
import javax.inject.Provider;
import fr.sdis83.remocra.authn.CurrentUser;
import fr.sdis83.remocra.authn.UserInfo;
import com.google.inject.Inject;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.FormParam;
import javax.ws.rs.Consumes;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import java.util.ArrayList;


import fr.sdis83.remocra.usecase.tournee.TourneeUseCase;
import fr.sdis83.remocra.repository.UtilisateursRepository;


@Path("/mobile/synchro")
@Produces("application/json; charset=UTF-8")
public class SynchroEndpoint {
    @Inject
    TourneeUseCase tourneeUseCase;

    @Inject
    UtilisateursRepository utilisateursRespository;


    @Inject
    @CurrentUser
    Provider<UserInfo> currentUser;


    @AuthDevice
    @Path("/tourneesdispos")
    @GET
    public Response getTourneesDispos() {
        // on va chercher l'organisme de l'utilisateur connecté
        Long idOrganisme = utilisateursRespository.getOrganisme(currentUser.get().userId());

        if (idOrganisme == null) {
            return Response.serverError().entity("Aucun organisme trouvé pour l'utilisateur connecté").build();
        }
        return Response.ok(tourneeUseCase.getTourneesDisponibles(idOrganisme)).build();
    }

    @AuthDevice
    @Path("/reservertournees")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @POST
    public Response reserverTournees(
            @FormParam("listIdTournees") @NotNull ArrayList<Long> listIdTournees
    ) {
        // On retourne les tournées réservées et celles qu'on n'a pas pu réserver
        return Response.ok(tourneeUseCase.reserveTournees(listIdTournees, currentUser.get().userId())).build();
    }
}
