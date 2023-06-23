package fr.sdis83.remocra.service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Root;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import fr.sdis83.remocra.domain.remocra.TypeHydrantAnomalie;
import fr.sdis83.remocra.domain.remocra.TypeHydrantAnomalieNature;
import fr.sdis83.remocra.web.message.ItemSorting;

@Configuration
public class TypeHydrantAnomalieService extends AbstractService<TypeHydrantAnomalie> {

    // private final Logger logger = Logger.getLogger(getClass());

    public TypeHydrantAnomalieService() {
        super(TypeHydrantAnomalie.class);
    }

    @Bean
    public TypeHydrantAnomalieService typeHydrantAnomalieService() {
        return new TypeHydrantAnomalieService();
    }

    @Override
    protected boolean processItemSortings(ArrayList<Order> orders, ItemSorting itemSorting, CriteriaBuilder cBuilder, Root<TypeHydrantAnomalie> from) {
        if ("critere_code".equals(itemSorting.getFieldName())) {
            Expression<String> cpPath = from.join("critere", JoinType.LEFT).get("code");
            orders.add(itemSorting.isDesc() ? cBuilder.desc(cpPath) : cBuilder.asc(cpPath));
            return true;
        }
        return super.processItemSortings(orders, itemSorting, cBuilder, from);
    }

    @Override
    protected void beforeDelete(TypeHydrantAnomalie attached) {
        Iterator<TypeHydrantAnomalieNature> it = attached.getAnomalieNatures().iterator();
        while (it.hasNext()) {
            TypeHydrantAnomalieNature t = it.next();
            t.getSaisies().clear();
        }
        attached.getAnomalieNatures().clear();
    }

    public boolean checkCodeExist(String code) {
             return  TypeHydrantAnomalie.findTypeHydrantAnomaliesByCode(code).getResultList().size() != 0;
    }

}
