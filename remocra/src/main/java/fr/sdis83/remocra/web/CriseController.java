package fr.sdis83.remocra.web;

import com.vividsolutions.jts.geom.Geometry;
import flexjson.JSONDeserializer;
import flexjson.JSONSerializer;
import fr.sdis83.remocra.db.model.remocra.tables.pojos.ProcessusEtlPlanification;
import fr.sdis83.remocra.domain.utils.RemocraDateHourTransformer;
import fr.sdis83.remocra.domain.utils.RemocraInstantTransformer;
import fr.sdis83.remocra.repository.CriseRepository;
import fr.sdis83.remocra.repository.ProcessusEtlPlanificationRepository;
import fr.sdis83.remocra.repository.TypeCriseStatutRepository;
import fr.sdis83.remocra.service.ParamConfService;
import fr.sdis83.remocra.util.GeometryUtil;
import fr.sdis83.remocra.web.message.ItemFilter;
import fr.sdis83.remocra.web.message.ItemSorting;
import fr.sdis83.remocra.web.model.Crise;
import fr.sdis83.remocra.web.model.CriseDocument;
import fr.sdis83.remocra.web.serialize.ext.AbstractExtListSerializer;
import fr.sdis83.remocra.web.serialize.ext.AbstractExtObjectSerializer;
import fr.sdis83.remocra.web.serialize.ext.SuccessErrorExtSerializer;
import fr.sdis83.remocra.web.serialize.transformer.GeometryTransformer;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.joda.time.Instant;
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

@RequestMapping("/crises")
@Controller
public class CriseController {

  @Autowired private CriseRepository criseRepository;

  @Autowired private ProcessusEtlPlanificationRepository processusEtlPlanificationRepository;

  @Autowired private ParamConfService paramConfService;

  @Autowired private TypeCriseStatutRepository typeCriseStatutRepository;

