package fr.sdis83.remocra.web;

import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import flexjson.JSONSerializer;
import fr.sdis83.remocra.db.model.remocra.tables.pojos.ProcessusEtl;
import fr.sdis83.remocra.db.model.remocra.tables.pojos.ProcessusEtlModele;
import fr.sdis83.remocra.db.model.remocra.tables.pojos.ProcessusEtlModeleParametre;
import fr.sdis83.remocra.domain.remocra.RemocraVueCombo;
import fr.sdis83.remocra.repository.ExportRepository;
import fr.sdis83.remocra.repository.ProcessusEtlModeleParametereRepository;
import fr.sdis83.remocra.repository.ProcessusEtlModeleRepository;
import fr.sdis83.remocra.util.DocumentUtil;
import fr.sdis83.remocra.web.message.ItemFilter;
import fr.sdis83.remocra.web.serialize.ext.AbstractExtListSerializer;
import fr.sdis83.remocra.web.serialize.ext.SuccessErrorExtSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartHttpServletRequest;

@RequestMapping("/exports")
@Controller
public class ExportController {


    @Autowired
    private ProcessusEtlModeleRepository processusEtlModeleRepository;

  @Autowired
  private ProcessusEtlModeleParametereRepository processusEtlModeleParametereRepository;


@Autowired
private ExportRepository exportRepository;


  @RequestMapping(value = "/{code}")
  @PreAuthorize("hasRight('TRAITEMENTS_C') or hasRight('IMPORT_CTP')")
  public void downloadExportDocument(@PathVariable("code") String code, HttpServletResponse response)
      throws IOException {
    String path = exportRepository.getExportFilePathFromCode(code);
    DocumentUtil.getInstance().downloadDocument(path, code, response);
    // Accusé de réception
    exportRepository.setExportAccuseFromCode(code);
  }
}
