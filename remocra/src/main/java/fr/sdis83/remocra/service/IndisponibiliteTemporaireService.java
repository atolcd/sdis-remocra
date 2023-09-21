package fr.sdis83.remocra.service;

import flexjson.JSONDeserializer;
import fr.sdis83.remocra.domain.remocra.Hydrant;
import fr.sdis83.remocra.domain.remocra.HydrantIndispoTemporaire;
import fr.sdis83.remocra.domain.remocra.TypeHydrantIndispoStatut;
import fr.sdis83.remocra.domain.remocra.Utilisateur;
import fr.sdis83.remocra.domain.utils.JSONMap;
import fr.sdis83.remocra.domain.utils.RemocraDateHourTransformer;
import fr.sdis83.remocra.web.deserialize.RemocraBeanObjectFactory;
import fr.sdis83.remocra.web.message.ItemFilter;
import fr.sdis83.remocra.web.message.ItemSorting;
import java.math.BigInteger;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.Transactional;

@Configuration
public class IndisponibiliteTemporaireService extends AbstractService<HydrantIndispoTemporaire> {

  protected enum Projection {
    ALL("*"),
    COUNT("count(*)");

    private final String value;

    private Projection(String value) {
      this.value = value;
    }

    public String toString() {
      return this.value;
    }
  }

  protected enum TypeCondition {
    EQ,
    DIFF,
    COND;
  }

  private final Logger logger = Logger.getLogger(getClass());

  @Autowired private UtilisateurService utilisateurService;

  public IndisponibiliteTemporaireService() {
    super(HydrantIndispoTemporaire.class);
  }

  @Bean
  public IndisponibiliteTemporaireService indisponibiliteTemporaireService() {
    return new IndisponibiliteTemporaireService();
  }

  @Autowired private UtilisateurService serviceUtilisateur;

  @Override
  protected Predicate processFilterItem(
      CriteriaQuery<?> itemQuery,
      Map<String, Object> parameters,
      Root<HydrantIndispoTemporaire> from,
      ItemFilter itemFilter) {
    // Filtrage par section
    if ("statut".equals(itemFilter.getFieldName())) {
      return statutFieldPredicate(from, itemFilter);
    }
    if ("nomStatut".equals(itemFilter.getFieldName())) {
      return nomStatutFieldPredicate(from, itemFilter);
    }
    return super.processFilterItem(itemQuery, parameters, from, itemFilter);
  }

  private Predicate statutFieldPredicate(
      Root<HydrantIndispoTemporaire> from, ItemFilter itemFilter) {
    // Création du prédicat de recherche
    CriteriaBuilder cBuilder = HydrantIndispoTemporaire.entityManager().getCriteriaBuilder();
    Path<String> statut = from.get("statut").get("id");
    return cBuilder.equal(statut, itemFilter.getValue());
  }

  private Predicate nomStatutFieldPredicate(
      Root<HydrantIndispoTemporaire> from, ItemFilter itemFilter) {
    // Création du prédicat de recherche
    CriteriaBuilder cBuilder = HydrantIndispoTemporaire.entityManager().getCriteriaBuilder();
    Path<String> statut = from.get("nomStatut");
    return cBuilder.equal(statut, itemFilter.getValue());
  }

