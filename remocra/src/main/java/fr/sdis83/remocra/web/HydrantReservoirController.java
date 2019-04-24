package fr.sdis83.remocra.web;

import fr.sdis83.remocra.web.serialize.ext.AbstractExtListSerializer;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import flexjson.JSONSerializer;
import fr.sdis83.remocra.domain.remocra.ITypeReferenceNomActif;
import fr.sdis83.remocra.domain.remocra.HydrantReservoir;

import java.util.List;

@RequestMapping("/hydrantreservoir")
@Controller
public class HydrantReservoirController{

    public JSONSerializer decorateSerializer(JSONSerializer serializer) {
        return serializer.exclude("data.actif").exclude("*.class");
    }

    @RequestMapping(value = "", headers = "Accept=application/json")
    public ResponseEntity<java.lang.String> listJson() {
        return new AbstractExtListSerializer<HydrantReservoir>("fr.sdis83.remocra.domain.remocra.HydrantReservoir retrieved.") {

            @Override
            protected List<HydrantReservoir> getRecords() {
                return HydrantReservoir.findAllHydrantReservoirs();
            }

        }.serialize();
    }

}