package fr.sdis83.remocra.service;

import org.springframework.context.annotation.Configuration;

import fr.sdis83.remocra.domain.remocra.OldebLocataire;

@Configuration
public class OldebLocataireService extends AbstractService<OldebLocataire> {

    public OldebLocataireService() {
        super(OldebLocataire.class);
    }

}
