package fr.sdis83.remocra.repository;

import static fr.sdis83.remocra.db.model.remocra.Tables.REQUETE_MODELE_DROIT;
import static fr.sdis83.remocra.db.model.remocra.Tables.REQUETE_MODELE_SELECTION;
import static fr.sdis83.remocra.db.model.remocra.tables.RequeteModele.REQUETE_MODELE;
import static fr.sdis83.remocra.db.model.remocra.tables.RequeteModeleParametre.REQUETE_MODELE_PARAMETRE;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.sdis83.remocra.db.model.remocra.tables.pojos.RequeteModele;
import fr.sdis83.remocra.db.model.remocra.tables.pojos.RequeteModeleParametre;
import fr.sdis83.remocra.db.model.remocra.tables.pojos.RequeteModeleSelection;
import fr.sdis83.remocra.domain.remocra.RemocraVueCombo;
import fr.sdis83.remocra.domain.remocra.Utilisateur;
import fr.sdis83.remocra.domain.remocra.ZoneCompetence;
import fr.sdis83.remocra.exception.BusinessException;
import fr.sdis83.remocra.service.UtilisateurService;
import fr.sdis83.remocra.web.message.ItemFilter;
import org.apache.log4j.Logger;
import org.joda.time.Instant;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.Transactional;

@Configuration
public class RequeteModeleRepository {
  private final Logger logger = Logger.getLogger(getClass());


  @Autowired
  DSLContext context;

  @Autowired
  private UtilisateurService utilisateurService;

  @Autowired
  private RequeteModeleParametereRepository requeteModeleParametereRepository;

  public RequeteModeleRepository() {

  }

  @PersistenceContext
  private EntityManager entityManager;

  @Bean
  public RequeteModeleRepository requeteModeleRepository(DSLContext context) {
    return new RequeteModeleRepository(context);
  }

  RequeteModeleRepository(DSLContext context) {
    this.context = context;
  }

  public List<RequeteModele> getAll(List<ItemFilter> itemFilters) {
    String categorie = null;
    //On filtre par rapport aux catégories (POINTDEAU,DFCI,...)
    if (itemFilters != null && !itemFilters.isEmpty()) {
      for (ItemFilter itemFilter : itemFilters) {
        if ("categorie".equals(itemFilter.getFieldName())) {
           categorie = itemFilter.getValue();
        }
      }
    }

    //On récupere les requêtes en fonction des profils
    List<RequeteModele> l = null;
    try {
      l = context.select().from(REQUETE_MODELE)
          .leftOuterJoin(REQUETE_MODELE_DROIT)
          .on(REQUETE_MODELE.ID.eq(REQUETE_MODELE_DROIT.REQUETE_MODELE))
          .where(REQUETE_MODELE_DROIT.PROFIL_DROIT.eq(utilisateurService.getCurrentProfilDroit().getId()).and(REQUETE_MODELE.CATEGORIE.eq(categorie))).fetchInto(RequeteModele.class);
    } catch (BusinessException e) {
      e.printStackTrace();
    }
    return l;
  }

  public int count() {
    return context.fetchCount(context.select().from(REQUETE_MODELE));
  }

