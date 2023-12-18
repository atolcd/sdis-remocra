package fr.sdis83.remocra.web;

import fr.sdis83.remocra.domain.remocra.DepotDocument;
import fr.sdis83.remocra.domain.remocra.Document;
import fr.sdis83.remocra.domain.remocra.Document.TypeDocument;
import fr.sdis83.remocra.domain.remocra.EmailModele;
import fr.sdis83.remocra.domain.remocra.EmailModele.EmailModeleEnum;
import fr.sdis83.remocra.domain.remocra.EmailModele.EmailModeleKeys;
import fr.sdis83.remocra.domain.remocra.Utilisateur;
import fr.sdis83.remocra.exception.BusinessException;
import fr.sdis83.remocra.service.MailUtils;
import fr.sdis83.remocra.web.serialize.ext.SuccessErrorExtSerializer;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

@RequestMapping("/depot")
@Controller
public class DepotController extends AbstractRemocraController {

  private final Logger logger = Logger.getLogger(getClass());

  @Autowired private MailUtils mailUtils;

  @RequestMapping(
      value = "/delibrights",
      method = RequestMethod.POST,
      headers = "Accept=application/json")
  @PreAuthorize("hasRight('DEPOT_DELIB_C')")
  public ResponseEntity<java.lang.String> uploadDelibRights() {
    return RemocraController.DUMMY_RESPONSE;
  }

  @RequestMapping(
      value = "/declahydrantrights",
      method = RequestMethod.POST,
      headers = "Accept=application/json")
  @PreAuthorize("hasRight('DEPOT_DECLAHYDRANT_C')")
  public ResponseEntity<java.lang.String> uploadDeclaHydrantRights() {
    return RemocraController.DUMMY_RESPONSE;
  }

  @RequestMapping(
      value = "/receptravauxrights",
      method = RequestMethod.POST,
      headers = "Accept=application/json")
  @PreAuthorize("hasRight('DEPOT_RECEPTRAVAUX_C')")
  public ResponseEntity<java.lang.String> uploadRecepTravauxRights() {
    return RemocraController.DUMMY_RESPONSE;
  }

  @RequestMapping(
      value = "/delib",
      method = RequestMethod.POST,
      headers = "Content-Type=multipart/form-data")
  @PreAuthorize("hasRight('DEPOT_DELIB_C')")
  public ResponseEntity<java.lang.String> uploadDelib(MultipartHttpServletRequest request) {
    return uploadDepot(request, TypeDocument.DELIB);
  }

  @RequestMapping(
      value = "/declahydrant",
      method = RequestMethod.POST,
      headers = "Content-Type=multipart/form-data")
  @PreAuthorize("hasRight('DEPOT_DECLAHYDRANT_C')")
  public ResponseEntity<java.lang.String> uploadDeclaHydrant(MultipartHttpServletRequest request) {
    return uploadDepot(request, TypeDocument.DECLAHYDRANT);
  }

  @RequestMapping(
      value = "/receptravaux",
      method = RequestMethod.POST,
      headers = "Content-Type=multipart/form-data")
  @PreAuthorize("hasRight('DEPOT_RECEPTRAVAUX_C')")
  public ResponseEntity<java.lang.String> uploadRecepTravaux(MultipartHttpServletRequest request) {
    return uploadDepot(request, TypeDocument.RECEPTRAVAUX);
  }

  protected ResponseEntity<java.lang.String> uploadDepot(
      MultipartHttpServletRequest request, TypeDocument typeDocument) {
    try {
      // Récupération des paramètres
      MultipartFile mf = request.getFile("depotUpload");

      // Enregistrement et envoi d'un email si nécessaire
      saveDepotFile(mf, typeDocument);

      // Réponse
      String fichier = mf.getOriginalFilename();
      return new SuccessErrorExtSerializer(
              true,
              "Le fichier" + (fichier == null ? "" : " '" + fichier + "'") + " a bien été déposé.")
          .serialize();

    } catch (Exception e) {
      e.printStackTrace();
      return new SuccessErrorExtSerializer(false, e.getMessage()).serialize();
    }
  }

  protected void saveDepotFile(MultipartFile mf, TypeDocument typeDocument) throws Exception {

    String depotRepertoire = null;
    if (typeDocument == TypeDocument.DELIB) {
      depotRepertoire = parametreProvider.get().getDossierDepotDelib();
    } else if (typeDocument == TypeDocument.DECLAHYDRANT) {
      depotRepertoire = parametreProvider.get().getDossierDepotDeclaHydrant();
    } else if (typeDocument == TypeDocument.RECEPTRAVAUX) {
      depotRepertoire = parametreProvider.get().getDossierDepotRecepTravaux();
    }

    if (depotRepertoire == null || depotRepertoire.isEmpty()) {
      logger.error("Depôt " + typeDocument + " : dossier non renseigné");
      throw new Exception("Dossier " + typeDocument + " non renseigné");
    }

    // Document "générique"
    Document d = createNonPersistedDocument(typeDocument, mf, depotRepertoire);

    DepotDocument dd = new DepotDocument();
    dd.setUtilisateur(utilisateurService.getCurrentUtilisateur());
    dd.setDocument(d);

    d.persist();
    dd.persist();

    String nomOrganisme = dd.getUtilisateur().getOrganisme().getNom();

    // Email
    String emailDestinataire = null;
    EmailModele emailModele = null;
    if (typeDocument == TypeDocument.DELIB) {
      emailDestinataire = parametreProvider.get().getEmailDepotDelib();
      emailModele = EmailModele.findByValue(EmailModeleEnum.DEPOT_DELIB);
    } else if (typeDocument == TypeDocument.DECLAHYDRANT) {
      emailDestinataire = parametreProvider.get().getEmailDepotDeclaHydrant();
      emailModele = EmailModele.findByValue(EmailModeleEnum.DEPOT_DECLAHYDRANT);
    } else if (typeDocument == TypeDocument.RECEPTRAVAUX) {
      emailDestinataire = parametreProvider.get().getEmailDepotRecepTravaux();
      emailModele = EmailModele.findByValue(EmailModeleEnum.DEPOT_RECEPTRAVAUX);
    }

    // Email de confirmation si toutes les info sont présentes en entrée
    if (emailDestinataire == null) {
      logger.debug("Depôt " + typeDocument + " : destinataire non renseigné. Pas d'envoi d'email");
    } else if (emailModele == null
        || emailModele.getObjet() == null
        || emailModele.getObjet().startsWith("TEMP_OBJET")) {
      logger.debug(
          "Depôt " + typeDocument + " : modèle d'email non renseigné. Pas d'envoi d'email");
    } else {
      mailUtils.envoiEmailWithModele(
          emailModele,
          getSystemUtilisateur(),
          emailDestinataire,
          EmailModele.emptyKeyMap()
              .add(EmailModeleKeys.URL_SITE, parametreProvider.get().getUrlSite())
              .add(EmailModeleKeys.CODE, d.getCode())
              .add(EmailModeleKeys.NOM_ORGANISME, nomOrganisme));
    }
  }

  public Utilisateur getSystemUtilisateur() throws BusinessException {
    Long sysUId = parametreProvider.get().getSystemUtilisateurId();
    if (sysUId == null) {
      BusinessException e = new BusinessException("L'utilisateur système n'a pas été paramétré");
      logger.error(e.getMessage(), e);
      throw e;
    }
    Utilisateur u = Utilisateur.findUtilisateur(sysUId);
    if (u == null) {
      BusinessException e = new BusinessException("L'utilisateur système n'a pas été trouvé");
      logger.error(e.getMessage(), e);
      throw e;
    }
    return u;
  }
}
