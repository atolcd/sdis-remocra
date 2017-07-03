package fr.sdis83.remocra.service;

import java.util.Map;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.context.annotation.Configuration;

import fr.sdis83.remocra.domain.remocra.CadastreParcelle;
import fr.sdis83.remocra.web.message.ItemFilter;

@Configuration
public class CadastreParcelleService extends AbstractService<CadastreParcelle> {

    public CadastreParcelleService() {
        super(CadastreParcelle.class);
    }

    @Override
    protected Predicate processFilterItem(CriteriaQuery<?> itemQuery, Map<String, Object> parameters, Root<CadastreParcelle> from, ItemFilter itemFilter) {
        // Filtrage par section
        if ("sectionId".equals(itemFilter.getFieldName())) {
            return sectionFieldPredicate(from, itemFilter);
        } else if ("numero".equals(itemFilter.getFieldName())) {
            return makeLikeFilterPredicate(from, itemFilter, "numero");
        }
        return super.processFilterItem(itemQuery, parameters, from, itemFilter);
    }

    /**
     * Filtre suivant l'id de la section
     *
     * @param from
     * @param itemFilter
     * @return
     */
    private Predicate sectionFieldPredicate(Root<CadastreParcelle> from, ItemFilter itemFilter) {
        CriteriaBuilder cBuilder = CadastreParcelle.entityManager().getCriteriaBuilder();
        Path<String> section = from.get("section").get("id");
        return cBuilder.equal(section, itemFilter.getValue());
    }

}
