package fr.sdis83.remocra.web;

import java.util.List;

import flexjson.JSONSerializer;
import fr.sdis83.remocra.repository.OgcServiceRepository;
import fr.sdis83.remocra.web.message.ItemFilter;
import fr.sdis83.remocra.web.model.OgcService;
import fr.sdis83.remocra.web.serialize.ext.AbstractExtListSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@RequestMapping("/ogcservice")
@Controller
public class OgcServiceController {


    @Autowired
    private OgcServiceRepository ogcServiceRepository;

  @RequestMapping(value = "", method = RequestMethod.GET, headers = "Accept=application/xml")
  @PreAuthorize("hasRight('CRISE_C')")
  public ResponseEntity<String> listJson(final @RequestParam(value = "page", required = false) Integer page,
                             final @RequestParam(value = "start", required = false) Integer start, final @RequestParam(value = "limit", required = false) Integer limit,
                             final @RequestParam(value = "query", required = false) String query, @RequestParam(value = "sort", required = false) String sorts,
                             @RequestParam(value = "filter", required = false) String filters) {
    final List<ItemFilter> itemFilterList = ItemFilter.decodeJson(filters);

    return new AbstractExtListSerializer<OgcService>("Crise retrieved.") {

      @Override
      protected JSONSerializer additionnalIncludeExclude(JSONSerializer serializer) {
        serializer.include("data.*");

        return serializer.include("total").include("message");
      }

      @Override
      protected List<OgcService> getRecords() {
        return ogcServiceRepository.getAll(itemFilterList);
      }

      @Override
      protected Long countRecords() {
        return
            Long.valueOf(ogcServiceRepository.count());
      }

    }.serialize();
  }



}
