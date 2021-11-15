package fr.sdis83.remocra.usecase.pei;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.google.inject.persist.Transactional;
import fr.sdis83.remocra.authn.CurrentUser;
import fr.sdis83.remocra.authn.UserInfo;
import fr.sdis83.remocra.db.model.remocra.tables.Organisme;
import fr.sdis83.remocra.db.model.remocra.tables.pojos.HydrantPena;
import fr.sdis83.remocra.db.model.remocra.tables.pojos.HydrantPibi;
import fr.sdis83.remocra.db.model.remocra.tables.pojos.TypeHydrantModele;
import fr.sdis83.remocra.db.model.tracabilite.Tables;
import fr.sdis83.remocra.db.model.tracabilite.tables.Hydrant;
import fr.sdis83.remocra.db.model.tracabilite.tables.HydrantVisite;
import fr.sdis83.remocra.repository.PeiRepository;
import fr.sdis83.remocra.web.exceptions.ResponseException;
import fr.sdis83.remocra.web.model.pei.PeiDiffModel;
import fr.sdis83.remocra.web.model.pei.PeiForm;
import fr.sdis83.remocra.web.model.pei.PeiModel;
import fr.sdis83.remocra.web.model.pei.PeiSpecifiqueModel;
import fr.sdis83.remocra.web.serializer.PeiDiffModelSerializer;
import org.jooq.DSLContext;
import org.jooq.Record3;
import org.jooq.SelectConditionStep;
import org.jooq.exception.DataAccessException;
import org.jooq.impl.DSL;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.ws.rs.core.Response;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static fr.sdis83.remocra.db.model.remocra.Tables.HYDRANT;
import static fr.sdis83.remocra.db.model.remocra.Tables.HYDRANT_PENA;
import static fr.sdis83.remocra.db.model.remocra.Tables.HYDRANT_PIBI;
import static fr.sdis83.remocra.db.model.remocra.Tables.ORGANISME;
import static fr.sdis83.remocra.db.model.remocra.Tables.TYPE_HYDRANT_DIAMETRE;
import static fr.sdis83.remocra.db.model.remocra.Tables.TYPE_HYDRANT_MARQUE;
import static fr.sdis83.remocra.db.model.remocra.Tables.TYPE_HYDRANT_MATERIAU;
import static fr.sdis83.remocra.db.model.remocra.Tables.TYPE_HYDRANT_MODELE;
import static fr.sdis83.remocra.db.model.remocra.Tables.TYPE_RESEAU_ALIMENTATION;
import static fr.sdis83.remocra.db.model.remocra.Tables.TYPE_RESEAU_CANALISATION;
import static fr.sdis83.remocra.db.model.remocra.Tables.UTILISATEUR;

public class PeiUseCase {

  @Inject
  PeiRepository peiRepository;

  @Inject @CurrentUser
  Provider<UserInfo> currentUser;

  private final DSLContext context;

  @Inject
  public PeiUseCase(DSLContext context) {
        this.context = context;
    }

  public List<PeiModel> getAll(String insee, String type, String codeNature, String natureDeci, Integer limit, Integer start) {
    return peiRepository.getAll(insee, type, codeNature, natureDeci, limit, start,
      currentUser.get().userId(), currentUser.get().type());
  }

  public PeiSpecifiqueModel getPeiSpecifique(String numero) throws ResponseException {
    if(!this.peiRepository.peiExist(numero)) {
     throw new ResponseException(Response.Status.BAD_REQUEST, "1000 : Le numéro spécifié ne correspond à aucun hydrant");
    }

    if(!this.isPeiAccessible(numero)) {
       throw new ResponseException(Response.Status.FORBIDDEN, "1300 : Le numéro spécifié ne correspond à aucun hydrant qui vous est accessible");
    }
    return peiRepository.getPeiSpecifique(numero);
  }

  public String getPeiCaracteristiques(String numero) throws ResponseException, JsonProcessingException {
    if(!this.peiRepository.peiExist(numero)) {
     throw new ResponseException(Response.Status.BAD_REQUEST, "1000 : Le numéro spécifié ne correspond à aucun hydrant");
    }

    if(!this.isPeiAccessible(numero)) {
       throw new ResponseException(Response.Status.FORBIDDEN, "1300 : Le numéro spécifié ne correspond à aucun hydrant qui vous est accessible");
    }

    return peiRepository.getPeiCaracteristiques(numero);
  }

