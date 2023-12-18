package fr.sdis83.remocra.web;

import static fr.sdis83.remocra.util.GeometryUtil.sridFromGeom;

import com.vividsolutions.jts.geom.Geometry;
import flexjson.JSONDeserializer;
import flexjson.JSONSerializer;
import fr.sdis83.remocra.db.model.remocra.tables.pojos.CriseSuivi;
import fr.sdis83.remocra.db.model.remocra.tables.pojos.TypeCriseProprieteEvenement;
import fr.sdis83.remocra.domain.remocra.RemocraVueCombo;
import fr.sdis83.remocra.domain.utils.RemocraDateHourTransformer;
import fr.sdis83.remocra.domain.utils.RemocraInstantTransformer;
import fr.sdis83.remocra.exception.BusinessException;
import fr.sdis83.remocra.ogc.cql.CompositeStatement;
import fr.sdis83.remocra.ogc.cql.Conjunction;
import fr.sdis83.remocra.ogc.cql.ObjectStatement;
import fr.sdis83.remocra.ogc.cql.Operator;
import fr.sdis83.remocra.ogc.cql.Statement;
import fr.sdis83.remocra.repository.CriseEvenementRepository;
import fr.sdis83.remocra.repository.CriseRepository;
import fr.sdis83.remocra.service.UtilisateurService;
import fr.sdis83.remocra.service.ZoneCompetenceService;
import fr.sdis83.remocra.util.FeatureUtil;
import fr.sdis83.remocra.util.GeometryUtil;
import fr.sdis83.remocra.web.deserialize.GeometryFactory;
import fr.sdis83.remocra.web.message.ItemFilter;
import fr.sdis83.remocra.web.model.CriseEvenement;
import fr.sdis83.remocra.web.serialize.ext.AbstractExtListSerializer;
import fr.sdis83.remocra.web.serialize.ext.AbstractExtObjectSerializer;
import fr.sdis83.remocra.web.serialize.ext.SuccessErrorExtSerializer;
import fr.sdis83.remocra.web.serialize.transformer.GeometryTransformer;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.Instant;
import org.jooq.Record;
import org.jooq.Result;
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
import org.springframework.web.multipart.MultipartHttpServletRequest;

@RequestMapping("/evenements")
@Controller
public class CriseEvenementController {

  private final Logger log = Logger.getLogger(getClass());

  @Autowired CriseRepository criseRepository;

  @Autowired CriseEvenementRepository criseEvenementRepository;

  @Autowired private ZoneCompetenceService zoneCompetenceService;

  @Autowired private UtilisateurService utilisateurService;

  @Autowired private GeoserverController geoserverController;

  @RequestMapping(
      value = "/{idCrise}",
      method = RequestMethod.GET,
      headers = "Accept=application/xml")
  @PreAuthorize("hasRight('CRISE_R')")
  public ResponseEntity<String> getEvents(
      final @PathVariable(value = "idCrise") Long id,
      @RequestParam(value = "filter", required = false) String filters) {

    final List<ItemFilter> itemFilterList = ItemFilter.decodeJson(filters);

    return new AbstractExtListSerializer<CriseEvenement>("Evènements retrieved.") {

      @Override
      protected JSONSerializer getJsonSerializer() {
        return new JSONSerializer()
            .exclude("*.class")
            .exclude("*.password")
            .exclude("*.salt")
            .transform(new GeometryTransformer(), Geometry.class)
            .transform(RemocraDateHourTransformer.getInstance(), Date.class)
            .transform(new RemocraInstantTransformer(), Instant.class)
            .include("data.*")
            .include("total")
            .include("message");
      }

      @Override
      protected JSONSerializer additionnalIncludeExclude(JSONSerializer serializer) {
        serializer.include("data.*");

        return serializer.include("message");
      }

      @Override
      protected List<CriseEvenement> getRecords() {
        return criseEvenementRepository.getEventByCrise(id, itemFilterList);
      }
    }.serialize();
  }

  @RequestMapping(
      value = "/{idCrise}/{idEvenement}",
      method = RequestMethod.GET,
      headers = "Accept=application/xml")
  @PreAuthorize("hasRight('CRISE_R')")
  public ResponseEntity<String> getEventsById(final @PathVariable(value = "idEvenement") Long id) {

    return new AbstractExtListSerializer<CriseEvenement>("Evènements retrieved.") {

      @Override
      protected JSONSerializer getJsonSerializer() {
        return new JSONSerializer()
            .exclude("*.class")
            .exclude("*.password")
            .exclude("*.salt")
            .transform(new GeometryTransformer(), Geometry.class)
            .transform(RemocraDateHourTransformer.getInstance(), Date.class)
            .transform(new RemocraInstantTransformer(), Instant.class)
            .include("data.*")
            .include("total")
            .include("message");
      }

      @Override
      protected JSONSerializer additionnalIncludeExclude(JSONSerializer serializer) {
        serializer.include("data.*");

        return serializer.include("message");
      }

      @Override
      protected List<CriseEvenement> getRecords() {
        return criseEvenementRepository.getEventById(id);
      }
    }.serialize();
  }

