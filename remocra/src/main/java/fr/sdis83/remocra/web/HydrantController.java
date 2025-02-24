package fr.sdis83.remocra.web;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Point;
import flexjson.JSONDeserializer;
import flexjson.JSONSerializer;
import fr.sdis83.remocra.GlobalConstants;
import fr.sdis83.remocra.domain.remocra.Hydrant;
import fr.sdis83.remocra.domain.remocra.Organisme;
import fr.sdis83.remocra.repository.HydrantRepository;
import fr.sdis83.remocra.repository.HydrantVisiteRepository;
import fr.sdis83.remocra.service.HydrantService;
import fr.sdis83.remocra.service.TourneeService;
import fr.sdis83.remocra.service.UtilisateurService;
import fr.sdis83.remocra.service.ZoneCompetenceService;
import fr.sdis83.remocra.usecase.importctp.ImportCtpUseCase;
import fr.sdis83.remocra.usecase.parametre.ParametreDataProvider;
import fr.sdis83.remocra.util.ExceptionUtils;
import fr.sdis83.remocra.util.FeatureUtil;
import fr.sdis83.remocra.util.GeometryUtil;
import fr.sdis83.remocra.web.deserialize.GeometryFactory;
import fr.sdis83.remocra.web.message.ItemFilter;
import fr.sdis83.remocra.web.message.ItemSorting;
import fr.sdis83.remocra.web.model.HydrantRecord;
import fr.sdis83.remocra.web.serialize.ext.AbstractExtListSerializer;
import fr.sdis83.remocra.web.serialize.ext.SuccessErrorExtSerializer;
import fr.sdis83.remocra.web.serialize.transformer.GeometryTransformer;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

@RequestMapping("/hydrants")
@Controller
public class HydrantController {

  @Autowired private HydrantService hydrantService;

  @Autowired private TourneeService tourneeService;

  @Autowired private ZoneCompetenceService zoneCompetenceService;

  @Autowired private UtilisateurService serviceUtilisateur;

  @Autowired private HydrantRepository hydrantRepository;

  @Autowired private HydrantVisiteRepository hydrantVisiteRepository;

  @Autowired private ImportCtpUseCase importCtpUseCase;
  @Autowired ParametreDataProvider parametreProvider;

  private final Logger logger = Logger.getLogger(getClass());

  @RequestMapping(value = "", method = RequestMethod.GET, headers = "Accept=application/json")
  @PreAuthorize("hasRight('HYDRANTS_R')")
  public ResponseEntity<java.lang.String> listJson(
      final @RequestParam(value = "page", required = false) Integer page,
      final @RequestParam(value = "start", required = false) Integer start,
      final @RequestParam(value = "limit", required = false) Integer limit,
      final @RequestParam(value = "sort", required = false) String sorts,
      final @RequestParam(value = "filter", required = false) String filters,
      final @RequestParam(value = "query", required = false) String query) {

    final List<ItemSorting> sortList = ItemSorting.decodeJson(sorts);
    final List<ItemFilter> itemFilterList = ItemFilter.decodeJson(filters);
    if (query != null && !query.isEmpty()) {
      itemFilterList.add(new ItemFilter("query", query));
    }

    // Comptage : zone de compétence simplifiée pour accélérer le calcul
    final List<ItemFilter> itemFilterCountList = new LinkedList<ItemFilter>();
    itemFilterCountList.addAll(itemFilterList);
    itemFilterCountList.add(new ItemFilter("zoneCompetenceSimplified", "true"));

    itemFilterList.add(new ItemFilter("zoneCompetence", "true"));

    return new AbstractExtListSerializer<Hydrant>(
        "fr.sdis83.remocra.domain.remocra.Hydrant retrieved.") {

      @Override
      protected JSONSerializer additionnalIncludeExclude(JSONSerializer serializer) {
        return new JSONSerializer()
            .include(
                "data.id",
                "data.numero",
                "data.natureNom",
                "data.natureCode",
                "data.dateRecep",
                "data.nomTournee",
                "data.code",
                "data.dateReco",
                "data.dateContr",
                "data.hbe",
                "data.jsonGeometrie",
                "data.dispoHbe",
                "data.indispoTemp",
                "data.dispoTerrestre",
                "data.debit",
                "data.commune.id",
                "data.nomCommune",
                "data.natureDeci.id",
                "data.spDeci.id",
                "data.nomNatureDeci",
                "data.codeNatureDeci",
                "data.adresse",
                "data.numeroInterne",
                "data.gestionnaireNom",
                "data.nature.id",
                "data.dateChangementDispoTerrestre",
                "data.visites.agent1",
                "data.visites.agent2",
                "data.visites.anomalies",
                "data.visites.auteurModificationFlag",
                "data.visites.ctrl_debit_pression",
                "data.visites.date",
                "data.visites.debit",
                "data.visites.debitMax",
                "data.visites.id",
                "data.visites.observations",
                "data.visites.pression",
                "data.visites.pressionDyn",
                "data.visites.pressionDynDeb",
                "data.visites.type.*",
                "data.dateChangementDispoTerrestre",
                "data.commune.insee")
            .exclude(
                "data.*",
                "*.class",
                "data.visites.organisme.*",
                "data.visites.utilisateurModification.*")
            .transform(new GeometryTransformer(), Geometry.class);
      }

      @Override
      protected List<Hydrant> getRecords() {
        Long organisme = serviceUtilisateur.getCurrentUtilisateur().getOrganisme().getId();
        List<Hydrant> l = hydrantService.find(start, limit, sortList, itemFilterList);
        for (Hydrant h : l) {
          h.setNomTournee(tourneeService.getNomTournee(h, organisme));
        }
        return l;
      }

      @Override
      protected Long countRecords() {
        return hydrantService.count(itemFilterCountList);
      }
    }.serialize();
  }

