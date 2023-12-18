package fr.sdis83.remocra.web;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.sdis83.remocra.GlobalConstants;
import fr.sdis83.remocra.data.ParamConfWithClDisplay;
import fr.sdis83.remocra.repository.ParamConfRepository;
import fr.sdis83.remocra.usecase.parametre.ParametreDataProvider;
import fr.sdis83.remocra.usecase.parametre.UpdateParametreUseCase;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@RequestMapping("/paramconfs")
@Controller
public class ParamConfController {

  @Autowired protected ParamConfRepository paramConfRepository;

  @Autowired protected ParametreDataProvider parametreDataProvider;

  @Autowired protected UpdateParametreUseCase updateParametreUseCase;

  private final Logger logger = Logger.getLogger(getClass());
  private final ObjectMapper objectMapper = new ObjectMapper();

  public ParamConfController() {}

  @RequestMapping(method = RequestMethod.GET, headers = "Accept=application/json;charset=utf-8")
  @PreAuthorize("hasRight('REFERENTIELS_C')")
  public ResponseEntity<java.lang.String> listJson() {
    try {
      Collection<ParamConfWithClDisplay> coll = paramConfRepository.getParametresForAdmin();
      // On alimente la classe concrète
      for (ParamConfWithClDisplay param : coll) {
        param.setClDisplay(parametreDataProvider.get().getClazz(param.getCle()).getSimpleName());
      }
      HttpHeaders responseHeaders = new HttpHeaders();
      responseHeaders.add("Content-Type", "text/html; charset=utf-8");

      return new ResponseEntity<>(
          objectMapper.writeValueAsString(coll), responseHeaders, HttpStatus.OK);
    } catch (Exception e) {
      this.logger.error(e.getMessage(), e);
      return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @RequestMapping(method = RequestMethod.PUT, headers = "Accept=application/json;charset=utf-8")
  @PreAuthorize("hasRight('REFERENTIELS_C')")
  public ResponseEntity<String> updateFromJson(final @RequestBody String json) {
    try {
      List<ParamConfWithClDisplay> paramToSave =
          objectMapper.readValue(json, new TypeReference<List<ParamConfWithClDisplay>>() {});
      for (ParamConfWithClDisplay paramConf : paramToSave) {
        updateParametreUseCase.updateParamConf(paramConf.getCle(), paramConf.getValeur());
      }
      return new ResponseEntity<>(objectMapper.writeValueAsString(paramToSave), HttpStatus.OK);
    } catch (IOException e) {
      logger.error(e.getMessage());
      return new ResponseEntity<>(
          GlobalConstants.GENERIQUE_MESSAGE_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  // ATTENTION : ici, le POST est considéré comme un PUT
  @RequestMapping(method = RequestMethod.POST, headers = "Accept=application/json;charset=utf-8")
  @PreAuthorize("hasRight('REFERENTIELS_C')")
  public ResponseEntity<java.lang.String> postFromJson(final @RequestBody String json) {
    return updateFromJson(json);
  }
}
