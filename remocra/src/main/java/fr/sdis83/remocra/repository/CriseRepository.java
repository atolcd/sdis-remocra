package fr.sdis83.remocra.repository;

import static fr.sdis83.remocra.db.model.remocra.Tables.COMMUNE;
import static fr.sdis83.remocra.db.model.remocra.Tables.CRISE;
import static fr.sdis83.remocra.db.model.remocra.Tables.CRISE_COMMUNE;
import static fr.sdis83.remocra.db.model.remocra.Tables.CRISE_DOCUMENT;
import static fr.sdis83.remocra.db.model.remocra.Tables.CRISE_REPERTOIRE_LIEU;
import static fr.sdis83.remocra.db.model.remocra.Tables.OGC_COUCHE;
import static fr.sdis83.remocra.db.model.remocra.Tables.PROCESSUS_ETL_PLANIFICATION;
import static fr.sdis83.remocra.db.model.remocra.Tables.PROCESSUS_ETL_PLANIFICATION_PARAMETRE;
import static fr.sdis83.remocra.db.model.remocra.Tables.REPERTOIRE_LIEU;
import static fr.sdis83.remocra.db.model.remocra.Tables.TYPE_CRISE;
import static fr.sdis83.remocra.db.model.remocra.Tables.TYPE_CRISE_STATUT;
import static fr.sdis83.remocra.db.model.remocra.tables.Document.DOCUMENT;
import static org.jooq.impl.DSL.row;

import com.vividsolutions.jts.geom.Geometry;
import flexjson.JSONDeserializer;
import fr.sdis83.remocra.GlobalConstants;
import fr.sdis83.remocra.db.model.remocra.tables.pojos.OgcCouche;
import fr.sdis83.remocra.db.model.remocra.tables.pojos.ProcessusEtlPlanificationParametre;
import fr.sdis83.remocra.db.model.remocra.tables.pojos.RepertoireLieu;
import fr.sdis83.remocra.domain.remocra.Commune;
import fr.sdis83.remocra.domain.remocra.Document;
import fr.sdis83.remocra.domain.utils.RemocraDateHourTransformer;
import fr.sdis83.remocra.service.ParamConfService;
import fr.sdis83.remocra.service.UtilisateurService;
import fr.sdis83.remocra.util.DocumentUtil;
import fr.sdis83.remocra.web.deserialize.GeometryFactory;
import fr.sdis83.remocra.web.message.ItemFilter;
import fr.sdis83.remocra.web.message.ItemSorting;
import fr.sdis83.remocra.web.model.Crise;
import fr.sdis83.remocra.web.model.ProcessusEtlPlanification;
import fr.sdis83.remocra.web.model.TypeCrise;
import fr.sdis83.remocra.web.model.TypeCriseStatut;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.Order;
import org.apache.log4j.Logger;
import org.joda.time.Instant;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Record1;
import org.jooq.Result;
import org.jooq.SortOrder;
import org.jooq.impl.DSL;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.NameTokenizers;
import org.modelmapper.jooq.RecordValueReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Configuration
public class CriseRepository {
  private final Logger logger = Logger.getLogger(getClass());
  private final DateFormat df = new SimpleDateFormat(RemocraDateHourTransformer.FORMAT);

  @Autowired DSLContext context;

  @Autowired protected ParamConfService paramConfService;

  @Autowired private UtilisateurService utilisateurService;

  @Autowired JpaTransactionManager transactionManager;

  @PersistenceContext private EntityManager entityManager;

  public CriseRepository() {}

  @Bean
  public CriseRepository criseRepository(DSLContext context) {
    return new CriseRepository(context);
  }

  CriseRepository(DSLContext context) {
    this.context = context;
  }

