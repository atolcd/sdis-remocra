package fr.sdis83.remocra.repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.vividsolutions.jts.geom.Point;
import fr.sdis83.remocra.db.model.remocra.tables.pojos.Hydrant;
import fr.sdis83.remocra.db.model.remocra.tables.pojos.HydrantVisite;
import fr.sdis83.remocra.db.model.remocra.tables.pojos.TypeHydrantAnomalie;
import fr.sdis83.remocra.db.model.remocra.tables.pojos.TypeHydrantImportctpErreur;
import fr.sdis83.remocra.domain.remocra.Document;
import fr.sdis83.remocra.domain.remocra.TypeDroit;
import fr.sdis83.remocra.exception.ImportCTPException;
import fr.sdis83.remocra.security.AuthoritiesUtil;
import fr.sdis83.remocra.service.HydrantService;
import fr.sdis83.remocra.service.ParamConfService;
import fr.sdis83.remocra.service.UtilisateurService;
import fr.sdis83.remocra.util.DocumentUtil;
import fr.sdis83.remocra.util.JSONUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.joda.time.Instant;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

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

@Configuration
public class HydrantRepository {

  @Autowired
  DSLContext context;

  @Autowired
  HydrantVisiteRepository hydrantVisiteRepository;

  @Autowired
  HydrantPibiRepository hydrantPibiRepository;

  @Autowired
  HydrantPenaRepository hydrantPenaRepository;

  @Autowired
  HydrantService hydrantService;

  @Autowired
  ParamConfService paramConfService;

  @Autowired
  UtilisateurService utilisateurService;

  @PersistenceContext
  protected EntityManager entityManager;

  @Autowired
  private AuthoritiesUtil authUtils;

  public HydrantRepository() {
  }

  @Bean
  public HydrantRepository hydrantRepository(DSLContext context) {
    return new HydrantRepository(context);
  }

  HydrantRepository(DSLContext context) {
    this.context = context;
  }

  public void updateHydrantFromFiche(Long id, String jsonData, String typeHydrant, Map<String, MultipartFile> files) throws Exception {
    ObjectMapper objectMapper = new ObjectMapper();

    Map<String, Object> data = objectMapper.readValue(jsonData, new TypeReference<Map<String,Object>>(){});
    Map<String, Object> visitesData = objectMapper.readValue(data.get("visites").toString(), new TypeReference<Map<String,Object>>(){});
    Map<String, Object> geometrieData = objectMapper.readValue(data.get("geometrie").toString(), new TypeReference<Map<String,Object>>(){});

    Hydrant hydrant = context.selectFrom(HYDRANT).where(HYDRANT.ID.eq(id)).fetchOneInto(Hydrant.class);
    if(hydrant == null) {
      throw new Exception("Le pei spécifié n'existe pas");
    }

    Point geom = this.hydrantService.coordonneesToPoint(geometrieData.toString());

    // Données de l'hydrant
    Hydrant h = new Hydrant();
    h.setId(id);

    // Reprise du numéro, ne sera changé que si les conditions sont réunies (pas de numéro interne ou renumérotation activée)
    h.setNumero(hydrant.getNumero());

    h.setNumeroInterne(JSONUtil.getInteger(data, "numeroInterne"));
    h.setNature(JSONUtil.getLong(data, "nature"));
    h.setNatureDeci(JSONUtil.getLong(data, "natureDeci"));
    h.setAutoriteDeci(JSONUtil.getLong(data, "autoriteDeci"));
    h.setSpDeci(JSONUtil.getLong(data, "spDeci"));
    h.setMaintenanceDeci(JSONUtil.getLong(data, "maintenanceDeci"));
    h.setGestionnaire(JSONUtil.getLong(data, "gestionnaire"));
    h.setSite(JSONUtil.getLong(data, "site"));
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
    h.setAuteurModificationFlag("USER");
    h.setDateModification(new Instant());

    this.updateHydrant(h);

    // Visites
    this.hydrantVisiteRepository.deleteVisiteFromFiche(id, visitesData.get("deleteVisite").toString());
    this.hydrantVisiteRepository.addVisiteFromFiche(id, visitesData.get("addVisite").toString());

    // Caractéristiques techniques
    if("PIBI".equalsIgnoreCase(typeHydrant)) {
      this.hydrantPibiRepository.updateHydrantPibiFromFiche(id, data);
    } else if("PENA".equalsIgnoreCase(typeHydrant)) {
      this.hydrantPenaRepository.updateHydrantPenaFromFiche(id, data);
    }

    // Documents
    // TODO : lors du passage des documents en jooq, passer le code dans le repository
    if (files != null && !files.isEmpty()) {
      for (MultipartFile file : files.values()) {
        if (!file.isEmpty()) {
          Document d = DocumentUtil.getInstance().createNonPersistedDocument(Document.TypeDocument.HYDRANT, file, paramConfService.getDossierDocHydrant());
          String sousType = DocumentUtil.getInstance().getSousType(file);

          Long idDoc = context.insertInto(DOCUMENT)
            .set(DOCUMENT.CODE, d.getCode())
            .set(DOCUMENT.DATE_DOC, new Instant(d.getDateDoc()))
            .set(DOCUMENT.FICHIER, d.getFichier())
            .set(DOCUMENT.REPERTOIRE, d.getRepertoire())
            .set(DOCUMENT.TYPE, d.getType().toString())
            .returning(DOCUMENT.ID).fetchOne().getValue(DOCUMENT.ID);

          context.insertInto(HYDRANT_DOCUMENT)
            .set(HYDRANT_DOCUMENT.HYDRANT, id)
            .set(HYDRANT_DOCUMENT.DOCUMENT, idDoc)
            .execute();
        }
      }
    }
  }

