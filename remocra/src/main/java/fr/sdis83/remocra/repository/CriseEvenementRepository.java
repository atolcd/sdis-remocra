package fr.sdis83.remocra.repository;

import static fr.sdis83.remocra.db.model.remocra.Tables.CRISE_DOCUMENT;
import static fr.sdis83.remocra.db.model.remocra.Tables.CRISE_EVENEMENT;
import static fr.sdis83.remocra.db.model.remocra.Tables.CRISE_EVENEMENT_COMPLEMENT;
import static fr.sdis83.remocra.db.model.remocra.Tables.CRISE_EVENEMENT_INTERVENTION;
import static fr.sdis83.remocra.db.model.remocra.Tables.CRISE_SUIVI;
import static fr.sdis83.remocra.db.model.remocra.Tables.INTERVENTION;
import static fr.sdis83.remocra.db.model.remocra.Tables.TYPE_CRISE_EVENEMENT_DROIT;
import static fr.sdis83.remocra.db.model.remocra.Tables.TYPE_CRISE_NATURE_EVENEMENT;
import static fr.sdis83.remocra.db.model.remocra.Tables.TYPE_CRISE_PROPRIETE_EVENEMENT;
import static fr.sdis83.remocra.db.model.remocra.Tables.UTILISATEUR;
import static fr.sdis83.remocra.db.model.remocra.tables.Document.DOCUMENT;
import static fr.sdis83.remocra.util.GeometryUtil.sridFromGeom;
import static org.jooq.impl.DSL.and;
import static org.jooq.impl.DSL.row;
import static org.jooq.impl.DSL.select;
import static org.jooq.impl.DSL.trueCondition;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.vividsolutions.jts.geom.Geometry;
import flexjson.JSONDeserializer;
import fr.sdis83.remocra.GlobalConstants;
import fr.sdis83.remocra.db.converter.InstantConverter;
import fr.sdis83.remocra.db.model.remocra.Tables;
import fr.sdis83.remocra.db.model.remocra.tables.CriseDocument;
import fr.sdis83.remocra.db.model.remocra.tables.pojos.CriseEvenementComplement;
import fr.sdis83.remocra.db.model.remocra.tables.pojos.CriseSuivi;
import fr.sdis83.remocra.db.model.remocra.tables.pojos.Intervention;
import fr.sdis83.remocra.db.model.remocra.tables.pojos.TypeCriseNatureEvenement;
import fr.sdis83.remocra.db.model.remocra.tables.pojos.TypeCriseProprieteEvenement;
import fr.sdis83.remocra.domain.remocra.Document;
import fr.sdis83.remocra.domain.remocra.RemocraVueCombo;
import fr.sdis83.remocra.domain.remocra.Utilisateur;
import fr.sdis83.remocra.domain.utils.RemocraDateHourTransformer;
import fr.sdis83.remocra.service.ParamConfService;
import fr.sdis83.remocra.service.UtilisateurService;
import fr.sdis83.remocra.util.DocumentUtil;
import fr.sdis83.remocra.util.GeometryUtil;
import fr.sdis83.remocra.util.StatementFormat;
import fr.sdis83.remocra.web.deserialize.GeometryFactory;
import fr.sdis83.remocra.web.message.ItemFilter;
import fr.sdis83.remocra.web.model.CriseEvenement;
import org.apache.log4j.Logger;
import org.joda.time.Instant;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.impl.DSL;
import org.json.JSONObject;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.NameTokenizers;
import org.modelmapper.jooq.RecordValueReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

@Configuration
public class CriseEvenementRepository {
  private final Logger logger = Logger.getLogger(getClass());
  private final  DateFormat df = new SimpleDateFormat(RemocraDateHourTransformer.FORMAT);

  @Autowired
  DSLContext context;

  @Autowired
  protected ParamConfService paramConfService;

  @Autowired
  protected CriseRepository criseRepository;


  @Autowired
  private UtilisateurService utilisateurService;

  @PersistenceContext
  private EntityManager entityManager;

  public CriseEvenementRepository() {

  }


  @Bean
  public CriseEvenementRepository criseEvenementRepository(DSLContext context) {
    return new CriseEvenementRepository(context);
  }

  CriseEvenementRepository(DSLContext context) {
    this.context = context;
  }

