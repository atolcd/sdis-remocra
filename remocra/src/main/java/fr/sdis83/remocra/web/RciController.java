package fr.sdis83.remocra.web;

import com.vividsolutions.jts.geom.Point;
import flexjson.JSONDeserializer;
import flexjson.JSONSerializer;
import fr.sdis83.remocra.domain.remocra.Document;
import fr.sdis83.remocra.domain.remocra.Document.TypeDocument;
import fr.sdis83.remocra.domain.remocra.Rci;
import fr.sdis83.remocra.domain.remocra.RciDocument;
import fr.sdis83.remocra.exception.BusinessException;
import fr.sdis83.remocra.service.RciService;
import fr.sdis83.remocra.util.ExceptionUtils;
import fr.sdis83.remocra.util.FeatureUtil;
import fr.sdis83.remocra.web.deserialize.GeometryFactory;
import fr.sdis83.remocra.web.serialize.ext.AbstractExtObjectSerializer;
import fr.sdis83.remocra.web.serialize.ext.SuccessErrorExtSerializer;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.apache.log4j.Logger;
import org.hibernate.exception.ConstraintViolationException;
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
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

@RequestMapping("/rci")
@Controller
public class RciController extends AbstractServiceableController<RciService, Rci> {

  private final Logger logger = Logger.getLogger(getClass());

  @Autowired private RciService service;

  @PersistenceContext protected EntityManager entityManager;

  @Override
  protected RciService getService() {
    return service;
  }

  @Override
  protected JSONSerializer additionnalIncludeExclude(JSONSerializer serializer) {
    return serializer
        // Commune
        .include("data.commune.id")
        .include("data.commune.nom")
        .exclude("data.commune.*")
        // Documents
        .include("data.rciDocuments.id")
        .include("data.rciDocuments.titre")
        .include("data.rciDocuments.code")
        .exclude("data.rciDocuments.*")
        // Global
        .exclude("*.class");
  }

  // -----------------------------------
  // -- Droits sur les méthodes CRUD
  // -----------------------------------

  @RequestMapping(headers = "Accept=application/json")
  @PreAuthorize("hasRight('RCI_C')")
  public ResponseEntity<java.lang.String> listJson(
      @RequestParam(value = "page", required = false) Integer page,
      final @RequestParam(value = "start", required = false) Integer start,
      final @RequestParam(value = "limit", required = false) Integer limit,
      @RequestParam(value = "sort", required = false) String sorts,
      @RequestParam(value = "filter", required = false) String filters,
      @RequestParam(value = "query", required = false) String query) {
    return this.doListJson(page, start, limit, sorts, filters, query);
  }

  @RequestMapping(value = "/{id}", method = RequestMethod.GET, headers = "Accept=application/json")
  @PreAuthorize("hasRight('RCI_C')")
  public ResponseEntity<java.lang.String> get(
      final @RequestParam(value = "id", required = true) Long id) {

    return new AbstractExtObjectSerializer<Rci>("fr.sdis83.remocra.domain.remocra.Rci retrieved.") {

      @Override
      protected JSONSerializer additionnalIncludeExclude(JSONSerializer serializer) {
        return RciController.this.additionnalIncludeExclude(serializer);
      }

      @Override
      protected Rci getRecord() {
        return Rci.findRci(id);
      }
    }.serialize();
  }

  @RequestMapping(value = "/{id}", method = RequestMethod.PUT, headers = "Accept=application/json")
  @PreAuthorize("hasRight('RCI_C')")
  public ResponseEntity<java.lang.String> update(
      final @PathVariable Long id, final @RequestBody String json) {
    return this.doUpdate(id, json);
  }

  @RequestMapping(
      value = "/{id}",
      method = RequestMethod.DELETE,
      headers = "Accept=application/json")
  @PreAuthorize("hasRight('RCI_C')")
  public ResponseEntity<java.lang.String> delete(@PathVariable("id") Long id) {
    return this.doDelete(id);
  }

  @RequestMapping(
      value = "/document/{id}",
      method = RequestMethod.DELETE,
      headers = "Accept=application/json")
  @PreAuthorize("hasRight('RCI_C')")
  @Transactional
  public ResponseEntity<java.lang.String> deleteRciDocument(@PathVariable("id") Long id) {
    try {
      getService().deleteDocument(id);
      return new SuccessErrorExtSerializer(true, "Document supprimé").serialize();
    } catch (Exception e) {
      logger.error(e.getMessage(), e);
      return new SuccessErrorExtSerializer(false, e.getMessage()).serialize();
    }
  }

  // -----------------------------------
  // -- Aspect Feature
  // -----------------------------------

  @RequestMapping(value = "/layer", method = RequestMethod.GET, headers = "Accept=application/json")
  @PreAuthorize("hasRight('RCI_C')")
  public ResponseEntity<java.lang.String> layer(final @RequestParam String bbox) {
    if (bbox == null || bbox.isEmpty()) {
      return FeatureUtil.getResponse(service.findAll());
    } else {
      return FeatureUtil.getResponse(service.findByBBOX(bbox));
    }
  }

