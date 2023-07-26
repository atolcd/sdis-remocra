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
import javax.ws.rs.PUT;
import javax.ws.rs.FormParam;
import javax.ws.rs.Consumes;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import fr.sdis83.remocra.repository.ParamConfRepository;
import fr.sdis83.remocra.web.model.referentiel.ContactModel;
import fr.sdis83.remocra.web.model.mobilemodel.ImmutableHydrantVisiteModel;

import java.io.IOException;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import fr.sdis83.remocra.web.model.mobilemodel.ImmutableTourneeModel;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.UUID;


import fr.sdis83.remocra.usecase.tournee.TourneeUseCase;
import fr.sdis83.remocra.usecase.synchro.SynchroUseCase;
import fr.sdis83.remocra.usecase.synchro.ValideIncomingMobile;
import fr.sdis83.remocra.repository.UtilisateursRepository;

import static fr.sdis83.remocra.repository.TypeDroitRepository.TypeDroitsPourMobile;


@Path("/mobile/synchro")
@Produces("application/json; charset=UTF-8")
@Consumes(MediaType.APPLICATION_JSON)
public class SynchroEndpoint {
    @Inject
    TourneeUseCase tourneeUseCase;

    @Inject
    UtilisateursRepository utilisateursRespository;


    @Inject
    SynchroUseCase synchroUseCase;

    @Inject
    ValideIncomingMobile valideIncomingMobile;


    @Inject
    ParamConfRepository paramConfRepository;


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


    @AuthDevice
    @Path("/createhydrant")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @POST
    public Response createHydrant(
            @FormParam("idHydrant")
            UUID idHydrant,
            @FormParam("lat")
            Double lat,
            @FormParam("lon")
            Double lon,
            @FormParam("code")
            String code,
            @FormParam("idGestionnaire")
            UUID idGestionnaire,
            @FormParam("observations")
            String observations,
            @FormParam("idNatureDeci")
            Long idNatureDeci,
            @FormParam("idNature")
            Long idNature
    ) {
        if(!synchroUseCase.getDroit(currentUser.get().userId(),
                TypeDroitsPourMobile.CREATION_PEI_MOBILE.getCodeDroitMobile()) ||
                paramConfRepository.getCreationPeiAppMobile().getValeur().equals("false")) {
            return accessDenied(TypeDroitsPourMobile.CREATION_PEI_MOBILE);
        }

        return synchroUseCase.insertHydrant(
                idHydrant,
                idGestionnaire,
                idNature,
                idNatureDeci,
                lon,
                lat,
                code,
                observations
        );
    }

    private Response accessDenied(TypeDroitsPourMobile typeDroit) {
        String s = "";
        switch (typeDroit) {
            case CREATION_PEI_MOBILE:
                s = "L'utilisateur " + currentUser.get().userId() + " n'a pas les droits de création de PEI.";
                break;
            case CREATION_GESTIONNAIRE_MOBILE:
                s = "L'utilisateur " + currentUser.get().userId() + " n'a pas les droits de création / modifications de gestionnaires.";
                break;
        }

        return Response.serverError()
                .entity(s)
                .build();
    }

    @AuthDevice
    @Path("/gestionnaires")
    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response getGestionnaire(
            @FormParam("idGestionnaire")
            UUID idGestionnaire,
            @FormParam("idRemocra")
            Long idRemocra,
            @FormParam("nomGestionnaire")
            String nomGestionnaire,
            @FormParam("codeGestionnaire")
            String codeGestionnaire
    ) {
        if (!synchroUseCase.getDroit(currentUser.get().userId(),
                TypeDroitsPourMobile.CREATION_GESTIONNAIRE_MOBILE.getCodeDroitMobile())) {
            return accessDenied(TypeDroitsPourMobile.CREATION_GESTIONNAIRE_MOBILE);
        }

        return synchroUseCase.insertGestionnaire(
                idRemocra,
                codeGestionnaire,
                nomGestionnaire,
                idGestionnaire
        );
    }

