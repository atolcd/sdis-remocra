package fr.sdis83.remocra.web;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import flexjson.JSONSerializer;
import fr.sdis83.remocra.domain.remocra.ITypeReferenceNomActif;
import fr.sdis83.remocra.domain.remocra.TypeHydrantMarque;

@RequestMapping("/typehydrantmarques")
@Controller
public class TypeHydrantMarqueController<T extends ITypeReferenceNomActif> extends AbstractTypeReferenceController<T> {

    public TypeHydrantMarqueController() {
        super(TypeHydrantMarque.class);
    }

    public JSONSerializer decorateSerializer(JSONSerializer serializer) {
        return serializer.include("data.modeles").exclude("data.actif");
    }

    @RequestMapping(headers = "Accept=application/json")
    public ResponseEntity<java.lang.String> listJson(@RequestParam(value = "filter", required = false) String filters) {
        return listJsonNR(filters);
    }

}
