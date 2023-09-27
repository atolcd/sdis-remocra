package fr.sdis83.remocra.repository;

import static fr.sdis83.remocra.db.model.remocra.Tables.COMMUNE;
import static fr.sdis83.remocra.db.model.remocra.Tables.CONTACT;
import static fr.sdis83.remocra.db.model.remocra.Tables.CONTACT_ROLES;
import static fr.sdis83.remocra.db.model.remocra.Tables.DROIT;
import static fr.sdis83.remocra.db.model.remocra.Tables.GESTIONNAIRE;
import static fr.sdis83.remocra.db.model.remocra.Tables.HYDRANT;
import static fr.sdis83.remocra.db.model.remocra.Tables.HYDRANT_ANOMALIES;
import static fr.sdis83.remocra.db.model.remocra.Tables.PARAM_CONF;
import static fr.sdis83.remocra.db.model.remocra.Tables.PROFIL_DROIT;
import static fr.sdis83.remocra.db.model.remocra.Tables.PROFIL_ORGANISME_UTILISATEUR_DROIT;
import static fr.sdis83.remocra.db.model.remocra.Tables.PROFIL_UTILISATEUR;
import static fr.sdis83.remocra.db.model.remocra.Tables.ROLE;
import static fr.sdis83.remocra.db.model.remocra.Tables.TYPE_DROIT;
import static fr.sdis83.remocra.db.model.remocra.Tables.TYPE_HYDRANT;
import static fr.sdis83.remocra.db.model.remocra.Tables.TYPE_HYDRANT_ANOMALIE;
import static fr.sdis83.remocra.db.model.remocra.Tables.TYPE_HYDRANT_ANOMALIE_NATURE;
import static fr.sdis83.remocra.db.model.remocra.Tables.TYPE_HYDRANT_ANOMALIE_NATURE_SAISIES;
import static fr.sdis83.remocra.db.model.remocra.Tables.TYPE_HYDRANT_CRITERE;
import static fr.sdis83.remocra.db.model.remocra.Tables.TYPE_HYDRANT_NATURE;
import static fr.sdis83.remocra.db.model.remocra.Tables.TYPE_HYDRANT_NATURE_DECI;
import static fr.sdis83.remocra.db.model.remocra.Tables.TYPE_HYDRANT_SAISIE;
import static fr.sdis83.remocra.db.model.remocra.Tables.UTILISATEUR;

import fr.sdis83.remocra.util.GlobalConstants;
import fr.sdis83.remocra.web.model.authn.ParamConfModel;
import fr.sdis83.remocra.web.model.mobilemodel.TypeDroitModel;
import fr.sdis83.remocra.web.model.referentiel.CommuneModel;
import fr.sdis83.remocra.web.model.referentiel.ContactModel;
import fr.sdis83.remocra.web.model.referentiel.ContactRoleModel;
import fr.sdis83.remocra.web.model.referentiel.GestionnaireModel;
import fr.sdis83.remocra.web.model.referentiel.HydrantAnomalieModel;
import fr.sdis83.remocra.web.model.referentiel.HydrantModel;
import fr.sdis83.remocra.web.model.referentiel.RoleModel;
import fr.sdis83.remocra.web.model.referentiel.TypeHydrantAnomalieModel;
import fr.sdis83.remocra.web.model.referentiel.TypeHydrantAnomalieNatureModel;
import fr.sdis83.remocra.web.model.referentiel.TypeHydrantAnomalieNatureSaisieModel;
import fr.sdis83.remocra.web.model.referentiel.TypeHydrantCritereModel;
import fr.sdis83.remocra.web.model.referentiel.TypeHydrantModel;
import fr.sdis83.remocra.web.model.referentiel.TypeHydrantNatureDeciModel;
import fr.sdis83.remocra.web.model.referentiel.TypeHydrantNatureModel;
import fr.sdis83.remocra.web.model.referentiel.TypeHydrantSaisieModel;
import java.util.List;
import javax.inject.Inject;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.impl.DSL;

public class ReferentielRepository {

  private final DSLContext context;
  private final String NOM_GROUPE_MOBILE = "Mobile";

  @Inject
  public ReferentielRepository(DSLContext context) {
    this.context = context;
  }

