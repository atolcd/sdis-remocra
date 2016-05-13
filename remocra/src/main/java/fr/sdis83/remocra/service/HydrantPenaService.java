package fr.sdis83.remocra.service;

import java.util.Map;

import javax.sql.DataSource;

import org.jboss.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import fr.sdis83.remocra.domain.remocra.HydrantPena;
import fr.sdis83.remocra.util.GeometryUtil;

@Configuration
public class HydrantPenaService extends AbstractHydrantService<HydrantPena> {

    private final Logger logger = Logger.getLogger(getClass());

    @Autowired
    DataSource dataSource;

    public HydrantPenaService() {
        super(HydrantPena.class);
    }

    @Bean
    public HydrantPenaService hydrantPenaService() {
        return new HydrantPenaService();
    }

    @Override
    @Transactional
    public HydrantPena setUpInformation(HydrantPena attached, Map<String, MultipartFile> files, Object... params) throws Exception {
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
}