  public List<Crise> getAll(
      List<ItemFilter> itemFilters, Integer limit, Integer start, List<ItemSorting> itemSortings) {
    // dans le cas de fusion on recupère les crises en fonction de type et en eliminant la crise
    // choisis
    Condition condition = this.getFilters(itemFilters);
    ArrayList<javax.persistence.criteria.Order> orders = new ArrayList<Order>();
    // Par défaut on tri par date activation DESC
    String sortField = "activation";
    SortOrder sortOrder = SortOrder.DESC;
    for (ItemSorting itemSorting : itemSortings) {
      sortField = itemSorting.getFieldName();
      sortOrder = itemSorting.isDesc() ? SortOrder.DESC : SortOrder.ASC;
    }

    Result<Record> criseRecord = null;
    List<Crise> cr = new ArrayList<Crise>();
    criseRecord =
        context
            .select(
                CRISE.ID.as("criseId"),
                CRISE.ACTIVATION.as("criseActivation"),
                CRISE.NOM.as("criseNom"),
                CRISE.DESCRIPTION.as("criseDescription"),
                CRISE.CLOTURE.as("criseCloture"),
                CRISE.CARTE_ANT.as("criseCarteAnt"),
                CRISE.CARTE_OP.as("criseCarteOp"))
            .select(
                TYPE_CRISE.ID.as("typeCriseId"),
                TYPE_CRISE.ACTIF.as("typeCriseActif"),
                TYPE_CRISE.NOM.as("typeCriseNom"),
                TYPE_CRISE.CODE.as("typeCriseCode"))
            .select(
                TYPE_CRISE_STATUT.ID.as("typeCriseStatutId"),
                TYPE_CRISE_STATUT.ACTIF.as("typeCriseStatutActif"),
                TYPE_CRISE_STATUT.NOM.as("typeCriseStatutNom"),
                TYPE_CRISE_STATUT.CODE.as("typeCriseStatutCode"))
            .from(CRISE)
            .join(TYPE_CRISE)
            .on(TYPE_CRISE.ID.eq(CRISE.TYPE_CRISE))
            .join(TYPE_CRISE_STATUT)
            .on(TYPE_CRISE_STATUT.ID.eq(CRISE.STATUT))
            .where(condition)
            .orderBy(CRISE.field(DSL.field(sortField)).sort(sortOrder))
            .limit(limit)
            .offset(start)
            .fetch();

    ModelMapper modelMapper = new ModelMapper();
    modelMapper.getConfiguration().addValueReader(new RecordValueReader());
    modelMapper.getConfiguration().setSourceNameTokenizer(NameTokenizers.CAMEL_CASE);

    for (Record r : criseRecord) {
      Crise crise = modelMapper.map(r, Crise.class);
      // On recupere la liste des communes
      List<Commune> c =
          context
              .select()
              .from(COMMUNE)
              .where(
                  COMMUNE.ID.in(
                      context
                          .select(CRISE_COMMUNE.COMMUNE)
                          .from(CRISE_COMMUNE)
                          .where(CRISE_COMMUNE.CRISE.eq(crise.getId()))
                          .fetchInto(Long.class)))
              .fetchInto(Commune.class);
      crise.setCommunes(c);
      cr.add(crise);
    }
    return cr;
  }

  public String getExtentById(Long id) {
    String extent = null;
    String sql =
        "SELECT St_AsEwkt(St_transform(St_SetSrid(CAST(St_Extent(geometrie) AS Geometry),"
            + GlobalConstants.SRID_2154
            + "),3857)) AS geometrie FROM remocra.commune WHERE id IN(SELECT commune FROM remocra.crise_commune WHERE crise ="
            + id
            + ")";
    Result<Record> result = context.fetch(sql);
    extent = String.valueOf(result.getValue(0, "geometrie"));
    return extent;
  }

  public String getActivationById(Long id) {
    String activation = null;
    Result<Record1<Instant>> result =
        context.select(CRISE.ACTIVATION).from(CRISE).where(CRISE.ID.eq(id)).fetch();
    activation = String.valueOf(result.getValue(0, "activation"));
    return activation;
  }