  public List<CommuneModel> getCommuneList() {
    return context
        .select(COMMUNE.ID.as("idRemocra"), COMMUNE.CODE, COMMUNE.NOM, COMMUNE.INSEE)
        .from(COMMUNE)
        .fetchInto(CommuneModel.class);
  }

  public List<HydrantModel> getHydrantList() {

    Field<Object> X = DSL.field("round(st_x({0})::numeric, 2)", HYDRANT.GEOMETRIE).as("x");
    Field<Object> Y = DSL.field("round(st_y({0})::numeric, 2)", HYDRANT.GEOMETRIE).as("y");
    Field<Object> LON =
        DSL.field("round(st_x(st_transform({0}, 4326))::numeric, 8)", HYDRANT.GEOMETRIE).as("lon");
    Field<Object> LAT =
        DSL.field("round(st_y(st_transform({0}, 4326))::numeric, 8)", HYDRANT.GEOMETRIE).as("lat");

    return context
        .select(
            HYDRANT.ID.as("idRemocra"),
            HYDRANT.NATURE.as("idNature"),
            HYDRANT.NATURE_DECI.as("idNatureDeci"),
            HYDRANT.DISPO_HBE,
            HYDRANT.DISPO_TERRESTRE,
            X,
            Y,
            LON,
            LAT,
            HYDRANT.NUMERO,
            HYDRANT.CODE,
            HYDRANT.COMMUNE.as("idCommune"),
            HYDRANT.VOIE,
            HYDRANT.VOIE2,
            HYDRANT.SUFFIXE_VOIE,
            HYDRANT.LIEU_DIT,
            HYDRANT.OBSERVATION,
            HYDRANT.GESTIONNAIRE.as("idRemocraGestionnaire"))
        .from(HYDRANT)
        .fetchInto(HydrantModel.class);
  }

  public List<HydrantAnomalieModel> getHydrantAnomalieList() {
    return context
        .selectDistinct(
            HYDRANT_ANOMALIES.HYDRANT.as("idHydrant"), HYDRANT_ANOMALIES.ANOMALIES.as("idAnomalie"))
        .from(HYDRANT_ANOMALIES)
        .where(
            HYDRANT_ANOMALIES.ANOMALIES.in(
                context
                    .selectDistinct(TYPE_HYDRANT_ANOMALIE.ID)
                    .from(TYPE_HYDRANT_ANOMALIE)
                    .where(TYPE_HYDRANT_ANOMALIE.CRITERE.isNotNull())
                    .and(
                        TYPE_HYDRANT_ANOMALIE.ID.in(
                            context
                                .selectDistinct(TYPE_HYDRANT_ANOMALIE_NATURE.ANOMALIE)
                                .from(TYPE_HYDRANT_ANOMALIE_NATURE)))))
        .fetchInto(HydrantAnomalieModel.class);
  }

  public List<GestionnaireModel> getGestionnaireList() {
    return context
        .select(
            GESTIONNAIRE.ID.as("idRemocra"),
            GESTIONNAIRE.CODE,
            GESTIONNAIRE.NOM,
            GESTIONNAIRE.ACTIF)
        .from(GESTIONNAIRE)
        .fetchInto(GestionnaireModel.class);
  }

  public List<ContactModel> getContactList() {
    return context
        .select(
            CONTACT.ID.as("idRemocra"),
            CONTACT.ID_APPARTENANCE.as("idRemocraGestionnaire"),
            CONTACT.FONCTION,
            CONTACT.CIVILITE,
            CONTACT.NOM,
            CONTACT.PRENOM,
            CONTACT.NUMERO_VOIE,
            CONTACT.VOIE,
            CONTACT.SUFFIXE_VOIE,
            CONTACT.LIEU_DIT,
            CONTACT.CODE_POSTAL,
            CONTACT.VILLE,
            CONTACT.PAYS,
            CONTACT.TELEPHONE,
            CONTACT.EMAIL)
        .from(CONTACT)
        .where(CONTACT.APPARTENANCE.eq("GESTIONNAIRE"))
        .fetchInto(ContactModel.class);
  }

  public List<RoleModel> getRoleList() {
    return context
        .select(ROLE.ID.as("idRemocra"), ROLE.CODE, ROLE.NOM, ROLE.ACTIF)
        .from(ROLE)
        .fetchInto(RoleModel.class);
  }

