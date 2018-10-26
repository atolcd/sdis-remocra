package fr.sdis83.remocra.web;

import java.util.List;

import flexjson.JSONSerializer;
import fr.sdis83.remocra.db.model.remocra.tables.pojos.TypeCriseNatureEvenement;
import fr.sdis83.remocra.db.model.remocra.tables.pojos.TypeCriseStatut;
import fr.sdis83.remocra.repository.TypeCriseNatureEvenementRepository;
import fr.sdis83.remocra.repository.TypeCriseStatutRepository;
import fr.sdis83.remocra.web.message.ItemFilter;
import fr.sdis83.remocra.web.serialize.ext.AbstractExtListSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@RequestMapping("/typecrisenatureevenement")
@Controller
public class TypeCriseNatureEvenementController {

  @Autowired
  TypeCriseNatureEvenementRepository typeCriseNatureEvenementRepository;


  @RequestMapping(value = "", method = RequestMethod.GET, headers = "Accept=application/xml")
  @PreAuthorize("hasRight('CRISE_C')")
  public ResponseEntity<String> listJson(final @RequestParam(value = "page", required = false) Integer page,
                                                   final @RequestParam(value = "start", required = false) Integer start, final @RequestParam(value = "limit", required = false) Integer limit,
                                                   final @RequestParam(value = "query", required = false) String query, @RequestParam(value = "sort", required = false) String sorts,
                                                   @RequestParam(value = "filter", required = false) String filters) {


    final List<ItemFilter> itemFilterList = ItemFilter.decodeJson(filters);
    return new AbstractExtListSerializer<TypeCriseNatureEvenement>("TypeCriseNatureEvenement retrieved.") {

      @Override
      protected JSONSerializer additionnalIncludeExclude(JSONSerializer serializer) {
        serializer.include("data.*");

        return serializer.include("total").include("message");
      }

      @Override
      protected List<TypeCriseNatureEvenement> getRecords() {
        return typeCriseNatureEvenementRepository.getAll(itemFilterList);
      }

      @Override
      protected Long countRecords() {
        return
           Long.valueOf(typeCriseNatureEvenementRepository.count());
      }

    }.serialize();
  }

  @RequestMapping(value = "/{idCrise}", method = RequestMethod.GET, headers = "Accept=application/xml")
  @PreAuthorize("hasRight('CRISE_C')")
  public ResponseEntity<String> getNatureByCrise(final @PathVariable(value = "idCrise") Long id){


    return new AbstractExtListSerializer<TypeCriseNatureEvenement>("TypeCriseNatureEvenement retrieved.") {

      @Override
      protected JSONSerializer additionnalIncludeExclude(JSONSerializer serializer) {
        serializer.include("data.*");

        return serializer.include("total").include("message");
      }

      @Override
      protected List<TypeCriseNatureEvenement> getRecords() {
        return typeCriseNatureEvenementRepository.getNatureByCrise(id);
      }

    }.serialize();
  }
}