  public Crise getCriseById(Long id) {

    Record criseRecord =
        context
            .select(
                CRISE.ID.as("criseId"),
                CRISE.ACTIVATION.as("criseActivation"),
                CRISE.NOM.as("criseNom"),
                CRISE.DESCRIPTION.as("criseDescription"),
                CRISE.CLOTURE.as("criseCloture"),
                CRISE.CARTE_ANT.as("criseCarteAnt"),
                CRISE.CARTE_OP.as("criseCarteOp"))
            .select(
                TYPE_CRISE.ID.as("typeCriseId"),
                TYPE_CRISE.ACTIF.as("typeCriseActif"),
                TYPE_CRISE.NOM.as("typeCriseNom"),
                TYPE_CRISE.CODE.as("typeCriseCode"))
            .select(
                TYPE_CRISE_STATUT.ID.as("typeCriseStatutId"),
                TYPE_CRISE_STATUT.ACTIF.as("typeCriseStatutActif"),
                TYPE_CRISE_STATUT.NOM.as("typeCriseStatutNom"),
                TYPE_CRISE_STATUT.CODE.as("typeCriseStatutCode"))
            .from(CRISE)
            .join(TYPE_CRISE)
            .on(TYPE_CRISE.ID.eq(CRISE.TYPE_CRISE))
            .join(TYPE_CRISE_STATUT)
            .on(TYPE_CRISE_STATUT.ID.eq(CRISE.STATUT))
            .where(CRISE.ID.eq(id))
            .fetchOne();

    ModelMapper modelMapper = new ModelMapper();
    modelMapper.getConfiguration().addValueReader(new RecordValueReader());
    modelMapper.getConfiguration().setSourceNameTokenizer(NameTokenizers.CAMEL_CASE);

    Crise crise = modelMapper.map(criseRecord, Crise.class);
    // On recupere la liste des communes
    List<Commune> c =
        context
            .select()
            .from(COMMUNE)
            .where(
                COMMUNE.ID.in(
                    context
                        .select(CRISE_COMMUNE.COMMUNE)
                        .from(CRISE_COMMUNE)
                        .where(CRISE_COMMUNE.CRISE.eq(crise.getId()))
                        .fetchInto(Long.class)))
            .fetchInto(Commune.class);
    crise.setCommunes(c);
    // On recupere la liste des répertoires des lieux
    List<RepertoireLieu> lieux =
        context
            .select()
            .from(REPERTOIRE_LIEU)
            .where(
                REPERTOIRE_LIEU.ID.in(
                    context
                        .select(CRISE_REPERTOIRE_LIEU.REPERTOIRE_LIEU)
                        .from(CRISE_REPERTOIRE_LIEU)
                        .where(CRISE_REPERTOIRE_LIEU.CRISE.eq(crise.getId()))
                        .fetchInto(Long.class)))
            .fetchInto(RepertoireLieu.class);
    crise.setRepertoireLieus(lieux);

    // On recupere la liste des processus activés
    List<ProcessusEtlPlanification> processus =
        context
            .select()
            .from(PROCESSUS_ETL_PLANIFICATION)
            .where(
                PROCESSUS_ETL_PLANIFICATION
                    .OBJET_CONCERNE
                    .eq(crise.getId())
                    .and(PROCESSUS_ETL_PLANIFICATION.CATEGORIE.eq("GESTION_CRISE")))
            .fetchInto(ProcessusEtlPlanification.class);
    for (ProcessusEtlPlanification processEtlPlanif : processus) {
      List<ProcessusEtlPlanificationParametre> processusEtlPlanificationParametres =
          context
              .select()
              .from(PROCESSUS_ETL_PLANIFICATION_PARAMETRE)
              .where(
                  PROCESSUS_ETL_PLANIFICATION_PARAMETRE.PROCESSUS_ETL_PLANIFICATION.eq(
                      processEtlPlanif.getId().longValue()))
              .fetchInto(ProcessusEtlPlanificationParametre.class);
      processEtlPlanif.setProcessusEtlPlanificationParametres(processusEtlPlanificationParametres);
    }
    crise.setProcessusEtlPlanifications(processus);

    // on parse le string carteOp en json, on récupère les couches en fonction des codes
    if (crise.getCarteOp() != null) {
      List<HashMap<String, Object>> couchesOp =
          new JSONDeserializer<List<HashMap<String, Object>>>().deserialize(crise.getCarteOp());
      List<String> codesOp = new ArrayList<String>();
      for (HashMap<String, Object> couche : couchesOp) {
        codesOp.add(String.valueOf(couche.get("id")));
      }

      List<OgcCouche> criseCouchesOp =
          context
              .select()
              .from(OGC_COUCHE)
              .where(OGC_COUCHE.CODE.in(codesOp))
              .fetchInto(OgcCouche.class);
      crise.setOgcCouchesOp(criseCouchesOp);
    }

    // on parse le string carteAnt en json, on récupère les couches en fonction des codes
    if (crise.getCarteAnt() != null) {
      List<HashMap<String, Object>> couchesAnt =
          new JSONDeserializer<List<HashMap<String, Object>>>().deserialize(crise.getCarteAnt());
      List<String> codesAnt = new ArrayList<String>();
      for (HashMap<String, Object> couche : couchesAnt) {
        codesAnt.add(String.valueOf(couche.get("id")));
      }

      List<OgcCouche> criseCouchesAnt =
          context
              .select()
              .from(OGC_COUCHE)
              .where(OGC_COUCHE.CODE.in(codesAnt))
              .fetchInto(OgcCouche.class);
      crise.setOgcCouchesAnt(criseCouchesAnt);
    }

    return crise;
  }

  @Transactional
  public int count(List<ItemFilter> itemFilters) {
    Condition condition = this.getFilters(itemFilters);
    return context.fetchCount(
        context
            .select()
            .from(CRISE)
            .join(TYPE_CRISE)
            .on(TYPE_CRISE.ID.eq(CRISE.TYPE_CRISE))
            .join(TYPE_CRISE_STATUT)
            .on(TYPE_CRISE_STATUT.ID.eq(CRISE.STATUT))
            .where(condition));
  }

