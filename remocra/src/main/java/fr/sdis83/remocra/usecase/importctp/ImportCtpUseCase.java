package fr.sdis83.remocra.usecase.importctp;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.PrecisionModel;
import fr.sdis83.remocra.GlobalConstants;
import fr.sdis83.remocra.db.model.remocra.tables.pojos.HydrantVisite;
import fr.sdis83.remocra.db.model.remocra.tables.pojos.TypeHydrantAnomalie;
import fr.sdis83.remocra.db.model.remocra.tables.pojos.TypeHydrantImportctpErreur;
import fr.sdis83.remocra.domain.remocra.TypeDroit;
import fr.sdis83.remocra.enums.TypeErreurImportCtp;
import fr.sdis83.remocra.exception.ImportCTPException;
import fr.sdis83.remocra.repository.HydrantRepository;
import fr.sdis83.remocra.repository.HydrantVisiteRepository;
import fr.sdis83.remocra.repository.TypeHydrantAnomalieRepository;
import fr.sdis83.remocra.repository.TypeHydrantImportCtpErreurRepository;
import fr.sdis83.remocra.security.AuthoritiesUtil;
import fr.sdis83.remocra.service.UtilisateurService;
import fr.sdis83.remocra.usecase.parametre.ParametreDataProvider;
import fr.sdis83.remocra.util.GeometryUtil;
import fr.sdis83.remocra.util.JSONUtil;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.cts.IllegalCoordinateException;
import org.cts.crs.CRSException;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.multipart.MultipartFile;

@Configuration
public class ImportCtpUseCase {
  // Constantes correspondant à la structure du fichier d'entrée
  private static final String ONGLET_SAISIE = "Saisies_resultats_CT";
  private static final int ONGLET_SAISIE_INDEX = 1;
  private static final int NB_LIGNES_RESERVEES = 4;

  private static final int LATITUDE_INDEX = 5;
  private static final int LONGITUDE_INDEX = 6;
  private static final int DEBUT_CTP_INDEX = 9;
  private static final int FIN_CTP_INDEX = 18;
  private static final int CTP_DATE_INDEX = 9;
  private static final int CTP_AGENT1_INDEX = 10;
  private static final int CTP_PRESSION_INDEX = 11;
  private static final int CTP_DEBIT_INDEX = 12;
  private static final int CTP_DEBUT_ANOMALIE_INDEX = 13;
  private static final int CTP_FIN_ANOMALIE_INDEX = 17;
  private static final int CTP_OBSERVATIONS_INDEX = 18;

  private static final double PRESSION_MAX = 20.0;

  // TODO changer le séparateur pour un caractère non possible, '_' par ex
  private static final String ANOMALIES_SEPARATEUR = "-";

  @Autowired private TypeHydrantImportCtpErreurRepository typeHydrantImportCtpErreurRepository;

  @Autowired private HydrantRepository hydrantRepository;

  @Autowired private HydrantVisiteRepository hydrantVisiteRepository;

  @Autowired private UtilisateurService utilisateurService;

  @Autowired private AuthoritiesUtil authUtils;
  @Autowired private ParametreDataProvider parametreDataProvider;

  @Autowired TypeHydrantAnomalieRepository typeHydrantAnomalieRepository;

  /**
   * Crée un noeud d'erreur global
   *
   * @param mapTypesErreur La map des erreurs possibles récupérée depuis la BDD
   * @param typeErreur TypeErreurImportCtp à présenter
   * @return LigneImportCtpData
   */
  private LigneImportCtpData buildGlobalErrorData(
      Map<String, TypeHydrantImportctpErreur> mapTypesErreur, TypeErreurImportCtp typeErreur) {
    TypeHydrantImportctpErreur erreur = mapTypesErreur.get(typeErreur.getCode());
    LigneImportCtpData ligneData = new LigneImportCtpData();
    ligneData.setNumeroLigne(0);
    ligneData.setBilanStyle(LigneImportCtpData.BilanStyle.valueOf(erreur.getType()));
    ligneData.setBilan(erreur.getMessage());

    return ligneData;
  }

  /**
   * Lors de l'mport des visites CTP via un fichier .xls ou .xlsx, vérifie la validité des données
   *
   * @param file Le fichier importé
   * @return String Le résultat de la vérification au format JSON
   */
  public String importCTP(MultipartFile file) throws JsonProcessingException {
    ObjectMapper mapper = new ObjectMapper();
    mapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);

    ImportCtpData data = new ImportCtpData();
    List<LigneImportCtpData> resultatVerifications = new ArrayList<>();

