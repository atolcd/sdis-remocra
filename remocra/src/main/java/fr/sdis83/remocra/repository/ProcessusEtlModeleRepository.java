package fr.sdis83.remocra.repository;

import static fr.sdis83.remocra.db.model.remocra.Tables.PROCESSUS_ETL_MODELE;
import static fr.sdis83.remocra.db.model.remocra.Tables.PROCESSUS_ETL_MODELE_DROIT;
import static fr.sdis83.remocra.db.model.remocra.Tables.PROCESSUS_ETL_MODELE_PARAMETRE;
import static fr.sdis83.remocra.db.model.remocra.Tables.PROCESSUS_ETL_PARAMETRE;
import static fr.sdis83.remocra.db.model.remocra.tables.ProcessusEtl.PROCESSUS_ETL;
import static fr.sdis83.remocra.db.model.remocra.tables.ProcessusEtlStatut.PROCESSUS_ETL_STATUT;

import flexjson.JSONDeserializer;
import fr.sdis83.remocra.GlobalConstants;
import fr.sdis83.remocra.db.model.remocra.Tables;
import fr.sdis83.remocra.db.model.remocra.tables.pojos.ProcessusEtl;
import fr.sdis83.remocra.db.model.remocra.tables.pojos.ProcessusEtlModele;
import fr.sdis83.remocra.domain.remocra.RemocraVueCombo;
import fr.sdis83.remocra.domain.remocra.Utilisateur;
import fr.sdis83.remocra.exception.BusinessException;
import fr.sdis83.remocra.service.UtilisateurService;
import fr.sdis83.remocra.usecase.parametre.ParametreDataProvider;
import fr.sdis83.remocra.util.StatementFormat;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.apache.log4j.Logger;
import org.joda.time.Instant;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

@Configuration
public class ProcessusEtlModeleRepository {
  private final Logger logger = Logger.getLogger(getClass());

  @Autowired DSLContext context;

  @Autowired private UtilisateurService utilisateurService;

  @Autowired private ParametreDataProvider parametreProvider;

  public ProcessusEtlModeleRepository() {}

  @PersistenceContext private EntityManager entityManager;

  @Bean
  public ProcessusEtlModeleRepository processusEtlModeleRepository(DSLContext context) {
    return new ProcessusEtlModeleRepository(context);
  }

  ProcessusEtlModeleRepository(DSLContext context) {
    this.context = context;
  }

  public List<ProcessusEtlModele> getAll(String categorie) {
    try {
      return context
          .select()
          .from(PROCESSUS_ETL_MODELE)
          .leftOuterJoin(PROCESSUS_ETL_MODELE_DROIT)
          .on(PROCESSUS_ETL_MODELE.ID.eq(PROCESSUS_ETL_MODELE_DROIT.MODELE))
          .where(
              PROCESSUS_ETL_MODELE_DROIT
                  .PROFIL_DROIT
                  .eq(utilisateurService.getCurrentProfilDroit().getId())
                  .and(
                      categorie == null
                          ? DSL.trueCondition()
                          : PROCESSUS_ETL_MODELE.CATEGORIE.eq(categorie)))
          .fetchInto(ProcessusEtlModele.class);
    } catch (BusinessException e) {
      e.printStackTrace();
    }
    return null;
  }

  public int count() {
    return context.fetchCount(context.select().from(PROCESSUS_ETL_MODELE));
  }