  public List<ContactRoleModel> getContactRoleList() {
    return context
        .selectDistinct(CONTACT_ROLES.CONTACT.as("idContact"), CONTACT_ROLES.ROLES.as("idRole"))
        .from(CONTACT_ROLES)
        .where(
            CONTACT_ROLES.CONTACT.in(
                context
                    .selectDistinct(CONTACT.ID)
                    .from(CONTACT)
                    .where(CONTACT.APPARTENANCE.eq("GESTIONNAIRE"))))
        .fetchInto(ContactRoleModel.class);
  }

  public List<TypeHydrantModel> getTypeHydrantList() {
    return context
        .select(
            TYPE_HYDRANT.ID.as("idRemocra"),
            TYPE_HYDRANT.CODE,
            TYPE_HYDRANT.NOM,
            TYPE_HYDRANT.ACTIF)
        .from(TYPE_HYDRANT)
        .fetchInto(TypeHydrantModel.class);
  }

  public List<TypeHydrantNatureModel> getTypeHydrantNatureList() {
    return context
        .select(
            TYPE_HYDRANT_NATURE.ID.as("idRemocra"),
            TYPE_HYDRANT_NATURE.CODE,
            TYPE_HYDRANT_NATURE.NOM,
            TYPE_HYDRANT_NATURE.TYPE_HYDRANT.as("idTypeHydrant"),
            TYPE_HYDRANT_NATURE.ACTIF)
        .from(TYPE_HYDRANT_NATURE)
        .fetchInto(TypeHydrantNatureModel.class);
  }

  public List<TypeHydrantNatureDeciModel> getTypeHydrantNatureDeciList() {
    return context
        .select(
            TYPE_HYDRANT_NATURE_DECI.ID.as("idRemocra"),
            TYPE_HYDRANT_NATURE_DECI.CODE,
            TYPE_HYDRANT_NATURE_DECI.NOM,
            TYPE_HYDRANT_NATURE_DECI.ACTIF)
        .from(TYPE_HYDRANT_NATURE_DECI)
        .fetchInto(TypeHydrantNatureDeciModel.class);
  }

  public List<TypeHydrantAnomalieModel> getTypeHydrantAnomalieList() {
    return context
        .select(
            TYPE_HYDRANT_ANOMALIE.ID.as("idRemocra"),
            TYPE_HYDRANT_ANOMALIE.CODE,
            TYPE_HYDRANT_ANOMALIE.NOM,
            TYPE_HYDRANT_ANOMALIE.CRITERE.as("idCritere"),
            TYPE_HYDRANT_ANOMALIE.ACTIF)
        .from(TYPE_HYDRANT_ANOMALIE)
        .where(TYPE_HYDRANT_ANOMALIE.CRITERE.isNotNull())
        .and(
            TYPE_HYDRANT_ANOMALIE.ID.in(
                context
                    .selectDistinct(TYPE_HYDRANT_ANOMALIE_NATURE.ANOMALIE)
                    .from(TYPE_HYDRANT_ANOMALIE_NATURE)))
        .fetchInto(TypeHydrantAnomalieModel.class);
  }

  public List<TypeHydrantAnomalieNatureModel> getTypeHydrantAnomalieNatureList() {
    return context
        .select(
            TYPE_HYDRANT_ANOMALIE_NATURE.ID.as("idRemocra"),
            TYPE_HYDRANT_ANOMALIE_NATURE.ANOMALIE.as("idTypeHydrantAnomalie"),
            TYPE_HYDRANT_ANOMALIE_NATURE.NATURE.as("idTypeHydrantNature"),
            TYPE_HYDRANT_ANOMALIE_NATURE.VAL_INDISPO_TERRESTRE,
            TYPE_HYDRANT_ANOMALIE_NATURE.VAL_INDISPO_HBE,
            TYPE_HYDRANT_ANOMALIE_NATURE.VAL_INDISPO_ADMIN)
        .from(TYPE_HYDRANT_ANOMALIE_NATURE)
        .where(
            TYPE_HYDRANT_ANOMALIE_NATURE.ANOMALIE.in(
                context
                    .selectDistinct(TYPE_HYDRANT_ANOMALIE.ID)
                    .from(TYPE_HYDRANT_ANOMALIE)
                    .where(TYPE_HYDRANT_ANOMALIE.CRITERE.isNotNull())))
        .fetchInto(TypeHydrantAnomalieNatureModel.class);
  }

