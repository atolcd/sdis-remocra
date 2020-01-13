package fr.sdis83.remocra.repository;

import fr.sdis83.remocra.db.model.remocra.tables.pojos.CourrierParametre;
import fr.sdis83.remocra.db.model.remocra.tables.pojos.CourrierModele;
import fr.sdis83.remocra.domain.remocra.RemocraVueCombo;
import fr.sdis83.remocra.domain.remocra.Utilisateur;
import fr.sdis83.remocra.service.ParamConfService;
import fr.sdis83.remocra.service.UtilisateurService;
import fr.sdis83.remocra.web.message.ItemFilter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import org.jooq.DSLContext;
import org.jooq.Meta;
import org.jooq.Record;
import org.jooq.Result;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.NameTokenizers;
import org.modelmapper.jooq.RecordValueReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import java.io.StringReader;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sf.jooreports.templates.DocumentTemplate;
import net.sf.jooreports.templates.DocumentTemplateFactory;
import org.xml.sax.InputSource;

import static fr.sdis83.remocra.db.model.remocra.Tables.COURRIER_DOCUMENT;
import static fr.sdis83.remocra.db.model.remocra.Tables.COURRIER_MODELE;
import static fr.sdis83.remocra.db.model.remocra.Tables.COURRIER_MODELE_DROIT;
import static fr.sdis83.remocra.db.model.remocra.Tables.COURRIER_PARAMETRE;
import static fr.sdis83.remocra.db.model.remocra.Tables.DOCUMENT;
import static fr.sdis83.remocra.db.model.remocra.Tables.EMAIL;
import static fr.sdis83.remocra.db.model.remocra.Tables.PARAM_CONF;
import static fr.sdis83.remocra.db.model.remocra.Tables.ORGANISME;
import static fr.sdis83.remocra.db.model.remocra.Tables.CONTACT;
import static fr.sdis83.remocra.db.model.remocra.Tables.UTILISATEUR;
import static fr.sdis83.remocra.db.model.remocra.Tables.THEMATIQUE;



//import static fr.sdis83.remocra.db.model.remocra.Tables.*;


@Configuration
public class CourrierRepository {

  @Autowired
  DSLContext context;

  @Autowired
  protected ParamConfService paramConfService;

  @Autowired
  private UtilisateurService utilisateurService;

  @Autowired
  private RequeteModeleRepository requeteModeleRepository;

  @PersistenceContext
  private EntityManager entityManager;

  public CourrierRepository() {

  }

  @Bean
  public CourrierRepository courrierRepository(DSLContext context) {
    return new CourrierRepository(context);
  }

  CourrierRepository(DSLContext context) {
    this.context = context;
  }


  public List<CourrierModele> getAllModeleByThematique(String thematique) {
    List<CourrierModele> l = null;
    l = context.select(COURRIER_MODELE.ID, COURRIER_MODELE.CODE, COURRIER_MODELE.LIBELLE, COURRIER_MODELE.DESCRIPTION,
            COURRIER_MODELE.MODELE_OTT, COURRIER_MODELE.SOURCE_XML, COURRIER_MODELE.MESSAGE_OBJET,
            COURRIER_MODELE.MESSAGE_CORPS, COURRIER_MODELE.THEMATIQUE)
            .from(COURRIER_MODELE)
            .join(COURRIER_MODELE_DROIT).on(COURRIER_MODELE.ID.eq(COURRIER_MODELE_DROIT.MODELE))
            .join(THEMATIQUE).on(COURRIER_MODELE.THEMATIQUE.eq(THEMATIQUE.ID))
            .where(THEMATIQUE.CODE.eq(thematique)
                    .and(COURRIER_MODELE_DROIT.PROFIL_DROIT.eq(utilisateurService.getCurrentUtilisateur().getProfilUtilisateur().getId()))
            ).fetchInto(CourrierModele.class);
    return l;
  }

  //Retourne les paramètres d'un modèle de courrier
  public List<CourrierParametre> getParamsByCourrierModele(Long idModele){
    List<CourrierParametre> l = null;
    l = context.select().from(COURRIER_PARAMETRE).where(COURRIER_PARAMETRE.MODELE.eq(idModele)).fetchInto(CourrierParametre.class);
    return l;
  }

