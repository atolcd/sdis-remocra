package fr.sdis83.remocra.usecase.parametre;

import fr.sdis83.remocra.repository.ParamConfRepository;
import fr.sdis83.remocra.repository.ParametreRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UpdateParametreUseCase {
  private final Logger logger = LoggerFactory.getLogger(getClass());

  @Autowired ParametreDataProvider parametreDataProvider;

  @Autowired ParametreRepository parametreRepository;

  @Autowired ParamConfRepository paramConfRepository;

  /**
   * Met à jour un paramètre en BDD, et invalide le cache.
   *
   * @param cle Clé du paramètre
   * @param valeur Valeur stringifiée du paramètre pour stockage dans une colonne TEXT
   */
  public void updateParamConf(String cle, String valeur) {
    paramConfRepository.updateParamConf(cle, valeur);
    logger.info("Invalidation des paramètres : enregistrement de ParamConf " + cle);
    parametreDataProvider.reloadParametres();
  }

  /**
   * Met à jour un paramètre en BDD, et invalide le cache. <br>
   * <b>La valeur sera stringifiée proprement par l'appelant pour garantir son stockage</b>
   *
   * @param cle Clé du paramètre
   * @param valeur Valeur stringifiée du paramètre pour stockage dans une colonne TEXT
   */
  public void updateParametre(String cle, String valeur) {
    parametreRepository.updateByKey(cle, valeur);
    logger.info("Invalidation des paramètres : enregistrement de Parametre " + cle);
    parametreDataProvider.reloadParametres();
  }
}
