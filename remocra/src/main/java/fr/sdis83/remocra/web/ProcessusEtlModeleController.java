package fr.sdis83.remocra.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import flexjson.JSONSerializer;
import fr.sdis83.remocra.GlobalConstants;
import fr.sdis83.remocra.db.model.remocra.tables.pojos.ProcessusEtl;
import fr.sdis83.remocra.db.model.remocra.tables.pojos.ProcessusEtlModeleParametre;
import fr.sdis83.remocra.domain.remocra.RemocraVueCombo;
import fr.sdis83.remocra.repository.ProcessusEtlModeleParametereRepository;
import fr.sdis83.remocra.repository.ProcessusEtlModeleRepository;
import fr.sdis83.remocra.web.serialize.ext.AbstractExtListSerializer;
import fr.sdis83.remocra.web.serialize.ext.SuccessErrorExtSerializer;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartHttpServletRequest;

@RequestMapping("/processusetlmodele")
@Controller
public class ProcessusEtlModeleController {

  @Autowired private ProcessusEtlModeleRepository processusEtlModeleRepository;

  @Autowired private ProcessusEtlModeleParametereRepository processusEtlModeleParametereRepository;
  private ObjectMapper objectMapper = new ObjectMapper();

  @RequestMapping(value = "", method = RequestMethod.GET, headers = "Accept=application/xml")
  @PreAuthorize("hasRight('CRISE_C')")
  public ResponseEntity<String> getListModeleCrise(
      final @RequestParam(value = "page", required = false) Integer page,
      final @RequestParam(value = "start", required = false) Integer start,
      final @RequestParam(value = "limit", required = false) Integer limit,
      final @RequestParam(value = "query", required = false) String query,
      @RequestParam(value = "sort", required = false) String sorts,
      @RequestParam(value = "filter", required = false) String filters) {

    try {
      String str =
          objectMapper.writeValueAsString(
              processusEtlModeleRepository.getAll(GlobalConstants.CATEGORIE_CRISE));
      return new ResponseEntity<>(str, HttpStatus.OK);
    } catch (Exception e) {
      return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @RequestMapping(
      value = "planificationdeci",
      method = RequestMethod.GET,
      headers = "Accept=application/xml")
  @PreAuthorize("hasRight('PLANIFIER_DECI')")
  public ResponseEntity<String> getListModeleDeci(
      final @RequestParam(value = "page", required = false) Integer page,
      final @RequestParam(value = "start", required = false) Integer start,
      final @RequestParam(value = "limit", required = false) Integer limit,
      final @RequestParam(value = "query", required = false) String query,
      @RequestParam(value = "sort", required = false) String sorts,
      @RequestParam(value = "filter", required = false) String filters) {
    try {
      String str =
          objectMapper.writeValueAsString(
              processusEtlModeleRepository.getAll(
                  GlobalConstants.CATEGORIE_COUVERTURE_HYDRAULIQUE));
      return new ResponseEntity<>(str, HttpStatus.OK);
    } catch (Exception e) {
      return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  /**
   * Récupère les paramètres d'un modèle de requete
   *
   * @param idModele
   * @return
   */
  @RequestMapping(
      value = "processusetlmodelparam/{id}",
      method = RequestMethod.GET,
      headers = "Accept=application/json")
  @PreAuthorize("hasRight('CRISE_C')")
  public ResponseEntity<java.lang.String> getmodelParams(final @PathVariable("id") Long idModele) {

    return new AbstractExtListSerializer<ProcessusEtlModeleParametre>(
        "Paramètres Processus Etl Modele retrieved.") {

      @Override
      protected JSONSerializer additionnalIncludeExclude(JSONSerializer serializer) {
        return serializer.exclude("*.class");
      }

      @SuppressWarnings("unchecked")
      @Override
      protected List<ProcessusEtlModeleParametre> getRecords() {

        return processusEtlModeleParametereRepository.getByProcessusEtlModele(idModele);
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
      value = "processusetlmodparalst/{id}",
      method = RequestMethod.GET,
      headers = "Accept=application/json")
  @PreAuthorize("hasRight('CRISE_C')")
  public ResponseEntity<java.lang.String> getListComboModelProcessusEtlLike(
      final @PathVariable("id") Long id,
      final @RequestParam(value = "query", required = false) String query,
      final @RequestParam(value = "limit", required = false) Integer limit) {

    return new AbstractExtListSerializer<RemocraVueCombo>(" retrieved.") {

      @Override
      protected List<RemocraVueCombo> getRecords() {
        try {
          return processusEtlModeleRepository.getComboValues(id, query, limit != null ? limit : 10);
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
      value = "{idmodele}",
      method = RequestMethod.POST,
      headers = "Content-Type=multipart/form-data")
  @PreAuthorize("hasRight('CRISE_C') or hasRight('PLANIFIER_DECI')")
  @Transactional
  public ResponseEntity<java.lang.String> createProcessEtl(MultipartHttpServletRequest request) {
    try {
      ProcessusEtl p = processusEtlModeleRepository.createProcess(request);
      return new SuccessErrorExtSerializer(
              true,
              "Le processus ETL a été enregistré. Un mail vous informera de l'issue du traitement.")
          .serialize();
    } catch (Exception e) {
      return new SuccessErrorExtSerializer(
              false, "Une erreur est survenue lors de l\'enregistrement du processus ETL")
          .serialize();
    }
  }

  @RequestMapping(
      value = "/withoutfile/{idmodele}",
      method = RequestMethod.POST,
      headers = "Accept=application/json")
  @PreAuthorize("hasRight('CRISE_C')")
  @Transactional
  public ResponseEntity<java.lang.String> createProcess(
      HttpServletRequest request, final @RequestBody String json) {
    try {
      ProcessusEtl p = processusEtlModeleRepository.createProcessWithoutFile(json);
      return new SuccessErrorExtSerializer(true, "Le processus Etl  a été enregistrée").serialize();
    } catch (Exception e) {
      return new SuccessErrorExtSerializer(
              false, "Une erreur est survenue lors de l\'enregistrement du processus Etl")
          .serialize();
    }
  }

  @RequestMapping(
      value = "/{codeProcess}",
      method = RequestMethod.POST,
      headers = "Accept=application/json")
  @Transactional
  public ResponseEntity<java.lang.String> createProcess(
      final @RequestBody String json, final @PathVariable("codeProcess") String codeProcess) {
    try {
      ProcessusEtl p = processusEtlModeleRepository.createProcess(codeProcess, json);
      return new SuccessErrorExtSerializer(true, "Le processus Etl  a été enregistrée").serialize();
    } catch (Exception e) {
      return new SuccessErrorExtSerializer(
              false, "Une erreur est survenue lors de l\'enregistrement du processus Etl")
          .serialize();
    }
  }
}
