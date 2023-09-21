package fr.sdis83.remocra.web;

import com.vividsolutions.jts.geom.Geometry;
import flexjson.JSONDeserializer;
import flexjson.JSONSerializer;
import fr.sdis83.remocra.domain.remocra.Oldeb;
import fr.sdis83.remocra.exception.BusinessException;
import fr.sdis83.remocra.service.OldebService;
import fr.sdis83.remocra.service.UtilisateurService;
import fr.sdis83.remocra.service.ZoneCompetenceService;
import fr.sdis83.remocra.util.FeatureUtil;
import fr.sdis83.remocra.util.GeometryUtil;
import fr.sdis83.remocra.web.message.ItemFilter;
import fr.sdis83.remocra.web.message.ItemSorting;
import fr.sdis83.remocra.web.serialize.ext.AbstractExtListSerializer;
import fr.sdis83.remocra.web.serialize.ext.AbstractExtObjectSerializer;
import fr.sdis83.remocra.web.serialize.ext.SuccessErrorExtSerializer;
import java.util.ArrayList;
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

@RequestMapping("/oldeb")
@Controller
public class OldebController {

  @Autowired private OldebService oldebService;

  @Autowired private UtilisateurService utilisateurService;

  @Autowired private ZoneCompetenceService zoneCompetenceService;

  /** Retour de la liste des oldebs */
  @RequestMapping(value = "", method = RequestMethod.GET, headers = "Accept=application/xml")
  @PreAuthorize("hasRight('OLDEB_R')")
  public ResponseEntity<java.lang.String> listJson(
      final @RequestParam(value = "page", required = false) Integer page,
      final @RequestParam(value = "start", required = false) Integer start,
      final @RequestParam(value = "limit", required = false) Integer limit,
      final @RequestParam(value = "query", required = false) String query,
      @RequestParam(value = "sort", required = false) String sorts,
      @RequestParam(value = "filter", required = false) String filters) {

    final List<ItemFilter> itemFilterList = ItemFilter.decodeJson(filters);
    // Ajout du filtre sur le numéro de parcelle
    if (query != null) {
      itemFilterList.add(new ItemFilter("parcelle", query));
    }

    final List<ItemSorting> sortList = ItemSorting.decodeJson(sorts);
    // Ajout du tri par défaut par ordre croissant Section/Parcelle
    if (sortList.isEmpty()) {
      sortList.add(new ItemSorting("section", "ASC"));
      sortList.add(new ItemSorting("parcelle", "ASC"));
    }

    // Gestion par communes contenues dans la zone de compétence
    itemFilterList.add(new ItemFilter("zoneCompetenceIdCom", "true"));

    return new AbstractExtListSerializer<Oldeb>("Oldeb retrieved.") {

      @Override
      protected JSONSerializer additionnalIncludeExclude(JSONSerializer serializer) {
        // Retour des données de l'oldeb
        serializer
            .include("data.id")
            .include("data.section")
            .include("data.commune.id")
            .include("data.parcelle")
            .include("data.adresse")
            .include("data.dateDerniereVisite")
            .include("data.geometrie")
            .include("data.nomZoneUrbanisme")
            .include("data.debroussaillement")
            .include("data.avis");

        return serializer
            .include("total")
            .include("message")
            .exclude("data.commune.geometrie")
            .exclude("*");
      }

      @Override
      protected List<Oldeb> getRecords() {
        return oldebService.find(start, limit, sortList, itemFilterList);
      }

      @Override
      protected Long countRecords() {
        return oldebService.count(itemFilterList);
      }
    }.serialize();
  }

  /** Retour d'un old par id */
  @RequestMapping(value = "/{id}", method = RequestMethod.GET, headers = "Accept=application/json")
  @PreAuthorize("hasRight('OLDEB_R')")
  public ResponseEntity<java.lang.String> getOldeb(
      final @RequestParam(value = "id", required = true) Long id) {

    return new AbstractExtObjectSerializer<Oldeb>("Oldeb retrieved.") {

      @Override
      protected JSONSerializer additionnalIncludeExclude(JSONSerializer serializer) {
        // Retour des données de l'oldeb
        return serializer
            .include("data.id")
            .include("data.section")
            .include("data.parcelle")
            .include("data.adresse")
            .include("data.dateDerniereVisite")
            .include("data.nomZoneUrbanisme")
            .include("data.oldebVisites")
            .include("data.oldebVisites.oldebVisiteSuites")
            .include("data.oldebVisites.oldebVisiteDocuments")
            .include("data.oldebVisites.typeOldebAnomalies")
            .include("data.oldebVisites.nomAction")
            .include("data.oldebVisites.nomDebAcces")
            .include("data.oldebVisites.nomDebParcelle")
            .include("data.typeOldebCaracteristiques")
            .include("data.oldebProprietes.proprietaire")
            /* .include("data.oldebProprietes.oldeb") */ .include("data.oldebLocataires")
            .include("data.debroussaillement")
            .include("data.avis")
            .exclude("data.commune.geometrie");
      }

      @Override
      protected Oldeb getRecord() {
        return Oldeb.findOldeb(id);
      }
    }.serialize();
  }

