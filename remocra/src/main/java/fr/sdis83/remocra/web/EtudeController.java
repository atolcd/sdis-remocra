package fr.sdis83.remocra.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.sdis83.remocra.repository.EtudeRepository;
import fr.sdis83.remocra.service.UtilisateurService;
import fr.sdis83.remocra.usecase.etude.EtudeUseCase;
import fr.sdis83.remocra.web.message.ItemFilter;
import fr.sdis83.remocra.web.message.ItemSorting;
import fr.sdis83.remocra.web.serialize.ext.AbstractExtObjectSerializer;
import fr.sdis83.remocra.web.serialize.ext.SuccessErrorExtSerializer;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
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
  @Autowired private EtudeUseCase etudeUseCase;

  @Autowired private UtilisateurService utilisateurService;

  private final Logger logger = Logger.getLogger(getClass());
  private final ObjectMapper objectMapper = new ObjectMapper();

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
    try {
      HttpHeaders responseHeaders = new HttpHeaders();
      responseHeaders.add("Content-Type", "text/html; charset=utf-8");
      return new ResponseEntity<>(
          objectMapper.writeValueAsString(
              etudeUseCase.getAll(
                  itemFilterList,
                  limit,
                  start,
                  sortList,
                  utilisateurService.getCurrentUtilisateur().getOrganisme().getId())),
          responseHeaders,
          HttpStatus.OK);
    } catch (Exception e) {
      logger.error("Impossible de récupérer les études " + e.getMessage());
      return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
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
      value = "/etendu/{idEtude}",
      method = RequestMethod.GET,
      headers = "Accept=application/json")
  @PreAuthorize("hasRight('PLANIFIER_DECI')")
  public ResponseEntity<String> getEtendu(final @PathVariable("idEtude") Long idEtude) {
    try {

      return new ResponseEntity(etudeRepository.getEtenduEtude(idEtude), HttpStatus.OK);
    } catch (Exception e) {
      e.printStackTrace();
      return new ResponseEntity(
          "Impossible de récupérer l'emprise géographique ", HttpStatus.INTERNAL_SERVER_ERROR);
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