  @RequestMapping(value = "/layer", method = RequestMethod.GET, headers = "Accept=application/json")
  @PreAuthorize("hasRight('HYDRANTS_R')")
  public ResponseEntity<java.lang.String> layer(final @RequestParam String bbox) {
    if (bbox == null || bbox.isEmpty()) {
      return FeatureUtil.getResponse(hydrantService.findAllHydrants());
    } else {
      return FeatureUtil.getResponse(hydrantService.findHydrantsByBBOX(bbox));
    }
  }

  @RequestMapping(
      value = "/desaffecter",
      method = RequestMethod.POST,
      headers = "Accept=application/json")
  @PreAuthorize("hasRight('TOURNEE_C')")
  public ResponseEntity<java.lang.String> desaffecter(
      final @RequestParam String json, final @RequestParam Boolean allOrganismes) {
    Integer nbHydrant = hydrantService.desaffecter(json, allOrganismes);
    return new SuccessErrorExtSerializer(
            true, nbHydrant.toString() + " désaffectation(s) effectuée(s)")
        .serialize();
  }

  @RequestMapping(
      value = "/affecter",
      method = RequestMethod.POST,
      headers = "Accept=application/json")
  @PreAuthorize("hasRight('TOURNEE_C')")
  public ResponseEntity<java.lang.String> affecter(final @RequestBody String json) {
    try {
      Integer nbHydrant = hydrantService.affecter(json);
      if (nbHydrant == 0) {
        return new SuccessErrorExtSerializer(
                false,
                "Affectation impossible: Il est incompatible "
                    + "d'effectuer des ROP privées avec des PEI publics et inversement")
            .serialize();
      }
      return new SuccessErrorExtSerializer(true, nbHydrant.toString() + " hydrant(s) affecté(s)")
          .serialize();
    } catch (Exception e) {
      if (ExceptionUtils.getNestedExceptionWithClass(e, ConstraintViolationException.class)
          != null) {
        return new SuccessErrorExtSerializer(false, "Une tournée existe déjà sous ce nom")
            .serialize();
      }
      return new SuccessErrorExtSerializer(false, e.getMessage()).serialize();
    }
  }

  @RequestMapping(
      value = "/checkHydrantsNatureDeci",
      method = RequestMethod.POST,
      headers = "Accept=application/json")
  @PreAuthorize("hasRight('TOURNEE_C')")
  public ResponseEntity<java.lang.String> checkHydrantsNatureDeci(final @RequestBody String json) {
    boolean correctTypeDECI = hydrantService.checkHydrantsNatureDeci(json);
    if (!correctTypeDECI) {
      return new SuccessErrorExtSerializer(
              false,
              "Création de tournée impossible : Il est incompatible "
                  + "d'effectuer des ROP privées avec des PEI publics et inversement")
          .serialize();
    }

    return new SuccessErrorExtSerializer(true, "Création de tournée possible").serialize();
  }

