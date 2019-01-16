package fr.sdis83.remocra.web;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.vividsolutions.jts.geom.Geometry;

import fr.sdis83.remocra.domain.remocra.Organisme;
import fr.sdis83.remocra.exception.BusinessException;
import fr.sdis83.remocra.security.AuthoritiesUtil;
import fr.sdis83.remocra.service.AuthService;
import fr.sdis83.remocra.service.ParamConfService;
import fr.sdis83.remocra.service.UtilisateurService;
import fr.sdis83.remocra.util.GeometryUtil;
import fr.sdis83.remocra.web.serialize.AccessRightSerializer;
import fr.sdis83.remocra.web.serialize.ext.AbstractExtListSerializer;
import fr.sdis83.remocra.web.serialize.ext.SuccessErrorExtSerializer;

@RequestMapping("/")
@Controller
public class RemocraController {

    private final Logger log = Logger.getLogger(getClass());

    /**
     * Réponse "factice" en succès (partagée). Elle est utilisée par exemple
     * avant un POST de document pour vérifier les droits de l'utilisateur
     * connecté.
     */
    public static final ResponseEntity<java.lang.String> DUMMY_RESPONSE = new SuccessErrorExtSerializer(true, "").serialize();
    public static final ResponseEntity<java.lang.String> DUMMY_RESPONSE_LIST = AbstractExtListSerializer.getDummy().serialize();
    /**
     * MODE DEBUG ou PRODUCTION
     */
    // Automatiquement passé à false lors du packaging (ne pas toucher cette
    // ligne)
    private static boolean modeDebug = true;

    // Automatiquement défini lors du packaging (ne pas toucher cette ligne)
    public static String REVISION_NUMBER = "";

    public static String VERSION_NUMBER = "0.11.1";

    @Autowired
    private AuthoritiesUtil authUtils;

    @Autowired
    private ParamConfService paramConfService;

    @Autowired
    private UtilisateurService utilisateurService;

    @PersistenceContext
    private EntityManager entityManager;

    @RequestMapping()
    public String show(HttpServletRequest request, Model model) {

        String serialRights = new AccessRightSerializer().serialize(authUtils.getCurrentRights());
        log.debug("Loading with Rights : " + serialRights);
        model.addAttribute("userRights", serialRights);

        // Emprise du territoire de compétence de l'utilisateur
        String bounds = null;

        // Récupération du code du profil de l'utilisateur connecté
        String userProfilDroit = null;
        String nomOrganisme = null;
        try {
            if (AuthService.isUserAuthenticated()) {
                userProfilDroit = utilisateurService.getCurrentProfilDroit().getCode();
                Organisme organisme = utilisateurService.getCurrentUtilisateur().getOrganisme();
                Geometry territoire = organisme.getZoneCompetence().getGeometrie();
                bounds = GeometryUtil.bboxFromGeometry(territoire);
                nomOrganisme = organisme.getNom().replace("'", "\\'");
            }
        } catch (BusinessException e) {
            log.debug("BusinessException lors de la récupération du code du profil de droits de l'utilisateur");
        }
        model.addAttribute("userProfilDroit", userProfilDroit);
        model.addAttribute("organisme", nomOrganisme);

        // Emprise par défaut
        model.addAttribute("initBounds", bounds == null ? "EPSG:900913;523593.64368054,5303506.7698006,849521.13224316,5486955.6376594"/* Var */: bounds);

        model.addAttribute("url_site", paramConfService.getUrlSite() == null ? "http://localhost:8080/remocra/" : paramConfService.getUrlSite());
        // Structure ou Chaine de caractères
        String clesIgn = paramConfService.getClesIgn();
        model.addAttribute("clesIgn", clesIgn.contains("{") ? clesIgn : "'" + clesIgn + "'");

        // Information mode debug en cours
        model.addAttribute("modeDebug", modeDebug);
        model.addAttribute("versionNumber", VERSION_NUMBER);
        model.addAttribute("revisionNumber", REVISION_NUMBER);
        // Numéro de patch (max) : requête native pour performance
        model.addAttribute("patchNumber", entityManager.createNativeQuery("select max(numero) from remocra.suivi_patches").getSingleResult().toString());

        // Afficher un message en haut de la page
        model.addAttribute("message_entete", paramConfService.getMessageEntete());
        model.addAttribute("titre_page", paramConfService.getTitrePage());

        // Afficher un message en haut de la page
        model.addAttribute("mention_cnil", paramConfService.getMentionCnil());

        model.addAttribute("hydrant_cfg", paramConfService.getHydrantCfg());
        model.addAttribute("hydrant_symbologie", paramConfService.getHydrantSymbologieMethode());

        model.addAttribute("coordonnees_format_affichage", paramConfService.getCoordonneesFormatAffichage());

        // Durée d'inactivité permis en secondes
        model.addAttribute("maxInactiveIntervalSec", request.getSession().getMaxInactiveInterval());

        // Orientations par défaut
        model.addAttribute("orientationX", paramConfService.getDefaultOrientationX());
        model.addAttribute("orientationY", paramConfService.getDefaultOrientationY());

        // Mode de visite des hydrants
        model.addAttribute("hydrant_visite_rapide", paramConfService.getHydrantVisiteRapide());

        return "remocra";
    }

    /**
     * Passage au mode "debug" : fichiers js non minifiés, chargement à la
     * volée, etc.
     * 
     * @return
     */
    @RequestMapping(value = "modedebug")
    public String tomodedebug(HttpServletRequest request, Model model) {
        modeDebug = true;
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
        modeDebug = false;
        // Plutôt que de passer par une redirection, on choisit de conserver le
        // comportement "INDEX"
        return this.show(request, model);
    }

    @RequestMapping(value = "dummy")
    public ResponseEntity<java.lang.String> dummy() {
        return DUMMY_RESPONSE;
    }
}