  public List<CriseEvenement> getEventByCrise(Long id, List<ItemFilter> itemFilters) {
    //dans le cas de fusion on recupère les crises en fonction de type et en eliminant la crise choisis

    Condition c  = getAndCondition(itemFilters);
    Result<Record> criseEvenementRecord = null;
    List<CriseEvenement> crEvenements = new ArrayList<CriseEvenement>();
    criseEvenementRecord = context.select(CRISE_EVENEMENT.ID.as("criseEvenementId"), CRISE_EVENEMENT.GEOMETRIE.as("criseEvenementGeometrie"), CRISE_EVENEMENT.NOM.as("criseEvenementNom"),
        CRISE_EVENEMENT.DESCRIPTION.as("criseEvenementDescription"), CRISE_EVENEMENT.CONSTAT.as("criseEvenementConstat"), CRISE_EVENEMENT.REDEFINITION.as("criseEvenementRedefinition"), CRISE_EVENEMENT.CLOTURE.as("criseEvenementCloture"),
        CRISE_EVENEMENT.ORIGINE.as("criseEvenementOrigine"),CRISE_EVENEMENT.CONTEXTE.as("criseEvenementContexte"), CRISE_EVENEMENT.IMPORTANCE.as("criseEvenementImportance"), CRISE_EVENEMENT.CRISE.as("criseCrise"))
        .select(TYPE_CRISE_NATURE_EVENEMENT.ID.as("typeCriseNatureEvenementId"), TYPE_CRISE_NATURE_EVENEMENT.ACTIF.as("typeCriseNatureEvenementActif"),
            TYPE_CRISE_NATURE_EVENEMENT.CODE.as("typeCriseNatureEvenementCode"), TYPE_CRISE_NATURE_EVENEMENT.NOM.as("typeCriseNatureEvenementNom"),
            TYPE_CRISE_NATURE_EVENEMENT.TYPE_GEOMETRIE.as("typeCriseNatureEvenementTypeGeometrie"))
        .select(UTILISATEUR.IDENTIFIANT.as("utilisateurIdentifiant"))
        .from(CRISE_EVENEMENT)
        .join(TYPE_CRISE_NATURE_EVENEMENT).on(TYPE_CRISE_NATURE_EVENEMENT.ID.eq(CRISE_EVENEMENT.NATURE_EVENEMENT))
        .leftOuterJoin(UTILISATEUR).on(UTILISATEUR.ID.eq(CRISE_EVENEMENT.AUTEUR_EVENEMENT))
        .where(CRISE_EVENEMENT.CRISE.eq(id)).and(c)
       .fetch();

    ModelMapper modelMapper = new ModelMapper();
    modelMapper.getConfiguration().addValueReader(new RecordValueReader());
    modelMapper.getConfiguration().setSourceNameTokenizer(NameTokenizers.CAMEL_CASE);

    for (Record evenement : criseEvenementRecord){
      CriseEvenement crEvenement = modelMapper.map(evenement, CriseEvenement.class);
      List<CriseSuivi> suivis= this.getMessages(crEvenement.getId());
      //On recupere la liste des suivis
      crEvenement.setCriseSuivis(suivis);
      crEvenements.add(crEvenement);
    }

    return crEvenements ;

  }

  public List<CriseEvenement> getEventById(Long id) {
    Result<Record> criseEvenementRecord = null;
    List<CriseEvenement> crEvenements = new ArrayList<CriseEvenement>();
    criseEvenementRecord = context.select(CRISE_EVENEMENT.ID.as("criseEvenementId"), CRISE_EVENEMENT.GEOMETRIE.as("criseEvenementGeometrie"), CRISE_EVENEMENT.NOM.as("criseEvenementNom"),
        CRISE_EVENEMENT.DESCRIPTION.as("criseEvenementDescription"), CRISE_EVENEMENT.CONSTAT.as("criseEvenementConstat"), CRISE_EVENEMENT.REDEFINITION.as("criseEvenementRedefinition"), CRISE_EVENEMENT.CLOTURE.as("criseEvenementCloture"),
        CRISE_EVENEMENT.ORIGINE.as("criseEvenementOrigine"), CRISE_EVENEMENT.IMPORTANCE.as("criseEvenementImportance"), CRISE_EVENEMENT.TAGS.as("criseEvenementTags"), CRISE_EVENEMENT.CRISE.as("criseCrise"))
        .select(TYPE_CRISE_NATURE_EVENEMENT.ID.as("typeCriseNatureEvenementId"), TYPE_CRISE_NATURE_EVENEMENT.ACTIF.as("typeCriseNatureEvenementActif"),
            TYPE_CRISE_NATURE_EVENEMENT.CODE.as("typeCriseNatureEvenementCode"), TYPE_CRISE_NATURE_EVENEMENT.NOM.as("typeCriseNatureEvenementNom"),
            TYPE_CRISE_NATURE_EVENEMENT.TYPE_GEOMETRIE.as("typeCriseNatureEvenementTypeGeometrie"))
        .select(UTILISATEUR.IDENTIFIANT.as("utilisateurIdentifiant"))
        .from(CRISE_EVENEMENT)
        .join(TYPE_CRISE_NATURE_EVENEMENT).on(TYPE_CRISE_NATURE_EVENEMENT.ID.eq(CRISE_EVENEMENT.NATURE_EVENEMENT))
        .leftOuterJoin(UTILISATEUR).on(UTILISATEUR.ID.eq(CRISE_EVENEMENT.AUTEUR_EVENEMENT))
        .where(CRISE_EVENEMENT.ID.eq(id))
        .fetch();

    ModelMapper modelMapper = new ModelMapper();
    modelMapper.getConfiguration().addValueReader(new RecordValueReader());
    modelMapper.getConfiguration().setSourceNameTokenizer(NameTokenizers.CAMEL_CASE);

    for (Record evenement : criseEvenementRecord){
      CriseEvenement crEvenement = modelMapper.map(evenement, CriseEvenement.class);
      //On recupere la liste des suivis
      List<CriseSuivi> suivis= this.getMessages(crEvenement.getId());
      crEvenement.setCriseSuivis(suivis);

      //On recupere la liste des complements
      List<CriseEvenementComplement> complement= this.getComplement(crEvenement.getId());
      crEvenement.setCriseComplement(complement);

      //On récupère la liste des interventions
      List<Intervention> interventions = this.getInterventions(crEvenement.getId());
      crEvenement.setInterventions(interventions);
      crEvenements.add(crEvenement);
    }

    return crEvenements ;

  }