  @RequestMapping(
      value = "/{id}",
      method = RequestMethod.POST,
      headers = "Content-Type=multipart/form-data")
  @PreAuthorize("hasRight('OLDEB_U')")
  public ResponseEntity<java.lang.String> updateOldeb(
      final @PathVariable Long id, MultipartHttpServletRequest request) {
    String json = request.getParameter("oldeb");
    String visiteDocumentsToDeleteJson = request.getParameter("visiteDocumentsToDelete");
    ArrayList<Integer> visiteDocumentsToDelete =
        visiteDocumentsToDeleteJson == null || visiteDocumentsToDeleteJson.isEmpty()
            ? new ArrayList<Integer>()
            : new JSONDeserializer<ArrayList<Integer>>().deserialize(visiteDocumentsToDeleteJson);
    Map<String, MultipartFile> files = request.getFileMap();
    try {
      final Oldeb attached =
          oldebService.update(id, json, visiteDocumentsToDelete.toArray(new Integer[] {}), files);
      if (attached != null) {
        return new AbstractExtObjectSerializer<Oldeb>(
            "Oldeb updated.", SuccessErrorExtSerializer.DEFAULT_CONTENT_TYPE) {
          @Override
          protected Oldeb getRecord() throws BusinessException {
            return attached;
          }
        }.serialize();
      }
    } catch (Exception e) {
      e.printStackTrace();
      return new SuccessErrorExtSerializer(false, e.getMessage()).serialize();
    }
    return new SuccessErrorExtSerializer(false, "Oldeb inexistant", HttpStatus.NOT_FOUND)
        .serialize();
  }

  @RequestMapping(
      value = "",
      method = RequestMethod.POST,
      headers = "Content-Type=multipart/form-data")
  @PreAuthorize("hasRight('OLDEB_C')")
  public ResponseEntity<java.lang.String> createOldeb(MultipartHttpServletRequest request) {
    String json = request.getParameter("oldeb");
    Map<String, MultipartFile> files = request.getFileMap();
    try {
      final Oldeb attached = oldebService.create(json, files);
      if (attached != null) {
        return new AbstractExtObjectSerializer<Oldeb>(
            "Oldeb created.", SuccessErrorExtSerializer.DEFAULT_CONTENT_TYPE) {
          @Override
          protected Oldeb getRecord() throws BusinessException {
            return attached;
          }
        }.serialize();
      }
    } catch (Exception e) {
      e.printStackTrace();
      return new SuccessErrorExtSerializer(false, e.getMessage()).serialize();
    }
    return new SuccessErrorExtSerializer(false, "Oldeb inexistant", HttpStatus.NOT_FOUND)
        .serialize();
  }

  @RequestMapping(
      value = "/{id}",
      method = RequestMethod.DELETE,
      headers = "Accept=application/json")
  @PreAuthorize("hasRight('OLDEB_D')")
  public ResponseEntity<java.lang.String> delete(@PathVariable("id") Long id) {
    try {
      oldebService.delete(id);
      return new SuccessErrorExtSerializer(true, "Obligation de débroussaillement supprimé")
          .serialize();
    } catch (Exception e) {
      return new SuccessErrorExtSerializer(false, e.getMessage()).serialize();
    }
  }

  @RequestMapping(value = "/layer", method = RequestMethod.GET, headers = "Accept=application/json")
  @PreAuthorize("hasRight('OLDEB_R')")
  public ResponseEntity<java.lang.String> layer(
      final @RequestParam String point, @RequestParam String projection) {

    if (point == null || point.isEmpty()) {
      return null;
    } else {
      if (projection.contains("EPSG:")) {
        projection = projection.replace("EPSG:", "");
      }
      return FeatureUtil.getResponse(oldebService.findOldebsByPoint(point, projection));
    }
  }

  /**
   * Service de mise à jour de la géométrie de l'oldeb dont l'id est passé en paramètre
   *
   * @param id, identifiant de l'obligation à mettre à jour
   * @param geometrie, nouvelle geometrie
   * @param srid, projection de la nouvelle geometrie
   * @return
   */
  @RequestMapping(
      value = "{id}/updategeom",
      method = RequestMethod.POST,
      headers = "Accept=application/json")
  @PreAuthorize("hasRight('OLDEB_C') or hasRight('OLDEB_U')")
  public ResponseEntity<java.lang.String> updateGeom(
      final @PathVariable(value = "id") Long id,
      final @RequestParam(value = "geometrie") String wkt,
      final @RequestParam(value = "srid") Integer srid) {

    try {
      Boolean result =
          zoneCompetenceService.check(wkt, srid, utilisateurService.getCurrentZoneCompetenceId());
      if (!result) {
        return new SuccessErrorExtSerializer(
                result,
                "Modification de la géometrie de l'obligation de débroussaillement non autorisée")
            .serialize();
      }
      // Désérialisation de nouvelle geometrie de l'oldeb
      Geometry geom = GeometryUtil.toGeometry(wkt, srid);

      oldebService.updateGeom(id, geom, srid);
    } catch (Exception e) {
      return new SuccessErrorExtSerializer(
              false,
              "Problème survenu lors de la modification de la géométrie de l'obligation de débroussaillement")
          .serialize();
    }

    return new SuccessErrorExtSerializer(
            true, "Géométrie de l'obligation de débroussaillement mise à jour.")
        .serialize();
  }

  @RequestMapping(
      value = "/checkdispo",
      method = RequestMethod.POST,
      headers = "Accept=application/json")
  @PreAuthorize("hasRight('OLDEB_R')")
  public ResponseEntity<java.lang.String> checkDispo(
      final @RequestParam(value = "id", required = false) Long id,
      final @RequestParam(value = "commune", required = false) Long codeCommune,
      final @RequestParam(value = "section", required = false) String section,
      final @RequestParam(value = "parcelle", required = false) String parcelle) {
    String message = oldebService.checkDispo(id, codeCommune, section, parcelle);
    return new SuccessErrorExtSerializer(message == null || message.isEmpty(), message).serialize();
  }
}
