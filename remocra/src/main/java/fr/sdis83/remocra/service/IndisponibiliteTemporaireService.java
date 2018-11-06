package fr.sdis83.remocra.service;

import flexjson.JSONDeserializer;
import fr.sdis83.remocra.domain.remocra.Hydrant;
import fr.sdis83.remocra.domain.remocra.HydrantIndispoTemporaire;
import fr.sdis83.remocra.domain.remocra.TypeHydrantIndispoStatut;
import fr.sdis83.remocra.domain.remocra.Utilisateur;
import fr.sdis83.remocra.domain.remocra.ZoneCompetence;
import fr.sdis83.remocra.domain.utils.RemocraDateHourTransformer;
import fr.sdis83.remocra.web.message.ItemFilter;
import fr.sdis83.remocra.web.message.ItemSorting;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.math.BigInteger;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@Configuration
public class IndisponibiliteTemporaireService extends AbstractService<HydrantIndispoTemporaire> {

    protected enum Projection {
        ALL("*"), COUNT("count(*)");

        private final String value;

        private Projection(String value) {
            this.value = value;
        }

        public String toString() {
            return this.value;
        }
    }

    private final Logger logger = Logger.getLogger(getClass());


    @Autowired
    private UtilisateurService utilisateurService;

    public IndisponibiliteTemporaireService() {
        super(HydrantIndispoTemporaire.class);
    }

    @Bean
    public IndisponibiliteTemporaireService indisponibiliteTemporaireService() {
        return new IndisponibiliteTemporaireService();
    }
    @Autowired
    private UtilisateurService serviceUtilisateur;

    @Override
    protected Predicate processFilterItem(CriteriaQuery<?> itemQuery, Map<String, Object> parameters, Root<HydrantIndispoTemporaire> from, ItemFilter itemFilter) {
        // Filtrage par section
        if ("statut".equals(itemFilter.getFieldName())) {
            return statutFieldPredicate(from, itemFilter);
        }
        if ("nomStatut".equals(itemFilter.getFieldName())) {
            return nomStatutFieldPredicate(from, itemFilter);
        }
        return super.processFilterItem(itemQuery, parameters, from, itemFilter);
    }

    private Predicate statutFieldPredicate(Root<HydrantIndispoTemporaire> from, ItemFilter itemFilter) {
        // Création du prédicat de recherche
        CriteriaBuilder cBuilder = HydrantIndispoTemporaire.entityManager().getCriteriaBuilder();
        Path<String> statut = from.get("statut").get("id");
        return cBuilder.equal(statut, itemFilter.getValue());
    }
    private Predicate nomStatutFieldPredicate(Root<HydrantIndispoTemporaire> from, ItemFilter itemFilter) {
        // Création du prédicat de recherche
        CriteriaBuilder cBuilder = HydrantIndispoTemporaire.entityManager().getCriteriaBuilder();
        Path<String> statut = from.get("nomStatut");
        return cBuilder.equal(statut, itemFilter.getValue());
    }