  public List<CriseEvenement> findCriseEventsByPoint(String point, String projection, Long crise) {

    String qlString = "select ce.id, ce.nom, st_asgeojson(ce.geometrie) as geometrie, tce.id as idnature, tce.nom as nomnature FROM remocra.crise_evenement as ce " +
        "join remocra.type_crise_nature_evenement as tce " +
        "on ce.nature_evenement = tce.id "
        // Si le point fait partie de la géometrie d'une crise
        + "where ce.crise = "+crise+" and (ST_Contains(geometrie,ST_Transform(ST_SetSRID(ST_makePoint(" + point + ")," + projection + "), "+ GlobalConstants.SRID_2154 +")) = true "
        // Et que la crise est dans la zone de compétence de
        // l'utilisateur connecté
        + "or st_distance(geometrie ,ST_Transform(ST_SetSRID(ST_makePoint(" + point + ")," + projection + "), "+ GlobalConstants.SRID_2154 +")) < 100 "

        + "and st_dwithin(geometrie, st_geomfromtext('"+utilisateurService.getCurrentUtilisateur().getOrganisme().getZoneCompetence().getGeometrie()+"', "+ GlobalConstants.SRID_2154 +"), 0) = true)";

    List<CriseEvenement> l = new ArrayList<CriseEvenement>();
    Result<Record> result = context.fetch(qlString);
    for (Record evenement : result){
      CriseEvenement cr = new CriseEvenement();
      cr.setId(Long.valueOf(String.valueOf(evenement.getValue("id"))));
      cr.setNom(String.valueOf(evenement.getValue("nom")));
      cr.setNatureId(Long.valueOf(String.valueOf(evenement.getValue("idnature"))));
      cr.setNatureNom(String.valueOf(evenement.getValue("nomnature")));
      if(evenement.getValue("geometrie") != null){
        cr.setGeoJsonGeometry(String.valueOf(evenement.getValue("geometrie")));
      }
      l.add(cr);
    }

    return l;
  }

  public List<CriseSuivi> getMessages(Long id){
      return context.select().from(CRISE_SUIVI).where(CRISE_SUIVI.EVENEMENT.eq(id)).fetchInto(CriseSuivi.class);
  }
  public List<CriseEvenementComplement> getComplement(Long id){
    return context.select().from(CRISE_EVENEMENT_COMPLEMENT).where(CRISE_EVENEMENT_COMPLEMENT.EVENEMENT.eq(id)).fetchInto(CriseEvenementComplement.class);
  }

  public List<Intervention> getInterventions(Long id){
    return context.select().from(CRISE_EVENEMENT_INTERVENTION)
        .join(INTERVENTION).on(CRISE_EVENEMENT_INTERVENTION.INTERVENTION.eq(INTERVENTION.ID)).where(CRISE_EVENEMENT_INTERVENTION.CRISE_EVENEMENT.eq(id)).fetchInto(Intervention.class);
  }