  public List<RemocraVueCombo> getComboValues(Long id, String pathParam, Integer limit)
      throws SQLException, ParseException {
    List<RemocraVueCombo> lstResult = new ArrayList<RemocraVueCombo>();
    @SuppressWarnings("unchecked")
    String query =
        context
            .select(PROCESSUS_ETL_MODELE_PARAMETRE.SOURCE_SQL)
            .from(PROCESSUS_ETL_MODELE_PARAMETRE)
            .where(PROCESSUS_ETL_MODELE_PARAMETRE.ID.eq(id))
            .fetchOne(PROCESSUS_ETL_MODELE_PARAMETRE.SOURCE_SQL);
    String libelle =
        context
            .select(PROCESSUS_ETL_MODELE_PARAMETRE.SOURCE_SQL_LIBELLE)
            .from(PROCESSUS_ETL_MODELE_PARAMETRE)
            .where(PROCESSUS_ETL_MODELE_PARAMETRE.ID.eq(id))
            .fetchOne(PROCESSUS_ETL_MODELE_PARAMETRE.SOURCE_SQL_LIBELLE);
    Pattern p = Pattern.compile("\\$\\{(.+?)\\}");
    Matcher matcher = p.matcher(query);
    List<String> requestParams = new ArrayList<String>();
    // on fait une liste des parametres de la requete '${}'
    while (matcher.find()) {
      String match = matcher.group(1);
      requestParams.add(match);
    }
    // On remplace tous les '${}' par '?'
    query = matcher.replaceAll("?");
    for (int i = 0; i < query.length(); i++) {
      query = query.replace("'?'", "?");
    }
    List<HashMap> typeParametre = new ArrayList<HashMap>();
    // On rajoute systématiquement les parametres Utilisateurs
    Utilisateur u = utilisateurService.getCurrentUtilisateur();
    HashMap<String, String> zcTmp = new HashMap<String, String>();
    zcTmp.put("nomparametre", GlobalConstants.ZONE_COMPETENCE_ID);
    zcTmp.put("type", "integer");
    zcTmp.put("valeur", String.valueOf(u.getOrganisme().getZoneCompetence().getId()));
    typeParametre.add(zcTmp);
    HashMap<String, String> orTmp = new HashMap<String, String>();
    orTmp.put("nomparametre", "ORGANISME_ID");
    orTmp.put("type", "integer");
    orTmp.put("valeur", String.valueOf(u.getOrganisme().getId()));
    typeParametre.add(orTmp);
    HashMap<String, String> uTmp = new HashMap<String, String>();
    uTmp.put("nomparametre", "UTILISATEUR_ID");
    uTmp.put("type", "integer");
    uTmp.put("valeur", String.valueOf(u.getId()));
    typeParametre.add(uTmp);
    Connection connection = context.configuration().connectionProvider().acquire();
    // on applique les filtres si y'en a
    if (pathParam != null && pathParam != "") {
      query =
          "SELECT * FROM ("
              + query
              + ") AS foo WHERE lower("
              + libelle
              + ") LIKE lower( "
              + "'%"
              + pathParam
              + "%') limit "
              + limit;
    }
    // On prépare la requete (sourceSql dans requete modele selection en settant les parametres ${})
    PreparedStatement preparedStatement = connection.prepareStatement(query);
    for (int i = 0; i < requestParams.size(); i++) {
      for (int j = 0; j < typeParametre.size(); j++) {
        // si le parametre de la requete correspond au parametre de json on le remplace par la
        // valeur (les index preparedStatement commence par 1)
        if (requestParams.get(i).equals(typeParametre.get(j).get("nomparametre"))) {
          StatementFormat.PreparedStatement(preparedStatement, i + 1, typeParametre.get(j));
        }
      }
    }
    ResultSet resultSet = preparedStatement.executeQuery();
    // On crée une liste avec les records à mettre dans la combo
    lstResult = this.resultSetToArrayList(resultSet);
    connection.close();
    return lstResult;
  }

  public List resultSetToArrayList(ResultSet rs) throws SQLException {
    ResultSetMetaData md = rs.getMetaData();
    int columns = md.getColumnCount();
    ArrayList list = new ArrayList();
    while (rs.next()) {
      HashMap row = new HashMap(columns);
      for (int i = 1; i <= columns; ++i) {
        row.put(md.getColumnName(i), rs.getObject(i));
      }
      list.add(row);
    }
    return list;
  }