  public Query getIndisponibilitesQuery(
      List<ItemFilter> itemFilter,
      List<ItemSorting> sortList,
      Projection projection,
      Integer limit,
      Integer offset) {
    Long zc = utilisateurService.getCurrentZoneCompetenceId();
    StringBuilder sql =
        new StringBuilder("select ")
            .append(projection)
            .append(" from (")
            .append(" select hit.*, (select c.nom ")
            .append("from remocra.commune c ")
            .append("join remocra.hydrant h on h.commune=c.id ")
            .append("join remocra.hydrant_indispo_temporaire_hydrant hith on h.id=hith.hydrant ")
            .append("join remocra.hydrant_indispo_temporaire hitd on hith.indisponibilite=hitd.id ")
            .append("where hitd.id=hit.id group by nom) as commune ")
            .append(" from remocra.hydrant_indispo_temporaire hit ")
            .append("where hit.id in (")
            .append("select indisponibilite from remocra.hydrant_indispo_temporaire_hydrant hith ")
            .append("where ")
            // Filtre par zone de compétence systématique
            .append("hith.hydrant in(")
            .append("select h.id from remocra.hydrant h where h.commune in(")
            .append(
                "select zcc.commune_id from remocra.zone_competence_commune zcc where zcc.zone_competence_id =:zc")
            .append("))");

    if (itemFilter != null && itemFilter.size() > 0) {
      for (ItemFilter f : itemFilter) {
        String fieldName = null;
        String fieldValue = null;
        String condition = null;
        TypeCondition tc = TypeCondition.EQ;
        if ("hydrantId".equals(f.getFieldName())) {
          fieldName = "hith.hydrant";
          fieldValue = f.getValue();
        } else if ("statut".equals(f.getFieldName())) {
          fieldName = "hit.statut";
          Long statut = Long.parseLong(f.getValue());
          fieldValue = f.getValue();
          if (statut < 0) {
            tc = TypeCondition.DIFF;
            fieldValue = (-statut) + "";
          }
        } else if ("commune".equals(f.getFieldName())) {
          tc = TypeCondition.COND;
          condition =
              "hith.hydrant in(select id from remocra.hydrant h2 where h2.commune="
                  + f.getValue()
                  + ")";
        } else {
          logger.info("Indispo temporaires, critère de filtre inconnu : " + f.getFieldName());
          continue;
        }
        sql.append(" and ");
        switch (tc) {
          case EQ:
            sql.append(fieldName).append("=").append(fieldValue);
            break;
          case DIFF:
            sql.append(fieldName).append("!=").append(fieldValue);
            break;
          case COND:
            sql.append(condition);
            break;
        }
      }
    }
    sql.append(")");
    if (sortList != null && sortList.size() > 0) {
      sql.append(" order by");
      boolean first = true;
      for (ItemSorting s : sortList) {
        String fieldName = null;
        if ("dateDebut".equals(s.getFieldName())) {
          fieldName = "date_debut";
        } else if ("dateFin".equals(s.getFieldName())) {
          fieldName = "date_fin";
        } else if ("motif".equals(s.getFieldName())) {
          fieldName = "motif";
        } else if ("commune".equals(s.getFieldName())) {
          fieldName = "commune";
        } else if ("basculeAutoIndispo".equals(s.getFieldName())) {
          fieldName = "bascule_auto_indispo";
        } else if ("basculeAutoDispo".equals(s.getFieldName())) {
          fieldName = "bascule_auto_dispo";
        } else if ("melAvantIndispo".equals(s.getFieldName())) {
          fieldName = "mel_avant_indispo";
        } else if ("melAvantDispo".equals(s.getFieldName())) {
          fieldName = "mel_avant_dispo";
        } else {
          logger.info("Indispo temporaires, critère de tri inconnu : " + s.getFieldName());
          continue;
        }
        if (!first) {
          sql.append(",");
        }
        first = false;
        sql.append(" ").append(fieldName).append(" ").append(s.getDirection());
      }
    }
    if (limit != null && offset != null) {
      sql.append(" limit :limit offset :offset");
    }
    sql.append(" ) AS R");
    Query query =
        projection == Projection.COUNT
            ? entityManager.createNativeQuery(sql.toString())
            : entityManager.createNativeQuery(sql.toString(), HydrantIndispoTemporaire.class);
    query.setParameter("zc", zc);
    if (limit != null && offset != null) {
      query.setParameter("limit", limit).setParameter("offset", offset);
    }
    return query;
  }

  public List<HydrantIndispoTemporaire> getIndisponibilites(
      Integer limit, Integer offset, List<ItemFilter> itemFilter, List<ItemSorting> sortList) {
    return getIndisponibilitesQuery(itemFilter, sortList, Projection.ALL, limit, offset)
        .getResultList();
  }

  public Long getIndisponibilitesCount(List<ItemFilter> itemFilter) {
    return ((BigInteger)
            getIndisponibilitesQuery(itemFilter, null, Projection.COUNT, null, null)
                .getSingleResult())
        .longValue();
  }

  public List<HydrantIndispoTemporaire> getAllIndisponibilite() {
    String sql = "SELECT i FROM HydrantIndispoTemporaire i";
    TypedQuery<HydrantIndispoTemporaire> query =
        entityManager.createQuery(sql, HydrantIndispoTemporaire.class);
    return query.getResultList();
  }

  private int insertIndisponibilitesTemporaires(ArrayList<Long> hydrants, Long idIndispo) {
    Query query;
    int result = 0;
    for (Long hydrant : hydrants) {
      query =
          entityManager
              .createNativeQuery(
                  ("INSERT INTO remocra.hydrant_indispo_temporaire_hydrant(indisponibilite,hydrant) SELECT :indisponibilite, :hydrant" /* +
                                                                                                                                       " WHERE NOT EXISTS ( SELECT 1 FROM remocra.hydrant_indispo_temporaire_hydrant WHERE hydrant = :hydrant)"*/))
              .setParameter("indisponibilite", idIndispo)
              .setParameter("hydrant", hydrant);
      result = result + query.executeUpdate();
    }
    return result;
  }

