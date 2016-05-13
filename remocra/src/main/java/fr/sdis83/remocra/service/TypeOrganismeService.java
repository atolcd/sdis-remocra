package fr.sdis83.remocra.service;

import org.springframework.context.annotation.Configuration;

import fr.sdis83.remocra.domain.remocra.TypeOrganisme;

@Configuration
public class TypeOrganismeService extends AbstractService<TypeOrganisme> {

    public TypeOrganismeService() {
        super(TypeOrganisme.class);
    }
}
