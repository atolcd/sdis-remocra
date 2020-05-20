package fr.sdis83.remocra.web;

import com.vividsolutions.jts.geom.Geometry;
import flexjson.JSONSerializer;
import fr.sdis83.remocra.domain.utils.RemocraDateHourTransformer;
import fr.sdis83.remocra.domain.utils.RemocraInstantTransformer;
import fr.sdis83.remocra.repository.EtudeRepository;
import fr.sdis83.remocra.web.message.ItemFilter;
import fr.sdis83.remocra.web.message.ItemSorting;
import fr.sdis83.remocra.web.model.Etude;
import fr.sdis83.remocra.web.serialize.ext.AbstractExtListSerializer;
import fr.sdis83.remocra.web.serialize.transformer.GeometryTransformer;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Date;
import java.util.List;

@RequestMapping("/etudes")
@Controller
public class EtudeController {

  @Autowired
  private EtudeRepository etudeRepository;

  @RequestMapping(value = "", method = RequestMethod.GET, headers = "Accept=application/xml")
  @PreAuthorize("hasRight('PLANIFIER_DECI')")
  public ResponseEntity<String> listJson(final @RequestParam(value = "page", required = false) Integer page,
                             final @RequestParam(value = "start", required = false) Integer start, final @RequestParam(value = "limit", required = false) Integer limit,
                             final @RequestParam(value = "query", required = false) String query, @RequestParam(value = "sort", required = false) String sorts,
                             @RequestParam(value = "filter", required = false) String filters) {
    final List<ItemFilter> itemFilterList = ItemFilter.decodeJson(filters);
    final List<ItemSorting> sortList = ItemSorting.decodeJson(sorts);


    return new AbstractExtListSerializer<Etude>("Etude retrieved.") {

      @Override
      protected JSONSerializer getJsonSerializer() {
        return new JSONSerializer().exclude("*.class").exclude("dateMiseaAJour").transform(new GeometryTransformer(), Geometry.class)
                .transform(RemocraDateHourTransformer.getInstance(), Date.class).transform(new RemocraInstantTransformer(), Instant.class)
            .include("data.*");
      }

      @Override
      protected List<Etude> getRecords() {
        return etudeRepository.getAll(itemFilterList, limit, start, sortList);
      }

      @Override
      protected Long countRecords() {
        return Long.valueOf(etudeRepository.count(itemFilterList));
      }

    }.serialize();
  }

}
