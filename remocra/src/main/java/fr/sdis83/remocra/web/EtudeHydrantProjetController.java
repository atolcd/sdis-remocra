package fr.sdis83.remocra.web;

import com.vividsolutions.jts.geom.Geometry;
import flexjson.JSONSerializer;
import fr.sdis83.remocra.repository.EtudeHydrantProjetRepository;
import fr.sdis83.remocra.usecase.parametre.ParametreDataProvider;
import fr.sdis83.remocra.web.message.ItemFilter;
import fr.sdis83.remocra.web.message.ItemSorting;
import fr.sdis83.remocra.web.model.EtudeHydrantProjet;
import fr.sdis83.remocra.web.serialize.ext.AbstractExtListSerializer;
import fr.sdis83.remocra.web.serialize.ext.SuccessErrorExtSerializer;
import fr.sdis83.remocra.web.serialize.transformer.GeometryTransformer;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartHttpServletRequest;

@RequestMapping("/etudehydrantprojet")
@Controller
public class EtudeHydrantProjetController {

  @Autowired private EtudeHydrantProjetRepository etudeHydrantProjetRepository;

  @Autowired private ParametreDataProvider parametreProvider;

  @RequestMapping(value = "", method = RequestMethod.GET, headers = "Accept=application/xml")
  @PreAuthorize("hasRight('PLANIFIER_DECI')")
  public ResponseEntity<String> listJson(
      final @RequestParam(value = "page", required = false) Integer page,
      final @RequestParam(value = "start", required = false) Integer start,
      final @RequestParam(value = "limit", required = false) Integer limit,
      final @RequestParam(value = "query", required = false) String query,
      @RequestParam(value = "sort", required = false) String sorts,
      @RequestParam(value = "filter", required = false) String filters) {
    final List<ItemFilter> itemFilterList = ItemFilter.decodeJson(filters);
    final List<ItemSorting> sortList = ItemSorting.decodeJson(sorts);

    return new AbstractExtListSerializer<EtudeHydrantProjet>("Etude retrieved.") {

      @Override
      protected JSONSerializer getJsonSerializer() {
        return new JSONSerializer()
            .exclude("*.class")
            .transform(new GeometryTransformer(), Geometry.class)
            .include("data.*");
      }

      @Override
      protected List<EtudeHydrantProjet> getRecords() {
        return etudeHydrantProjetRepository.getAll(itemFilterList, limit, start, sortList);
      }

      @Override
      protected Long countRecords() {
        return Long.valueOf(etudeHydrantProjetRepository.count(itemFilterList));
      }
    }.serialize();
  }

  @RequestMapping(
      value = "",
      method = RequestMethod.POST,
      headers = "Content-Type=multipart/form-data")
  @PreAuthorize("hasRight('PLANIFIER_DECI')")
  @Transactional
  public ResponseEntity<String> addPeiProjet(MultipartHttpServletRequest request) {
    try {
      String json = request.getParameter("peiProjet");

      etudeHydrantProjetRepository.addPeiProjet(json);
      return new SuccessErrorExtSerializer(true, "Le pei projet a bien été ajouté.").serialize();
    } catch (Exception e) {
      e.printStackTrace();
      return new SuccessErrorExtSerializer(
              false, "Une erreur est survenue lors de la création du PEi projet")
          .serialize();
    }
  }

  @RequestMapping(
      value = "/update",
      method = RequestMethod.POST,
      headers = "Content-Type=multipart/form-data")
  @PreAuthorize("hasRight('PLANIFIER_DECI')")
  @Transactional
  public ResponseEntity<String> updatePeiProjet(MultipartHttpServletRequest request) {
    try {
      String json = request.getParameter("peiProjet");

      etudeHydrantProjetRepository.updatePeiProjet(json);
      return new SuccessErrorExtSerializer(true, "Le pei projet a bien été mis à jour.")
          .serialize();
    } catch (Exception e) {
      e.printStackTrace();
      return new SuccessErrorExtSerializer(
              false, "Une erreur est survenue lors de la création du PEI projet")
          .serialize();
    }
  }

  @PreAuthorize("hasRight('PLANIFIER_DECI')")
  @RequestMapping(value = "", method = RequestMethod.DELETE, headers = "Accept=application/json")
  public ResponseEntity<java.lang.String> delete(final @RequestBody String json) {
    try {
      etudeHydrantProjetRepository.deletePeiProjet(json);
      return new SuccessErrorExtSerializer(true, "PEI projet supprimé").serialize();
    } catch (Exception e) {
      return new SuccessErrorExtSerializer(false, e.getMessage()).serialize();
    }
  }

  @PreAuthorize("hasRight('PLANIFIER_DECI')")
  @RequestMapping(
      value = "/updategeometrie",
      method = RequestMethod.POST,
      headers = "Accept=application/json")
  @Transactional
  public ResponseEntity<String> updateGeometrie(MultipartHttpServletRequest request) {
    try {
      String json = request.getParameter("peiProjet");

      etudeHydrantProjetRepository.updateGeometrie(json, parametreProvider.get().getSridInt());
      return new SuccessErrorExtSerializer(true, "Le pei projet a bien été déplacé.").serialize();
    } catch (Exception e) {
      e.printStackTrace();
      return new SuccessErrorExtSerializer(
              false, "Une erreur est survenue lors du déplacement du PEI projet")
          .serialize();
    }
  }
}
