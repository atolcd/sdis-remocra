package fr.sdis83.remocra.service;

import java.util.ArrayList;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Root;

import org.springframework.context.annotation.Configuration;

import fr.sdis83.remocra.domain.remocra.Droit;
import fr.sdis83.remocra.web.message.ItemSorting;

@Configuration
public class DroitService extends AbstractService<Droit> {

    public DroitService() {
        super(Droit.class);
    }

    @Override
    protected boolean processItemSortings(ArrayList<Order> orders, ItemSorting itemSorting, CriteriaBuilder cBuilder, Root<Droit> from) {
        if ("typeDroitId".equals(itemSorting.getFieldName())) {
            Expression<String> cpPath = from.join("typeDroit").get("nom");
            orders.add(itemSorting.isDesc() ? cBuilder.desc(cpPath) : cBuilder.asc(cpPath));
            return true;
        } else if ("profilDroitId".equals(itemSorting.getFieldName())) {
            Expression<String> cpPath = from.join("profilDroit").get("nom");
            orders.add(itemSorting.isDesc() ? cBuilder.desc(cpPath) : cBuilder.asc(cpPath));
            return true;
        } else {
            return super.processItemSortings(orders, itemSorting, cBuilder, from);
        }
    }
}
