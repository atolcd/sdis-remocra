package fr.sdis83.remocra.repository;

import static fr.sdis83.remocra.db.model.remocra.Tables.PROCESSUS_ETL_MODELE;
import static fr.sdis83.remocra.db.model.remocra.Tables.PROCESSUS_ETL_MODELE_DROIT;
import static fr.sdis83.remocra.db.model.remocra.Tables.PROCESSUS_ETL_MODELE_PARAMETRE;
import static fr.sdis83.remocra.db.model.remocra.Tables.REQUETE_MODELE_PARAMETRE;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import fr.sdis83.remocra.db.model.remocra.tables.pojos.ProcessusEtlModele;
import fr.sdis83.remocra.domain.remocra.RemocraVueCombo;
import fr.sdis83.remocra.domain.remocra.Utilisateur;
import fr.sdis83.remocra.exception.BusinessException;
import fr.sdis83.remocra.service.UtilisateurService;
import fr.sdis83.remocra.web.message.ItemFilter;
import org.apache.log4j.Logger;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ProcessusEtlModeleRepository {
  private final Logger logger = Logger.getLogger(getClass());


  @Autowired
  DSLContext context;

  @Autowired
  private UtilisateurService utilisateurService;


  public ProcessusEtlModeleRepository() {

  }

  @PersistenceContext
  private EntityManager entityManager;

  @Bean
  public ProcessusEtlModeleRepository processusEtlModeleRepository(DSLContext context) {
    return new ProcessusEtlModeleRepository(context);
  }

  ProcessusEtlModeleRepository(DSLContext context) {
    this.context = context;
  }

  public List<ProcessusEtlModele> getAll(List<ItemFilter> itemFilters) {
    String categorie = null;
    //On filtre par rapport aux catégories (POINTDEAU,DFCI,...)
    if (itemFilters != null && !itemFilters.isEmpty()) {
      for (ItemFilter itemFilter : itemFilters) {
        if ("categorie".equals(itemFilter.getFieldName())) {
           categorie = itemFilter.getValue();
        }
      }
    }

    //On récupere les processusEtls en fonction des profils
    List<ProcessusEtlModele> l = null;
    try {
      l = context.select().from(PROCESSUS_ETL_MODELE)
          .leftOuterJoin(PROCESSUS_ETL_MODELE_DROIT)
          .on(PROCESSUS_ETL_MODELE.ID.eq(PROCESSUS_ETL_MODELE_DROIT.MODELE))
          .where(PROCESSUS_ETL_MODELE_DROIT.PROFIL_DROIT.eq(utilisateurService.getCurrentProfilDroit().getId()).and(PROCESSUS_ETL_MODELE.CATEGORIE.eq(categorie))).fetchInto(ProcessusEtlModele.class);
    } catch (BusinessException e) {
      e.printStackTrace();
    }
    return l;
  }

  public int count() {
    return context.fetchCount(context.select().from(PROCESSUS_ETL_MODELE));
  }

  public List<RemocraVueCombo> getComboValues(Long id, String pathParam) throws SQLException, ParseException {
    List<RemocraVueCombo> lstResult = new ArrayList<RemocraVueCombo>();
    @SuppressWarnings("unchecked")
    String query = context.select(PROCESSUS_ETL_MODELE_PARAMETRE.SOURCE_SQL).from(PROCESSUS_ETL_MODELE_PARAMETRE).where(PROCESSUS_ETL_MODELE_PARAMETRE.ID.eq(id)).fetchOne(PROCESSUS_ETL_MODELE_PARAMETRE.SOURCE_SQL);
    String libelle = context.select(PROCESSUS_ETL_MODELE_PARAMETRE.SOURCE_SQL_LIBELLE).from(PROCESSUS_ETL_MODELE_PARAMETRE).where(PROCESSUS_ETL_MODELE_PARAMETRE.ID.eq(id)).fetchOne(PROCESSUS_ETL_MODELE_PARAMETRE.SOURCE_SQL_LIBELLE);
    Pattern p = Pattern.compile("\\$\\{(.+?)\\}");
    Matcher matcher = p.matcher(query);
    List<String> requestParams = new ArrayList<String>();
    //on fait une liste des parametres de la requete '${}'
    while (matcher.find()) {
      String match = matcher.group(1);
      requestParams.add(match);
    }
    //On remplace tous les '${}' par '?'
    query = matcher.replaceAll("?");
    for (int i = 0; i < query.length(); i++) {
      query= query.replace("'?'", "?");
    }
    List<HashMap> typeParametre = new ArrayList<HashMap>();
    //On rajoute systématiquement les parametres Utilisateurs
    Utilisateur u = utilisateurService.getCurrentUtilisateur();
    HashMap<String,String> zcTmp = new HashMap<String, String>();
    zcTmp.put("nomparametre", "ZONE_COMPETENCE_ID");
    zcTmp.put("type","integer");
    zcTmp.put("valeur", String.valueOf(u.getOrganisme().getZoneCompetence().getId()));
    typeParametre.add(zcTmp);
    HashMap<String,String> orTmp = new HashMap<String, String>();
    orTmp.put("nomparametre", "ORGANISME_ID");
    orTmp.put("type","integer");
    orTmp.put("valeur", String.valueOf(u.getOrganisme().getId()));
    typeParametre.add(orTmp);
    HashMap<String,String> uTmp = new HashMap<String, String>();
    uTmp.put("nomparametre", "UTILISATEUR_ID");
    uTmp.put("type","integer");
    uTmp.put("valeur", String.valueOf(u.getId()));
    typeParametre.add(uTmp);
    Connection connection = context.configuration().connectionProvider().acquire();
    //on applique les filtres si y'en a
    if(pathParam != null && pathParam != "") {
      query = "SELECT * FROM ("+ query +") AS foo WHERE lower(" +libelle +") LIKE lower( "+"'%"+pathParam+"%')" ;
    }
    //On prépare la requete (sourceSql dans requete modele selection en settant les parametres ${})
    PreparedStatement preparedStatement = connection.prepareStatement(query);
    for (int i = 0; i < requestParams.size(); i++) {
      for (int j = 0; j < typeParametre.size(); j++) {
        //si le parametre de la requete correspond au parametre de json on le remplace par la valeur (les index preparedStatement commence par 1)
        if (requestParams.get(i).equals(typeParametre.get(j).get("nomparametre"))) {
          this.setObject(preparedStatement,i+1, typeParametre.get(j));
        }
      }
    }
    ResultSet resultSet = preparedStatement.executeQuery();
    //On crée une liste avec les records à mettre dans la combo
    lstResult = this.resultSetToArrayList(resultSet);
    connection.close();
    return lstResult;
  }

  public List resultSetToArrayList(ResultSet rs) throws SQLException{
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

  public void setObject(PreparedStatement ps, int index ,HashMap parameterObj) throws ParseException, SQLException {

    if (parameterObj.get("type").toString().equalsIgnoreCase("Byte")) {
      ps.setInt(index, (Byte.valueOf(parameterObj.get("valeur").toString())));
    } else if (parameterObj.get("type").toString().equalsIgnoreCase("Character varying")) {

      ps.setString(index,"'"+parameterObj.get("valeur").toString().replaceAll("'", "''")+"'");
    } else if (parameterObj.get("type").toString().equalsIgnoreCase("Double precision")) {
      ps.setDouble(index, (Double.valueOf(parameterObj.get("valeur").toString())));
    } else if (parameterObj.get("type").toString().equalsIgnoreCase("Integer")) {
      ps.setInt(index, (Integer.valueOf(parameterObj.get("valeur").toString())));
    } else if (parameterObj.get("type").toString().equalsIgnoreCase("Long")) {
      ps.setLong(index, (Long.valueOf(parameterObj.get("valeur").toString())));
    } else if (parameterObj.get("type").toString().equalsIgnoreCase("UUid")) {
      ps.setLong(index, (Long.valueOf(parameterObj.get("valeur").toString())));
    } else if (parameterObj.get("type").toString().equalsIgnoreCase("Float")) {
      ps.setFloat(index, ((Float.valueOf(parameterObj.get("valeur").toString()))));
    } else if (parameterObj.get("type").toString().equalsIgnoreCase("Date")) {
      DateFormat format = new SimpleDateFormat("yyyy dd mm");
      ps.setObject(index, "'"+parameterObj.get("valeur").toString()+"'");
    } else if (parameterObj.get("type").toString().equalsIgnoreCase("Time")) {
      ps.setString(index,"'"+parameterObj.get("valeur").toString()+"'");
    } else if (parameterObj.get("type").toString().equalsIgnoreCase("Timestamp")) {
      ps.setString(index,"'"+parameterObj.get("valeur").toString()+"'");
    } else if (parameterObj.get("type").toString().equalsIgnoreCase("Boolean")) {
      //boolean x = parameterObj.get("valeur").toString().equalsIgnoreCase("true") ? true : false;
      ps.setObject(index, parameterObj.get("valeur"), Types.BOOLEAN);
    } else {
      ps.setObject(index,parameterObj.get("valeur"));
    }
  }

}