  @RequestMapping(
      value = "",
      method = RequestMethod.POST,
      headers = "Content-Type=multipart/form-data")
  @PreAuthorize("hasRight('CRISE_C')")
  @Transactional
  public ResponseEntity<java.lang.String> createCriseEvent(MultipartHttpServletRequest request) {
    try {
      fr.sdis83.remocra.db.model.remocra.tables.pojos.CriseEvenement c =
          criseEvenementRepository.createEvent(request);
      return new SuccessErrorExtSerializer(true, "La crise " + c.getNom() + " a été enregistrée")
          .serialize();
    } catch (Exception e) {
      return new SuccessErrorExtSerializer(
              false, "Une erreur est survenue lors de la création de l'évènement")
          .serialize();
    }
  }

  @RequestMapping(
      value = "/message",
      method = RequestMethod.POST,
      headers = "Accept=application/json")
  @PreAuthorize("hasRight('CRISE_C')")
  @Transactional
  public ResponseEntity<java.lang.String> createMessage(final @RequestBody String json) {
    try {

      CriseSuivi cs = criseEvenementRepository.createMessage(json);
      return new SuccessErrorExtSerializer(true, "La crise " + cs.getObjet() + " a été enregistrée")
          .serialize();
    } catch (Exception e) {
      return new SuccessErrorExtSerializer(
              false, "Une erreur est survenue lors de la création du message")
          .serialize();
    }
  }

  @RequestMapping(value = "/layer", method = RequestMethod.GET, headers = "Accept=application/json")
  @PreAuthorize("hasRight('CRISE_R')")
  public ResponseEntity<java.lang.String> layer(
      final @RequestParam String point,
      @RequestParam String projection,
      @RequestParam String crise) {

    if (point == null || point.isEmpty()) {
      return null;
    } else {
      if (projection.contains("EPSG:")) {
        projection = projection.replace("EPSG:", "");
      }
      return FeatureUtil.getResponse(
          criseEvenementRepository.findCriseEventsByPoint(point, projection, Long.valueOf(crise)));
    }
  }

  @RequestMapping(
      value = "/proprietes/{idNature}",
      method = RequestMethod.GET,
      headers = "Accept=application/json")
  @PreAuthorize("hasRight('CRISE_R')")
  public ResponseEntity<String> getProprietes(final @PathVariable(value = "idNature") Long id) {

    return new AbstractExtListSerializer<TypeCriseProprieteEvenement>("Proprietes retrieved.") {

      @Override
      protected JSONSerializer getJsonSerializer() {
        return new JSONSerializer()
            .transform(RemocraDateHourTransformer.getInstance(), Date.class)
            .transform(new RemocraInstantTransformer(), Instant.class)
            .include("data.*")
            .include("total")
            .include("message");
      }

      @Override
      protected JSONSerializer additionnalIncludeExclude(JSONSerializer serializer) {
        serializer.include("data.*");

        return serializer.include("message");
      }

      @Override
      protected List<TypeCriseProprieteEvenement> getRecords() {
        return criseEvenementRepository.getProprietes(id);
      }
    }.serialize();
  }

  /**
   * Retourne la liste des valeurs pour un paramètre de type 'combo'.
   *
   * @param id
   * @return
   */
  @RequestMapping(
      value = "/evenementmodparalst/{id}",
      method = RequestMethod.GET,
      headers = "Accept=application/json")
  @PreAuthorize("hasRight('CRISE_C')")
  public ResponseEntity<java.lang.String> getListComboModelProprieteLike(
      final @PathVariable("id") Long id,
      final @RequestParam(value = "query", required = false) String query,
      final @RequestParam(value = "limit", required = false) Integer limit) {

    return new AbstractExtListSerializer<RemocraVueCombo>(" retrieved.") {

      @Override
      protected List<RemocraVueCombo> getRecords() {
        try {
          return criseEvenementRepository.getComboValues(id, query, limit != null ? limit : 10);
        } catch (SQLException e) {
          e.printStackTrace();
        } catch (ParseException e) {
          e.printStackTrace();
        }
        return null;
      }
    }.serialize();
  }

