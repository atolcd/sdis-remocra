package fr.sdis83.remocra.repository;

import static fr.sdis83.remocra.db.model.couverture_hydraulique.Tables.RESEAU;
import static fr.sdis83.remocra.db.model.remocra.Tables.ETUDE;
import static fr.sdis83.remocra.db.model.remocra.Tables.ETUDE_COMMUNES;
import static fr.sdis83.remocra.db.model.remocra.Tables.ETUDE_DOCUMENTS;
import static fr.sdis83.remocra.db.model.remocra.Tables.TYPE_ETUDE;
import static fr.sdis83.remocra.db.model.remocra.Tables.TYPE_ETUDE_STATUT;
import static fr.sdis83.remocra.db.model.remocra.tables.Commune.COMMUNE;
import static fr.sdis83.remocra.db.model.remocra.tables.Document.DOCUMENT;

import flexjson.JSONDeserializer;
import fr.sdis83.remocra.GlobalConstants;
import fr.sdis83.remocra.db.model.remocra.tables.pojos.Commune;
import fr.sdis83.remocra.domain.remocra.Document;
import fr.sdis83.remocra.domain.remocra.Organisme;
import fr.sdis83.remocra.domain.utils.RemocraDateHourTransformer;
import fr.sdis83.remocra.service.UtilisateurService;
import fr.sdis83.remocra.usecase.etude.DocumentEtudeData;
import fr.sdis83.remocra.usecase.etude.EtudeData;
import fr.sdis83.remocra.usecase.parametre.ParametreDataProvider;
import fr.sdis83.remocra.util.DocumentUtil;
import fr.sdis83.remocra.web.message.ItemFilter;
import fr.sdis83.remocra.web.message.ItemSorting;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.apache.log4j.Logger;
import org.joda.time.Instant;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.SortOrder;
import org.jooq.impl.DSL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.web.multipart.MultipartFile;

@Configuration
public class EtudeRepository {
  private final Logger logger = Logger.getLogger(getClass());
  private final DateFormat df = new SimpleDateFormat(RemocraDateHourTransformer.FORMAT);

  @Autowired DSLContext context;

  @Autowired JpaTransactionManager transactionManager;

  @PersistenceContext private EntityManager entityManager;

  @Autowired protected ParametreDataProvider parametreProvider;

  @Autowired private UtilisateurService utilisateurService;

  public EtudeRepository() {}

  @Bean
  public EtudeRepository EtudeRepository(DSLContext context) {
    return new EtudeRepository(context);
  }

  EtudeRepository(DSLContext context) {
    this.context = context;
  }

  public Collection<EtudeData> getAll(
      Integer limit, Integer start, ItemSorting itemSortings, Condition condition) {
    return context
        .select(
            ETUDE.ID,
            ETUDE.NOM,
            ETUDE.NUMERO,
            ETUDE.DESCRIPTION,
            ETUDE.DATE_MAJ.as("instantMaj"),
            ETUDE.ORGANISME,
            TYPE_ETUDE.ID.as("type.id"),
            TYPE_ETUDE.NOM.as("type.nom"),
            TYPE_ETUDE.CODE.as("type.code"),
            TYPE_ETUDE_STATUT.ID.as("statut.id"),
            TYPE_ETUDE_STATUT.NOM.as("statut.nom"),
            TYPE_ETUDE_STATUT.CODE.as("statut.code"))
        .from(ETUDE)
        .join(TYPE_ETUDE)
        .on(TYPE_ETUDE.ID.eq(ETUDE.TYPE))
        .join(TYPE_ETUDE_STATUT)
        .on(TYPE_ETUDE_STATUT.ID.eq(ETUDE.STATUT))
        .where(condition)
        .orderBy(
            itemSortings == null
                ? ETUDE.STATUT.asc()
                : ETUDE
                    .field(itemSortings.getFieldName())
                    .sort(itemSortings.isAsc() ? SortOrder.ASC : SortOrder.DESC))
        .limit(limit != null ? limit : 10)
        .offset(start != null ? start : 0)
        .fetchInto(EtudeData.class);
  }

  public Map<Long, List<Commune>> getCommunesByEtude(Collection<Long> listIdEtude) {
    return context
        .select(ETUDE_COMMUNES.ETUDE, COMMUNE.ID, COMMUNE.NOM, COMMUNE.INSEE)
        .from(ETUDE_COMMUNES)
        .join(COMMUNE)
        .on(ETUDE_COMMUNES.COMMUNE.eq(COMMUNE.ID))
        .where(ETUDE_COMMUNES.ETUDE.in(listIdEtude))
        .fetchGroups(ETUDE_COMMUNES.ETUDE, Commune.class);
  }