  public fr.sdis83.remocra.db.model.remocra.tables.pojos.CriseEvenement createEvent( MultipartHttpServletRequest request) throws ParseException {

    Map<String, MultipartFile> files = request.getFileMap();
    fr.sdis83.remocra.db.model.remocra.tables.pojos.CriseEvenement c = new fr.sdis83.remocra.db.model.remocra.tables.pojos.CriseEvenement();
    c.setNom(request.getParameter("nom"));
    c.setDescription( request.getParameter("description"));
    c.setOrigine(request.getParameter("origine"));
    c.setImportance(Integer.valueOf(request.getParameter("importance")));
    c.setTags(request.getParameter("tags"));
    c.setContexte(request.getParameter("contexte"));
    c.setCrise(Long.valueOf(String.valueOf(request.getParameter("crise"))));
    c.setAuteurEvenement(utilisateurService.getCurrentUtilisateur().getId());
    Geometry geom = null;
    if(request.getParameter("geometrie") != null){
      String[] coord = request.getParameter("geometrie").split(";");
      Integer srid = sridFromGeom(coord[0]);
      geom = GeometryUtil.toGeometry(coord[1],srid);
    }
    if(request.getParameter("cloture") != null){
      c.setCloture(new Instant());
    }

    TypeCriseNatureEvenement tcn = context.select().from(TYPE_CRISE_NATURE_EVENEMENT)
        .where(TYPE_CRISE_NATURE_EVENEMENT.ID.eq(Long.valueOf(String.valueOf(request.getParameter("natureEvent")))))
        .fetchInto(TypeCriseNatureEvenement.class).get(0);
    c.setNatureEvenement(tcn.getId());
    Date constat = df.parse(request.getParameter("constat"));
    Instant t = new Instant(constat);
    c.setConstat(t);
    int result = context.insertInto(CRISE_EVENEMENT, CRISE_EVENEMENT.NOM, CRISE_EVENEMENT.GEOMETRIE, CRISE_EVENEMENT.DESCRIPTION, CRISE_EVENEMENT.CONSTAT, CRISE_EVENEMENT.CLOTURE,
        CRISE_EVENEMENT.ORIGINE, CRISE_EVENEMENT.IMPORTANCE, CRISE_EVENEMENT.TAGS,CRISE_EVENEMENT.CRISE, CRISE_EVENEMENT.NATURE_EVENEMENT,
        CRISE_EVENEMENT.AUTEUR_EVENEMENT,CRISE_EVENEMENT.CONTEXTE)
        .values(c.getNom(), geom != null ? geom : null  ,  c.getDescription(),c.getConstat(), c.getCloture(),c.getOrigine(), c.getImportance(), c.getTags(), c.getCrise(),c.getNatureEvenement(),c.getAuteurEvenement(), c.getContexte()).execute();
    if(result!=0) {
      //on sélectionne la dernioère insertion
      Long idCriseEvent = context.select(DSL.max((CRISE_EVENEMENT.ID))).from(CRISE_EVENEMENT).fetchOne().value1();


      // ajout des documents
      this.addEventDocuments(files, c.getCrise(), idCriseEvent);

      //Ajout des complements
      this.addComplement(idCriseEvent, request.getParameterMap());

      //Ajout des interventions
      //mise à jour des interventions
      if(request.getParameter("interventions") != null) {
        List<Integer> interventions = new ArrayList<Integer>();
        interventions = new JSONDeserializer<ArrayList<Integer>>().deserialize(request.getParameter("interventions"));
        this.addInterventions(idCriseEvent, interventions);

      }

      context.update(CRISE_EVENEMENT)
          .set(CRISE_EVENEMENT.NOM
              ,c.getNom())
          .where(CRISE_EVENEMENT.ID.eq(Long.valueOf(idCriseEvent))).execute();

    }


    return c;

  }