  @RequestMapping(value = "", method = RequestMethod.GET, headers = "Accept=application/xml")
  @PreAuthorize("hasRight('CRISE_R')")
  public ResponseEntity<String> listJson(
      final @RequestParam(value = "page", required = false) Integer page,
      final @RequestParam(value = "start", required = false) Integer start,
      final @RequestParam(value = "limit", required = false) Integer limit,
      final @RequestParam(value = "query", required = false) String query,
      @RequestParam(value = "sort", required = false) String sorts,
      @RequestParam(value = "filter", required = false) String filters) {
    final List<ItemFilter> itemFilterList = ItemFilter.decodeJson(filters);
    final List<ItemSorting> sortList = ItemSorting.decodeJson(sorts);

    return new AbstractExtListSerializer<Crise>("Crise retrieved.") {

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

        return serializer.include("total").include("message");
      }

      @Override
      protected List<Crise> getRecords() {
        return criseRepository.getAll(itemFilterList, limit, start, sortList);
      }

      @Override
      protected Long countRecords() {
        return Long.valueOf(criseRepository.count(itemFilterList));
      }
    }.serialize();
  }

  @RequestMapping(value = "/{id}", method = RequestMethod.GET, headers = "Accept=application/json")
  @PreAuthorize("hasRight('CRISE_R')")
  @Transactional
  public ResponseEntity<java.lang.String> getCrise(
      @PathVariable("id") final Long id, @RequestBody String json) {
    return new AbstractExtObjectSerializer<Crise>("Crise retrieved.") {

      @Override
      protected JSONSerializer additionnalIncludeExclude(JSONSerializer serializer) {
        return serializer
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
      protected Crise getRecord() {
        return criseRepository.getCriseById(id);
      }
    }.serialize();
  }

  @RequestMapping(value = "", method = RequestMethod.POST, headers = "Accept=application/json")
  @PreAuthorize("hasRight('CRISE_C')")
  @Transactional
  public ResponseEntity<java.lang.String> createCrise(final @RequestBody String json)
      throws ParseException {
    try {
      List<ProcessusEtlPlanification> existantPlanifs =
          processusEtlPlanificationRepository.getAll();
      fr.sdis83.remocra.db.model.remocra.tables.pojos.Crise c = criseRepository.create(json);
      List<ProcessusEtlPlanification> afterInsertPlannifs =
          processusEtlPlanificationRepository.getAll();
      List<ProcessusEtlPlanification> newPlannifs = new ArrayList<ProcessusEtlPlanification>();
      for (ProcessusEtlPlanification newP : afterInsertPlannifs) {
        boolean toAdd = true;
        for (ProcessusEtlPlanification oldP : existantPlanifs) {
          if (newP.getId().longValue() == oldP.getId().longValue()) {
            toAdd = false;
          }
        }
        if (toAdd) {
          newPlannifs.add(newP);
        }
      }
      processusEtlPlanificationRepository.scheduleJobs(newPlannifs);
      return new SuccessErrorExtSerializer(true, "La crise " + c.getNom() + " a été enregistrée")
          .serialize();
    } catch (Exception e) {
      return new SuccessErrorExtSerializer(
              false, "Une erreur est survenue lors de la création de la crise")
          .serialize();
    }
  }

  @Transactional
  @RequestMapping(
      value = "/{idCrise}/documents",
      method = RequestMethod.POST,
      headers = "Content-Type=multipart/form-data")
  @PreAuthorize("hasRight('CRISE_C')")
  public ResponseEntity<java.lang.String> addNewDocuments(
      final @PathVariable("idCrise") Long idCrise, MultipartHttpServletRequest request) {
    try {
      Map<String, MultipartFile> files = request.getFileMap();
      Geometry geom = null;
      if (request.getParameter("geometrie") != null
          && !(request.getParameter("geometrie").isEmpty())) {
        String geometrie = request.getParameter("geometrie");
        geom = GeometryUtil.geometryFromBBox(geometrie);
      }
      // traitement des fichiers
      int i = criseRepository.addDocuments(files, idCrise, geom);
      if (i != 0) {
        return new SuccessErrorExtSerializer(true, "Documents ajoutés ").serialize();
      }
      return new SuccessErrorExtSerializer(false, "Erreur lors De l'enregistrement des documents")
          .serialize();

    } catch (Exception e) {
      return new SuccessErrorExtSerializer(false, "Erreur lors De l'enregistrement des documents")
          .serialize();
    }
  }

  @Transactional
  @RequestMapping(value = "/{idCrise}/documents", method = RequestMethod.GET)
  @PreAuthorize("hasRight('CRISE_C')")
  public ResponseEntity<java.lang.String> getDocuments(
      @PathVariable("idCrise") final Long idCrise) {
    return new AbstractExtListSerializer<CriseDocument>("Crise Document retrieved.") {

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
      protected List<CriseDocument> getRecords() {
        try {
          return criseRepository.getDocuments(idCrise);
        } catch (ParseException e) {
          e.printStackTrace();
        }
        return null;
      }

      @Override
      protected Long countRecords() {
        return Long.valueOf(criseRepository.countDocuments(idCrise));
      }
    }.serialize();
  }

  @RequestMapping(value = "/{id}", method = RequestMethod.PUT, headers = "Accept=application/json")
  @PreAuthorize("hasRight('CRISE_C')")
  @Transactional
  public ResponseEntity<java.lang.String> updateFromJson(
      @PathVariable("id") Long id, @RequestBody String json) {
    try {

      List<ProcessusEtlPlanification> existantPlanifs =
          processusEtlPlanificationRepository.getAll();
      fr.sdis83.remocra.db.model.remocra.tables.pojos.Crise c = criseRepository.update(id, json);
      List<ProcessusEtlPlanification> afterInsertPlannifs =
          processusEtlPlanificationRepository.getAll();
      List<ProcessusEtlPlanification> toStartPlannifs = new ArrayList<ProcessusEtlPlanification>();
      List<ProcessusEtlPlanification> toDeletePlannifs = new ArrayList<ProcessusEtlPlanification>();
      for (ProcessusEtlPlanification newP : afterInsertPlannifs) {
        boolean toAdd = true;
        for (ProcessusEtlPlanification oldP : existantPlanifs) {
          if (newP.getId().longValue() == oldP.getId().longValue()) {
            toAdd = false;
          }
        }
        if (toAdd) {
          toStartPlannifs.add(newP);
        }
      }

      for (ProcessusEtlPlanification oldP : existantPlanifs) {
        boolean toDelete = true;
        for (ProcessusEtlPlanification newP : afterInsertPlannifs) {
          if (oldP.getId().longValue() == newP.getId().longValue()) {
            toDelete = false;
          }
        }
        if (toDelete) {
          toDeletePlannifs.add(oldP);
        }
      }
      if (c.getStatut().longValue()
          == typeCriseStatutRepository.getByCode("TERMINE").getId().longValue()) {
        processusEtlPlanificationRepository.killJobs(toDeletePlannifs);
        return new SuccessErrorExtSerializer(true, "La crise " + c.getNom() + " est Terminée")
            .serialize();

      } else {
        processusEtlPlanificationRepository.rescheduleJobs(toDeletePlannifs, toStartPlannifs);
        return new SuccessErrorExtSerializer(true, "La crise " + c.getNom() + " est mise à jours")
            .serialize();
      }
    } catch (Exception e) {
      return new SuccessErrorExtSerializer(
              false, "Une erreur est survenue lors de la mise à jour de la crise")
          .serialize();
    }
  }

  @RequestMapping(
      value = "/{id}/fusion",
      method = RequestMethod.POST,
      headers = "Accept=application/json")
  @PreAuthorize("hasRight('CRISE_C')")
  @Transactional
  public ResponseEntity<java.lang.String> fusionFromJson(
      @PathVariable("id") Long id, @RequestBody String json) {
    try {

      HashMap<String, Object> items =
          new JSONDeserializer<HashMap<String, Object>>().deserialize(json);

      DateFormat df = new SimpleDateFormat(RemocraDateHourTransformer.FORMAT);
      Date dateFusion = df.parse(String.valueOf(items.get("dateFusion")));
      List<Long> idFusionnedCrises = new ArrayList<Long>();
      List<Integer> idFusionned = (List<Integer>) items.get("fusionnedCrises");
      for (Integer i : idFusionned) {
        idFusionnedCrises.add(Long.valueOf(i));
      }
      List<ProcessusEtlPlanification> toDelete = new ArrayList<ProcessusEtlPlanification>();
      // on arrete les jobs associées aux processusetlplannifications
      for (Long idFusionnedCrise : idFusionnedCrises) {
        toDelete.addAll(
            processusEtlPlanificationRepository.getPlanifById(Long.valueOf(idFusionnedCrise)));
      }
      processusEtlPlanificationRepository.killJobs(toDelete);
      // on supprime les processusetlPlanifications
      for (Long idFusionnedCrise : idFusionnedCrises) {
        processusEtlPlanificationRepository.deletePlanifs(Long.valueOf(idFusionnedCrise));
      }
      criseRepository.fusion(id, idFusionnedCrises, dateFusion);
      return new SuccessErrorExtSerializer(true, "La fusion des crises a été effectué avec succèes")
          .serialize();

    } catch (Exception e) {
      return new SuccessErrorExtSerializer(
              false, "Une erreur est survenue lors de la mise à jour de la crise")
          .serialize();
    }
  }

  @RequestMapping(
      value = "/{id}/geometrie",
      method = RequestMethod.GET,
      headers = "Accept=application/json")
  @PreAuthorize("hasRight('CRISE_R')")
  public ResponseEntity<java.lang.String> getExtent(final @PathVariable("id") Long id) {

    return new AbstractExtObjectSerializer<String>("Crise retrieved.") {

      @Override
      protected JSONSerializer additionnalIncludeExclude(JSONSerializer serializer) {
        return serializer
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
      protected String getRecord() {
        return criseRepository.getExtentById(id);
      }
    }.serialize();
  }

  @RequestMapping(
      value = "/coordonneaffichage",
      method = RequestMethod.GET,
      headers = "Accept=application/json")
  @PreAuthorize("hasRight('CRISE_R')")
  public ResponseEntity<java.lang.String> getParamConf() {

    return new AbstractExtObjectSerializer<String>("CoordonneAffichage retrieved.") {

      @Override
      protected String getRecord() {
        return paramConfService.getCoordonneesFormatAffichage();
      }
    }.serialize();
  }

  @RequestMapping(
      value = "/cleign",
      method = RequestMethod.GET,
      headers = "Accept=application/json")
  @PreAuthorize("hasRight('CRISE_R')")
  public ResponseEntity<java.lang.String> getCleIgn() {

    return new AbstractExtObjectSerializer<String>("cleign retrieved.") {

      @Override
      protected String getRecord() {
        return paramConfService.getClesIgn();
      }
    }.serialize();
  }

  @RequestMapping(
      value = "/{id}/activation",
      method = RequestMethod.GET,
      headers = "Accept=application/json")
  @PreAuthorize("hasRight('CRISE_R')")
  public ResponseEntity<java.lang.String> getActivation(final @PathVariable("id") Long id) {

    return new AbstractExtObjectSerializer<String>("Crise retrieved.") {

      @Override
      protected JSONSerializer additionnalIncludeExclude(JSONSerializer serializer) {
        return serializer
            .exclude("*.class")
            .exclude("*.password")
            .exclude("*.salt")
            .transform(RemocraDateHourTransformer.getInstance(), Date.class)
            .transform(new RemocraInstantTransformer(), Instant.class)
            .include("data.*")
            .include("total")
            .include("message");
      }

      @Override
      protected String getRecord() {
        return criseRepository.getActivationById(id);
      }
    }.serialize();
  }
}
