package fr.sdis83.remocra.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import fr.sdis83.remocra.domain.remocra.TypeOrganisme;
import fr.sdis83.remocra.service.TypeOrganismeService;

@RequestMapping("/typeorganisme")
@Controller
public class TypeOrganismeController extends AbstractServiceableController<TypeOrganismeService, TypeOrganisme> {

    @Autowired
    private TypeOrganismeService service;

    @Override
    protected TypeOrganismeService getService() {
        return service;
    }
}