  @RequestMapping(
      value = "/message/{idMessage}",
      method = RequestMethod.GET,
      headers = "Accept=application/xml")
  @PreAuthorize("hasRight('CRISE_R')")
  public ResponseEntity<String> getMessageById(final @PathVariable(value = "idMessage") Long id) {

    return new AbstractExtListSerializer<CriseSuivi>("Message retrieved.") {

      @Override
      protected JSONSerializer getJsonSerializer() {
        return new JSONSerializer()
            .transform(RemocraDateHourTransformer.getInstance(), Date.class)
            .transform(new RemocraInstantTransformer(), Instant.class)
            .include("data.*")
            .include("total")
            .include("message");
      }

      @Override
      protected JSONSerializer additionnalIncludeExclude(JSONSerializer serializer) {
        serializer.include("data.*");

        return serializer.include("message");
      }

      @Override
      protected List<CriseSuivi> getRecords() {
        return criseEvenementRepository.getMessageById(id);
      }
    }.serialize();
  }

  @Transactional
  @RequestMapping(value = "/{idEvent}/docevents", method = RequestMethod.GET)
  @PreAuthorize("hasRight('CRISE_R')")
  public ResponseEntity<java.lang.String> getDocuments(
      @PathVariable("idEvent") final Long idEvent) {
    return new AbstractExtListSerializer<fr.sdis83.remocra.db.model.remocra.tables.pojos.Document>(
        "Crise Document retrieved.") {

      @Override
      protected JSONSerializer getJsonSerializer() {
        return new JSONSerializer()
            .exclude("*.class")
            .transform(RemocraDateHourTransformer.getInstance(), Date.class)
            .transform(new RemocraInstantTransformer(), Instant.class)
            .include("data.*")
            .include("total")
            .include("message");
      }

      @Override
      protected JSONSerializer additionnalIncludeExclude(JSONSerializer serializer) {
        serializer.include("data.*");

        return serializer.include("total").include("message");
      }

      @Override
      protected List<fr.sdis83.remocra.db.model.remocra.tables.pojos.Document> getRecords() {
        return criseEvenementRepository.getDocuments(idEvent);
      }

      @Override
      protected Long countRecords() {
        return Long.valueOf(criseEvenementRepository.countDocuments(idEvent));
      }
    }.serialize();
  }

  @RequestMapping(
      value = "/{id}",
      method = RequestMethod.POST,
      headers = "Content-Type=multipart/form-data")
  @PreAuthorize("hasRight('CRISE_C')")
  @Transactional
  public ResponseEntity<java.lang.String> updateCriseEvent(
      @PathVariable("id") Long id, MultipartHttpServletRequest request) {
    try {

      fr.sdis83.remocra.db.model.remocra.tables.pojos.CriseEvenement c =
          criseEvenementRepository.updateEvent(id, request);
      return new SuccessErrorExtSerializer(true, "L\'évenement " + c.getNom() + " a été mis à jour")
          .serialize();
    } catch (Exception e) {
      return new SuccessErrorExtSerializer(
              false, "Une erreur est survenue lors de la mise à jour de l\'évènement'")
          .serialize();
    }
  }

  @RequestMapping(
      value = "/origines/{idCrise}",
      method = RequestMethod.GET,
      headers = "Accept=application/json")
  @PreAuthorize("hasRight('CRISE_R')")
  public ResponseEntity<java.lang.String> getOrigines(
      final @PathVariable(value = "idCrise") Long idCrise,
      final @RequestParam(value = "query", required = false) String query) {

    return new AbstractExtObjectSerializer<List<String>>("origines retrieved.") {

      @Override
      protected List<String> getRecord() {
        return criseEvenementRepository.getCriseOrigines(idCrise, query);
      }
    }.serialize();
  }

  @RequestMapping(
      value = "/tags/{idCrise}",
      method = RequestMethod.GET,
      headers = "Accept=application/json")
  @PreAuthorize("hasRight('CRISE_C')")
  public ResponseEntity<java.lang.String> getTags(
      final @PathVariable(value = "idCrise") Long idCrise) {

    return new AbstractExtObjectSerializer<List<String>>("tags retrieved.") {

      @Override
      protected List<String> getRecord() {
        return criseEvenementRepository.getCriseTags(idCrise);
      }
    }.serialize();
  }

