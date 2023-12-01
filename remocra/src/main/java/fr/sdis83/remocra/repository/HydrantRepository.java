package fr.sdis83.remocra.repository;

import static fr.sdis83.remocra.db.model.remocra.Tables.COMMUNE;
import static fr.sdis83.remocra.db.model.remocra.Tables.DOCUMENT;
import static fr.sdis83.remocra.db.model.remocra.Tables.HYDRANT;
import static fr.sdis83.remocra.db.model.remocra.Tables.HYDRANT_DOCUMENT;
import static fr.sdis83.remocra.db.model.remocra.Tables.HYDRANT_VISITE;
import static fr.sdis83.remocra.db.model.remocra.Tables.TYPE_HYDRANT_ANOMALIE;
import static fr.sdis83.remocra.db.model.remocra.Tables.TYPE_HYDRANT_ANOMALIE_NATURE;
import static fr.sdis83.remocra.db.model.remocra.Tables.TYPE_HYDRANT_ANOMALIE_NATURE_SAISIES;
import static fr.sdis83.remocra.db.model.remocra.Tables.TYPE_HYDRANT_IMPORTCTP_ERREUR;
import static fr.sdis83.remocra.db.model.remocra.Tables.TYPE_HYDRANT_SAISIE;
import static org.apache.poi.ss.usermodel.Row.MissingCellPolicy.RETURN_BLANK_AS_NULL;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.vividsolutions.jts.geom.Point;
import fr.sdis83.remocra.GlobalConstants;
import fr.sdis83.remocra.db.model.remocra.tables.pojos.Hydrant;
import fr.sdis83.remocra.db.model.remocra.tables.pojos.HydrantVisite;
import fr.sdis83.remocra.db.model.remocra.tables.pojos.TypeHydrantAnomalie;
import fr.sdis83.remocra.db.model.remocra.tables.pojos.TypeHydrantImportctpErreur;
import fr.sdis83.remocra.domain.remocra.Commune;
import fr.sdis83.remocra.domain.remocra.Document;
import fr.sdis83.remocra.domain.remocra.Organisme;
import fr.sdis83.remocra.domain.remocra.TypeDroit;
import fr.sdis83.remocra.enums.TypeErreurImportCtp;
import fr.sdis83.remocra.exception.BusinessException;
import fr.sdis83.remocra.exception.ImportCTPException;
import fr.sdis83.remocra.security.AuthoritiesUtil;
import fr.sdis83.remocra.service.HydrantService;
import fr.sdis83.remocra.service.ParamConfService;
import fr.sdis83.remocra.service.UtilisateurService;
import fr.sdis83.remocra.util.DocumentUtil;
import fr.sdis83.remocra.util.GeometryUtil;
import fr.sdis83.remocra.util.JSONUtil;
import fr.sdis83.remocra.web.message.ItemFilter;
import fr.sdis83.remocra.web.message.ItemSorting;
import fr.sdis83.remocra.web.model.HydrantRecord;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.sql.DataSource;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.cts.IllegalCoordinateException;
import org.cts.crs.CRSException;
import org.joda.time.DateTime;
import org.joda.time.Instant;
import org.joda.time.Period;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Result;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.NameTokenizers;
import org.modelmapper.jooq.RecordValueReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Configuration
public class HydrantRepository {

  @Autowired DSLContext context;

  @Autowired HydrantVisiteRepository hydrantVisiteRepository;

  @Autowired HydrantPibiRepository hydrantPibiRepository;

  @Autowired HydrantPenaRepository hydrantPenaRepository;

  @Autowired HydrantService hydrantService;

  @Autowired ParamConfService paramConfService;

  @Autowired UtilisateurService utilisateurService;

  @Autowired DataSource dataSource;

  @PersistenceContext protected EntityManager entityManager;

  @Autowired private AuthoritiesUtil authUtils;

  public static final String AUTEUR_MODIFICATION_FLAG = "USER";
  public static final String TYPE_HYDRANT_PENA = "PENA";
  public static final String TYPE_HYDRANT_PIBI = "PIBI";

  private final Logger logger = Logger.getLogger(getClass());

  public HydrantRepository() {}

  @Bean
  public HydrantRepository hydrantRepository(DSLContext context) {
    return new HydrantRepository(context);
  }

  HydrantRepository(DSLContext context) {
    this.context = context;
  }

  public void updateHydrantFromFiche(
      Long id, String jsonData, String typeHydrant, Map<String, MultipartFile> files)
      throws Exception {
    ObjectMapper objectMapper = new ObjectMapper();

    Map<String, Object> data =
        objectMapper.readValue(jsonData, new TypeReference<Map<String, Object>>() {});
    Map<String, Object> visitesData =
        objectMapper.readValue(
            data.get("visites").toString(), new TypeReference<Map<String, Object>>() {});
    Map<String, Object> geometrieData =
        objectMapper.readValue(
            data.get("geometrie").toString(), new TypeReference<Map<String, Object>>() {});

    Hydrant hydrant = this.getById(id);
    if (hydrant == null) {
      throw new Exception("Le pei spécifié n'existe pas");
    }

    Point geom = this.hydrantService.coordonneesToPoint(geometrieData.toString());

    // Données de l'hydrant
    Hydrant h = new Hydrant();
    h.setId(id);

    // Reprise du numéro, ne sera changé que si les conditions sont réunies (pas de numéro interne
    // ou renumérotation activée)
    h.setNumero(hydrant.getNumero());

    h.setNumeroInterne(JSONUtil.getInteger(data, "numeroInterne"));
    h.setNature(JSONUtil.getLong(data, "nature"));
    h.setNatureDeci(JSONUtil.getLong(data, "natureDeci"));
    h.setAutoriteDeci(JSONUtil.getLong(data, "autoriteDeci"));
    h.setSpDeci(JSONUtil.getLong(data, "spDeci"));
    h.setMaintenanceDeci(JSONUtil.getLong(data, "maintenanceDeci"));
    h.setGestionnaire(JSONUtil.getLong(data, "gestionnaire"));
    h.setGestionnaireSite(JSONUtil.getLong(data, "gestionnaireSite"));
    h.setSuffixeVoie(JSONUtil.getString(data, "suffixeVoie"));
    h.setCommune(JSONUtil.getLong(data, "commune"));
    h.setDomaine(JSONUtil.getLong(data, "domaine"));
    h.setNumeroVoie(JSONUtil.getInteger(data, "numeroVoie"));
    h.setEnFace(JSONUtil.getBoolean(data, "enFace"));
    h.setNiveau(JSONUtil.getLong(data, "niveau"));
    h.setVoie(JSONUtil.getString(data, "voie"));
    h.setVoie2(JSONUtil.getString(data, "voie2"));
    h.setLieuDit(JSONUtil.getString(data, "lieuDit"));
    h.setComplement(JSONUtil.getString(data, "complement"));
    h.setAnneeFabrication(JSONUtil.getInteger(data, "anneeFabrication"));
    h.setGeometrie(geom);
    h.setUtilisateurModification(utilisateurService.getCurrentUtilisateur().getId());
    h.setOrganisme(utilisateurService.getCurrentUtilisateur().getOrganisme().getId());
    h.setAuteurModificationFlag(AUTEUR_MODIFICATION_FLAG);
    h.setDateModification(new Instant());

    this.updateHydrant(h);

    // Visites
    this.hydrantVisiteRepository.deleteVisiteFromFiche(
        id, visitesData.get("deleteVisite").toString());
    this.hydrantVisiteRepository.addVisiteFromFiche(id, visitesData.get("addVisite").toString());

    // Caractéristiques techniques
    if (TYPE_HYDRANT_PIBI.equalsIgnoreCase(typeHydrant)) {
      this.hydrantPibiRepository.updateHydrantPibiFromFiche(id, data);
    } else if (TYPE_HYDRANT_PENA.equalsIgnoreCase(typeHydrant)) {
      this.hydrantPenaRepository.updateHydrantPenaFromFiche(id, data);
    }

    // Documents
    // TODO : lors du passage des documents en jooq, passer le code dans le repository
    if (files != null && !files.isEmpty()) {
      for (MultipartFile file : files.values()) {
        if (!file.isEmpty()) {
          Document d =
              DocumentUtil.getInstance()
                  .createNonPersistedDocument(
                      Document.TypeDocument.HYDRANT, file, paramConfService.getDossierDocHydrant());
          String sousType = DocumentUtil.getInstance().getSousType(file);

          Long idDoc =
              context
                  .insertInto(DOCUMENT)
                  .set(DOCUMENT.CODE, d.getCode())
                  .set(DOCUMENT.DATE_DOC, new Instant(d.getDateDoc()))
                  .set(DOCUMENT.FICHIER, d.getFichier())
                  .set(DOCUMENT.REPERTOIRE, d.getRepertoire())
                  .set(DOCUMENT.TYPE, d.getType().toString())
                  .returning(DOCUMENT.ID)
                  .fetchOne()
                  .getValue(DOCUMENT.ID);

          context
              .insertInto(HYDRANT_DOCUMENT)
              .set(HYDRANT_DOCUMENT.HYDRANT, id)
              .set(HYDRANT_DOCUMENT.DOCUMENT, idDoc)
              .execute();
        }
      }
    }
  }