  public String updatePeiCaracteristiques(String numero, PeiForm peiform) throws ResponseException {
    if(!this.peiRepository.peiExist(numero)) {
     throw new ResponseException(Response.Status.BAD_REQUEST, "1000 : Le numéro spécifié ne correspond à aucun hydrant");
    }

    if(!this.userCanEditPei(numero) || !this.isPeiAccessible(numero)) {
       throw new ResponseException(Response.Status.FORBIDDEN, "1300 : Le numéro spécifié ne correspond à aucun hydrant qui vous est accessible");
    }

    String typePei = context.select(HYDRANT.CODE)
      .from(HYDRANT)
      .where(HYDRANT.NUMERO.eq(numero))
      .fetchOneInto(String.class);

    if(typePei.equalsIgnoreCase("PIBI")){
      return updatePibiCaracteristiques(numero, peiform);
    } else{
      return updatePenaCaracteristiques(numero, peiform);
    }

  }

  @Transactional
  public String updatePibiCaracteristiques(String numero, PeiForm peiForm) throws ResponseException {
    try{
      if(peiForm.peiJumele() != null){
        String error = peiRepository.jumelagePei(numero, peiForm.peiJumele());
        if(error != null) {
          throw new ResponseException(Response.Status.METHOD_NOT_ALLOWED, error);
        }
      }
      HydrantPibi hydrantPibi = context
        .select(HYDRANT_PIBI.fields())
        .from(HYDRANT_PIBI)
        .join(HYDRANT).on(HYDRANT.ID.eq(HYDRANT_PIBI.ID))
        .where(HYDRANT.NUMERO.equalIgnoreCase(numero))
        .fetchOneInto(HydrantPibi.class);

      Long idDiametre = null;
      Long idMarque = null;
      Long idModele = null;
      Long idNatureReseau = null;
      Long idNatureCanalisation = null;

      if(peiForm.codeDiametre() != null){
        idDiametre = context.select(TYPE_HYDRANT_DIAMETRE.ID)
          .from(TYPE_HYDRANT_DIAMETRE)
          .where(TYPE_HYDRANT_DIAMETRE.CODE.eq(peiForm.codeDiametre().toUpperCase()))
          .fetchOneInto(Long.class);
        if(idDiametre == null) {
          throw new ResponseException(Response.Status.BAD_REQUEST, "1001 : Le code du diamètre saisi ne correspond à aucune valeur connue");
        }
      }
      hydrantPibi.setDiametre(idDiametre);

      if(peiForm.codeMarque() != null){
        idMarque = context.select(TYPE_HYDRANT_MARQUE.ID)
          .from(TYPE_HYDRANT_MARQUE)
          .where(TYPE_HYDRANT_MARQUE.CODE.eq(peiForm.codeMarque().toUpperCase()))
          .fetchOneInto(Long.class);
        if(idMarque == null){
          throw new ResponseException(Response.Status.BAD_REQUEST, "1002 : Le code de marque saisi ne correspond à aucune valeur connue");
        } else {
          hydrantPibi.setModele(null); // Changement de marque => on met le modèle à null
        }
      }
      hydrantPibi.setMarque(idMarque);

      if(peiForm.codeModele() != null){
        List<TypeHydrantModele> modeles = context.select(TYPE_HYDRANT_MODELE.fields())
          .from(TYPE_HYDRANT_MODELE)
          .where(TYPE_HYDRANT_MODELE.CODE.upper().eq(peiForm.codeModele().toUpperCase()))
          .fetchInto(TypeHydrantModele.class);

        if(modeles.size() == 0){
          throw new ResponseException(Response.Status.BAD_REQUEST, "1003 : Le code de modèle saisi ne correspond à aucune valeur connue");
        }

        Long marque = (peiForm.codeMarque() != null) ? idMarque : hydrantPibi.getMarque();
        boolean matchFound = false;
        for(TypeHydrantModele modele : modeles) {
          if(modele.getMarque().equals(marque)) {
            idModele = modele.getId();
            matchFound = true;
          }
        }

        // Parmis les modèles retournés, aucun ne correspond à la marque renseignée
        if(!matchFound) {
          throw new ResponseException(Response.Status.BAD_REQUEST, "1008 : Le code de modèle saisi n'appartient pas à la marque renseignée");
        }
      }
      hydrantPibi.setModele(idModele);

      if(peiForm.codeNatureReseau() != null){
        idNatureReseau = context.select(TYPE_RESEAU_ALIMENTATION.ID)
          .from(TYPE_RESEAU_ALIMENTATION)
          .where(TYPE_RESEAU_ALIMENTATION.CODE.eq(peiForm.codeNatureReseau().toUpperCase()))
          .fetchOneInto(Long.class);
        if(idNatureReseau == null){
            throw new ResponseException(Response.Status.BAD_REQUEST, "1004 : Le code de nature du réseau saisi ne correspond à aucune valeur connue");
        }
      }
      hydrantPibi.setTypeReseauAlimentation(idNatureReseau);

      if(peiForm.codeNatureCanalisation() != null){
        idNatureCanalisation = context.select(TYPE_RESEAU_CANALISATION.ID)
          .from(TYPE_RESEAU_CANALISATION)
          .where(TYPE_RESEAU_CANALISATION.CODE.eq(peiForm.codeNatureCanalisation().toUpperCase()))
          .fetchOneInto(Long.class);
        if(idNatureCanalisation == null){
          throw new ResponseException(Response.Status.BAD_REQUEST, "1005 : Le code de nature de canalisation saisi ne correspond à aucune valeur connue");
        }
      }
      hydrantPibi.setTypeReseauCanalisation(idNatureCanalisation);

      hydrantPibi.setDispositifInviolabilite(peiForm.inviolabilite());
      hydrantPibi.setRenversable(peiForm.renversable());
      hydrantPibi.setSurpresse(peiForm.reseauSurpresse());
      hydrantPibi.setAdditive(peiForm.reseauAdditive());
      hydrantPibi.setDiametreCanalisation(peiForm.diametreCanalisation());

      peiRepository.updatePibiCaracteristiques(hydrantPibi, peiForm.anneeFabrication());
      return "PIBI mis à jour avec succès";
    } catch (DataAccessException e){
      throw new ResponseException(Response.Status.INTERNAL_SERVER_ERROR, "1301 : Une erreur est survenue lors de la mise à jour de l'hydrant");
    }
  }