  @RequestMapping(
      value = "/checkTournee",
      method = RequestMethod.POST,
      headers = "Accept=application/json")
  @PreAuthorize("hasRight('TOURNEE_C')")
  public ResponseEntity<java.lang.String> checkTournee(
      final @RequestParam String json,
      final @RequestParam(value = "idOrganisme", required = false) Long idOrganisme) {
    Map<Hydrant, String> withSameOrganism = hydrantService.checkTournee(json, idOrganisme);
    if (withSameOrganism.isEmpty()) {
      return new SuccessErrorExtSerializer(true, "aucune tournée avec le même organisme")
          .serialize();
    }
    String msg = "<div class=\"listHydrant\">";
    for (Map.Entry<Hydrant, String> entry : withSameOrganism.entrySet()) {
      msg += "<li>" + entry.getKey().getNumero() + " (" + entry.getValue() + ")</li>";
      // traitements
    }
    msg += "</div>";
    if (withSameOrganism.size() == 1) {
      return new SuccessErrorExtSerializer(
              false,
              "Le point d\'eau ci-dessous est associé à une autre tournée pour l'organisme spécifié :"
                  + msg
                  + "<br> Merci de le désaffecter avant de procéder à une nouvelle réaffectation.")
          .serialize();
    }

    return new SuccessErrorExtSerializer(
            false,
            "Les points d\'eau ci-dessous sont associés à d\'autres tournées pour l'organisme spécifié :"
                + msg
                + "<br> Merci de les désaffecter avant de procéder à une nouvelle réaffectation.")
        .serialize();
  }

  @RequestMapping(
      value = "/checkReservation",
      method = RequestMethod.POST,
      headers = "Accept=application/json")
  @PreAuthorize("hasRight('TOURNEE_C')")
  public ResponseEntity<java.lang.String> checkReservation(final @RequestBody String json) {
    Map<Hydrant, String> withReservation = hydrantService.checkReservation(json);
    if (withReservation.isEmpty()) {
      return new SuccessErrorExtSerializer(true, "aucune tournée est réservée").serialize();
    }
    String msg = "<div class=\"listHydrant\">";
    for (Map.Entry<Hydrant, String> entry : withReservation.entrySet()) {
      msg += "<li>" + entry.getKey().getNumero() + " (" + entry.getValue() + ")</li>";
      // traitements
    }
    msg += "</div>";
    if (withReservation.size() == 1) {
      return new SuccessErrorExtSerializer(
              false,
              "Le point d\'eau ci-dessous appartient à une tournée reservée :"
                  + msg
                  + "<br> En continuant il sera désaffecté. "
                  + "Ceci n\'engendre pas de perte de données lors de la synchronisation mais impacte potentiellement le pourcentage si la tournée n\'a pas été finalisée.")
          .serialize();
    }

    return new SuccessErrorExtSerializer(
            false,
            "Les points d\'eau ci-dessous appartiennent à une tournée reservée :"
                + msg
                + "<br> En continuant ils seront désaffectés. "
                + "Ceci n\'engendre pas de perte de données lors de la synchronisation mais impacte potentiellement le pourcentage si la tournée n\'a pas été finalisée.")
        .serialize();
  }

  @RequestMapping(
      value = "/checkdispo",
      method = RequestMethod.POST,
      headers = "Accept=application/json")
  @PreAuthorize("hasRight('HYDRANTS_R')")
  public ResponseEntity<java.lang.String> checkDispo(
      final @RequestParam(value = "id", required = false) Long id,
      final @RequestParam(value = "nature", required = false) Long nature,
      final @RequestParam(value = "commune", required = false) Long codeCommune,
      final @RequestParam(value = "num", required = false) String num,
      final @RequestParam(value = "geometrie", required = false) String geometrie,
      final @RequestParam(value = "nature_deci", required = false) Long nature_deci) {
    String message =
        hydrantService.checkDispo(
            id,
            nature,
            codeCommune,
            (num == null || num.isEmpty() ? null : Integer.valueOf(num)),
            geometrie,
            nature_deci);
    return new SuccessErrorExtSerializer(message == null || message.isEmpty(), message).serialize();
  }