  /**
   * Créé un hydrant depuis les informations passées par la fiche PEI Une fois l'hydrant créé, on
   * transmet les informations à la fonction d'update
   *
   * @param jsonData Les données du PEI
   * @param typeHydrant Le type de PEI
   * @param files Les fichiers du PEI
   */
  public void createHydrantFromFiche(
      String jsonData, String typeHydrant, Map<String, MultipartFile> files) throws Exception {

    ObjectMapper objectMapper = new ObjectMapper();

    Map<String, Object> data =
        objectMapper.readValue(jsonData, new TypeReference<Map<String, Object>>() {});
    Map<String, Object> visitesData =
        objectMapper.readValue(
            data.get("visites").toString(), new TypeReference<Map<String, Object>>() {});
    Map<String, Object> geometrieData =
        objectMapper.readValue(
            data.get("geometrie").toString(), new TypeReference<Map<String, Object>>() {});

    Point geom = this.hydrantService.coordonneesToPoint(geometrieData.toString());

    Hydrant h = new Hydrant();
    h.setCode(typeHydrant);
    h.setNature(JSONUtil.getLong(data, "nature"));
    h.setNatureDeci(JSONUtil.getLong(data, "natureDeci"));
    h.setAutoriteDeci(JSONUtil.getLong(data, "autoriteDeci"));
    h.setSpDeci(JSONUtil.getLong(data, "spDeci"));
    h.setMaintenanceDeci(JSONUtil.getLong(data, "maintenanceDeci"));
    h.setGestionnaire(JSONUtil.getLong(data, "gestionnaire"));
    h.setGestionnaireSite(JSONUtil.getLong(data, "gestionnaire_site"));
    h.setSuffixeVoie(JSONUtil.getString(data, "suffixeVoie"));
    h.setCommune(JSONUtil.getLong(data, "commune"));
    h.setDomaine(JSONUtil.getLong(data, "domaine"));
    h.setNumeroVoie(JSONUtil.getInteger(data, "numeroVoie"));
    h.setEnFace(JSONUtil.getBoolean(data, "enFace"));
    h.setNiveau(JSONUtil.getLong(data, "niveau"));
    h.setVoie(JSONUtil.getString(data, "voie"));
    h.setVoie2(JSONUtil.getString(data, "voie2"));
    h.setLieuDit(JSONUtil.getString(data, "lieuDit"));
    h.setComplement(JSONUtil.getString(data, "complement"));
    h.setAnneeFabrication(JSONUtil.getInteger(data, "anneeFabrication"));
    h.setGeometrie(geom);
    h.setUtilisateurModification(utilisateurService.getCurrentUtilisateur().getId());
    h.setOrganisme(utilisateurService.getCurrentUtilisateur().getOrganisme().getId());
    h.setAuteurModificationFlag(AUTEUR_MODIFICATION_FLAG);
    h.setDateModification(new Instant());

    Long id = this.createHydrant(h);

    if (TYPE_HYDRANT_PIBI.equalsIgnoreCase(typeHydrant)) {
      this.hydrantPibiRepository.createHydrantPibiFromFiche(id, data);
    } else if (TYPE_HYDRANT_PENA.equalsIgnoreCase(typeHydrant)) {
      this.hydrantPenaRepository.createHydrantPenaFromFiche(id, data);
    }

    this.hydrantVisiteRepository.deleteVisiteFromFiche(
        id, visitesData.get("deleteVisite").toString());
    this.hydrantVisiteRepository.addVisiteFromFiche(id, visitesData.get("addVisite").toString());

    // Documents
    // TODO : lors du passage des documents en jooq, passer le code dans le repository
    if (files != null && !files.isEmpty()) {
      for (MultipartFile file : files.values()) {
        if (!file.isEmpty()) {
          Document d =
              DocumentUtil.getInstance()
                  .createNonPersistedDocument(
                      Document.TypeDocument.HYDRANT, file, paramConfService.getDossierDocHydrant());
          String sousType = DocumentUtil.getInstance().getSousType(file);

          Long idDoc =
              context
                  .insertInto(DOCUMENT)
                  .set(DOCUMENT.CODE, d.getCode())
                  .set(DOCUMENT.DATE_DOC, new Instant(d.getDateDoc()))
                  .set(DOCUMENT.FICHIER, d.getFichier())
                  .set(DOCUMENT.REPERTOIRE, d.getRepertoire())
                  .set(DOCUMENT.TYPE, d.getType().toString())
                  .returning(DOCUMENT.ID)
                  .fetchOne()
                  .getValue(DOCUMENT.ID);

          context
              .insertInto(HYDRANT_DOCUMENT)
              .set(HYDRANT_DOCUMENT.HYDRANT, id)
              .set(HYDRANT_DOCUMENT.DOCUMENT, idDoc)
              .execute();
        }
      }
    }
  }