  public fr.sdis83.remocra.db.model.remocra.tables.pojos.Crise create(String json)
      throws ParseException {
    HashMap<String, Object> items =
        new JSONDeserializer<HashMap<String, Object>>()
            .use("crise", Crise.class)
            .use(Date.class, RemocraDateHourTransformer.getInstance())
            .use(Geometry.class, new GeometryFactory())
            .deserialize(json);

    Map<String, Object> criseData = (Map<String, Object>) items.get("crise");
    List<Integer> idCommunes = (List<Integer>) items.get("communes");
    List<Integer> idRepertoires = (List<Integer>) items.get("repertoires");
    List<Map<String, Object>> processusPlanif =
        (List<Map<String, Object>>) items.get("processusPlanif");
    fr.sdis83.remocra.db.model.remocra.tables.pojos.Crise c =
        new fr.sdis83.remocra.db.model.remocra.tables.pojos.Crise();
    c.setNom((String) criseData.get("nom"));
    c.setDescription((String) criseData.get("description"));
    Date dateActivation = df.parse(String.valueOf(criseData.get("activation")));
    Instant t = new Instant(dateActivation);
    c.setActivation(t);
    c.setCarteOp("[" + criseData.get("carteOp") + "]");
    c.setCarteAnt("[" + criseData.get("carteAnt") + "]");
    TypeCrise tc =
        context
            .select()
            .from(TYPE_CRISE)
            .where(TYPE_CRISE.ID.eq(Long.valueOf(String.valueOf(items.get("typeCrise")))))
            .fetchInto(TypeCrise.class)
            .get(0);
    TypeCriseStatut tcs =
        context
            .select()
            .from(TYPE_CRISE_STATUT)
            .where(TYPE_CRISE_STATUT.CODE.eq("EN_COURS"))
            .fetchInto(TypeCriseStatut.class)
            .get(0);
    c.setTypeCrise(tc.getId());
    c.setStatut(tcs.getId());
    c.setAuteurCrise(utilisateurService.getCurrentUtilisateur().getId());
    int result =
        context
            .insertInto(
                CRISE,
                CRISE.NOM,
                CRISE.TYPE_CRISE,
                CRISE.DESCRIPTION,
                CRISE.ACTIVATION,
                CRISE.STATUT,
                CRISE.CARTE_ANT,
                CRISE.CARTE_OP,
                CRISE.AUTEUR_CRISE)
            .values(
                c.getNom(),
                c.getTypeCrise(),
                c.getDescription(),
                c.getActivation(),
                c.getStatut(),
                c.getCarteAnt(),
                c.getCarteOp(),
                c.getAuteurCrise())
            .execute();
    if (result != 0) {
      Long idCrise = context.select(DSL.max((CRISE.ID))).from(CRISE).fetchOne().value1();
      if (idCommunes.size() != 0) {
        for (Integer idCommune : idCommunes) {
          int insertCommunes =
              context
                  .insertInto(CRISE_COMMUNE, CRISE_COMMUNE.CRISE, CRISE_COMMUNE.COMMUNE)
                  .values(idCrise, Long.valueOf(idCommune))
                  .execute();
        }
      }
      if (processusPlanif.size() != 0) {
        System.out.println(processusPlanif);
        for (Map<String, Object> processPlanif : processusPlanif) {
          List<Map<String, Object>> parametres =
              (List<Map<String, Object>>) processPlanif.get("parametres");
          // Pas de paramètres on insère la plannification
          int insertProcessPlanif =
              context
                  .insertInto(
                      PROCESSUS_ETL_PLANIFICATION,
                      PROCESSUS_ETL_PLANIFICATION.MODELE,
                      PROCESSUS_ETL_PLANIFICATION.EXPRESSION,
                      PROCESSUS_ETL_PLANIFICATION.CATEGORIE,
                      PROCESSUS_ETL_PLANIFICATION.OBJET_CONCERNE)
                  .values(
                      Long.valueOf(String.valueOf(processPlanif.get("idModele"))),
                      String.valueOf("0 0/" + processPlanif.get("expressionCron") + " * ? * * *"),
                      "GESTION_CRISE",
                      idCrise)
                  .execute();

          // Si on a plusieur parametres on fait deja une insertion dans la table planif on récupere
          // l'id de planif et on rajoute les parametres
          if (parametres.size() != 0) {
            Long idProcessPlanif =
                context
                    .select(DSL.max((PROCESSUS_ETL_PLANIFICATION.ID)))
                    .from(PROCESSUS_ETL_PLANIFICATION)
                    .fetchOne()
                    .value1();
            for (Map<String, Object> parametre : parametres) {
              int insertProcessPlanifParametre =
                  context
                      .insertInto(
                          PROCESSUS_ETL_PLANIFICATION_PARAMETRE,
                          PROCESSUS_ETL_PLANIFICATION_PARAMETRE.PROCESSUS_ETL_PLANIFICATION,
                          PROCESSUS_ETL_PLANIFICATION_PARAMETRE.PARAMETRE,
                          PROCESSUS_ETL_PLANIFICATION_PARAMETRE.VALEUR)
                      .values(
                          idProcessPlanif,
                          Long.valueOf(String.valueOf(parametre.get("parametre"))),
                          String.valueOf(parametre.get("valeur")))
                      .execute();
            }
          }
        }
      }
      if (idRepertoires.size() != 0) {
        for (Integer idRepertoire : idRepertoires) {
          int insertRepertoires =
              context
                  .insertInto(
                      CRISE_REPERTOIRE_LIEU,
                      CRISE_REPERTOIRE_LIEU.CRISE,
                      CRISE_REPERTOIRE_LIEU.REPERTOIRE_LIEU)
                  .values(idCrise, Long.valueOf(idRepertoire))
                  .execute();
        }
      }

      // on fait un update pour déclencher le trigger et remplir la table crise suivi
      context.update(CRISE).set(CRISE.NOM, c.getNom()).where(CRISE.ID.eq(idCrise)).execute();
    }

    return c;
  }

