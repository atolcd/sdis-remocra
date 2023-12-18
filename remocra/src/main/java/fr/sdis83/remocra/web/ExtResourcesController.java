package fr.sdis83.remocra.web;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/** Proxy vers des ressources externes "ext-res" (paramètre DOSSIER_RESSOURCES_EXTERNES). */
@RequestMapping("/ext-res")
@Controller
public class ExtResourcesController extends AbstractProxyResourcesController {

  private final Logger logger = Logger.getLogger(getClass());

  @Override
  String getUriBasePath() {
    return ".*/ext-res";
  }

  @Override
  String getFileBasePath() {
    return parametreProvider.get().getDossierRessourcesExternes();
  }
}
