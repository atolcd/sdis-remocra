package fr.sdis83.remocra.service;

import fr.sdis83.remocra.domain.remocra.ProfilOrganismeUtilisateurDroit;
import fr.sdis83.remocra.web.message.ItemSorting;
import java.util.ArrayList;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Root;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ProfilOrganismeUtilisateurDroitService
    extends AbstractService<ProfilOrganismeUtilisateurDroit> {

  public ProfilOrganismeUtilisateurDroitService() {
    super(ProfilOrganismeUtilisateurDroit.class);
  }

  @Override
  protected boolean processItemSortings(
      ArrayList<Order> orders,
      ItemSorting itemSorting,
      CriteriaBuilder cBuilder,
      Root<ProfilOrganismeUtilisateurDroit> from) {
    if ("profilOrganismeId".equals(itemSorting.getFieldName())) {
      Expression<String> cpPath = from.join("profilOrganisme").get("nom");
      orders.add(itemSorting.isDesc() ? cBuilder.desc(cpPath) : cBuilder.asc(cpPath));
      return true;
    } else if ("profilUtilisateurId".equals(itemSorting.getFieldName())) {
      Expression<String> cpPath = from.join("profilUtilisateur").get("nom");
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
