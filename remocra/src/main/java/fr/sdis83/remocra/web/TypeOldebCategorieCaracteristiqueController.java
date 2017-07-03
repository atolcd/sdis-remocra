package fr.sdis83.remocra.web;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import flexjson.JSONSerializer;
import fr.sdis83.remocra.domain.remocra.ITypeReferenceNomActif;
import fr.sdis83.remocra.domain.remocra.TypeOldebCategorieCaracteristique;

@RequestMapping("/typeoldebcategoriecaracteristique")
@Controller
public class TypeOldebCategorieCaracteristiqueController<T extends ITypeReferenceNomActif> extends AbstractTypeReferenceController<T> {

    public TypeOldebCategorieCaracteristiqueController() {
        super(TypeOldebCategorieCaracteristique.class);
    }

    @Override
    public JSONSerializer decorateSerializer(JSONSerializer serializer) {
        return serializer.include("data.typeOldebCaracteristiques").exclude("data.typeOldebCaracteristiques.actif").exclude("data.actif").exclude("*.class");
    }

    @Override
    @RequestMapping(headers = "Accept=application/json")
    public ResponseEntity<java.lang.String> listJson(@RequestParam(value = "filter", required = false) String filters) {
        return listJsonNR(filters);
    }

}
