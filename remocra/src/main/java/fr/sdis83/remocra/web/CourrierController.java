package fr.sdis83.remocra.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vividsolutions.jts.geom.Geometry;
import flexjson.JSONDeserializer;
import flexjson.JSONSerializer;
import fr.sdis83.remocra.db.model.remocra.tables.pojos.CourrierModele;
import fr.sdis83.remocra.db.model.remocra.tables.pojos.CourrierParametre;
import fr.sdis83.remocra.domain.remocra.Document;
import fr.sdis83.remocra.domain.remocra.RemocraVueCombo;
import fr.sdis83.remocra.exception.BusinessException;
import fr.sdis83.remocra.repository.CourrierRepository;
import fr.sdis83.remocra.repository.DestinataireRepository;
import fr.sdis83.remocra.service.TelechargementService;
import fr.sdis83.remocra.service.UtilisateurService;
import fr.sdis83.remocra.usecase.destinatairecourrier.DestinataireCourrierUseCase;
import fr.sdis83.remocra.usecase.parametre.ParametreDataProvider;
import fr.sdis83.remocra.util.DocumentUtil;
import fr.sdis83.remocra.web.message.ItemFilter;
import fr.sdis83.remocra.web.message.ItemSorting;
import fr.sdis83.remocra.web.model.CourrierDocumentModel;
import fr.sdis83.remocra.web.serialize.ext.AbstractExtListSerializer;
import fr.sdis83.remocra.web.serialize.ext.SuccessErrorExtSerializer;
import freemarker.ext.dom.NodeModel;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.naming.AuthenticationException;
import javax.servlet.http.HttpServletResponse;
import net.sf.jooreports.templates.DocumentTemplate;
import net.sf.jooreports.templates.DocumentTemplateFactory;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.encoding.MessageDigestPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.xml.sax.InputSource;

@RequestMapping("/courrier")
@Controller
public class CourrierController {

  @Autowired TelechargementService telechargementsService;
  @Autowired CourrierRepository courrierRepository;
  @Autowired UtilisateurService utilisateurService;
  @Autowired DestinataireRepository destinataireRepository;
  @Autowired DestinataireCourrierUseCase destinataireCourrierUseCase;

  @Autowired private ParametreDataProvider parametreProvider;

  @Autowired private MessageDigestPasswordEncoder messageDigestPasswordEncoder;

  private final ObjectMapper objectMapper = new ObjectMapper();
  private final Logger logger = Logger.getLogger(getClass());

  @RequestMapping(
      value = "/with/{thematique}",
      method = RequestMethod.GET,
      headers = "Accept=application/json")
  @PreAuthorize("hasRight('COURRIER_C')")
  public ResponseEntity<String> listJson(final @PathVariable("thematique") String thematique) {

    return new AbstractExtListSerializer<CourrierModele>(
        "fr.sdis83.remocra.domain.remocra.CourrierModele retrieved.") {
      @Override
      protected JSONSerializer additionnalIncludeExclude(JSONSerializer serializer) {
        return new JSONSerializer()
            .include("data.id")
            .include("data.categorie")
            .include("data.libelle")
            .include("data.description")
            .include("data.modele_ott")
            .include("data.source_xml");
      }

      @Override
      protected List<CourrierModele> getRecords() throws BusinessException {
        return courrierRepository.getAllModeleByThematique(thematique);
      }
    }.serialize();
  }

  /** Méthode d'accès aux courriers de l'utilisateur */
  @RequestMapping(
      value = "/courrierdocument",
      method = RequestMethod.GET,
      headers = "Accept=application/json")
  @PreAuthorize(
      "hasRight('COURRIER_UTILISATEUR_R') or hasRight('COURRIER_ORGANISME_R') or hasRight('COURRIER_ADMIN_R')")
  public ResponseEntity<String> courrierdocument(
      final @RequestParam(value = "page", required = false) Integer page,
      final @RequestParam(value = "distinct", required = false, defaultValue = "false") boolean
              distinct,
      final @RequestParam(value = "start", required = true) Integer start,
      final @RequestParam(value = "limit", required = true) Integer limit,
      final @RequestParam(value = "sort", required = false) String sorts,
      final @RequestParam(value = "filter", required = false) String filters) {

    final List<ItemSorting> sortList = ItemSorting.decodeJson(sorts);
    final List<ItemFilter> itemFilterList = ItemFilter.decodeJson(filters);

    return new AbstractExtListSerializer<CourrierDocumentModel>("Courriers retrieved.") {
      @Override
      protected List<CourrierDocumentModel> getRecords()
          throws BusinessException, AuthenticationException {
        return courrierRepository.getCourriersAccessibles(
            distinct, start, limit, itemFilterList, sortList);
      }

      @Override
      protected Long countRecords() {
        return Long.valueOf(
            courrierRepository.getCourriersAccessiblesCount(distinct, itemFilterList, sortList));
      }
    }.serialize();
  }

