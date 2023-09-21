package fr.sdis83.remocra.web;

import fr.sdis83.remocra.domain.remocra.ProfilUtilisateur;
import fr.sdis83.remocra.service.ProfilUtilisateurService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/profilutilisateurs")
@Controller
public class ProfilUtilisateurController
    extends AbstractServiceableController<ProfilUtilisateurService, ProfilUtilisateur> {

  @Autowired private ProfilUtilisateurService service;

  @Override
  protected ProfilUtilisateurService getService() {
    return service;
  }
}