  /**
   * Mise à jour d'un départ de feu.
   *
   * @param id
   * @param request
   * @param jsonRci
   * @param fileCounter
   * @return
   */
  @Transactional
  @RequestMapping(
      value = "/{id}",
      method = RequestMethod.POST,
      headers = "Content-Type=multipart/form-data")
  @PreAuthorize("hasRight('RCI_C')")
  public ResponseEntity<java.lang.String> update(
      final @PathVariable("id") Long id,
      MultipartHttpServletRequest request,
      final @RequestParam String jsonRci,
      final @RequestParam int fileCounter) {
    try {
      final Rci attached = getService().update(id, jsonRci, null);
      if (attached != null) {

        // AJOUT PAR RAPPORT AU PARENT : DEBUT
        // Récupération des fichiers
        for (int i = 0; i < fileCounter; i++) {
          // Récupération des paramètres
          saveRciFile(request.getFile("doc" + i), attached);
        }
        // AJOUT PAR RAPPORT AU PARENT : FIN

        return new AbstractExtObjectSerializer<Rci>(
            "Model updated", SuccessErrorExtSerializer.DEFAULT_CONTENT_TYPE) {
          @Override
          protected JSONSerializer additionnalIncludeExclude(JSONSerializer serializer) {
            return RciController.this.additionnalIncludeExclude(serializer);
          }

          @Override
          protected Rci getRecord() throws BusinessException {
            return attached;
          }
        }.serialize();
      }
    } catch (Exception e) {
      if (ExceptionUtils.getNestedExceptionWithClass(e, ConstraintViolationException.class)
          != null) {
        // Contrainte non respectée. Par pour le ModelT Droit,
        // certainement l'unicité : (TypeDroit,ProfilDroit)
        return new SuccessErrorExtSerializer(false, getConstraintViolationExceptionMsg())
            .serialize();
      }
      logger.error(e.getMessage(), e);
      return new SuccessErrorExtSerializer(false, e.getMessage()).serialize();
    }
    return new SuccessErrorExtSerializer(false, "Model inexistant", HttpStatus.NOT_FOUND)
        .serialize();
  }

  /**
   * Création d'un départ de feu.
   *
   * @param request
   * @param jsonRci
   * @param fileCounter
   * @return
   */
  @Transactional
  @RequestMapping(
      value = "",
      method = RequestMethod.POST,
      headers = "Content-Type=multipart/form-data")
  @PreAuthorize("hasRight('RCI_C')")
  public ResponseEntity<java.lang.String> create(
      MultipartHttpServletRequest request,
      final @RequestParam String jsonRci,
      final @RequestParam int fileCounter) {

    try {
      final Rci attached = getService().create(jsonRci, null);

      // AJOUT PAR RAPPORT AU PARENT : DEBUT
      // Récupération des fichiers
      for (int i = 0; i < fileCounter; i++) {
        // Récupération des paramètres
        saveRciFile(request.getFile("doc" + i), attached);
      }
      // AJOUT PAR RAPPORT AU PARENT : FIN

      return new AbstractExtObjectSerializer<Rci>(
          "Model created", SuccessErrorExtSerializer.DEFAULT_CONTENT_TYPE) {
        @Override
        protected JSONSerializer additionnalIncludeExclude(JSONSerializer serializer) {
          return RciController.this.additionnalIncludeExclude(serializer);
        }

        @Override
        protected Rci getRecord() throws BusinessException {
          return attached;
        }
      }.serialize();
    } catch (Exception e) {
      if (ExceptionUtils.getNestedExceptionWithClass(e, ConstraintViolationException.class)
          != null) {
        // Contrainte non respectée. Par pour le ModelT Droit,
        // certainement l'unicité : (TypeDroit,ProfilDroit)
        return new SuccessErrorExtSerializer(false, getConstraintViolationExceptionMsg())
            .serialize();
      }
      logger.error(e.getMessage(), e);
      return new SuccessErrorExtSerializer(false, e.getMessage()).serialize();
    }
  }

  protected void saveRciFile(MultipartFile mf, Rci rci) throws Exception {
    String depotRepertoire = paramConfService.getDossierDepotRci();

    // Document "générique"
    Document d = createNonPersistedDocument(TypeDocument.RCI, mf, depotRepertoire);

    RciDocument pd = new RciDocument();
    pd.setRci(rci);
    pd.setDocument(d);
    rci.getRciDocuments().add(pd);

    d.persist();
  }

  @RequestMapping(
      value = "{id}/deplacer",
      method = RequestMethod.POST,
      headers = "Accept=application/json")
  @PreAuthorize("hasRight('RCI_C')")
  public ResponseEntity<java.lang.String> deplacer(
      final @PathVariable(value = "id") Long id,
      final @RequestParam(value = "geometrie") String geometrie,
      final @RequestParam(value = "srid") Integer srid) {
    try {
      JSONDeserializer<Point> deserializer = new JSONDeserializer<Point>();
      deserializer.use((String) null, new GeometryFactory());
      Point point = deserializer.deserialize(geometrie);
      point.setSRID(srid);
      getService().deplacer(id, point, srid);
    } catch (Exception e) {
      return new SuccessErrorExtSerializer(
              false, "Problème survenu lors du déplacement du départ de feu")
          .serialize();
    }
    return new SuccessErrorExtSerializer(true, "Départ de feu déplacé").serialize();
  }
}
