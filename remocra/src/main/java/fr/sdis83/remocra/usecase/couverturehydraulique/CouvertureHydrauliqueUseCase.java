package fr.sdis83.remocra.usecase.couverturehydraulique;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.sdis83.remocra.repository.CouvertureHydrauliqueRepository;
import fr.sdis83.remocra.usecase.parametre.ParametreDataProvider;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CouvertureHydrauliqueUseCase {

  @Autowired CouvertureHydrauliqueRepository couvertureHydrauliqueRepository;

  @Autowired protected ParametreDataProvider parametreDataProvider;

  private static final Logger logger = LoggerFactory.getLogger(CouvertureHydrauliqueUseCase.class);
  private final ObjectMapper objectMapper = new ObjectMapper();

  public CouvertureHydrauliqueUseCase() {}

  public void calcul(
      String hydrantsExistants,
      String hydrantsProjet,
      Long idEtude,
      Boolean useReseauImporte,
      Boolean useReseauImporteWithCourant) {
    List<Integer> listPei = new ArrayList<>();
    List<Integer> listPeiProjet = new ArrayList<>();
    try {
      Map<String, List<Integer>> existants =
          objectMapper.readValue(
              hydrantsExistants, new TypeReference<HashMap<String, ArrayList<Integer>>>() {});
      listPei = existants.get("hydrants");
    } catch (IOException e) {
      logger.error(
          "Couverture hydraulique : Impossible d'extraire les ids des hydrants "
              + hydrantsExistants);
    }

    try {
      Map<String, List<Integer>> projets =
          objectMapper.readValue(
              hydrantsProjet, new TypeReference<HashMap<String, ArrayList<Integer>>>() {});
      listPeiProjet = projets.get("projets");
    } catch (IOException e) {
      logger.error(
          "Couverture hydraulique : Impossible d'extraire les ids des PEI en projet "
              + hydrantsProjet);
    }

    Long reseauImporte = useReseauImporte ? idEtude : null;

    couvertureHydrauliqueRepository.deleteCouverture(idEtude);

    int profondeurCouverture = parametreDataProvider.get().getProfondeurCouverture();
    int distanceMaxParcours = parametreDataProvider.get().getDeciDistanceMaxParcours();

    List<Integer> distances = new ArrayList<>();
    for (String s :
        parametreDataProvider.get().getDeciIsodistances().replaceAll(" ", "").split(",")) {
      distances.add(Integer.parseInt(s));
    }

    couvertureHydrauliqueRepository.executeInsererJoinctionPei(
        distanceMaxParcours, reseauImporte, listPei, listPeiProjet, useReseauImporteWithCourant);

    couvertureHydrauliqueRepository.executeParcoursCouverture(
        reseauImporte,
        idEtude,
        distances,
        listPei,
        listPeiProjet,
        profondeurCouverture,
        useReseauImporteWithCourant);

    couvertureHydrauliqueRepository.executeCouvertureHydrauliqueZonage(
        idEtude, distances, listPei, listPeiProjet, profondeurCouverture);

    couvertureHydrauliqueRepository.executeRetirerJonctionPei();
  }
}