  /**
   * Créé un hydrant depuis les informations passées par la fiche PEI
   * Une fois l'hydrant créé, on transmet les informations à la fonction d'update
   * @param jsonData Les données du PEI
   * @param typeHydrant Le type de PEI
   * @param files Les fichiers du PEI
   */
  public void createHydrantFromFiche(String jsonData, String typeHydrant, Map<String, MultipartFile> files) throws Exception {

    ObjectMapper objectMapper = new ObjectMapper();

    Map<String, Object> data = objectMapper.readValue(jsonData, new TypeReference<Map<String,Object>>(){});
    Map<String, Object> visitesData = objectMapper.readValue(data.get("visites").toString(), new TypeReference<Map<String,Object>>(){});
    Map<String, Object> geometrieData = objectMapper.readValue(data.get("geometrie").toString(), new TypeReference<Map<String,Object>>(){});

    Point geom = this.hydrantService.coordonneesToPoint(geometrieData.toString());

    Hydrant h = new Hydrant();
    h.setCode(typeHydrant);
    h.setNature(JSONUtil.getLong(data, "nature"));
    h.setNatureDeci(JSONUtil.getLong(data, "natureDeci"));
    h.setAutoriteDeci(JSONUtil.getLong(data, "autoriteDeci"));
    h.setSpDeci(JSONUtil.getLong(data, "spDeci"));
    h.setMaintenanceDeci(JSONUtil.getLong(data, "maintenanceDeci"));
    h.setGestionnaire(JSONUtil.getLong(data, "gestionnaire"));
    h.setSite(JSONUtil.getLong(data, "site"));
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
    h.setAuteurModificationFlag("USER");
    h.setDateModification(new Instant());

    Long id = this.createHydrant(h);

    if("PIBI".equalsIgnoreCase(typeHydrant)) {
      this.hydrantPibiRepository.createHydrantPibiFromFiche(id, data);
    } else if("PENA".equalsIgnoreCase(typeHydrant)) {
      this.hydrantPenaRepository.createHydrantPenaFromFiche(id, data);
    }

    this.hydrantVisiteRepository.deleteVisiteFromFiche(id, visitesData.get("deleteVisite").toString());
    this.hydrantVisiteRepository.addVisiteFromFiche(id, visitesData.get("addVisite").toString());

    // Documents
    // TODO : lors du passage des documents en jooq, passer le code dans le repository
    if (files != null && !files.isEmpty()) {
      for (MultipartFile file : files.values()) {
        if (!file.isEmpty()) {
          Document d = DocumentUtil.getInstance().createNonPersistedDocument(Document.TypeDocument.HYDRANT, file, paramConfService.getDossierDocHydrant());
          String sousType = DocumentUtil.getInstance().getSousType(file);

          Long idDoc = context.insertInto(DOCUMENT)
                  .set(DOCUMENT.CODE, d.getCode())
                  .set(DOCUMENT.DATE_DOC, new Instant(d.getDateDoc()))
                  .set(DOCUMENT.FICHIER, d.getFichier())
                  .set(DOCUMENT.REPERTOIRE, d.getRepertoire())
                  .set(DOCUMENT.TYPE, d.getType().toString())
                  .returning(DOCUMENT.ID).fetchOne().getValue(DOCUMENT.ID);

          context.insertInto(HYDRANT_DOCUMENT)
                  .set(HYDRANT_DOCUMENT.HYDRANT, id)
                  .set(HYDRANT_DOCUMENT.DOCUMENT, idDoc)
                  .execute();
        }
      }
    }
  }