  @Transactional
  public HydrantIndispoTemporaire setIndispoTemp(String json) {

    HashMap<String, Object> obj = new JSONDeserializer<HashMap<String, Object>>().deserialize(json);

    JSONDeserializer<HydrantIndispoTemporaire> deserializer =
        new JSONDeserializer<HydrantIndispoTemporaire>();
    deserializer
        .use(null, this.cls)
        .use(Date.class, RemocraDateHourTransformer.getInstance())
        .use(Object.class, new RemocraBeanObjectFactory(this.entityManager));

    ArrayList<Long> listeIdPei = new ArrayList<Long>();
    for (String idPei : ((ArrayList<String>) obj.get("tabIdPeiConcernes"))) {
      listeIdPei.add(Long.valueOf(idPei));
    }

    Long idIndispoTemp = null;
    if (obj.get("idIndispoTemp") != null) {
      idIndispoTemp = Long.valueOf(String.valueOf(obj.get("idIndispoTemp")));
    }
    obj.remove("idIndispoTemp");
    obj.remove("tabIdPeiConcernes");

    // Formatage des dates
    Date dateDebut = null;
    Date dateFin = null;
    try {
      dateDebut =
          new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(String.valueOf(obj.get("dateDebut")));
    } catch (Exception e) {
      e.printStackTrace();
    }
    obj.remove("dateDebut");

    if (!String.valueOf(obj.get("dateFin")).equals("null")) {
      try {
        dateFin =
            new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(String.valueOf(obj.get("dateFin")));
      } catch (Exception e) {
        e.printStackTrace();
      }
      obj.remove("dateFin");
    }

    // Problème champ observation (les retours chariot posent problème
    // Provoque erreur lors de l'appel  : indispo =
    // deserializer.deserialize(JSONMap.fromMap(obj).toString());
    String observation = (String) obj.get("observation");
    obj.remove("observation");

    HydrantIndispoTemporaire indispo = new HydrantIndispoTemporaire();
    indispo = deserializer.deserialize(JSONMap.fromMap(obj).toString());

    if (idIndispoTemp != null) {
      indispo.setId(idIndispoTemp);
    }

    indispo.setDateDebut(dateDebut);
    indispo.setDateFin(dateFin);
    indispo.setObservation(observation);

    // Définition du statut
    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    Date date = new Date();
    Date dateCourante = null;
    try {
      dateCourante = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(dateFormat.format(date));
    } catch (Exception e) {
      e.printStackTrace();
    }

    if (dateDebut.before(dateCourante) && (dateFin == null || dateCourante.before(dateFin))) {
      indispo.setStatut(
          TypeHydrantIndispoStatut.findTypeHydrantIndispoStatutByCode("EN_COURS")
              .getSingleResult());
    } else if (dateCourante.before(dateDebut)) {
      indispo.setStatut(
          TypeHydrantIndispoStatut.findTypeHydrantIndispoStatutByCode("PLANIFIE")
              .getSingleResult());
    } else {
      indispo.setStatut(
          TypeHydrantIndispoStatut.findTypeHydrantIndispoStatutByCode("TERMINE").getSingleResult());
    }

    try {
      this.setUpInformation(indispo, null);
    } catch (Exception e) {
      e.printStackTrace();
    }

    if (idIndispoTemp != null) {
      indispo.merge();
    } else {
      indispo.persist();
      indispo.flush();
    }

    // this.entityManager.persist(indispo);

    // Si lindipo est en cours, on ajoute une anomalie aux hydrants
    if ("EN_COURS".equals(indispo.getStatut().getCode())) {
      // on update l'utilisateur
      this.updateUserModification(listeIdPei);
      // on ajoute l'anomalie indispoTemporaire
      this.setHydrantAnomalie(listeIdPei);
    }

    // On ajoute les pei dans la table hydrant_indispo_temporaire_hydrant
    this.insertIndisponibilitesTemporaires(listeIdPei, indispo.getId());

    return indispo;
  }

  private int deleteHydrantsIndispo(HydrantIndispoTemporaire indispo) {
    Query query;
    int result = 0;
    query =
        entityManager
            .createNativeQuery(
                ("DELETE FROM remocra.hydrant_indispo_temporaire_hydrant WHERE indisponibilite = :indisponibilite"))
            .setParameter("indisponibilite", indispo.getId());
    result = result + query.executeUpdate();
    return result;
  }