  @RequestMapping(
      value = "/{idEvent}/updategeom",
      method = RequestMethod.POST,
      headers = "Accept=application/json")
  @PreAuthorize("hasRight('CRISE_C') or hasRight('CRISE_U')")
  @Transactional
  public ResponseEntity<java.lang.String> updateGeometrie(
      final @PathVariable(value = "idEvent") Long id, final @RequestBody String json) {
    try {
      Map<String, Object> wkt =
          new JSONDeserializer<HashMap<String, Object>>()
              .use(Geometry.class, new GeometryFactory())
              .deserialize(json);
      Geometry geom = null;
      String[] coord = String.valueOf(wkt.get("geometrie")).split(";");
      Integer srid = sridFromGeom(coord[0]);
      geom = GeometryUtil.toGeometry(coord[1], srid);
      Boolean result =
          zoneCompetenceService.check(
              coord[1], srid, utilisateurService.getCurrentZoneCompetenceId());
      // Boolean isClos = criseEvenementRepository.getClos(id);
      if (!result) {
        return new SuccessErrorExtSerializer(
                result, "Modification de la géometrie de l'évènement non autorisée")
            .serialize();
      }

      Result<Record> r = criseEvenementRepository.updateGeom(id, coord[1], srid);
      if (r.isEmpty()) {
        return new SuccessErrorExtSerializer(true, "Clos").serialize();
      }

    } catch (Exception e) {
      return new SuccessErrorExtSerializer(false, e.getMessage().toString()).serialize();
    }

    return new SuccessErrorExtSerializer(true, "Géométrie de l'évènement mise à jour.").serialize();
  }

  @RequestMapping(value = "/wms", method = RequestMethod.GET)
  @PreAuthorize("hasRight('CRISE_R')")
  public void proxyWms(
      HttpServletRequest request,
      HttpServletResponse response,
      @RequestParam(value = "filter", required = false) String filters)
      throws BusinessException {
    Map<String, String> params = GeoserverController.getMapParamsFromRequest(request);

    List<ItemFilter> itemFilters = ItemFilter.decodeJson(filters);
    Map<String, Statement> statementMap = toStatementMap(itemFilters);

    // Sécurité : au moins une crise passée et vérification des accès
    CompositeStatement cs = (CompositeStatement) statementMap.get("crise");
    Collection<Long> crises = new LinkedList<Long>();
    for (Statement s : cs.getStatements()) {
      ObjectStatement os = (ObjectStatement) s;
      crises.add(Long.parseLong(os.getValue()));
    }
    if (crises.size() < 1) {
      log.error("Couche évènements de crise : aucune crise fournie");
      response.setStatus(400);
    } else {
      // On compte les crises accessibles
      Long count = criseRepository.countAccessiblesCrisesIn((Long[]) crises.toArray(new Long[] {}));
      if (count != crises.size()) {
        log.error("Couche évènements de crise : au moins une crise inaccessible");
        response.setStatus(403);
      }
    }

    // Sécurité : filtre sur crise active
    statementMap.put("SEC_crise_active", new ObjectStatement("crise_active", Operator.EQ, "true"));

    // Sécurité : filtre sur type_crise_categorie_evenement
    String categorieEvenementIds = getCategorieEvenementIdsForProfilDroitStr();
    if (categorieEvenementIds == null) {
      log.error("Couche évènements de crise : aucune catégorie  d'évènement accessible");
      response.setStatus(403);
      return;
    }
    statementMap.put(
        "SEC_categorie_evenement",
        new ObjectStatement("categorie_evenement", Operator.IN, categorieEvenementIds));

    String cqlFilter = toCQLFilter(statementMap);
    if (cqlFilter != null && cqlFilter.length() > 0) {
      params.put("CQL_FILTER", cqlFilter);
    }

    // Nettoyage et appel au proxy WMS
    params.remove("filter");
    geoserverController.proxyWms(request, response, "remocra/wms", params);
  }

  String getCategorieEvenementIdsForProfilDroitStr() throws BusinessException {
    Long[] ids =
        criseEvenementRepository.getCategorieEvenementIdsForProfilDroit(
            utilisateurService.getCurrentProfilDroit().getId());
    if (ids.length < 1) {
      return null;
    }
    StringBuffer sbIds = new StringBuffer("(");
    for (Long l : ids) {
      if (sbIds.length() > 1) {
        sbIds.append(",");
      }
      sbIds.append(l);
    }
    sbIds.append(")");
    return sbIds.toString();
  }

  /**
   * Crée une requête CQL "ET" à partir des filtres fournis
   *
   * @param filters
   * @return
   */
  String toCQLFilter(List<ItemFilter> filters) {
    Map<String, Statement> statementMap = toStatementMap(filters);
    return toCQLFilter(statementMap);
  }

