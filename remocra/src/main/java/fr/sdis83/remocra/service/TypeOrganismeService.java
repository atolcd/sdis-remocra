package fr.sdis83.remocra.service;

import fr.sdis83.remocra.domain.remocra.TypeOrganisme;
import java.util.List;
import javax.persistence.Query;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TypeOrganismeService extends AbstractService<TypeOrganisme> {

  public TypeOrganismeService() {
    super(TypeOrganisme.class);
  }

  /**
   * Vérifie si le type d'organisme est attribué à un ou plusieurs organismes dont le parent est
   * configuré
   *
   * @param id L'ID du type d'organisme spécifique
   * @return INT le nombre d'organismes avec ce profil ayant un parent de configuré
   */
  public int nbOrganismesAvecParentEtProfilSpecifique(Long id) {
    Query query =
        entityManager
            .createNativeQuery(
                "SELECT CAST(COUNT(*) AS INTEGER) FROM remocra.organisme "
                    + "WHERE type_organisme = :id AND organisme_parent IS NOT NULL")
            .setParameter("id", id);
    List<Integer> response = query.getResultList();
    return response.get(0);
  }
}
