package fr.sdis83.remocra.web.mobile;

import fr.sdis83.remocra.authn.AuthDevice;
import fr.sdis83.remocra.authn.CurrentUser;
import fr.sdis83.remocra.authn.UserInfo;
import fr.sdis83.remocra.repository.ParametreRepository;
import fr.sdis83.remocra.repository.ReferentielRepository;
import fr.sdis83.remocra.repository.UtilisateursRepository;
import fr.sdis83.remocra.usecase.referentiel.BuildAdresseCompleteUseCase;
import fr.sdis83.remocra.usecase.referentiel.GetTypeVisiteUtilisateur;
import fr.sdis83.remocra.usecase.referentiel.PeiCaracteristiquesUseCase;
import fr.sdis83.remocra.util.GlobalConstants;
import fr.sdis83.remocra.web.model.mobilemodel.TypeDroitModel;
import fr.sdis83.remocra.web.model.referentiel.ContactModel;
import fr.sdis83.remocra.web.model.referentiel.ContactRoleModel;
import fr.sdis83.remocra.web.model.referentiel.GestionnaireModel;
import fr.sdis83.remocra.web.model.referentiel.HydrantAnomalieModel;
import fr.sdis83.remocra.web.model.referentiel.HydrantModel;
import fr.sdis83.remocra.web.model.referentiel.ParametreData;
import fr.sdis83.remocra.web.model.referentiel.RoleModel;
import fr.sdis83.remocra.web.model.referentiel.TypeHydrantAnomalieModel;
import fr.sdis83.remocra.web.model.referentiel.TypeHydrantAnomalieNatureModel;
import fr.sdis83.remocra.web.model.referentiel.TypeHydrantAnomalieNatureSaisieModel;
import fr.sdis83.remocra.web.model.referentiel.TypeHydrantCritereModel;
import fr.sdis83.remocra.web.model.referentiel.TypeHydrantModel;
import fr.sdis83.remocra.web.model.referentiel.TypeHydrantNatureDeciModel;
import fr.sdis83.remocra.web.model.referentiel.TypeHydrantNatureModel;
import fr.sdis83.remocra.web.model.referentiel.TypeHydrantSaisieModel;
import io.swagger.v3.oas.annotations.Operation;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.inject.Inject;
import javax.inject.Provider;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/mobile/referentiel")
@Produces("application/json; charset=UTF-8")
@Consumes(MediaType.APPLICATION_JSON)
public class MobileReferentielEndpoint {

  @Inject ReferentielRepository referentielRepository;
  @Inject UtilisateursRepository utilisateursRepository;
  @Inject ParametreRepository parametreRepository;
  @Inject BuildAdresseCompleteUseCase buildAdresseCompleteUseCase;
  @Inject PeiCaracteristiquesUseCase peiCaracteristiquesUseCase;
  @Inject GetTypeVisiteUtilisateur getTypeVisiteUtilisateur;

  @Inject @CurrentUser Provider<UserInfo> currentUser;

  @GET
  @Path("/")
  @Operation(
      summary = "Retourne le référentiel pour l'application mobile",
      tags = {GlobalConstants.REMOCRA_MOBILE_TAG},
      hidden = true)
  @AuthDevice
  public Response getReferentiel() {
    Long idUtilisateur = currentUser.get().userId();

    List<ParametreData> paramsConf = referentielRepository.getParamConfMobileList();
    paramsConf.addAll(parametreRepository.getParametresMobile());

    List<TypeHydrantSaisieModel> typeVisiteUtilisateur =
        getTypeVisiteUtilisateur.execute(
            idUtilisateur, referentielRepository.getTypeHydrantSaisieList());

    return Response.ok(
            new ReferentielResponse(
                buildAdresseCompleteUseCase.execute(referentielRepository.getHydrantList()),
                referentielRepository.getHydrantAnomalieList(),
                referentielRepository.getGestionnaireList(),
                referentielRepository.getContactList(),
                referentielRepository.getRoleList(),
                referentielRepository.getContactRoleList(),
                referentielRepository.getTypeHydrantList(),
                referentielRepository.getTypeHydrantNatureList(),
                referentielRepository.getTypeHydrantNatureDeciList(),
                referentielRepository.getTypeHydrantAnomalieList(),
                referentielRepository.getTypeHydrantAnomalieNatureList(),
                referentielRepository.getTypeHydrantAnomalieNatureSaisieList(
                    typeVisiteUtilisateur.stream()
                        .map(TypeHydrantSaisieModel::getIdRemocra)
                        .collect(Collectors.toList())),
                referentielRepository.getTypeHydrantCritereList(),
                typeVisiteUtilisateur,
                paramsConf,
                referentielRepository.getTypeDroitMobileList(idUtilisateur),
                utilisateursRepository.getNomPrenom(idUtilisateur),
                peiCaracteristiquesUseCase.getPeiCaracteristiques()))
        .build();
  }