  /**
   * Créé un hydrant en base
   *
   * @return
   */
  private Long createHydrant(Hydrant h) {
    h = NumeroUtilRepository.setCodeZoneSpecAndNumeros(h, h.getCode());

    Long id =
        context
            .insertInto(HYDRANT)
            .set(HYDRANT.NUMERO, h.getNumero())
            .set(HYDRANT.NUMERO_INTERNE, h.getNumeroInterne())
            .set(HYDRANT.CODE, h.getCode())
            .set(HYDRANT.NATURE, h.getNature())
            .set(HYDRANT.NATURE_DECI, h.getNatureDeci())
            .set(HYDRANT.AUTORITE_DECI, h.getAutoriteDeci())
            .set(HYDRANT.SP_DECI, h.getSpDeci())
            .set(HYDRANT.MAINTENANCE_DECI, h.getMaintenanceDeci())
            .set(HYDRANT.GESTIONNAIRE, h.getGestionnaire())
            .set(HYDRANT.GESTIONNAIRE_SITE, h.getGestionnaireSite())
            .set(HYDRANT.SUFFIXE_VOIE, h.getSuffixeVoie())
            .set(HYDRANT.COMMUNE, h.getCommune())
            .set(HYDRANT.DOMAINE, h.getDomaine())
            .set(HYDRANT.NUMERO_VOIE, h.getNumeroVoie())
            .set(HYDRANT.EN_FACE, h.getEnFace())
            .set(HYDRANT.NIVEAU, h.getNiveau())
            .set(HYDRANT.VOIE, h.getVoie())
            .set(HYDRANT.VOIE2, h.getVoie2())
            .set(HYDRANT.LIEU_DIT, h.getLieuDit())
            .set(HYDRANT.COMPLEMENT, h.getComplement())
            .set(HYDRANT.ANNEE_FABRICATION, h.getAnneeFabrication())
            .set(HYDRANT.GEOMETRIE, h.getGeometrie())
            .set(HYDRANT.DATE_MODIFICATION, h.getDateModification())
            .set(HYDRANT.ORGANISME, h.getOrganisme())
            .set(HYDRANT.UTILISATEUR_MODIFICATION, h.getUtilisateurModification())
            .set(HYDRANT.AUTEUR_MODIFICATION_FLAG, h.getAuteurModificationFlag())
            .set(HYDRANT.ZONE_SPECIALE, h.getZoneSpeciale())
            .returning(HYDRANT.ID)
            .fetchOne()
            .getValue(HYDRANT.ID);

    return id;
  }

  /**
   * Met à jour un hydrant en base
   *
   * @param h Les données de l'hydrant
   * @return L'hydrant mis à jour
   */
  private Hydrant updateHydrant(Hydrant h) {

    if ((h.getNumeroInterne() == null || h.getNumeroInterne() <= 0)
        || paramConfService.getHydrantRenumerotationActivation()) {
      h = NumeroUtilRepository.setCodeZoneSpecAndNumeros(h, h.getCode());
    }

    context
        .update(HYDRANT)
        .set(HYDRANT.NUMERO, h.getNumero())
        .set(HYDRANT.NUMERO_INTERNE, h.getNumeroInterne())
        .set(HYDRANT.NATURE, h.getNature())
        .set(HYDRANT.NATURE_DECI, h.getNatureDeci())
        .set(HYDRANT.AUTORITE_DECI, h.getAutoriteDeci())
        .set(HYDRANT.SP_DECI, h.getSpDeci())
        .set(HYDRANT.MAINTENANCE_DECI, h.getMaintenanceDeci())
        .set(HYDRANT.GESTIONNAIRE, h.getGestionnaire())
        .set(HYDRANT.GESTIONNAIRE_SITE, h.getGestionnaireSite())
        .set(HYDRANT.SUFFIXE_VOIE, h.getSuffixeVoie())
        .set(HYDRANT.COMMUNE, h.getCommune())
        .set(HYDRANT.DOMAINE, h.getDomaine())
        .set(HYDRANT.NUMERO_VOIE, h.getNumeroVoie())
        .set(HYDRANT.EN_FACE, h.getEnFace())
        .set(HYDRANT.NIVEAU, h.getNiveau())
        .set(HYDRANT.VOIE, h.getVoie())
        .set(HYDRANT.VOIE2, h.getVoie2())
        .set(HYDRANT.LIEU_DIT, h.getLieuDit())
        .set(HYDRANT.COMPLEMENT, h.getComplement())
        .set(HYDRANT.ANNEE_FABRICATION, h.getAnneeFabrication())
        .set(HYDRANT.GEOMETRIE, h.getGeometrie())
        .set(HYDRANT.DATE_MODIFICATION, h.getDateModification())
        .set(HYDRANT.ORGANISME, h.getOrganisme())
        .set(HYDRANT.UTILISATEUR_MODIFICATION, h.getUtilisateurModification())
        .set(HYDRANT.AUTEUR_MODIFICATION_FLAG, h.getAuteurModificationFlag())
        .set(HYDRANT.ZONE_SPECIALE, h.getZoneSpeciale())
        .where(HYDRANT.ID.eq(h.getId()))
        .execute();

    return this.getById(h.getId());
  }

