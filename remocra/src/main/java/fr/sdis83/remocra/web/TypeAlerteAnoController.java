package fr.sdis83.remocra.web;

import fr.sdis83.remocra.domain.remocra.ITypeReferenceNomActif;
import fr.sdis83.remocra.domain.remocra.TypeAlerteAno;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RequestMapping("/typealerteanos")
@Controller
public class TypeAlerteAnoController<T extends ITypeReferenceNomActif>
    extends AbstractTypeReferenceController<T> {

  public TypeAlerteAnoController() {
    super(TypeAlerteAno.class);
  }

  @RequestMapping(headers = "Accept=application/json")
  public ResponseEntity<java.lang.String> listJson(
      @RequestParam(value = "filter", required = false) String filters) {
    return listJsonNR(filters);
  }
}
