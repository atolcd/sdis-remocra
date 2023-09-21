package fr.sdis83.remocra.service;

import fr.sdis83.remocra.domain.remocra.HydrantPena;
import fr.sdis83.remocra.domain.remocra.HydrantVisite;
import fr.sdis83.remocra.util.GeometryUtil;
import java.util.List;
import java.util.Map;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.sql.DataSource;
import org.jboss.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Configuration
public class HydrantPenaService extends AbstractHydrantService<HydrantPena> {

  private final Logger logger = Logger.getLogger(getClass());

  @Autowired DataSource dataSource;

  @PersistenceContext protected EntityManager entityManager;

  public HydrantPenaService() {
    super(HydrantPena.class);
  }

  @Bean
  public HydrantPenaService hydrantPenaService() {
    return new HydrantPenaService();
  }

  @Override
  @Transactional
  public HydrantPena setUpInformation(
      HydrantPena attached, Map<String, MultipartFile> files, Object... params) throws Exception {
    super.setUpInformation(attached, files, params);
    // Calcul des coordonnées DFCI si nécessaire
    if (attached.getCoordDFCI() == null || attached.getCoordDFCI().isEmpty()) {
      try {
        String coordDFCI = GeometryUtil.findCoordDFCIFromGeom(dataSource, attached.getGeometrie());
        attached.setCoordDFCI(coordDFCI);
      } catch (Exception e) {
        logger.debug("Problème lors de la requête sur la table remocra_referentiel.carro_dfci", e);
      }
    }
    return attached;
  }

  @Transactional
  public boolean delete(Long id) throws Exception {
    // On supprime les aspirations associés
    Query query =
        entityManager
            .createNativeQuery(("DELETE FROM remocra.hydrant_aspiration WHERE pena = :id"))
            .setParameter("id", id);
    query.executeUpdate();
    List<HydrantVisite> listeVisites = HydrantVisite.findHydrantVisitesByHydrant(id);
    for (HydrantVisite visite : listeVisites) {
      visite.remove();
    }
    super.delete(id);
    return true;
  }

  @Transactional
  public boolean launchTrigger(Long id) throws Exception {
    // Pour déclencher le calcul des anomalies via trigger
    entityManager
        .createNativeQuery("update remocra.hydrant_pena set capacite=capacite where id=:id")
        .setParameter("id", id)
        .executeUpdate();
    return true;
  }
}
