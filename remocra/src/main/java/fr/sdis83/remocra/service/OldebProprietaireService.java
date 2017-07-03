package fr.sdis83.remocra.service;

import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.Parameter;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.ParameterExpression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.context.annotation.Configuration;

import fr.sdis83.remocra.domain.remocra.OldebProprietaire;
import fr.sdis83.remocra.web.message.ItemFilter;

@Configuration
public class OldebProprietaireService extends AbstractService<OldebProprietaire> {

    public OldebProprietaireService() {
        super(OldebProprietaire.class);
    }

    @Override
    protected Predicate processFilterItem(CriteriaQuery<?> itemQuery, Map<String, Object> parameters, Root<OldebProprietaire> from, ItemFilter itemFilter) {
        // Filtrage par section
        if ("nomProprietaire".equals(itemFilter.getFieldName())) {

            // Création du prédicat de recherche
            EntityManager em = OldebProprietaire.entityManager();
            CriteriaBuilder cBuilder = em.getCriteriaBuilder();
            // Construction du prédicat de recherche phonétique
            Expression<String> nomPath = from.get("nom");
            Expression<String> lowerNomPath = cBuilder.lower(nomPath);
            ParameterExpression<String> nomprocheExpr = cBuilder.parameter(String.class);
            parameters.put("nomProprietaire", nomprocheExpr);
            // Soundex
            Expression<String> soundexDb = cBuilder.function("soundexfr", String.class, lowerNomPath);
            Expression<String> soundexTest = cBuilder.function("soundexfr", String.class, nomprocheExpr);
            Predicate soundexPredicate = cBuilder.equal(soundexDb, soundexTest);
            // Jaro winkler
            Expression<Double> jaroCall = cBuilder.function("jarowinkler", Double.class, lowerNomPath, nomprocheExpr);
            Predicate jaroPredicate = cBuilder.greaterThanOrEqualTo(jaroCall, 0.75d);
            // Soundex OU Jaro winkler
            Predicate phoneticPredicate = cBuilder.or(soundexPredicate, jaroPredicate);
            return phoneticPredicate;
        }
        return super.processFilterItem(itemQuery, parameters, from, itemFilter);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    protected void manageParameters(Query query, Map<String, Object> parameters, List<ItemFilter> itemFilters) {
        ItemFilter nomFilter = ItemFilter.getFilter(itemFilters, "nomProprietaire");
        if (nomFilter != null) {
            String nomProprietaire = nomFilter.getValue().toLowerCase();
            query.setParameter((Parameter) parameters.get("nomProprietaire"), nomProprietaire);

        }
    }
}
