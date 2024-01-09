package fr.sdis83.remocra.repository;

import static fr.sdis83.remocra.db.model.remocra.Tables.REQUETE_MODELE_DROIT;
import static fr.sdis83.remocra.db.model.remocra.Tables.REQUETE_MODELE_SELECTION;
import static fr.sdis83.remocra.db.model.remocra.tables.RequeteModele.REQUETE_MODELE;
import static fr.sdis83.remocra.db.model.remocra.tables.RequeteModeleParametre.REQUETE_MODELE_PARAMETRE;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.sdis83.remocra.GlobalConstants;
import fr.sdis83.remocra.db.model.remocra.tables.pojos.RequeteModele;
import fr.sdis83.remocra.db.model.remocra.tables.pojos.RequeteModeleParametre;
import fr.sdis83.remocra.db.model.remocra.tables.pojos.RequeteModeleSelection;
import fr.sdis83.remocra.domain.remocra.RemocraVueCombo;
import fr.sdis83.remocra.domain.remocra.Utilisateur;
import fr.sdis83.remocra.exception.BusinessException;
import fr.sdis83.remocra.service.UtilisateurService;
import fr.sdis83.remocra.usecase.parametre.ParametreDataProvider;
import fr.sdis83.remocra.util.StatementFormat;
import fr.sdis83.remocra.web.message.ItemFilter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import org.apache.log4j.Logger;
import org.joda.time.Instant;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.Transactional;

@Configuration
public class RequeteModeleRepository {
  private final Logger logger = Logger.getLogger(getClass());

  @Autowired DSLContext context;

  @Autowired private UtilisateurService utilisateurService;

  @Autowired protected ParametreDataProvider parametreProvider;

  @Autowired private RequeteModeleParametereRepository requeteModeleParametereRepository;

  public RequeteModeleRepository() {}

  @PersistenceContext private EntityManager entityManager;

  @Bean
  public RequeteModeleRepository requeteModeleRepository(DSLContext context) {
    return new RequeteModeleRepository(context);
  }

  RequeteModeleRepository(DSLContext context) {
    this.context = context;
  }

  public List<RequeteModele> getAll(List<ItemFilter> itemFilters) {
    List<Condition> conditions = new ArrayList<>();
    // On filtre par rapport aux catégories (POINTDEAU,DFCI,...)
    if (itemFilters != null && !itemFilters.isEmpty()) {
      for (ItemFilter itemFilter : itemFilters) {
        if ("categorie".equals(itemFilter.getFieldName())) {
          conditions.add(REQUETE_MODELE.CATEGORIE.eq(itemFilter.getValue()));
        }
        if ("query".equals(itemFilter.getFieldName())) {
          conditions.add(REQUETE_MODELE.LIBELLE.likeIgnoreCase("%" + itemFilter.getValue() + "%"));
        }
      }
    }

    // On récupere les requêtes en fonction des profils
    List<RequeteModele> l = null;
    try {
      conditions.add(
          REQUETE_MODELE_DROIT.PROFIL_DROIT.eq(utilisateurService.getCurrentProfilDroit().getId()));
      l =
          context
              .select()
              .from(REQUETE_MODELE)
              .leftOuterJoin(REQUETE_MODELE_DROIT)
              .on(REQUETE_MODELE.ID.eq(REQUETE_MODELE_DROIT.REQUETE_MODELE))
              .where(conditions)
              .orderBy(REQUETE_MODELE.LIBELLE)
              .fetchInto(RequeteModele.class);
    } catch (BusinessException e) {
      e.printStackTrace();
    }
    return l;
  }

  public int count() {
    return context.fetchCount(context.select().from(REQUETE_MODELE));
  }

