package fr.sdis83.remocra.web;

import fr.sdis83.remocra.domain.remocra.ProfilDroit;
import fr.sdis83.remocra.exception.BusinessException;
import fr.sdis83.remocra.service.UtilisateurService;
import fr.sdis83.remocra.service.ZoneCompetenceService;
import fr.sdis83.remocra.usecase.parametre.ParametreDataProvider;
import fr.sdis83.remocra.util.GeometryUtil;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URLDecoder;
import java.util.Enumeration;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.MethodNotSupportedException;
import org.apache.http.impl.DefaultHttpRequestFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.UrlResource;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

@RequestMapping("/proxy")
@Controller
public class ProxyController {

  private final Logger log = Logger.getLogger(getClass());

  @Autowired private UtilisateurService utilisateurService;

  @Autowired private ParametreDataProvider parametreProvider;

  @Autowired private ZoneCompetenceService zoneCompetenceService;

  /**
   * Retourne le fichier de configuration à utiliser pour la transformation XSL. Dans l'ordre :
   * fichier correspondant au profil, puis fichier public. Enfin, une exception si aucun fichier
   * trouvé
   *
   * @return
   * @throws FileNotFoundException
   */
  protected File getGetFeatureInfoTransfoFile() throws FileNotFoundException {
    String codeFeuille = null, defaultCodeFeuille = "public";
    try {
      ProfilDroit profilDroit = utilisateurService.getCurrentProfilDroit();
      codeFeuille =
          profilDroit == null
              ? defaultCodeFeuille
              : profilDroit.getFeuilleDeStyleGeoServer().toLowerCase();
    } catch (AccessDeniedException e) {
      log.error("Unable to find profile for utilisateur. Access Denied");
      codeFeuille = defaultCodeFeuille;
    } catch (BusinessException e) {
      log.error("Unable to find profile for utilisateur.");
      codeFeuille = defaultCodeFeuille;
    }
    String folder = parametreProvider.get().getDossierGetFeatureInfo();
    File returned = new File(folder + File.separatorChar + codeFeuille + ".xsl");
    if (!returned.exists()) {
      returned = new File(folder + File.separatorChar + defaultCodeFeuille + ".xsl");
      if (!returned.exists()) {
        throw new FileNotFoundException("Fichier de configuration non trouvé");
      }
    }
    return returned;
  }

  @RequestMapping()
  public void proxy(
      @RequestParam(value = "url", required = true) String url,
      HttpServletRequest request,
      HttpServletResponse response) {

    try {
      // On décode l'URL (gestion des accents, des &, etc.)
      url = URLDecoder.decode(url, "utf-8");
      log.info("Proxy init vers : " + url);

      // --------------------
      // Préparation de la requête
      // --------------------

      // On passe par un UriComponentsBuilder car la méthode d'avant
      // empêchait de modifier les paramètres de la requête
      UriComponentsBuilder b = UriComponentsBuilder.fromHttpUrl(url);

      // Récupération de l'URL cible
      URI targetUri = new UrlResource(url).getURI();
      HttpHost httpHost =
          new HttpHost(targetUri.getHost(), targetUri.getPort(), targetUri.getScheme());
      // Paramètres supplémentaires éventuels (hors url)
      @SuppressWarnings("unchecked")
      Enumeration<String> paramNames = request.getParameterNames();
      while (paramNames.hasMoreElements()) {
        String paramName = paramNames.nextElement();
        if (!"url".equals(paramName)) {
          b.replaceQueryParam(paramName, request.getParameter(paramName));
        }
      }

      UriComponents c = b.build();
      String targetURL = c.toUriString();

      log.info("Proxy corr vers : " + targetURL);
      HttpRequest targetRequest =
          new DefaultHttpRequestFactory().newHttpRequest(request.getMethod(), targetURL);

      // Entêtes
      @SuppressWarnings("unchecked")
      Enumeration<String> headerNames = request.getHeaderNames();
      while (headerNames.hasMoreElements()) {
        String headerName = headerNames.nextElement();
        targetRequest.setHeader(headerName, request.getHeader(headerName));
      }
      // --------------------
      // Exécution de la requête
      // --------------------

      DefaultHttpClient httpclient = new DefaultHttpClient();
      HttpResponse srcResponse = httpclient.execute(httpHost, targetRequest);

      // --------------------
      // Traitement de la réponse
      // --------------------

      // Entêtes
      Header[] headers = srcResponse.getAllHeaders();
      for (Header header : headers) {
        response.setHeader(header.getName(), header.getValue());
        if ("Content-Type".equalsIgnoreCase(header.getName())) {
          response.setContentType(header.getValue());
        }
      }
      InputStream is = srcResponse.getEntity().getContent();
      OutputStream os = response.getOutputStream();

      byte[] buffer = new byte[1024];
      int bytesRead;
      while ((bytesRead = is.read(buffer)) != -1) {
        os.write(buffer, 0, bytesRead);
      }
      os.flush();
      os.close();

      // On a déjà traité la réponse si bien qu'on ne retourne rien.

    } catch (MethodNotSupportedException e) {
      log.error("La méthode invoquée n'est pas supportée", e);
      response.setStatus(500);
    } catch (IOException e) {
      log.error("Une erreur d'entrée / sortie est survenue", e);
      response.setStatus(500);
    }
  }