  /**
   * Crée une requête CQL "ET" à partir des groupes de statements fournis
   *
   * @param statementMap
   * @return
   */
  String toCQLFilter(Map<String, Statement> statementMap) {
    Statement s = null;
    if (statementMap.size() < 1) {
      return null;
    } else if (statementMap.size() == 1) {
      s = statementMap.values().iterator().next();
    } else {
      s = new CompositeStatement(Conjunction.AND, statementMap.values());
    }
    return s.toCQL();
  }

  /**
   * Crée une Map : Clé de groupe -> Statements pour réaliser une requête "ET" entre les groupes et
   * "OU" au sein des groupes
   *
   * @param filters
   * @return
   */
  Map<String, Statement> toStatementMap(List<ItemFilter> filters) {
    Map<String, Statement> returned = new HashMap<String, Statement>();
    for (ItemFilter filter : filters) {
      if ("crise".equals(filter.getFieldName())) {
        secureAddToCompositeOrStatement(
            returned, "crise", new ObjectStatement("crise", Operator.EQ, filter.getValue()));
      } else if ("type".equals(filter.getFieldName())) {
        secureAddToCompositeOrStatement(
            returned,
            "type",
            new ObjectStatement("nature_evenement", Operator.EQ, filter.getValue()));
      } else if ("statut".equals(filter.getFieldName())) {
        ObjectStatement newStmt = null;
        if ("En cours".equals(filter.getValue())) {
          newStmt = new ObjectStatement("cloture", Operator.IS_NULL);
        } else if ("Clos".equals(filter.getValue())) {
          newStmt = new ObjectStatement("cloture", Operator.IS_NOT_NULL);
        }
        if (newStmt != null) {
          secureAddToCompositeOrStatement(returned, "statut", newStmt);
        }
      } else if ("periode".equals(filter.getFieldName())) {
        String periodeValue = filter.getValue();
        int minutes = 0;
        if ("<10mn".equals(periodeValue)) {
          minutes = 10;
        } else if ("<30mn".equals(periodeValue)) {
          minutes = 30;
        } else if ("<1h".equals(periodeValue)) {
          minutes = 60;
        } else if ("<24h".equals(periodeValue)) {
          minutes = 1440;
        } else {
          log.warn("Filtre CQL inconnu, " + filter.getFieldName() + " : " + periodeValue);
          continue;
        }
        DateTime periodeDT = new DateTime().minusMinutes(minutes);
        secureAddToCompositeOrStatement(
            returned,
            "periode",
            new ObjectStatement("dernier_message", Operator.AFTER, periodeDT.toString()));
      } else if ("tag".equals(filter.getFieldName())) {
        secureAddToCompositeOrStatement(
            returned,
            "origine",
            new ObjectStatement("tags", Operator.LIKE, "'%" + filter.getValue() + "%'"));
      } else if ("origine".equals(filter.getFieldName())) {
        secureAddToCompositeOrStatement(
            returned,
            "origine",
            new ObjectStatement("origine", Operator.EQ, "'" + filter.getValue() + "'"));
      } // on n'applique aucun filtre en mode anticipation
      else if ("contexte".equals(filter.getFieldName())) {
        if (!(("ANTICIPATION").equals(filter.getValue()))) {
          secureAddToCompositeOrStatement(
              returned,
              "contexte",
              new ObjectStatement("contexte", Operator.EQ, "'" + filter.getValue() + "'"));
        }

      } else if ("importance".equals(filter.getFieldName())) {
        List<Integer> filterValue = new ArrayList<Integer>();
        int value = Integer.valueOf(filter.getValue());
        while (value != 0) {
          filterValue.add(value);
          value--;
        }
        StringBuffer sbIds = new StringBuffer("(");
        for (Integer l : filterValue) {
          if (sbIds.length() > 1) {
            sbIds.append(",");
          }
          sbIds.append(l);
        }
        sbIds.append(")");
        secureAddToCompositeOrStatement(
            returned,
            "importance",
            new ObjectStatement("importance", Operator.IN, sbIds.toString()));
      } else {
        log.warn("Filtre CQL inconnu : " + filter.getFieldName());
      }
    }
    return returned;
  }

  /**
   * Utilitaire qui ajoute un statement à la Map de manière sécurisée.
   *
   * @param statements
   * @param key
   * @param statement
   */
  void secureAddToCompositeOrStatement(
      Map<String, Statement> statements, String key, Statement statement) {
    CompositeStatement s = (CompositeStatement) statements.get(key);
    if (s == null) {
      s = new CompositeStatement(Conjunction.OR);
      statements.put(key, s);
    }
    s.addStatement(statement);
  }
}
