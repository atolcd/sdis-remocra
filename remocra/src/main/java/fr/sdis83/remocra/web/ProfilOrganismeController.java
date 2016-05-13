package fr.sdis83.remocra.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import fr.sdis83.remocra.domain.remocra.ProfilOrganisme;
import fr.sdis83.remocra.service.ProfilOrganismeService;

@RequestMapping("/profilorganismes")
@Controller
public class ProfilOrganismeController extends AbstractServiceableController<ProfilOrganismeService, ProfilOrganisme> {

    @Autowired
    private ProfilOrganismeService service;

    @Override
    protected ProfilOrganismeService getService() {
        return service;
    }
}
