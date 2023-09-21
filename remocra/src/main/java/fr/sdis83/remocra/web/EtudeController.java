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
import fr.sdis83.remocra.web.serialize.ext.AbstractExtObjectSerializer;
import fr.sdis83.remocra.web.serialize.ext.SuccessErrorExtSerializer;
import fr.sdis83.remocra.web.serialize.transformer.GeometryTransformer;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

@RequestMapping("/etudes")
@Controller
public class EtudeController {

  @Autowired private EtudeRepository etudeRepository;

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

    return new AbstractExtListSerializer<Etude>("Etude retrieved.") {

      @Override
      protected JSONSerializer getJsonSerializer() {
        return new JSONSerializer()
            .exclude("*.class")
            .transform(new GeometryTransformer(), Geometry.class)
            .transform(RemocraDateHourTransformer.getInstance(), Date.class)
            .transform(new RemocraInstantTransformer(), Instant.class)
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

  /**
   * Vérifie si une étude existe déjà avec le numéro passé en paramètre
   *
   * @return TRUE si le numéro n'est pas utilisé, FALSE sinon
   */
  @RequestMapping(
      value = "/checknumero",
      method = RequestMethod.GET,
      headers = "Accept=application/json")
  @PreAuthorize("hasRight('PLANIFIER_DECI')")
  public ResponseEntity<java.lang.String> checknumero(
      final @RequestParam(value = "numero", required = false) String numero) {
    return new AbstractExtObjectSerializer<Boolean>("Vérification du numéro effectuée") {
      @Override
      protected Boolean getRecord() {
        return etudeRepository.checkNumero(numero);
      }
    }.serialize();
  }

  @RequestMapping(
      value = "",
      method = RequestMethod.POST,
      headers = "Content-Type=multipart/form-data")
  @PreAuthorize("hasRight('PLANIFIER_DECI')")
  @Transactional
  public ResponseEntity<java.lang.String> addEtude(MultipartHttpServletRequest request) {
    try {
      String json = request.getParameter("etude");
      Map<String, MultipartFile> files = request.getFileMap();

      long idEtude = etudeRepository.addEtude(json);
      etudeRepository.addDocuments(files, idEtude);
      return new SuccessErrorExtSerializer(true, "L'étude a bien été ajoutée.").serialize();
    } catch (Exception e) {
      e.printStackTrace();
      return new SuccessErrorExtSerializer(
              false, "Une erreur est survenue lors de la création de l'étude.")
          .serialize();
    }
  }

  @RequestMapping(
      value = "/editEtude",
      method = RequestMethod.POST,
      headers = "Content-Type=multipart/form-data")
  @PreAuthorize("hasRight('PLANIFIER_DECI')")
  @Transactional
  public ResponseEntity<java.lang.String> editEtude(MultipartHttpServletRequest request) {
    try {
      String json = request.getParameter("etude");
      Map<String, MultipartFile> files = request.getFileMap();
      String removedDocuments = request.getParameter("removedDocuments");

      long idEtude = etudeRepository.editEtude(json);
      etudeRepository.addDocuments(files, idEtude);
      etudeRepository.removeDocuments(removedDocuments, idEtude);
      return new SuccessErrorExtSerializer(true, "L'étude a bien été modifiée.").serialize();
    } catch (Exception e) {
      e.printStackTrace();
      return new SuccessErrorExtSerializer(
              false, "Une erreur est survenue lors de la création de l'étude.")
          .serialize();
    }
  }

  @RequestMapping(
      value = "/clore/{id}",
      method = RequestMethod.POST,
      headers = "Accept=application/json")
  @PreAuthorize("hasRight('PLANIFIER_DECI')")
  @Transactional
  public ResponseEntity<java.lang.String> cloreEtude(final @PathVariable(value = "id") Long id) {
    try {
      etudeRepository.cloreEtude(id);
      return new SuccessErrorExtSerializer(true, "L'étude a bien été close.").serialize();
    } catch (Exception e) {
      e.printStackTrace();
      return new SuccessErrorExtSerializer(
              false, "Une erreur est survenue lors de la création de l'étude.")
          .serialize();
    }
  }
}