    public Query getIndisponibilitesQuery(List<ItemFilter> itemFilter, List<ItemSorting> sortList, Projection projection, Integer limit, Integer offset) {
        ZoneCompetence zc = utilisateurService.getCurrentUtilisateur().getOrganisme().getZoneCompetence();
        StringBuilder sql = new StringBuilder("select ").append(projection)
                .append(" from remocra.hydrant_indispo_temporaire hit where hit.id in (")
                .append("select indisponibilite from remocra.hydrant_indispo_temporaire_hydrant hith ")
                .append("where ")
                // Filtre par zone de compétence systématique
                .append("hith.hydrant in(")
                .append("select h.id from remocra.hydrant h where h.commune in(")
                .append("select c.id from remocra.commune c where st_Overlaps((select geometrie from remocra.zone_competence zc where zc.id = :zc), c.geometrie)")
                .append(" or st_contains((select geometrie from remocra.zone_competence zc where zc.id = :zc), c.geometrie)")
                .append("))");
                //
        if (itemFilter!=null && itemFilter.size()>0) {
            for (ItemFilter f : itemFilter) {
                String fieldName = null;
                String fieldValue = null;
                if ("hydrantId".equals(f.getFieldName())) {
                    fieldName = "hith.hydrant";
                    fieldValue = f.getValue();
                } else if ("statut".equals(f.getFieldName())) {
                    fieldName = "hit.statut";
                    fieldValue = f.getValue();
                } else {
                    logger.info("Indispo temporaires, critère de filtre inconnu : " + f.getFieldName());
                    continue;
                }
                sql.append(" and ").append(fieldName).append("=").append(fieldValue);
            }
        }
        sql.append(")");
        if (sortList!=null && sortList.size()>0) {
            sql.append(" order by");
            boolean first = true;
            for (ItemSorting s : sortList) {
                String fieldName = null;
                if ("datePrevDebut".equals(s.getFieldName())) {
                    fieldName = "date_prev_debut";
                } else if ("datePrevFin".equals(s.getFieldName())) {
                    fieldName = "date_prev_fin";
                } else if ("dateDebut".equals(s.getFieldName())) {
                    fieldName = "date_debut";
                } else if ("dateFin".equals(s.getFieldName())) {
                    fieldName = "date_fin";
                } else if ("motif".equals(s.getFieldName())) {
                    fieldName = "motif";
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
        Query query = projection == Projection.COUNT ? entityManager.createNativeQuery(sql.toString())
                : entityManager.createNativeQuery(sql.toString(), HydrantIndispoTemporaire.class);
        query.setParameter("zc", zc.getId());
        if (limit != null && offset != null) {
            query.setParameter("limit", limit).setParameter("offset", offset);
        }
        return query;
    }

    public List<HydrantIndispoTemporaire> getIndisponibilites(Integer limit, Integer offset, List<ItemFilter> itemFilter, List<ItemSorting> sortList) {
        return getIndisponibilitesQuery(itemFilter, sortList, Projection.ALL, limit, offset).getResultList();
    }

    public Long getIndisponibilitesCount(List<ItemFilter> itemFilter) {
        return ((BigInteger) getIndisponibilitesQuery(itemFilter, null, Projection.COUNT, null, null).getSingleResult()).longValue();
    }

    public List<HydrantIndispoTemporaire> getAllIndisponibilite() {
        String sql = "SELECT i FROM HydrantIndispoTemporaire i";
        TypedQuery<HydrantIndispoTemporaire> query = entityManager.createQuery(sql, HydrantIndispoTemporaire.class);
        return query.getResultList();
    }

    @Transactional
    public Integer setIndispo(String json) {
        HashMap<String, Object> items = new JSONDeserializer<HashMap<String, Object>>().use(Date.class, RemocraDateHourTransformer.getInstance()).deserialize(json);

        // id des hydrants
        ArrayList<Long> ids = new ArrayList<Long>();
        for (Integer item : ((ArrayList<Integer>) items.get("ids"))) {
            ids.add(Long.valueOf(item));
        }

        // id de l'indisponibilite
        Long indispoId = null;
        HashMap<String,Object> obj = (HashMap<String, Object>) items.get("indispo");
         if (obj.get("id") != null) {
            indispoId = Long.valueOf(String.valueOf(obj.get("id")));
        }

        DateFormat df = new SimpleDateFormat(RemocraDateHourTransformer.FORMAT);
        HydrantIndispoTemporaire indispo = null;
            // il faut créer une indisponibilite
            indispo = new HydrantIndispoTemporaire();
        if(indispoId != null) {
            indispo = HydrantIndispoTemporaire.findHydrantIndispoTemporaire(indispoId);
            //On supprime tout les hydrants qui correspondent à cette indisponibilite
            this.deleteHydrantsIndispo(indispo);
        }
            Date datePrevDebut = null;
            if(obj.get("datePrevDebut")!=null) {
                try {
                    datePrevDebut = df.parse(String.valueOf(obj.get("datePrevDebut")));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
            Date datePrevFin = null;
            if(obj.get("datePrevFin")!=null) {
                try {
                    datePrevFin = df.parse(String.valueOf(obj.get("datePrevFin")));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }

            Date dateReelDebut = null;
            if(obj.get("dateDebut")!= null) {
                try {
                    dateReelDebut = df.parse(String.valueOf(obj.get("dateDebut")));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }

            Date dateReelFin = null;
            if(obj.get("dateFin")!=null) {
                try {
                    dateReelFin = df.parse(String.valueOf(obj.get("dateFin")));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }

            indispo.setDateDebut(dateReelDebut);
            indispo.setDateFin(dateReelFin);
            indispo.setDatePrevDebut(datePrevDebut);
            indispo.setDatePrevFin(datePrevFin);
            indispo.setTotalHydrants(ids.size());
            indispo.setMotif(String.valueOf(obj.get("motif")));
            if(dateReelDebut != null && dateReelFin == null){
                indispo.setStatut(TypeHydrantIndispoStatut.findTypeHydrantIndispoStatutByCode("EN_COURS").getSingleResult());
            }else if(dateReelFin != null) {
                indispo.setStatut(TypeHydrantIndispoStatut.findTypeHydrantIndispoStatutByCode("TERMINE").getSingleResult());
            }else {
                indispo.setStatut(TypeHydrantIndispoStatut.findTypeHydrantIndispoStatutByCode("PLANIFIE").getSingleResult());
            }

        if(indispoId != null) {
            indispo.merge();
        }else {
            indispo.persist();
            indispo.flush();
        }
        if("EN_COURS".equals(indispo.getStatut().getCode())){
                //on update l'utilisateur
                this.updateUserModification(ids);
                //on ajoute l'anomalie indispoTeporaire
                this.setHydrantAnomalie(ids);
        }
           return this.insertIndisponibilitesTemporaires(ids,indispo);
    }

    private int insertIndisponibilitesTemporaires(ArrayList<Long> hydrants, HydrantIndispoTemporaire indispo) {
        Query query;
        int result = 0;
        for (Long hydrant : hydrants) {
            query = entityManager
                .createNativeQuery(
                    ("INSERT INTO remocra.hydrant_indispo_temporaire_hydrant(indisponibilite,hydrant) SELECT :indisponibilite, :hydrant"/* +
                        " WHERE NOT EXISTS ( SELECT 1 FROM remocra.hydrant_indispo_temporaire_hydrant WHERE hydrant = :hydrant)"*/))
                .setParameter("indisponibilite", indispo.getId())
                .setParameter("hydrant", hydrant);
                result = result + query.executeUpdate();
        }
        return result;
    }

    private int deleteHydrantsIndispo(HydrantIndispoTemporaire indispo) {
        Query query;
        int result = 0;
            query = entityManager
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
            Query query = entityManager
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
                query = entityManager
                    .createNativeQuery(
                        "insert into remocra.hydrant_anomalies(hydrant,anomalies) select :hydrantId, (select  id from remocra.type_hydrant_anomalie where code = 'INDISPONIBILITE_TEMP')"+
                            " WHERE NOT EXISTS (SELECT 1 FROM remocra.hydrant_anomalies WHERE hydrant = :hydrantId AND anomalies = (select  id from remocra.type_hydrant_anomalie where code = 'INDISPONIBILITE_TEMP'))")
                    .setParameter("hydrantId", id);
                result = result + query.executeUpdate();
            }return result;
        }return result;
    }



    @Transactional
    public int deleteHydrantAnomalie(ArrayList<Long> hydrants) {

        int result = 0;
        if (!hydrants.isEmpty()) {
            Query query;
            for (Long id : hydrants) {
                query = entityManager
                    .createNativeQuery(
                        "DELETE FROM remocra.hydrant_anomalies WHERE hydrant= :hydrantId AND anomalies = (SELECT id FROM remocra.type_hydrant_anomalie WHERE code = 'INDISPONIBILITE_TEMP')" +
                            " AND ((SELECT COUNT (statut) FROM remocra.hydrant_indispo_temporaire" +
                            " WHERE id IN (SELECT indisponibilite FROM remocra.hydrant_indispo_temporaire_hydrant WHERE hydrant = :hydrantId) AND statut = 1) =0)")
                    .setParameter("hydrantId", id);
                result = result + query.executeUpdate();
            }return result;
        }return result;
    }

    @Transactional
    public int updateUserModification(ArrayList<Long> hydrants) {
        int result = 0;
        if (!hydrants.isEmpty()) {
            Utilisateur userModification = serviceUtilisateur.getCurrentUtilisateur();
            Query query;
            for (Long id : hydrants) {
                query = entityManager
                    .createNativeQuery(
                        "UPDATE remocra.hydrant" +
                            " SET utilisateur_modification = :userModification" +
                            " WHERE id = :hydrantId")
                    .setParameter("userModification", userModification.getId()).setParameter("hydrantId", id);
                result = result + query.executeUpdate();
            }return result;
        }return result;
    }

}