  //Retourne la requête XML correspondant au modèle de courrier
  public String getModeleXmlQuery(Long idModele, Map<String, String[]> mapParametres){
      try {
        String query = context.select(COURRIER_MODELE.SOURCE_XML).from(COURRIER_MODELE).where(COURRIER_MODELE.ID.eq(idModele)).fetchOne(COURRIER_MODELE.SOURCE_XML);
        String queryFilled = fillXml(query, mapParametres);
        Result<Record> result = context.fetch(queryFilled);
        String resultXml = "";
        for(Record r : result) {
          resultXml = resultXml+r.getValue(0);
        }
        return resultXml;

      } catch(Exception e){
        e.printStackTrace();
        return null;
      }
  }

  /**
   * Insertion du courrier dans la table document
   */
  public void insertDocument(String code, String fichier){
    try{
      int exist = context.fetchCount(context.select().from(DOCUMENT).where(DOCUMENT.CODE.eq(code)));
      if(exist == 0){
        int result = context.insertInto(DOCUMENT, DOCUMENT.CODE, DOCUMENT.FICHIER, DOCUMENT.REPERTOIRE, DOCUMENT.TYPE)
                .values(code, fichier, paramConfService.getDossierCourriersExternes()+"/"+code+"/", "COURRIER").execute();
      }
    }catch(Exception e){
      e.printStackTrace();
    }
  }

  /**
   * Insertion dans la table courrier_document
   * @param code code du dossier contenant le courrier
   */
  public void insertCourrierDocument( String code, String nomDestinataire, String typeDestinataire, String idDestinataire){
    try{
      Long idDocument = context.select(DOCUMENT.ID).from(DOCUMENT).where(DOCUMENT.CODE.eq(code)).fetchOne(DOCUMENT.ID);
      int result = context.insertInto(COURRIER_DOCUMENT, COURRIER_DOCUMENT.DOCUMENT,
              COURRIER_DOCUMENT.NOM_DESTINATAIRE, COURRIER_DOCUMENT.TYPE_DESTINATAIRE, COURRIER_DOCUMENT.ID_DESTINATAIRE)
              .values(idDocument, nomDestinataire, typeDestinataire, idDestinataire).execute();
    }catch(Exception e){
      e.printStackTrace();
    }
  }

  /**
   * Insertion de la notification du courrier dans la table email
   */
  public void insertEmail(String nomCourrier, String destinataire, String typeDest, String idDest, String codeCourrier){
    try{
      String courrierNom = nomCourrier.split("\\.")[0];
      Long idDocument = context.select(DOCUMENT.ID).from(DOCUMENT).where(DOCUMENT.CODE.eq(codeCourrier)).fetchOne(DOCUMENT.ID);
      String codeLien = context.select(COURRIER_DOCUMENT.CODE).from(COURRIER_DOCUMENT)
                      .where(COURRIER_DOCUMENT.DOCUMENT.eq(idDocument).and(COURRIER_DOCUMENT.ID_DESTINATAIRE.eq(idDest))).fetchOne(COURRIER_DOCUMENT.CODE);
      String corpsMail = context.select(COURRIER_MODELE.MESSAGE_CORPS).from(COURRIER_MODELE).where(COURRIER_MODELE.CODE.eq(courrierNom)).fetchOne(COURRIER_MODELE.MESSAGE_CORPS);
      String objetMail = context.select(COURRIER_MODELE.MESSAGE_OBJET).from(COURRIER_MODELE).where(COURRIER_MODELE.CODE.eq(courrierNom)).fetchOne(COURRIER_MODELE.MESSAGE_OBJET);
      String emailDestinataire = getMailDestinataire(idDest, typeDest);
      String nomExpediteur = context.select(PARAM_CONF.VALEUR).from(PARAM_CONF).where(PARAM_CONF.CLE.eq("PDI_SMTP_EME_NAME")).fetchOne(PARAM_CONF.VALEUR);
      String mailExpediteur = context.select(PARAM_CONF.VALEUR).from(PARAM_CONF).where(PARAM_CONF.CLE.eq("PDI_SMTP_EME_MAIL")).fetchOne(PARAM_CONF.VALEUR);
      String corpsMailFilled = fillCorpsMail(corpsMail, codeLien);
      int result = context.insertInto(EMAIL, EMAIL.CORPS, EMAIL.DESTINATAIRE,EMAIL.DESTINATAIRE_EMAIL, EMAIL.EXPEDITEUR, EMAIL.EXPEDITEUR_EMAIL, EMAIL.OBJET)
              .values(corpsMailFilled, destinataire, emailDestinataire, nomExpediteur, mailExpediteur, objetMail).execute();
    }catch (Exception e){
      e.printStackTrace();
    }
  }

