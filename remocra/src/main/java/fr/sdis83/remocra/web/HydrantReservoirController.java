package fr.sdis83.remocra.web;

import flexjson.JSONSerializer;
import fr.sdis83.remocra.domain.remocra.HydrantReservoir;
import fr.sdis83.remocra.exception.BusinessException;
import fr.sdis83.remocra.service.HydrantReservoirService;
import fr.sdis83.remocra.web.serialize.ext.AbstractExtListSerializer;
import fr.sdis83.remocra.web.serialize.ext.AbstractExtObjectSerializer;
import fr.sdis83.remocra.web.serialize.ext.SuccessErrorExtSerializer;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@RequestMapping("/reservoir")
@Controller
public class HydrantReservoirController {

  @Autowired HydrantReservoirService service;

  public JSONSerializer decorateSerializer(JSONSerializer serializer) {
    return serializer.exclude("data.actif").exclude("*.class");
  }

  @RequestMapping(value = "", headers = "Accept=application/json")
  public ResponseEntity<java.lang.String> listJson() {
    return new AbstractExtListSerializer<HydrantReservoir>(
        "fr.sdis83.remocra.domain.remocra.HydrantReservoir retrieved.") {

      @Override
      protected List<HydrantReservoir> getRecords() {
        return HydrantReservoir.findAllHydrantReservoirs();
      }
    }.serialize();
  }

  @RequestMapping(value = "", method = RequestMethod.POST, headers = "Accept=application/json")
  public ResponseEntity<java.lang.String> create(final @RequestBody String json) {
    try {
      final HydrantReservoir attached = service.create(json, null);
      return new AbstractExtObjectSerializer<HydrantReservoir>("HydrantReservoir created") {
        @Override
        protected HydrantReservoir getRecord() throws BusinessException {
          return attached;
        }
      }.serialize();
    } catch (Exception e) {
      e.printStackTrace();
      return new SuccessErrorExtSerializer(false, e.getMessage()).serialize();
    }
  }
}
