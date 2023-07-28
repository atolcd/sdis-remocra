package fr.sdis83.remocra.web.mobile;

import fr.sdis83.remocra.authn.AuthDevice;
import fr.sdis83.remocra.repository.ReferentielRepository;
import fr.sdis83.remocra.web.model.referentiel.CommuneModel;
import fr.sdis83.remocra.web.model.referentiel.ContactModel;
import fr.sdis83.remocra.web.model.referentiel.ContactRoleModel;
import fr.sdis83.remocra.web.model.referentiel.GestionnaireModel;
import fr.sdis83.remocra.web.model.referentiel.HydrantAnomalieModel;
import fr.sdis83.remocra.web.model.referentiel.HydrantModel;
import fr.sdis83.remocra.web.model.referentiel.RoleModel;
import fr.sdis83.remocra.web.model.referentiel.TypeHydrantAnomalieModel;
import fr.sdis83.remocra.web.model.referentiel.TypeHydrantCritereModel;
import fr.sdis83.remocra.web.model.referentiel.TypeHydrantModel;
import fr.sdis83.remocra.web.model.referentiel.TypeHydrantNatureDeciModel;
import fr.sdis83.remocra.web.model.referentiel.TypeHydrantNatureModel;
import fr.sdis83.remocra.web.model.referentiel.TypeHydrantSaisieModel;
import java.util.List;
import javax.inject.Inject;
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

  @GET
  @Path("/")
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
            referentielRepository.getTypeHydrantCritereList(),
            referentielRepository.getTypeHydrantSaisieList()
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
    public final List<TypeHydrantCritereModel> typesHydrantCritere;
    public final List<TypeHydrantSaisieModel> typesHydrantSaisie;

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
                               List<TypeHydrantCritereModel> typesHydrantCritere,
                               List<TypeHydrantSaisieModel> typesHydrantSaisie) {
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
      this.typesHydrantCritere = typesHydrantCritere;
      this.typesHydrantSaisie = typesHydrantSaisie;
    }
  }
}