  public Map<Long, List<DocumentEtudeData>> getDocumentsByEtude(Collection<Long> listIdEtude) {
    return context
        .select(
            ETUDE_DOCUMENTS.ETUDE,
            DOCUMENT.CODE.as("codeDocument"),
            DOCUMENT.ID.as("idDocument"),
            DOCUMENT.FICHIER.as("fichierDocument"),
            ETUDE_DOCUMENTS.NOM.as("nomEtudeDocument"))
        .from(ETUDE_DOCUMENTS)
        .join(DOCUMENT)
        .on(ETUDE_DOCUMENTS.DOCUMENT.eq(DOCUMENT.ID))
        .where(ETUDE_DOCUMENTS.ETUDE.in(listIdEtude))
        .fetchGroups(ETUDE_DOCUMENTS.ETUDE, DocumentEtudeData.class);
  }

  public Collection<Long> getEtudeIdsWithReseauImporte() {
    return context
        .selectDistinct(RESEAU.ETUDE)
        .from(RESEAU)
        .where(RESEAU.ETUDE.isNotNull())
        .fetchInto(Long.class);
  }

  public int count(Condition condition) {
    return context
        .selectCount()
        .from(ETUDE)
        .join(TYPE_ETUDE)
        .on(TYPE_ETUDE.ID.eq(ETUDE.TYPE))
        .join(TYPE_ETUDE_STATUT)
        .on(TYPE_ETUDE_STATUT.ID.eq(ETUDE.STATUT))
        .where(condition)
        .fetchOneInto(int.class);
  }

  public Condition getFilters(
      List<ItemFilter> itemFilters,
      long idOrganismeUtilisateur,
      Collection<Integer> organismesAppartenance) {
    ItemFilter type = ItemFilter.getFilter(itemFilters, "type");
    ItemFilter statut = ItemFilter.getFilter(itemFilters, "statut");
    ItemFilter id = ItemFilter.getFilter(itemFilters, "id");

    Condition condition = DSL.trueCondition();
    if (type != null) {
      condition = condition.and(TYPE_ETUDE.CODE.eq(type.getValue()));
    }
    if (statut != null) {
      condition = condition.and(TYPE_ETUDE_STATUT.CODE.eq(statut.getValue()));
    }
    if (id != null) {
      condition = condition.and(ETUDE.ID.eq(Long.valueOf(id.getValue())));
    }

    Collection<Integer> organismesZC = Organisme.getOrganismesZC(idOrganismeUtilisateur);
    condition =
        condition.and(
            ETUDE
                .ORGANISME
                .isNotNull()
                .and(ETUDE.ORGANISME.in(organismesAppartenance))
                .or(ETUDE.ORGANISME.in(organismesZC)));
    return condition;
  }

  public boolean checkNumero(String numero) {
    return context.fetchCount(context.select().from(ETUDE).where(ETUDE.NUMERO.eq(numero))) == 0;
  }

  public long addEtude(String json) {
    HashMap<String, Object> obj = new JSONDeserializer<HashMap<String, Object>>().deserialize(json);

    // Récupération du type d'étude
    long idTypeEtude =
        context
            .select(TYPE_ETUDE.ID)
            .from(TYPE_ETUDE)
            .where(TYPE_ETUDE.CODE.eq(obj.get("type").toString()))
            .fetchOneInto(Long.class);

    long idTypeEtudeStatut =
        context
            .select(TYPE_ETUDE_STATUT.ID)
            .from(TYPE_ETUDE_STATUT)
            .where(TYPE_ETUDE_STATUT.CODE.eq("EN_COURS"))
            .fetchOneInto(Long.class);

    long idOrganismeUtilisateur =
        this.utilisateurService.getCurrentUtilisateur().getOrganisme().getId();

    long idEtude =
        context
            .insertInto(ETUDE)
            .set(ETUDE.NOM, obj.get("nom").toString())
            .set(ETUDE.NUMERO, obj.get("numero").toString())
            .set(ETUDE.DESCRIPTION, obj.get("description").toString())
            .set(ETUDE.TYPE, idTypeEtude)
            .set(ETUDE.STATUT, idTypeEtudeStatut)
            .set(ETUDE.ORGANISME, idOrganismeUtilisateur)
            .returning(ETUDE.ID)
            .fetchOne()
            .getValue(ETUDE.ID);

    ArrayList<Integer> communes =
        new JSONDeserializer<ArrayList<Integer>>().deserialize(obj.get("communes").toString());
    // Ajout des communes
    for (Integer idCommune : communes) {
      context
          .insertInto(ETUDE_COMMUNES)
          .set(ETUDE_COMMUNES.ETUDE, idEtude)
          .set(ETUDE_COMMUNES.COMMUNE, new Long(idCommune))
          .execute();
    }

    return idEtude;
  }

