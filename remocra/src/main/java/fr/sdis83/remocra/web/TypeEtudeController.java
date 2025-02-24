package fr.sdis83.remocra.web;

import flexjson.JSONSerializer;
import fr.sdis83.remocra.domain.remocra.ITypeReferenceNomActif;
import fr.sdis83.remocra.domain.remocra.TypeEtude;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RequestMapping("/typeetude")
@Controller
public class TypeEtudeController<T extends ITypeReferenceNomActif>
    extends AbstractTypeReferenceController<T> {

  public TypeEtudeController() {
    super(TypeEtude.class);
  }

  public JSONSerializer decorateSerializer(JSONSerializer serializer) {
    return serializer.exclude("data.actif").exclude("*.class");
  }

  @RequestMapping(headers = "Accept=application/json")
  public ResponseEntity<java.lang.String> listJson(
      @RequestParam(value = "filter", required = false) String filters) {
    return listJsonNR(filters);
  }
}