  @Override
  @Transactional
  public boolean delete(Long id) throws Exception {
    // La suppression d'une indisponibilie temporaire
    HydrantIndispoTemporaire indispo = HydrantIndispoTemporaire.findHydrantIndispoTemporaire(id);
    if (indispo.getHydrants().isEmpty()) {
      indispo.remove();
    } else {
      Iterator<Hydrant> hydrants = indispo.getHydrants().iterator();
      ArrayList<Long> ids = new ArrayList<Long>();
      while (hydrants.hasNext()) {
        ids.add(hydrants.next().getId());
      }
      Query query =
          entityManager
              .createNativeQuery(
                  ("DELETE FROM remocra.hydrant_indispo_temporaire_hydrant WHERE indisponibilite = :indisponibilite"))
              .setParameter("indisponibilite", id);
      query.executeUpdate();
      indispo.remove();
      this.deleteHydrantAnomalie(ids);
    }

    return true;
  }

  @Transactional
  public int setHydrantAnomalie(ArrayList<Long> hydrants) {
    int result = 0;
    if (!hydrants.isEmpty()) {
      Query query;
      for (Long id : hydrants) {
        query =
            entityManager
                .createNativeQuery(
                    "insert into remocra.hydrant_anomalies(hydrant,anomalies) select :hydrantId, (select  id from remocra.type_hydrant_anomalie where code = 'INDISPONIBILITE_TEMP')"
                        + " WHERE NOT EXISTS (SELECT 1 FROM remocra.hydrant_anomalies WHERE hydrant = :hydrantId AND anomalies = (select  id from remocra.type_hydrant_anomalie where code = 'INDISPONIBILITE_TEMP'))")
                .setParameter("hydrantId", id);
        result = result + query.executeUpdate();
      }
      return result;
    }
    return result;
  }

  @Transactional
  public int deleteHydrantAnomalie(ArrayList<Long> hydrants) {

    int result = 0;
    if (!hydrants.isEmpty()) {
      Query query;
      for (Long id : hydrants) {
        query =
            entityManager
                .createNativeQuery(
                    "DELETE FROM remocra.hydrant_anomalies WHERE hydrant= :hydrantId AND anomalies = (SELECT id FROM remocra.type_hydrant_anomalie WHERE code = 'INDISPONIBILITE_TEMP')"
                        + " AND ((SELECT COUNT (statut) FROM remocra.hydrant_indispo_temporaire"
                        + " WHERE id IN (SELECT indisponibilite FROM remocra.hydrant_indispo_temporaire_hydrant WHERE hydrant = :hydrantId) AND statut = 1) =0)")
                .setParameter("hydrantId", id);
        result = result + query.executeUpdate();
      }
      return result;
    }
    return result;
  }

  @Transactional
  public int updateUserModification(ArrayList<Long> hydrants) {
    int result = 0;
    if (!hydrants.isEmpty()) {
      Utilisateur userModification = serviceUtilisateur.getCurrentUtilisateur();
      Query query;
      for (Long id : hydrants) {
        query =
            entityManager
                .createNativeQuery(
                    "UPDATE remocra.hydrant"
                        + " SET utilisateur_modification = :userModification"
                        + " WHERE id = :hydrantId")
                .setParameter("userModification", userModification.getId())
                .setParameter("hydrantId", id);
        result = result + query.executeUpdate();
      }
      return result;
    }
    return result;
  }

  @Transactional
  public void deleteIfEmpty(Long id) throws Exception {
    Query query =
        entityManager.createNativeQuery(
            "SELECT CAST(id as INTEGER) FROM remocra.hydrant_indispo_temporaire where id in (SELECT indisponibilite FROM remocra.hydrant_indispo_temporaire_hydrant hith where hith.hydrant = "
                + id
                + ")");
    List<Integer> l = query.getResultList();
    for (Integer indispoId : l) {
      // Si c'est le dernier on supprime l'indispo
      HydrantIndispoTemporaire hit =
          HydrantIndispoTemporaire.findHydrantIndispoTemporaire(Long.valueOf(indispoId));
      if (hit.getCountHydrant() == 1) {
        this.delete(hit.getId());
      }
    }
  }

  @Transactional
  public List<String> hydrantsTjrsIndispo(Long id) {
    Query query =
        entityManager.createNativeQuery(
            "SELECT h.numero from remocra.hydrant h "
                + "WHERE h.dispo_terrestre = 'INDISPO' "
                + "AND h.id IN (SELECT hydrant FROM remocra.hydrant_indispo_temporaire_hydrant "
                + "WHERE indisponibilite = :id)");
    query.setParameter("id", id);
    List<String> result = query.getResultList();
    return result;
  }
}
