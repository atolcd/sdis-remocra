package fr.sdis83.remocra.web;

import flexjson.JSONSerializer;
import fr.sdis83.remocra.domain.remocra.DebitSimultane;
import fr.sdis83.remocra.exception.BusinessException;
import fr.sdis83.remocra.service.DebitSimultaneService;
import fr.sdis83.remocra.usecase.parametre.ParametreDataProvider;
import fr.sdis83.remocra.util.FeatureUtil;
import fr.sdis83.remocra.web.message.ItemFilter;
import fr.sdis83.remocra.web.message.ItemSorting;
import fr.sdis83.remocra.web.serialize.ext.AbstractExtListSerializer;
import fr.sdis83.remocra.web.serialize.ext.AbstractExtObjectSerializer;
import fr.sdis83.remocra.web.serialize.ext.SuccessErrorExtSerializer;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

@RequestMapping("/debitsimultane")
@Controller
public class DebitSimultaneController {

  @Autowired private DebitSimultaneService debitSimultaneService;

  @Autowired private ParametreDataProvider parametreProvider;

  public JSONSerializer decorateSerializer(JSONSerializer serializer) {
    return serializer.exclude("data.actif").exclude("*.class");
  }

  @RequestMapping(value = "", method = RequestMethod.GET, headers = "Accept=application/json")
  public ResponseEntity<String> listJson(
      final @RequestParam(value = "page", required = false) Integer page,
      final @RequestParam(value = "start", required = false) Integer start,
      final @RequestParam(value = "limit", required = false) Integer limit,
      final @RequestParam(value = "query", required = false) String query,
      @RequestParam(value = "sort", required = false) String sorts,
      @RequestParam(value = "filter", required = false) String filters) {

    final List<ItemFilter> itemFilterList = ItemFilter.decodeJson(filters);
    final List<ItemSorting> itemSortList = ItemSorting.decodeJson(sorts);

    return new AbstractExtListSerializer<DebitSimultane>("DebitSimultane retrieved.") {

      @Override
      protected JSONSerializer additionnalIncludeExclude(JSONSerializer serializer) {
        serializer
            .include("data.mesures.hydrants.hydrant.id")
            .include("data.mesures.hydrants.hydrant.numero")
            .include("data.mesures.hydrants.hydrant.diametreCanalisation")
            .include("data.mesures.hydrants.hydrant.typeReseauAlimentation.*")
            .exclude("data.mesures.hydrants.*");
        return serializer.exclude("data.actif").exclude("*.class");
      }

      @Override
      protected List<DebitSimultane> getRecords() {
        return debitSimultaneService.find(start, limit, itemSortList, itemFilterList);
      }

      @Override
      protected Long countRecords() {
        return Long.valueOf(debitSimultaneService.count(itemFilterList));
      }
    }.serialize();
  }

  /**
   * Retourne la liste des débits simultanés situés autour d'un point
   *
   * @param lon longitude
   * @param lat latitude
   * @param srid L'identifiant du système de projection des coordonnées
   * @param distance La distance en mètre maximale entre les coordonnées et les débits simultanés
   *     retournés
   * @return
   */
  @RequestMapping(
      value = "/getdebitssimultanesfromlonlat",
      method = RequestMethod.GET,
      headers = "Accept=application/json")
  @PreAuthorize("hasRight('DEBITS_SIMULTANES_R')")
  public ResponseEntity<java.lang.String> layer(
      final @RequestParam Double lon,
      final @RequestParam Double lat,
      final @RequestParam Integer srid,
      final @RequestParam Integer distance) {
    return FeatureUtil.getResponse(
        this.debitSimultaneService.getDebitSimultaneFromLonLat(lon, lat, srid, distance));
  }

  @RequestMapping(
      value = "/{id}",
      method = RequestMethod.DELETE,
      headers = "Accept=application/json")
  @PreAuthorize("hasRight('DEBITS_SIMULTANES_C')")
  public ResponseEntity<java.lang.String> deleteDebitSimultane(@PathVariable("id") Long id) {
    try {
      debitSimultaneService.delete(id);
      return new SuccessErrorExtSerializer(true, "Débit simultané supprimé").serialize();
    } catch (Exception e) {
      return new SuccessErrorExtSerializer(false, e.getMessage()).serialize();
    }
  }

