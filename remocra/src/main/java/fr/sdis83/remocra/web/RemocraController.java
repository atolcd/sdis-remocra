package fr.sdis83.remocra.web;

import com.vividsolutions.jts.geom.Geometry;
import flexjson.JSONSerializer;
import fr.sdis83.remocra.GlobalConstants;
import fr.sdis83.remocra.domain.remocra.Organisme;
import fr.sdis83.remocra.domain.remocra.TypeDroit;
import fr.sdis83.remocra.exception.BusinessException;
import fr.sdis83.remocra.repository.ParametreRepository;
import fr.sdis83.remocra.security.AuthoritiesUtil;
import fr.sdis83.remocra.service.AuthService;
import fr.sdis83.remocra.service.HydrantService;
import fr.sdis83.remocra.service.UtilisateurService;
import fr.sdis83.remocra.usecase.parametre.ParametreDataProvider;
import fr.sdis83.remocra.util.GeometryUtil;
import fr.sdis83.remocra.web.serialize.AccessRightSerializer;
import fr.sdis83.remocra.web.serialize.ext.AbstractExtListSerializer;
import fr.sdis83.remocra.web.serialize.ext.SuccessErrorExtSerializer;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.jar.Attributes;
import java.util.jar.Manifest;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.Months;
import org.joda.time.Period;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@RequestMapping("/")
@Controller
public class RemocraController {

  private final Logger log = Logger.getLogger(getClass());

  /**
   * Réponse "factice" en succès (partagée). Elle est utilisée par exemple avant un POST de document
   * pour vérifier les droits de l'utilisateur connecté.
   */
  public static final ResponseEntity<java.lang.String> DUMMY_RESPONSE =
      new SuccessErrorExtSerializer(true, "").serialize();

  public static final ResponseEntity<java.lang.String> DUMMY_RESPONSE_LIST =
      AbstractExtListSerializer.getDummy().serialize();

  @Autowired ServletContext servletContext;

  @Autowired private AuthoritiesUtil authUtils;

  @Autowired private ParametreDataProvider parametreProvider;

  @Autowired private UtilisateurService utilisateurService;
  @Autowired private ParametreRepository parametreRepository;

  @Autowired private HydrantService hydrantService;

  @PersistenceContext private EntityManager entityManager;

