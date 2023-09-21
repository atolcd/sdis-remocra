package fr.sdis83.remocra.web;

import flexjson.JSONSerializer;
import fr.sdis83.remocra.domain.pdi.ModeleTraitement;
import fr.sdis83.remocra.domain.pdi.ModeleTraitementParametre;
import fr.sdis83.remocra.domain.pdi.PdiVueCombo;
import fr.sdis83.remocra.domain.pdi.Traitement;
import fr.sdis83.remocra.domain.pdi.TraitementParametre;
import fr.sdis83.remocra.domain.remocra.Thematique.ThematiqueEnum;
import fr.sdis83.remocra.domain.remocra.TypeDroit.TypeDroitEnum;
import fr.sdis83.remocra.domain.remocra.Utilisateur;
import fr.sdis83.remocra.exception.BusinessException;
import fr.sdis83.remocra.repository.RequeteModeleRepository;
import fr.sdis83.remocra.security.AuthoritiesUtil;
import fr.sdis83.remocra.service.ParamConfService;
import fr.sdis83.remocra.service.TraitementsService;
import fr.sdis83.remocra.service.UtilisateurService;
import fr.sdis83.remocra.web.message.ItemFilter;
import fr.sdis83.remocra.web.serialize.ext.AbstractExtListSerializer;
import fr.sdis83.remocra.web.serialize.ext.SuccessErrorExtSerializer;
import java.io.File;
import java.util.List;
import java.util.Map;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

/**
 * Gère le module des traitements
 *
 * @author api
 */
@RequestMapping("/traitements")
@Controller
public class TraitementsController {

  @PersistenceContext private EntityManager entityManager;

  @Autowired private TraitementsService traitementsService;

  @Autowired private ParamConfService paramConfService;

  @Autowired private UtilisateurService utilisateurService;

  @Autowired private AuthoritiesUtil authUtils;

  @Autowired private RequeteModeleRepository requeteModeleRepository;

  /**
   * Vérifie que l'utilisateur a le droit d'accéder à la brique Traitements pour la thématique. Tout
   * traitement hors spécifique (puis les traitements dits spécifiques sont là pour outrepasser ça
   * justement).
   *
   * @param thematiqueId La thématique concernée par le test.
   * @throws AccessDeniedException Si l'utilisateur n'a pas les accès attendus.
   */
  protected void checkRightsForThematique(int thematiqueId) throws AccessDeniedException {
    if (authUtils.hasRight(TypeDroitEnum.TRAITEMENTS_C)) {
      // Accès à tous les traitements
      return;
    }
    // On fonction de la thématique, on laisse passer ou non
    if (thematiqueId == ThematiqueEnum.POINTDEAU.getValue()) {
      if (authUtils.hasRight(TypeDroitEnum.HYDRANTS_TRAITEMENT_C)) {
        // Accès à la thématique HYDRANTS
        return;
      }
    } else if (thematiqueId == ThematiqueEnum.PERMIS.getValue()) {
      if (authUtils.hasRight(TypeDroitEnum.PERMIS_TRAITEMENT_C)) {
        // Accès à la thématique PERMIS_TRAITEMENT
        return;
      }
    } else if (thematiqueId == ThematiqueEnum.RCI.getValue()) {
      if (authUtils.hasRight(TypeDroitEnum.RCI_C)) {
        // Accès à la thématique RCI
        return;
      }
    } else if (thematiqueId == ThematiqueEnum.OLD.getValue()) {
      if (authUtils.hasRight(TypeDroitEnum.OLDEB_R)) {
        // Accès à la thématique OLDEB
        return;
      }
    }
    // Autre thématique : aucun accès
    throw new AccessDeniedException("Accès insuffisant");
  }

