package fr.sdis83.remocra.web;

import flexjson.JSONSerializer;
import fr.sdis83.remocra.domain.remocra.TypeHydrantAspiration;
import fr.sdis83.remocra.web.serialize.ext.AbstractExtListSerializer;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/typehydrantaspiration")
@Controller
public class TypeHydrantAspirationController {

  public JSONSerializer decorateSerializer(JSONSerializer serializer) {
    return serializer.exclude("data.actif").exclude("*.class");
  }

  @RequestMapping(value = "", headers = "Accept=application/json")
  public ResponseEntity<java.lang.String> listJson() {
    return new AbstractExtListSerializer<TypeHydrantAspiration>(
        "fr.sdis83.remocra.domain.remocra.TypeHydrantAspiration retrieved.") {

      @Override
      protected List<TypeHydrantAspiration> getRecords() {
        return TypeHydrantAspiration.findAllTypeHydrantAspirations();
      }
    }.serialize();
  }
}
