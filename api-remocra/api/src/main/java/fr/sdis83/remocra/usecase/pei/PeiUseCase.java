package fr.sdis83.remocra.usecase.pei;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.inject.persist.Transactional;
import fr.sdis83.remocra.authn.CurrentUser;
import fr.sdis83.remocra.authn.UserInfo;
import fr.sdis83.remocra.db.model.tables.pojos.HydrantPena;
import fr.sdis83.remocra.db.model.tables.pojos.HydrantPibi;
import fr.sdis83.remocra.repository.PeiRepository;
import fr.sdis83.remocra.web.exceptions.ResponseException;
import fr.sdis83.remocra.web.model.pei.PeiForm;
import fr.sdis83.remocra.web.model.pei.PeiModel;
import fr.sdis83.remocra.web.model.pei.PeiSpecifiqueModel;
import org.jooq.DSLContext;
import org.jooq.Record3;
import org.jooq.SelectConditionStep;
import org.jooq.exception.DataAccessException;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.ws.rs.core.Response;

import java.util.List;

import static fr.sdis83.remocra.db.model.Tables.HYDRANT;
import static fr.sdis83.remocra.db.model.Tables.HYDRANT_PENA;
import static fr.sdis83.remocra.db.model.Tables.HYDRANT_PIBI;
import static fr.sdis83.remocra.db.model.Tables.TYPE_HYDRANT_DIAMETRE;
import static fr.sdis83.remocra.db.model.Tables.TYPE_HYDRANT_MARQUE;
import static fr.sdis83.remocra.db.model.Tables.TYPE_HYDRANT_MATERIAU;
import static fr.sdis83.remocra.db.model.Tables.TYPE_HYDRANT_MODELE;
import static fr.sdis83.remocra.db.model.Tables.TYPE_RESEAU_ALIMENTATION;
import static fr.sdis83.remocra.db.model.Tables.TYPE_RESEAU_CANALISATION;

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
    if(this.isPeiAccessible(numero)) {
      PeiSpecifiqueModel pei = peiRepository.getPeiSpecifique(numero);
      if(pei == null) {
        throw new ResponseException(Response.Status.BAD_REQUEST, "Le numéro spécifié ne correspond à aucun hydrant");
      }
      return peiRepository.getPeiSpecifique(numero);
    } else {
      throw new ResponseException(Response.Status.FORBIDDEN, "Le numéro spécifié ne correspond à aucun hydrant qui vous est accessible");
    }
  }

  public String getPeiCaracteristiques(String numero) throws ResponseException, JsonProcessingException {
    if(!this.peiRepository.peiExist(numero)) {
     throw new ResponseException(Response.Status.BAD_REQUEST, "Le numéro spécifié ne correspond à aucun hydrant");
    }

    if(!this.isPeiAccessible(numero)) {
       throw new ResponseException(Response.Status.FORBIDDEN, "Le numéro spécifié ne correspond à aucun hydrant qui vous est accessible");
    }

    return peiRepository.getPeiCaracteristiques(numero);
  }

  public String updatePeiCaracteristiques(String numero, PeiForm peiform) throws ResponseException {

    if(!this.peiRepository.peiExist(numero)) {
     throw new ResponseException(Response.Status.BAD_REQUEST, "Le numéro spécifié ne correspond à aucun hydrant");
    }

    if(!this.userCanEditPei(numero) || !this.isPeiAccessible(numero)) {
       throw new ResponseException(Response.Status.FORBIDDEN, "Le numéro spécifié ne correspond à aucun hydrant qui vous est accessible");
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

      Integer idDiametre = null;
      Long idMarque = null;
      Long idModele = null;
      Long idNatureReseau = null;
      Long idNatureCanalisation = null;

      if(peiForm.codeDiametre() != null){
        idDiametre = context.select(TYPE_HYDRANT_DIAMETRE.ID)
          .from(TYPE_HYDRANT_DIAMETRE)
          .where(TYPE_HYDRANT_DIAMETRE.CODE.eq(peiForm.codeDiametre().toUpperCase()))
          .fetchOneInto(Integer.class);
        if(idDiametre == null) {
          throw new ResponseException(Response.Status.BAD_REQUEST, "Le code du diamètre saisi ne correspond à aucune valeur connue");
        }
      }
      hydrantPibi.setDiametreCanalisation(idDiametre);

      if(peiForm.codeMarque() != null){
        idMarque = context.select(TYPE_HYDRANT_MARQUE.ID)
          .from(TYPE_HYDRANT_MARQUE)
          .where(TYPE_HYDRANT_MARQUE.CODE.eq(peiForm.codeMarque().toUpperCase()))
          .fetchOneInto(Long.class);
        if(idMarque == null){
          throw new ResponseException(Response.Status.BAD_REQUEST, "Le code de marque saisi ne correspond à aucune valeur connue");
        }
      }
      hydrantPibi.setMarque(idMarque);

      if(peiForm.codeModele() != null){
        idModele = context.select(TYPE_HYDRANT_MODELE.ID)
          .from(TYPE_HYDRANT_MODELE)
          .where(TYPE_HYDRANT_MODELE.CODE.eq(peiForm.codeModele().toUpperCase()))
          .fetchOneInto(Long.class);
        if(idModele == null){
          throw new ResponseException(Response.Status.BAD_REQUEST, "Le code de modèle saisi ne correspond à aucune valeur connue");
        }
      }
      hydrantPibi.setModele(idModele);

      if(peiForm.codeNatureReseau() != null){
        idNatureReseau = context.select(TYPE_RESEAU_ALIMENTATION.ID)
          .from(TYPE_RESEAU_ALIMENTATION)
          .where(TYPE_RESEAU_ALIMENTATION.CODE.eq(peiForm.codeNatureReseau().toUpperCase()))
          .fetchOneInto(Long.class);
        if(idNatureReseau == null){
            throw new ResponseException(Response.Status.BAD_REQUEST, "Le code de nature du réseau saisi ne correspond à aucune valeur connue");
        }
      }
      hydrantPibi.setTypeReseauAlimentation(idNatureReseau);

      if(peiForm.codeNatureCanalisation() != null){
        idNatureCanalisation = context.select(TYPE_RESEAU_CANALISATION.ID)
          .from(TYPE_RESEAU_CANALISATION)
          .where(TYPE_RESEAU_CANALISATION.CODE.eq(peiForm.codeNatureCanalisation().toUpperCase()))
          .fetchOneInto(Long.class);
        if(idNatureCanalisation == null){
          throw new ResponseException(Response.Status.BAD_REQUEST, "Le code de nature de canalisation saisi ne correspond à aucune valeur connue");
        }
      }
      hydrantPibi.setTypeReseauCanalisation(idNatureCanalisation);

      hydrantPibi.setDispositifInviolabilite(peiForm.inviolabilite());
      hydrantPibi.setRenversable(peiForm.renversable());
      hydrantPibi.setSurpresse(peiForm.reseauSurpresse());
      hydrantPibi.setAdditive(peiForm.reseauAdditive());

      peiRepository.updatePibiCaracteristiques(hydrantPibi, peiForm.anneeFabrication());
      return "PIBI mis à jour avec succès";
    } catch (DataAccessException | ResponseException e){
      throw new ResponseException(Response.Status.INTERNAL_SERVER_ERROR, "Une erreur est survenue lors de la mise à jour de l'hydrant");
    }
  }

  public String updatePenaCaracteristiques(String numero, PeiForm peiForm) throws ResponseException {
    try {
      Long codeMateriau = context.select(TYPE_HYDRANT_MATERIAU.ID)
        .from(TYPE_HYDRANT_MATERIAU)
        .where(TYPE_HYDRANT_MATERIAU.CODE.eq(peiForm.codeMateriau().toUpperCase()))
        .fetchOneInto(Long.class);

      if(codeMateriau == null) {
        throw new ResponseException(Response.Status.BAD_REQUEST, "Le code de matériau saisi ne correspond à aucune valeur connue");
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

    } catch (DataAccessException | ResponseException e){
      throw new ResponseException(Response.Status.INTERNAL_SERVER_ERROR, "Une erreur est survenue lors de la mise à jour de l'hydrant");
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
}
