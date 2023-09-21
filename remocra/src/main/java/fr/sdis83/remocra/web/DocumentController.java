package fr.sdis83.remocra.web;

import fr.sdis83.remocra.exception.BusinessException;
import fr.sdis83.remocra.usecase.importdocument.ImportDocumentsUseCase;
import fr.sdis83.remocra.web.serialize.ext.AbstractExtListSerializer;
import java.util.List;
import javax.naming.AuthenticationException;
import org.apache.commons.math3.util.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@RequestMapping("/documents")
@Controller
public class DocumentController {

  @Autowired private ImportDocumentsUseCase importDocumentsUseCase;

  @RequestMapping(value = "/import", method = RequestMethod.GET)
  public ResponseEntity<String> importDocuments() {
    return new AbstractExtListSerializer<Pair<ImportDocumentsUseCase.Severity, String>>(
        "Import documents") {
      @Override
      protected List<Pair<ImportDocumentsUseCase.Severity, String>> getRecords()
          throws BusinessException, AuthenticationException {
        return importDocumentsUseCase.execute();
      }
    }.serialize();
  }
}
