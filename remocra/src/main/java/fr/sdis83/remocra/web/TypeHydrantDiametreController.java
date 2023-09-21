package fr.sdis83.remocra.web;

import flexjson.JSONSerializer;
import fr.sdis83.remocra.domain.remocra.ITypeReferenceNomActif;
import fr.sdis83.remocra.domain.remocra.TypeHydrantDiametre;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RequestMapping("/typehydrantdiametres")
@Controller
public class TypeHydrantDiametreController<T extends ITypeReferenceNomActif>
    extends AbstractTypeReferenceController<T> {

  public TypeHydrantDiametreController() {
    super(TypeHydrantDiametre.class);
  }

  public JSONSerializer decorateSerializer(JSONSerializer serializer) {
    return serializer.include("data.natures").exclude("data.actif").exclude("*.class");
  }

  @RequestMapping(headers = "Accept=application/json")
  public ResponseEntity<java.lang.String> listJson(
      @RequestParam(value = "filter", required = false) String filters) {
    return listJsonNR(filters);
  }
}