  @RequestMapping(value = "/{id}", method = RequestMethod.GET, headers = "Accept=application/json")
  @PreAuthorize("hasRight('DEBITS_SIMULTANES_R')")
  public ResponseEntity<java.lang.String> getDebitSimultane(
      final @PathVariable("id") Long id,
      @RequestParam(value = "sort", required = false) String sorts) {
    final List<ItemSorting> itemSortList = ItemSorting.decodeJson(sorts);
    return new AbstractExtObjectSerializer<DebitSimultane>(
        "fr.sdis83.remocra.domain.remocra.DebitSimultane retrieved.") {
      @Override
      protected JSONSerializer additionnalIncludeExclude(JSONSerializer serializer) {
        serializer
            .include("data.mesures.hydrants.hydrant.id")
            .include("data.mesures.hydrants.hydrant.numero")
            .include("data.mesures.hydrants.hydrant.diametreCanalisation")
            .include("data.mesures.hydrants.hydrant.typeReseauAlimentation.*")
            .exclude("data.mesures.hydrants.*");

        return serializer;
      }

      @Override
      protected DebitSimultane getRecord() {
        return DebitSimultane.findDebitSimultane(id);
      }
    }.serialize();
  }

  @RequestMapping(
      value = "/checkds",
      method = RequestMethod.GET,
      headers = "Accept=application/json")
  @PreAuthorize("hasRight('DEBITS_SIMULTANES_R')")
  public ResponseEntity<java.lang.String> checkDebitSimultane(
      final @RequestParam("ids") String listIds) {

    try {
      // On vérifie si les hydrants appartiennent déjà à un debit simultane et on retoourne un
      // message avec les numeros concernés
      String s = debitSimultaneService.checkDs(listIds);
      if (s != null) {
        return new SuccessErrorExtSerializer(true, s).serialize();
      } else {
        return new SuccessErrorExtSerializer(true, "hasNotDs").serialize();
      }

    } catch (Exception e) {
      return new SuccessErrorExtSerializer(false, e.getMessage()).serialize();
    }
  }

  @RequestMapping(
      value = "/{id}",
      method = RequestMethod.POST,
      headers = "Content-Type=multipart/form-data")
  @PreAuthorize("hasRight('DEBITS_SIMULTANES_C')")
  public ResponseEntity<java.lang.String> updateDebitSimultane(
      final @PathVariable Long id, MultipartHttpServletRequest request) {
    String json = request.getParameter("debitSimultane");
    Map<String, MultipartFile> files = request.getFileMap();
    try {
      final DebitSimultane attached =
          debitSimultaneService.update(id, json, files, parametreProvider.get().getSridInt());
      if (attached != null) {
        return new AbstractExtObjectSerializer<DebitSimultane>(
            "Debit simultane updated.", SuccessErrorExtSerializer.DEFAULT_CONTENT_TYPE) {

          @Override
          protected DebitSimultane getRecord() throws BusinessException {
            return attached;
          }
        }.serialize();
      }
    } catch (Exception e) {
      e.printStackTrace();
      return new SuccessErrorExtSerializer(false, e.getMessage()).serialize();
    }
    return new SuccessErrorExtSerializer(false, "Debit simultané inexistant", HttpStatus.NOT_FOUND)
        .serialize();
  }

  @RequestMapping(
      value = "",
      method = RequestMethod.POST,
      headers = "Content-Type=multipart/form-data")
  @PreAuthorize("hasRight('DEBITS_SIMULTANES_C')")
  public ResponseEntity<java.lang.String> createDebitSimultane(
      MultipartHttpServletRequest request) {
    String json = request.getParameter("debitSimultane");
    try {
      final DebitSimultane attached = debitSimultaneService.create(json, null);
      return new AbstractExtObjectSerializer<DebitSimultane>("DebitSimultane created") {
        @Override
        protected DebitSimultane getRecord() throws BusinessException {
          return attached;
        }
      }.serialize();
    } catch (Exception e) {
      e.printStackTrace();
      return new SuccessErrorExtSerializer(false, e.getMessage()).serialize();
    }
  }

  @RequestMapping(
      value = "updategeometry/{id}",
      method = RequestMethod.GET,
      headers = "Accept=application/json")
  @PreAuthorize("hasRight('DEBITS_SIMULTANES_C')")
  public ResponseEntity<java.lang.String> updateGeometry(@PathVariable("id") Long id) {
    try {
      debitSimultaneService.updateGeometry(id);
      return new SuccessErrorExtSerializer(true, "Géométrie du débit simultané mise à jour")
          .serialize();
    } catch (Exception e) {
      return new SuccessErrorExtSerializer(false, e.getMessage()).serialize();
    }
  }
}