  public fr.sdis83.remocra.db.model.remocra.tables.pojos.CriseEvenement updateEvent(Long id, MultipartHttpServletRequest request) throws ParseException {

    Map<String, MultipartFile> files = request.getFileMap();
    List<Long> filesToSave = new ArrayList<Long>();
    if(request.getParameter("filesToSave") != null) {
      filesToSave = new JSONDeserializer<ArrayList<Long>>().deserialize(request.getParameter("filesToSave"));
    }
    fr.sdis83.remocra.db.model.remocra.tables.pojos.CriseEvenement c = new fr.sdis83.remocra.db.model.remocra.tables.pojos.CriseEvenement();
    c.setNom(request.getParameter("nom"));
    c.setDescription(request.getParameter("description"));
    c.setContexte(request.getParameter("contexte"));
    Date constat = df.parse(request.getParameter("constat"));
    Instant t = new Instant(constat);
    c.setConstat(t);
    if(request.getParameter("cloture") != null){
      c.setCloture(new Instant());
    }
    c.setOrigine(request.getParameter("origine"));
    c.setImportance(Integer.valueOf(request.getParameter("importance")));
    c.setTags(request.getParameter("tags"));
    c.setCrise(Long.valueOf(String.valueOf(request.getParameter("crise"))));
    c.setAuteurEvenement(utilisateurService.getCurrentUtilisateur().getId());
    TypeCriseNatureEvenement tcn = context.select().from(TYPE_CRISE_NATURE_EVENEMENT)
        .where(TYPE_CRISE_NATURE_EVENEMENT.ID.eq(Long.valueOf(String.valueOf(request.getParameter("natureEvent")))))
        .fetchInto(TypeCriseNatureEvenement.class).get(0);
    c.setNatureEvenement(tcn.getId());
    Instant redefinition = new Instant();
    c.setRedefinition(redefinition);

    //mise à jour des documents
    context.delete(CRISE_DOCUMENT).where(CRISE_DOCUMENT.EVENEMENT.eq(id)).and(filesToSave.isEmpty() ? DSL.trueCondition() : CRISE_DOCUMENT.DOCUMENT.notIn(filesToSave)).execute();
    this.addEventDocuments(files, c.getCrise(), id);

    //mise à jour des complements
    context.delete(CRISE_EVENEMENT_COMPLEMENT).where(CRISE_EVENEMENT_COMPLEMENT.EVENEMENT.eq(id)).execute();
    this.addComplement(id, request.getParameterMap());

    //mise à jour des interventions
    if(request.getParameter("interventions") != null) {
      List<Integer> interventions = new ArrayList<Integer>();
      interventions = new JSONDeserializer<ArrayList<Integer>>().deserialize(request.getParameter("interventions"));
      context.delete(CRISE_EVENEMENT_INTERVENTION).where(CRISE_EVENEMENT_INTERVENTION.INTERVENTION.eq(id)).execute();
      this.addInterventions(id, interventions);

    }


    if(c.getCloture() != null){
      context.update(CRISE_EVENEMENT).set(row( CRISE_EVENEMENT.NOM, CRISE_EVENEMENT.DESCRIPTION, CRISE_EVENEMENT.CONSTAT, CRISE_EVENEMENT.REDEFINITION,CRISE_EVENEMENT.CLOTURE,
          CRISE_EVENEMENT.ORIGINE, CRISE_EVENEMENT.IMPORTANCE, CRISE_EVENEMENT.TAGS,CRISE_EVENEMENT.CRISE, CRISE_EVENEMENT.NATURE_EVENEMENT,
          CRISE_EVENEMENT.AUTEUR_EVENEMENT, CRISE_EVENEMENT.CONTEXTE)
          ,row(c.getNom(), c.getDescription(),c.getConstat(), c.getRedefinition(), c.getCloture(), c.getOrigine(), c.getImportance(), c.getTags(), c.getCrise(),c.getNatureEvenement(), c.getAuteurEvenement(), c.getContexte())).where(CRISE_EVENEMENT.ID.eq(id)).execute();
    }else {
      context.update(CRISE_EVENEMENT).set(row( CRISE_EVENEMENT.NOM, CRISE_EVENEMENT.DESCRIPTION, CRISE_EVENEMENT.CONSTAT, CRISE_EVENEMENT.REDEFINITION,
          CRISE_EVENEMENT.ORIGINE, CRISE_EVENEMENT.IMPORTANCE, CRISE_EVENEMENT.TAGS,CRISE_EVENEMENT.CRISE, CRISE_EVENEMENT.NATURE_EVENEMENT,
          CRISE_EVENEMENT.AUTEUR_EVENEMENT, CRISE_EVENEMENT.CONTEXTE)
          ,row(c.getNom(), c.getDescription(),c.getConstat(), c.getRedefinition(), c.getOrigine(), c.getImportance(), c.getTags(), c.getCrise(),c.getNatureEvenement(), c.getAuteurEvenement(), c.getContexte())).where(CRISE_EVENEMENT.ID.eq(id)).execute();
    }
    return c;

  }

  public CriseSuivi createMessage(String json) throws ParseException {
    Map<String, Object> criseMessageData = new JSONDeserializer<HashMap<String, Object>>().use(Date.class, RemocraDateHourTransformer.getInstance())
        .use(Geometry.class, new GeometryFactory()).deserialize(json);

    fr.sdis83.remocra.db.model.remocra.tables.pojos.CriseSuivi cs = new CriseSuivi();
    cs.setObjet((String) criseMessageData.get("objet"));
    cs.setMessage((String) criseMessageData.get("message"));
    Date constat = df.parse(String.valueOf(criseMessageData.get("creation")));
    Instant t = new Instant(constat);
    cs.setCreation(t);
    cs.setOrigine((String) criseMessageData.get("origine"));
    cs.setImportance((Integer) criseMessageData.get("importance"));
    cs.setTags((String) criseMessageData.get("tags"));
    cs.setCrise(Long.valueOf(String.valueOf(criseMessageData.get("crise"))));
    cs.setEvenement(Long.valueOf(String.valueOf(criseMessageData.get("evenement"))));

    int result = context.insertInto(CRISE_SUIVI, CRISE_SUIVI.OBJET, CRISE_SUIVI.MESSAGE, CRISE_SUIVI.CREATION,
        CRISE_SUIVI.ORIGINE, CRISE_SUIVI.IMPORTANCE, CRISE_SUIVI.TAGS, CRISE_SUIVI.CRISE, CRISE_SUIVI.EVENEMENT)
        .values(cs.getObjet(), cs.getMessage(),cs.getCreation(),cs.getOrigine(), cs.getImportance(), cs.getTags(), cs.getCrise(),cs.getEvenement()).execute();

    return cs;

  }

