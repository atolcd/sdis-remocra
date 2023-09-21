package fr.sdis83.remocra.web;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/** Proxy vers des courriers externes "ext-res" (paramètre DOSSIER_COURRIER). */
@RequestMapping("/ext-courrier")
@Controller
public class ExtCourriersController extends AbstractProxyResourcesController {

  private final Logger logger = Logger.getLogger(getClass());

  @Override
  String getUriBasePath() {
    return ".*/ext-courrier";
  }

  @Override
  String getFileBasePath() {
    return paramConfService.getDossierCourriersExternes();
  }
}