  /** Renvoie le nombre de courrier accessibles par l'utilisateur */
  @RequestMapping(
      value = "/courrierdocumentcount",
      method = RequestMethod.GET,
      headers = "Accept=application/json")
  @PreAuthorize(
      "hasRight('COURRIER_UTILISATEUR_R') or hasRight('COURRIER_ORGANISME_R') or hasRight('COURRIER_ADMIN_R')")
  public ResponseEntity<String> courrierdocumentCount(
      final @RequestParam(value = "page", required = false) Integer page,
      final @RequestParam(value = "distinct", required = false, defaultValue = "false") boolean
              distinct,
      final @RequestParam(value = "sort", required = false) String sorts,
      final @RequestParam(value = "filter", required = false) String filters) {

    final List<ItemSorting> sortList = ItemSorting.decodeJson(sorts);
    final List<ItemFilter> itemFilterList = ItemFilter.decodeJson(filters);

    try {
      int count =
          courrierRepository.getCourriersAccessiblesCount(distinct, itemFilterList, sortList);
      return new SuccessErrorExtSerializer(true, String.valueOf(count)).serialize();
    } catch (Exception e) {
      return new SuccessErrorExtSerializer(false, e.getMessage()).serialize();
    }
  }

  @RequestMapping(
      value = "/getdocument",
      method = RequestMethod.GET,
      headers = "Accept=application/json")
  @PreAuthorize(
      "hasRight('COURRIER_UTILISATEUR_R') or hasRight('COURRIER_ORGANISME_R') or hasRight('COURRIER_ADMIN_R')")
  public void getDocument(
      final @RequestParam(value = "code") String code, HttpServletResponse response)
      throws IOException {
    this.downloadCourrierDocument(code, response);
  }

  /**
   * Retourne un document à partir de son code. Un accusé est ensuite mis pour tous les courriers
   * liés à ce document
   *
   * @param code Le code du document
   */
  @RequestMapping(
      value = "/getdocumentfromcode/{code}",
      method = RequestMethod.GET,
      headers = "Accept=application/json")
  @PreAuthorize(
      "hasRight('COURRIER_UTILISATEUR_R') or hasRight('COURRIER_ORGANISME_R') or hasRight('COURRIER_ADMIN_R')")
  public void getDocumentFromCode(
      final @PathVariable(value = "code") String code, HttpServletResponse response)
      throws IOException {

    String path = telechargementsService.getRemocraFilePathFromCode(code);
    DocumentUtil.getInstance().downloadDocument(path, code, response);

    if (response.containsHeader("Content-Type")) {
      courrierRepository.setAccuseForDocumentCode(code);
    }
  }

  @RequestMapping(value = "/{code}")
  public void downloadCourrierDocument(
      @PathVariable("code") String code, HttpServletResponse response) throws IOException {
    String path = telechargementsService.getCourrierFilePathFromCode(code);
    DocumentUtil.getInstance().downloadDocument(path, code, response);

    // Accusé de réception si le document a été trouvé: se baser juste sur le code ne garantit pas
    // que l'utilisateur a bien récupéré le fichier
    // Cas typique: document présent dans la base mais pas sur le disque
    if (response.containsHeader("Content-Type")) {
      telechargementsService.setCourrierAccuseFromCode(code);
    }
  }

  @RequestMapping(value = "/show/{code}")
  public void showCourrierDocument(@PathVariable("code") String code, HttpServletResponse response)
      throws IOException {
    String path = telechargementsService.getCourrierFilePathFromCode(code);
    DocumentUtil.getInstance().showDocument(path, code, response);
    // Accusé de réception
    telechargementsService.setCourrierAccuseFromCode(code);
  }

  /** Retourne la liste des paramètres pour un modele de courrier. */
  @RequestMapping(
      value = "/courrierParams/{id}",
      method = RequestMethod.GET,
      headers = "Accept=application/json")
  @PreAuthorize("hasRight('COURRIER_C')")
  public ResponseEntity<java.lang.String> getCourrierParams(
      final @PathVariable("id") Long idModele) {
    return new AbstractExtListSerializer<CourrierParametre>(
        "Paramètres Modèle Courrier retrieved.") {
      @Override
      protected JSONSerializer additionnalIncludeExclude(JSONSerializer serializer) {
        return serializer.exclude("*.class");
      }

      @Override
      protected List<CourrierParametre> getRecords() {
        return courrierRepository.getParamsByCourrierModele(idModele);
      }
    }.serialize();
  }