  public List<String> getCriseOrigines(Long idCrise, String query){
    Condition c = trueCondition();
    if(query != null) {
       c = and(CRISE_EVENEMENT.ORIGINE.startsWith(query));
    }
    return context.selectDistinct(CRISE_EVENEMENT.ORIGINE).from(CRISE_EVENEMENT).where(CRISE_EVENEMENT.CRISE.eq(idCrise)).and(c).fetchInto(String.class);
  }

  public List<String> getCriseTags(Long idCrise){
    return context.selectDistinct(CRISE_EVENEMENT.TAGS).from(CRISE_EVENEMENT).where(CRISE_EVENEMENT.CRISE.eq(idCrise)).fetchInto(String.class);
  }

  public List<CriseSuivi> getMessageById(Long id){
    return context.select().from(CRISE_SUIVI).where(CRISE_SUIVI.ID.eq(id)).fetchInto(CriseSuivi.class);
  }

  public Condition getAndCondition(List<ItemFilter> itemFilters){
    Condition c = trueCondition();
    Condition co = trueCondition();
    Condition ct = trueCondition();
    Condition cs = trueCondition();
    Condition cp = trueCondition();
    Condition cta = trueCondition();
    Condition ci = trueCondition();
    //Par defaut en mode anticipation on voit tout
    Condition cc = trueCondition();

    int origine = 0;
    int type = 0;
    int statut = 0;
    int periode = 0;
    int importance = 0;
    int tag = 0;
    for(ItemFilter filter: itemFilters){
      if(filter.getFieldName().equals("origine")){
        origine++;
        if(origine != 1){
          co = co.or(CRISE_EVENEMENT.ORIGINE.eq(filter.getValue()));
        }else {
          co= DSL.and(CRISE_EVENEMENT.ORIGINE.eq(filter.getValue()));
        }
      } else if(filter.getFieldName().equals("type")){
        type++;
        if(type != 1){
          ct = ct.or(CRISE_EVENEMENT.NATURE_EVENEMENT.eq(Long.valueOf(filter.getValue())));
        }else {
          ct= DSL.and(CRISE_EVENEMENT.NATURE_EVENEMENT.eq(Long.valueOf(filter.getValue())));
        }
      }else if(filter.getFieldName().equals("statut")){
        statut++;
        if(statut != 1){
          if(filter.getValue().equals("En cours")){
            cs = cs.or(CRISE_EVENEMENT.CLOTURE.isNull());
          }else {
            cs = cs.or(CRISE_EVENEMENT.CLOTURE.isNotNull());
          }
        }else {
          if(filter.getValue().equals("En cours")){
            cs= DSL.and(CRISE_EVENEMENT.CLOTURE.isNull());
          }else {
            cs= DSL.and(CRISE_EVENEMENT.CLOTURE.isNotNull());
          }

        }
      }else if(filter.getFieldName().equals("periode")){
        periode++;
        Instant t = new Instant();
        if(periode != 1){
          if(filter.getValue().equals("<10mn")){
            cp = cp.or(CRISE_EVENEMENT.ID.in(select(CRISE_SUIVI.EVENEMENT).from(CRISE_SUIVI).where(CRISE_SUIVI.CREATION.greaterThan(new Instant().toDateTime().minusMinutes(10).toInstant()))));
          }else if(filter.getValue().equals("<30mn")){
            cp = cp.or(CRISE_EVENEMENT.ID.in(select(CRISE_SUIVI.EVENEMENT).from(CRISE_SUIVI).where(CRISE_SUIVI.CREATION.greaterThan(new Instant().toDateTime().minusMinutes(30).toInstant()))));
          }else if(filter.getValue().equals("<1h")){
            cp = cp.or(CRISE_EVENEMENT.ID.in(select(CRISE_SUIVI.EVENEMENT).from(CRISE_SUIVI).where(CRISE_SUIVI.CREATION.greaterThan(new Instant().toDateTime().minusHours(1).toInstant()))));
          }else if(filter.getValue().equals("<24h")) {
            cp = cp.or(CRISE_EVENEMENT.ID.in(select(CRISE_SUIVI.EVENEMENT).from(CRISE_SUIVI).where(CRISE_SUIVI.CREATION.greaterThan(new Instant().toDateTime().minusHours(24).toInstant()))));
          }
        }else {
          if(filter.getValue().equals("<10mn")){
            cp = DSL.and(CRISE_EVENEMENT.ID.in(select(CRISE_SUIVI.EVENEMENT).from(CRISE_SUIVI).where(CRISE_SUIVI.CREATION.greaterThan(new Instant().toDateTime().minusMinutes(10).toInstant()))));
          }else if(filter.getValue().equals("<30mn")){
            cp = DSL.and(CRISE_EVENEMENT.ID.in(select(CRISE_SUIVI.EVENEMENT).from(CRISE_SUIVI).where(CRISE_SUIVI.CREATION.greaterThan(new Instant().toDateTime().minusMinutes(30).toInstant()))));
          }else if(filter.getValue().equals("<1h")){
            cp = DSL.and(CRISE_EVENEMENT.ID.in(select(CRISE_SUIVI.EVENEMENT).from(CRISE_SUIVI).where(CRISE_SUIVI.CREATION.greaterThan(new Instant().toDateTime().minusHours(1).toInstant()))));
          }else if(filter.getValue().equals("<24h")) {
            cp = DSL.and(CRISE_EVENEMENT.ID.in(select(CRISE_SUIVI.EVENEMENT).from(CRISE_SUIVI).where(CRISE_SUIVI.CREATION.greaterThan(new Instant().toDateTime().minusHours(24).toInstant()))));
          }
        }
      }else if(filter.getFieldName().equals("tag")){
        tag++;
        if(tag != 1){
          cta = cta.or(CRISE_EVENEMENT.TAGS.like("%"+filter.getValue()+"%"));
        }else {
          cta= DSL.and(CRISE_EVENEMENT.TAGS.like("%"+filter.getValue()+"%"));
        }
      }else if(filter.getFieldName().equals("importance")){
        importance++;
        List<Integer> filterValue = new ArrayList<Integer>();
        int value = Integer.valueOf(filter.getValue());
        while (value != 0){
          filterValue.add(value);
          value --;
        }
        if(importance != 1){
          ci = ci.or(CRISE_EVENEMENT.IMPORTANCE.in(filterValue));
        }else {
          ci= DSL.and(CRISE_EVENEMENT.IMPORTANCE.in(filterValue));
        }
      } //on n'applique aucun filtre en mode anticipation
      else if(filter.getFieldName().equals("contexte") && !(("ANTICIPATION").equals(filter.getValue()))){
        cc = DSL.and(CRISE_EVENEMENT.CONTEXTE.like("%"+filter.getValue()+"%"));
      }
    }
    c = c.and(co).and(ct).and(cs).and(cp).and(cta).and(ci).and(cc);
    return c;
  }