  @RequestMapping()
  public String show(HttpServletRequest request, Model model) {
    String serialRights = new AccessRightSerializer().serialize(authUtils.getCurrentRights());
    log.debug("Loading with Rights : " + serialRights);
    model.addAttribute("userRights", serialRights);

    // Emprise du territoire de compétence de l'utilisateur
    String bounds = null;
    Geometry territoire = null;

    // Récupération du code du profil de l'utilisateur connecté
    String userProfilDroit = null;
    String nomOrganisme = null;
    try {
      if (AuthService.isUserAuthenticated()) {
        userProfilDroit = utilisateurService.getCurrentProfilDroit().getCode();
        Organisme organisme = utilisateurService.getCurrentUtilisateur().getOrganisme();
        territoire = organisme.getZoneCompetence().getGeometrie();
        bounds = GeometryUtil.bboxFromGeometry(territoire, parametreProvider.get().getSridInt());
        nomOrganisme = organisme.getNom().replace("'", "\\'");
      }
    } catch (BusinessException e) {
      log.debug(
          "BusinessException lors de la récupération du code du profil de droits de l'utilisateur");
    }
    model.addAttribute("userProfilDroit", userProfilDroit);
    model.addAttribute("organisme", nomOrganisme);

    // Emprise par défaut
    String centrageDefaut;
    if (bounds == null) {
      // Centrage sur la BBOX saisie au niveau paramètre, sinon fallback sur le Var
      if (parametreProvider.get().getCentrageGrandPublic() != null) {
        centrageDefaut = parametreProvider.get().getCentrageGrandPublic();
      } else {
        centrageDefaut =
            "EPSG:900913;523593.64368054,5303506.7698006,849521.13224316,5486955.6376594";
      }
    } else {
      centrageDefaut = bounds;
    }
    model.addAttribute("initBounds", centrageDefaut);

    model.addAttribute(
        "url_site",
        parametreProvider.get().getUrlSite() == null
            ? "http://localhost:8080/remocra/"
            : parametreProvider.get().getUrlSite());
    // Structure ou Chaine de caractères
    String clesIgn = parametreProvider.get().getClesIgn();
    model.addAttribute("clesIgn", clesIgn.contains("{") ? clesIgn : "'" + clesIgn + "'");

    // Information mode debug en cours
    model.addAttribute("modeDebug", isModeDebug());
    model.addAttribute("versionNumber", getProjectVersion());
    model.addAttribute("revisionNumber", getRevisionNumber());
    // Numéro de patch (max) : requête native pour performance
    model.addAttribute(
        "patchNumber",
        entityManager
            .createNativeQuery("select max(numero) from remocra.suivi_patches")
            .getSingleResult()
            .toString());

    // Afficher un message en haut de la page
    model.addAttribute("message_entete", parametreProvider.get().getMessageEntete());
    model.addAttribute("titre_page", parametreProvider.get().getTitrePage());

    // Afficher un message en haut de la page
    model.addAttribute("mention_cnil", parametreProvider.get().getMentionCnil());

    model.addAttribute("hydrant_cfg", parametreProvider.get().getHydrantCfg());
    model.addAttribute("hydrant_symbologie", parametreProvider.get().getHydrantSymbologieMethode());

    model.addAttribute(
        "coordonnees_format_affichage", parametreProvider.get().getCoordonneesFormatAffichage());

    // Orientations par défaut
    model.addAttribute("orientationX", parametreProvider.get().getDefaultOrientationX());
    model.addAttribute("orientationY", parametreProvider.get().getDefaultOrientationY());

    // Mode de visite des hydrants
    model.addAttribute("hydrant_visite_rapide", parametreProvider.get().getHydrantVisiteRapide());

    // Durée de mise en évidence lors de la localisation
    model.addAttribute(
        "hydrant_highlight_duree", parametreProvider.get().getHydrantHighlightDuree());

    // Paramétrage des colonnes du tableau de suivi des PEI
    model.addAttribute(
        "hydrant_colonnes",
        (new JSONSerializer()).serialize(parametreProvider.get().getHydrantColonnes()));

    // Autoriser le zoom sur les adresses adresse.data.gouv.fr
    model.addAttribute("hydrant_zoom_numero", parametreProvider.get().getHydrantZoomNumero());

    model.addAttribute(
        "hydrant_deplacement_dist_warn", parametreProvider.get().getHydrantDeplacementDistWarn());

    model.addAttribute(
        "hydrant_generation_carte_tournee",
        parametreProvider.get().getHydrantGenerationCarteTournee());

    // Définit la complexité du mot de passe
    model.addAttribute("complexite_password", parametreProvider.get().getComplexitePassword());

    // Vitesse de l'eau dans le réseau retenue par le SDIS
    model.addAttribute("vitesse_eau", parametreProvider.get().getVitesseEau());

    model.addAttribute("show_historique", parametreProvider.get().getHydrantNombreHistorique() > 0);

    model.addAttribute("srid", parametreProvider.get().getSridInt());

    model.addAttribute(
        "buffer_carte", parametreProvider.get().getValeur(GlobalConstants.CLE_BUFFER_CARTE));

    return "remocra";
  }

  /**
   * Passage au mode "debug" : fichiers js non minifiés, chargement à la volée, etc.
   *
   * @return
   */
  @RequestMapping(value = "modedebug")
  public String tomodedebug(HttpServletRequest request, Model model) {
    isModeDebug = true;
    // Plutôt que de passer par une redirection, on choisit de conserver le
    // comportement "INDEX"
    return this.show(request, model);
  }