  public fr.sdis83.remocra.db.model.remocra.tables.pojos.Crise update(Long id, String json)
      throws ParseException, SQLException {
    HashMap<String, Object> items =
        new JSONDeserializer<HashMap<String, Object>>()
            .use("crise", Crise.class)
            .use(Date.class, RemocraDateHourTransformer.getInstance())
            .use(Geometry.class, new GeometryFactory())
            .deserialize(json);

    Map<String, Object> criseData = (Map<String, Object>) items.get("crise");
    List<Integer> idCommunes = (List<Integer>) items.get("communes");
    List<Integer> idRepertoires = (List<Integer>) items.get("repertoires");
    List<Map<String, Object>> processusPlanif =
        (List<Map<String, Object>>) items.get("processusPlanif");
    fr.sdis83.remocra.db.model.remocra.tables.pojos.Crise c =
        new fr.sdis83.remocra.db.model.remocra.tables.pojos.Crise();
    c.setNom((String) criseData.get("nom"));
    c.setDescription((String) criseData.get("description"));
    Date dateActivation = df.parse(String.valueOf(criseData.get("activation")));
    Instant tActivation = new Instant(dateActivation);
    c.setActivation(tActivation);

    if (criseData.get("cloture") != null) {
      Date dateCloture = df.parse(String.valueOf(criseData.get("cloture")));
      Instant tCloture = new Instant(dateCloture);
      c.setCloture(tCloture);
    }
    c.setCarteOp("[" + criseData.get("carteOp") + "]");
    c.setCarteAnt("[" + criseData.get("carteAnt") + "]");
    TypeCrise tc =
        context
            .select()
            .from(TYPE_CRISE)
            .where(TYPE_CRISE.ID.eq(Long.valueOf(String.valueOf(items.get("typeCrise")))))
            .fetchInto(TypeCrise.class)
            .get(0);

    String codeStatut = c.getCloture() != null ? "TERMINE" : "EN_COURS";
    TypeCriseStatut tcs =
        context
            .select()
            .from(TYPE_CRISE_STATUT)
            .where(TYPE_CRISE_STATUT.CODE.eq(codeStatut))
            .fetchInto(TypeCriseStatut.class)
            .get(0);

    c.setTypeCrise(tc.getId());
    c.setStatut(tcs.getId());
    c.setStatut(tcs.getId());
    c.setAuteurCrise(utilisateurService.getCurrentUtilisateur().getId());
    int result = 0;
    if (c.getCloture() != null) {
      result =
          context
              .update(CRISE)
              .set(
                  row(
                      CRISE.NOM,
                      CRISE.TYPE_CRISE,
                      CRISE.DESCRIPTION,
                      CRISE.ACTIVATION,
                      CRISE.CLOTURE,
                      CRISE.STATUT,
                      CRISE.CARTE_ANT,
                      CRISE.CARTE_OP,
                      CRISE.AUTEUR_CRISE),
                  row(
                      c.getNom(),
                      c.getTypeCrise(),
                      c.getDescription(),
                      c.getActivation(),
                      c.getCloture(),
                      c.getStatut(),
                      c.getCarteAnt(),
                      c.getCarteOp(),
                      c.getAuteurCrise()))
              .where(CRISE.ID.eq(id))
              .execute();
    } else {
      result =
          context
              .update(CRISE)
              .set(
                  row(
                      CRISE.NOM,
                      CRISE.TYPE_CRISE,
                      CRISE.DESCRIPTION,
                      CRISE.ACTIVATION,
                      CRISE.STATUT,
                      CRISE.CARTE_ANT,
                      CRISE.CARTE_OP,
                      CRISE.AUTEUR_CRISE),
                  row(
                      c.getNom(),
                      c.getTypeCrise(),
                      c.getDescription(),
                      c.getActivation(),
                      c.getStatut(),
                      c.getCarteAnt(),
                      c.getCarteOp(),
                      c.getAuteurCrise()))
              .where(CRISE.ID.eq(id))
              .execute();
    }

    if (result != 0) {
      List<Commune> originCommunes =
          context
              .select()
              .from(COMMUNE)
              .where(
                  COMMUNE.ID.in(
                      context
                          .select(CRISE_COMMUNE.COMMUNE)
                          .from(CRISE_COMMUNE)
                          .where(CRISE_COMMUNE.CRISE.eq(id))
                          .fetchInto(Long.class)))
              .fetchInto(Commune.class);
      // Si le nombre de commune change === redefinition de territoire
      Instant redefinition = null;
      if (idCommunes.size() != originCommunes.size()) {
        redefinition = new Instant();
      } else {
        for (Integer idCommune : idCommunes) {
          boolean toAdd = true;
          for (Commune originCommune : originCommunes) {
            Long idOriginCommune = originCommune.getId();
            if (idCommune.longValue() == idOriginCommune.longValue()) {
              toAdd = false;
            }
          }
          if (toAdd) {
            // Si on ajoute des communes on change la date de redefinition
            redefinition = new Instant();
          }
        }
      }
      int deleteCommunes =
          context.deleteFrom(CRISE_COMMUNE).where(CRISE_COMMUNE.CRISE.eq(id)).execute();
      for (Integer idCommune : idCommunes) {
        int insertCommunes =
            context
                .insertInto(CRISE_COMMUNE, CRISE_COMMUNE.CRISE, CRISE_COMMUNE.COMMUNE)
                .values(id, Long.valueOf(idCommune))
                .execute();
      }
      if (redefinition != null) {
        context
            .update(CRISE)
            .set(CRISE.REDEFINITION, redefinition)
            .where(CRISE.ID.eq(id))
            .execute();
      }

      int deleteProcessPlanif =
          context
              .deleteFrom(PROCESSUS_ETL_PLANIFICATION)
              .where(PROCESSUS_ETL_PLANIFICATION.OBJET_CONCERNE.eq(id))
              .execute();
      // si la crise est en cours on enregistre les plannifs
      if (c.getCloture() == null) {
        for (Map<String, Object> processPlanif : processusPlanif) {
          System.out.println(processusPlanif);

          List<Map<String, Object>> parametres =
              (List<Map<String, Object>>) processPlanif.get("parametres");

          int insertProcessPlanif =
              context
                  .insertInto(
                      PROCESSUS_ETL_PLANIFICATION,
                      PROCESSUS_ETL_PLANIFICATION.MODELE,
                      PROCESSUS_ETL_PLANIFICATION.EXPRESSION,
                      PROCESSUS_ETL_PLANIFICATION.CATEGORIE,
                      PROCESSUS_ETL_PLANIFICATION.OBJET_CONCERNE)
                  .values(
                      Long.valueOf(String.valueOf(processPlanif.get("idModele"))),
                      String.valueOf("0 0/" + processPlanif.get("expressionCron") + " * ? * * *"),
                      "GESTION_CRISE",
                      id)
                  .execute();
          if (parametres.size() != 0) {
            // Si on a plusieur parametres récupere l'id de planif et on rajoute les parametres
            Long idProcessPlanif =
                context
                    .select(DSL.max((PROCESSUS_ETL_PLANIFICATION.ID)))
                    .from(PROCESSUS_ETL_PLANIFICATION)
                    .fetchOne()
                    .value1();
            for (Map<String, Object> parametre : parametres) {
              int insertProcessPlanifParametre =
                  context
                      .insertInto(
                          PROCESSUS_ETL_PLANIFICATION_PARAMETRE,
                          PROCESSUS_ETL_PLANIFICATION_PARAMETRE.PROCESSUS_ETL_PLANIFICATION,
                          PROCESSUS_ETL_PLANIFICATION_PARAMETRE.PARAMETRE,
                          PROCESSUS_ETL_PLANIFICATION_PARAMETRE.VALEUR)
                      .values(
                          idProcessPlanif,
                          Long.valueOf(String.valueOf(parametre.get("parametre"))),
                          String.valueOf(parametre.get("valeur")))
                      .execute();
            }
          }
        }
      }

      int deleteRepertoires =
          context
              .deleteFrom(CRISE_REPERTOIRE_LIEU)
              .where(CRISE_REPERTOIRE_LIEU.CRISE.eq(id))
              .execute();
      for (Integer idRepertoire : idRepertoires) {
        int insertRepertoires =
            context
                .insertInto(
                    CRISE_REPERTOIRE_LIEU,
                    CRISE_REPERTOIRE_LIEU.CRISE,
                    CRISE_REPERTOIRE_LIEU.REPERTOIRE_LIEU)
                .values(id, Long.valueOf(idRepertoire))
                .execute();
      }
    }
    return c;
  }

