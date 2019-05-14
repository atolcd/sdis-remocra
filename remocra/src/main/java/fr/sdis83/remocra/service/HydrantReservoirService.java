package fr.sdis83.remocra.service;

import fr.sdis83.remocra.domain.remocra.HydrantReservoir;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HydrantReservoirService extends AbstractService<HydrantReservoir> {

    public HydrantReservoirService() {
        super(HydrantReservoir.class);
    }

}