  /**
   * Vérifie que l'utilisateur a au moins un accès qui est censé le laisser accéder à la brique des
   * traitements. Tout traitement hors spécifique (puis les traitements dits spécifiques sont là
   * pour outrepasser ça justement).
   *
   * @throws AccessDeniedException Si l'utilisateur n'a pas les accès attendus.
   */
  protected void checkRightsAtLeastOne() throws AccessDeniedException {
    if (authUtils.hasRight(TypeDroitEnum.TRAITEMENTS_C)
        || authUtils.hasRight(TypeDroitEnum.HYDRANTS_TRAITEMENT_C)
        || authUtils.hasRight(TypeDroitEnum.PERMIS_TRAITEMENT_C)
        || authUtils.hasRight(TypeDroitEnum.RCI_C)
        || authUtils.hasRight(TypeDroitEnum.OLDEB_R)) {
      return;
    }
    throw new AccessDeniedException("Accès insuffisant");
  }

  /**
   * Récupère les modèles de traitement
   *
   * @param id
   * @return
   */
  @RequestMapping(
      value = "modelestraitement/{id}",
      method = RequestMethod.GET,
      headers = "Accept=application/json")
  // @PreAuthorize : tests internes pour gérer les spécificités
  public ResponseEntity<java.lang.String> getModeles(
      final @PathVariable("id") Long id,
      @RequestParam(value = "filter", required = false) String filters) {

    List<ItemFilter> itemFilterList = null;
    ItemFilter filtreCode = null;

    if (filters != null && !filters.equals("")) {
      itemFilterList = ItemFilter.decodeJson(filters);
      filtreCode = itemFilterList.get(0);
    }

    final Long code =
        filtreCode != null && !filtreCode.getValue().equals("")
            ? Long.valueOf(filtreCode.getValue())
            : null;

    return new AbstractExtListSerializer<ModeleTraitement>("Modeles Traitement retrieved.") {
      @Override
      protected JSONSerializer additionnalIncludeExclude(JSONSerializer serializer) {
        return serializer
            .include("data.idmodele")
            .include("data.description")
            .exclude("data.code")
            .exclude("data.ref_chemin")
            .exclude("data.ref_nom")
            .exclude("data.type")
            .exclude("data.version")
            .exclude("data.message_echec")
            .exclude("data.message_succes");
      }

      @Override
      protected List<ModeleTraitement> getRecords() {
        // Traitements : par ordre de priorité : id de l'URL, code sous
        // la forme d'un filter, AUCUN
        Long codeTraitement = id != null ? id : code;
        if (codeTraitement != null) {
          int intCodeTraitement = codeTraitement.intValue();
          checkRightsForThematique(intCodeTraitement);
          return ModeleTraitement.findModeleTraitementsByCode(intCodeTraitement).getResultList();
        }
        if (!authUtils.hasRight(TypeDroitEnum.TRAITEMENTS_C)) {
          // Pas accès global : on interdit
          throw new AccessDeniedException("Accès insuffisant");
        }
        return ModeleTraitement.findAllModeleTraitements();
      }
    }.serialize();
  }

  /**
   * Récupère les paramètres d'un modèle de traitement
   *
   * @param idModele
   * @return
   */
  @RequestMapping(
      value = "modeletraitementparam/{id}",
      method = RequestMethod.GET,
      headers = "Accept=application/json")
  // @PreAuthorize : tests internes pour gérer les spécificités
  public ResponseEntity<java.lang.String> getParamModeles(
      final @PathVariable("id") Integer idModele) {

    return new AbstractExtListSerializer<ModeleTraitementParametre>(
        "Paramètres Modeles Traitement retrieved.") {

      @Override
      protected JSONSerializer additionnalIncludeExclude(JSONSerializer serializer) {
        return serializer.exclude("data.idmodele");
      }

      @SuppressWarnings("unchecked")
      @Override
      protected List<ModeleTraitementParametre> getRecords() {

        ModeleTraitement modeleTraitement = ModeleTraitement.findModeleTraitement(idModele);
        checkRightsForThematique(modeleTraitement.getCode().intValue());

        // order by o.form_num_ordre
        Query query =
            entityManager.createQuery(
                "Select o from ModeleTraitementParametre o where o.idmodele = :idmodele  order by o.formNumOrdre");
        query.setParameter("idmodele", modeleTraitement);

        return query.getResultList();
      }
    }.serialize();
  }