  public int fusion(Long id, List<Long> idFusionnedCrises, Date dateFusion)
      throws ParseException, SQLException {
    Instant tRedefinition = null;
    List<Commune> fusionnedCommunes =
        context
            .select()
            .from(COMMUNE)
            .where(
                COMMUNE.ID.in(
                    context
                        .select(CRISE_COMMUNE.COMMUNE)
                        .from(CRISE_COMMUNE)
                        .where(CRISE_COMMUNE.CRISE.in(idFusionnedCrises))
                        .fetchInto(Long.class)))
            .fetchInto(Commune.class);
    List<Commune> originCommunes =
        context
            .select()
            .from(COMMUNE)
            .where(
                COMMUNE.ID.in(
                    context
                        .select(CRISE_COMMUNE.COMMUNE)
                        .from(CRISE_COMMUNE)
                        .where(CRISE_COMMUNE.CRISE.eq(id))
                        .fetchInto(Long.class)))
            .fetchInto(Commune.class);
    // on rajoute la liste des communes à la crise conservé
    // on duplicateKey ne marche pas on le fait à la main
    List<Commune> toFusion = new ArrayList<Commune>();
    if (originCommunes.size() != 0) {
      for (Commune fusionnedCommune : fusionnedCommunes) {
        boolean toAdd = true;
        for (Commune originCommune : originCommunes) {
          Long idOriginCommune = originCommune.getId();
          Long idFusionnedCommune = fusionnedCommune.getId();
          if (idFusionnedCommune.longValue() == idOriginCommune.longValue()) {
            toAdd = false;
          }
        }
        if (toAdd) {
          toFusion.add(fusionnedCommune);
          // Si on ajoute des communes on change la date de redefinition
          tRedefinition = new Instant(dateFusion);
        }
      }
      for (Commune fusionnedCommune : toFusion) {
        Long idFusionnedCommune = Long.valueOf(fusionnedCommune.getId());
        int insertCommunes =
            context
                .insertInto(CRISE_COMMUNE, CRISE_COMMUNE.CRISE, CRISE_COMMUNE.COMMUNE)
                .values(id, idFusionnedCommune)
                .execute();
      }
    } else {
      for (Commune fusionnedCommune : fusionnedCommunes) {
        Long idFusionnedCommune = Long.valueOf(fusionnedCommune.getId());
        int insertCommunes =
            context
                .insertInto(CRISE_COMMUNE, CRISE_COMMUNE.CRISE, CRISE_COMMUNE.COMMUNE)
                .values(id, idFusionnedCommune)
                .execute();
      }
    }
    if (tRedefinition != null) {
      context
          .update(CRISE)
          .set(
              row(CRISE.REDEFINITION, CRISE.AUTEUR_CRISE),
              row(tRedefinition, utilisateurService.getCurrentUtilisateur().getId()))
          .where(CRISE.ID.in(id))
          .execute();
    }
    // on passe tout les crises fusionnée en statut fusionne
    Instant tFusion = new Instant(dateFusion);
    TypeCriseStatut tcs =
        context
            .select()
            .from(TYPE_CRISE_STATUT)
            .where(TYPE_CRISE_STATUT.CODE.eq("FUSIONNE"))
            .fetchInto(TypeCriseStatut.class)
            .get(0);
    int update =
        context
            .update(CRISE)
            .set(
                row(CRISE.CLOTURE, CRISE.STATUT, CRISE.CRISE_PARENTE, CRISE.AUTEUR_CRISE),
                row(tFusion, tcs.getId(), id, utilisateurService.getCurrentUtilisateur().getId()))
            .where(CRISE.ID.in(idFusionnedCrises))
            .execute();

    return update;
  }