  /**
   * Lors de l'mport des visites CTP via un fichier .xls ou .xlsx, vérifie la validité des données
   *
   * @param file Le fichier importé
   * @return String Le résultat de la vérification au format JSON
   */
  public String importCTP(MultipartFile file) {
    ObjectMapper mapper = new ObjectMapper();
    ObjectNode data = mapper.createObjectNode();
    ArrayNode arrayResultatVerifications = mapper.createArrayNode();

    // Erreur a retourner en cas de problème avec le fichier
    ObjectNode erreurFichier = mapper.createObjectNode();
    erreurFichier.put("numero_ligne", 0);
    erreurFichier.put("insee", "");
    erreurFichier.put("numeroInterne", "");
    erreurFichier.put("dateCtp", "");
    erreurFichier.put("bilan_style", "ERREUR");

    boolean lecturePossible = true;
    Workbook workbook = null;

    // Gestion erreur fichier illisible
    try {
      workbook = WorkbookFactory.create(file.getInputStream());
      workbook.setMissingCellPolicy(RETURN_BLANK_AS_NULL);
    } catch (Exception e) {
      TypeHydrantImportctpErreur erreur =
          context
              .selectFrom(TYPE_HYDRANT_IMPORTCTP_ERREUR)
              .where(
                  TYPE_HYDRANT_IMPORTCTP_ERREUR.CODE.eq(
                      TypeErreurImportCtp.ERR_FICHIER_INNAC.getCode()))
              .fetchOneInto(TypeHydrantImportctpErreur.class);
      erreurFichier.put("bilan", erreur.getMessage());
      arrayResultatVerifications.add(erreurFichier);
      lecturePossible = false;
    }

    // Gestion erreur feuille n°2 (sur laquelle se trouve les données) n'existe pas
    Sheet sheet = null;
    try {
      if (lecturePossible) {
        sheet = workbook.getSheetAt(1);
        if (sheet == null || !"Saisies_resultats_CT".equals(sheet.getSheetName())) {
          throw new Exception(TypeErreurImportCtp.ERR_ONGLET_ABS.getCode());
        }
      }
    } catch (Exception e) {
      TypeHydrantImportctpErreur erreur =
          context
              .selectFrom(TYPE_HYDRANT_IMPORTCTP_ERREUR)
              .where(
                  TYPE_HYDRANT_IMPORTCTP_ERREUR.CODE.eq(
                      TypeErreurImportCtp.ERR_ONGLET_ABS.getCode()))
              .fetchOneInto(TypeHydrantImportctpErreur.class);
      erreurFichier.put("bilan", erreur.getMessage());
      arrayResultatVerifications.add(erreurFichier);
      lecturePossible = false;
    }

    // Lecture des valeurs
    if (lecturePossible) {
      int nbLigne = 4;
      boolean estFini = false;
      while (nbLigne < sheet.getPhysicalNumberOfRows() && !estFini) {
        ObjectNode resultatVerification = null;
        try {
          Row r = sheet.getRow(nbLigne);
          if (r.getFirstCellNum() == 0
              && r.getCell(0)
                  != null) { // Evite de traiter la ligne "fantôme" détectée par la librairie à
            // cause des
            // combo des anomalies
            resultatVerification = this.checkLineValidity(r);
            resultatVerification.put("numero_ligne", nbLigne + 1);
            arrayResultatVerifications.add(resultatVerification);
          } else {
            estFini = true;
          }
        } catch (
            ImportCTPException
                e) { // Interception d'une erreur : on arrête les vérifications et on indique la
          // cause
          resultatVerification = e.getData();
          TypeHydrantImportctpErreur erreur =
              context
                  .selectFrom(TYPE_HYDRANT_IMPORTCTP_ERREUR)
                  .where(TYPE_HYDRANT_IMPORTCTP_ERREUR.CODE.eq(e.getCodeErreur()))
                  .fetchOneInto(TypeHydrantImportctpErreur.class);
          resultatVerification.put("bilan", erreur.getMessage());
          resultatVerification.put("bilan_style", erreur.getType());
          resultatVerification.put("numero_ligne", nbLigne + 1);
          resultatVerification.remove("warnings");
          arrayResultatVerifications.add(resultatVerification);
        }
        nbLigne++;
      }
    }
    data.set("bilanVerifications", arrayResultatVerifications);
    return data.toString();
  }

