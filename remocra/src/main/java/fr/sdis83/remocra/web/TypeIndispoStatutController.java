package fr.sdis83.remocra.web;

import flexjson.JSONSerializer;
import fr.sdis83.remocra.domain.remocra.ITypeReferenceNomActif;
import fr.sdis83.remocra.domain.remocra.TypeHydrantIndispoStatut;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RequestMapping("/typeindispostatut")
@Controller
public class TypeIndispoStatutController<T extends ITypeReferenceNomActif> extends AbstractTypeReferenceController<T> {

    public TypeIndispoStatutController() {
        super(TypeHydrantIndispoStatut.class);
    }

    @Override
    public JSONSerializer decorateSerializer(JSONSerializer serializer) {
        return serializer.exclude("data.actif").exclude("*.class");
    }

    @Override
    @RequestMapping(headers = "Accept=application/json")
    public ResponseEntity<String> listJson(@RequestParam(value = "filter", required = false) String filters) {
        return listJsonNR(filters);
    }

}
