package fr.sdis83.remocra.service;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import flexjson.JSONDeserializer;
import fr.sdis83.remocra.domain.remocra.HydrantAspiration;
import org.springframework.context.annotation.Configuration;

import fr.sdis83.remocra.web.message.ItemFilter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class HydrantAspirationService extends AbstractService<HydrantAspiration> {

    public HydrantAspirationService() {
        super(HydrantAspiration.class);
    }

    @Override
    protected Predicate processFilterItem(CriteriaQuery<?> itemQuery, Map<String, Object> parameters, Root<HydrantAspiration> from, ItemFilter itemFilter) {
        CriteriaBuilder cBuilder = this.getCriteriaBuilder();
        Predicate predicat = null;
        if ("id".equals(itemFilter.getFieldName())) {
            Expression<String> cpPath = from.get("id");
            predicat = cBuilder.equal(cpPath, itemFilter.getValue());
        } else if ("pena".equals(itemFilter.getFieldName())) {
            Expression<String> cpPath = from.get("pena").get("id");
            predicat = cBuilder.equal(cpPath, itemFilter.getValue());
        }
        return predicat;
    }

}