  public long editEtude(String json) {
    HashMap<String, Object> obj = new JSONDeserializer<HashMap<String, Object>>().deserialize(json);

    long idTypeEtude =
        context
            .select(TYPE_ETUDE.ID)
            .from(TYPE_ETUDE)
            .where(TYPE_ETUDE.CODE.eq(obj.get("type").toString()))
            .fetchOneInto(Long.class);

    long idTypeEtudeStatut =
        context
            .select(TYPE_ETUDE_STATUT.ID)
            .from(TYPE_ETUDE_STATUT)
            .where(TYPE_ETUDE_STATUT.CODE.eq("EN_COURS"))
            .fetchOneInto(Long.class);

    long idEtude =
        context
            .update(ETUDE)
            .set(ETUDE.NOM, obj.get("nom").toString())
            .set(ETUDE.NUMERO, obj.get("numero").toString())
            .set(ETUDE.DESCRIPTION, obj.get("description").toString())
            .set(ETUDE.TYPE, idTypeEtude)
            .set(ETUDE.STATUT, idTypeEtudeStatut)
            .set(ETUDE.DATE_MAJ, new Instant())
            .where(ETUDE.ID.eq(Long.valueOf(obj.get("id").toString())))
            .returning(ETUDE.ID)
            .fetchOne()
            .getValue(ETUDE.ID);

    // Mise à jour des communes
    context.delete(ETUDE_COMMUNES).where(ETUDE_COMMUNES.ETUDE.eq(idEtude)).execute();

    ArrayList<Integer> communes =
        new JSONDeserializer<ArrayList<Integer>>().deserialize(obj.get("communes").toString());
    for (Integer idCommune : communes) {
      context
          .insertInto(ETUDE_COMMUNES)
          .set(ETUDE_COMMUNES.ETUDE, idEtude)
          .set(ETUDE_COMMUNES.COMMUNE, new Long(idCommune))
          .execute();
    }
    return idEtude;
  }

  public int addDocuments(Map<String, MultipartFile> files, long idEtude) {
    if (files != null && !files.isEmpty()) {
      for (MultipartFile file : files.values()) {
        if (!file.isEmpty()) {
          try {
            Document d =
                DocumentUtil.getInstance()
                    .createNonPersistedDocument(
                        Document.TypeDocument.PLANIFICATION,
                        file,
                        parametreProvider.get().getDossierDepotPlanification());
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
                .insertInto(ETUDE_DOCUMENTS)
                .set(ETUDE_DOCUMENTS.ETUDE, idEtude)
                .set(ETUDE_DOCUMENTS.DOCUMENT, docId)
                .set(ETUDE_DOCUMENTS.NOM, file.getName())
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

  public void removeDocuments(String removedFiles, Long idEtude) {
    ArrayList<String> removedDocuments =
        new JSONDeserializer<ArrayList<String>>().deserialize(removedFiles);
    for (String codeDocument : removedDocuments) {
      Long idDocument =
          context
              .select(DOCUMENT.ID)
              .from(DOCUMENT)
              .where(DOCUMENT.CODE.eq(codeDocument))
              .fetchOneInto(Long.class);
      context.delete(ETUDE_DOCUMENTS).where(ETUDE_DOCUMENTS.DOCUMENT.eq(idDocument)).execute();
      context.delete(DOCUMENT).where(DOCUMENT.ID.eq(idDocument)).execute();
    }
  }

  public void cloreEtude(Long id) {
    long idTypeEtudeStatut =
        context
            .select(TYPE_ETUDE_STATUT.ID)
            .from(TYPE_ETUDE_STATUT)
            .where(TYPE_ETUDE_STATUT.CODE.eq("TERMINEE"))
            .fetchOneInto(Long.class);
    context.update(ETUDE).set(ETUDE.STATUT, idTypeEtudeStatut).where(ETUDE.ID.eq(id)).execute();
  }

  /**
   * Permet de récupérer l'emprise géographique d'une étude
   *
   * @param idEtude
   * @return
   */
  public String getEtenduEtude(Long idEtude) {
    return context
        .select(
            DSL.field(
                    "St_AsEwkt(St_transform(St_SetSrid(CAST(St_Extent("
                        + COMMUNE.GEOMETRIE
                        + ") as Geometry), "
                        + parametreProvider.get().getSridInt()
                        + "), "
                        + Integer.parseInt(GlobalConstants.SRID_3857)
                        + "))")
                .as("geometrie"))
        .from(ETUDE)
        .join(ETUDE_COMMUNES)
        .on(ETUDE.ID.eq(ETUDE_COMMUNES.ETUDE))
        .join(COMMUNE)
        .on(COMMUNE.ID.eq(ETUDE_COMMUNES.COMMUNE))
        .where(ETUDE.ID.eq(idEtude))
        .fetchOneInto(String.class);
  }
}