  /**
   * Créé un hydrant en base
   * @return
   */
  private Long createHydrant(Hydrant h) {
    h = NumeroUtilRepository.setCodeZoneSpecAndNumeros(h, h.getCode());

    Long id = context
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
      .set(HYDRANT.SITE, h.getSite())
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
      .returning(HYDRANT.ID).fetchOne().getValue(HYDRANT.ID);

    return id;
  }

  /**
   * Met à jour un hydrant en base
   * @param h Les données de l'hydrant
   * @return L'hydrant mis à jour
   */
  private Hydrant updateHydrant(Hydrant h) {


    if((h.getNumeroInterne() == null || h.getNumeroInterne() <= 0) || paramConfService.getHydrantRenumerotationActivation()) {
      h = NumeroUtilRepository.setCodeZoneSpecAndNumeros(h, h.getCode());
    }

    context.update(HYDRANT)
      .set(HYDRANT.NUMERO, h.getNumero())
      .set(HYDRANT.NUMERO_INTERNE, h.getNumeroInterne())
      .set(HYDRANT.NATURE, h.getNature())
      .set(HYDRANT.NATURE_DECI, h.getNatureDeci())
      .set(HYDRANT.AUTORITE_DECI, h.getAutoriteDeci())
      .set(HYDRANT.SP_DECI, h.getSpDeci())
      .set(HYDRANT.MAINTENANCE_DECI, h.getMaintenanceDeci())
      .set(HYDRANT.GESTIONNAIRE, h.getGestionnaire())
      .set(HYDRANT.SITE, h.getSite())
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
      .where(HYDRANT.ID.eq(h.getId()))
      .execute();

    return context
      .selectFrom(HYDRANT)
      .where(HYDRANT.ID.eq(h.getId()))
      .fetchOneInto(Hydrant.class);
  }