  public List<RemocraVueCombo> getComboValues(Long id, String pathParam)
      throws SQLException, ParseException {
    List<RemocraVueCombo> lstResult = new ArrayList<RemocraVueCombo>();
    @SuppressWarnings("unchecked")
    String query =
        context
            .select(REQUETE_MODELE_PARAMETRE.SOURCE_SQL)
            .from(REQUETE_MODELE_PARAMETRE)
            .where(REQUETE_MODELE_PARAMETRE.ID.eq(id))
            .fetchOne(REQUETE_MODELE.SOURCE_SQL);
    String libelle =
        context
            .select(REQUETE_MODELE_PARAMETRE.SOURCE_SQL_LIBELLE)
            .from(REQUETE_MODELE_PARAMETRE)
            .where(REQUETE_MODELE_PARAMETRE.ID.eq(id))
            .fetchOne(REQUETE_MODELE_PARAMETRE.SOURCE_SQL_LIBELLE);
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
    zcTmp.put("valeur", String.valueOf(utilisateurService.getCurrentZoneCompetenceId()));
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
              + "%')";
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

  public Long insert(RequeteModeleSelection requeteModeleSelection) {
    // On supprime la requetemodeleselection ayant le même modèle et le même utilisateur
    context
        .deleteFrom(REQUETE_MODELE_SELECTION)
        .where(
            REQUETE_MODELE_SELECTION
                .MODELE
                .eq(requeteModeleSelection.getModele())
                .and(
                    REQUETE_MODELE_SELECTION.UTILISATEUR.eq(
                        requeteModeleSelection.getUtilisateur())))
        .execute();
    // On insère une nouvelle
    return context
        .insertInto(
            REQUETE_MODELE_SELECTION,
            REQUETE_MODELE_SELECTION.UTILISATEUR,
            REQUETE_MODELE_SELECTION.DATE,
            REQUETE_MODELE_SELECTION.REQUETE,
            REQUETE_MODELE_SELECTION.MODELE)
        .values(
            requeteModeleSelection.getUtilisateur(),
            requeteModeleSelection.getDate(),
            requeteModeleSelection.getRequete(),
            requeteModeleSelection.getModele())
        .returning(REQUETE_MODELE_SELECTION.ID)
        .fetchOne()
        .getValue(REQUETE_MODELE_SELECTION.ID);
  }

  public List<Map> executeRequest(Long idmodele, String json)
      throws SQLException, IOException, ParseException {
    List<Map> resultat = new ArrayList<>();
    RequeteModele requeteModele = getById(idmodele);

    String sourceSql = requeteModele.getSourceSql();
    List<RequeteModeleParametre> requeteModeleParametres =
        requeteModeleParametereRepository.getByRequeteModele(idmodele);

    List<Map> typeParametre = new ArrayList<>();
    ;
    // On parcourt le json

    List<Map> result = new ObjectMapper().readValue(json, List.class);

    // On crée une liste des parametres demandés par la séléction
    for (RequeteModeleParametre requeteModeleParametre : requeteModeleParametres) {
      for (int i = 0; i < result.size(); i++) {
        if (requeteModeleParametre.getNom().equals(result.get(i).get("nomparametre"))) {
          Map<String, Object> typeParametreTmp = result.get(i);
          typeParametreTmp.put("type", requeteModeleParametre.getTypeValeur());
          typeParametre.add(typeParametreTmp);
        }
      }
    }

    // On rajoute systématiquement la zone de compétence
    Map<String, String> zcTmp = new HashMap<>();
    zcTmp.put("nomparametre", GlobalConstants.ZONE_COMPETENCE_ID);
    zcTmp.put("type", "integer");
    zcTmp.put("valeur", String.valueOf(utilisateurService.getCurrentZoneCompetenceId()));
    typeParametre.add(zcTmp);

    try {
      //
      Pattern p = Pattern.compile("\\$\\{(.+?)\\}");
      Matcher matcher = p.matcher(sourceSql);
      List<String> requestParams = new ArrayList<>();
      Connection connection = context.configuration().connectionProvider().acquire();

      // on fait une liste des parametres de la requetes '${}'
      while (matcher.find()) {
        requestParams.add(matcher.group(1));
      }

      // On remplace tous les '${}' par '?'
      sourceSql = matcher.replaceAll("?");
      sourceSql = sourceSql.replaceAll("'\\?'", "?");

      // On parcourt la liste des paramètres demandés par la requête et on boucle sur la liste des
      // paramètres venus du json
      PreparedStatement preparedStatement = connection.prepareStatement(sourceSql);
      for (int i = 0; i < requestParams.size(); i++) {
        for (int j = 0; j < typeParametre.size(); j++) {
          // si le parametre de la requete correspond au parametre de json on le remplace par la
          // valeur (les index preparedStatement commence par 1)
          if (requestParams.get(i).equals(typeParametre.get(j).get("nomparametre"))) {
            StatementFormat.PreparedStatement(preparedStatement, i + 1, typeParametre.get(j));
          }
        }
      }
      // Insertion de la requete dans requete_modele_selection
      RequeteModeleSelection requeteModeleSelection = new RequeteModeleSelection();
      Instant instant = new Instant();
      requeteModeleSelection.setDate(instant);
      requeteModeleSelection.setUtilisateur(utilisateurService.getCurrentUtilisateur().getId());
      requeteModeleSelection.setRequete(preparedStatement.toString());
      requeteModeleSelection.setModele(requeteModele.getId());
      Long idRequeteModeleSelection = this.insert(requeteModeleSelection);

      ResultSet resultSet;
      if (idRequeteModeleSelection != null) {
        resultSet = connection.createStatement().executeQuery(requeteModeleSelection.getRequete());
      } else {
        return null;
      }

      for (int i = 1; i <= resultSet.getMetaData().getColumnCount(); i++) {
        if (!resultSet.getMetaData().getColumnName(i).equalsIgnoreCase("wkt")) {
          HashMap<String, Object> colonne = new HashMap<String, Object>();
          colonne.put("header", resultSet.getMetaData().getColumnName(i));
          colonne.put("type", resultSet.getMetaData().getColumnTypeName(i));
          colonne.put("requete", idRequeteModeleSelection);
          resultat.add(colonne);
        }
      }
      preparedStatement.close();
      connection.close();
      return resultat;

    } catch (PatternSyntaxException pse) {
      System.err.println("Le pattern n'a pas un format correct.");
    }
    return null;
  }

  @Transactional
  public List<Object> getResults(Long id, Integer start, Integer limit) throws SQLException {
    // L'id est l'identifiant de la requete modele selection
    Connection connection = context.configuration().connectionProvider().acquire();
    String s =
        context
            .select(REQUETE_MODELE_SELECTION.REQUETE)
            .from(REQUETE_MODELE_SELECTION)
            .where(REQUETE_MODELE_SELECTION.ID.eq(id))
            .fetchOne(REQUETE_MODELE_SELECTION.REQUETE);
    Boolean spatial =
        context
            .select(REQUETE_MODELE.SPATIAL)
            .from(REQUETE_MODELE)
            .where(
                REQUETE_MODELE.ID.eq(
                    context
                        .select(REQUETE_MODELE_SELECTION.MODELE)
                        .from(REQUETE_MODELE_SELECTION)
                        .where(REQUETE_MODELE_SELECTION.ID.eq(id))))
            .fetchOne(REQUETE_MODELE.SPATIAL);
    Statement p = connection.createStatement();
    ResultSet rs =
        p.executeQuery(
            "select * from (" + s.toString() + ") as params limit " + limit + " offset " + start);
    List<Object> l = this.resultSetToArrayList(rs, id);
    connection.close();
    // Si la requête est spatiale on insère les détails
    if (spatial) {
      // On insere les détails de la selection
      Query q =
          entityManager
              .createNativeQuery(
                  "INSERT INTO remocra.requete_modele_selection_detail (selection, geometrie ) SELECT :id, ST_GeomFromText(wkt, :srid) FROM ("
                      + s.toString()
                      + ") AS selection")
              .setParameter("id", id)
              .setParameter("srid", parametreProvider.get().getSridInt());
      ;
      int r = q.executeUpdate();

      // On update le champ étendu
      if (r != 0) {
        Query query =
            entityManager
                .createNativeQuery(
                    "UPDATE remocra.requete_modele_selection SET etendu = (SELECT st_setsrid(CAST(st_extent(geometrie) AS Geometry), :srid) "
                        + "FROM remocra.requete_modele_selection_detail WHERE selection = :id) WHERE id = :id")
                .setParameter("id", id)
                .setParameter("srid", parametreProvider.get().getSridInt());
        query.executeUpdate();
      }
    }
    return l;
  }

  @Transactional
  public Long countRecords(Long id) throws SQLException {
    String s =
        context
            .select(REQUETE_MODELE_SELECTION.REQUETE)
            .from(REQUETE_MODELE_SELECTION)
            .where(REQUETE_MODELE_SELECTION.ID.eq(id))
            .fetchOne(REQUETE_MODELE_SELECTION.REQUETE);
    Query q =
        entityManager.createNativeQuery(
            "(SELECT COUNT(*) AS nb FROM (" + s.toString() + ") AS countRecords)");
    return Long.valueOf(q.getSingleResult().toString());
  }

  public List resultSetToArrayList(ResultSet rs, Long id) throws SQLException {
    ResultSetMetaData md = rs.getMetaData();
    int columns = md.getColumnCount();
    ArrayList list = new ArrayList();
    while (rs.next()) {
      HashMap row = new HashMap(columns);
      for (int i = 1; i <= columns; ++i) {
        if (!md.getColumnName(i).equalsIgnoreCase("wkt")) {
          row.put(md.getColumnName(i), rs.getObject(i));
        }
      }
      list.add(row);
    }
    return list;
  }

  @Transactional
  public String getEtendu(Long id) {
    Query q =
        entityManager.createNativeQuery(
            "select st_asEWKT(etendu) from remocra.requete_modele_selection where id= :id ");
    q.setParameter("id", id);
    String geom = q.getSingleResult().toString();
    return geom != null ? geom : null;
  }

  @Transactional
  public Boolean doDelete(Long id) {
    int deleted =
        context
            .delete(REQUETE_MODELE_SELECTION)
            .where(REQUETE_MODELE_SELECTION.ID.eq(id))
            .execute();
    return deleted == 1;
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

  @Transactional
  public String getSourceSqlFromSelection(Long id) {
    String s =
        context
            .select(REQUETE_MODELE_SELECTION.REQUETE)
            .from(REQUETE_MODELE_SELECTION)
            .where(REQUETE_MODELE_SELECTION.ID.eq(id))
            .fetchOne(REQUETE_MODELE_SELECTION.REQUETE);
    return s;
  }

  public RequeteModele getById(Long idRequeteModele) {
    return context
        .selectFrom(REQUETE_MODELE)
        .where(REQUETE_MODELE.ID.eq(idRequeteModele))
        .fetchOneInto(RequeteModele.class);
  }
}
