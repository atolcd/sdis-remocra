package fr.sdis83.remocra.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import flexjson.JSONSerializer;
import fr.sdis83.remocra.domain.remocra.Organisme;
import fr.sdis83.remocra.service.OrganismeService;

@RequestMapping("/organismes")
@Controller
public class OrganismeController extends AbstractServiceableController<OrganismeService, Organisme> {

    @Autowired
    private OrganismeService service;

    @Override
    protected OrganismeService getService() {
        return service;
    }

    @Override
    protected JSONSerializer additionnalIncludeExclude(JSONSerializer serializer) {
        return serializer.exclude("data.zoneCompetence.geometrie");
    }
}