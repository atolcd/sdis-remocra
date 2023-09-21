package fr.sdis83.remocra.web;

import fr.sdis83.remocra.exception.AnomalieException;
import fr.sdis83.remocra.exception.SQLBusinessException;
import fr.sdis83.remocra.exception.XmlDroitException;
import fr.sdis83.remocra.exception.XmlValidationException;
import fr.sdis83.remocra.service.ParamConfService;
import fr.sdis83.remocra.service.XmlService;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.CannotCreateTransactionException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

@RequestMapping("/xml")
@Controller
public class XmlController {

  public static final String XML_RESPONSE_OK =
      "<?xml version=\"1.0\" encoding=\"UTF-8\"?><success/>";
  public static final String XML_RESPONSE_UPDATE_HYDRANTS_ERROR =
      "<?xml version=\"1.0\" encoding=\"UTF-8\"?><error><message>Problème de sérialisation</message></error>";
  public static final String XML_RESPONSE_UPDATE_HYDRANTS_OK =
      "<?xml version=\"1.0\" encoding=\"UTF-8\"?><success><message>Hydrants enregistrés avec succès</message></success>";
  public static final String XML_RESPONSE_DB_CONNECTION_ERROR =
      "<?xml version=\"1.0\" encoding=\"UTF-8\"?><error><message>Problème de connexion à la base</message></error>";

  private static final String UPDATEHYDRANTS_XML_DEBUG_FILE_REQ = "updatehydrants-req.xml";
  private static final String UPDATEHYDRANTS_XML_DEBUG_FILE_RESP = "updatehydrants-resp.xml";

  private final Logger log = Logger.getLogger(getClass());

  // Tracer les requêtes et les réponses dans un fichier
  private static boolean traceRequests = false;

  @Autowired private XmlService xmlService;

  @Autowired private ParamConfService paramConfService;

  @RequestMapping(value = "/communes")
  public void getCommunes(HttpServletResponse response) throws IOException {
    prepareXMLResponse(response);
    try {
      xmlService.serializeCommunes(response.getOutputStream());
    } catch (Exception e) {
      manageSQLBusinessExceptionOrException(response, e);
    }
  }

  @RequestMapping(value = "/anomalies")
  public void getAnomalies(HttpServletResponse response) throws IOException {
    prepareXMLResponse(response);
    try {
      xmlService.serializeAnomalies(response.getOutputStream());
    } catch (Exception e) {
      manageSQLBusinessExceptionOrException(response, e);
    }
  }

  @RequestMapping(value = "/diametres")
  public void getDiametres(HttpServletResponse response) throws IOException {
    prepareXMLResponse(response);
    try {
      xmlService.serializeDiametres(response.getOutputStream());
    } catch (Exception e) {
      manageSQLBusinessExceptionOrException(response, e);
    }
  }

  @RequestMapping(value = "/domaines")
  public void getDomaines(HttpServletResponse response) throws IOException {
    prepareXMLResponse(response);
    try {
      xmlService.serializeDomaines(response.getOutputStream());
    } catch (Exception e) {
      manageSQLBusinessExceptionOrException(response, e);
    }
  }

  @RequestMapping(value = "/positionnements")
  public void getPositionnements(HttpServletResponse response) throws IOException {
    prepareXMLResponse(response);
    try {
      xmlService.serializePositionnements(response.getOutputStream());
    } catch (Exception e) {
      manageSQLBusinessExceptionOrException(response, e);
    }
  }

  @RequestMapping(value = "/materiaux")
  public void getMateriaux(HttpServletResponse response) throws IOException {
    prepareXMLResponse(response);
    try {
      xmlService.serializeMateriaux(response.getOutputStream());
    } catch (Exception e) {
      manageSQLBusinessExceptionOrException(response, e);
    }
  }

  @RequestMapping(value = "/natures")
  public void getNatures(HttpServletResponse response) throws IOException {
    prepareXMLResponse(response);
    try {
      xmlService.serializeNatures(response.getOutputStream());
    } catch (Exception e) {
      manageSQLBusinessExceptionOrException(response, e);
    }
  }

  @RequestMapping(value = "/modeles")
  public void getModeles(HttpServletResponse response) throws IOException {
    prepareXMLResponse(response);
    try {
      xmlService.serializeModeles(response.getOutputStream());
    } catch (Exception e) {
      manageSQLBusinessExceptionOrException(response, e);
    }
  }