    // On récupère tous les types d'erreur en une seule fois pour accès rapide au travers d'une map
    Map<String, TypeHydrantImportctpErreur> mapTypesErreur =
        typeHydrantImportCtpErreurRepository.getAll();

    // On récupère toutes les anomalies pour accès rapide
    Map<String, TypeHydrantAnomalie> mapAnomalies = typeHydrantAnomalieRepository.getMapByCode();

    Workbook workbook;
    // Gestion erreur fichier illisible
    try {
      workbook = WorkbookFactory.create(file.getInputStream());
      workbook.setMissingCellPolicy(Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
    } catch (Exception e) {
      resultatVerifications.add(
          buildGlobalErrorData(mapTypesErreur, TypeErreurImportCtp.ERR_FICHIER_INNAC));
      data.setBilanVerifications(resultatVerifications);
      return data.toString();
    }

    // Gestion erreur feuille n°2 (sur laquelle se trouvent les données) n'existe pas
    Sheet sheet;
    try {
      sheet = workbook.getSheetAt(ONGLET_SAISIE_INDEX);
      if (sheet == null || !ONGLET_SAISIE.equals(sheet.getSheetName())) {
        throw new Exception();
      }
    } catch (Exception e) {
      resultatVerifications.add(
          buildGlobalErrorData(mapTypesErreur, TypeErreurImportCtp.ERR_ONGLET_ABS));
      data.setBilanVerifications(resultatVerifications);
      return data.toString();
    }

    // Lecture des valeurs
    int nbLigne = NB_LIGNES_RESERVEES;
    boolean estFini = false;
    while (nbLigne < sheet.getPhysicalNumberOfRows() && !estFini) {
      LigneImportCtpData resultatVerification;
      try {
        Row r = sheet.getRow(nbLigne);
        if (r.getFirstCellNum() == 0 && r.getCell(0) != null) {
          // Evite de traiter la ligne "fantôme" détectée par la librairie à cause des  combo des
          // anomalies
          resultatVerification = this.checkLineValidity(r, mapTypesErreur, mapAnomalies);
          resultatVerification.setNumeroLigne(nbLigne + 1);
          resultatVerifications.add(resultatVerification);
        } else {
          estFini = true;
        }
      } catch (ImportCTPException e) {
        resultatVerification = e.getData();
        resultatVerification.setBilan(mapTypesErreur.get(e.getTypeErreur().getCode()).getMessage());
        resultatVerification.setBilanStyle(
            LigneImportCtpData.BilanStyle.valueOf(
                mapTypesErreur.get(e.getTypeErreur().getCode()).getType()));
        resultatVerification.setNumeroLigne(nbLigne + 1);
        resultatVerifications.add(resultatVerification);
        resultatVerification.removeWarnings();
      }
      nbLigne++;
    }
    data.setBilanVerifications(resultatVerifications);
    return mapper.writeValueAsString(data);
  }

  /**
   * Retourne une valeur textuelle de la cellule même quand excel la formate sous forme numérique
   * (Ex : code Insee)
   *
   * @param c Cell
   * @return String la représentation en String de la valeur de la cellule
   */
  private String getStringValueFromCell(Cell c) {
    if (c.getCellType() == org.apache.poi.ss.usermodel.CellType.STRING) {
      return c.getStringCellValue();
    }
    if (c.getCellType() == org.apache.poi.ss.usermodel.CellType.NUMERIC) {
      return String.valueOf((int) c.getNumericCellValue());
    }
    return null;
  }

