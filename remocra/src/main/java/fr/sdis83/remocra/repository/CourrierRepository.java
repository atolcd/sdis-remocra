package fr.sdis83.remocra.repository;

import fr.sdis83.remocra.db.model.remocra.tables.pojos.CourrierModele;
import fr.sdis83.remocra.db.model.remocra.tables.pojos.CourrierParametre;
import fr.sdis83.remocra.domain.remocra.Organisme;
import fr.sdis83.remocra.domain.remocra.RemocraVueCombo;
import fr.sdis83.remocra.domain.remocra.TypeDroit;
import fr.sdis83.remocra.domain.remocra.Utilisateur;
import fr.sdis83.remocra.exception.BusinessException;
import fr.sdis83.remocra.security.AuthoritiesUtil;
import fr.sdis83.remocra.util.StatementFormat;
import fr.sdis83.remocra.service.ParamConfService;
import fr.sdis83.remocra.service.UtilisateurService;
import fr.sdis83.remocra.web.message.ItemFilter;
import fr.sdis83.remocra.web.message.ItemSorting;
import fr.sdis83.remocra.web.model.CourrierDocumentModel;
import org.apache.log4j.Logger;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Result;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.NameTokenizers;
import org.modelmapper.jooq.RecordValueReader;
import org.postgresql.jdbc.PgSQLXML;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
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
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static fr.sdis83.remocra.db.model.remocra.Tables.CONTACT;
import static fr.sdis83.remocra.db.model.remocra.Tables.COURRIER_DOCUMENT;
import static fr.sdis83.remocra.db.model.remocra.Tables.COURRIER_MODELE;
import static fr.sdis83.remocra.db.model.remocra.Tables.COURRIER_MODELE_DROIT;
import static fr.sdis83.remocra.db.model.remocra.Tables.COURRIER_PARAMETRE;
import static fr.sdis83.remocra.db.model.remocra.Tables.DOCUMENT;
import static fr.sdis83.remocra.db.model.remocra.Tables.EMAIL;
import static fr.sdis83.remocra.db.model.remocra.Tables.PARAM_CONF;
import static fr.sdis83.remocra.db.model.remocra.Tables.ORGANISME;
import static fr.sdis83.remocra.db.model.remocra.Tables.PARAM_CONF;
import static fr.sdis83.remocra.db.model.remocra.Tables.THEMATIQUE;
import static fr.sdis83.remocra.db.model.remocra.Tables.UTILISATEUR;

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

  @Autowired
  private AuthoritiesUtil authUtils;

  @PersistenceContext
  private EntityManager entityManager;

  private final Logger logger = Logger.getLogger(getClass());

  public CourrierRepository() {

  }

  @Bean
  public CourrierRepository courrierRepository(DSLContext context) {
    return new CourrierRepository(context);
  }

  CourrierRepository(DSLContext context) {
    this.context = context;
  }


  public List<CourrierModele> getAllModeleByThematique(String thematique) throws BusinessException {
    List<CourrierModele> l = null;
    //System.out.println(utilisateurService.getCurrentUtilisateur().getProfilUtilisateur().getId());
    l = context.select(COURRIER_MODELE.ID, COURRIER_MODELE.CODE, COURRIER_MODELE.LIBELLE, COURRIER_MODELE.DESCRIPTION,
            COURRIER_MODELE.MODELE_OTT, COURRIER_MODELE.SOURCE_XML, COURRIER_MODELE.MESSAGE_OBJET,
            COURRIER_MODELE.MESSAGE_CORPS, COURRIER_MODELE.THEMATIQUE)
            .from(COURRIER_MODELE)
            .join(COURRIER_MODELE_DROIT).on(COURRIER_MODELE.ID.eq(COURRIER_MODELE_DROIT.MODELE))
            .join(THEMATIQUE).on(COURRIER_MODELE.THEMATIQUE.eq(THEMATIQUE.ID))
            .where(THEMATIQUE.CODE.eq(thematique)
                    .and(COURRIER_MODELE_DROIT.PROFIL_DROIT.eq(utilisateurService.getCurrentProfilDroit().getId()))
            ).fetchInto(CourrierModele.class);
    return l;
  }


    /**
     * Information des courriers accessibles par l'utilisateur courant en fonction de ses droits
     * @return Un objet JSON contenant les informations sur les courriers
     */
  public List<CourrierDocumentModel> getCourriersAccessibles(Boolean distinct, Integer start, Integer limit, List<ItemFilter> itemFilter, List<ItemSorting> sortList) {

    /* Niveau des droits d'accès de l'utilisateur courant
     *  1 : Courriers propres uniquement
     *  2 : Courriers propres, courriers de son organisme + courrier des organismes enfants
     *  3 : Tous les courriers de l'application
     */
    int niveauDroits = 1;
    if(authUtils.hasRight(TypeDroit.TypeDroitEnum.COURRIER_ADMIN_R)) {
        niveauDroits = 3;
    } else if(authUtils.hasRight(TypeDroit.TypeDroitEnum.COURRIER_ORGANISME_R)) {
        niveauDroits = 2;
    }
    
    StringBuilder requete = new StringBuilder();
    if(distinct) {
      requete.append("SELECT DISTINCT codeDocument, nomDocument, dateDoc, objet FROM (");
    }

    requete.append(this.getRequeteCourriersAccessibles(niveauDroits, itemFilter, sortList));

    if(distinct) {
      requete.append(" ) AS R ORDER BY dateDoc DESC ");
    }
    requete.append(" LIMIT "+limit+" OFFSET "+start);


    List<CourrierDocumentModel> listeCourriers = new ArrayList<>();

    Result<Record> records = context.resultQuery(requete.toString()).fetch();

    ModelMapper modelMapper = new ModelMapper();
    modelMapper.getConfiguration().addValueReader(new RecordValueReader());
    modelMapper.getConfiguration().setSourceNameTokenizer(NameTokenizers.CAMEL_CASE);

    for(Record r : records) {
      CourrierDocumentModel cd = modelMapper.map(r, CourrierDocumentModel.class);

      Utilisateur u = utilisateurService.getCurrentUtilisateur();
      Long idUtilisateur = u.getId();
      Long idOrganisme = u.getOrganisme().getId();
      Long idDestinataire = cd.getIdDestinataire();
      String typeDestinataire = cd.getTypeDestinataire();
      if(
        (idUtilisateur == idDestinataire && "UTILISATEUR".equalsIgnoreCase(typeDestinataire)) ||
          (idOrganisme == idDestinataire && "ORGANISME".equalsIgnoreCase(typeDestinataire))
      ) {
        cd.setUtilisateurDestinataire(true);
      }else{
        cd.setUtilisateurDestinataire(false);

      }
      listeCourriers.add(cd);
    }

    return listeCourriers;
  }

  private StringBuilder getRequeteCourriersAccessibles(Integer niveauDroits, List<ItemFilter> itemFilter, List<ItemSorting> sortList) {
    StringBuilder requete = new StringBuilder();

    requete.append("SELECT cd.id as id, cd.document as document, cd.code as code, cd.nom_destinataire as nomDestinataire, cd.type_destinataire as typeDestinataire, " +
                    "cd.reference as reference, cd.objet as objet, cd.expediteur as expediteur, "+
                    "cd.id_destinataire as idDestinataire, cd.accuse as accuse, COALESCE(u.email, o.email_contact, c.email) as mail, d.date_doc as dateDoc, " +
                    "d.code as codeDocument, d.fichier as nomDocument "+
                    "FROM remocra.courrier_document cd " +
                    "JOIN remocra.document d ON d.id = cd.document "+
                    "LEFT JOIN remocra.utilisateur u ON u.id = cd.id_destinataire AND type_destinataire = 'UTILISATEUR' "+
                    "LEFT JOIN remocra.organisme o ON o.id = cd.id_destinataire AND type_destinataire = 'ORGANISME' "+
                    "LEFT JOIN remocra.contact c ON c.id = cd.id_destinataire AND type_destinataire = 'CONTACT' WHERE ( ");



    // Selon les droits de l'utilisateur, on détermine les clauses de la condition WHERE

    StringBuilder conditionDroits = new StringBuilder();

    // Droit de lire ses propes courriers -> on regade les courriers adressés au mail de l'utilisateur courant (utilisateur ou contact)
    if(niveauDroits >= 1) {
        conditionDroits.append("(UPPER(COALESCE(u.email, o.email_contact, c.email)) = UPPER('");
        conditionDroits.append(utilisateurService.getCurrentUtilisateur().getEmail());
        conditionDroits.append("')) ");
    }

    // Droit de lire les courriers de son organisme: en plus de lire ses courriers, lire ceux de son organisme + ceux de ses organismes enfants
    if (niveauDroits >= 2) {
        String listeOrganismes = (Organisme.getOrganismeAndChildren(utilisateurService.getCurrentUtilisateur().getOrganisme().getId().intValue())).toString();
        conditionDroits.append(" OR (cd.type_destinataire = 'ORGANISME' AND cd.id_destinataire IN ");
        conditionDroits.append(listeOrganismes.replace("[", "(").replace("]", ")"));
        conditionDroits.append(" ) ");
    }

    // Droit de lire tous les courriers
    if(niveauDroits == 3) {
      conditionDroits.append(" OR TRUE");
    }

    requete.append(conditionDroits+" ) ");


    // Mise en place des filtres
    if (itemFilter!=null && itemFilter.size()>0) {
        StringBuilder conditionFiltre = new StringBuilder();
        conditionFiltre.append("true");
        for(ItemFilter f : itemFilter) {
            if ("document".equalsIgnoreCase(f.getFieldName())) {
                conditionFiltre.append(" AND (cd.document = ").append(f.getValue()).append(") ");

            } else if ("objet".equalsIgnoreCase(f.getFieldName())) {
                conditionFiltre.append(" AND (UPPER(cd.objet) LIKE '%").append(f.getValue().toUpperCase()).append("%') ");

            } else if ("nomDestinataire".equalsIgnoreCase(f.getFieldName())) {
                conditionFiltre.append(" AND (UPPER(cd.nom_destinataire) LIKE '%").append(f.getValue().toUpperCase()).append("%') ");

            } else if("accuse".equalsIgnoreCase(f.getFieldName())) {
                if("true".equalsIgnoreCase(f.getValue())) {
                    conditionFiltre.append(" AND cd.accuse IS NOT NULL ");
                } else if("false".equalsIgnoreCase(f.getValue())) {
                    conditionFiltre.append(" AND cd.accuse IS NULL ");
                }
            } else if("destinataire".equalsIgnoreCase(f.getFieldName())) {
                conditionFiltre.append(" AND (UPPER(COALESCE(u.email, o.email_contact, c.email)) LIKE '%").append(f.getValue().toUpperCase()).append("%') ");
            } else if("date".equalsIgnoreCase(f.getFieldName())) {
                conditionFiltre.append(" AND (d.date_doc > '").append(f.getValue()).append("') ");
            } else if("reference".equalsIgnoreCase(f.getFieldName())) {
                conditionFiltre.append(" AND (UPPER(cd.reference) LIKE '%").append(f.getValue().toUpperCase()).append("%') ");
            } else if("expediteur".equalsIgnoreCase(f.getFieldName())) {
                conditionFiltre.append(" AND (UPPER(cd.expediteur) LIKE '%").append(f.getValue().toUpperCase()).append("%') ");
            } else {
                logger.info("CourrierRepository - critère de filtre inconnu : " + f.getFieldName());
            }
        }
        if(conditionFiltre.length() > 0) {
            requete.append(" AND (").append(conditionFiltre.toString()).append(") ");
        }
    }

    // Mise en place du critère de tri
    if(!sortList.isEmpty()) {
      ItemSorting sort = sortList.get(0); // On n'utilise qu'un seul critère à la fois par requête

      if("date".equalsIgnoreCase(sort.getFieldName())) {
          requete.append(" ORDER BY d.date_doc "+sort.getDirection()+" ");
      } else if("objet".equalsIgnoreCase(sort.getFieldName())) {
          requete.append(" ORDER BY UPPER(cd.objet) "+sort.getDirection()+" ");
      } else if("destinataire".equalsIgnoreCase(sort.getFieldName())) {
          requete.append(" ORDER BY UPPER(COALESCE(u.email, o.email_contact, c.email)) "+sort.getDirection()+" ");
      } else if("reference".equalsIgnoreCase(sort.getFieldName())) {
          requete.append(" ORDER BY UPPER(cd.reference) "+sort.getDirection()+" ");
      } else if("expediteur".equalsIgnoreCase(sort.getFieldName())) {
          requete.append(" ORDER BY UPPER(cd.expediteur) "+sort.getDirection()+" ");
      } else {
          logger.info("CourrierRepository - critère de tri inconnu : "+sort.getFieldName());
          requete.append(" ORDER BY d.date_doc DESC ");
      }

        /*
         * Si ce n'est pas déjà un tri par date, on rajoute quand même un tri par date
         * Cela permet de garder les courriers groupés par document, ce qui n'est pas garanti si le critère de tri existe en doublon (ex: nom de fichier)
         */
      if(!"date".equalsIgnoreCase(sort.getFieldName())) {
          requete.append(" , d.date_doc DESC ");
      }
    } else {
        requete.append(" ORDER BY d.date_doc DESC ");
    }

    return requete;
  }

    /**
     * Retourne le nombre de courriers accessibles par l'utilisateur courant
     */
  public Integer getCourriersAccessiblesCount(Boolean distinct, List<ItemFilter> itemFilter, List<ItemSorting> sortList)
  {
    int niveauDroits = 1;
    if(authUtils.hasRight(TypeDroit.TypeDroitEnum.COURRIER_ADMIN_R)) {
        niveauDroits = 3;
    } else if(authUtils.hasRight(TypeDroit.TypeDroitEnum.COURRIER_ORGANISME_R)) {
        niveauDroits = 2;
    }

    StringBuilder count = new StringBuilder();
    count.append("SELECT COUNT(*) FROM (");
    if(distinct) {
        count.append("SELECT DISTINCT codeDocument FROM (");
    }
    count.append(this.getRequeteCourriersAccessibles(niveauDroits, itemFilter, sortList)).append(") AS R");
    if(distinct) {
        count.append(") AS R2");
    }
    Query query = entityManager.createNativeQuery(count.toString());
    return Integer.valueOf(query.getSingleResult().toString());
  }

    /**
     * Met un accusé pour tous les courriers contenant un fichier spécifique
     * @param code Le code du document contenu dans les fichiers
     */
  @Transactional
  public void setAccuseForDocumentCode(String code) {

      entityManager.createNativeQuery("UPDATE remocra.courrier_document AS cd " +
              "SET accuse = NOW() " +
              "FROM remocra.document as d " +
              "WHERE d.id = cd.document " +
              "AND d.code = :code")
            .setParameter("code", code)
            .executeUpdate();
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
        List<PgSQLXML> result = (List<PgSQLXML>)context.fetchValues(queryFilled);
        String resultXml = result.get(0).getString();
        return resultXml;

      } catch(Exception e){
        e.printStackTrace();
        return null;
      }
  }

  /**
   * Insertion du courrier dans la table document
   */
  public String insertDocument(String code, String fichier){
    try{
      int exist = context.fetchCount(context.select().from(DOCUMENT).where(DOCUMENT.CODE.eq(code)));
      if(exist == 0){
        int result = context.insertInto(DOCUMENT, DOCUMENT.CODE, DOCUMENT.FICHIER, DOCUMENT.REPERTOIRE, DOCUMENT.TYPE)
                .values(code, fichier, paramConfService.getDossierCourriersExternes()+"/"+code+"/", "COURRIER").execute();
      }
      return "";
    }catch(Exception e){
      e.printStackTrace();
      return "remocra.document ";
    }
  }

  /**
   * Insertion dans la table courrier_document
   * @param code code du dossier contenant le courrier
   */
  public String insertCourrierDocument( String code, String nomDestinataire, String typeDestinataire, Long idDestinataire, String reference, String objet, String expediteur){
    try{
      Long idDocument = context.select(DOCUMENT.ID).from(DOCUMENT).where(DOCUMENT.CODE.eq(code)).fetchOne(DOCUMENT.ID);
      int result = context.insertInto(COURRIER_DOCUMENT, COURRIER_DOCUMENT.DOCUMENT,
              COURRIER_DOCUMENT.NOM_DESTINATAIRE, COURRIER_DOCUMENT.TYPE_DESTINATAIRE, COURRIER_DOCUMENT.ID_DESTINATAIRE, COURRIER_DOCUMENT.REFERENCE, COURRIER_DOCUMENT.OBJET, COURRIER_DOCUMENT.EXPEDITEUR)
              .values(idDocument, nomDestinataire, typeDestinataire, idDestinataire, reference, objet, expediteur).execute();
      return "";
    }catch(Exception e){
      e.printStackTrace();
      return "remocra.courrierDocument ";
    }
  }

  /**
   * Insertion de la notification du courrier dans la table email
   */
  public String insertEmail(String codeModele, String destinataire, String typeDest, Long idDest, String codeCourrier){
    try{
      Long idDocument = context.select(DOCUMENT.ID).from(DOCUMENT).where(DOCUMENT.CODE.eq(codeCourrier)).fetchOne(DOCUMENT.ID);
      String codeLien = context.select(COURRIER_DOCUMENT.CODE).from(COURRIER_DOCUMENT)
                      .where(COURRIER_DOCUMENT.DOCUMENT.eq(idDocument).and(COURRIER_DOCUMENT.ID_DESTINATAIRE.eq(idDest))).fetchOne(COURRIER_DOCUMENT.CODE);
      String corpsMail = context.select(COURRIER_MODELE.MESSAGE_CORPS).from(COURRIER_MODELE).where(COURRIER_MODELE.CODE.eq(codeModele)).fetchOne(COURRIER_MODELE.MESSAGE_CORPS);
      String objetMail = context.select(COURRIER_MODELE.MESSAGE_OBJET).from(COURRIER_MODELE).where(COURRIER_MODELE.CODE.eq(codeModele)).fetchOne(COURRIER_MODELE.MESSAGE_OBJET);
      String emailDestinataire = getMailDestinataire(idDest, typeDest);
      String nomExpediteur = context.select(PARAM_CONF.VALEUR).from(PARAM_CONF).where(PARAM_CONF.CLE.eq("PDI_SMTP_EME_NAME")).fetchOne(PARAM_CONF.VALEUR);
      String mailExpediteur = context.select(PARAM_CONF.VALEUR).from(PARAM_CONF).where(PARAM_CONF.CLE.eq("PDI_SMTP_EME_MAIL")).fetchOne(PARAM_CONF.VALEUR);
      String corpsMailFilled = "";

      if(corpsMail != null && codeLien != null) {
        corpsMailFilled = fillCorpsMail(corpsMail, codeLien);
      } else {
        return "remocra.email ";
      }
      int result = context.insertInto(EMAIL, EMAIL.CORPS, EMAIL.DESTINATAIRE,EMAIL.DESTINATAIRE_EMAIL, EMAIL.EXPEDITEUR, EMAIL.EXPEDITEUR_EMAIL, EMAIL.OBJET)
              .values(corpsMailFilled, destinataire, emailDestinataire, nomExpediteur, mailExpediteur, objetMail).execute();
      return "";
    }catch (Exception e){
      e.printStackTrace();
      return "remocra.email ";
    }
  }

  public String getMailDestinataire(Long idDest, String typeDest){
    String emailDest = "";

    if(typeDest.equals("ORGANISME")) {
      emailDest = context.select(ORGANISME.EMAIL_CONTACT).from(ORGANISME).where(ORGANISME.ID.eq(idDest)).fetchOne(ORGANISME.EMAIL_CONTACT);
    } else if(typeDest.equals("CONTACT")) {
      emailDest = context.select(CONTACT.EMAIL).from(CONTACT).where(CONTACT.ID.eq(idDest)).fetchOne(CONTACT.EMAIL);
    } else if(typeDest.equals("UTILISATEUR")) {
      emailDest = context.select(UTILISATEUR.EMAIL).from(UTILISATEUR).where(UTILISATEUR.ID.eq(idDest)).fetchOne(UTILISATEUR.EMAIL);
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
          StatementFormat.PreparedStatement(preparedStatement,i+1, typeParametre.get(j));
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
}
