package fr.sdis83.remocra.service;

import org.springframework.context.annotation.Configuration;

import fr.sdis83.remocra.domain.remocra.TypeDroit;

@Configuration
public class TypeDroitService extends AbstractService<TypeDroit> {

    public TypeDroitService() {
        super(TypeDroit.class);
    }
}
