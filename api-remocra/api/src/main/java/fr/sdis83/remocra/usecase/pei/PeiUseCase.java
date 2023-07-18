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
import fr.sdis83.remocra.usecase.utils.DateUtils;
import fr.sdis83.remocra.usecase.utils.UseCaseUtils;
import fr.sdis83.remocra.util.GlobalConstants;
import fr.sdis83.remocra.web.exceptions.ResponseException;
import fr.sdis83.remocra.web.model.pei.PeiDiffModel;
import fr.sdis83.remocra.web.model.pei.PeiForm;
import fr.sdis83.remocra.web.model.pei.PeiModel;
import fr.sdis83.remocra.web.model.pei.PeiSpecifiqueModel;
import fr.sdis83.remocra.web.serializer.PeiDiffModelSerializer;
import org.jooq.DSLContext;
import org.jooq.Record5;
import org.jooq.RecordMapper;
import org.jooq.exception.DataAccessException;
import org.jooq.impl.DSL;
import org.jooq.tools.StringUtils;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.ws.rs.core.Response;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static fr.sdis83.remocra.db.model.remocra.Tables.HYDRANT;
import static fr.sdis83.remocra.db.model.remocra.Tables.HYDRANT_PENA;
import static fr.sdis83.remocra.db.model.remocra.Tables.HYDRANT_PIBI;
import static fr.sdis83.remocra.db.model.remocra.Tables.ORGANISME;
import static fr.sdis83.remocra.db.model.remocra.Tables.TYPE_HYDRANT_DIAMETRE;
import static fr.sdis83.remocra.db.model.remocra.Tables.TYPE_HYDRANT_DIAMETRE_NATURES;
import static fr.sdis83.remocra.db.model.remocra.Tables.TYPE_HYDRANT_MARQUE;
import static fr.sdis83.remocra.db.model.remocra.Tables.TYPE_HYDRANT_MATERIAU;
import static fr.sdis83.remocra.db.model.remocra.Tables.TYPE_HYDRANT_MODELE;
import static fr.sdis83.remocra.db.model.remocra.Tables.TYPE_HYDRANT_NATURE;
import static fr.sdis83.remocra.db.model.remocra.Tables.TYPE_RESEAU_ALIMENTATION;
import static fr.sdis83.remocra.db.model.remocra.Tables.TYPE_RESEAU_CANALISATION;
import static fr.sdis83.remocra.db.model.remocra.Tables.UTILISATEUR;

public class PeiUseCase {

  @Inject
  PeiRepository peiRepository;

  @Inject
  @CurrentUser
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
    if (!this.peiRepository.peiExist(numero)) {
      throw new ResponseException(Response.Status.BAD_REQUEST, "1000 : Le numéro spécifié ne correspond à aucun hydrant");
    }

