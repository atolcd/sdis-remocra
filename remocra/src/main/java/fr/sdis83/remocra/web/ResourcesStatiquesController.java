package fr.sdis83.remocra.web;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/** Proxy vers des ressources externes "static". */
@RequestMapping("/static")
@Controller
public class ResourcesStatiquesController extends AbstractProxyResourcesController {

  private final Logger logger = Logger.getLogger(getClass());

  @Override
  String getUriBasePath() {
    return ".*/static";
  }

  @Override
  String getFileBasePath() {
    return System.getProperty("client-ng.dir", "/usr/share/tomcat6/webapps/remocra/static/");
  }
}