  @RequestMapping(value = "/marques")
  public void getMarques(HttpServletResponse response) throws IOException {
    prepareXMLResponse(response);
    try {
      xmlService.serializeMarques(response.getOutputStream());
    } catch (Exception e) {
      manageSQLBusinessExceptionOrException(response, e);
    }
  }

  @RequestMapping(value = "/volconstates")
  public void getVolConstates(HttpServletResponse response) throws IOException {
    prepareXMLResponse(response);
    try {
      xmlService.serializeVolConstates(response.getOutputStream());
    } catch (Exception e) {
      manageSQLBusinessExceptionOrException(response, e);
    }
  }

  @RequestMapping(value = "/referentiels")
  public void getReferentiels(HttpServletResponse response) throws IOException {
    prepareXMLResponse(response);
    try {
      xmlService.serializeReferentiels(response.getOutputStream());
    } catch (Exception e) {
      manageSQLBusinessExceptionOrException(response, e);
    }
  }

  @RequestMapping(value = "/saisies")
  public void getSaisies(HttpServletResponse response) throws IOException {
    prepareXMLResponse(response);
    try {
      xmlService.serializeSaisies(response.getOutputStream());
    } catch (Exception e) {
      manageSQLBusinessExceptionOrException(response, e);
    }
  }

  @RequestMapping(value = "/naturesDeci")
  public void getNaturesDeci(HttpServletResponse response) throws IOException {
    prepareXMLResponse(response);
    try {
      xmlService.serializeNaturesDeci(response.getOutputStream());
    } catch (Exception e) {
      manageSQLBusinessExceptionOrException(response, e);
    }
  }

  /**
   * @param response
   * @param tournees facultatif. Absence = suppression des verrous
   * @param lock facultatif, true par défaut. false = on ne pose pas de verrou.
   * @throws IOException
   */
  @RequestMapping(value = "/tournees")
  @Transactional
  @PreAuthorize(
      "hasRight('HYDRANTS_C') or hasRight('HYDRANTS_RECONNAISSANCE_C') or hasRight('HYDRANTS_CONTROLE_C')")
  public void getTournees(
      HttpServletResponse response,
      final @RequestParam(value = "tournees", required = false) String tournees,
      final @RequestParam(value = "lock", required = false, defaultValue = "true") boolean lock)
      throws IOException {
    prepareXMLResponse(response);
    try {
      List<Long> idTournees = new ArrayList<Long>();
      if (tournees != null) {
        String[] idStrTournees = tournees.split(",");
        for (String id : idStrTournees) {
          try {
            Long idTournee = Long.valueOf(id);
            idTournees.add(idTournee);
          } catch (NumberFormatException ex) {

          }
        }
      }
      xmlService.serializeTournees(idTournees, response.getOutputStream(), lock);
    } catch (Exception e) {
      manageSQLBusinessExceptionOrException(response, e);
    }
  }

  @RequestMapping(
      value = "/hydrants/file",
      method = RequestMethod.POST,
      headers = "Content-Type=multipart/form-data")
  @PreAuthorize(
      "hasRight('HYDRANTS_TELEVERSER_C') and (hasRight('HYDRANTS_C') or hasRight('HYDRANTS_RECONNAISSANCE_C') or hasRight('HYDRANTS_CONTROLE_C'))")
  public void updateHydrantsFile(
      MultipartHttpServletRequest request,
      HttpServletResponse response,
      final @RequestParam(value = "v", required = false) Integer v)
      throws IOException {
    String xml = null;
    MultipartFile mf = request.getFile("file");
    InputStream is = mf.getInputStream();
    try {
      StringWriter writer = new StringWriter();
      IOUtils.copy(is, writer, "UTF-8");
      xml = writer.toString();
      is.close();
    } finally {
      is.close();
    }
    updateHydrantsInternal(xml, response, v);
  }

  @RequestMapping(value = "/hydrants", method = RequestMethod.POST)
  @PreAuthorize(
      "hasRight('HYDRANTS_C') or hasRight('HYDRANTS_RECONNAISSANCE_C') or hasRight('HYDRANTS_CONTROLE_C')")
  public void updateHydrants(
      final @RequestBody String xml,
      HttpServletResponse response,
      final @RequestParam(value = "v", required = false) Integer v)
      throws IOException {
    updateHydrantsInternal(xml, response, v);
  }

