package fr.sdis83.remocra.web;

import java.util.List;

import flexjson.JSONSerializer;
import fr.sdis83.remocra.db.model.remocra.tables.pojos.Intervention;
import fr.sdis83.remocra.repository.InterventionRepository;
import fr.sdis83.remocra.web.message.ItemFilter;
import fr.sdis83.remocra.web.serialize.ext.AbstractExtListSerializer;
import fr.sdis83.remocra.web.serialize.ext.SuccessErrorExtSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@RequestMapping("/intervention")
@Controller
public class InterventionController {


    @Autowired
    private InterventionRepository interventionRepository;

  @RequestMapping(value = "", method = RequestMethod.GET, headers = "Accept=application/xml")
  @PreAuthorize("hasRight('CRISE_R')")
  public ResponseEntity<String> listJson(final @RequestParam(value = "page", required = false) Integer page,
                             final @RequestParam(value = "start", required = false) Integer start, final @RequestParam(value = "limit", required = false) Integer limit,
                             final @RequestParam(value = "query", required = false) String query, @RequestParam(value = "sort", required = false) String sorts,
                             @RequestParam(value = "filter", required = false) String filters) {
    final List<ItemFilter> itemFilterList = ItemFilter.decodeJson(filters);

    return new AbstractExtListSerializer<Intervention>("Intervention retrieved.") {

      @Override
      protected JSONSerializer additionnalIncludeExclude(JSONSerializer serializer) {
        serializer.include("data.*");

        return serializer.include("total").include("message");
      }

      @Override
      protected List<Intervention> getRecords() {
        return interventionRepository.getAll();
      }

      @Override
      protected Long countRecords() {
        return
            Long.valueOf(interventionRepository.count());
      }

    }.serialize();
  }


  @RequestMapping(value = "/{id}", method = RequestMethod.GET, headers = "Accept=application/json")
  @PreAuthorize("hasRight('CRISE_R')")
  public ResponseEntity<java.lang.String> getInterventions(final @PathVariable("id") Long idCrise) {

    return new AbstractExtListSerializer<Intervention>("Intervention By crise retrieved.") {

      @Override
      protected JSONSerializer additionnalIncludeExclude(JSONSerializer serializer) {
        return serializer.exclude("*.class");
      }

      @Override
      protected List<Intervention> getRecords() {

        return interventionRepository.getByCrise(idCrise);
      }

    }.serialize();
  }

  @RequestMapping(value = "/indicateur", method = RequestMethod.GET, headers = "Accept=application/xml")
  public ResponseEntity<java.lang.String> indicateurXml(final @RequestParam(value="code") String code) {
    HttpHeaders responseHeaders = new HttpHeaders();
    responseHeaders.add("Content-Type", "application/json;charset=utf-8");
    String xmlDoc = interventionRepository.getXmlIndicateur(code);
    if(xmlDoc != null) {
      return new ResponseEntity<String>(xmlDoc.toString(), responseHeaders, HttpStatus.OK);
    }
    else {
      return new SuccessErrorExtSerializer(false, "Une erreur est survenue lors de l'exécution de la requête").serialize();
    }

  }

}
