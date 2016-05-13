package fr.sdis83.remocra.web;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import flexjson.JSONSerializer;
import fr.sdis83.remocra.domain.remocra.ITypeReferenceNomActif;
import fr.sdis83.remocra.domain.remocra.TypeAlerteElt;

@RequestMapping("/typealerteelts")
@Controller
public class TypeAlerteEltController<T extends ITypeReferenceNomActif> extends AbstractTypeReferenceController<T> {

    static protected final String ORDER_COLUMN_NAME = "nom";
    
    public TypeAlerteEltController() {
        super(TypeAlerteElt.class);
    }
    
    public JSONSerializer decorateSerializer(JSONSerializer serializer) {
        return serializer.include("data.sousTypeAlerteElts").exclude("*.class");
    }
    
    @RequestMapping(headers = "Accept=application/json")
    public ResponseEntity<java.lang.String> listJson(@RequestParam(value = "filter", required = false) String filters) {
        return listJsonNR(filters);
    }

    @Override
    public String getOrderColName() {
        return ORDER_COLUMN_NAME;
    }
}
