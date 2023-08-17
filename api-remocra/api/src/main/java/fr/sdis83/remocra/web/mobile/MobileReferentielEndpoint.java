package fr.sdis83.remocra.web.mobile;

import fr.sdis83.remocra.authn.AuthDevice;
import fr.sdis83.remocra.authn.CurrentUser;
import fr.sdis83.remocra.authn.UserInfo;
import fr.sdis83.remocra.repository.ReferentielRepository;
import fr.sdis83.remocra.web.model.referentiel.CommuneModel;
import fr.sdis83.remocra.web.model.referentiel.ContactModel;
import fr.sdis83.remocra.web.model.referentiel.ContactRoleModel;
import fr.sdis83.remocra.web.model.referentiel.GestionnaireModel;
import fr.sdis83.remocra.web.model.referentiel.HydrantAnomalieModel;
import fr.sdis83.remocra.web.model.referentiel.HydrantModel;
import fr.sdis83.remocra.web.model.referentiel.RoleModel;
import fr.sdis83.remocra.web.model.referentiel.TypeHydrantAnomalieModel;
import fr.sdis83.remocra.web.model.referentiel.TypeHydrantAnomalieNatureModel;
import fr.sdis83.remocra.web.model.referentiel.TypeHydrantCritereModel;
import fr.sdis83.remocra.web.model.referentiel.TypeHydrantModel;
import fr.sdis83.remocra.web.model.referentiel.TypeHydrantNatureDeciModel;
import fr.sdis83.remocra.web.model.referentiel.TypeHydrantNatureModel;
import fr.sdis83.remocra.web.model.referentiel.TypeHydrantAnomalieNatureSaisieModel;
import fr.sdis83.remocra.web.model.referentiel.TypeHydrantSaisieModel;
import io.swagger.v3.oas.annotations.Operation;
import fr.sdis83.remocra.util.GlobalConstants;
import fr.sdis83.remocra.web.model.authn.ParamConfModel;
import fr.sdis83.remocra.web.model.mobilemodel.TypeDroitModel;

import java.util.List;
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

  @Inject
  ReferentielRepository referentielRepository;

  @Inject
  @CurrentUser
  Provider<UserInfo> currentUser;

  @GET
  @Path("/")
  @Operation(summary = "Retourne le référentiel pour l'application mobile", tags = {GlobalConstants.REMOCRA_MOBILE_TAG}, hidden = true)
  @AuthDevice
  public Response getReferentiel() {
    return Response.ok(
        new ReferentielResponse(
            referentielRepository.getCommuneList(),
            referentielRepository.getHydrantList(),
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
            referentielRepository.getTypeHydrantAnomalieNatureSaisieList(),
            referentielRepository.getTypeHydrantCritereList(),
            referentielRepository.getTypeHydrantSaisieList(),
            referentielRepository.getParamConfMobileList(),
            referentielRepository.getTypeDroitMobileList(currentUser.get().userId())
        )
    ).build();
  }

  static class ReferentielResponse {
    public final List<CommuneModel> communes;
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
    public final List<ParamConfModel> paramsConf;

    public final List<TypeDroitModel> typesDroit;

    public ReferentielResponse(List<CommuneModel> communes,
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
                               List<ParamConfModel> paramsConf,
                               List<TypeDroitModel> typesDroit) {
      this.communes = communes;
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
    }
  }
}
