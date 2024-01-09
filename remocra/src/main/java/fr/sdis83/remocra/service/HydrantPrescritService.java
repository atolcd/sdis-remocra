package fr.sdis83.remocra.service;

import fr.sdis83.remocra.domain.remocra.HydrantPrescrit;
import fr.sdis83.remocra.usecase.parametre.ParametreDataProvider;
import fr.sdis83.remocra.util.GeometryUtil;
import java.util.List;
import java.util.Map;
import javax.persistence.TypedQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Configuration
public class HydrantPrescritService extends AbstractService<HydrantPrescrit> {

  @Autowired private UtilisateurService utilisateurService;

  @Autowired private ParametreDataProvider parametreProvider;

  public HydrantPrescritService() {
    super(HydrantPrescrit.class);
  }

  public List<HydrantPrescrit> findAllHydrantPrescrits() {
    TypedQuery<HydrantPrescrit> query =
        entityManager
            .createQuery(
                "SELECT o FROM HydrantPrescrit o WHERE contains (:zoneCompetence, geometrie) = true",
                HydrantPrescrit.class)
            .setParameter(
                "zoneCompetence",
                utilisateurService
                    .getCurrentUtilisateur()
                    .getOrganisme()
                    .getZoneCompetence()
                    .getGeometrie());
    return query.getResultList();
  }

  public List<HydrantPrescrit> findHydrantPrescritsByBBOX(String bbox, int srid) {
    TypedQuery<HydrantPrescrit> query =
        entityManager
            .createQuery(
                "SELECT o FROM HydrantPrescrit o "
                    + "where contains (transform(:filter, :srid), geometrie) = true "
                    + "and contains (:zoneCompetence, geometrie) = true",
                HydrantPrescrit.class)
            .setParameter("filter", GeometryUtil.geometryFromBBox(bbox, srid))
            .setParameter(
                "zoneCompetence",
                utilisateurService
                    .getCurrentUtilisateur()
                    .getOrganisme()
                    .getZoneCompetence()
                    .getGeometrie())
            .setParameter("srid", srid);
    return query.getResultList();
  }

  @Transactional
  @Override
  public HydrantPrescrit setUpInformation(
      HydrantPrescrit attached, Map<String, MultipartFile> files, Object... params)
      throws Exception {
    // traitement géométrie
    attached.getGeometrie().setSRID(parametreProvider.get().getSridInt());
    if (attached.getOrganisme() == null) {
      attached.setOrganisme(utilisateurService.getCurrentUtilisateur().getOrganisme());
    }
    return attached;
  }
}