  public List<fr.sdis83.remocra.db.model.remocra.tables.pojos.Document> getDocuments(Long eventId){
    List<fr.sdis83.remocra.db.model.remocra.tables.pojos.Document> l;
    l = (context.select().from(DOCUMENT)
        .where(DOCUMENT.ID.in(context.select(Tables.CRISE_DOCUMENT.DOCUMENT)
            .from(Tables.CRISE_DOCUMENT).where(Tables.CRISE_DOCUMENT.EVENEMENT.eq(eventId)).fetchInto(Long.class)))).fetchInto(fr.sdis83.remocra.db.model.remocra.tables.pojos.Document.class);
    return l;
  }

  public int countDocuments(Long eventId){

    return context.fetchCount(context.select().from(DOCUMENT)
        .where(DOCUMENT.ID.in(context.select(Tables.CRISE_DOCUMENT.DOCUMENT)
            .from(Tables.CRISE_DOCUMENT).where(Tables.CRISE_DOCUMENT.EVENEMENT.eq(eventId)).fetchInto(Long.class))));
  }

  public Result<Record> updateGeom(Long id, String wkt, int srid ) {
    Instant redefinition = new Instant();
    Geometry geom = GeometryUtil.toGeometry(wkt,srid);
    Instant t = new Instant();
    String sql = "update remocra.crise_evenement set geometrie = st_geomfromtext('"+geom+"',"+srid+") , redefinition ='"+
        new InstantConverter().to(t) +"' where id ="+id + " and cloture is null returning cloture" ;
    return context.fetch(sql);

   /* context.update(CRISE_EVENEMENT)
        .set(row(CRISE_EVENEMENT.GEOMETRIE, CRISE_EVENEMENT.REDEFINITION)
              ,row( geom != null ? geom :null , redefinition))
        .where(CRISE_EVENEMENT.ID.eq(id))
        .execute();*/
  }

  /**
   * Retourne les identifiants des catégories accessibles au profil de droit.
   *
   * @param profilDroit
   * @return
   */
  public Long[] getCategorieEvenementIdsForProfilDroit(Long profilDroit) {
    return context.select(TYPE_CRISE_EVENEMENT_DROIT.CATEGORIE_EVENEMENT).from(TYPE_CRISE_EVENEMENT_DROIT)
            .where(TYPE_CRISE_EVENEMENT_DROIT.PROFIL_DROIT.eq(profilDroit))
            .fetchArray(TYPE_CRISE_EVENEMENT_DROIT.CATEGORIE_EVENEMENT);
  }