  /**
   * Lors de l'import des visites CTP, vérifie la validité d'une ligne donnée
   *
   * @param row La ligne contenant toutes les cellules requises
   * @return ObjectNode un object JSON contenant les résultat de la validation pour cette ligne
   * @throws ImportCTPException En cas de donnée incorrecte, déclenche une exception gérée et
   *     traitée par la fonction parente
   */
  private ObjectNode checkLineValidity(Row row) throws ImportCTPException {
    ObjectMapper mapper = new ObjectMapper();
    ObjectNode data = mapper.createObjectNode();
    ArrayNode arrayWarnings = mapper.createArrayNode();
    data.put("bilan", "CT Validé");
    data.put("bilan_style", "OK");

    Integer xls_codeSdis = (int) row.getCell(0).getNumericCellValue();
    String xls_commune = row.getCell(1).getStringCellValue();
    String xls_insee = this.getStringValueFromCell(row.getCell(2));
    Integer xls_numeroInterne = (int) row.getCell(3).getNumericCellValue();

    SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
    data.put("insee", xls_insee);
    data.put("numeroInterne", xls_numeroInterne);

    ObjectNode dataVisite = mapper.createObjectNode();
    SimpleDateFormat formatterDateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:00");

    // Vérification si l'hydrant renseigné correspond bien à l'hydrant en base
    Hydrant h =
        context
            .select(HYDRANT.fields())
            .from(HYDRANT)
            .join(COMMUNE)
            .on(COMMUNE.ID.eq(HYDRANT.COMMUNE))
            .where(COMMUNE.INSEE.eq(xls_insee))
            .and(HYDRANT.NUMERO_INTERNE.eq(xls_numeroInterne))
            .fetchOneInto(Hydrant.class);

    if (h == null || (h.getId() != xls_codeSdis.longValue())) {
      throw new ImportCTPException(TypeErreurImportCtp.ERR_MAUVAIS_NUM_PEI.getCode(), data);
    }

    // On vérifie si le PEI est bien dans la zone de compétence de l'utilisateur
    Boolean dansZoneCompetence =
        context
            .resultQuery(
                "SELECT ST_CONTAINS(zc.geometrie, h.geometrie) "
                    + "FROM remocra.zone_competence zc "
                    + "JOIN remocra.hydrant h on h.id = {0}"
                    + "WHERE zc.id = {1};",
                h.getId(), this.utilisateurService.getCurrentZoneCompetenceId())
            .fetchOneInto(Boolean.class);

    if (dansZoneCompetence == null || !dansZoneCompetence) {
      throw new ImportCTPException(TypeErreurImportCtp.ERR_DEHORS_ZC.getCode(), data);
    }

    // Si la visite CTP n'est pas renseignée (si tous les champs composant les informations de la
    // visite sont vides)
    boolean ctpRenseigne = false;
    for (int i = 9; i < 18; i++) {
      if (row.getCell(i) != null && row.getCell(i).getCellType() != CellType.BLANK) {
        ctpRenseigne = true;
      }
    }
    if (!ctpRenseigne) {
      // On passe par un throw car à ce stade on a déjà toutes les infos que l'on souhaite afficher
      // On n'a pas besoin de continuer les vérifications
      throw new ImportCTPException(TypeErreurImportCtp.INFO_IGNORE.getCode(), data);
    }

    // Vérifications au niveau de la date
    if (row.getCell(9) == null || row.getCell(9).getCellType() == CellType.BLANK) {
      throw new ImportCTPException(TypeErreurImportCtp.ERR_DATE_MANQ.getCode(), data);
    }

    Date xls_dateCtp = null;
    try {
      xls_dateCtp = row.getCell(9).getDateCellValue();
    } catch (Exception e) {
      throw new ImportCTPException(TypeErreurImportCtp.ERR_FORMAT_DATE.getCode(), data);
    }

    if (xls_dateCtp.after(new Date())) {
      throw new ImportCTPException(TypeErreurImportCtp.ERR_DATE_POST.getCode(), data);
    }

    Integer nbVisite =
        context
            .selectCount()
            .from(HYDRANT_VISITE)
            .join(TYPE_HYDRANT_SAISIE)
            .on(HYDRANT_VISITE.TYPE.eq(TYPE_HYDRANT_SAISIE.ID))
            .where(
                HYDRANT_VISITE
                    .HYDRANT
                    .eq(h.getId())
                    .and(TYPE_HYDRANT_SAISIE.CODE.eq("CTRL"))
                    .and(HYDRANT_VISITE.CTRL_DEBIT_PRESSION)
                    .and(HYDRANT_VISITE.DATE.greaterThan(new Instant(xls_dateCtp))))
            .fetchOneInto(Integer.class);
    if (nbVisite > 0) {
      String str =
          context
              .select(TYPE_HYDRANT_IMPORTCTP_ERREUR.MESSAGE)
              .from(TYPE_HYDRANT_IMPORTCTP_ERREUR)
              .where(
                  TYPE_HYDRANT_IMPORTCTP_ERREUR.CODE.eq(
                      TypeErreurImportCtp.WARN_DATE_ANTE.getCode()))
              .fetchOneInto(String.class);
      arrayWarnings.add(str);
    }

    // On vérifie que le PEI dispose de ses deux premières visites (réception et ROI) pour pouvoir
    // lui adjoindre une visite CTP
    nbVisite =
        context
            .selectCount()
            .from(HYDRANT_VISITE)
            .where(HYDRANT_VISITE.HYDRANT.eq(h.getId()))
            .fetchOneInto(Integer.class);
    if (nbVisite < 2) {
      throw new ImportCTPException(TypeErreurImportCtp.ERR_VISITES_MANQUANTES.getCode(), data);
    }

    // On vérifie si il n'y pas de visite à la même date et heure
    nbVisite =
        context
            .selectCount()
            .from(HYDRANT_VISITE)
            .join(TYPE_HYDRANT_SAISIE)
            .on(HYDRANT_VISITE.TYPE.eq(TYPE_HYDRANT_SAISIE.ID))
            .where(
                HYDRANT_VISITE
                    .HYDRANT
                    .eq(h.getId())
                    .and(TYPE_HYDRANT_SAISIE.CODE.eq("CTRL"))
                    .and(HYDRANT_VISITE.CTRL_DEBIT_PRESSION)
                    .and(HYDRANT_VISITE.DATE.eq(new Instant(xls_dateCtp))))
            .fetchOneInto(Integer.class);

    if (nbVisite > 0) {
      throw new ImportCTPException(TypeErreurImportCtp.ERR_VISITE_EXISTANTE.getCode(), data);
    }

    data.put("dateCtp", formatter.format(xls_dateCtp));

    String xls_agent1 = null;
    if (row.getCell(10) == null || row.getCell(10).getCellType() == CellType.BLANK) {
      throw new ImportCTPException(TypeErreurImportCtp.ERR_AGENT1_ABS.getCode(), data);
    } else {
      xls_agent1 = row.getCell(10).getStringCellValue();
    }

    Integer xls_debit = null;
    try {
      if (row.getCell(12) != null
          && row.getCell(12).getCellType() != CellType.BLANK) { // Si une valeur est renseignée
        xls_debit = (int) row.getCell(12).getNumericCellValue();

        if (row.getCell(12).getNumericCellValue() % 1
            != 0) { // Si on a réalisé une troncature lors de la lecture de la valeur
          TypeHydrantImportctpErreur info =
              context
                  .selectFrom(TYPE_HYDRANT_IMPORTCTP_ERREUR)
                  .where(
                      TYPE_HYDRANT_IMPORTCTP_ERREUR.CODE.eq(
                          TypeErreurImportCtp.INFO_TRONC_DEBIT.getCode()))
                  .fetchOneInto(TypeHydrantImportctpErreur.class);
          data.put("bilan", info.getMessage());
          data.put("bilan_style", info.getType());
        }
        if (xls_debit < 0) {
          throw new Exception();
        }
      }
    } catch (Exception e) {
      throw new ImportCTPException(TypeErreurImportCtp.ERR_FORMAT_DEBIT.getCode(), data);
    }

    Double xls_pression = null;
    try {
      if (row.getCell(11) != null
          && row.getCell(11).getCellType() != CellType.BLANK) { // Si une valeur est renseignée

        xls_pression = this.getNumericValueFromCell(row.getCell(11));
        if (xls_pression < 0) {
          throw new Exception();
        }
      }
    } catch (Exception e) {
      throw new ImportCTPException(TypeErreurImportCtp.ERR_FORMAT_PRESS.getCode(), data);
    }

    if (xls_pression != null && xls_pression > 20.0) {
      throw new ImportCTPException(TypeErreurImportCtp.ERR_PRESS_ELEVEE.getCode(), data);
    }

    String warningDebitPression = null;
    if (xls_pression == null && xls_debit == null) {
      warningDebitPression = TypeErreurImportCtp.WARN_DEB_PRESS_VIDE.getCode();
    } else if (xls_pression == null) {
      warningDebitPression = TypeErreurImportCtp.WARN_PRESS_VIDE.getCode();
    } else if (xls_debit == null) {
      warningDebitPression = TypeErreurImportCtp.WARN_DEBIT_VIDE.getCode();
    }

    if (warningDebitPression != null) {
      String str =
          context
              .select(TYPE_HYDRANT_IMPORTCTP_ERREUR.MESSAGE)
              .from(TYPE_HYDRANT_IMPORTCTP_ERREUR)
              .where(TYPE_HYDRANT_IMPORTCTP_ERREUR.CODE.eq(warningDebitPression))
              .fetchOneInto(String.class);
      arrayWarnings.add(str);
    }

    /* Si l'utilisateur a le droit de déplacer un PEI, on affiche un warning si la distance de déplacement est supérieure
    a la distance renseignée dans les paramètres de l'application */
    if (this.authUtils.hasRight(TypeDroit.TypeDroitEnum.HYDRANTS_DEPLACEMENT_C)) {
      Double latitude = null;
      Double longitude = null;
      try {
        latitude = this.getNumericValueFromCell(row.getCell(5));
        longitude = this.getNumericValueFromCell(row.getCell(6));
      } catch (Exception e) {
        throw new ImportCTPException(TypeErreurImportCtp.ERR_COORD_GPS.getCode(), data);
      }

      Integer distance =
          context
              .resultQuery(
                  "SELECT ST_DISTANCE(ST_transform(ST_SetSRID(ST_MakePoint({0}, {1}),4326), {2}), h.geometrie) "
                      + "FROM remocra.hydrant h "
                      + "WHERE h.id = {3};",
                  longitude, latitude, GlobalConstants.SRID_2154, h.getId())
              .fetchOneInto(Integer.class);

      if (distance > this.paramConfService.getHydrantDeplacementDistWarn()) {
        String str =
            context
                .select(TYPE_HYDRANT_IMPORTCTP_ERREUR.MESSAGE)
                .from(TYPE_HYDRANT_IMPORTCTP_ERREUR)
                .where(
                    TYPE_HYDRANT_IMPORTCTP_ERREUR.CODE.eq(
                        TypeErreurImportCtp.WARN_DEPLACEMENT.getCode()))
                .fetchOneInto(String.class);
        arrayWarnings.add(str);
      }

      dataVisite.put("latitude", latitude);
      dataVisite.put("longitude", longitude);
    }

    // Vérifications anomalies
    ArrayList<Long> id_anomalies = new ArrayList<Long>();

    // On récupère les identifiants des anomalies inscrites dans le fichier
    for (int i = 13; i < 17; i++) {
      if (row.getCell(i) != null && row.getCell(i).getCellType() != CellType.BLANK) {
        String xls_anomalie = row.getCell(i).getStringCellValue();
        String code = xls_anomalie.substring(xls_anomalie.indexOf('-') + 2);
        TypeHydrantAnomalie anomalie =
            context
                .selectFrom(TYPE_HYDRANT_ANOMALIE)
                .where(TYPE_HYDRANT_ANOMALIE.CODE.upper().eq(code.toUpperCase()))
                .fetchOneInto(TypeHydrantAnomalie.class);
        if (anomalie == null) {
          throw new ImportCTPException(TypeErreurImportCtp.ERR_ANO_INCONNU.getCode(), data);
        }
        id_anomalies.add(anomalie.getId());
      }
    }

    // On récupère les anomalies de la visite précédente qui ne sont pas possibles pour un contexte
    // CTP
    HydrantVisite derniereVisite =
        context
            .selectFrom(HYDRANT_VISITE)
            .where(HYDRANT_VISITE.HYDRANT.eq(h.getId()))
            .and(HYDRANT_VISITE.DATE.lessThan(new Instant(xls_dateCtp)))
            .orderBy(HYDRANT_VISITE.DATE.desc())
            .limit(1)
            .fetchOneInto(HydrantVisite.class);
    if (derniereVisite != null
        && derniereVisite.getAnomalies() != null
        && derniereVisite.getAnomalies().length() > 0) {
      String[] strArrayAnomalies =
          derniereVisite
              .getAnomalies()
              .replaceAll("\\[", "")
              .replaceAll("]", "")
              .replaceAll(" ", "")
              .split(",");
      ArrayList<Long> idAnomaliesDerniereVisite = new ArrayList<Long>();

      // Récupération de toutes les anomalies possibles pour ce contexte
      ArrayList<Long> idAnomaliesCTP =
          (ArrayList<Long>)
              context
                  .select(TYPE_HYDRANT_ANOMALIE.ID)
                  .from(TYPE_HYDRANT_ANOMALIE)
                  .join(TYPE_HYDRANT_ANOMALIE_NATURE)
                  .on(TYPE_HYDRANT_ANOMALIE_NATURE.ANOMALIE.eq(TYPE_HYDRANT_ANOMALIE.ID))
                  .join(TYPE_HYDRANT_ANOMALIE_NATURE_SAISIES)
                  .on(
                      TYPE_HYDRANT_ANOMALIE_NATURE_SAISIES.TYPE_HYDRANT_ANOMALIE_NATURE.eq(
                          TYPE_HYDRANT_ANOMALIE_NATURE.ID))
                  .join(TYPE_HYDRANT_SAISIE)
                  .on(TYPE_HYDRANT_SAISIE.ID.eq(TYPE_HYDRANT_ANOMALIE_NATURE_SAISIES.SAISIES))
                  .where(TYPE_HYDRANT_SAISIE.CODE.equalIgnoreCase("CTRL"))
                  .fetchInto(Long.class);

      for (String s : strArrayAnomalies) {
        if (StringUtils.isNotEmpty(s)) {
          Long l = Long.parseLong(s);
          // Si une anomalie est trouvée et n'existe pas pour le contexte CTP, on la reprends de la
          // visite précédente
          if (idAnomaliesCTP.indexOf(l) == -1) {
            id_anomalies.add(l);
          }
        }
      }
    }

    ArrayNode arrayAnomalies = mapper.createArrayNode();
    for (Long i : id_anomalies) {
      arrayAnomalies.add(i);
    }

    String observation =
        (row.getLastCellNum() - 1 >= 18) ? row.getCell(18).getStringCellValue() : null;

    /**
     * Ajout des données de la visite à ajouter aux informations JSON Ces données ont déjà été
     * vérifiées ici, il n'y a pas besoin de dupliquer les vérifications avant l'ajout en base
     */
    dataVisite.set("anomalies", arrayAnomalies);
    dataVisite.put("date", formatterDateTime.format(xls_dateCtp));
    dataVisite.put("hydrant", h.getId());
    dataVisite.put("agent1", xls_agent1);
    dataVisite.put("debit", xls_debit);
    dataVisite.put("pression", xls_pression);
    dataVisite.put("observation", observation);

    data.set("dataVisite", dataVisite);

    if (arrayWarnings.size() > 0) {
      data.set("warnings", arrayWarnings);
      data.put("bilan_style", "WARNING");
    }
    return data;
  }