  /**
   * Passage au mode "debug" : fichiers js minifiés
   *
   * @return
   */
  @RequestMapping(value = "modeinfo")
  public String tomodeinfo(HttpServletRequest request, Model model) {
    isModeDebug = false;
    // Plutôt que de passer par une redirection, on choisit de conserver le
    // comportement "INDEX"
    return this.show(request, model);
  }

  @RequestMapping(value = "dummy")
  public ResponseEntity<java.lang.String> dummy() {
    return DUMMY_RESPONSE;
  }

  // Mode debug par défaut si le manifest est absent
  private Boolean isModeDebug = null;

  public boolean isModeDebug() {
    if (isModeDebug == null) {
      String filePath = servletContext.getRealPath("/META-INF/MANIFEST.MF");
      isModeDebug = !new File(filePath).exists();
    }
    return isModeDebug;
  }

  // Version : 0.12.3
  String projectVersion = null;

  public String getProjectVersion() {
    if (projectVersion == null) {
      projectVersion = getManifestInfo("Project-Version");
    }
    return projectVersion;
  }

  // Numéro de commit court : 969f7
  String revisionNumber = null;

  public String getRevisionNumber() {
    if (revisionNumber == null) {
      revisionNumber = getManifestInfo("Revision-Number");
    }
    return revisionNumber;
  }

  protected String getManifestInfo(String key) {
    try {
      String filePath = servletContext.getRealPath("/META-INF/MANIFEST.MF");
      InputStream input = new FileInputStream(filePath);
      if (input == null) {
        return "";
      }
      Manifest manifest = new Manifest(input);
      if (manifest != null) {
        Attributes mainAttributes = manifest.getMainAttributes();
        if (mainAttributes != null) {
          String returned = mainAttributes.getValue(key);
          if (returned == null) {
            return "";
          }
          return returned;
        }
      }
    } catch (IOException e) {
      //
    }
    return "";
  }

  /**
   * Vérifie si les conditions sont réunies pour l'affichage d'un message à la connexion de
   * l'utilisateur
   */
  @RequestMapping(
      value = "/checkMessage",
      method = RequestMethod.GET,
      headers = "Accept=application/json")
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<java.lang.String> checkMessage(final @RequestBody String json) {
    // PEIs indisponibles depuis trop longtemps
    if (authUtils.hasRight(TypeDroit.TypeDroitEnum.HYDRANTS_R)
        && parametreProvider.get().getHydrantLongueIndisponibiliteJours() > 0
        && parametreProvider.get().getHydrantLongueIndisponibiliteMessage().trim().length() > 0) {

      String codeOrganisme =
          utilisateurService.getCurrentUtilisateur().getOrganisme().getTypeOrganisme().getCode();

      if (codeOrganisme.matches(
          parametreProvider.get().getHydrantLongueIndisponibiliteTypeOrganisme())) {
        ArrayList<String> peis = hydrantService.checkHydrantsDureeIndispo();
        if (peis.size() > 0) {
          Integer nbJoursIndispoMax =
              parametreProvider.get().getHydrantLongueIndisponibiliteJours();
          DateTime today = new DateTime();
          DateTime limitDate = today.minus(Period.days(nbJoursIndispoMax));
          int nbMonths = Months.monthsBetween(limitDate, today).getMonths();

          limitDate = limitDate.plus(Period.months(nbMonths));
          int nbJours = Days.daysBetween(limitDate, today).getDays();

          StringBuffer sb =
              new StringBuffer("<div class=\"listHydrant\">")
                  .append(
                      parametreProvider
                          .get()
                          .getHydrantLongueIndisponibiliteMessage()
                          .replace("%MOIS%", String.valueOf(nbMonths))
                          .replace("%JOURS%", String.valueOf(nbJours)));

          for (String s : peis) {
            sb.append("<li>").append(s).append("</li>");
          }
          sb.append("</div>");
          return new SuccessErrorExtSerializer(true, sb.toString()).serialize();
        }
      }
    }
    return new SuccessErrorExtSerializer(true, null).serialize();
  }
}
