package fr.sdis83.remocra.web;

import flexjson.JSONSerializer;
import fr.sdis83.remocra.domain.remocra.CadastreParcelle;
import fr.sdis83.remocra.service.CadastreParcelleService;
import fr.sdis83.remocra.web.message.ItemFilter;
import fr.sdis83.remocra.web.message.ItemSorting;
import fr.sdis83.remocra.web.serialize.ext.AbstractExtListSerializer;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/** Classe de retour d'une parcelle suivant différentes provenances (OLD, CADASTRE..) */
@RequestMapping("/cadastreparcelle")
@Controller
public class CadastreParcelleController {

  @Autowired private CadastreParcelleService cadastreParcelleService;

  /** Retour d'une liste de parcelles suivant un filtre et un type de recherche */
  @RequestMapping(value = "", method = RequestMethod.GET, headers = "Accept=application/xml")
  public ResponseEntity<java.lang.String> listJson(
      final @RequestParam(value = "page", required = false) Integer page,
      final @RequestParam(value = "start", required = false) Integer start,
      final @RequestParam(value = "limit", required = false) Integer limit,
      final @RequestParam(value = "query", required = false) String query,
      @RequestParam(value = "sort", required = false) String sorts,
      @RequestParam(value = "filter", required = false) String filters) {

    final List<ItemFilter> itemFilterList = ItemFilter.decodeJson(filters);

    // Ajout du tri par numéro de parcelle si non précisé
    final List<ItemSorting> sortList = ItemSorting.decodeJson(sorts);
    if (sortList.isEmpty()) {
      sortList.add(new ItemSorting("numero", "ASC"));
    }

    // Ajout du filtre sur le numéro de parcelle
    if (query != null) {
      itemFilterList.add(new ItemFilter("numero", query));
    }

    return new AbstractExtListSerializer<CadastreParcelle>("CadastreParcelle retrieved.") {

      @Override
      protected JSONSerializer additionnalIncludeExclude(JSONSerializer serializer) {
        // Vérification s'il faut retourner la géometrie
        serializer.include("data.id").include("data.numero").include("data.geometrie");

        // Mise à jour du serializer s'il y a besoin de retourner la
        // géometrie
        updateSerialiezWithGeometrie(itemFilterList, serializer);

        return serializer.exclude("*");
      }

      @Override
      protected List<CadastreParcelle> getRecords() {
        return cadastreParcelleService.find(start, limit, sortList, itemFilterList);
      }
    }.serialize();
  }

  /**
   * Mise à jour du sérializer s'il y a la géometrie de la parcelle à retourner
   *
   * @param itemFilterList
   * @param serializer
   */
  private void updateSerialiezWithGeometrie(
      final List<ItemFilter> itemFilterList, JSONSerializer serializer) {
    // Vérification s'il y a des géometries ou non
    boolean returnGeom = false;
    for (ItemFilter itemFilter : itemFilterList) {
      if ("geometrie".equals(itemFilter.getFieldName())) {
        returnGeom = "true".equals(itemFilter.getValue());
      }
    }
    // S'il y a le paramètre "geometrie" à "true" retour des
    // données de l'old avec la géométrie
    if (returnGeom) {
      serializer.include("data.geometrie");
    }
  }
}