  /** Retourne la liste des valeurs pour un paramètre de type 'combo'. */
  @RequestMapping(
      value = "courriermodparalst/{id}",
      method = RequestMethod.GET,
      headers = "Accept=application/json")
  @PreAuthorize("hasRight('COURRIER_C')")
  public ResponseEntity<java.lang.String> getListComboModelCourrierLike(
      final @PathVariable("id") Long id,
      final @RequestParam(value = "query", required = false) String query,
      final @RequestParam(value = "limit", required = false) Integer limit) {

    return new AbstractExtListSerializer<RemocraVueCombo>(" retrieved.") {

      @Override
      protected List<RemocraVueCombo> getRecords() {
        try {
          return courrierRepository.getComboValues(id, query, limit != null ? limit : 10);
        } catch (SQLException e) {
          e.printStackTrace();
        } catch (ParseException e) {
          e.printStackTrace();
        }
        return null;
      }
    }.serialize();
  }

  /**
   * Récupère la liste des destinataires possible pour l'émission de courriers
   *
   * @return une liste de DestinataireModel contenant des organismes, utilisateur et contacts
   *     gestionnaire et organisme
   */
  @RequestMapping(
      value = "/destinataires/{useZC}",
      method = RequestMethod.GET,
      headers = "Accept=application/json;charset=utf-8")
  @PreAuthorize("hasRight('COURRIER_C')")
  public ResponseEntity<String> getAllDestinatairesCourrier(
      @PathVariable("useZC") final boolean useZC) {
    try {
      Geometry geometryOrganismeCurrentUser =
          utilisateurService
              .getCurrentUtilisateur()
              .getOrganisme()
              .getZoneCompetence()
              .getGeometrie();
      return new ResponseEntity<>(
          objectMapper.writeValueAsString(
              destinataireCourrierUseCase.getAllDestinataireCourrier(
                  useZC, geometryOrganismeCurrentUser, parametreProvider.get().getSridInt())),
          HttpStatus.OK);
    } catch (Exception e) {
      this.logger.error(e.getMessage(), e);
      return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
  };

  // Génération du courrier a partir des informations du formulaire
  @RequestMapping(
      value = "generecourrier/{id}",
      method = RequestMethod.POST,
      headers = "Content-Type=multipart/form-data")
  @PreAuthorize("hasRight('COURRIER_C')")
  @Transactional
  public ResponseEntity<java.lang.String> genereCourrier(
      MultipartHttpServletRequest request, final @PathVariable(value = "id") Long idModele) {
    String erreur = "";
    try {
      Map<String, String[]> mapParametres = request.getParameterMap();
      // Ouverture du modèle de courrier
      String cheminModele = courrierRepository.getNomModele(idModele);
      InputStream textTplInputStream;
      if (new File(cheminModele).exists()) {
        textTplInputStream = new FileInputStream(new File(cheminModele));
      } else {
        return new SuccessErrorExtSerializer(false, "Le modèle de courrier n'existe pas.")
            .serialize();
      }
      // Récupération de la requête SQL correspondant au modèle (génère du XML)
      String xmlQuery = courrierRepository.getModeleXmlQuery(idModele, mapParametres);
      InputSource xmlSource = new InputSource(new StringReader(xmlQuery));
      NodeModel nodeModel = NodeModel.parse(xmlSource);

      // Transformation du modèle de courrier en DocumentTemplate
      DocumentTemplateFactory documentTemplateFactory = new DocumentTemplateFactory();
      DocumentTemplate odtTemplate = null;
      odtTemplate = documentTemplateFactory.getTemplate(textTplInputStream);

      // Création d'un dossier avec une clé aléatoire
      String[] nomModeleTemp = cheminModele.split("/");
      String nomModele = nomModeleTemp[nomModeleTemp.length - 1];
      File fichier = new File(nomModele);
      Document docOTT =
          DocumentUtil.getInstance()
              .createNonPersistedDocument(
                  Document.TypeDocument.COURRIER,
                  fichier,
                  parametreProvider.get().getDossierCourriersExternes() + "/courrier_temp");
      FileOutputStream textFileOutputStream =
          new FileOutputStream(new File(docOTT.getRepertoire() + docOTT.getFichier()));
      // Association du modèle et du XML
      odtTemplate.createDocument(nodeModel, textFileOutputStream);

      // Génération du PDF
      String nomPdf = docOTT.getFichier().split("\\.")[0] + ".pdf";
      FileInputStream textFileInputStream =
          new FileInputStream(new File(docOTT.getRepertoire() + docOTT.getFichier()));
      FileOutputStream pdfFileOutputStream =
          new FileOutputStream(new File(docOTT.getRepertoire() + nomPdf));
      DocumentUtil.getInstance().generePdf(textFileInputStream, pdfFileOutputStream);

      // Signature du pdf
      if (!parametreProvider.get().getPdiCheminPfxFile().equals("")
          && !parametreProvider.get().getPdiPfxPassword().equals("")) {
        // todo : ajouter les dépendances qu'il faut
        // DocumentUtil.getInstance().signPdf(docOTT.getRepertoire() + nomPdf,
        // parametreProvider.get().getPdiCheminPfxFile(),
        // parametreProvider.get().getPdiPfxPassword());
      }

      // Suppression de l'ott
      File ott = new File(docOTT.getRepertoire() + docOTT.getFichier());
      ott.delete();

      textTplInputStream.close();
      textFileOutputStream.close();
      textFileInputStream.close();
      pdfFileOutputStream.close();
      fichier.delete();

      return new SuccessErrorExtSerializer(true, docOTT.getCode() + File.separator + nomPdf)
          .serialize();

    } catch (NullPointerException npe) {
      npe.printStackTrace();
      erreur =
          "Erreur lors de l'exécution de la requête : NullPointerException. La requête ne renvoie aucune ligne avec les données saisies.";
    } catch (Exception e) {
      e.printStackTrace();
      erreur =
          "Erreur lors du remplissage du template: un noeud xml est manquant. Peut venir d'une donnée manquante dans la base de données.";
    }
    return new SuccessErrorExtSerializer(false, erreur).serialize();
  }

  @RequestMapping(
      value = "notifier",
      method = RequestMethod.POST,
      headers = "Accept=application/json;charset=utf-8")
  @PreAuthorize("hasRight('COURRIER_C')")
  @Transactional
  public ResponseEntity<java.lang.String> notifierCourrier(final @RequestBody String json) {
    try {
      HashMap<String, Object> mapParametres =
          new JSONDeserializer<HashMap<String, Object>>().deserialize(json);

      String codeCourrier = String.valueOf(mapParametres.get("codeCourrier"));
      String nomCourrier = String.valueOf(mapParametres.get("nomCourrier"));
      String reference = String.valueOf(mapParametres.get("reference"));
      String objet = String.valueOf(mapParametres.get("objet"));
      String codeModele = String.valueOf(mapParametres.get("codeModele"));
      // Déplacement du dossier+pdf dans dossier courriers
      File origine =
          new File(
              parametreProvider.get().getDossierCourriersExternes()
                  + "/courrier_temp/"
                  + codeCourrier);
      File destination =
          new File(parametreProvider.get().getDossierCourriersExternes() + "/" + codeCourrier);
      origine.renameTo(destination);

      // Insertion dans table document

      String erreurDocument = courrierRepository.insertDocument(codeCourrier, nomCourrier);

      // Insertion dans table courrier_document
      List<Object> destinataires = new ArrayList<>();
      destinataires.addAll((List<Object>) mapParametres.get("destinataires"));
      String erreurCourrierDocument = "";
      String erreurEmail = "";
      for (Object destinataire : destinataires) {
        Long idDest = Long.valueOf(String.valueOf(((Map) destinataire).get("id")));
        String typeDest = String.valueOf(((Map) destinataire).get("type")).toUpperCase();
        String nomDest = String.valueOf(((Map) destinataire).get("nom"));

        if (erreurDocument.equals("")) {
          // Insertion dans la table courrier_document
          String organismeUtilisateur =
              utilisateurService.getCurrentUtilisateur().getOrganisme().getNom();
          erreurCourrierDocument =
              courrierRepository.insertCourrierDocument(
                  codeCourrier, nomDest, typeDest, idDest, reference, objet, organismeUtilisateur);
        }

        if (erreurDocument.equals("") && erreurCourrierDocument.equals("")) {
          // Insertion dans la table email
          erreurEmail =
              courrierRepository.insertEmail(codeModele, nomDest, typeDest, idDest, codeCourrier);
        }
      }

      if (!erreurDocument.equals("")
          || !erreurCourrierDocument.equals("")
          || !erreurEmail.equals("")) {
        return new SuccessErrorExtSerializer(
                false,
                "Erreur : Une erreur est survenue lors de la notification du courrier."
                    + " Problème d'insertion dans la table "
                    + erreurDocument
                    + erreurCourrierDocument
                    + erreurEmail)
            .serialize();
      } else {
        return new SuccessErrorExtSerializer(
                true, "Succès de la demande de notification du courrier.")
            .serialize();
      }
    } catch (Exception e) {
      e.printStackTrace();
      return new SuccessErrorExtSerializer(
              false, "Une erreur est survenue lors de la notification du courrier.")
          .serialize();
    }
  }
}
