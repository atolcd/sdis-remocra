package fr.sdis83.remocra.web;

import fr.sdis83.remocra.repository.ExportRepository;
import fr.sdis83.remocra.repository.ProcessusEtlModeleParametereRepository;
import fr.sdis83.remocra.repository.ProcessusEtlModeleRepository;
import fr.sdis83.remocra.util.DocumentUtil;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/exports")
@Controller
public class ExportController {

  @Autowired private ProcessusEtlModeleRepository processusEtlModeleRepository;

  @Autowired private ProcessusEtlModeleParametereRepository processusEtlModeleParametereRepository;

  @Autowired private ExportRepository exportRepository;

  @RequestMapping(value = "/{code}")
  @PreAuthorize("hasRight('TRAITEMENTS_C') or hasRight('IMPORT_CTP')")
  public void downloadExportDocument(
      @PathVariable("code") String code, HttpServletResponse response) throws IOException {
    String path = exportRepository.getExportFilePathFromCode(code);
    DocumentUtil.getInstance().downloadDocument(path, code, response);
    // Accusé de réception
    exportRepository.setExportAccuseFromCode(code);
  }
}