  @RequestMapping(
      value = "/{idmodele}",
      method = RequestMethod.POST,
      headers = "Accept=application/json")
  // @PreAuthorize : tests internes pour gérer les spécificités
  public ResponseEntity<java.lang.String> createTraitement(
      final @PathVariable("idmodele") Integer idmodele,
      final @RequestParam("jsonValeurs") String json,
      HttpServletRequest request)
      throws BusinessException {

    ModeleTraitement modeleTraitement = ModeleTraitement.findModeleTraitement(idmodele);
    checkRightsForThematique(modeleTraitement.getCode().intValue());

    return doCreateTraitement(idmodele, json, request);
  }

  @RequestMapping(
      value = "/requetage",
      method = RequestMethod.GET,
      headers = "Accept=application/json")
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<java.lang.String> createTraitement(HttpServletRequest request)
      throws BusinessException {

    Integer idModeleRequetage = paramConfService.getIdTraitementRequetage();
    ModeleTraitement modele = ModeleTraitement.findModeleTraitement(idModeleRequetage);
    // checkRightsForThematique(modele.getCode().intValue());

    Traitement trt = new Traitement();
    trt.setIdtraitement(-1);
    TraitementParametre[] paramArray = new TraitementParametre[2];

    TraitementParametre tpSpatial = new TraitementParametre();
    tpSpatial.setIdparametre(
        ModeleTraitementParametre.findModeleTraitementParametresByNomAndIdmodele("SPATIAL", modele)
            .getSingleResult());
    tpSpatial.setIdtraitement(trt);
    tpSpatial.setValeur(String.valueOf(request.getParameter("spatial")));
    paramArray[0] = tpSpatial;

    String sourceSql =
        requeteModeleRepository.getSourceSqlFromSelection(
            Long.valueOf(request.getParameter("requete")));
    TraitementParametre tpRequete = new TraitementParametre();
    tpRequete.setIdparametre(
        ModeleTraitementParametre.findModeleTraitementParametresByNomAndIdmodele("REQUETE", modele)
            .getSingleResult());
    tpRequete.setIdtraitement(trt);
    tpRequete.setValeur(sourceSql);
    paramArray[1] = tpRequete;

    String jsonValeurs =
        new JSONSerializer()
            .include("idparametre.idparametre")
            .include("idtraitement.idtraitement")
            .include("valeur")
            .exclude("*")
            .serialize(paramArray);
    return doCreateTraitement(modele.getIdmodele(), jsonValeurs, request);
  }

  /**
   * Sauvegarde en base un traitement
   *
   * @param idmodele : modèle du traitement
   * @param json : paramètres du traitement associés aux valeurs saisies
   * @param request : Requête Http
   * @return
   * @throws BusinessException
   */
  public ResponseEntity<java.lang.String> doCreateTraitement(
      final @PathVariable("idmodele") Integer idmodele,
      final @RequestParam("jsonValeurs") String json,
      HttpServletRequest request)
      throws BusinessException {

    try {

      Traitement newTraitement = traitementsService.createTraitement(idmodele, json);

      if (request instanceof MultipartHttpServletRequest) {
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;

        if (multipartRequest.getFileNames() != null) {
          // Répertoire "depots PDI"

          String basePath = paramConfService.getDossierDepotPdi();

          // Répertoire "depots PDI" du traitement
          String lpadDirname = String.format("%010d", newTraitement.getIdtraitement());
          String uploadDirPath = basePath + File.separator + lpadDirname;

          File depotDir = new File(uploadDirPath);
          if (!depotDir.exists()) {
            // Créer le répertoire
            if (!depotDir.mkdir()) {
              throw new SecurityException("Impossible de créer le répertoire " + uploadDirPath);
            }
          }

          Map<String, MultipartFile> mapFile = ((MultipartHttpServletRequest) request).getFileMap();

          for (Map.Entry<String, MultipartFile> entry : mapFile.entrySet()) {
            CommonsMultipartFile file = (CommonsMultipartFile) entry.getValue();
            // Création du fichier sur disque
            String targetFilePath = depotDir + File.separator + file.getOriginalFilename();
            File targetTmpFile =
                new File(depotDir + File.separator + file.getOriginalFilename() + ".tmp");
            if (depotDir.canWrite()) {
              // Copie dans un fichier temporaire
              file.transferTo(targetTmpFile);
              // Fichier définitif : on retire le .tmp
              targetTmpFile.renameTo(new File(targetFilePath));
            } else {
              throw new SecurityException("Impossible de créer le fichier " + targetFilePath);
            }
          }
        }
      }

      if (newTraitement != null) {
        return new SuccessErrorExtSerializer(true, "Traitement créé avec succès !").serialize();
      } else {
        return new SuccessErrorExtSerializer(false, "Problème lors de la création du Traitement !")
            .serialize();
      }

    } catch (Exception e) {
      e.printStackTrace();
      return new SuccessErrorExtSerializer(false, e.getMessage()).serialize();
    }
  }