  @RequestMapping(
      value = "/document/{id}",
      method = RequestMethod.DELETE,
      headers = "Accept=application/json")
  @PreAuthorize("hasRight('HYDRANTS_C')")
  @Transactional
  public ResponseEntity<java.lang.String> deleteHydrantDocument(@PathVariable("id") Long id) {
    try {
      hydrantService.deleteDocument(id);
      return new SuccessErrorExtSerializer(true, "Document supprimé").serialize();
    } catch (Exception e) {
      return new SuccessErrorExtSerializer(false, e.getMessage()).serialize();
    }
  }

  @RequestMapping(
      value = "{id}/deplacer",
      method = RequestMethod.POST,
      headers = "Accept=application/json")
  @PreAuthorize("hasRight('HYDRANTS_DEPLACEMENT_C')")
  public ResponseEntity<java.lang.String> deplacer(
      final @PathVariable(value = "id") Long id,
      final @RequestParam(value = "geometrie") String geometrie,
      final @RequestParam(value = "srid") Integer srid) {

    try {
      JSONDeserializer<Point> deserializer = new JSONDeserializer<Point>();
      deserializer.use((String) null, new GeometryFactory());
      Point point = deserializer.deserialize(geometrie);
      point.setSRID(srid);
      Boolean result =
          zoneCompetenceService.check(point, serviceUtilisateur.getCurrentZoneCompetenceId());
      if (!result) {
        return new SuccessErrorExtSerializer(result, "Déplacement du point d'eau non autorisé")
            .serialize();
      }
      hydrantRepository.deplacer(id, point, srid);
    } catch (Exception e) {
      return new SuccessErrorExtSerializer(
              false, "Problème survenu lors du déplacement du point d'eau")
          .serialize();
    }
    return new SuccessErrorExtSerializer(true, "Point d'eau déplacé").serialize();
  }

  /**
   * Transforme les coordonnées passées en projection Lambert 93
   *
   * @param srid Le système de projection actuel des coordonnées
   * @param degres Flag booléen (0: degrés sexagesimaux, 1: degrés décimaux)
   * @return
   */
  @RequestMapping(
      value = "/transformCoordonnees",
      method = RequestMethod.GET,
      headers = "Accept=application/json")
  public ResponseEntity<java.lang.String> transformCoordonnees(
      final @RequestParam(value = "srid") Integer srid,
      final @RequestParam(value = "latitude") Double latitude,
      final @RequestParam(value = "longitude") Double longitude,
      final @RequestParam(value = "degres", required = false, defaultValue = "true") boolean
              degres) {
    try {
      // Lambert 83, on renvoie les coordonnées existantes
      if (srid == parametreProvider.get().getSridInt()) {
        return new SuccessErrorExtSerializer(true, longitude + "," + latitude).serialize();
      } else {
        double[] coordonneConvert =
            GeometryUtil.transformCordinate(
                longitude, latitude, parametreProvider.get().getSridInt(), srid);

        // Si on a choisi le système WGS84 et qu'on ne souhaite pas l'exprimer en degrés décimaux,
        // on le convertit en degrés minutes secondes
        if (srid == GlobalConstants.SRID_4326 && !degres) {
          String longitudeConvert =
              GeometryUtil.convertDegresDecimauxToSexagesimaux(coordonneConvert[0]);
          String latitudeConvert =
              GeometryUtil.convertDegresDecimauxToSexagesimaux(coordonneConvert[1]);
          return new SuccessErrorExtSerializer(true, longitudeConvert + "," + latitudeConvert)
              .serialize();
        }
        return new SuccessErrorExtSerializer(true, coordonneConvert[0] + "," + coordonneConvert[1])
            .serialize();
      }

    } catch (Exception e) {
      return new SuccessErrorExtSerializer(
              false, "Problème survenu lors du calcul de la projection de coordonnées")
          .serialize();
    }
  }

