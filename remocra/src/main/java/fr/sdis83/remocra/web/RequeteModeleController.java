package fr.sdis83.remocra.web;

import flexjson.JSONException;
import flexjson.JSONSerializer;
import fr.sdis83.remocra.db.model.remocra.tables.pojos.RequeteModele;
import fr.sdis83.remocra.db.model.remocra.tables.pojos.RequeteModeleParametre;
import fr.sdis83.remocra.domain.remocra.RemocraVueCombo;
import fr.sdis83.remocra.exception.BusinessException;
import fr.sdis83.remocra.repository.RequeteModeleParametereRepository;
import fr.sdis83.remocra.repository.RequeteModeleRepository;
import fr.sdis83.remocra.web.message.ItemFilter;
import fr.sdis83.remocra.web.serialize.ext.AbstractExtListSerializer;
import fr.sdis83.remocra.web.serialize.ext.SuccessErrorExtSerializer;
import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.jooq.tools.json.JSONArray;
import org.jooq.tools.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@RequestMapping("/requetemodele")
@Controller
public class RequeteModeleController {

  @Autowired private RequeteModeleRepository requeteModeleRepository;

  @Autowired private RequeteModeleParametereRepository requeteModeleParametereRepository;

  @RequestMapping(value = "", method = RequestMethod.GET, headers = "Accept=application/xml")
  @PreAuthorize("hasRight('HYDRANTS_ANALYSE_C')")
  public ResponseEntity<java.lang.String> listJson(
      final @RequestParam(value = "page", required = false) Integer page,
      final @RequestParam(value = "start", required = false) Integer start,
      final @RequestParam(value = "limit", required = false) Integer limit,
      final @RequestParam(value = "query", required = false) String query,
      @RequestParam(value = "sort", required = false) String sorts,
      @RequestParam(value = "filter", required = false) String filters) {

    final List<ItemFilter> itemFilterList = ItemFilter.decodeJson(filters);

    return new AbstractExtListSerializer<RequeteModele>("RequeteModele retrieved.") {

      @Override
      protected JSONSerializer additionnalIncludeExclude(JSONSerializer serializer) {
        serializer.include("data.*");

        return serializer.include("total").include("message");
      }

      @Override
      protected List<RequeteModele> getRecords() {
        return requeteModeleRepository.getAll(itemFilterList);
      }

      @Override
      protected Long countRecords() {
        return Long.valueOf(requeteModeleRepository.count());
      }
    }.serialize();
  }

  /**
   * Récupère les paramètres d'un modèle de requete
   *
   * @param idModele
   * @return
   */
  @RequestMapping(
      value = "requetemodelparam/{id}",
      method = RequestMethod.GET,
      headers = "Accept=application/json")
  @PreAuthorize("hasRight('HYDRANTS_ANALYSE_C')")
  public ResponseEntity<java.lang.String> getmodelParams(final @PathVariable("id") Long idModele) {

    return new AbstractExtListSerializer<RequeteModeleParametre>(
        "Paramètres Modeles Requete retrieved.") {

      @Override
      protected JSONSerializer additionnalIncludeExclude(JSONSerializer serializer) {
        return serializer.exclude("*.class");
      }

      @SuppressWarnings("unchecked")
      @Override
      protected List<RequeteModeleParametre> getRecords() {

        return requeteModeleParametereRepository.getByRequeteModele(idModele);
      }
    }.serialize();
  }

  /**
   * Retourne la liste des valeurs pour un paramètre de type 'combo'.
   *
   * @param id
   * @return
   */
  @RequestMapping(
      value = "reqmodparalst/{id}",
      method = RequestMethod.GET,
      headers = "Accept=application/json")
  @PreAuthorize("hasRight('HYDRANTS_ANALYSE_C')")
  public ResponseEntity<java.lang.String> getListComboModeleRequeteLike(
      final @PathVariable("id") Long id,
      final @RequestParam(value = "query", required = false) String query) {

    return new AbstractExtListSerializer<RemocraVueCombo>(" retrieved.") {

      @Override
      protected List<RemocraVueCombo> getRecords() {
        try {
          return requeteModeleRepository.getComboValues(id, query);
        } catch (SQLException e) {
          e.printStackTrace();
        } catch (ParseException e) {
          e.printStackTrace();
        }
        return null;
      }
    }.serialize();
  }