  /**
   * Retourne la liste des valeurs pour un paramètre de type 'combo'. Si query est fourni, une
   * requête de type LIKE est réalisée sur le libellé. Vérifier si la vue existe bien en base.
   *
   * @param nomvue
   * @param query
   * @return
   */
  @RequestMapping(
      value = "modtrtparalst/{nomvue}",
      method = RequestMethod.GET,
      headers = "Accept=application/json")
  // @PreAuthorize : tests internes pour gérer les spécificités
  public ResponseEntity<java.lang.String> getListComboModeleTraitementLike(
      final @PathVariable("nomvue") String nomvue,
      final @RequestParam(value = "query", required = false) String query) {

    // Ici, on teste si l'utilisateur a au moins un accès parmis les
    // traitements de l'interface de traitements
    checkRightsAtLeastOne();

    return new AbstractExtListSerializer<PdiVueCombo>(nomvue + " retrieved.") {

      @Override
      protected List<PdiVueCombo> getRecords() {
        return traitementsService.getComboValues(nomvue, query);
      }
    }.serialize();
  }

  /**
   * Sauvegarde en base une demande de téléchargement de donnée "métadonnées"
   *
   * @param request : Requête Http
   * @return
   * @throws BusinessException
   */
  @RequestMapping(
      value = "/specifique/metadonnees/{codeexport}",
      method = RequestMethod.GET,
      headers = "Accept=application/json")
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<java.lang.String> createTraitementSpecifiqueMetadonnees(
      HttpServletRequest request, final @PathVariable("codeexport") String codeExport)
      throws BusinessException {

    // Le chemin/nom va permettre de trouver le traitement correspondant
    String ref_chemin = "/demandes/export";
    String ref_nom = "exporter_donnees";
    ModeleTraitement modele = null;
    try {
      modele = ModeleTraitement.findModeleTraitementParCheminEtNom(ref_chemin, ref_nom);
    } catch (Exception e) {
      // Le problème peut être du au fait qu'aucun traitement n'est trouvé, qu'il existe plusieurs
      // traitement pour le même chemin/nom...
      return new SuccessErrorExtSerializer(false, "Problème lors de la création du Traitement !")
          .serialize();
    }

    // Il y a deux paramètres à passer pour exporter les données d'une
    // métadonnée
    TraitementParametre[] paramArray = new TraitementParametre[2];

    // MODELE_EXPORT_ID
    Long idExportModele = traitementsService.getExportModeleFromCode(codeExport).getId();

    // ZONE_COMPETENCE_ID
    Long idZoneCompetence = utilisateurService.getCurrentZoneCompetenceId();

    // Premier paramètre = id du modèle d'export

    // obligatoire pour éviter "Not-null property references a transient value"
    Traitement trt = new Traitement();
    trt.setIdtraitement(-1);

    TraitementParametre tpModeleExport = new TraitementParametre();
    tpModeleExport.setIdparametre(
        ModeleTraitementParametre.findModeleTraitementParametresByNomAndIdmodele(
                "MODELE_EXPORT_ID", modele)
            .getSingleResult());
    tpModeleExport.setIdtraitement(trt);
    tpModeleExport.setValeur(idExportModele.toString());
    paramArray[0] = tpModeleExport;

    // Deuxième paramètre = id de la zone de compétence

    TraitementParametre tpZoneCompetence = new TraitementParametre();
    tpZoneCompetence.setIdparametre(
        ModeleTraitementParametre.findModeleTraitementParametresByNomAndIdmodele(
                "ZONE_COMPETENCE_ID", modele)
            .getSingleResult());
    tpZoneCompetence.setIdtraitement(trt);
    tpZoneCompetence.setValeur(idZoneCompetence.toString());
    paramArray[1] = tpZoneCompetence;

    String jsonValeurs =
        new JSONSerializer()
            .include("idparametre.idparametre")
            .include("idtraitement.idtraitement")
            .include("valeur")
            .exclude("*")
            .serialize(paramArray);
    return doCreateTraitement(modele.getIdmodele(), jsonValeurs, request);
  }