  /**
   * Lit un nombre réel depuis une cellule La valeur doit être au format décimal point ou virgule
   *
   * @param c La cellule contentant la donnée
   * @return La valeur de la coordonnée de type Double
   * @throws Exception La valeur ne respecte pas le format attendu
   */
  Double getNumericValueFromCell(Cell c) throws Exception {
    if (c.getCellType() == CellType.NUMERIC) {
      return c.getNumericCellValue();
    }

    if (c.getCellType() != CellType.STRING) {
      throw new Exception();
    }

    String str_cell = c.getStringCellValue().replaceAll(",", ".");
    if (!str_cell.matches("([+-]?\\d+\\.?\\d+)\\s*")) {
      throw new Exception();
    }
    return Double.parseDouble(str_cell);
  }

  /**
   * Pour pallier au problème quand une cellule est formattée à la saisie par Excel automatiquement
   * au format Numéric et qu'il est attendu un String (Ex : code Insee)
   *
   * @param c
   * @return
   */
  private String getStringValueFromCell(Cell c) {
    if (c.getCellType() == CellType.STRING) {
      return c.getStringCellValue();
    }
    if (c.getCellType() == CellType.NUMERIC) {
      return String.valueOf((int) c.getNumericCellValue());
    }
    return null;
  }

  public List<HydrantRecord> getAll(
      List<ItemFilter> itemFilters, Integer limit, Integer start, List<ItemSorting> itemSortings) {
    String condition = this.getFilters(itemFilters);

    boolean triAlphaNumerique = this.paramConfService.getHydrantMethodeTriAlphanumerique();

    String sortFields =
        (triAlphaNumerique)
            ? "numero ASC"
            : "length(numero) ASC, numero ASC"; // Critères de tri par défaut si aucun renseigné

    if (itemSortings.size() > 0) {
      Collection<String> listOrderFields = new ArrayList<String>();
      boolean hasNumero = false;
      for (ItemSorting item : itemSortings) {
        if ("numero".equals(item.getFieldName())) {
          hasNumero = true;
          if (!triAlphaNumerique) { // Un tri sur le numéro se transforme automatiquement en tri
            // naturel (sauf paramétrage contraire)
            listOrderFields.add("length(numero) " + item.getDirection());
          }
        }
        listOrderFields.add(item.getFieldName() + " " + item.getDirection());
      }
      if (!hasNumero) {
        listOrderFields.add(sortFields);
      }
      sortFields = StringUtils.join(listOrderFields.toArray(), ",");
    }

    Result<Record> hydrantRecord = null;

    String orgAndChildren =
        StringUtils.join(
            Organisme.getOrganismeAndChildren(
                    utilisateurService.getCurrentUtilisateur().getOrganisme().getId().intValue())
                .toArray(),
            ",");

    List<HydrantRecord> hr = new ArrayList<HydrantRecord>();
    StringBuffer sbReq =
        new StringBuffer(getAutoriteDeci())
            .append(
                "select h.id  as  id , h.code as code, ST_AsGeoJSON(h.geometrie) as jsonGeometrie, h.numero as numero, h.date_contr as dateContr,")
            .append(
                " case when char_length(h.voie)>0 and char_length(h.voie2)>0 then h.voie || ' - ' || h.voie2 else voie end as adresse,")
            .append(" h.dispo_terrestre as dispoTerrestre,")
            .append(" h.dispo_hbe as dispoHbe,")
            .append(" h.date_reco as dateReco, h.numero_interne as numeroInterne,")
            .append(" h.date_recep as dateRecep,")
            .append(" c.nom as nomCommune,")
            .append(" thnd.nom as nomNatureDeci,")
            .append(" thn.nom as natureNom,")
            .append(
                " (SELECT COALESCE(string_agg(nom, ', ' order by nom), '') FROM remocra.tournee t where t.affectation in ("
                    + orgAndChildren
                    + ") and ")
            .append(
                " t.id in (SELECT tournees FROM remocra.hydrant_tournees ht where ht.hydrant = h.id)) as nomTournee,")
            .append(
                " (select count(*) from remocra.hydrant_anomalies ha where ha.hydrant = h.id AND ha.anomalies = (select tha.id from remocra.type_hydrant_anomalie tha where tha.code = 'INDISPONIBILITE_TEMP')) as indispoTemp, ")
            .append(" autoriteDeci.autoriteDeci")
            .append(" from remocra.hydrant h")
            .append(" join remocra.commune c on c.id = h.commune")
            .append(" join remocra.type_hydrant_nature_deci thnd on thnd.id = h.nature_deci")
            .append(" join remocra.type_hydrant_nature thn on thn.id = h.nature")
            .append(" left join autoriteDeci on autoriteDeci.id = h.autorite_deci ")
            .append(" where " + condition)
            .append(" order by " + sortFields)
            .append(" limit " + limit)
            .append(" offset " + start);

    hydrantRecord = context.fetch(sbReq.toString());

    ModelMapper modelMapper = new ModelMapper();
    modelMapper.getConfiguration().addValueReader(new RecordValueReader());
    modelMapper.getConfiguration().setSourceNameTokenizer(NameTokenizers.CAMEL_CASE);
    for (Record r : hydrantRecord) {
      HydrantRecord hydrant = modelMapper.map(r, HydrantRecord.class);
      hr.add(hydrant);
    }
    return hr;
  }