  public List<RemocraVueCombo> getComboValues(Long id) throws SQLException, ParseException {
    List<RemocraVueCombo> lstResult = new ArrayList<RemocraVueCombo>();
    @SuppressWarnings("unchecked")
    String query = context.select(REQUETE_MODELE_PARAMETRE.SOURCE_SQL).from(REQUETE_MODELE_PARAMETRE).where(REQUETE_MODELE_PARAMETRE.ID.eq(id)).fetchOne(REQUETE_MODELE.SOURCE_SQL);
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

  public List<HashMap> executeRequest(Long idmodele, String json) throws SQLException, IOException, ParseException {
   List<HashMap> resultat = new ArrayList<HashMap>();
    RequeteModele r =  context.select().from(REQUETE_MODELE).where(REQUETE_MODELE.ID.eq(idmodele)).fetchOne().into(RequeteModele.class);
    String sourceSql = r.getSourceSql();
    List<RequeteModeleParametre> requeteModeleParametres = requeteModeleParametereRepository.getByRequeteModele(idmodele);
    List<HashMap> typeParametre = new ArrayList<HashMap>();
    //On parcourt le json

    List<HashMap> result = new ObjectMapper().readValue(json, List.class);
    //On crée une liste des parametres demandés par la séléction
    for(RequeteModeleParametre requeteModeleParametre :  requeteModeleParametres) {
      for (int i  = 0 ; i<result.size();i++) {
         if(requeteModeleParametre.getNom().equals(result.get(i).get("nomparametre"))) {
           HashMap<String,Object >typeParametreTmp = new HashMap<String,Object>();
           typeParametreTmp.put("nomparametre", requeteModeleParametre.getNom());
           typeParametreTmp.put("type",requeteModeleParametre.getTypeValeur());
           typeParametreTmp.put("valeur",result.get(i).get("valeur").toString());
           typeParametre.add(typeParametreTmp);
         }
      }
    }

    //On rajoute systématiquement la zone de compétence
    ZoneCompetence zc = utilisateurService.getCurrentUtilisateur().getOrganisme().getZoneCompetence();
    HashMap<String,String> zcTmp = new HashMap<String, String>();
    zcTmp.put("nomparametre", "ZONE_COMPETENCE_ID");
    zcTmp.put("type","integer");
    zcTmp.put("valeur", String.valueOf(zc.getId()));
    typeParametre.add(zcTmp);

    try {
      //
      Pattern p = Pattern.compile("\\$\\{(.+?)\\}");
      Matcher matcher = p.matcher(sourceSql);
      List<String> requestParams = new ArrayList<String>();
      Connection connection = context.configuration().connectionProvider().acquire();

      //on fait une liste des parametres de la requetes '${}'
      while (matcher.find()) {
        String match = matcher.group(1);
        requestParams.add(match);
      }

      //On remplace tous les '${}' par '?'
      sourceSql = matcher.replaceAll("?");
      for (int i = 0; i < sourceSql.length(); i++) {
        sourceSql= sourceSql.replace("'?'", "?");
      }

      //On parcourt la liste des paramètres demandés par la requête et on boucle sur la liste des paramètres venus du json
      PreparedStatement preparedStatement = connection.prepareStatement(sourceSql);
      for (int i = 0; i < requestParams.size(); i++) {
        for (int j = 0; j < typeParametre.size(); j++) {
          //si le parametre de la requete correspond au parametre de json on le remplace par la valeur (les index preparedStatement commence par 1)
          if (requestParams.get(i).equals(typeParametre.get(j).get("nomparametre"))) {
            this.setObject(preparedStatement,i+1, typeParametre.get(j));
          }
        }
      }
      //Insertion de la requete dans requete_modele_selection
      RequeteModeleSelection rms = new RequeteModeleSelection();
      Instant instant = new Instant();
      rms.setDate(instant);
      rms.setUtilisateur(utilisateurService.getCurrentUtilisateur().getId());
      rms.setRequete(preparedStatement.toString());
      rms.setModele(r.getId());
      int id = this.insert(rms);
      //Le renvoi des clés automatiquement générées n'est pas supporté pour cette version de jooq (3.6) : On fait une requete pour selectionner le dernier id ajouté
      Long lastRmd =  context.select(REQUETE_MODELE_SELECTION.ID).from(REQUETE_MODELE_SELECTION).where(REQUETE_MODELE_SELECTION.MODELE.eq(rms.getModele())
              .and(REQUETE_MODELE_SELECTION.UTILISATEUR.eq(rms.getUtilisateur()))).fetchOne().value1();
      ResultSet resultSet;
      if(id != 0){
        resultSet = connection.createStatement().executeQuery(rms.getRequete());
      }
      else {
        return null;
      }


      for (int i = 1;i<= resultSet.getMetaData().getColumnCount();i++) {
        if(!resultSet.getMetaData().getColumnName(i).equalsIgnoreCase("wkt")){
          HashMap<String, Object> colonne= new HashMap<String, Object>();
          colonne.put("header", resultSet.getMetaData().getColumnName(i));
          colonne.put("type", resultSet.getMetaData().getColumnTypeName(i));
          colonne.put("requete", lastRmd);
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

  public int insert(RequeteModeleSelection requeteModeleSelection) {
    //On supprime la requetemodeleselection ayant le meme modele et le meme utilisateur
    context.deleteFrom(REQUETE_MODELE_SELECTION)
        .where(REQUETE_MODELE_SELECTION.MODELE.eq(requeteModeleSelection.getModele())
            .and(REQUETE_MODELE_SELECTION.UTILISATEUR.eq(requeteModeleSelection.getUtilisateur())))
            .execute();
    //On insère une nouvelle
    return context.insertInto(REQUETE_MODELE_SELECTION,
        REQUETE_MODELE_SELECTION.UTILISATEUR, REQUETE_MODELE_SELECTION.DATE, REQUETE_MODELE_SELECTION.REQUETE, REQUETE_MODELE_SELECTION.MODELE)
        .values(requeteModeleSelection.getUtilisateur(), requeteModeleSelection.getDate(),requeteModeleSelection.getRequete(),requeteModeleSelection.getModele())
        .execute();
  }

  @Transactional
  public int insertDetail(String rs, Long lastRmd) throws SQLException {
    String geom =  rs.split(";")[1];
    Query query = entityManager.createNativeQuery("INSERT INTO remocra.requete_modele_selection_detail (selection,geometrie) SELECT :selection, (select st_geometryfromtext(:geometrie, 2154))")
    .setParameter("selection", lastRmd).setParameter("geometrie",geom);
    return query.executeUpdate();
  }

  @Transactional
  public List<Object> getResults (Long id, Integer start, Integer limit) throws SQLException {
    //L'id est l'identifiant de la requete modele selection
    Connection connection = context.configuration().connectionProvider().acquire();
    String s = context.select(REQUETE_MODELE_SELECTION.REQUETE).from(REQUETE_MODELE_SELECTION).where(REQUETE_MODELE_SELECTION.ID.eq(id)).fetchOne(REQUETE_MODELE_SELECTION.REQUETE);
    Boolean spatial = context.select(REQUETE_MODELE.SPATIAL).from(REQUETE_MODELE).where(REQUETE_MODELE.ID.eq(
        context.select(REQUETE_MODELE_SELECTION.MODELE).from(REQUETE_MODELE_SELECTION).where(REQUETE_MODELE_SELECTION.ID.eq(id)))).fetchOne(REQUETE_MODELE.SPATIAL);
    Statement p = connection.createStatement();
    ResultSet rs = p.executeQuery("select * from (" + s.toString() + ") as params limit "+limit+" offset "+start);
    List <Object> l = this.resultSetToArrayList(rs , id);
    connection.close();
    //Si la requête est spatiale on insère les détails
    if(spatial) {
      //On insere les détails de la selection
      Query q = entityManager.createNativeQuery("INSERT INTO remocra.requete_modele_selection_detail (selection, geometrie ) SELECT :id, ST_GeomFromText(wkt, 2154) FROM ("+s.toString()+") AS selection")
          .setParameter("id", id);
      int r = q.executeUpdate();

      //On update le champ étendu
      if(r != 0){
       Query query = entityManager.createNativeQuery("UPDATE remocra.requete_modele_selection SET etendu = (SELECT st_setsrid(CAST(st_extent(geometrie) AS Geometry),2154) " +
                  "FROM remocra.requete_modele_selection_detail WHERE selection = :id) WHERE id = :id")
                  .setParameter("id",id);
          query.executeUpdate();
      }

    }
    return l;
  }

  @Transactional
  public Long countRecords(Long id) throws SQLException {
    String s = context.select(REQUETE_MODELE_SELECTION.REQUETE).from(REQUETE_MODELE_SELECTION).where(REQUETE_MODELE_SELECTION.ID.eq(id)).fetchOne(REQUETE_MODELE_SELECTION.REQUETE);
    Query q = entityManager.createNativeQuery("(SELECT COUNT(*) AS nb FROM ("+ s.toString()+ ") AS countRecords)");
    return Long.valueOf(q.getSingleResult().toString());
  }


  public List resultSetToArrayList(ResultSet rs, Long id) throws SQLException{
    ResultSetMetaData md = rs.getMetaData();
    int columns = md.getColumnCount();
    ArrayList list = new ArrayList();
    while (rs.next()){
      HashMap row = new HashMap(columns);
      for(int i=1; i<=columns; ++i){
        if(!md.getColumnName(i).equalsIgnoreCase("wkt")){
          row.put(md.getColumnName(i),rs.getObject(i));
        }
      }
      list.add(row);
    }
    return list;
  }

  @Transactional
  public String getEtendu(Long id) {
    Object geom = context.select(REQUETE_MODELE_SELECTION.ETENDU).from(REQUETE_MODELE_SELECTION).where(REQUETE_MODELE_SELECTION.ID.eq(id)).fetchOne(REQUETE_MODELE_SELECTION.ETENDU);
    if(geom != null){
      String geometrie =  geom.toString();
      return geometrie;
    }
    return null;
  }

  @Transactional
  public Boolean doDelete(Long id) {
    int deleted = context.delete(REQUETE_MODELE_SELECTION).where(REQUETE_MODELE_SELECTION.ID.eq(id)).execute();
    return deleted == 1;
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

  @Transactional
  public String getSourceSqlFromSelection(Long id) {
    String s = context.select(REQUETE_MODELE_SELECTION.REQUETE).from(REQUETE_MODELE_SELECTION).where(REQUETE_MODELE_SELECTION.ID.eq(id)).fetchOne(REQUETE_MODELE_SELECTION.REQUETE);
    return s;
  }

}
