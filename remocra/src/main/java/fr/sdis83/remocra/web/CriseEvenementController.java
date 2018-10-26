package fr.sdis83.remocra.web;

import java.text.ParseException;
import java.util.Date;
import java.util.List;

import com.vividsolutions.jts.geom.Geometry;
import flexjson.JSONSerializer;
import fr.sdis83.remocra.db.model.remocra.tables.pojos.CriseSuivi;
import fr.sdis83.remocra.domain.utils.RemocraDateHourTransformer;
import fr.sdis83.remocra.domain.utils.RemocraInstantTransformer;
import fr.sdis83.remocra.repository.CriseEvenementRepository;
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

@RequestMapping("/evenements")
@Controller
public class CriseEvenementController {


  @Autowired
  CriseEvenementRepository criseEvenementRepository;

  @RequestMapping(value = "/{idCrise}", method = RequestMethod.GET, headers = "Accept=application/xml")
  @PreAuthorize("hasRight('CRISE_C')")
  public ResponseEntity<String> getEvents(final @PathVariable(value = "idCrise") Long id,  @RequestParam(value = "filter", required = false) String filters) {

    final List<ItemFilter> itemFilterList = ItemFilter.decodeJson(filters);


    return new AbstractExtListSerializer<CriseEvenement>("Evenements retrieved.") {

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

    return new AbstractExtListSerializer<CriseEvenement>("Evenements retrieved.") {

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


  @RequestMapping(value = "", method = RequestMethod.POST, headers = "Accept=application/json")
  @PreAuthorize("hasRight('CRISE_C')")
  @Transactional
  public ResponseEntity<java.lang.String> createCriseEvent(final @RequestBody String json) {
    try{

      fr.sdis83.remocra.db.model.remocra.tables.pojos.CriseEvenement c = criseEvenementRepository.createEvent(json);
      return new SuccessErrorExtSerializer(true, "La crise " +c.getNom()+ " a été enregistrée").serialize();
    }catch(Exception e){
      return new SuccessErrorExtSerializer(false, "Une erreur est survenue lors de la création de la crise").serialize();
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
      return new SuccessErrorExtSerializer(false, "Une erreur est survenue lors de la création de la crise").serialize();
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

  @RequestMapping(value = "/{id}", method = RequestMethod.PUT, headers = "Accept=application/json")
  @PreAuthorize("hasRight('CRISE_C')")
  @Transactional
  public ResponseEntity<java.lang.String> updateCriseEvent(@PathVariable("id") Long id, @RequestBody String json) {
    try{

      fr.sdis83.remocra.db.model.remocra.tables.pojos.CriseEvenement c = criseEvenementRepository.updateEvent(id, json);
      return new SuccessErrorExtSerializer(true, "L\'évenement " +c.getNom()+ " a été enregistrée").serialize();
    }catch(Exception e){
      return new SuccessErrorExtSerializer(false, "Une erreur est survenue lors de la création de l\'évenement'").serialize();
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

}