  public Condition getFilters(List<ItemFilter> itemFilters) {
    ItemFilter type = ItemFilter.getFilter(itemFilters, "typeCrise");
    ItemFilter statut = ItemFilter.getFilter(itemFilters, "typeCriseStatut");
    ItemFilter criseId = ItemFilter.getFilter(itemFilters, "idCrise");
    // dans le cas de fusion on recuperer les crises en fonction de type et en eliminant la crise
    // choisis
    Condition condition = DSL.trueCondition();
    if (type != null) {
      condition = condition.and(CRISE.TYPE_CRISE.eq(Long.valueOf(type.getValue())));
    }
    if (statut != null) {
      condition = condition.and(CRISE.STATUT.eq(Long.valueOf(statut.getValue())));
    }
    if (type != null && statut != null) {
      condition =
          condition.and(
              CRISE
                  .TYPE_CRISE
                  .eq(Long.valueOf(type.getValue()))
                  .and(CRISE.STATUT.eq(Long.valueOf(statut.getValue()))));
    }
    if (type != null && criseId != null) {
      condition =
          condition.and(
              CRISE
                  .ID
                  .isDistinctFrom(Long.valueOf(criseId.getValue()))
                  .and(TYPE_CRISE.ID.eq(Long.valueOf(type.getValue())))
                  .and(TYPE_CRISE_STATUT.CODE.eq("EN_COURS")));
    }
    return condition;
  }