  /**
   * Sauvegarde en base une demande de traitement "Atlas"
   *
   * @param request : Requête Http
   * @return
   * @throws BusinessException
   */
  @RequestMapping(
      value = "/specifique/atlas",
      method = RequestMethod.GET,
      headers = "Accept=application/json")
  @PreAuthorize("hasRight('DFCI_R')")
  public ResponseEntity<java.lang.String> createTraitementSpecifiqueAtlas(
      HttpServletRequest request) throws BusinessException {

    // Id du traitement Atlas
    Integer idModeleAtlas = paramConfService.getIdTraitementAtlas();
    ModeleTraitement modele = ModeleTraitement.findModeleTraitement(idModeleAtlas);

    // Id de l'utilisateur
    Utilisateur utilisateur = utilisateurService.getCurrentUtilisateur();
    Long idUtilisateur = utilisateur.getId();

    TraitementParametre[] paramArray = new TraitementParametre[1];
    Traitement trt = new Traitement();
    trt.setIdtraitement(-1); // obligatoire pour éviter
    // "Not-null property references a transient value"

    TraitementParametre tp = new TraitementParametre();
    tp.setIdparametre(
        ModeleTraitementParametre.findModeleTraitementParametresByNomAndIdmodele(
                "LST_UTILISATEURS", modele)
            .getSingleResult());
    tp.setIdtraitement(trt);
    tp.setValeur(idUtilisateur.toString());
    paramArray[0] = tp;

    // Paramètres
    String jsonValeurs =
        new JSONSerializer()
            .include("idparametre.idparametre")
            .include("idtraitement.idtraitement")
            .include("valeur")
            .exclude("*")
            .serialize(paramArray);

    return doCreateTraitement(idModeleAtlas, jsonValeurs, request);
  }