  public ProcessusEtl createProcess(MultipartHttpServletRequest request) throws IOException {

    ProcessusEtl p = new ProcessusEtl();
    // Ajout du processusEtl
    Long idModele = Long.valueOf(String.valueOf(request.getParameter("processus")));
    Instant t = new Instant();
    int priorite = Integer.valueOf(String.valueOf(request.getParameter("priorite")));
    Utilisateur u = utilisateurService.getCurrentUtilisateur();
    context
        .insertInto(
            PROCESSUS_ETL,
            PROCESSUS_ETL.MODELE,
            PROCESSUS_ETL.STATUT,
            PROCESSUS_ETL.UTILISATEUR,
            PROCESSUS_ETL.PRIORITE,
            PROCESSUS_ETL.DEMANDE)
        .values(
            idModele,
            context
                .select(PROCESSUS_ETL_STATUT.ID)
                .from(PROCESSUS_ETL_STATUT)
                .where(PROCESSUS_ETL_STATUT.CODE.eq("A"))
                .fetchOne()
                .value1(),
            Long.valueOf(u.getId()),
            priorite,
            t)
        .execute();
    Long idProcess =
        context
            .select(DSL.max((Tables.PROCESSUS_ETL.ID)))
            .from(Tables.PROCESSUS_ETL)
            .fetchOne()
            .value1();
    if (request.getFileNames() != null) {
      this.addProcessFiles(request, idProcess);
    }

    // Ajout des paramètres
    Map mapParams = request.getParameterMap();
    for (Object key : mapParams.keySet()) {
      String keyStr = (String) key;
      String[] values = (String[]) (mapParams.get(keyStr));
      for (String val : values) {
        if (keyStr.substring(0, 5).equals("input")) {
          context
              .insertInto(
                  PROCESSUS_ETL_PARAMETRE,
                  PROCESSUS_ETL_PARAMETRE.PROCESSUS,
                  PROCESSUS_ETL_PARAMETRE.PARAMETRE,
                  PROCESSUS_ETL_PARAMETRE.VALEUR)
              .values(idProcess, Long.valueOf(keyStr.substring(5)), val)
              .execute();
        }
      }
    }
    return p;
  }

  // creation de processus en Extjs (le post des files est différent du vuejs)
  public ProcessusEtl createProcessWithoutFile(String json) {
    Map mapParams = new JSONDeserializer<Map>().deserialize(json);
    ProcessusEtl p = new ProcessusEtl();
    // Ajout du processusEtl
    Long idModele = Long.valueOf(String.valueOf(mapParams.get("processus")));
    Instant t = new Instant();
    int priorite = Integer.valueOf(String.valueOf(mapParams.get("priorite")));
    Utilisateur u = utilisateurService.getCurrentUtilisateur();
    context
        .insertInto(
            PROCESSUS_ETL,
            PROCESSUS_ETL.MODELE,
            PROCESSUS_ETL.STATUT,
            PROCESSUS_ETL.UTILISATEUR,
            PROCESSUS_ETL.PRIORITE,
            PROCESSUS_ETL.DEMANDE)
        .values(
            idModele,
            context
                .select(PROCESSUS_ETL_STATUT.ID)
                .from(PROCESSUS_ETL_STATUT)
                .where(PROCESSUS_ETL_STATUT.CODE.eq("A"))
                .fetchOne()
                .value1(),
            Long.valueOf(u.getId()),
            priorite,
            t)
        .execute();
    Long idProcess =
        context
            .select(DSL.max((Tables.PROCESSUS_ETL.ID)))
            .from(Tables.PROCESSUS_ETL)
            .fetchOne()
            .value1();
    // Ajout des paramètres
    for (Object key : mapParams.keySet()) {
      String keyStr = (String) key;
      if (keyStr.substring(0, 5).equals("input")) {
        String val = (String) (mapParams.get(keyStr));
        context
            .insertInto(
                PROCESSUS_ETL_PARAMETRE,
                PROCESSUS_ETL_PARAMETRE.PROCESSUS,
                PROCESSUS_ETL_PARAMETRE.PARAMETRE,
                PROCESSUS_ETL_PARAMETRE.VALEUR)
            .values(idProcess, Long.valueOf(keyStr.substring(5)), val)
            .execute();
      }
    }
    return p;
  }

