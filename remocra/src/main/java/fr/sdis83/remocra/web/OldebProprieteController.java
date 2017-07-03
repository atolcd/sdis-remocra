package fr.sdis83.remocra.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import fr.sdis83.remocra.domain.remocra.OldebPropriete;
import fr.sdis83.remocra.service.OldebProprieteService;

@RequestMapping("/propriete")
@Controller
public class OldebProprieteController extends AbstractServiceableController<OldebProprieteService, OldebPropriete> {

    @Autowired
    private OldebProprieteService service;

    @Override
    protected OldebProprieteService getService() {
        return service;
    }
}
