package fr.sdis83.remocra.web;

import fr.sdis83.remocra.domain.remocra.OldebPropriete;
import fr.sdis83.remocra.service.OldebProprieteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/propriete")
@Controller
public class OldebProprieteController
    extends AbstractServiceableController<OldebProprieteService, OldebPropriete> {

  @Autowired private OldebProprieteService service;

  @Override
  protected OldebProprieteService getService() {
    return service;
  }
}