  /**
   * Proxy spécialisé vers GeoServer. Utilisé notamment avec les méthodes GetMap et GetFeatureInfo
   *
   * <p>S'occupe des droits : modifie la requête
   *
   * @param request
   * @param response
   */
  @RequestMapping(value = "/wms")
  public void proxyWms(HttpServletRequest request, HttpServletResponse response) {
    // appli (remocra/proxy/wms) vers geoserver (geoserver/remocra/wms)

    // --------------------
    // Préparation de la requête
    // --------------------

    try {
      log.info("Proxy WMS : server name : " + request.getServerName());

      // Si dev local, on pointe sur le geoserver de tests, sinon sur le
      // geoserver local
      String pattern = "http://.*/remocra/proxy/wms";
      String wmsBaseUrl = parametreProvider.get().getWmsBaseUrl();
      String targetURL =
          request
              .getRequestURL()
              .toString()
              .replaceFirst(
                  pattern,
                  parametreProvider.get().getWmsBaseUrl()
                      + (wmsBaseUrl.endsWith("/") ? "" : "/")
                      + "remocra/wms");

      // Récupération de l'URL cible
      URI targetUri = new UrlResource(targetURL).getURI();
      HttpHost httpHost =
          new HttpHost(targetUri.getHost(), targetUri.getPort(), targetUri.getScheme());

      log.info("Proxy WMS : targetURL : " + targetURL);

      // On passe par un UriComponentsBuilder car la méthode d'avant
      // empêchait de modifier les paramètres de la requête
      UriComponentsBuilder b = UriComponentsBuilder.fromHttpUrl(targetURL);

      String reqParam = request.getParameter("REQUEST");
      boolean isGetFeatureInfo = "GetFeatureInfo".equals(reqParam);
      boolean isGetMap = "GetMap".equals(reqParam);

      boolean isPublic = false;
      if (isGetMap || isGetFeatureInfo) {
        isPublic = isLayersParameterValuePublic(request.getParameter("LAYERS"));
      }
      log.info("Proxy WMS : requête " + (isPublic ? "publique" : "authentifiée"));

      // On traitera la réponse => pas de zip
      boolean acceptEncoding = !isGetFeatureInfo;

      Long zoneCompetence = isPublic ? null : utilisateurService.getCurrentZoneCompetenceId();

      // GetFeatureInfo : on filtre en amont selon la Zone de compétence
      if (isGetFeatureInfo) {
        // SRS, BBOX
        String bbox = request.getParameter("BBOX");

        String wkt = GeometryUtil.wktFromBBox(bbox);
        Integer srid = GeometryUtil.sridFromEpsgCode(request.getParameter("SRS"));
        if (!isPublic && !zoneCompetenceService.check(wkt, srid, zoneCompetence)) {
          log.info("Proxy WMS : hors zone compétence");
          response.setStatus(403);
          return;
        }
      }

      // Paramètres supplémentaires éventuels
      @SuppressWarnings("unchecked")
      Enumeration<String> paramNames = request.getParameterNames();
      while (paramNames.hasMoreElements()) {
        String paramName = paramNames.nextElement();
        b.replaceQueryParam(paramName, request.getParameter(paramName));
      }

      // Paramètre de mise en forme dans le cas d'une requête
      // GetFeatureInfo
      if (isGetFeatureInfo) {
        // On force le format
        b.replaceQueryParam("INFO_FORMAT");
        b.replaceQueryParam("info_format", "application/vnd.ogc.gml/3.1.1");
      }

      // GetMap : on fait filtrer Geoserver selon la Zone de compétence
      if (isGetMap) {
        if (!isPublic) {
          String[] layers = request.getParameter("LAYERS").split(",");
          // Exemple avec deux couches (clause INCLUDE si couche non
          // filtrée) :
          // &CQL_FILTER=INCLUDE;WITHIN(geometrie,(querySingle('remocra:zone_competence','geometrie','id=26')))
          // On passe par l'id en échappant avec %22 (double quote)
          // car mot réservé.
          // C'est plus sûr qu'en passant par une chaine de caractères
          // (code=VAR par exemple)
          String idZone = zoneCompetence.toString();
          String cqlFilterValue =
              "WITHIN(geometrie,(querySingle('remocra:zone_competence','geometrie','%22id%22="
                  + idZone
                  + "')))";

          // On répère autant de fois la clause qu'il y a de couches
          String repeatedCqlFilterValue = StringUtils.repeat(cqlFilterValue, ";", layers.length);
          b.replaceQueryParam("CQL_FILTER", repeatedCqlFilterValue);
        }
      }

      String targetURLWithFilter = b.build().toUriString();

      log.info("Proxy WMS : targetURLWithFilter : " + targetURLWithFilter);
      HttpRequest targetRequest =
          new DefaultHttpRequestFactory().newHttpRequest(request.getMethod(), targetURLWithFilter);

      // Entêtes
      @SuppressWarnings("unchecked")
      Enumeration<String> headerNames = request.getHeaderNames();
      while (headerNames.hasMoreElements()) {
        String headerName = headerNames.nextElement();
        if ("authorization".equalsIgnoreCase(headerName)) {
          // Pour éviter de propager l'authentification Remocra vers le GeoServer
          continue;
        }
        targetRequest.setHeader(headerName, request.getHeader(headerName));
      }

      if (!acceptEncoding) {
        // On désire un résultat non compressé (pas de gzip, deflate,
        // etc.), sans quoi il faut le traiter ultérieurement
        targetRequest.setHeader("Accept-Encoding", "");
      }

      // --------------------
      // Exécution de la requête
      // --------------------

      DefaultHttpClient httpclient = new DefaultHttpClient();
      HttpResponse srcResponse = httpclient.execute(httpHost, targetRequest);

      // --------------------
      // Traitement de la réponse
      // --------------------

      // Entêtes
      Header[] headers = srcResponse.getAllHeaders();
      for (Header header : headers) {
        response.setHeader(header.getName(), header.getValue());
        if ("Content-Type".equalsIgnoreCase(header.getName())) {
          response.setContentType(header.getValue());
        }
      }
      InputStream is = srcResponse.getEntity().getContent();
      OutputStream os = response.getOutputStream();

      try {
        if (isGetFeatureInfo) {
          // Mise en forme via transfo XSL selon profil
          File transfoFile = getGetFeatureInfoTransfoFile();
          log.info("Proxy WMS : transfo : " + transfoFile.getAbsolutePath());
          StreamSource xmlSource = new StreamSource(is);

          StreamSource styleSource = new StreamSource(transfoFile);
          TransformerFactory factory = TransformerFactory.newInstance();
          Transformer transformer = factory.newTransformer(styleSource);

          StreamResult result = new StreamResult(os);
          transformer.transform(xmlSource, result);

        } else {
          // Retour direct (image ou autre)
          byte[] buffer = new byte[1024];
          int bytesRead;
          while ((bytesRead = is.read(buffer)) != -1) {
            os.write(buffer, 0, bytesRead);
          }
        }

      } catch (TransformerException e) {
        log.error("Problème de transformation", e);
      } catch (IOException e) {
        log.error("Problème d'entrée/sortie", e);
      } finally {
        os.flush();
        os.close();
      }

      // On a déjà traité la réponse si bien qu'on ne retourne rien.

    } catch (MethodNotSupportedException e) {
      log.error("La méthode invoquée n'est pas supportée", e);
      response.setStatus(500);
    } catch (IOException e) {
      log.error("Une erreur d'entrée / sortie est survenue", e);
      response.setStatus(500);
    }
  }

  /**
   * Retourne VRAI s'il s'agit de couches publiques.
   *
   * @param layersParamValue
   * @return
   */
  boolean isLayersParameterValuePublic(String layersParamValue) {
    String[] paramLayers = layersParamValue.split(",");

    if (paramLayers.length != 1) {
      return false;
    }

    // Toute couche demandée
    for (String aParamLayer : paramLayers) {
      // Toute couche publique en base
      for (String aPublicDbLayer : parametreProvider.get().getWmsPublicLayers()) {
        if (aPublicDbLayer.equals(aParamLayer)) {
          // Couche trouvée : publique
          return true;
        }
      }
    }
    return false;
  }
}