  public List<TypeHydrantAnomalieNatureSaisieModel> getTypeHydrantAnomalieNatureSaisieList() {
    return context
        .select(
            TYPE_HYDRANT_ANOMALIE_NATURE_SAISIES.TYPE_HYDRANT_ANOMALIE_NATURE.as(
                "idTypeHydrantAnomalieNature"),
            TYPE_HYDRANT_ANOMALIE_NATURE_SAISIES.SAISIES.as("idTypeHydrantSaisie"))
        .from(TYPE_HYDRANT_ANOMALIE_NATURE_SAISIES)
        .where(
            TYPE_HYDRANT_ANOMALIE_NATURE_SAISIES.TYPE_HYDRANT_ANOMALIE_NATURE.in(
                context
                    .selectDistinct(TYPE_HYDRANT_ANOMALIE_NATURE.ID)
                    .from(TYPE_HYDRANT_ANOMALIE_NATURE)
                    .where(
                        TYPE_HYDRANT_ANOMALIE_NATURE.ANOMALIE.in(
                            context
                                .selectDistinct(TYPE_HYDRANT_ANOMALIE.ID)
                                .from(TYPE_HYDRANT_ANOMALIE)
                                .where(TYPE_HYDRANT_ANOMALIE.CRITERE.isNotNull())))))
        .fetchInto(TypeHydrantAnomalieNatureSaisieModel.class);
  }

  public List<TypeHydrantCritereModel> getTypeHydrantCritereList() {
    return context
        .select(
            TYPE_HYDRANT_CRITERE.ID.as("idRemocra"),
            TYPE_HYDRANT_CRITERE.CODE,
            TYPE_HYDRANT_CRITERE.NOM,
            TYPE_HYDRANT_CRITERE.ACTIF)
        .from(TYPE_HYDRANT_CRITERE)
        .fetchInto(TypeHydrantCritereModel.class);
  }

  public List<TypeHydrantSaisieModel> getTypeHydrantSaisieList() {
    return context
        .select(
            TYPE_HYDRANT_SAISIE.ID.as("idRemocra"),
            TYPE_HYDRANT_SAISIE.CODE,
            TYPE_HYDRANT_SAISIE.NOM,
            TYPE_HYDRANT_SAISIE.ACTIF)
        .from(TYPE_HYDRANT_SAISIE)
        .where(TYPE_HYDRANT_SAISIE.CODE.ne(GlobalConstants.TypeVisite.LECTURE.getCode()))
        .fetchInto(TypeHydrantSaisieModel.class);
  }

  public List<ParamConfModel> getParamConfMobileList() {
    return context
        .selectFrom(PARAM_CONF)
        .where(PARAM_CONF.NOMGROUPE.eq(NOM_GROUPE_MOBILE))
        .fetchInto(ParamConfModel.class);
  }

  public List<TypeDroitModel> getTypeDroitMobileList(Long idUtilisateur) {
    return context
        .select(TYPE_DROIT.ID, TYPE_DROIT.CODE, TYPE_DROIT.CATEGORIE)
        .from(TYPE_DROIT)
        .join(DROIT)
        .on(DROIT.TYPE_DROIT.eq(TYPE_DROIT.ID))
        .join(PROFIL_DROIT)
        .on(PROFIL_DROIT.ID.eq(DROIT.PROFIL_DROIT))
        .join(PROFIL_ORGANISME_UTILISATEUR_DROIT)
        .on(PROFIL_ORGANISME_UTILISATEUR_DROIT.PROFIL_DROIT.eq(PROFIL_DROIT.ID))
        .join(PROFIL_UTILISATEUR)
        .on(PROFIL_UTILISATEUR.ID.eq(PROFIL_ORGANISME_UTILISATEUR_DROIT.PROFIL_UTILISATEUR))
        .join(UTILISATEUR)
        .on(UTILISATEUR.PROFIL_UTILISATEUR.eq(PROFIL_UTILISATEUR.ID))
        .where(TYPE_DROIT.CATEGORIE.eq(NOM_GROUPE_MOBILE))
        .and(UTILISATEUR.ID.eq(idUtilisateur))
        .fetchInto(TypeDroitModel.class);
  }
}