  /**
   * @deprecated Transforme des coordonnées JSON (latitude/longitude) en géométrie de type Point
   *     Actuellement utilisé uniquement par la fiche PEI, a retirer une fois toute la fiche sera
   *     passée en JOOQ
   * @param json Les coordonnées du PEI
   * @return Géoémtrie de type Point
   */
  @RequestMapping(
      value = "/getUpdatedCoordonnees",
      method = RequestMethod.POST,
      headers = "Accept=application/json")
  public ResponseEntity<java.lang.String> updateCoordonnees(final @RequestBody String json) {
    try {
      Point p = hydrantService.coordonneesToPoint(json);
      return new SuccessErrorExtSerializer(true, p.toString()).serialize();
    } catch (Exception e) {
      System.out.println(e);
      return new SuccessErrorExtSerializer(
              false, "Problème survenu lors de la mise à jour des coordonnées")
          .serialize();
    }
  }

  @RequestMapping(
      value = "/getDesaffectationMesssage",
      method = RequestMethod.POST,
      headers = "Accept=application/json")
  public ResponseEntity<java.lang.String> getDesaffectationMesssage(
      final @RequestParam(value = "hydrants", required = false) String hydrants) {
    try {
      String reponse =
          hydrantService.getDesaffectationMesssage(
              hydrants,
              Organisme.getOrganismeAndChildren(
                  (serviceUtilisateur.getCurrentUtilisateur().getOrganisme().getId()).intValue()));
      return new SuccessErrorExtSerializer(true, reponse).serialize();
    } catch (Exception e) {
      System.out.println(e);
      return new SuccessErrorExtSerializer(
              false, "Problème survenu lors de la mise à jour des coordonnées")
          .serialize();
    }
  }

  @RequestMapping(
      value = "/importctpverification",
      method = RequestMethod.POST,
      headers = "Content-Type=multipart/form-data")
  public ResponseEntity<java.lang.String> importCtpVerification(
      MultipartHttpServletRequest request) {
    try {
      MultipartFile mf = request.getFile("file");
      String result = this.importCtpUseCase.importCTP(mf);
      return new SuccessErrorExtSerializer(true, result).serialize();
    } catch (Exception e) {
      this.logger.error(e.getMessage(), e);
      return new SuccessErrorExtSerializer(
              false, "Problème survenu lors de la vérification de l'import CTP")
          .serialize();
    }
  }

  @RequestMapping(
      value = "/importctp",
      method = RequestMethod.POST,
      headers = "Accept=application/json")
  public ResponseEntity<java.lang.String> importCtp(MultipartHttpServletRequest request) {
    try {
      String json = request.getParameter("visites");
      this.importCtpUseCase.addVisiteFromImportCtp(json);
      return new SuccessErrorExtSerializer(true, "").serialize();
    } catch (Exception e) {
      this.logger.error(e.getMessage(), e);
      return new SuccessErrorExtSerializer(false, "Problème survenu lors de l'import CTP'")
          .serialize();
    }
  }

  @RequestMapping(
      value = "/records",
      method = RequestMethod.GET,
      headers = "Accept=application/json")
  @PreAuthorize("hasRight('HYDRANTS_R')")
  public ResponseEntity<java.lang.String> listRecords(
      final @RequestParam(value = "page", required = false) Integer page,
      final @RequestParam(value = "start", required = false) Integer start,
      final @RequestParam(value = "limit", required = false) Integer limit,
      final @RequestParam(value = "sort", required = false) String sorts,
      final @RequestParam(value = "filter", required = false) String filters,
      final @RequestParam(value = "query", required = false) String query) {

    final List<ItemSorting> sortList = ItemSorting.decodeJson(sorts);
    final List<ItemFilter> itemFilterList = ItemFilter.decodeJson(filters);

    if (query != null && !query.isEmpty()) {
      itemFilterList.add(new ItemFilter("numero", query));
    }

    itemFilterList.add(new ItemFilter("zoneCompetence", "true"));

    return new AbstractExtListSerializer<HydrantRecord>(
        "fr.sdis83.remocra.domain.remocra.Hydrant retrieved.") {

      @Override
      protected JSONSerializer additionnalIncludeExclude(JSONSerializer serializer) {
        return new JSONSerializer()
            .exclude("*.class")
            .transform(new GeometryTransformer(), Geometry.class);
      }

      @Override
      protected List<HydrantRecord> getRecords() {
        List<HydrantRecord> l = hydrantRepository.getAll(itemFilterList, limit, start, sortList);
        return l;
      }

      @Override
      protected Long countRecords() {
        Long total = Long.valueOf(hydrantRepository.countHydrants(itemFilterList));
        return total;
      }
    }.serialize();
  }
}
