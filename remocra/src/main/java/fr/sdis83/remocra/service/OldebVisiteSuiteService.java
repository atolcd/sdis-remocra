package fr.sdis83.remocra.service;

import java.util.Map;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.context.annotation.Configuration;

import fr.sdis83.remocra.domain.remocra.OldebVisiteSuite;
import fr.sdis83.remocra.web.message.ItemFilter;

@Configuration
public class OldebVisiteSuiteService extends AbstractService<OldebVisiteSuite> {

    public OldebVisiteSuiteService() {
        super(OldebVisiteSuite.class);
    }

    @Override
    protected Predicate processFilterItem(CriteriaQuery<?> itemQuery, Map<String, Object> parameters, Root<OldebVisiteSuite> from, ItemFilter itemFilter) {
        // Filtrage par visite
        if ("visite".equals(itemFilter.getFieldName())) {
            return visiteFieldPredicate(from, itemFilter);
        }
        return super.processFilterItem(itemQuery, parameters, from, itemFilter);
    }

    private Predicate visiteFieldPredicate(Root<OldebVisiteSuite> from, ItemFilter itemFilter) {
        // Création du prédicat de recherche
        CriteriaBuilder cBuilder = OldebVisiteSuite.entityManager().getCriteriaBuilder();
        Path<String> visite = from.get("visite");
        return cBuilder.equal(visite, itemFilter.getValue());
    }

}
