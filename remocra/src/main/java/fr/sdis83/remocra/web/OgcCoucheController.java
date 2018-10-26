package fr.sdis83.remocra.web;

import java.util.List;

import flexjson.JSONSerializer;
import fr.sdis83.remocra.db.model.remocra.tables.pojos.OgcCouche;
import fr.sdis83.remocra.repository.OgcCoucheRepository;
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

@RequestMapping("/ogccouche")
@Controller
public class OgcCoucheController {


    @Autowired
    private OgcCoucheRepository ogcCoucheRepository;

  @RequestMapping(value = "", method = RequestMethod.GET, headers = "Accept=application/xml")
  @PreAuthorize("hasRight('CRISE_R')")
  public ResponseEntity<String> listJson(final @RequestParam(value = "page", required = false) Integer page,
                             final @RequestParam(value = "start", required = false) Integer start, final @RequestParam(value = "limit", required = false) Integer limit,
                             final @RequestParam(value = "query", required = false) String query, @RequestParam(value = "sort", required = false) String sorts,
                             @RequestParam(value = "filter", required = false) String filters) {
    final List<ItemFilter> itemFilterList = ItemFilter.decodeJson(filters);

    return new AbstractExtListSerializer<OgcCouche>("Crise retrieved.") {

      @Override
      protected JSONSerializer additionnalIncludeExclude(JSONSerializer serializer) {
        serializer.include("data.*");

        return serializer.include("total").include("message");
      }

      @Override
      protected List<OgcCouche> getRecords() {
        return ogcCoucheRepository.getAll(itemFilterList);
      }

      @Override
      protected Long countRecords() {
        return
            Long.valueOf(ogcCoucheRepository.count());
      }

    }.serialize();
  }

  @RequestMapping(value = "/{id}", method = RequestMethod.GET, headers = "Accept=application/json")
  @PreAuthorize("hasRight('CRISE_R')")
  public ResponseEntity<java.lang.String> getLayers(final @PathVariable("id") Long idService) {

    return new AbstractExtListSerializer<OgcCouche>("Ogc Layer retrieved.") {

      @Override
      protected JSONSerializer additionnalIncludeExclude(JSONSerializer serializer) {
        return serializer.exclude("*.class");
      }

      @Override
      protected List<OgcCouche> getRecords() {

        return ogcCoucheRepository.getByService(idService);
      }

    }.serialize();
  }

}