  /**
   * Sauvegarde en base une demande de traitement "Oldeb"
   *
   * @param request : Requête Http
   * @return
   * @throws BusinessException
   */
  @RequestMapping(
      value = "/specifique/oldeb",
      method = RequestMethod.GET,
      headers = "Accept=application/json")
  @PreAuthorize("hasRight('OLDEB_R')")
  public ResponseEntity<java.lang.String> createTraitementSpecifiqueOldeb(
      HttpServletRequest request /*
                                       * , final @RequestParam(value =
                                       * "commune", required = false) Long
                                       * commune, final @RequestParam(value =
                                       * "section", required = false) String
                                       * section, final @RequestParam(value =
                                       * "parcelle", required = false) String
                                       * parcelle
                                       */) throws BusinessException {

    String commune = String.valueOf(request.getParameter("commune"));
    String section = request.getParameter("section");
    String parcelle = request.getParameter("parcelle");

    // Id du traitement Oldeb
    Integer idModeleOldeb = paramConfService.getIdTraitementOldeb();
    ModeleTraitement modele = ModeleTraitement.findModeleTraitement(idModeleOldeb);

    // Id de l'utilisateur
    /*
     * Utilisateur utilisateur = utilisateurService.getCurrentUtilisateur();
     * Long idUtilisateur = utilisateur.getId();
     */

    TraitementParametre[] paramArray = new TraitementParametre[3];
    Traitement trt = new Traitement();
    trt.setIdtraitement(-1); // obligatoire pour éviter
    // "Not-null property references a transient value"

    TraitementParametre tp = new TraitementParametre();
    // commune
    tp.setIdparametre(
        ModeleTraitementParametre.findModeleTraitementParametresByNomAndIdmodele(
                "COMMUNE_ID", modele)
            .getSingleResult());
    tp.setIdtraitement(trt);
    tp.setValeur(commune);
    paramArray[0] = tp;

    // section
    tp = new TraitementParametre();
    tp.setIdparametre(
        ModeleTraitementParametre.findModeleTraitementParametresByNomAndIdmodele(
                "NUM_SECTION", modele)
            .getSingleResult());
    tp.setIdtraitement(trt);
    tp.setValeur(section);
    paramArray[1] = tp;

    // parcelle
    tp = new TraitementParametre();
    tp.setIdparametre(
        ModeleTraitementParametre.findModeleTraitementParametresByNomAndIdmodele(
                "NUM_PARCELLE", modele)
            .getSingleResult());
    tp.setIdtraitement(trt);
    tp.setValeur(parcelle);
    paramArray[2] = tp;

    // Paramètres
    String jsonValeurs =
        new JSONSerializer()
            .include("idparametre.idparametre")
            .include("idtraitement.idtraitement")
            .include("valeur")
            .exclude("*")
            .serialize(paramArray);

    return doCreateTraitement(idModeleOldeb, jsonValeurs, request);
  }

  /**
   * Sauvegarde en base une demande de traitement "Purge KML"
   *
   * @param request : Requête Http
   * @return
   * @throws BusinessException
   */
  @RequestMapping(
      value = "/specifique/purgekml",
      method = RequestMethod.GET,
      headers = "Accept=application/json")
  @PreAuthorize("hasRight('RISQUES_KML_C')")
  public ResponseEntity<java.lang.String> createTraitementSpecifiquePurgeKml(
      HttpServletRequest request) throws BusinessException {

    // Id du traitement Purge Kml
    Integer idModelePurgeKml = paramConfService.getIdTraitementPurgeKml();

    // Paramètres
    String jsonValeurs = "[]";

    return doCreateTraitement(idModelePurgeKml, jsonValeurs, request);
  }

  /**
   * Sauvegarde en base une demande de traitement "Points d'eau à numéroter"
   *
   * @param request : Requête Http
   * @return
   * @throws BusinessException
   */
  @RequestMapping(
      value = "/specifique/hydrantsnonnum",
      method = RequestMethod.GET,
      headers = "Accept=application/json")
  @PreAuthorize(
      "hasRight('TRAITEMENTS_C') or hasRight('HYDRANTS_EXPORT_NON_NUM_C') or hasRight('HYDRANTS_TRAITEMENT_C')")
  public ResponseEntity<java.lang.String> createTraitementSpecifiqueHydrantsNonNum(
      HttpServletRequest request) throws BusinessException {

    // Id du traitement Hydrants à numéroter
    Integer idModeleHydrantsNonNum = paramConfService.getIdTraitementHydrantsNonNum();
    ModeleTraitement modele = ModeleTraitement.findModeleTraitement(idModeleHydrantsNonNum);

    // ORGANISME_CIS_ID
    String valeurIdParametreOrganismeCIS = "-1";
    Utilisateur utilisateur = utilisateurService.getCurrentUtilisateur();
    String codeTypeOrganisme = utilisateur.getOrganisme().getTypeOrganisme().getCode();
    if ("CIS".equals(codeTypeOrganisme)) {
      valeurIdParametreOrganismeCIS = utilisateur.getOrganisme().getId().toString();
    } else if (!"SDIS".equals(codeTypeOrganisme)) {
      throw new BusinessException("L'utilisateur n'est pas membre du SDIS ou d'un CIS");
    }

    TraitementParametre[] paramArray = new TraitementParametre[1];
    Traitement trt = new Traitement();
    trt.setIdtraitement(-1); // obligatoire pour éviter
    // "Not-null property references a transient value"

    // ORGANISME_CIS_ID
    TraitementParametre tp = new TraitementParametre();
    tp.setIdparametre(
        ModeleTraitementParametre.findModeleTraitementParametresByNomAndIdmodele(
                "ORGANISME_CIS_ID", modele)
            .getSingleResult());
    tp.setIdtraitement(trt);
    tp.setValeur(valeurIdParametreOrganismeCIS);
    paramArray[0] = tp;

    String jsonValeurs =
        new JSONSerializer()
            .include("idparametre.idparametre")
            .include("idtraitement.idtraitement")
            .include("valeur")
            .exclude("*")
            .serialize(paramArray);

    return doCreateTraitement(idModeleHydrantsNonNum, jsonValeurs, request);
  }

