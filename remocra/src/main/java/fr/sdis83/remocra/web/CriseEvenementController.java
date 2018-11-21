package fr.sdis83.remocra.web;

import static fr.sdis83.remocra.util.GeometryUtil.sridFromGeom;

import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.vividsolutions.jts.geom.Geometry;
import flexjson.JSONDeserializer;
import flexjson.JSONSerializer;
import fr.sdis83.remocra.db.model.remocra.tables.pojos.CriseSuivi;
import fr.sdis83.remocra.domain.utils.RemocraDateHourTransformer;
import fr.sdis83.remocra.domain.utils.RemocraInstantTransformer;
import fr.sdis83.remocra.repository.CriseEvenementRepository;
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
import org.springframework.web.multipart.MultipartHttpServletRequest;

@RequestMapping("/evenements")
@Controller
public class CriseEvenementController {


  @Autowired
  CriseEvenementRepository criseEvenementRepository;

  @Autowired
  private ZoneCompetenceService zoneCompetenceService;

  @Autowired
  private UtilisateurService utilisateurService;

  @RequestMapping(value = "/{idCrise}", method = RequestMethod.GET, headers = "Accept=application/xml")
  @PreAuthorize("hasRight('CRISE_C')")
  public ResponseEntity<String> getEvents(final @PathVariable(value = "idCrise") Long id,  @RequestParam(value = "filter", required = false) String filters) {

    final List<ItemFilter> itemFilterList = ItemFilter.decodeJson(filters);


    return new AbstractExtListSerializer<CriseEvenement>("Evènements retrieved.") {

      @Override
      protected JSONSerializer getJsonSerializer() {
        return new JSONSerializer().exclude("*.class").exclude("*.password").exclude("*.salt").transform(new GeometryTransformer(), Geometry.class)
            .transform(RemocraDateHourTransformer.getInstance(), Date.class).transform(new RemocraInstantTransformer(), Instant.class)
            .include("data.*").include("total").include("message");
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

  @RequestMapping(value = "/{idCrise}/{idEvenement}", method = RequestMethod.GET, headers = "Accept=application/xml")
  @PreAuthorize("hasRight('CRISE_C')")
  public ResponseEntity<String> getEventsById(final @PathVariable(value = "idEvenement") Long id) {

    return new AbstractExtListSerializer<CriseEvenement>("Evènements retrieved.") {

      @Override
      protected JSONSerializer getJsonSerializer() {
        return new JSONSerializer().exclude("*.class").exclude("*.password").exclude("*.salt").transform(new GeometryTransformer(), Geometry.class)
            .transform(RemocraDateHourTransformer.getInstance(), Date.class).transform(new RemocraInstantTransformer(), Instant.class)
            .include("data.*").include("total").include("message");
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


  @RequestMapping(value = "", method = RequestMethod.POST,  headers = "Content-Type=multipart/form-data")
  @PreAuthorize("hasRight('CRISE_C')")
  @Transactional
  public ResponseEntity<java.lang.String> createCriseEvent( MultipartHttpServletRequest request) {
    try{
      fr.sdis83.remocra.db.model.remocra.tables.pojos.CriseEvenement c = criseEvenementRepository.createEvent(request);
      return new SuccessErrorExtSerializer(true, "La crise " +c.getNom()+ " a été enregistrée").serialize();
    }catch(Exception e){
      return new SuccessErrorExtSerializer(false, "Une erreur est survenue lors de la création de l'évènement").serialize();
    }
  }

  @RequestMapping(value = "/message", method = RequestMethod.POST, headers = "Accept=application/json")
  @PreAuthorize("hasRight('CRISE_C')")
  @Transactional
  public ResponseEntity<java.lang.String> createMessage(final @RequestBody String json) {
    try{

      CriseSuivi cs = criseEvenementRepository.createMessage(json);
      return new SuccessErrorExtSerializer(true, "La crise " +cs.getObjet()+ " a été enregistrée").serialize();
    }catch(Exception e){
      return new SuccessErrorExtSerializer(false, "Une erreur est survenue lors de la création du message").serialize();
    }
  }

  @RequestMapping(value = "/layer", method = RequestMethod.GET, headers = "Accept=application/json")
  @PreAuthorize("hasRight('CRISE_R')")
  public ResponseEntity<java.lang.String> layer(final @RequestParam String point, @RequestParam String projection) {

    if (point == null || point.isEmpty()) {
      return null;
    } else {
      if (projection.contains("EPSG:")) {
        projection = projection.replace("EPSG:", "");
      }
      return FeatureUtil.getResponse(criseEvenementRepository.findCriseEventsByPoint(point, projection));
    }
  }

  @RequestMapping(value = "/message/{idMessage}", method = RequestMethod.GET, headers = "Accept=application/xml")
  @PreAuthorize("hasRight('CRISE_C')")
  public ResponseEntity<String> getMessageById(final @PathVariable(value = "idMessage") Long id) {

    return new AbstractExtListSerializer<CriseSuivi>("Message retrieved.") {

      @Override
      protected JSONSerializer getJsonSerializer() {
        return new JSONSerializer()
            .transform(RemocraDateHourTransformer.getInstance(), Date.class).transform(new RemocraInstantTransformer(), Instant.class)
            .include("data.*").include("total").include("message");
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
  @PreAuthorize("hasRight('CRISE_C')")
  public ResponseEntity<java.lang.String> getDocuments(@PathVariable("idEvent") final Long idEvent) {
    return new AbstractExtListSerializer<fr.sdis83.remocra.db.model.remocra.tables.pojos.Document>("Crise Document retrieved.") {

      @Override
      protected JSONSerializer getJsonSerializer() {
        return new JSONSerializer().exclude("*.class").transform(RemocraDateHourTransformer.getInstance(), Date.class).transform(new RemocraInstantTransformer(), Instant.class)
            .include("data.*").include("total").include("message");
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

  @RequestMapping(value = "/{id}", method = RequestMethod.POST, headers = "Content-Type=multipart/form-data")
  @PreAuthorize("hasRight('CRISE_C')")
  @Transactional
  public ResponseEntity<java.lang.String> updateCriseEvent(@PathVariable("id") Long id, MultipartHttpServletRequest request) {
    try{

      fr.sdis83.remocra.db.model.remocra.tables.pojos.CriseEvenement c = criseEvenementRepository.updateEvent(id, request);
      return new SuccessErrorExtSerializer(true, "L\'évenement " +c.getNom()+ " a été mis à jour").serialize();
    }catch(Exception e){
      return new SuccessErrorExtSerializer(false, "Une erreur est survenue lors de la mise à jour de l\'évènement'").serialize();
    }
  }

  @RequestMapping(value = "/origines/{idCrise}", method = RequestMethod.GET, headers = "Accept=application/json")
  @PreAuthorize("hasRight('CRISE_C')")
  public ResponseEntity<java.lang.String> getOrigines(final @PathVariable(value = "idCrise") Long idCrise,  final @RequestParam(value = "query", required = false) String query) {

    return new AbstractExtObjectSerializer<List<String>>("origines retrieved.") {

      @Override
      protected List<String> getRecord() {
        return criseEvenementRepository.getCriseOrigines(idCrise, query);
      }

    }.serialize();
  }

  @RequestMapping(value = "/tags/{idCrise}", method = RequestMethod.GET, headers = "Accept=application/json")
  @PreAuthorize("hasRight('CRISE_C')")
  public ResponseEntity<java.lang.String> getTags(final @PathVariable(value = "idCrise") Long idCrise) {

    return new AbstractExtObjectSerializer<List<String>>("tags retrieved.") {

      @Override
      protected List<String> getRecord() {
        return criseEvenementRepository.getCriseTags(idCrise);
      }

    }.serialize();
  }

  @RequestMapping(value = "/{idEvent}/updategeom", method = RequestMethod.POST, headers = "Accept=application/json")
  @PreAuthorize("hasRight('CRISE_C') or hasRight('CRISE_U')")
  @Transactional
  public ResponseEntity<java.lang.String> updateGeometrie(final @PathVariable(value = "idEvent") Long id, final @RequestBody String json) {
    try {
      Map<String, Object> wkt = new JSONDeserializer<HashMap<String, Object>>().use(Geometry.class, new GeometryFactory()).deserialize(json);
      Geometry geom = null;
      Integer srid = 2154;
      String[] coord = String.valueOf(wkt.get("geometrie")).split(";");
      srid = sridFromGeom(coord[0]);
      geom = GeometryUtil.toGeometry(coord[1],srid);
      Boolean result = zoneCompetenceService.check(coord[1], srid, utilisateurService.getCurrentZoneCompetenceId());
      if (!result) {
        return new SuccessErrorExtSerializer(result, "Modification de la géometrie de l'évènement non autorisée").serialize();
      }

      criseEvenementRepository.updateGeom(id, coord[1],srid);

    } catch (Exception e) {
      return new SuccessErrorExtSerializer(false, e.getMessage().toString()).serialize();
    }

    return new SuccessErrorExtSerializer(true, "Géométrie de l'évènement mise à jour.").serialize();
  }

}
