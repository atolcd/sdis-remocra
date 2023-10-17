package fr.sdis83.remocra.repository;

import static fr.sdis83.remocra.db.model.couverture_hydraulique.Tables.RESEAU;
import static fr.sdis83.remocra.db.model.remocra.Tables.ETUDE;
import static fr.sdis83.remocra.db.model.remocra.Tables.ETUDE_COMMUNES;
import static fr.sdis83.remocra.db.model.remocra.Tables.ETUDE_DOCUMENTS;
import static fr.sdis83.remocra.db.model.remocra.Tables.ORGANISME;
import static fr.sdis83.remocra.db.model.remocra.Tables.TYPE_ETUDE;
import static fr.sdis83.remocra.db.model.remocra.Tables.TYPE_ETUDE_STATUT;
import static fr.sdis83.remocra.db.model.remocra.tables.Commune.COMMUNE;
import static fr.sdis83.remocra.db.model.remocra.tables.Document.DOCUMENT;

import flexjson.JSONDeserializer;
import fr.sdis83.remocra.GlobalConstants;
import fr.sdis83.remocra.db.model.remocra.tables.pojos.Organisme;
import fr.sdis83.remocra.db.model.remocra.tables.pojos.TypeEtude;
import fr.sdis83.remocra.db.model.remocra.tables.pojos.TypeEtudeStatut;
import fr.sdis83.remocra.domain.remocra.Document;
import fr.sdis83.remocra.domain.utils.RemocraDateHourTransformer;
import fr.sdis83.remocra.service.ParamConfService;
import fr.sdis83.remocra.service.UtilisateurService;
import fr.sdis83.remocra.util.DocumentUtil;
import fr.sdis83.remocra.web.message.ItemFilter;
import fr.sdis83.remocra.web.message.ItemSorting;
import fr.sdis83.remocra.web.model.Etude;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.apache.log4j.Logger;
import org.joda.time.Instant;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Record;
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
public class EtudeRepository {
  private final Logger logger = Logger.getLogger(getClass());
  private final DateFormat df = new SimpleDateFormat(RemocraDateHourTransformer.FORMAT);

  @Autowired DSLContext context;

  @Autowired JpaTransactionManager transactionManager;

  @PersistenceContext private EntityManager entityManager;

  @Autowired protected ParamConfService paramConfService;

  @Autowired private UtilisateurService utilisateurService;

  public EtudeRepository() {}

  @Bean
  public EtudeRepository EtudeRepository(DSLContext context) {
    return new EtudeRepository(context);
  }

  EtudeRepository(DSLContext context) {
    this.context = context;
  }

