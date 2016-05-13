package fr.sdis83.remocra.web;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import fr.sdis83.remocra.domain.remocra.ITypeReferenceNomActif;
import fr.sdis83.remocra.domain.remocra.SousTypeAlerteElt;

@RequestMapping("/soustypealerteelts")
@Controller
public class SousTypeAlerteEltController<T extends ITypeReferenceNomActif> extends AbstractTypeReferenceController<T> {
    
    public SousTypeAlerteEltController() {
        super(SousTypeAlerteElt.class);
    }

    @RequestMapping(headers = "Accept=application/json")
    public ResponseEntity<java.lang.String> listJson(@RequestParam(value = "filter", required = false) String filters) {
        return listJsonNR(filters);
    }
}