  public String updatePenaCaracteristiques(String numero, PeiForm peiForm) throws ResponseException {
    try {
      Long codeMateriau = context.select(TYPE_HYDRANT_MATERIAU.ID)
        .from(TYPE_HYDRANT_MATERIAU)
        .where(TYPE_HYDRANT_MATERIAU.CODE.eq(peiForm.codeMateriau().toUpperCase()))
        .fetchOneInto(Long.class);

      if(codeMateriau == null) {
        throw new ResponseException(Response.Status.BAD_REQUEST, "1006 : Le code de matériau saisi ne correspond à aucune valeur connue");
      }

      HydrantPena hydrantPena = context
        .select(HYDRANT_PENA.fields())
        .from(HYDRANT_PENA)
        .join(HYDRANT).on(HYDRANT.ID.eq(HYDRANT_PENA.ID))
        .where(HYDRANT.NUMERO.equalIgnoreCase(numero))
        .fetchOneInto(HydrantPena.class);

      hydrantPena.setMateriau(codeMateriau);
      hydrantPena.setIllimitee(peiForm.capaciteIllimitee());
      hydrantPena.setIncertaine(peiForm.ressourceIncertaine());
      hydrantPena.setCapacite(peiForm.capacite());
      hydrantPena.setQAppoint(peiForm.debitAppoint());
      hydrantPena.setHbe(peiForm.equipeHBE());

      peiRepository.updatePenaCaracteristiques(hydrantPena);
      return "PENA mis à jour avec succès";

    } catch (DataAccessException e){
      throw new ResponseException(Response.Status.INTERNAL_SERVER_ERROR, "1301 : Une erreur est survenue lors de la mise à jour de l'hydrant");
    }

  }

