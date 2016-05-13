package fr.sdis83.remocra.service;

import java.util.List;
import java.util.Map;

import javax.persistence.TypedQuery;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import fr.sdis83.remocra.domain.remocra.HydrantPrescrit;
import fr.sdis83.remocra.util.GeometryUtil;

@Configuration
public class HydrantPrescritService extends AbstractService<HydrantPrescrit> {

    @Autowired
    private UtilisateurService utilisateurService;

    public HydrantPrescritService() {
        super(HydrantPrescrit.class);
    }

    public List<HydrantPrescrit> findAllHydrantPrescrits() {
        return HydrantPrescrit.findAllHydrantPrescrits();
    }

    public List<HydrantPrescrit> findHydrantPrescritsByBBOX(String bbox) {
        TypedQuery<HydrantPrescrit> query = entityManager.createQuery("SELECT o FROM HydrantPrescrit o where dwithin (geometrie, transform(:filter, 2154), 0) = true",
                HydrantPrescrit.class).setParameter("filter", GeometryUtil.geometryFromBBox(bbox));
        return query.getResultList();
    }

    @Transactional
    @Override
    public HydrantPrescrit setUpInformation(HydrantPrescrit attached, Map<String, MultipartFile> files, Object... params) throws Exception {
        // traitement géométrie
        attached.getGeometrie().setSRID(2154);
        if (attached.getOrganisme() == null) {
            attached.setOrganisme(utilisateurService.getCurrentUtilisateur().getOrganisme());
        }
        return attached;
    }

}