  public String getFilters(List<ItemFilter> itemFilters) {
    String condition = "1 = 1";
    for (ItemFilter itemFilter : itemFilters) {
      if ("tournee".equals(itemFilter.getFieldName())) {
        condition +=
            " and h.id in  (select hydrant from remocra.hydrant_tournees where tournees = "
                + Long.valueOf(itemFilter.getValue())
                + ")";
      } else if ("zoneCompetence".equals(itemFilter.getFieldName())) {
        Long zoneCompetenceId = utilisateurService.getCurrentZoneCompetenceId();
        condition +=
            " and st_contains((select st_simplify(zc.geometrie ,1) from remocra.zone_competence zc where  id ="
                + zoneCompetenceId
                + " ), h.geometrie)";
      } else if ("autoriteDeci".equals(itemFilter.getFieldName())) {
        condition += " and autoriteDeci ilike '%" + itemFilter.getValue() + "%'";
      } else if ("numero".equals(itemFilter.getFieldName())) {
        condition +=
            " and upper(h.numero) like '%"
                + itemFilter.getValue().toUpperCase(Locale.FRANCE)
                + "%' ";
      } else if ("dateReco".equals(itemFilter.getFieldName())) {
        Integer nbMonths = Integer.valueOf(itemFilter.getValue());
        if (nbMonths != 0) {
          final DateTime datePrive =
              new DateTime()
                  .minus(Period.days(paramConfService.getHydrantRenouvellementRecoPrive()));
          final DateTime datePublic =
              new DateTime()
                  .minus(Period.days(paramConfService.getHydrantRenouvellementRecoPublic()));
          if (nbMonths > 0) {
            condition +=
                " and case when thnd.code = 'PRIVE' then h.date_reco <= '"
                    + datePrive.plus(Period.months(nbMonths)).toDate()
                    + "'::date and h.date_reco >= '"
                    + datePrive.toDate()
                    + "'::date"
                    + " else h.date_reco <='"
                    + datePublic.plus(Period.months(nbMonths)).toDate()
                    + "'::date and h.date_reco >= '"
                    + datePublic.toDate()
                    + "'::date end";
          } else {
            condition +=
                " and case when thnd.code = 'PRIVE' then h.date_reco <= '"
                    + datePrive.toDate()
                    + "'::date else h.date_reco <='"
                    + datePublic.toDate()
                    + "'::date end";
          }
        }
      } else if ("dateContr".equals(itemFilter.getFieldName())) {
        Integer nbMonths = Integer.valueOf(itemFilter.getValue());
        if (nbMonths != 0) {
          final DateTime datePrive =
              new DateTime()
                  .minus(Period.days(paramConfService.getHydrantRenouvellementCtrlPrive()));
          final DateTime datePublic =
              new DateTime()
                  .minus(Period.days(paramConfService.getHydrantRenouvellementCtrlPublic()));
          if (nbMonths > 0) {
            condition +=
                " and case when thnd.code = 'PRIVE' then h.date_contr <= '"
                    + datePrive.plus(Period.months(nbMonths)).toDate()
                    + "'::date and h.date_contr >= '"
                    + datePrive.toDate()
                    + "'::date"
                    + " else h.date_contr <='"
                    + datePublic.plus(Period.months(nbMonths)).toDate()
                    + "'::date and h.date_contr >= '"
                    + datePublic.toDate()
                    + "'::date end";
          } else {
            condition +=
                " and case when thnd.code = 'PRIVE' then h.date_contr <= '"
                    + datePrive.toDate()
                    + "'::date else h.date_contr <='"
                    + datePublic.toDate()
                    + "'::date end";
          }
        }
      } else if ("nature".equals(itemFilter.getFieldName())) {
        condition += " and h.nature = " + Long.valueOf(itemFilter.getValue());
      } else if ("naturecode".equals(itemFilter.getFieldName())) {
        // Exemples de valeur : 'PI,PA' ou 'PI' ou ''

      } else if ("dispoTerrestre".equals(itemFilter.getFieldName())) {
        condition += " and h.dispo_terrestre like '" + itemFilter.getValue() + "'";
      } else if ("dispoHbe".equals(itemFilter.getFieldName())) {
        condition += " and h.dispo_hbe like '" + itemFilter.getValue() + "'";
      } else if ("nomCommune".equals(itemFilter.getFieldName())) {
        /**
         * A cause de l'utilisation hybride du champ commune, le serveur peut récupérer deux données
         * différentes sur le même path selon le type de donnée fourni : - Un nombre entier
         * (identifiant) lors d'une sélection dans la combo - Une chaîne de caratères (nom) lors de
         * la saisie de texte dans la combo
         */
        if (StringUtils.isNumeric(itemFilter.getValue())) {
          condition += " and c.id = " + Long.valueOf(itemFilter.getValue());
        } else {
          condition +=
              " and upper(c.nom) like '%" + itemFilter.getValue().toUpperCase(Locale.FRANCE) + "%'";
        }
      } else if ("nomNatureDeci".equals(itemFilter.getFieldName())) {
        condition += " and h.nature_deci = " + Long.valueOf(itemFilter.getValue());
      } else if ("codeNatureDeci".equals(itemFilter.getFieldName())) {

      } else if ("adresse".equals(itemFilter.getFieldName())) {
        condition +=
            " and (lower(h.voie || ' - ' || h.voie2) like '%"
                + itemFilter.getValue().toLowerCase()
                + "%' or lower(h.voie)  like '%"
                + itemFilter.getValue().toLowerCase()
                + "%')";
      } else if ("numeroInterne".equals(itemFilter.getFieldName())) {
        condition += " and h.numero_interne = " + Integer.valueOf(itemFilter.getValue());
      } else {
        return condition;
      }
    }
    return condition;
  }