  /**
   * Sauvegarde en base une demande de traitement "Nombre d'alertes par utilisateur"
   *
   * @param request : Requête Http
   * @return
   * @throws BusinessException
   */
  @RequestMapping(
      value = "/specifique/nbalertesutilisateur",
      method = RequestMethod.GET,
      headers = "Accept=application/json")
  @PreAuthorize("hasRight('TRAITEMENTS_C') or hasRight('ALERTES_EXPORT_C')")
  public ResponseEntity<java.lang.String> createTraitementSpecifiqueAlertesUtilisateur(
      HttpServletRequest request) throws BusinessException {

    // Id du traitement Hydrants à numéroter
    Integer idModeleAlertesUtilisateurs = paramConfService.getIdTraitementNbAlertesParUtilisateur();
    ModeleTraitement modele = ModeleTraitement.findModeleTraitement(idModeleAlertesUtilisateurs);

    // ORGANISME_CIS_ID
    String valeurIdParametreOrganismeCIS = "-1";
    Utilisateur utilisateur = utilisateurService.getCurrentUtilisateur();
    String codeTypeOrganisme = utilisateur.getOrganisme().getTypeOrganisme().getCode();
    if ("CIS".equals(codeTypeOrganisme)) {
      valeurIdParametreOrganismeCIS = utilisateur.getOrganisme().getId().toString();
    } else if (!"SDIS".equals(codeTypeOrganisme)) {
      throw new BusinessException("L'utilisateur n'est pas membre du SDIS ou d'un CIS");
    }

    TraitementParametre[] paramArray = new TraitementParametre[3];
    Traitement trt = new Traitement();
    trt.setIdtraitement(-1); // obligatoire pour éviter
    // "Not-null property references a transient value"

    // ORGANISME_CIS_ID
    TraitementParametre tp = new TraitementParametre();
    tp.setIdparametre(
        ModeleTraitementParametre.findModeleTraitementParametresByNomAndIdmodele(
                "ORGANISME_CIS_ID", modele)
            .getSingleResult());
    tp.setIdtraitement(trt);
    tp.setValeur(valeurIdParametreOrganismeCIS);
    paramArray[0] = tp;
    // DATE_DEB
    tp = new TraitementParametre();
    tp.setIdparametre(
        ModeleTraitementParametre.findModeleTraitementParametresByNomAndIdmodele("DATE_DEB", modele)
            .getSingleResult());
    tp.setIdtraitement(trt);
    tp.setValeur("2013-01-01");
    paramArray[1] = tp;
    // DATE_FIN
    tp = new TraitementParametre();
    tp.setIdparametre(
        ModeleTraitementParametre.findModeleTraitementParametresByNomAndIdmodele("DATE_FIN", modele)
            .getSingleResult());
    tp.setIdtraitement(trt);
    tp.setValeur("2999-12-31");
    paramArray[2] = tp;

    String jsonValeurs =
        new JSONSerializer()
            .include("idparametre.idparametre")
            .include("idtraitement.idtraitement")
            .include("valeur")
            .exclude("*")
            .serialize(paramArray);

    return doCreateTraitement(idModeleAlertesUtilisateurs, jsonValeurs, request);
  }
}
