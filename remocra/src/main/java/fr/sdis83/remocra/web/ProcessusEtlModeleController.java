package fr.sdis83.remocra.web;

import java.sql.SQLException;
import java.text.ParseException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import flexjson.JSONSerializer;
import fr.sdis83.remocra.db.model.remocra.tables.pojos.ProcessusEtl;
import fr.sdis83.remocra.db.model.remocra.tables.pojos.ProcessusEtlModele;
import fr.sdis83.remocra.db.model.remocra.tables.pojos.ProcessusEtlModeleParametre;
import fr.sdis83.remocra.domain.remocra.RemocraVueCombo;
import fr.sdis83.remocra.exception.BusinessException;
import fr.sdis83.remocra.repository.ProcessusEtlModeleParametereRepository;
import fr.sdis83.remocra.repository.ProcessusEtlModeleRepository;
import fr.sdis83.remocra.web.message.ItemFilter;
import fr.sdis83.remocra.web.serialize.ext.AbstractExtListSerializer;
import fr.sdis83.remocra.web.serialize.ext.SuccessErrorExtSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartHttpServletRequest;

@RequestMapping("/processusetlmodele")
@Controller
public class ProcessusEtlModeleController {


    @Autowired
    private ProcessusEtlModeleRepository processusEtlModeleRepository;

  @Autowired
  private ProcessusEtlModeleParametereRepository processusEtlModeleParametereRepository;

  @RequestMapping(value = "", method = RequestMethod.GET, headers = "Accept=application/xml")
  @PreAuthorize("hasRight('CRISE_C')")
  public ResponseEntity<String> listJson(final @RequestParam(value = "page", required = false) Integer page,
                                                   final @RequestParam(value = "start", required = false) Integer start, final @RequestParam(value = "limit", required = false) Integer limit,
                                                   final @RequestParam(value = "query", required = false) String query, @RequestParam(value = "sort", required = false) String sorts,
                                                   @RequestParam(value = "filter", required = false) String filters) {


    final List<ItemFilter> itemFilterList = ItemFilter.decodeJson(filters);

    return new AbstractExtListSerializer<ProcessusEtlModele>("ProcessusEtlModele retrieved.") {

      @Override
      protected JSONSerializer additionnalIncludeExclude(JSONSerializer serializer) {
        serializer.include("data.*");

        return serializer.include("total").include("message");
      }

      @Override
      protected List<ProcessusEtlModele> getRecords() {
        return processusEtlModeleRepository.getAll(itemFilterList);
      }

      @Override
      protected Long countRecords() {
        return
           Long.valueOf(processusEtlModeleRepository.count());
      }

    }.serialize();
  }

  /**
   * Récupère les paramètres d'un modèle de requete
   *
   * @param idModele
   * @return
   */
  @RequestMapping(value = "processusetlmodelparam/{id}", method = RequestMethod.GET, headers = "Accept=application/json")
  @PreAuthorize("hasRight('CRISE_C')")
  public ResponseEntity<java.lang.String> getmodelParams(final @PathVariable("id") Long idModele) {

    return new AbstractExtListSerializer<ProcessusEtlModeleParametre>("Paramètres Processus Etl Modele retrieved.") {

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
  @RequestMapping(value = "processusetlmodparalst/{id}", method = RequestMethod.GET, headers = "Accept=application/json")
  @PreAuthorize("hasRight('CRISE_C')")
  public ResponseEntity<java.lang.String> getListComboModelProcessusEtlLike(
      final @PathVariable("id") Long id,
      final @RequestParam(value = "query", required = false) String query,
      final @RequestParam(value = "limit", required = false) Integer limit){

    return new AbstractExtListSerializer<RemocraVueCombo>(" retrieved.") {

      @Override
      protected List<RemocraVueCombo> getRecords() {
        try{
          return processusEtlModeleRepository.getComboValues(id, query, limit != null ? limit : 10);
        } catch (SQLException e) {
          e.printStackTrace();
        } catch (ParseException e) {
          e.printStackTrace();
        }return  null;
      }

    }.serialize();
  }



  @RequestMapping(value = "{idmodele}", method = RequestMethod.POST,  headers = "Content-Type=multipart/form-data")
  @PreAuthorize("hasRight('CRISE_C')")
  @Transactional
  public ResponseEntity<java.lang.String> createProcessEtl( MultipartHttpServletRequest request) {
    try{
      ProcessusEtl p = processusEtlModeleRepository.createProcess(request);
      return new SuccessErrorExtSerializer(true, "Le processus Etl  a été enregistrée").serialize();
    }catch(Exception e){
      return new SuccessErrorExtSerializer(false, "Une erreur est survenue lors de l\'enregistrement du processus Etl").serialize();
    }
  }
}
