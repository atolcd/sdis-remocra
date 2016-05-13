package fr.sdis83.remocra.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import flexjson.JSONSerializer;
import fr.sdis83.remocra.domain.remocra.ITypeReferenceNomActif;
import fr.sdis83.remocra.domain.remocra.TypePermisInterservice;

@RequestMapping("/typepermisinterservices")
@Controller
public class TypePermisInterserviceController<T extends ITypeReferenceNomActif> extends AbstractTypeReferenceController<T> {

    public TypePermisInterserviceController() {
        super(TypePermisInterservice.class);
    }

    public JSONSerializer decorateSerializer(JSONSerializer serializer) {
        return serializer.exclude("*.class");
    }
}