  public String getMailDestinataire(String idDest, String typeDest){
    String emailDest = "";

    if(typeDest.equals("ORGANISME")) {
      emailDest = context.select(ORGANISME.EMAIL_CONTACT).from(ORGANISME).where(ORGANISME.ID.eq(Long.valueOf(idDest))).fetchOne(ORGANISME.EMAIL_CONTACT);
    } else if(typeDest.equals("CONTACT")) {
      emailDest = context.select(CONTACT.EMAIL).from(CONTACT).where(CONTACT.ID.eq(Long.valueOf(idDest))).fetchOne(CONTACT.EMAIL);
    } else if(typeDest.equals("UTILISATEUR")) {
      emailDest = context.select(UTILISATEUR.EMAIL).from(UTILISATEUR).where(UTILISATEUR.ID.eq(Long.valueOf(idDest))).fetchOne(UTILISATEUR.EMAIL);
    }else {
      return null;
    }

    return emailDest;
  }

  /**
   *
   * @param corpsMail
   * @return corps du mail avec le lien de téléchargement du courrier
   */
  public String fillCorpsMail(String corpsMail,String codeCourrier){
    String corpsMailFilled = "";
    CharSequence key = "[URL_TELECHARGEMENT]";
    CharSequence value = context.select(PARAM_CONF.VALEUR).from(PARAM_CONF).where(PARAM_CONF.CLE.eq("PDI_URL_SITE")).fetchOne(PARAM_CONF.VALEUR)+"courrier/"+codeCourrier;
    corpsMailFilled = corpsMail.replace(key, value);
    return corpsMailFilled;
  }

  /**
   * Remplace les ${Variables} par leur valeur si elle existe dans la map
   * @param query requête SQL avec des balises ${}
   * @param mapParametres liste avec les variables remplacant les ${}
   * @return
   */

  public String fillXml(String query, Map<String, String[]> mapParametres){
    String result = query;
    for(Map.Entry<String, String[]> param : mapParametres.entrySet()){
      if(result.contains(param.getKey())){
        CharSequence key = "${"+param.getKey()+"}";
        CharSequence value = param.getValue()[0];
        result =result.replace(key, value);
      }
    }
    return result;
  }

  public String getNomModele(Long idModele){
    String nomModele = context.select(COURRIER_MODELE.MODELE_OTT).from(COURRIER_MODELE).where(COURRIER_MODELE.ID.eq(idModele)).fetchOne(COURRIER_MODELE.MODELE_OTT);
    return nomModele;
  }

  public List<RemocraVueCombo> getComboValues(Long id, String pathParam, Integer limit) throws SQLException, ParseException {
    List<RemocraVueCombo> lstResult = new ArrayList<RemocraVueCombo>();
    @SuppressWarnings("unchecked")
    String query = context.select(COURRIER_PARAMETRE.SOURCE_SQL).from(COURRIER_PARAMETRE).where(COURRIER_PARAMETRE.ID.eq(id)).fetchOne(COURRIER_PARAMETRE.SOURCE_SQL);
    String libelle = context.select(COURRIER_PARAMETRE.SOURCE_SQL_LIBELLE).from(COURRIER_PARAMETRE).where(COURRIER_PARAMETRE.ID.eq(id)).fetchOne(COURRIER_PARAMETRE.SOURCE_SQL_LIBELLE);
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
      query = "SELECT * FROM ("+ query +") AS foo WHERE lower(" +libelle +") LIKE lower( "+"'%"+pathParam+"%') limit "+limit ;
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
}