  /**
   * Détermine si le PEI spécifié est accessible à l'utilisateur courant
   * @param numero Le numéro du PEI
   */
  public boolean isPeiAccessible(String numero) {

    Long userId = currentUser.get().userId();
    String userType = currentUser.get().type();

    SelectConditionStep<Record3<Long, Long, Long>> record = context
      .select(HYDRANT.MAINTENANCE_DECI, HYDRANT.SP_DECI, HYDRANT_PIBI.SERVICE_EAUX)
      .from(HYDRANT)
      .leftJoin(HYDRANT_PIBI).on(HYDRANT_PIBI.ID.eq(HYDRANT.ID))
      .where(HYDRANT.NUMERO.equalIgnoreCase(numero));

    // Si l'utilisateur est la maintenance DECI de l'hydrant
    Long maintenanceDeci = (record.fetchOne(HYDRANT.MAINTENANCE_DECI) != null) ? record.fetchOne(HYDRANT.MAINTENANCE_DECI).longValue() : null;
    if(maintenanceDeci != null && maintenanceDeci.equals(userId) && ("SERVICEEAUX".equalsIgnoreCase(userType)
        || "PRESTATAIRE_TECHNIQUE".equalsIgnoreCase(userType)
        || "COMMUNE".equalsIgnoreCase(userType)
        || "EPCI".equalsIgnoreCase(userType))) {
      return true;
    }

    // Si l'utilisateur est le service public DECI de l'hydrant
    Long sp_deci = (record.fetchOne(HYDRANT.SP_DECI) != null) ? record.fetchOne(HYDRANT.SP_DECI).longValue() : null;
    if(sp_deci != null && sp_deci.equals(userId) && ("COMMUNE".equalsIgnoreCase(userType)
      || "EPCI".equalsIgnoreCase(userType))) {
      return true;
    }

    // Si l'utilisateur est le service des eaux de l'hydrant
    Long serviceEaux = (record.fetchOne(HYDRANT_PIBI.SERVICE_EAUX) != null) ? record.fetchOne(HYDRANT_PIBI.SERVICE_EAUX).longValue() : null;

    if(serviceEaux != null && serviceEaux.equals(userId) && "SERVICEEAUX".equalsIgnoreCase(userType)) {
      return true;
    }

    return false;
  }



  /**
   * Indique si l'utilisateur actuel peut modifier le PEI actuel et ses caractéristiques
   * L'utilisateur peut modifier le pei si son organisme est relié à celui-ci, sauf quand il n'est que le service des
   * eaux de ce PEI
   * @param numero Numéro du pei
   * @return
   */
  public boolean userCanEditPei(String numero) {
    Long userId = currentUser.get().userId();
    String userType = currentUser.get().type();

    SelectConditionStep<Record3<Long, Long, Long>> record = context
      .select(HYDRANT.MAINTENANCE_DECI, HYDRANT.SP_DECI, HYDRANT_PIBI.SERVICE_EAUX)
      .from(HYDRANT)
      .leftJoin(HYDRANT_PIBI).on(HYDRANT_PIBI.ID.eq(HYDRANT.ID))
      .where(HYDRANT.NUMERO.equalIgnoreCase(numero));

    // Si l'utilisateur est la maintenance DECI de l'hydrant
    Long maintenanceDeci = (record.fetchOne(HYDRANT.MAINTENANCE_DECI) != null) ? record.fetchOne(HYDRANT.MAINTENANCE_DECI).longValue() : null;
    if(maintenanceDeci != null && maintenanceDeci.equals(userId) && ("SERVICEEAUX".equalsIgnoreCase(userType)
        || "PRESTATAIRE_TECHNIQUE".equalsIgnoreCase(userType)
        || "COMMUNE".equalsIgnoreCase(userType)
        || "EPCI".equalsIgnoreCase(userType))) {
      return true;
    }

    // Si l'utilisateur est le service public DECI de l'hydrant
    Long sp_deci = (record.fetchOne(HYDRANT.SP_DECI) != null) ? record.fetchOne(HYDRANT.SP_DECI).longValue() : null;
    if(sp_deci != null && sp_deci.equals(userId) && ("COMMUNE".equalsIgnoreCase(userType)
      || "EPCI".equalsIgnoreCase(userType))) {
      return true;
    }
    return false;
  }

