package fr.sdis83.remocra.web;

import flexjson.JSONSerializer;
import fr.sdis83.remocra.db.model.remocra.tables.pojos.RepertoireLieu;
import fr.sdis83.remocra.repository.RepertoireLieuRepository;
import fr.sdis83.remocra.web.message.ItemFilter;
import fr.sdis83.remocra.web.model.RepertoireLieuData;
import fr.sdis83.remocra.web.serialize.ext.AbstractExtListSerializer;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@RequestMapping("/repertoirelieu")
@Controller
public class RepertoireLieuController {

  @Autowired private RepertoireLieuRepository repertoireLieuRepository;

  @RequestMapping(value = "", method = RequestMethod.GET, headers = "Accept=application/xml")
  @PreAuthorize("hasRight('ZOOM_LIEU_R')")
  public ResponseEntity<String> listJson(
      final @RequestParam(value = "page", required = false) Integer page,
      final @RequestParam(value = "start", required = false) Integer start,
      final @RequestParam(value = "limit", required = false) Integer limit,
      final @RequestParam(value = "query", required = false) String query,
      @RequestParam(value = "sort", required = false) String sorts,
      @RequestParam(value = "filter", required = false) String filters) {
    final List<ItemFilter> itemFilterList = ItemFilter.decodeJson(filters);

    return new AbstractExtListSerializer<RepertoireLieuData>("Repertoire Lieu retrieved.") {

      @Override
      protected JSONSerializer additionnalIncludeExclude(JSONSerializer serializer) {
        return serializer.include("total").include("message");
      }

      @Override
      protected List<RepertoireLieuData> getRecords() {
        return repertoireLieuRepository.getAll(query);
      }
    }.serialize();
  }

  @RequestMapping(
      value = "/{idCrise}",
      method = RequestMethod.GET,
      headers = "Accept=application/xml")
  @PreAuthorize("hasRight('ZOOM_LIEU_R')")
  public ResponseEntity<String> listJson(
      final @PathVariable(value = "idCrise") Long id,
      final @RequestParam(value = "query", required = false) String query) {

    return new AbstractExtListSerializer<RepertoireLieuData>("Repertoire Lieu retrieved.") {

      @Override
      protected JSONSerializer additionnalIncludeExclude(JSONSerializer serializer) {
        serializer.include("data.*");

        return serializer.include("total").include("message");
      }

      @Override
      protected List<RepertoireLieuData> getRecords() {
        return repertoireLieuRepository.getAllById(id, query);
      }
    }.serialize();
  }

  @RequestMapping(
      value = "/records",
      method = RequestMethod.GET,
      headers = "Accept=application/xml")
  @PreAuthorize("hasRight('CRISE_C')")
  public ResponseEntity<String> listRecords() {
    return new AbstractExtListSerializer<RepertoireLieu>("Repertoire Lieu retrieved.") {

      @Override
      protected JSONSerializer additionnalIncludeExclude(JSONSerializer serializer) {
        serializer.include("data.*");

        return serializer.include("total").include("message");
      }

      @Override
      protected List<RepertoireLieu> getRecords() {
        return repertoireLieuRepository.getAll();
      }

      @Override
      protected Long countRecords() {
        return Long.valueOf(repertoireLieuRepository.count());
      }
    }.serialize();
  }
}