  public List<TypeCriseProprieteEvenement> getProprietes(Long natureId){
    List<TypeCriseProprieteEvenement> l;
    l = context.select().from(TYPE_CRISE_PROPRIETE_EVENEMENT).where(TYPE_CRISE_PROPRIETE_EVENEMENT.NATURE_EVENEMENT.eq(natureId)).fetchInto(TypeCriseProprieteEvenement.class);
    return l;
  }

  public List<RemocraVueCombo> getComboValues(Long id, String pathParam, Integer limit) throws SQLException, ParseException {
    List<RemocraVueCombo> lstResult = new ArrayList<RemocraVueCombo>();
    @SuppressWarnings("unchecked")
    String query = context.select(TYPE_CRISE_PROPRIETE_EVENEMENT.SOURCE_SQL).from(TYPE_CRISE_PROPRIETE_EVENEMENT).where(TYPE_CRISE_PROPRIETE_EVENEMENT.ID.eq(id)).fetchOne(TYPE_CRISE_PROPRIETE_EVENEMENT.SOURCE_SQL);
    String libelle = context.select(TYPE_CRISE_PROPRIETE_EVENEMENT.SOURCE_SQL_LIBELLE).from(TYPE_CRISE_PROPRIETE_EVENEMENT).where(TYPE_CRISE_PROPRIETE_EVENEMENT.ID.eq(id)).fetchOne(TYPE_CRISE_PROPRIETE_EVENEMENT.SOURCE_SQL_LIBELLE);
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
// TODO : factoriser cette partie dans utils
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

  public void addComplement(Long idCriseEvent, Map mapParams){
    for(Object key : mapParams.keySet()){
      String keyStr = (String)key;
      String[] values = (String[])(mapParams.get(keyStr));
      for(String  val : values){
        if(keyStr.contains("input")){
          JSONObject obj = new JSONObject(val);
          String valformatee = obj.get("valeurformatee").toString();
          String valsource = obj.get("valeursource") != null ? obj.get("valeursource").toString() :null;
          Long propriete = Long.valueOf(keyStr.substring(5));
          context.insertInto(CRISE_EVENEMENT_COMPLEMENT, CRISE_EVENEMENT_COMPLEMENT.VALEUR_SOURCE,CRISE_EVENEMENT_COMPLEMENT.VALEUR_FORMATEE,CRISE_EVENEMENT_COMPLEMENT.EVENEMENT,CRISE_EVENEMENT_COMPLEMENT.PROPRIETE_EVENEMENT)
              .values(valsource,valformatee,idCriseEvent,propriete).execute();
        }
      }
    }
  }

  public void addInterventions(Long idCriseEvent, List<Integer> interventions){
    if(interventions.size() != 0){
      for (Integer id : interventions){
        try {
          context.insertInto(CRISE_EVENEMENT_INTERVENTION, CRISE_EVENEMENT_INTERVENTION.CRISE_EVENEMENT, CRISE_EVENEMENT_INTERVENTION.INTERVENTION)
              .values(idCriseEvent, Long.valueOf(id)).execute();
        } catch ( Exception e) {
          e.printStackTrace();
        }
      }
    }

  }
  public void addEventDocuments(Map<String, MultipartFile> files, Long criseId, Long eventId) {
    if (files != null && !files.isEmpty()) {
      for (MultipartFile file : files.values()) {
        if (!file.isEmpty()) {
          try {
            Document d = DocumentUtil.getInstance().createNonPersistedDocument(Document.TypeDocument.CRISE, file, paramConfService.getDossierDocCrise());
            String sousType = DocumentUtil.getInstance().getSousType(file);
            context.insertInto(DOCUMENT, DOCUMENT.CODE, DOCUMENT.DATE_DOC, DOCUMENT.FICHIER, DOCUMENT.REPERTOIRE, DOCUMENT.TYPE)
                .values(d.getCode(), new Instant(d.getDateDoc()), d.getFichier(), d.getRepertoire(), d.getType().toString()).execute();
            Long docId = context.select(DSL.max((DOCUMENT.ID))).from(DOCUMENT).fetchOne().value1();
            context.insertInto(CriseDocument.CRISE_DOCUMENT, CriseDocument.CRISE_DOCUMENT.SOUS_TYPE, CriseDocument.CRISE_DOCUMENT.DOCUMENT, CriseDocument.CRISE_DOCUMENT.CRISE, CriseDocument.CRISE_DOCUMENT.EVENEMENT)
                .values(sousType, docId, criseId, eventId).execute();
          } catch (Exception e) {
            e.printStackTrace();

          }
        }
      }
    }
  }
}