  /**
   * Retourne les PEI ayant subit une modification depuis une date spécifique
   * @param dateString La date format YYYY-MM-DD hh:mm à partir de laquelle les changements on du avoir lieu
   * @return
   */
  public String diff(String dateString) throws ResponseException {

    Date date = null;
    if(dateString != null) {
      String pattern = "yyyy-MM-dd HH:mm:ss";
      SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
      simpleDateFormat.setLenient(false);
      try {
        date = simpleDateFormat.parse(dateString);
      } catch (ParseException e) {
        throw new ResponseException(Response.Status.BAD_REQUEST, "1010 : La date spécifiée n'existe pas ou ne respecte pas le format YYYY-MM-DD hh:mm:ss");
      }
    } else {
      throw new ResponseException(Response.Status.BAD_REQUEST, "1010 : La date spécifiée n'existe pas ou ne respecte pas le format YYYY-MM-DD hh:mm:ss");
    }

    Organisme organisme = ORGANISME.as("organisme");
    Organisme organismeUtilisateur = ORGANISME.as("organismeUtilisateur");
    Hydrant tracabiliteHydrant = Tables.HYDRANT.as("tracabiliteHydrant");
    HydrantVisite tracabiliteHydrantVisite = Tables.HYDRANT_VISITE.as("tracabiliteHydrantVisite");

    // Données de tracabilite.hydrant
    List<PeiDiffModel> listeCarac = context.select(tracabiliteHydrant.NUMERO, tracabiliteHydrant.DATE_OPERATION.as("dateModification"),
      tracabiliteHydrant.NOM_OPERATION.as("operation"), DSL.concat(UTILISATEUR.NOM, DSL.val(" "), UTILISATEUR.PRENOM).as("utilisateurModification"),
      organismeUtilisateur.NOM.as("utilisateurModificationOrganisme"), organisme.NOM.as("organismeModification"),
      tracabiliteHydrant.AUTEUR_MODIFICATION_FLAG.as("auteurModificationFlag"), DSL.val("CARACTERISTIQUES").as("type")
    )
    .from(tracabiliteHydrant)
    .leftJoin(UTILISATEUR).on(UTILISATEUR.ID.eq(tracabiliteHydrant.UTILISATEUR_MODIFICATION))
    .leftJoin(organismeUtilisateur).on(organismeUtilisateur.ID.eq(UTILISATEUR.ORGANISME))
    .leftJoin(organisme).on(organisme.ID.eq(tracabiliteHydrant.ORGANISME))
    .where(tracabiliteHydrant.DATE_OPERATION.greaterOrEqual(date.toInstant()).and(tracabiliteHydrant.NUMERO.isNotNull()))
    .fetchInto(PeiDiffModel.class);

    // Données de tracabilité.hydrant_visite
    List<PeiDiffModel> listeVisite = context.select(HYDRANT.NUMERO, tracabiliteHydrantVisite.DATE_OPERATION.as("dateModification"),
        tracabiliteHydrantVisite.NOM_OPERATION.as("operation"), DSL.concat(UTILISATEUR.NOM, DSL.val(" "), UTILISATEUR.PRENOM).as("utilisateurModification"),
        organismeUtilisateur.NOM.as("utilisateurModificationOrganisme"), organisme.NOM.as("organismeModification"),
        tracabiliteHydrantVisite.AUTEUR_MODIFICATION_FLAG.as("auteurModificationFlag"), DSL.val("VISITES").as("type"))
      .from(tracabiliteHydrantVisite)
      .join(HYDRANT).on(HYDRANT.ID.eq(tracabiliteHydrantVisite.HYDRANT))
      .leftJoin(UTILISATEUR).on(UTILISATEUR.ID.eq(tracabiliteHydrantVisite.UTILISATEUR_MODIFICATION))
      .leftJoin(organismeUtilisateur).on(organismeUtilisateur.ID.eq(UTILISATEUR.ORGANISME))
      .leftJoin(organisme).on(organisme.ID.eq(tracabiliteHydrantVisite.ORGANISME))
      .where(tracabiliteHydrantVisite.DATE_OPERATION.greaterOrEqual(date.toInstant()).and(HYDRANT.NUMERO.isNotNull()))
      .fetchInto(PeiDiffModel.class);

    ObjectMapper mapper = new ObjectMapper();
    ArrayList<String> listeDiff = new ArrayList<String>();

    for(PeiDiffModel p : Stream.concat(listeCarac.stream(), listeVisite.stream()).collect(Collectors.toList())) {
      if(this.isPeiAccessible(p.getNumero()) || ("CARACTERISTIQUES".equals(p.getType()) && "DELETE".equals(p.getOperation()))) {

        SimpleModule module = new SimpleModule("PeiDiffModelSerializer", new Version(1, 0, 0, null, null, null));
        module.addSerializer(PeiDiffModel.class, new PeiDiffModelSerializer());
        mapper.registerModule(module);
        try {
          String jsonPei = mapper.writeValueAsString(p);
          listeDiff.add(jsonPei);
        } catch (JsonProcessingException e) {
          e.printStackTrace();
        }
      }
    }

    return listeDiff.toString();
  }
}
