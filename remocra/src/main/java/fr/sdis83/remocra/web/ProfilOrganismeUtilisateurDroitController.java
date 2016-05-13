package fr.sdis83.remocra.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import fr.sdis83.remocra.domain.remocra.ProfilOrganismeUtilisateurDroit;
import fr.sdis83.remocra.service.ProfilOrganismeUtilisateurDroitService;

@RequestMapping("/proorgutidroits")
@Controller
public class ProfilOrganismeUtilisateurDroitController extends AbstractServiceableController<ProfilOrganismeUtilisateurDroitService, ProfilOrganismeUtilisateurDroit> {

    @Autowired
    private ProfilOrganismeUtilisateurDroitService service;

    @Override
    protected ProfilOrganismeUtilisateurDroitService getService() {
        return service;
    }

    @Override
    protected String getConstraintViolationExceptionMsg() {
        return "Un enregistrement existe déjà pour ce profil organisme et ce profil utilisateur.<br/>Veuillez modifier votre saisie.";
    }
}
