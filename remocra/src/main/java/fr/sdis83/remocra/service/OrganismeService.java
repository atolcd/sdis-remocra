package fr.sdis83.remocra.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.log4j.Logger;
import org.springframework.context.annotation.Configuration;

import fr.sdis83.remocra.domain.remocra.Organisme;
import fr.sdis83.remocra.web.message.ItemFilter;
import fr.sdis83.remocra.web.message.ItemSorting;
import org.springframework.transaction.annotation.Transactional;

@Configuration
public class OrganismeService extends AbstractService<Organisme> {

    private final Logger logger = Logger.getLogger(getClass());

    public OrganismeService() {
        super(Organisme.class);
    }

    @Override
    protected Predicate processFilterItem(CriteriaQuery<?> itemQuery, Map<String, Object> parameters, Root<Organisme> from, ItemFilter itemFilter) {
        CriteriaBuilder cBuilder = this.getCriteriaBuilder();
        Predicate predicat = null;
        if ("id".equals(itemFilter.getFieldName())) {
            Expression<String> cpPath = from.get("id");
            predicat = cBuilder.equal(cpPath, itemFilter.getValue());
        } else if ("query".equals(itemFilter.getFieldName())) {
            Expression<String> cpPath = from.get("nom");
            predicat = cBuilder.like(cBuilder.upper(cpPath), "%" + itemFilter.getValue().toUpperCase() + "%");
        } else {
            logger.info("processFilterItem non traité " + itemFilter.getFieldName() + " (" + itemFilter.getValue() + ")");
        }
        return predicat;
    }

    @Override
    protected boolean processItemSortings(ArrayList<Order> orders, ItemSorting itemSorting, CriteriaBuilder cBuilder, Root<Organisme> from) {
        if ("typeOrganismeId".equals(itemSorting.getFieldName())) {
            Expression<String> cpPath = from.join("typeOrganisme").get("nom");
            orders.add(itemSorting.isDesc() ? cBuilder.desc(cpPath) : cBuilder.asc(cpPath));
            return true;
        } else if ("profilOrganismeId".equals(itemSorting.getFieldName())) {
            Expression<String> cpPath = from.join("profilOrganisme").get("nom");
            orders.add(itemSorting.isDesc() ? cBuilder.desc(cpPath) : cBuilder.asc(cpPath));
            return true;
        } else if ("zoneCompetenceId".equals(itemSorting.getFieldName())) {
            Expression<String> cpPath = from.join("zoneCompetence").get("nom");
            orders.add(itemSorting.isDesc() ? cBuilder.desc(cpPath) : cBuilder.asc(cpPath));
            return true;
        } else if ("organismeParentId".equals(itemSorting.getFieldName())) {
            Expression<String> cpPath = from.join("organismeParent").get("nom");
            orders.add(itemSorting.isDesc() ? cBuilder.desc(cpPath) : cBuilder.asc(cpPath));
            return true;
        } else {
            return super.processItemSortings(orders, itemSorting, cBuilder, from);
        }
    }

    /**
     * Désaffecte l'organisme parent de tous les organismes correspondant à un type d'organisme spécifique
     * @param idTypeOrganisme L'ID du type d'organisme spécifique
     */
    @Transactional
    public int removeOrganismeParentForSpecificType(Long idTypeOrganisme){
        Query query = entityManager
                .createNativeQuery(
                        ("UPDATE remocra.organisme SET organisme_parent=NULL WHERE type_organisme=(:idTypeOrganisme)"))
                .setParameter("idTypeOrganisme", idTypeOrganisme);
        return query.executeUpdate();
    }

    /**
     * Renvoie le nombre d'organismes enfants pour un organisme donné
     * @param id L'ID de l'organisme parent
     * @return INT le nombre d'organismes enfants
     */
    public int nbOrganismesAvecParentEtProfilSpecifique(Long id){
        Query query = entityManager.createNativeQuery("SELECT CAST(COUNT(*) AS INTEGER) FROM remocra.organisme " +
                "WHERE organisme_parent = :id")
                .setParameter("id", id);
        List<Integer> response = query.getResultList();
        return response.get(0);
    }

    /**
     * Désaffecte les organismes parent d'un organisme spécifique
     * @param idOrganisme L'ID de l'organisme parent
     */
    @Transactional
    public int removeOrganismeParentForSpecificParent(Long idOrganisme){
        Query query = entityManager
                .createNativeQuery(
                        ("UPDATE remocra.organisme SET organisme_parent=NULL WHERE organisme_parent=(:idOrganisme)"))
                .setParameter("idOrganisme", idOrganisme);
        return query.executeUpdate();
    }

}