    @AuthDevice
    @Path("/contacts")
    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response getContact(
            @FormParam("idContact")
            UUID idContact,
            @FormParam("idRemocra")
            Long idRemocra,
            @FormParam("idGestionnaire")
            UUID idGestionnaire,
            @FormParam("nom")
            String nomContact,
            @FormParam("prenom")
            String prenomContact,
            @FormParam("fonction")
            String fonctionContact,
            @FormParam("civilite")
            String civiliteContact,
            @FormParam("codePostal")
            String codePostalContact,
            @FormParam("voie")
            String voieContact,
            @FormParam("suffixeVoie")
            String suffixeVoieContact,
            @FormParam("numeroVoie")
            String numeroVoieContact,
            @FormParam("lieuDit")
            String lieuDitContact,
            @FormParam("telephone")
            String telephoneContact,
            @FormParam("email")
            String emailContact,
            @FormParam("ville")
            String villeContact,
            @FormParam("pays")
            String paysContact
    ) {
        if (!synchroUseCase.getDroit(currentUser.get().userId(),
                TypeDroitsPourMobile.CREATION_GESTIONNAIRE_MOBILE.getCodeDroitMobile())) {
            return accessDenied(TypeDroitsPourMobile.CREATION_GESTIONNAIRE_MOBILE);
        }

        return synchroUseCase.insertContact(
                new ContactModel(
                        idRemocra,
                        null,
                        nomContact,
                        prenomContact,
                        civiliteContact,
                        fonctionContact,
                        codePostalContact,
                        voieContact,
                        numeroVoieContact,
                        suffixeVoieContact,
                        villeContact,
                        lieuDitContact,
                        paysContact,
                        telephoneContact,
                        emailContact),
                idContact,
                idGestionnaire

        );
    }

    @AuthDevice
    @Path("/contactsrole")
    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response getContactRole(
            @FormParam("idContact")
            UUID idContact,
            @FormParam("idRoleRemocra")
            Long idRoleRemocra
    ) {
        if (!synchroUseCase.getDroit(currentUser.get().userId(),
                TypeDroitsPourMobile.CREATION_GESTIONNAIRE_MOBILE.getCodeDroitMobile())) {
            return accessDenied(TypeDroitsPourMobile.CREATION_GESTIONNAIRE_MOBILE);
        }

        return synchroUseCase.insertContactRole(idContact, idRoleRemocra);
    }

    @AuthDevice
    @Path("/synchrohydrantvisite")
    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response synchroHydrantVisite(
            @FormParam("idHydrantVisite")
            UUID idHydrantVisite,
            @FormParam("idHydrant")
            Long idHydrant,
            @FormParam("date")
            String date,
            @FormParam("idTypeVisite")
            Long idTypeVisite,
            @FormParam("ctrDebitPression")
            boolean ctrDebitPression,
            @FormParam("agent1")
            String agent1,
            @FormParam("agent2")
            String agent2,
            @FormParam("debit")
            int debit,
            @FormParam("pression")
            double pression,
            @FormParam("pressionDyn")
            double pressionDyn,
            @FormParam("observations")
            String observations
    ) {
        // Gestion de la date
        String pattern = "yyyy-MM-dd HH:mm:ss";
        ZonedDateTime moment;
        try {
            moment = ZonedDateTime.parse(date, DateTimeFormatter.ofPattern(pattern).withZone(ZoneId.systemDefault()));
        } catch (DateTimeParseException dtpe) {
            return Response.serverError().entity("Le format de la date est incorrect : " + date).build();
        }

        return synchroUseCase.insertVisite(
                ImmutableHydrantVisiteModel.builder()
                        .idHydrantVisite(idHydrantVisite)
                        .idHydrant(idHydrant)
                        .date(moment)
                        .idTypeVisite(idTypeVisite)
                        .ctrDebitPression(ctrDebitPression)
                        .agent1(agent1)
                        .agent2(agent2)
                        .debit(debit)
                        .pression(pression)
                        .pressionDyn(pressionDyn)
                        .observations(observations)
                        .build()
        );
    }

    @AuthDevice
    @Path("/synchrohydrantvisiteanomalie")
    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response synchroHydrantVisiteAnomalies(
            @FormParam("idHydrantVisite")
            UUID idHydrantVisite,
            @FormParam("idAnomalie")
            Long idAnomalie
    ) {
        return synchroUseCase.insertHydrantVisiteAnomalie(idHydrantVisite, idAnomalie);
    }

    @AuthDevice
    @Path("/synchrotournee")
    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response synchroTournee(
           @FormParam("idTourneeRemocra")
           Long idTourneeRemocra,
           @FormParam("nom")
           String nom
    ) {
        return synchroUseCase.insertTournee(
                ImmutableTourneeModel.builder()
                        .idRemocra(idTourneeRemocra)
                        .affectation(currentUser.get().userId())
                        .nom(nom)
                        .build(),
            currentUser.get().userId());
    }

    @AuthDevice
    @Path("/incomingtoremocra")
    @PUT
    public Response incomingToRemocra() {
        try {
            valideIncomingMobile.execute(currentUser.get().userId());
            return Response.ok().build();
        } catch (Exception e) {
            return Response.serverError().entity(e.getMessage()).build();
        }
    }
}