  public List<Etude> getAll(
      List<ItemFilter> itemFilters, Integer limit, Integer start, List<ItemSorting> itemSortings) {
    if (limit == null) limit = 100;
    if (start == null) start = 0;

    long idOrganismeUtilisateur = utilisateurService.getCurrentUtilisateur().getOrganisme().getId();
    // Etudes créées par l'organisme de l'utilisateur ou l'un de ses organismes enfant
    ArrayList<Integer> organismesAppartenance =
        fr.sdis83.remocra.domain.remocra.Organisme.getOrganismeAndChildren(
            (int) idOrganismeUtilisateur);
    // Etudes créées par son organisme d’appartenance de l’utilisateur connecté ou un de ses enfants
    ArrayList<Integer> organismesZC =
        fr.sdis83.remocra.domain.remocra.Organisme.getOrganismesZC(idOrganismeUtilisateur);

    Condition condition = this.getFilters(itemFilters, organismesZC);

    String sortField = "date_maj";
    SortOrder sortOrder = SortOrder.DESC;
    for (ItemSorting itemSorting : itemSortings) {
      sortField = itemSorting.getFieldName();
      sortOrder = itemSorting.isDesc() ? SortOrder.DESC : SortOrder.ASC;
    }

    List<Record> listRecords =
        context
            .select()
            .from(ETUDE)
            .join(TYPE_ETUDE)
            .on(TYPE_ETUDE.ID.eq(ETUDE.TYPE))
            .join(TYPE_ETUDE_STATUT)
            .on(TYPE_ETUDE_STATUT.ID.eq(ETUDE.STATUT))
            .where(condition)
            .orderBy(ETUDE.field(DSL.field(sortField)).sort(sortOrder))
            .limit(limit)
            .offset(start)
            .fetch();

    ModelMapper modelMapper = new ModelMapper();
    modelMapper.getConfiguration().addValueReader(new RecordValueReader());
    modelMapper.getConfiguration().setSourceNameTokenizer(NameTokenizers.CAMEL_CASE);

    List<Etude> listeEtudes = new ArrayList<>();

    for (Record r : listRecords) {
      Etude etude = modelMapper.map(r, Etude.class);

      // Type
      TypeEtude type =
          context
              .select()
              .from(TYPE_ETUDE)
              .where(TYPE_ETUDE.ID.eq(Long.valueOf(r.getValue("type").toString())))
              .fetchOneInto(TypeEtude.class);
      etude.setType(type);

      // Statut
      TypeEtudeStatut statut =
          context
              .select()
              .from(TYPE_ETUDE_STATUT)
              .where(TYPE_ETUDE_STATUT.ID.eq(Long.valueOf(r.getValue("statut").toString())))
              .fetchOneInto(TypeEtudeStatut.class);
      etude.setStatut(statut);

      // Organisme
      Organisme organisme =
          context
              .select()
              .from(ORGANISME)
              .where(ORGANISME.ID.eq(Long.valueOf(r.getValue("organisme").toString())))
              .fetchOneInto(Organisme.class);
      etude.setOrganisme(organisme);

      // Communes
      List<fr.sdis83.remocra.db.model.remocra.tables.pojos.Commune> recordsCommune =
          context
              .select(
                  COMMUNE.ID,
                  COMMUNE.NOM,
                  COMMUNE.CODE,
                  COMMUNE.INSEE,
                  COMMUNE.PPRIF,
                  COMMUNE.GEOMETRIE)
              .from(COMMUNE)
              .join(ETUDE_COMMUNES)
              .on(ETUDE_COMMUNES.COMMUNE.eq(COMMUNE.ID))
              .join(ETUDE)
              .on(ETUDE.ID.eq(ETUDE_COMMUNES.ETUDE))
              .where(ETUDE.ID.eq(Long.valueOf(r.getValue("id").toString())))
              .fetchInto(fr.sdis83.remocra.db.model.remocra.tables.pojos.Commune.class);
      etude.setCommunes(
          new ArrayList<fr.sdis83.remocra.db.model.remocra.tables.pojos.Commune>(recordsCommune));

      // Documents
      List<fr.sdis83.remocra.db.model.remocra.tables.pojos.Document> recordsDocument =
          context
              .select(
                  DOCUMENT.ID,
                  DOCUMENT.DATE,
                  DOCUMENT.DATE_DOC,
                  DOCUMENT.TYPE,
                  DOCUMENT.REPERTOIRE,
                  DOCUMENT.FICHIER,
                  DOCUMENT.CODE)
              .from(DOCUMENT)
              .join(ETUDE_DOCUMENTS)
              .on(ETUDE_DOCUMENTS.DOCUMENT.eq(DOCUMENT.ID))
              .join(ETUDE)
              .on(ETUDE.ID.eq(ETUDE_DOCUMENTS.ETUDE))
              .where(ETUDE.ID.eq(Long.valueOf(r.getValue("id").toString())))
              .fetchInto(fr.sdis83.remocra.db.model.remocra.tables.pojos.Document.class);
      etude.setDocuments(
          new ArrayList<fr.sdis83.remocra.db.model.remocra.tables.pojos.Document>(recordsDocument));

      Map<String, String> documentsNoms =
          context
              .select(DOCUMENT.CODE, ETUDE_DOCUMENTS.NOM)
              .from(DOCUMENT)
              .join(ETUDE_DOCUMENTS)
              .on(ETUDE_DOCUMENTS.DOCUMENT.eq(DOCUMENT.ID))
              .join(ETUDE)
              .on(ETUDE.ID.eq(ETUDE_DOCUMENTS.ETUDE))
              .where(ETUDE.ID.eq(Long.valueOf(r.getValue("id").toString())))
              .fetch()
              .intoMap(DOCUMENT.CODE, ETUDE_DOCUMENTS.NOM);
      listeEtudes.add(etude);
      etude.setDocumentsNoms(documentsNoms);

      // On vérifie la présence d'un réseau importé
      Integer nbReseauImportes =
          context
              .select(RESEAU.ID.count())
              .from(RESEAU)
              .where(RESEAU.ETUDE.eq(Long.valueOf(r.getValue("id").toString())))
              .fetchOneInto(Integer.class);
      etude.setReseauImporte((nbReseauImportes > 0) ? true : false);

      // On détermine si l'étude peut être modifiée ou non
      etude.setReadOnly(
          (organismesAppartenance.contains(organisme.getId().intValue()) ? false : true));
    }
    return listeEtudes;
  }

  @Transactional
  public int count(List<ItemFilter> itemFilters) {
    long idOrganismeUtilisateur = utilisateurService.getCurrentUtilisateur().getOrganisme().getId();
    ArrayList<Integer> organismesZC =
        fr.sdis83.remocra.domain.remocra.Organisme.getOrganismesZC(idOrganismeUtilisateur);

    Condition condition = this.getFilters(itemFilters, organismesZC);
    return context.fetchCount(
        context
            .select()
            .from(ETUDE)
            .join(TYPE_ETUDE)
            .on(TYPE_ETUDE.ID.eq(ETUDE.TYPE))
            .join(TYPE_ETUDE_STATUT)
            .on(TYPE_ETUDE_STATUT.ID.eq(ETUDE.STATUT))
            .where(condition));
  }

  public Condition getFilters(List<ItemFilter> itemFilters, ArrayList<Integer> organismesZC) {
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

    long idOrganismeUtilisateur = utilisateurService.getCurrentUtilisateur().getOrganisme().getId();
    // Etudes créées par son organisme d’appartenance de l’utilisateur connecté ou un de ses enfants
    ArrayList<Integer> organismesAppartenance =
        fr.sdis83.remocra.domain.remocra.Organisme.getOrganismeAndChildren(
            (int) idOrganismeUtilisateur);
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
                        paramConfService.getDossierDepotPlanification());
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
                        + GlobalConstants.SRID_2154
                        + "), 3857))")
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
