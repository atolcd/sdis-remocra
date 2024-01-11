package fr.sdis83.remocra.usecase.etude;

import fr.sdis83.remocra.db.model.remocra.tables.pojos.Commune;
import fr.sdis83.remocra.repository.EtudeRepository;
import fr.sdis83.remocra.util.ListWithCountData;
import fr.sdis83.remocra.web.message.ItemFilter;
import fr.sdis83.remocra.web.message.ItemSorting;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.jooq.Condition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EtudeUseCase {

  @Autowired private EtudeRepository etudeRepository;

  public EtudeUseCase() {}

  public ListWithCountData<EtudeData> getAll(
      List<ItemFilter> itemFilters,
      Integer limit,
      Integer start,
      List<ItemSorting> itemSortings,
      long idOrganismeUtilisateur) {

    // Etudes créées par son organisme d’appartenance de l’utilisateur connecté ou un de ses enfants
    Collection<Integer> organismesAppartenance =
        fr.sdis83.remocra.domain.remocra.Organisme.getOrganismeAndChildren(
            (int) idOrganismeUtilisateur);
    Condition condition =
        etudeRepository.getFilters(itemFilters, idOrganismeUtilisateur, organismesAppartenance);

    Collection<EtudeData> etudes =
        etudeRepository.getAll(
            limit, start, itemSortings.isEmpty() ? null : itemSortings.get(0), condition);

    // TODO refaire avec les streams
    List<Long> listIdEtude = new ArrayList<>();
    for (EtudeData etude : etudes) {
      listIdEtude.add(etude.getId());
    }
    Map<Long, List<fr.sdis83.remocra.db.model.remocra.tables.pojos.Commune>> communesByEtude =
        etudeRepository.getCommunesByEtude(listIdEtude);

    Map<Long, List<DocumentEtudeData>> documentsByEtude =
        etudeRepository.getDocumentsByEtude(listIdEtude);

    Collection<Long> idsEtudeWithReseauImporte = etudeRepository.getEtudeIdsWithReseauImporte();

    for (EtudeData e : etudes) {
      List<Commune> communes = communesByEtude.get(e.getId());
      List<DocumentEtudeData> documents = documentsByEtude.get(e.getId());
      if (communes != null) {
        e.setCommunes(communes);
      } else {
        e.setCommunes(Collections.emptyList());
      }

      if (documents != null) {
        e.setDocuments(documents);
      } else {
        e.setDocuments(Collections.emptyList());
      }

      e.setReseauImporte(idsEtudeWithReseauImporte.contains(e.getId()));
      e.setReadOnly(organismesAppartenance.contains(e.getOrganisme()));
    }

    return new ListWithCountData<>(etudes, etudeRepository.count(condition));
  }
}