  public void updateHydrantsInternal(
      final @RequestBody String xml,
      HttpServletResponse response,
      final @RequestParam(value = "v", required = false) Integer v)
      throws IOException {

    if (traceRequests) {
      // Sérialisation de la requête sur disque
      PrintWriter out =
          new PrintWriter(
              paramConfService.getPdiCheminLog()
                  + File.separator
                  + UPDATEHYDRANTS_XML_DEBUG_FILE_REQ);
      out.print(xml);
      out.close();
    }

    prepareXMLResponse(response);
    String xmlResp = null;
    try {
      xmlService.deSerializeHydrants(xml, v);
      response.setStatus(HttpServletResponse.SC_OK);
      xmlResp = XML_RESPONSE_UPDATE_HYDRANTS_OK;
    } catch (XmlValidationException e) {
      log.error(e);
      response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
      xmlResp = e.getMessageXMLError();
    } catch (CannotCreateTransactionException e) {
      log.error(e);
      response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
      xmlResp = XML_RESPONSE_DB_CONNECTION_ERROR;
    } catch (SQLBusinessException e) {
      xmlResp = manageSQLBusinessExceptionOrException(response, e);
    } catch (XmlDroitException e) {
      log.error(e);
      response.setStatus(HttpServletResponse.SC_FORBIDDEN);
      xmlResp = e.getMessageXMLError();
    } catch (AnomalieException e) {
      log.error(e);
      response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
      xmlResp = e.getMessage();
    } catch (Exception e) {
      log.error(e);
      response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
      xmlResp = XML_RESPONSE_UPDATE_HYDRANTS_ERROR;
    }
    response.getOutputStream().println(xmlResp);

    if (traceRequests) {
      // Sérialisation de la réponse sur disque
      PrintWriter out =
          new PrintWriter(
              paramConfService.getPdiCheminLog()
                  + File.separator
                  + UPDATEHYDRANTS_XML_DEBUG_FILE_RESP);
      out.print(xmlResp);
      out.close();
    }
  }

  @RequestMapping(value = "/pourcentages", method = RequestMethod.POST)
  @PreAuthorize(
      "hasRight('HYDRANTS_C') or hasRight('HYDRANTS_RECONNAISSANCE_C') or hasRight('HYDRANTS_CONTROLE_C')")
  public void updateTournees(
      final @RequestBody String xml,
      HttpServletResponse response,
      final @RequestParam(value = "v", required = false) Integer v)
      throws IOException {

    prepareXMLResponse(response);
    String xmlResp = null;
    try {
      xmlService.deSerializeTournees(xml, v);
      response.setStatus(HttpServletResponse.SC_OK);
      xmlResp = XML_RESPONSE_UPDATE_HYDRANTS_OK;
    } catch (XmlValidationException e) {
      log.error(e);
      response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
      xmlResp = e.getMessageXMLError();
    } catch (CannotCreateTransactionException e) {
      log.error(e);
      response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
      xmlResp = XML_RESPONSE_DB_CONNECTION_ERROR;
    } catch (SQLBusinessException e) {
      xmlResp = manageSQLBusinessExceptionOrException(response, e);
    } catch (Exception e) {
      log.error(e);
      response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
      xmlResp = XML_RESPONSE_UPDATE_HYDRANTS_ERROR;
    }
    response.getOutputStream().println(xmlResp);
  }

  protected void prepareXMLResponse(HttpServletResponse response) {
    response.setHeader("Content-Type", "application/xml;charset=utf-8");
  }

  protected String manageSQLBusinessExceptionOrException(HttpServletResponse response, Exception e)
      throws IOException {
    log.error(e);
    response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    String xmlResp =
        e instanceof SQLBusinessException
            ? ((SQLBusinessException) e).getMessageXMLError()
            : XML_RESPONSE_UPDATE_HYDRANTS_ERROR;
    response.getOutputStream().println(xmlResp);
    return xmlResp;
  }

  @RequestMapping(value = "trace")
  @PreAuthorize("hasRight('REFERENTIELS_C')")
  public void trace(HttpServletResponse response) throws IOException {
    traceRequests = true;
    response.setStatus(HttpServletResponse.SC_OK);
    response.getOutputStream().println(XML_RESPONSE_OK);
  }

  @RequestMapping(value = "notrace")
  @PreAuthorize("hasRight('REFERENTIELS_C')")
  public void notrace(HttpServletResponse response) throws IOException {
    traceRequests = false;
    response.setStatus(HttpServletResponse.SC_OK);
    response.getOutputStream().println(XML_RESPONSE_OK);
  }
}
