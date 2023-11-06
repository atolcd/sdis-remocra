package fr.sdis83.remocra.usecase.parametre.agents;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.sdis83.remocra.GlobalConstants;
import fr.sdis83.remocra.db.model.remocra.enums.TypeGestionAgent;
import fr.sdis83.remocra.repository.ParametreRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AgentsUseCase {

  @Autowired ParametreRepository parametreRepository;

  /**
   * Renvoie le type d'agent sélectionné en fonction des paramètres de l'application.
   *
   * @return Une chaîne de caractères représentant le type d'agent sélectionné.
   */
  public String getTypeAgentsSelected() {

    return parametreRepository.getByCle(GlobalConstants.PARAMETRE_AGENTS).getValeurParametre();
  }

  /**
   * Renvoie une représentation JSON des types de gestion d'agents disponibles.
   *
   * @return Une chaîne JSON représentant les types de gestion d'agents.
   * @throws JsonProcessingException si une exception survient lors de la conversion en JSON.
   */
  public String getTypeAgentsSelectable() throws JsonProcessingException {
    return new ObjectMapper().writeValueAsString(TypeGestionAgent.values());
  }
}
