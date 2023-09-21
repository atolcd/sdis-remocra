package fr.sdis83.remocra.web;

import fr.sdis83.remocra.service.TelechargementService;
import fr.sdis83.remocra.util.DocumentUtil;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/telechargement")
@Controller
public class TelechargementController {

  @Autowired TelechargementService telechargementsService;

  @RequestMapping(value = "/{code}")
  public void downloadPdiDocument(@PathVariable("code") String code, HttpServletResponse response)
      throws IOException {
    DocumentUtil.getInstance()
        .downloadDocument(telechargementsService.getPdiFilePathFromCode(code), code, response);
  }

  @RequestMapping(value = "/document/{code}")
  public void downloadRemocraDocument(
      @PathVariable("code") String code, HttpServletResponse response) throws IOException {
    DocumentUtil.getInstance()
        .downloadDocument(telechargementsService.getRemocraFilePathFromCode(code), code, response);
  }

  @RequestMapping(value = "/show/{code}")
  public void showRemocraDocument(@PathVariable("code") String code, HttpServletResponse response)
      throws IOException {
    DocumentUtil.getInstance()
        .showDocument(telechargementsService.getRemocraFilePathFromCode(code), code, response);
  }
}