  /**
   * Bout de requête qui permet de récupérer l'autorité DECI
   *
   * @return
   */
  private String getAutoriteDeci() {
    return "with autoriteDeci AS (select h.autorite_deci as id,  "
        + " case "
        + "   when type_organisme.code = 'COMMUNE' then 'Maire (' || o.nom || ')'"
        + "   when type_organisme.code = 'EPCI' then 'Président (' || o.nom||')'"
        + "   when type_organisme.code = 'PREFECTURE' then 'Préfet (' || o.nom||')'"
        + " else o.nom end as autoriteDeci"
        + " from remocra.organisme o join remocra.type_organisme on type_organisme.id = o.type_organisme"
        + " join remocra.hydrant h on h.autorite_deci = o.id"
        + " group by h.autorite_deci, type_organisme.code, o.nom) ";
  }

  @Transactional
  public long countHydrants(List<ItemFilter> itemFilters) {
    String condition = this.getFilters(itemFilters);
    StringBuffer sbReq =
        new StringBuffer(getAutoriteDeci())
            .append(" select count(h.id) as  total")
            .append(" from remocra.hydrant h")
            .append(" join remocra.commune c on c.id = h.commune")
            .append(" join remocra.type_hydrant_nature_deci thnd on thnd.id = h.nature_deci")
            .append(" join remocra.type_hydrant_nature thn on thn.id = h.nature")
            .append(" left join autoriteDeci on autoriteDeci.id = h.autorite_deci")
            .append(" where")
            .append(" " + condition);

    return (Long) context.fetchOne(sbReq.toString()).getValue("total");
  }

  public Hydrant getById(Long id) {
    return context.selectFrom(HYDRANT).where(HYDRANT.ID.eq(id)).fetchOneInto(Hydrant.class);
  }

  /**
   * Permet d'update la position d'un hydrant en prenant en compte le changement de commune s'il y
   * a.
   *
   * @param id identifiant de l'hydrant
   * @param point la position de l'hydrant
   * @param srid
   * @return l'hydrant modifié
   * @throws BusinessException
   */
  private Hydrant updateHydrantDeplacement(Long id, Point point, Integer srid)
      throws BusinessException {
    Hydrant h = this.getById(id);
    if (h == null) {
      BusinessException e = new BusinessException("L'hydrant n'existe pas en base");
      throw e;
    }

    h.setDateGps(null);
    h.setDateModification(new Instant());
    h.setUtilisateurModification(utilisateurService.getCurrentUtilisateur().getId());
    h.setAuteurModificationFlag(AUTEUR_MODIFICATION_FLAG);
    h.setOrganisme(utilisateurService.getCurrentUtilisateur().getOrganisme().getId());
    point.setSRID(srid);
    h.setGeometrie(point);

    // Gestion de la commune
    List<Commune> listeCommune = Commune.findCommunesByPoint(srid, point.toString());
    if (!listeCommune.isEmpty()) {
      h.setCommune(listeCommune.get(0).getId());
    }
    return this.updateHydrant(h);
  }

  /**
   * Permet de déplacer un hydrant depuis la carte
   *
   * @param id
   * @param point
   * @param srid
   * @throws CRSException
   * @throws IllegalCoordinateException
   * @throws BusinessException
   */
  public void deplacer(Long id, Point point, Integer srid)
      throws CRSException, IllegalCoordinateException, BusinessException {
    fr.sdis83.remocra.db.model.remocra.tables.pojos.Hydrant h =
        this.updateHydrantDeplacement(id, point, srid);

    if (TYPE_HYDRANT_PENA.equalsIgnoreCase(h.getCode())) {
      try {
        String coordDFCI = GeometryUtil.findCoordDFCIFromGeom(dataSource, point);
        hydrantPenaRepository.updateCoorDdfci(id, coordDFCI);
      } catch (Exception e) {
        logger.debug("Problème lors de la requête sur la table remocra_referentiel.carro_dfci", e);
      }
    }
  }

  public Long getIdHydrantByNumero(String numero) {
    return context
        .select(HYDRANT.ID)
        .from(HYDRANT)
        .where(HYDRANT.NUMERO.eq(numero))
        .fetchOneInto(Long.class);
  }

  public void updateHydrantGestionnaire(Long idHydrant, Long newGestionnaireId) {
    context
        .update(HYDRANT)
        .set(HYDRANT.GESTIONNAIRE, newGestionnaireId)
        .where(HYDRANT.ID.eq(idHydrant))
        .execute();
  }

  public void updateHydrantGestionnaireSite(Long idHydrant, Long newGestionnaireSiteId) {
    context
        .update(HYDRANT)
        .set(HYDRANT.GESTIONNAIRE_SITE, newGestionnaireSiteId)
        .where(HYDRANT.ID.eq(idHydrant))
        .execute();
  }
}