  static class ReferentielResponse {
    public final List<HydrantModel> hydrants;
    public final List<HydrantAnomalieModel> hydrantsAnomalies;
    public final List<GestionnaireModel> gestionnaires;
    public final List<ContactModel> contacts;
    public final List<RoleModel> roles;
    public final List<ContactRoleModel> contactsRoles;
    public final List<TypeHydrantModel> typesHydrant;
    public final List<TypeHydrantNatureModel> typesHydrantNature;
    public final List<TypeHydrantNatureDeciModel> typesHydrantNatureDeci;
    public final List<TypeHydrantAnomalieModel> typesHydrantAnomalie;
    public final List<TypeHydrantAnomalieNatureModel> typesHydrantAnomalieNature;
    public final List<TypeHydrantAnomalieNatureSaisieModel> typesHydrantAnomalieNatureSaisie;
    public final List<TypeHydrantCritereModel> typesHydrantCritere;
    public final List<TypeHydrantSaisieModel> typesHydrantSaisie;
    public final List<ParametreData> paramsConf;
    public final List<TypeDroitModel> typesDroit;
    public final String utilisateurConnecte;
    public final Map<Long, String> peiCaracteristiques;

    public ReferentielResponse(
        List<HydrantModel> hydrants,
        List<HydrantAnomalieModel> hydrantsAnomalies,
        List<GestionnaireModel> gestionnaires,
        List<ContactModel> contacts,
        List<RoleModel> roles,
        List<ContactRoleModel> contactsRoles,
        List<TypeHydrantModel> typesHydrant,
        List<TypeHydrantNatureModel> typesHydrantNature,
        List<TypeHydrantNatureDeciModel> typesHydrantNatureDeci,
        List<TypeHydrantAnomalieModel> typesHydrantAnomalie,
        List<TypeHydrantAnomalieNatureModel> typesHydrantAnomalieNature,
        List<TypeHydrantAnomalieNatureSaisieModel> typesHydrantAnomalieNatureSaisie,
        List<TypeHydrantCritereModel> typesHydrantCritere,
        List<TypeHydrantSaisieModel> typesHydrantSaisie,
        List<ParametreData> paramsConf,
        List<TypeDroitModel> typesDroit,
        String utilisateurConnecte,
        Map<Long, String> peiCaracteristiques) {
      this.hydrants = hydrants;
      this.hydrantsAnomalies = hydrantsAnomalies;
      this.gestionnaires = gestionnaires;
      this.contacts = contacts;
      this.roles = roles;
      this.contactsRoles = contactsRoles;
      this.typesHydrant = typesHydrant;
      this.typesHydrantNature = typesHydrantNature;
      this.typesHydrantNatureDeci = typesHydrantNatureDeci;
      this.typesHydrantAnomalie = typesHydrantAnomalie;
      this.typesHydrantAnomalieNature = typesHydrantAnomalieNature;
      this.typesHydrantAnomalieNatureSaisie = typesHydrantAnomalieNatureSaisie;
      this.typesHydrantCritere = typesHydrantCritere;
      this.typesHydrantSaisie = typesHydrantSaisie;
      this.paramsConf = paramsConf;
      this.typesDroit = typesDroit;
      this.utilisateurConnecte = utilisateurConnecte;
      this.peiCaracteristiques = peiCaracteristiques;
    }
  }
}
