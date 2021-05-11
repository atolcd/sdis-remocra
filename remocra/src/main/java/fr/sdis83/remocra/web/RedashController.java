package fr.sdis83.remocra.web;

import fr.sdis83.remocra.domain.remocra.Utilisateur;
import fr.sdis83.remocra.service.ParamConfService;
import fr.sdis83.remocra.service.UtilisateurService;
import fr.sdis83.remocra.service.ZoneCompetenceService;
import org.apache.http.*;
import org.apache.http.impl.DefaultHttpRequestFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.UrlResource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.util.Enumeration;

@RequestMapping("/redash")
@Controller
public class RedashController {

    private final Logger log = Logger.getLogger(getClass());

    @Autowired
    private UtilisateurService utilisateurService;

    @Autowired
    private ParamConfService paramConfService;

    @Autowired
    private ZoneCompetenceService zoneCompetenceService;

    @RequestMapping(value = "/**")
    @PreAuthorize("hasRight('DASHBOARD_R')")
    public void api(HttpServletRequest request, HttpServletResponse response) {

        try {
            Long zoneCompetence = utilisateurService.getCurrentZoneCompetenceId();
            Utilisateur u = utilisateurService.getCurrentUtilisateur();
            String redashUrl = "http://localhost:5000" + request.getContextPath() + request.getServletPath();
            log.info("Proxy init vers : " + redashUrl);
            // --------------------
            // Préparation de la requête
            // --------------------

            UriComponentsBuilder b = UriComponentsBuilder.fromHttpUrl(redashUrl);
            // Récupération de l'URL cible
            URI targetUri = new UrlResource(redashUrl).getURI();
            HttpHost httpHost = new HttpHost(targetUri.getHost(), targetUri.getPort(), targetUri.getScheme());

            // Paramètres supplémentaires éventuels (hors url)

            UriComponents c = b.queryParam("p_utilisateur",u.getId())
                    .queryParam("p_zone_competence",zoneCompetence)
                    .queryParam("p_organisme",u.getOrganisme().getId())
                    .build();
            String targetURL = c.toUriString();

            log.info("Proxy corr vers : " + targetURL);
            HttpRequest targetRequest = new DefaultHttpRequestFactory().newHttpRequest(request.getMethod(), targetURL);
            // Entêtes
            @SuppressWarnings("unchecked")
            Enumeration<String> headerNames = request.getHeaderNames();
            while (headerNames.hasMoreElements()) {
                String headerName = headerNames.nextElement();
                if ("content-length".equalsIgnoreCase(headerName)) {
                    continue;
                }
                targetRequest.setHeader(headerName, request.getHeader(headerName));
            }

            // --------------------
            // Exécution de la requête
            // --------------------

            DefaultHttpClient httpclient = new DefaultHttpClient();
            HttpResponse srcResponse = httpclient.execute(httpHost, targetRequest);
            // Entêtes
            Header[] headers = srcResponse.getAllHeaders();
            for (Header header : headers) {
                response.setHeader(header.getName(), header.getValue());
            }

            // --------------------
            // Traitement de la réponse
            // --------------------
            InputStream is = null;
            if(srcResponse.getEntity() != null){
               is = srcResponse.getEntity().getContent();
            }

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


}