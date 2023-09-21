package fr.sdis83.remocra.web;

import flexjson.JSONSerializer;
import fr.sdis83.remocra.domain.remocra.ITypeReferenceNomActif;
import fr.sdis83.remocra.domain.remocra.TypePermisInterservice;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RequestMapping("/typepermisinterservices")
@Controller
public class TypePermisInterserviceController<T extends ITypeReferenceNomActif>
    extends AbstractTypeReferenceController<T> {

  public TypePermisInterserviceController() {
    super(TypePermisInterservice.class);
  }

  public JSONSerializer decorateSerializer(JSONSerializer serializer) {
    return serializer.exclude("*.class");
  }

  @RequestMapping(headers = "Accept=application/json")
  public ResponseEntity<String> listJson(
      @RequestParam(value = "filter", required = false) String filters) {
    return listJsonNR(filters);
  }
}