  @RequestMapping(
      value = "/{idmodele}",
      method = RequestMethod.POST,
      headers = "Accept=application/json")
  @PreAuthorize("hasRight('HYDRANTS_ANALYSE_C')")
  public ResponseEntity<java.lang.String> createrequest(
      final @PathVariable("idmodele") Integer idmodele,
      final @RequestParam("jsonValeurs") String json)
      throws BusinessException, IOException, SQLException, ParseException {

    try {
      String s =
          this.listmap_to_json_string(
              requeteModeleRepository.executeRequest(Long.valueOf(idmodele), json));
      if (s != null) {
        return new SuccessErrorExtSerializer(true, s).serialize();
      } else {
        return new SuccessErrorExtSerializer(false, "Problème lors de la création de la requête !")
            .serialize();
      }

    } catch (Exception e) {
      e.printStackTrace();
      return new SuccessErrorExtSerializer(false, e.getMessage()).serialize();
    }
  }

  public String listmap_to_json_string(List<HashMap> list) {
    JSONArray json_arr = new JSONArray();
    for (Map<String, Object> map : list) {
      JSONObject json_obj = new JSONObject();
      for (Map.Entry<String, Object> entry : map.entrySet()) {
        String key = entry.getKey();
        Object value = entry.getValue();
        try {
          json_obj.put(key, value);
        } catch (JSONException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
      }
      json_arr.add(json_obj);
    }
    return json_arr.toString();
  }

  /**
   * Retourne la liste des records à mettre dans le tableau des données
   *
   * @param id
   * @return
   */
  @RequestMapping(
      value = "reqmodresult/{id}",
      method = RequestMethod.GET,
      headers = "Accept=application/json")
  @PreAuthorize("hasRight('HYDRANTS_ANALYSE_C')")
  public ResponseEntity<java.lang.String> getListofResults(
      final @PathVariable("id") Long id,
      final @RequestParam(value = "page", required = false) Integer page,
      final @RequestParam(value = "start", required = false) Integer start,
      final @RequestParam(value = "limit", required = false) Integer limit,
      final @RequestParam(value = "query", required = false) String query,
      @RequestParam(value = "sort", required = false) String sorts,
      @RequestParam(value = "filter", required = false) String filters) {

    // On rejoue la requete pour récupérer les résultats
    return new AbstractExtListSerializer<Object>("retrieved") {

      @Override
      protected List<Object> getRecords() {
        try {
          return requeteModeleRepository.getResults(id, start, limit);
        } catch (SQLException e) {
          e.printStackTrace();
        }
        return null;
      }

      @Override
      protected Long countRecords() {
        try {
          return Long.valueOf(requeteModeleRepository.countRecords(id));
        } catch (SQLException e) {
          e.printStackTrace();
        }
        return null;
      }
    }.serialize();
  }

  /**
   * retourne l'etendu pour calculer la bbox.
   *
   * @param id
   * @return
   */
  @RequestMapping(
      value = "reqmodetendu/{id}",
      method = RequestMethod.GET,
      headers = "Accept=application/json")
  @PreAuthorize("hasRight('HYDRANTS_ANALYSE_C')")
  public ResponseEntity<String> getEtendu(
      final @PathVariable("id") Long id,
      final @RequestParam(value = "page", required = false) Integer page,
      final @RequestParam(value = "start", required = false) Integer start,
      final @RequestParam(value = "limit", required = false) Integer limit,
      final @RequestParam(value = "query", required = false) String query,
      @RequestParam(value = "sort", required = false) String sorts,
      @RequestParam(value = "filter", required = false) String filters) {
    try {
      String s = String.valueOf(requeteModeleRepository.getEtendu(id));
      if (s != null) {
        return new SuccessErrorExtSerializer(true, String.valueOf(s)).serialize();
      } else {
        return new SuccessErrorExtSerializer(
                false, "Problème lors de la récupération de l'emprise géographique!")
            .serialize();
      }

    } catch (Exception e) {
      e.printStackTrace();
      return new SuccessErrorExtSerializer(false, e.getMessage()).serialize();
    }
  }

  /**
   * Purger la Sélection
   *
   * @param id
   * @return
   */
  @RequestMapping(
      value = "/{id}",
      method = RequestMethod.DELETE,
      headers = "Accept=application/json")
  @PreAuthorize("hasRight('HYDRANTS_ANALYSE_C')")
  public ResponseEntity<java.lang.String> delete(@PathVariable("id") Long id) {
    try {
      requeteModeleRepository.doDelete(id);
      return new SuccessErrorExtSerializer(true, "Sélection supprimée").serialize();
    } catch (Exception e) {
      return new SuccessErrorExtSerializer(false, e.getMessage()).serialize();
    }
  }
}
