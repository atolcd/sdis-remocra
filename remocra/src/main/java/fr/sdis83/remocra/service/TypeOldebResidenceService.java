package fr.sdis83.remocra.service;

import org.springframework.context.annotation.Configuration;

import fr.sdis83.remocra.domain.remocra.TypeOldebResidence;

@Configuration
public class TypeOldebResidenceService extends AbstractService<TypeOldebResidence> {

    public TypeOldebResidenceService() {
        super(TypeOldebResidence.class);
    }
}