    if (!this.isPeiAccessible(numero)) {
      throw new ResponseException(Response.Status.FORBIDDEN, "1300 : Le numéro spécifié ne correspond à aucun hydrant qui vous est accessible");
    }
    return peiRepository.getPeiSpecifique(numero);
  }

  public String getPeiCaracteristiques(String numero) throws ResponseException, JsonProcessingException {
    if (!this.peiRepository.peiExist(numero)) {
      throw new ResponseException(Response.Status.BAD_REQUEST, "1000 : Le numéro spécifié ne correspond à aucun hydrant");
    }

    if (!this.isPeiAccessible(numero)) {
      throw new ResponseException(Response.Status.FORBIDDEN, "1300 : Le numéro spécifié ne correspond à aucun hydrant qui vous est accessible");
    }

    return peiRepository.getPeiCaracteristiques(numero);
  }

  public String updatePeiCaracteristiques(String numero, PeiForm peiform) throws ResponseException {
    if (!this.peiRepository.peiExist(numero)) {
      throw new ResponseException(Response.Status.BAD_REQUEST, "1000 : Le numéro spécifié ne correspond à aucun hydrant");
    }

    if (!this.userCanEditPei(numero) || !this.isPeiAccessible(numero)) {
      throw new ResponseException(Response.Status.FORBIDDEN, "1300 : Le numéro spécifié ne correspond à aucun hydrant qui vous est accessible");
    }

    String typePei = context.select(HYDRANT.CODE)
            .from(HYDRANT)
            .where(HYDRANT.NUMERO.eq(numero))
            .fetchOneInto(String.class);

    if (typePei.equalsIgnoreCase("PIBI")) {
      return updatePibiCaracteristiques(numero, peiform);
    } else {
      return updatePenaCaracteristiques(numero, peiform);
    }

  }

  @Transactional
  public String updatePibiCaracteristiques(String numero, PeiForm peiForm) throws ResponseException {
    try {
      if (peiForm.peiJumele() != null) {
        String error = peiRepository.jumelagePei(numero, peiForm.peiJumele());
        if (error != null) {
          throw new ResponseException(Response.Status.METHOD_NOT_ALLOWED, error);
        }
      }
      HydrantPibi hydrantPibi = context
              .select(HYDRANT_PIBI.fields())
              .from(HYDRANT_PIBI)
              .join(HYDRANT).on(HYDRANT.ID.eq(HYDRANT_PIBI.ID))
              .where(HYDRANT.NUMERO.equalIgnoreCase(numero))
              .fetchOneInto(HydrantPibi.class);

      fr.sdis83.remocra.db.model.remocra.tables.pojos.Hydrant pei = context.select(HYDRANT.fields())
              .from(HYDRANT)
              .where(HYDRANT.NUMERO.equalIgnoreCase(numero))
              .fetchOneInto(fr.sdis83.remocra.db.model.remocra.tables.pojos.Hydrant.class);

      Long idDiametre = null;
      Long idMarque = null;
      Long idModele = null;
      Long idNatureReseau = null;
      Long idNatureCanalisation = null;

      if (peiForm.codeDiametre() != null) {
        idDiametre = context.select(TYPE_HYDRANT_DIAMETRE.ID)
                .from(TYPE_HYDRANT_DIAMETRE)
                .where(TYPE_HYDRANT_DIAMETRE.CODE.eq(peiForm.codeDiametre().toUpperCase()))
                .fetchOneInto(Long.class);
        if (idDiametre == null) {
          throw new ResponseException(Response.Status.BAD_REQUEST, "1001 : Le code du diamètre saisi ne correspond à aucune valeur connue");
        }

        Integer nbNatures = context.selectCount()
                .from(TYPE_HYDRANT_DIAMETRE)
                .join(TYPE_HYDRANT_DIAMETRE_NATURES).on(TYPE_HYDRANT_DIAMETRE_NATURES.TYPE_HYDRANT_DIAMETRE.eq(TYPE_HYDRANT_DIAMETRE.ID))
                .join(TYPE_HYDRANT_NATURE).on(TYPE_HYDRANT_NATURE.ID.eq(TYPE_HYDRANT_DIAMETRE_NATURES.NATURES))
                .where(TYPE_HYDRANT_NATURE.ID.eq(pei.getNature()).and(TYPE_HYDRANT_DIAMETRE.ID.eq(idDiametre)))
                .fetchOneInto(Integer.class);
        if (nbNatures == 0) {
          throw new ResponseException(Response.Status.BAD_REQUEST, "1101 : Le diamètre nominal renseigné par EDP n'est pas accepté dans Remocra pour un PEI de cette nature");
        }
      }
      hydrantPibi.setDiametre(idDiametre);

      if (peiForm.codeMarque() != null) {
        idMarque = context.select(TYPE_HYDRANT_MARQUE.ID)
                .from(TYPE_HYDRANT_MARQUE)
                .where(TYPE_HYDRANT_MARQUE.CODE.eq(peiForm.codeMarque().toUpperCase()))
                .fetchOneInto(Long.class);
        if (idMarque == null) {
          throw new ResponseException(Response.Status.BAD_REQUEST, "1002 : Le code de marque saisi ne correspond à aucune valeur connue");
        } else {
          hydrantPibi.setModele(null); // Changement de marque => on met le modèle à null
        }
      }
      hydrantPibi.setMarque(idMarque);

      if (peiForm.codeModele() != null) {
        List<TypeHydrantModele> modeles = context.select(TYPE_HYDRANT_MODELE.fields())
                .from(TYPE_HYDRANT_MODELE)
                .where(TYPE_HYDRANT_MODELE.CODE.upper().eq(peiForm.codeModele().toUpperCase()))
                .fetchInto(TypeHydrantModele.class);

        if (modeles.size() == 0) {
          throw new ResponseException(Response.Status.BAD_REQUEST, "1003 : Le code de modèle saisi ne correspond à aucune valeur connue");
        }

        Long marque = (peiForm.codeMarque() != null) ? idMarque : hydrantPibi.getMarque();
        boolean matchFound = false;
        for (TypeHydrantModele modele : modeles) {
          if (modele.getMarque().equals(marque)) {
            idModele = modele.getId();
            matchFound = true;
          }
        }

        // Parmis les modèles retournés, aucun ne correspond à la marque renseignée
        if (!matchFound) {
          throw new ResponseException(Response.Status.BAD_REQUEST, "1008 : Le code de modèle saisi n'appartient pas à la marque renseignée");
        }
      }
      hydrantPibi.setModele(idModele);

      if (peiForm.codeNatureReseau() != null) {
        idNatureReseau = context.select(TYPE_RESEAU_ALIMENTATION.ID)
                .from(TYPE_RESEAU_ALIMENTATION)
                .where(TYPE_RESEAU_ALIMENTATION.CODE.eq(peiForm.codeNatureReseau().toUpperCase()))
                .fetchOneInto(Long.class);
        if (idNatureReseau == null) {
          throw new ResponseException(Response.Status.BAD_REQUEST, "1004 : Le code de nature du réseau saisi ne correspond à aucune valeur connue");
        }
      }
      hydrantPibi.setTypeReseauAlimentation(idNatureReseau);

      if (peiForm.codeNatureCanalisation() != null) {
        idNatureCanalisation = context.select(TYPE_RESEAU_CANALISATION.ID)
                .from(TYPE_RESEAU_CANALISATION)
                .where(TYPE_RESEAU_CANALISATION.CODE.eq(peiForm.codeNatureCanalisation().toUpperCase()))
                .fetchOneInto(Long.class);
        if (idNatureCanalisation == null) {
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
    } catch (DataAccessException e) {
      throw new ResponseException(Response.Status.INTERNAL_SERVER_ERROR, "1301 : Une erreur est survenue lors de la mise à jour de l'hydrant");
    }
  }

  public String updatePenaCaracteristiques(String numero, PeiForm peiForm) throws ResponseException {
    try {
      Long codeMateriau = context.select(TYPE_HYDRANT_MATERIAU.ID)
              .from(TYPE_HYDRANT_MATERIAU)
              .where(TYPE_HYDRANT_MATERIAU.CODE.eq(peiForm.codeMateriau().toUpperCase()))
              .fetchOneInto(Long.class);

      if (codeMateriau == null) {
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

    } catch (DataAccessException e) {
      throw new ResponseException(Response.Status.INTERNAL_SERVER_ERROR, "1301 : Une erreur est survenue lors de la mise à jour de l'hydrant");
    }

  }

  /**
   * Classe utilitaire permettant de connaitre les infos d'accessibilité d'un hydrant,
   * soit au travers du getter *isAccessible*, soit des propriétés élémentaires qui le composent
   */
  public static class HydrantAccessibilite {
    private final long id;
    private final String numero;
    private final Long maintenanceDECI;
    private final Long servicePublicDECI;
    private final Long serviceEaux;
    private final boolean accessible;

    public HydrantAccessibilite(long id, String numero, Long maintenanceDECI, Long servicePublicDECI, Long serviceEaux, boolean accessible) {
      this.id = id;
      this.numero = numero;
      this.maintenanceDECI = maintenanceDECI;
      this.servicePublicDECI = servicePublicDECI;
      this.serviceEaux = serviceEaux;
      this.accessible = accessible;
    }

    public long getId() {
      return id;
    }

    public String getNumero() {
      return numero;
    }

    public Long getMaintenanceDECI() {
      return maintenanceDECI;
    }

    public Long getServicePublicDECI() {
      return servicePublicDECI;
    }

    public Long getServiceEaux() {
      return serviceEaux;
    }

    public boolean isAccessible() {
      return accessible;
    }

  }

  /**
   * Retourne les infos d'accessibilité d'un PEI dont le numéro est passé en paramètre
   *
   * @param numeroPei String
   * @return HydrantAccessibilite
   */
  protected HydrantAccessibilite getHydrantAccessibilite(String numeroPei) {
    if (StringUtils.isEmpty(numeroPei))
      throw new IllegalArgumentException("numeroPEI can't be null or empty");

    return listHydrantsAccessibilite(Collections.singletonList(numeroPei)).get(0);
  }

  /**
   * Retourne les propriétés permettant de calculer l'accessibilité des PEI
   * <ul>
   *   <li>si ListPei est vide, de tous les PEI</li>
   *   <li>sinon, ceux dont le *numéro* est compris dans la liste</li>
   * </ul>
   *
   * @param listPei List<String> (Pei.numero)
   * @return List<HydrantAccessibilite>
   */
  protected List<HydrantAccessibilite> listHydrantsAccessibilite(List<String> listPei) {
    if (listPei == null) {
      throw new IllegalArgumentException("null argument given : listPei");
    }

    UseCaseUtils.OrganismeIdType organisme = new UseCaseUtils.OrganismeIdType(currentUser.get().userId(), currentUser.get().type());


    return context.select(HYDRANT.ID, HYDRANT.NUMERO, HYDRANT.MAINTENANCE_DECI, HYDRANT.SP_DECI, HYDRANT_PIBI.SERVICE_EAUX)
            .from(HYDRANT)
            .leftJoin(HYDRANT_PIBI).on(HYDRANT_PIBI.ID.eq(HYDRANT.ID))
            .where(listPei.isEmpty() ? DSL.noCondition() : HYDRANT.NUMERO.in(listPei))
            .fetch(new RecordMapper<>() {
              @Override
              public HydrantAccessibilite map(Record5<Long, String, Long, Long, Long> record) {
                return new HydrantAccessibilite(
                        record.value1(),
                        record.value2(),
                        record.value3(),
                        record.value4(),
                        record.value5(),
                        computeAccessibilite(record)
                );
              }

              private boolean computeAccessibilite(Record5<Long, String, Long, Long, Long> record) {
                return UseCaseUtils.isApiAdmin(organisme)
                        || UseCaseUtils.isMaintenanceDECI(record.value3(), organisme)
                        || UseCaseUtils.isServicePublicDECI(record.value4(), organisme)
                        || UseCaseUtils.isServiceEaux(record.value5(), organisme);

              }
            });
  }





  /**
   * Détermine si le PEI spécifié est accessible à l'utilisateur courant
   *
   * @param numeroPei Le numéro du PEI
   */
  public boolean isPeiAccessible(String numeroPei) {
    return getHydrantAccessibilite(numeroPei).isAccessible();
  }

  /**
   * Indique si l'utilisateur actuel peut modifier le PEI actuel et ses caractéristiques
   * L'utilisateur peut modifier le pei si son organisme est relié à celui-ci, sauf quand il n'est que le service des
   * eaux de ce PEI
   *
   * @param numero Numéro du pei
   * @return
   */
  public boolean userCanEditPei(String numero) {

    UseCaseUtils.OrganismeIdType organisme = new UseCaseUtils.OrganismeIdType(currentUser.get().userId(), currentUser.get().type());

    if (UseCaseUtils.isApiAdmin(organisme)) {
      return true;
    }

    HydrantAccessibilite hydrantAccessibilite = listHydrantsAccessibilite(Collections.singletonList(numero)).get(0);

    if (UseCaseUtils.isApiAdmin(organisme)) {
      return true;
    }

    if (UseCaseUtils.isMaintenanceDECI(hydrantAccessibilite.getMaintenanceDECI(),organisme)) {
      return true;
    }

    if (UseCaseUtils.isServicePublicDECI(hydrantAccessibilite.getServicePublicDECI(), organisme)) {
      return true;
    }

    return false;
  }

  /**
   * Retourne les PEI ayant subit une modification depuis une date spécifique
   *
   * @param dateString La date format YYYY-MM-DD hh:mm à partir de laquelle rechercher les changements
   * @return String les PEI en question au format JSON
   */
  public String diff(String dateString) throws ResponseException {
    ZonedDateTime moment = null;
    boolean valide = true;
    if (dateString != null) {
      try {
        moment = DateUtils.getMoment(dateString);
      } catch (DateTimeParseException dtpe) {
        valide = false;
      }
    } else {
      valide = false;
    }
    if (!valide) {
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
            .where(tracabiliteHydrant.DATE_OPERATION.greaterOrEqual(moment.toInstant()).and(tracabiliteHydrant.NUMERO.isNotNull()))
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
            .where(tracabiliteHydrantVisite.DATE_OPERATION.greaterOrEqual(moment.toInstant()).and(HYDRANT.NUMERO.isNotNull()))
            .fetchInto(PeiDiffModel.class);

    ObjectMapper mapper = new ObjectMapper();
    SimpleModule module = new SimpleModule("PeiDiffModelSerializer", new Version(1, 0, 0, null, null, null));
    module.addSerializer(PeiDiffModel.class, new PeiDiffModelSerializer());
    mapper.registerModule(module);

    List<PeiDiffModel> diffs = Stream.concat(listeCarac.stream(), listeVisite.stream()).collect(Collectors.toList());
    // On va chercher les numéros de tous les PEI concernés par une modification (hydrant + visite)
    List<String> listModifiedPei = diffs
            .stream()
            .map(PeiDiffModel::getNumero)
            .distinct()
            .collect(Collectors.toList());

    // Une seule requête pour calculer leur accessibilité, on se servira de la map<numero, POJO> par la suite
    Map<String, HydrantAccessibilite> mapAccessibilite = listHydrantsAccessibilite(listModifiedPei)
            .stream()
            .collect(Collectors.toMap(HydrantAccessibilite::getNumero, Function.identity()));
    List<String> listeDiff = new ArrayList<>();
    for (PeiDiffModel p : diffs) {
      if (mapAccessibilite.get(p.getNumero()) != null && mapAccessibilite.get(p.getNumero()).isAccessible()
              || ("CARACTERISTIQUES".equals(p.getType()) && "DELETE".equals(p.getOperation()))) {

        try {
          listeDiff.add(mapper.writeValueAsString(p));
        } catch (JsonProcessingException e) {
          e.printStackTrace();
        }
      }
    }

    return listeDiff.toString();
  }
}