  /**
   * Lit un nombre réel depuis une cellule. La valeur doit être au format décimal point ou virgule
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
   * Lors de l'import des visites CTP, vérifie la validité d'une ligne donnée
   *
   * @param row La ligne contenant toutes les cellules requises
   * @param mapTypesErreur Les types d'erreurs possibles
   * @param mapAnomalies Les anomalies possibles
   * @return DataImportCtp un POJO contenant les résultat de la validation pour cette ligne, à
   *     sérialiser
   * @throws ImportCTPException En cas de donnée incorrecte, déclenche une exception gérée et
   *     traitée par la fonction parente
   */
  private LigneImportCtpData checkLineValidity(
      Row row,
      Map<String, TypeHydrantImportctpErreur> mapTypesErreur,
      Map<String, TypeHydrantAnomalie> mapAnomalies)
      throws ImportCTPException {
    ObjectMapper mapper = new ObjectMapper();
    LigneImportCtpData data = new LigneImportCtpData();
    List<String> warnings = new ArrayList<>();
    data.setBilan("CT Validé");
    data.setBilanStyle(LigneImportCtpData.BilanStyle.OK);

    int idHydrant = (int) row.getCell(CellDefinition.ID_HYDRANT.index).getNumericCellValue();
    String codeInsee = this.getStringValueFromCell(row.getCell(CellDefinition.CODE_INSEE.index));
    int numeroInterne =
        (int) row.getCell(CellDefinition.NUMERO_INTERNE.index).getNumericCellValue();

    SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
    data.setInsee(codeInsee);
    data.setNumeroInterne(numeroInterne);

    SimpleDateFormat formatterDateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:00");

    // Vérification si l'hydrant renseigné correspond bien à l'hydrant en base
    if (!hydrantRepository.checkForImportCTP(idHydrant, numeroInterne, codeInsee)) {
      throw new ImportCTPException(TypeErreurImportCtp.ERR_MAUVAIS_NUM_PEI, data);
    }

    // On vérifie si le PEI est bien dans la zone de compétence de l'utilisateur
    if (!hydrantRepository.withinZoneCompetence(
        idHydrant, this.utilisateurService.getCurrentZoneCompetenceId())) {
      throw new ImportCTPException(TypeErreurImportCtp.ERR_DEHORS_ZC, data);
    }

    // Si la visite CTP n'est pas renseignée (si tous les champs composant les informations de la
    // visite sont vides)
    boolean ctpRenseigne = false;
    for (int i = DEBUT_CTP_INDEX; i < FIN_CTP_INDEX; i++) {
      if (row.getCell(i) != null && row.getCell(i).getCellType() != CellType.BLANK) {
        ctpRenseigne = true;
      }
    }
    if (!ctpRenseigne) {
      // On passe par un throw car à ce stade on a déjà toutes les infos que l'on souhaite afficher
      // On n'a pas besoin de continuer les vérifications
      throw new ImportCTPException(TypeErreurImportCtp.INFO_IGNORE, data);
    }

    // Vérifications au niveau de la date
    if (row.getCell(CTP_DATE_INDEX) == null
        || row.getCell(CTP_DATE_INDEX).getCellType() == CellType.BLANK) {
      throw new ImportCTPException(TypeErreurImportCtp.ERR_DATE_MANQ, data);
    }

    Date dateCtp;
    try {
      dateCtp = row.getCell(CTP_DATE_INDEX).getDateCellValue();
    } catch (Exception e) {
      throw new ImportCTPException(TypeErreurImportCtp.ERR_FORMAT_DATE, data);
    }

    if (dateCtp.after(new Date())) {
      throw new ImportCTPException(TypeErreurImportCtp.ERR_DATE_POST, data);
    }

    Instant instantCtp = new Instant(dateCtp);

    int nbVisite = hydrantRepository.getNbVisitesCtrlWithCondition(idHydrant, instantCtp);
    if (nbVisite > 0) {
      warnings.add(mapTypesErreur.get(TypeErreurImportCtp.WARN_DATE_ANTE.getCode()).getMessage());
    }

    // On vérifie que le PEI dispose de ses deux premières visites (réception et ROI) pour pouvoir
    // lui adjoindre une visite CTP
    nbVisite = hydrantRepository.getNbVisites(idHydrant);
    if (nbVisite < 2) {
      throw new ImportCTPException(TypeErreurImportCtp.ERR_VISITES_MANQUANTES, data);
    }

    // On vérifie si il n'y pas de visite à la même date et heure
    nbVisite = hydrantRepository.getNbVisitesCtrlEqInstant(idHydrant, instantCtp);
    if (nbVisite > 0) {
      throw new ImportCTPException(TypeErreurImportCtp.ERR_VISITE_EXISTANTE, data);
    }

    data.setDateCtp(formatter.format(dateCtp));

    String xls_agent1;
    if (row.getCell(CTP_AGENT1_INDEX) == null
        || row.getCell(CTP_AGENT1_INDEX).getCellType() == CellType.BLANK) {
      throw new ImportCTPException(TypeErreurImportCtp.ERR_AGENT1_ABS, data);
    } else {
      xls_agent1 = row.getCell(CTP_AGENT1_INDEX).getStringCellValue();
    }

    Integer xls_debit = null;
    try {
      if (row.getCell(CTP_DEBIT_INDEX) != null
          && row.getCell(CTP_DEBIT_INDEX).getCellType() != CellType.BLANK) {
        xls_debit = (int) row.getCell(CTP_DEBIT_INDEX).getNumericCellValue();

        if (row.getCell(CTP_DEBIT_INDEX).getNumericCellValue() % 1 != 0) {
          // Si on a réalisé une troncature lors de la lecture de la valeur
          TypeHydrantImportctpErreur info =
              mapTypesErreur.get(TypeErreurImportCtp.INFO_TRONC_DEBIT.getCode());
          data.setBilan(info.getMessage());
          data.setBilanStyle(LigneImportCtpData.BilanStyle.valueOf(info.getType()));
        }
        if (xls_debit < 0) {
          throw new Exception();
        }
      }
    } catch (Exception e) {
      throw new ImportCTPException(TypeErreurImportCtp.ERR_FORMAT_DEBIT, data);
    }

    Double xls_pression = null;
    try {
      if (row.getCell(CTP_PRESSION_INDEX) != null
          && row.getCell(CTP_PRESSION_INDEX).getCellType() != CellType.BLANK) {
        xls_pression = this.getNumericValueFromCell(row.getCell(CTP_PRESSION_INDEX));
        if (xls_pression < 0) {
          throw new Exception();
        }
      }
    } catch (Exception e) {
      throw new ImportCTPException(TypeErreurImportCtp.ERR_FORMAT_PRESS, data);
    }

    if (xls_pression != null && xls_pression > PRESSION_MAX) {
      throw new ImportCTPException(TypeErreurImportCtp.ERR_PRESS_ELEVEE, data);
    }

    TypeErreurImportCtp warningDebitPression = null;
    if (xls_pression == null && xls_debit == null) {
      warningDebitPression = TypeErreurImportCtp.WARN_DEB_PRESS_VIDE;
    } else if (xls_pression == null) {
      warningDebitPression = TypeErreurImportCtp.WARN_PRESS_VIDE;
    } else if (xls_debit == null) {
      warningDebitPression = TypeErreurImportCtp.WARN_DEBIT_VIDE;
    }

    if (warningDebitPression != null) {
      warnings.add(mapTypesErreur.get(warningDebitPression.getCode()).getMessage());
    }

    LigneImportCtpVisiteData dataVisite = new LigneImportCtpVisiteData();

    // Si l'utilisateur a le droit de déplacer un PEI, on affiche un warning si la distance de
    // déplacement est supérieure à la distance renseignée dans les paramètres de l'application
    if (this.authUtils.hasRight(TypeDroit.TypeDroitEnum.HYDRANTS_DEPLACEMENT_C)) {
      Double latitude;
      Double longitude;
      try {
        latitude = this.getNumericValueFromCell(row.getCell(LATITUDE_INDEX));
        longitude = this.getNumericValueFromCell(row.getCell(LONGITUDE_INDEX));
      } catch (Exception e) {
        throw new ImportCTPException(TypeErreurImportCtp.ERR_COORD_GPS, data);
      }

      int distance = hydrantRepository.getDistanceFromCoordonnees(idHydrant, latitude, longitude);

      if (distance > parametreDataProvider.get().getHydrantDeplacementDistWarn()) {
        warnings.add(
            mapTypesErreur.get(TypeErreurImportCtp.WARN_DEPLACEMENT.getCode()).getMessage());
      }

      dataVisite.setLatitude(latitude);
      dataVisite.setLongitude(longitude);
    }

    // Vérifications anomalies
    List<Long> id_anomalies = new ArrayList<>();

    // On récupère les identifiants des anomalies inscrites dans le fichier
    for (int i = CTP_DEBUT_ANOMALIE_INDEX; i < CTP_FIN_ANOMALIE_INDEX; i++) {
      if (row.getCell(i) != null
          && row.getCell(i).getCellType() != CellType.BLANK
          && !row.getCell(i).getStringCellValue().trim().isEmpty()) {
        String xls_anomalie = row.getCell(i).getStringCellValue();
        String codeAnomalie = xls_anomalie.split(ANOMALIES_SEPARATEUR)[1].toUpperCase();

        TypeHydrantAnomalie anomalie = mapAnomalies.get(codeAnomalie);
        if (anomalie == null) {
          throw new ImportCTPException(TypeErreurImportCtp.ERR_ANO_INCONNU, data);
        }
        id_anomalies.add(anomalie.getId());
      }
    }

    // On récupère les anomalies de la visite précédente qui ne sont pas possibles pour un contexte
    // CTP
    HydrantVisite derniereVisite = hydrantRepository.getDernierVisite(idHydrant, instantCtp);
    if (derniereVisite != null
        && derniereVisite.getAnomalies() != null
        && !derniereVisite.getAnomalies().isEmpty()) {
      String[] strArrayAnomalies =
          derniereVisite
              .getAnomalies()
              .replaceAll("\\[", "")
              .replaceAll("]", "")
              .replaceAll(" ", "")
              .split(",");
      // Récupération de toutes les anomalies possibles pour ce contexte
      List<Long> idAnomaliesCTP = typeHydrantAnomalieRepository.getIdsAnomaliesCTP();

      for (String s : strArrayAnomalies) {
        if (StringUtils.isNotEmpty(s)) {
          Long l = Long.parseLong(s);
          // Si une anomalie est trouvée et n'existe pas pour le contexte CTP, on la reprend de la
          // visite précédente
          if (!idAnomaliesCTP.contains(l)) {
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
        (row.getLastCellNum() - 1 >= FIN_CTP_INDEX) && row.getCell(CTP_OBSERVATIONS_INDEX) != null
            ? row.getCell(CTP_OBSERVATIONS_INDEX).getStringCellValue()
            : null;

    // Ajout des données de la visite à ajouter aux informations JSON Ces données ont déjà été
    // vérifiées ici, il n'y a pas besoin de dupliquer les vérifications avant l'ajout en base
    new LigneImportCtpVisiteData(
        id_anomalies,
        formatterDateTime.format(dateCtp),
        idHydrant,
        xls_agent1,
        xls_debit,
        xls_pression,
        observation);

    data.setDataVisite(dataVisite);

    if (!warnings.isEmpty()) {
      data.addAllWarnings(warnings);
      data.setBilanStyle(LigneImportCtpData.BilanStyle.WARNING);
    }
    return data;
  }

  private enum CellDefinition {
    ID_HYDRANT(0, "Identifiant de l'hydrant", RemocraCellType.INT),
    COMMUNE(1, "Commune - non utilisé", RemocraCellType.STRING),
    CODE_INSEE(2, "Code INSEE de la commune", RemocraCellType.STRING),
    NUMERO_INTERNE(3, "Numéro interne", RemocraCellType.INT);

    private final int index;

    CellDefinition(int index, String definition, RemocraCellType cellType) {
      this.index = index;
    }
  }

  protected enum RemocraCellType {
    STRING,
    INT
  }

  public void addVisiteFromImportCtp(String visitesData)
      throws IOException, IllegalCoordinateException, CRSException {
    ObjectMapper objectMapper = new ObjectMapper();
    List<Map<String, Object>> data =
        objectMapper.readValue(visitesData, new TypeReference<ArrayList<Map<String, Object>>>() {});
    Long idTypeVisiteControle = hydrantVisiteRepository.getIdControle();

    for (Map<String, Object> visite : data) {
      HydrantVisite v = new HydrantVisite();

      if (JSONUtil.getLong(visite, "hydrant") == null) {
        throw new IllegalArgumentException("id hydrant null");
      }
      long idHydrant = JSONUtil.getLong(visite, "hydrant");
      v.setHydrant(idHydrant);
      v.setDate(JSONUtil.getInstant(visite, "date"));
      v.setAgent1(JSONUtil.getString(visite, "agent1"));
      v.setDebit(JSONUtil.getInteger(visite, "debit"));
      v.setPression(JSONUtil.getDouble(visite, "pression"));
      v.setObservations(JSONUtil.getString(visite, "observation"));
      v.setType(idTypeVisiteControle);
      v.setCtrlDebitPression(v.getDebit() != null && v.getPression() != null);
      v.setAnomalies(JSONUtil.getString(visite, "anomalies"));

      HydrantVisite newVisite = hydrantVisiteRepository.addVisite(v);

      // Si données de longitude/latitude fournies, on déplace le PEI
      Double latitude = JSONUtil.getDouble(visite, "latitude");
      Double longitude = JSONUtil.getDouble(visite, "longitude");

      int srid =
          Integer.parseInt(parametreDataProvider.get().getValeurString(GlobalConstants.CLE_SRID));

      if (latitude != null
          && longitude != null
          && this.authUtils.hasRight(TypeDroit.TypeDroitEnum.HYDRANTS_DEPLACEMENT_CTP_C)) {
        GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), srid);
        double[] coordonnees =
            GeometryUtil.transformCordinate(longitude, latitude, "4326", String.valueOf(srid));
        Point p = geometryFactory.createPoint(new Coordinate(coordonnees[0], coordonnees[1]));
        hydrantRepository.updateGeometrie(idHydrant, p);
      }

      hydrantVisiteRepository.launchTriggerAnomalies(idHydrant);

      // launch trigger pibi pour calcul d'indispo
      hydrantRepository.updatePibiForTrigger(newVisite.getHydrant());
    }
  }
}
