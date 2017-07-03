package fr.sdis83.remocra.service;

import org.springframework.context.annotation.Configuration;

import fr.sdis83.remocra.domain.remocra.OldebPropriete;

@Configuration
public class OldebProprieteService extends AbstractService<OldebPropriete> {

    public OldebProprieteService() {
        super(OldebPropriete.class);
    }

}