  //
  public int addDocuments(Map<String, MultipartFile> files, Long criseId, Geometry geometrie) {
    if (files != null && !files.isEmpty()) {
      for (MultipartFile file : files.values()) {
        if (!file.isEmpty()) {
          try {
            Document d =
                DocumentUtil.getInstance()
                    .createNonPersistedDocument(
                        Document.TypeDocument.CRISE, file, paramConfService.getDossierDocCrise());
            String sousType = DocumentUtil.getInstance().getSousType(file);
            context
                .insertInto(
                    DOCUMENT,
                    DOCUMENT.CODE,
                    DOCUMENT.DATE_DOC,
                    DOCUMENT.FICHIER,
                    DOCUMENT.REPERTOIRE,
                    DOCUMENT.TYPE)
                .values(
                    d.getCode(),
                    new Instant(d.getDateDoc()),
                    d.getFichier(),
                    d.getRepertoire(),
                    d.getType().toString())
                .execute();
            Long docId = context.select(DSL.max((DOCUMENT.ID))).from(DOCUMENT).fetchOne().value1();
            context
                .insertInto(
                    CRISE_DOCUMENT,
                    CRISE_DOCUMENT.SOUS_TYPE,
                    CRISE_DOCUMENT.DOCUMENT,
                    CRISE_DOCUMENT.CRISE,
                    CRISE_DOCUMENT.GEOMETRIE)
                .values(sousType, docId, criseId, geometrie != null ? geometrie : null)
                .execute();
          } catch (Exception e) {
            e.printStackTrace();
            return 0;
          }
        }
      }
      return 1;
    }
    return 0;
  }

  public List<fr.sdis83.remocra.web.model.CriseDocument> getDocuments(Long criseId)
      throws ParseException {
    /*l =context.select().from(CRISE_DOCUMENT).join(DOCUMENT).on(CRISE_DOCUMENT.DOCUMENT.eq(DOCUMENT.ID))
    .where(CRISE_DOCUMENT.CRISE.eq(criseId)).fetchInto(fr.sdis83.remocra.web.model.CriseDocument.class);*/

    List<fr.sdis83.remocra.web.model.CriseDocument> crDocuments =
        new ArrayList<fr.sdis83.remocra.web.model.CriseDocument>();
    String sql =
        "select  cd.crise, cd.sous_type, cd.document, st_asgeojson(cd.geometrie) as geometrie, d.fichier, d.date, d.date_doc, d.code from remocra.crise_document as cd join remocra.document as d on cd.document = d.id where cd.crise ="
            + criseId;
    Result<Record> result = context.fetch(sql);

    for (Record r : result) {
      fr.sdis83.remocra.web.model.CriseDocument doc =
          new fr.sdis83.remocra.web.model.CriseDocument();
      doc.setCode(String.valueOf(r.getValue("code")));
      doc.setCrise(Long.valueOf(String.valueOf(r.getValue("crise"))));
      doc.setDate(new Instant(r.getValue("date")));
      doc.setFichier(String.valueOf(r.getValue("fichier")));
      doc.setSousType(String.valueOf("sous_type"));
      if (r.getValue("geometrie") != null) {
        doc.setGeometrie(String.valueOf(r.getValue("geometrie")));
      }

      crDocuments.add(doc);
    }

    return crDocuments;
  }

  public int countDocuments(Long criseId) {

    return context.fetchCount(
        context
            .select()
            .from(CRISE_DOCUMENT)
            .join(DOCUMENT)
            .on(CRISE_DOCUMENT.DOCUMENT.eq(DOCUMENT.ID))
            .where(CRISE_DOCUMENT.CRISE.eq(criseId)));
  }

  /**
   * Retourne le nombre de crises accessibles, filtrées à partir de la liste d'identifiants fournis.
   *
   * @param criseIds
   * @return
   */
  public Long countAccessiblesCrisesIn(Long[] criseIds) {
    Long zcId =
        utilisateurService.getCurrentUtilisateur().getOrganisme().getZoneCompetence().getId();
    StringBuffer sbReq =
        new StringBuffer("select count(distinct cr.id) as c")
            .append(" from remocra.crise cr")
            .append(" join remocra.crise_commune crco on (crco.crise=cr.id)")
            .append(" join remocra.commune co on (co.id=crco.commune)")
            .append(" where")
            .append(" co.geometrie && (select geometrie from remocra.zone_competence where id=")
            .append(zcId)
            .append(")")
            .append(" and st_intersects(co.geometrie, ")
            .append("(select geometrie from remocra.zone_competence where id=")
            .append(zcId)
            .append(")")
            .append(")")
            .append(" and cr.id in ")
            .append(toParenthesisArray(criseIds));
    Record rec = context.fetchOne(sbReq.toString());
    return (Long) rec.getValue("c");
  }

  public String toParenthesisArray(Long[] criseIds) {
    StringBuffer sbIds = new StringBuffer("(");
    for (Long l : criseIds) {
      if (sbIds.length() > 1) {
        sbIds.append(",");
      }
      sbIds.append(l);
    }
    sbIds.append(")");
    return sbIds.toString();
  }
}
