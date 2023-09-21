package fr.sdis83.remocra.web;

import fr.sdis83.remocra.domain.remocra.OldebLocataire;
import fr.sdis83.remocra.service.OldebLocataireService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/locataire")
@Controller
public class OldebLocataireController
    extends AbstractServiceableController<OldebLocataireService, OldebLocataire> {

  @Autowired private OldebLocataireService service;

  @Override
  protected OldebLocataireService getService() {
    return service;
  }
}
