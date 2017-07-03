package fr.sdis83.remocra.service;

import java.util.Map;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.context.annotation.Configuration;

import fr.sdis83.remocra.domain.remocra.CadastreSection;
import fr.sdis83.remocra.web.message.ItemFilter;

@Configuration
public class CadastreSectionService extends AbstractService<CadastreSection> {

    public CadastreSectionService() {
        super(CadastreSection.class);
    }

    @Override
    protected Predicate processFilterItem(CriteriaQuery<?> itemQuery, Map<String, Object> parameters, Root<CadastreSection> from, ItemFilter itemFilter) {
        if ("communeId".equals(itemFilter.getFieldName())) {
            return communeFieldPredicate(from, itemFilter);

        } else if ("numero".equals(itemFilter.getFieldName())) {
            if (itemFilter.getValue() != null) {
                return makeLikeFilterPredicate(from, itemFilter, "numero");
            }
        }
        return super.processFilterItem(itemQuery, parameters, from, itemFilter);
    }

    /**
     * Filtre suivant l'id de la commune
     *
     * @param from
     * @param itemFilter
     * @return
     */
    private Predicate communeFieldPredicate(Root<CadastreSection> from, ItemFilter itemFilter) {
        CriteriaBuilder cBuilder = CadastreSection.entityManager().getCriteriaBuilder();
        Path<String> commune = from.get("commune").get("id");
        Predicate equal = cBuilder.equal(commune, itemFilter.getValue());
        return equal;
    }

}