  public void addProcessFiles(MultipartHttpServletRequest request, Long idProcess)
      throws IOException {
    String codeProcess =
        context
            .select(PROCESSUS_ETL.CODE)
            .from(PROCESSUS_ETL)
            .where(PROCESSUS_ETL.ID.eq(idProcess))
            .fetchOne()
            .value1();
    // Ajout des fichiers
    // Répertoire "depots PDI"
    String basePath = parametreProvider.get().getDossierDepotPdi();
    // Répertoire "depots PDI" du traitement
    String uploadDirPath = basePath + File.separator + codeProcess;

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

  /**
   * Permet d'insérer une demande de traitement : table processus_etl + processus_etl_parametre
   *
   * @param codeProcess : code du traitement (processur_etl_modele)
   * @param json : json contenant les paramètres du traitement (processur_etl_modele_paraùetre)
   *     ainsi que la priorité
   * @return
   */
  public ProcessusEtl createProcess(String codeProcess, String json) {
    Long idModele =
        context
            .select(PROCESSUS_ETL_MODELE.ID)
            .from(PROCESSUS_ETL_MODELE)
            .where(PROCESSUS_ETL_MODELE.CODE.eq(codeProcess))
            .fetchOne()
            .value1();
    Map mapParams = new JSONDeserializer<Map>().deserialize(json);
    ProcessusEtl p = new ProcessusEtl();
    // Ajout du processusEtl
    Instant t = new Instant();
    Integer prior = Integer.valueOf(String.valueOf(mapParams.get("priorite")));
    Utilisateur u = utilisateurService.getCurrentUtilisateur();
    context
        .insertInto(
            PROCESSUS_ETL,
            PROCESSUS_ETL.MODELE,
            PROCESSUS_ETL.STATUT,
            PROCESSUS_ETL.UTILISATEUR,
            PROCESSUS_ETL.PRIORITE,
            PROCESSUS_ETL.DEMANDE)
        .values(
            idModele,
            context
                .select(PROCESSUS_ETL_STATUT.ID)
                .from(PROCESSUS_ETL_STATUT)
                .where(PROCESSUS_ETL_STATUT.CODE.eq("A"))
                .fetchOne()
                .value1(),
            Long.valueOf(u.getId()),
            prior,
            t)
        .execute();
    Long idProcess =
        context
            .select(DSL.max((Tables.PROCESSUS_ETL.ID)))
            .from(Tables.PROCESSUS_ETL)
            .fetchOne()
            .value1();
    // Ajout des paramètres
    for (Object key : mapParams.keySet()) {
      String keyStr = (String) key;
      // la priorite n'est pas un paramètre
      if (!"priorite".equalsIgnoreCase(keyStr)) {
        Long idParam =
            context
                .select(PROCESSUS_ETL_MODELE_PARAMETRE.ID)
                .from(PROCESSUS_ETL_MODELE_PARAMETRE)
                .where(PROCESSUS_ETL_MODELE_PARAMETRE.NOM.eq(keyStr))
                .fetchOne()
                .value1();
        String val = String.valueOf(mapParams.get(keyStr));
        context
            .insertInto(
                PROCESSUS_ETL_PARAMETRE,
                PROCESSUS_ETL_PARAMETRE.PROCESSUS,
                PROCESSUS_ETL_PARAMETRE.PARAMETRE,
                PROCESSUS_ETL_PARAMETRE.VALEUR)
            .values(idProcess, idParam, val)
            .execute();
      }
    }
    return p;
  }
}