  /**
   * Lors de l'mport des visites CTP via un fichier .xls ou .xlsx, vérifie la validité des données
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
    } catch(Exception e) {
      TypeHydrantImportctpErreur erreur = context.selectFrom(TYPE_HYDRANT_IMPORTCTP_ERREUR)
        .where(TYPE_HYDRANT_IMPORTCTP_ERREUR.CODE.eq("ERR_FICHIER_INNAC"))
        .fetchOneInto(TypeHydrantImportctpErreur.class);
      erreurFichier.put("bilan", erreur.getMessage());
      arrayResultatVerifications.add(erreurFichier);
      lecturePossible = false;
    }

    // Gestion erreur feuille n°2 (sur laquelle se trouve les données) n'existe pas
    Sheet sheet = null;
    try {
      if(lecturePossible) {
        sheet = workbook.getSheetAt(1);
        if (sheet == null || !"Saisies_resultats_CT".equals(sheet.getSheetName())) {
          throw new Exception("ERR_ONGLET_ABS");
        }
      }
    } catch(Exception e) {
        TypeHydrantImportctpErreur erreur = context.selectFrom(TYPE_HYDRANT_IMPORTCTP_ERREUR)
          .where(TYPE_HYDRANT_IMPORTCTP_ERREUR.CODE.eq("ERR_ONGLET_ABS"))
          .fetchOneInto(TypeHydrantImportctpErreur.class);
        erreurFichier.put("bilan", erreur.getMessage());
        arrayResultatVerifications.add(erreurFichier);
        lecturePossible = false;
    }

    // Lecture des valeurs
    if(lecturePossible) {
      for(int nbLigne = 4; nbLigne < sheet.getPhysicalNumberOfRows(); nbLigne++) {
        ObjectNode resultatVerification = null;
        try {
          Row r = sheet.getRow(nbLigne);
          if(r.getFirstCellNum() == 0) { // Evite de traiter la ligne "fantôme" détectée par la librairie à cause des combo des anomalies
            resultatVerification = this.checkLineValidity(r);
            resultatVerification.put("numero_ligne", nbLigne + 1);
            arrayResultatVerifications.add(resultatVerification);
          }
        } catch (ImportCTPException e) { // Interception d'une erreur : on arrête les vérifications et on indique la cause
          resultatVerification = e.getData();
          TypeHydrantImportctpErreur erreur = context.selectFrom(TYPE_HYDRANT_IMPORTCTP_ERREUR)
            .where(TYPE_HYDRANT_IMPORTCTP_ERREUR.CODE.eq(e.getCodeErreur()))
            .fetchOneInto(TypeHydrantImportctpErreur.class);
          resultatVerification.put("bilan", erreur.getMessage());
          resultatVerification.put("bilan_style", erreur.getType());
          resultatVerification.put("numero_ligne", nbLigne + 1);
          resultatVerification.remove("warnings");
          arrayResultatVerifications.add(resultatVerification);
        }
      }
    }
    data.set("bilanVerifications", arrayResultatVerifications);
    return data.toString();
  }

  /**
   * Lors de l'import des visites CTP, vérifie la validité d'une ligne donnée
   * @param row La ligne contenant toutes les cellules requises
   * @return ObjectNode un object JSON contenant les résultat de la validation pour cette ligne
   * @throws ImportCTPException En cas de donnée incorrecte, déclenche une exception gérée et traitée par la fonction parente
   */
  private ObjectNode checkLineValidity(Row row) throws ImportCTPException {
    ObjectMapper mapper = new ObjectMapper();
    ObjectNode data = mapper.createObjectNode();
    ArrayNode arrayWarnings = mapper.createArrayNode();
    data.put("bilan", "CT Validé");
    data.put("bilan_style", "OK");

    Integer xls_codeSdis = (int)row.getCell(0).getNumericCellValue();
    String xls_commune = row.getCell(1).getStringCellValue();
    String xls_insee = row.getCell(2).getStringCellValue();
    Integer xls_numeroInterne = (int)row.getCell(3).getNumericCellValue();

    SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
    data.put("insee", xls_insee);
    data.put("numeroInterne", xls_numeroInterne);

    ObjectNode dataVisite = mapper.createObjectNode();
    SimpleDateFormat formatterDateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:00");

    // Vérification si l'hydrant renseigné correspond bien à l'hydrant en base
    Hydrant h = context.select(HYDRANT.fields())
      .from(HYDRANT)
      .join(COMMUNE).on(COMMUNE.ID.eq(HYDRANT.COMMUNE))
      .where(COMMUNE.INSEE.eq(xls_insee)).and(HYDRANT.NUMERO_INTERNE.eq(xls_numeroInterne))
      .fetchOneInto(Hydrant.class);

    if(h == null || (h.getId() != xls_codeSdis.longValue())) {
      throw new ImportCTPException("ERR_MAUVAIS_NUM_PEI", data);
    }

    // On vérifie si le PEI est bien dans la zone de compétence de l'utilisateur
    Boolean dansZoneCompetence = context.resultQuery("SELECT ST_CONTAINS(zc.geometrie, h.geometrie) " +
        "FROM remocra.zone_competence zc " +
        "JOIN remocra.hydrant h on h.id = {0}" +
        "WHERE zc.id = {1};",
      h.getId(),
      this.utilisateurService.getCurrentZoneCompetenceId()).fetchOneInto(Boolean.class);

    if(dansZoneCompetence == null || !dansZoneCompetence) {
      throw new ImportCTPException("ERR_DEHORS_ZC", data);
    }

    // Si la visite CTP n'est pas renseignée (si tous les champs composant les informations de la visite sont vides)
    boolean ctpRenseigne = false;
    for(int i = 9; i < 18; i++) {
      if(row.getCell(i) != null && row.getCell(i).getCellType() != CellType.BLANK) {
        ctpRenseigne = true;
      }
    }
    if(!ctpRenseigne) {
      // On passe par un throw car à ce stade on a déjà toutes les infos que l'on souhaite afficher
      // On n'a pas besoin de continuer les vérifications
      throw new ImportCTPException("INFO_IGNORE", data);
    }

    // Vérifications au niveau de la date
    if(row.getCell(9) == null || row.getCell(9).getCellType() == CellType.BLANK) {
      throw new ImportCTPException("ERR_DATE_MANQ", data);
    }

    Date xls_dateCtp = null;
    try {
      xls_dateCtp = row.getCell(9).getDateCellValue();
    }catch(Exception e) {
      throw new ImportCTPException("ERR_FORMAT_DATE", data);
    }

    if(xls_dateCtp.after(new Date())) {
      throw new ImportCTPException("ERR_DATE_POST", data);
    }

    Integer nbVisite = context.selectCount()
        .from(HYDRANT_VISITE)
          .join(TYPE_HYDRANT_SAISIE).on(HYDRANT_VISITE.TYPE.eq(TYPE_HYDRANT_SAISIE.ID))
        .where(HYDRANT_VISITE.HYDRANT.eq(h.getId()).and(TYPE_HYDRANT_SAISIE.CODE.eq("CTRL")).and(HYDRANT_VISITE.CTRL_DEBIT_PRESSION)
          .and(HYDRANT_VISITE.DATE.greaterThan(new Instant(xls_dateCtp)))).fetchOneInto(Integer.class);
    if(nbVisite > 0) {
      String str = context.select(TYPE_HYDRANT_IMPORTCTP_ERREUR.MESSAGE)
        .from(TYPE_HYDRANT_IMPORTCTP_ERREUR)
        .where(TYPE_HYDRANT_IMPORTCTP_ERREUR.CODE.eq("WARN_DATE_ANTE"))
        .fetchOneInto(String.class);
      arrayWarnings.add(str);
    }

    // On vérifie que le PEI dispose de ses deux premières visites (réception et ROI) pour pouvoir lui adjoindre une visite CTP
    nbVisite = context.selectCount()
        .from(HYDRANT_VISITE)
        .where(HYDRANT_VISITE.HYDRANT.eq(h.getId()))
        .fetchOneInto(Integer.class);
    if(nbVisite < 2) {
      throw new ImportCTPException("ERR_VISITES_MANQUANTES", data);
    }


    data.put("dateCtp", formatter.format(xls_dateCtp));

    String xls_agent1 = null;
    if(row.getCell(10) == null || row.getCell(10).getCellType() == CellType.BLANK) {
      throw new ImportCTPException("ERR_AGENT1_ABS", data);
    } else {
      xls_agent1 = row.getCell(10).getStringCellValue();
    }


    Integer xls_debit = null;
    try {
      if(row.getCell(12) != null && row.getCell(12).getCellType() != CellType.BLANK) { // Si une valeur est renseignée
        xls_debit = (int) row.getCell(12).getNumericCellValue();

        if(row.getCell(12).getNumericCellValue() % 1 != 0) { // Si on a réalisé une troncature lors de la lecture de la valeur
          TypeHydrantImportctpErreur info = context.selectFrom(TYPE_HYDRANT_IMPORTCTP_ERREUR)
            .where(TYPE_HYDRANT_IMPORTCTP_ERREUR.CODE.eq("INFO_TRONC_DEBIT"))
            .fetchOneInto(TypeHydrantImportctpErreur.class);
          data.put("bilan", info.getMessage());
          data.put("bilan_style", info.getType());
        }
        if (xls_debit < 0) {
          throw new Exception();
        }
      }
    } catch(Exception e) {
      throw new ImportCTPException("ERR_FORMAT_DEBIT", data);
    }

    Double xls_pression = null;
    try {
      if(row.getCell(11) != null && row.getCell(11).getCellType() != CellType.BLANK) { // Si une valeur est renseignée
        xls_pression = row.getCell(11).getNumericCellValue();
        if (xls_pression < 0) {
          throw new Exception();
        }
      }
    } catch(Exception e) {
      throw new ImportCTPException("ERR_FORMAT_PRESS", data);
    }

    if(xls_pression != null && xls_pression > 20.0) {
      throw new ImportCTPException("ERR_PRESS_ELEVEE", data);
    }

    String warningDebitPression = null;
    if(xls_pression == null && xls_debit == null) {
      warningDebitPression = "WARN_DEB_PRESS_VIDE";
    } else if (xls_pression == null) {
      warningDebitPression = "WARN_PRESS_VIDE";
    } else if (xls_debit == null) {
      warningDebitPression = "WARN_DEBIT_VIDE";
    }

    if(warningDebitPression != null) {
      String str = context.select(TYPE_HYDRANT_IMPORTCTP_ERREUR.MESSAGE)
        .from(TYPE_HYDRANT_IMPORTCTP_ERREUR)
        .where(TYPE_HYDRANT_IMPORTCTP_ERREUR.CODE.eq(warningDebitPression))
        .fetchOneInto(String.class);
      arrayWarnings.add(str);
    }


    /* Si l'utilisateur a le droit de déplacer un PEI, on affiche un warning si la distance de déplacement est supérieure
       a la distance renseignée dans les paramètres de l'application */
    if(this.authUtils.hasRight(TypeDroit.TypeDroitEnum.HYDRANTS_DEPLACEMENT_C)) {
      Double latitude = null;
      Double longitude = null;
      try {
        latitude = this.readCoord(row.getCell(5));
        longitude = this.readCoord(row.getCell(6));
      } catch(Exception e) {
        throw new ImportCTPException("ERR_COORD_GPS", data);
      }

      Integer distance = context.resultQuery("SELECT ST_DISTANCE(ST_transform(ST_SetSRID(ST_MakePoint({0}, {1}),4326), 2154), h.geometrie) " +
          "FROM remocra.hydrant h " +
          "WHERE h.id = {2};",
        longitude, latitude, h.getId()).fetchOneInto(Integer.class);

      if(distance > this.paramConfService.getHydrantDeplacementDistWarn()) {
        String str = context.select(TYPE_HYDRANT_IMPORTCTP_ERREUR.MESSAGE)
          .from(TYPE_HYDRANT_IMPORTCTP_ERREUR)
          .where(TYPE_HYDRANT_IMPORTCTP_ERREUR.CODE.eq("WARN_DEPLACEMENT"))
          .fetchOneInto(String.class);
        arrayWarnings.add(str);
      }

      dataVisite.put("latitude", latitude);
      dataVisite.put("longitude", longitude);
    }

    //Vérifications anomalies
    ArrayList<Long> id_anomalies = new ArrayList<Long>();

    // On récupère les identifiants des anomalies inscrites dans le fichier
    for(int i = 13; i < 17; i++) {
      if(row.getCell(i) != null && row.getCell(i).getCellType() != CellType.BLANK) {
        String xls_anomalie = row.getCell(i).getStringCellValue();
        String code = xls_anomalie.substring(xls_anomalie.indexOf('-')+2);
        TypeHydrantAnomalie anomalie = context.selectFrom(TYPE_HYDRANT_ANOMALIE)
          .where(TYPE_HYDRANT_ANOMALIE.CODE.upper().eq(code.toUpperCase()))
          .fetchOneInto(TypeHydrantAnomalie.class);
        if(anomalie == null) {
          throw new ImportCTPException("ERR_ANO_INCONNU", data);
        }
        id_anomalies.add(anomalie.getId());
      }
    }

    // On récupère les anomalies de la visite précédente qui ne sont pas possibles pour un contexte CTP
    HydrantVisite derniereVisite = context
      .selectFrom(HYDRANT_VISITE)
      .where(HYDRANT_VISITE.HYDRANT.eq(h.getId())).and(HYDRANT_VISITE.DATE.lessThan(new Instant(xls_dateCtp)))
      .orderBy(HYDRANT_VISITE.DATE.desc()).limit(1).fetchOneInto(HydrantVisite.class);
    if(derniereVisite != null && derniereVisite.getAnomalies() != null && derniereVisite.getAnomalies().length() > 0) {
      String[] strArrayAnomalies = derniereVisite.getAnomalies().replaceAll("\\[", "").replaceAll("]", "").replaceAll(" ", "").split(",");
      ArrayList<Long> idAnomaliesDerniereVisite = new ArrayList<Long>();

      // Récupération de toutes les anomalies possibles pour ce contexte
      ArrayList<Long> idAnomaliesCTP = (ArrayList<Long>) context
        .select(TYPE_HYDRANT_ANOMALIE.ID)
        .from(TYPE_HYDRANT_ANOMALIE)
        .join(TYPE_HYDRANT_ANOMALIE_NATURE).on(TYPE_HYDRANT_ANOMALIE_NATURE.ANOMALIE.eq(TYPE_HYDRANT_ANOMALIE.ID))
        .join(TYPE_HYDRANT_ANOMALIE_NATURE_SAISIES).on(TYPE_HYDRANT_ANOMALIE_NATURE_SAISIES.TYPE_HYDRANT_ANOMALIE_NATURE.eq(TYPE_HYDRANT_ANOMALIE_NATURE.ID))
        .join(TYPE_HYDRANT_SAISIE).on(TYPE_HYDRANT_SAISIE.ID.eq(TYPE_HYDRANT_ANOMALIE_NATURE_SAISIES.SAISIES))
        .where(TYPE_HYDRANT_SAISIE.CODE.equalIgnoreCase("CTRL"))
        .fetchInto(Long.class);

      for(String s : strArrayAnomalies) {
        if(StringUtils.isNotEmpty(s)){
          Long l = Long.parseLong(s);
          // Si une anomalie est trouvée et n'existe pas pour le contexte CTP, on la reprends de la visite précédente
          if(idAnomaliesCTP.indexOf(l) == -1) {
            id_anomalies.add(l);
          }
        }


      }
    }

    ArrayNode arrayAnomalies = mapper.createArrayNode();
    for(Long i : id_anomalies) {
      arrayAnomalies.add(i);
    }

    String observation = (row.getLastCellNum()-1 >= 18) ? row.getCell(18).getStringCellValue() : null;

    /**
     * Ajout des données de la visite à ajouter aux informations JSON
     * Ces données ont déjà été vérifiées ici, il n'y a pas besoin de dupliquer les vérifications avant l'ajout en base
     */
    dataVisite.set("anomalies", arrayAnomalies);
    dataVisite.put("date", formatterDateTime.format(xls_dateCtp));
    dataVisite.put("hydrant", h.getId());
    dataVisite.put("agent1", xls_agent1);
    dataVisite.put("debit", xls_debit);
    dataVisite.put("pression", xls_pression);
    dataVisite.put("observation", observation);

    data.set("dataVisite", dataVisite);

    if(arrayWarnings.size() > 0) {
      data.set("warnings", arrayWarnings);
      data.put("bilan_style", "WARNING");
    }
    return data;
  }

  /**
   * Lit une coordonnée depuis une cellule
   * La coordonnée doit être au format décimal point ou virgule
   * @param c La cellule contentant la donnée
   * @return La valeur de la coordonnée de type Double
   * @throws Exception La valeur ne respecte pas le format attendu
   */
  Double readCoord(Cell c) throws Exception {
    if(c.getCellType() == CellType.NUMERIC) {
     return c.getNumericCellValue();
    }

    if(c.getCellType() != CellType.STRING) {
      throw new Exception();
    }

    String str_cell = c.getStringCellValue().replaceAll(",", ".");
    if (!str_cell.matches("([+-]?\\d+\\.?\\d+)\\s*")) {
      throw new Exception();
    }
    return Double.parseDouble(str_cell);
  }

}
