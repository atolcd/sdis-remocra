package fr.sdis83.remocra.web;

import fr.sdis83.remocra.domain.remocra.Droit;
import fr.sdis83.remocra.service.DroitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/droits")
@Controller
public class DroitController extends AbstractServiceableController<DroitService, Droit> {

  @Autowired private DroitService service;

  @Override
  protected DroitService getService() {
    return service;
  }

  @Override
  protected String getConstraintViolationExceptionMsg() {
    return "Un droit existe déjà pour ce profil et ce type de droit.<br/>Veuillez modifier votre saisie.";
  }
}
