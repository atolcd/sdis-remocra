package fr.sdis83.remocra.web;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import fr.sdis83.remocra.web.serialize.StreamFileUtils;

/**
 * Proxy vers des ressources externes (URLs /ext-res/**). Si le fichier concerné
 * sur disque n'existe pas (paramètre DOSSIER_RESSOURCES_EXTERNES), le proxy
 * essaie de retourner le fichier du répertoire "webapp" s'il est trouvé, une
 * erreur 404 sinon.
 * 
 * @author cva
 * 
 */
@RequestMapping("/ext-res")
@Controller
public class ExtResourcesController extends AbstractRemocraController {

    private final Logger logger = Logger.getLogger(getClass());

    @Autowired
    ApplicationContext appplicationContext;

    /**
     * Retourne une ressource dans le flux. Si la ressource externe existe,
     * c'est elle qui est transmise. Sinon on essaie de transmettre la ressource
     * équivalente du dossier encapsulé dans l'application (webapp/**).
     * 
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping(value = "/**")
    public void proxExtResource(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String partialPath = request.getRequestURI().replaceAll(".*/ext-res", "");
        File file = getFile(partialPath);
        showDocument(file, response);
    }

    /**
     * Retourne le fichier trouvé.
     * 
     * @param partialPath
     *            chemin partiel. Doit commencer par /
     * @return
     */
    protected File getFile(String partialPath) {
        if (partialPath == null) {
            logger.error("Ressource externe non précisée");
            return null;
        }
        String resExtBasePath = paramConfService.getDossierRessourcesExternes();
        File file = new File(resExtBasePath + partialPath);
        if (!file.exists()) {
            logger.trace("Ressource externe non trouvée : " + file.getAbsolutePath());
            // Si ressource externe absente, on essaie de retourner la ressource
            // de l'application (si elle existe)
            Resource res = appplicationContext.getResource(partialPath);
            try {
                file = res.getFile();
            } catch (IOException e) {
                logger.info("Ressource externe non trouvée : " + partialPath);
            }
            if (!file.exists()) {
                logger.info("Ressource interne non trouvée : " + file.getAbsolutePath());
                return null;
            }
        }
        return file;
    }

    protected void showDocument(File file, HttpServletResponse response) throws IOException {
        if (file == null) {
            response.setStatus(404);
            return;
        }

        if (file.getName().endsWith(".htm")) {
            response.setContentType("text/html");
        } else {
            String contentType = StreamFileUtils.getContentTypeFromFile(file);
            if (contentType != null) {
                response.setContentType(contentType);
            }
        }
        response.setContentLength(new Long(file.length()).intValue());

        InputStream is = new FileInputStream(file);
        OutputStream os = response.getOutputStream();

        byte[] buffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = is.read(buffer)) != -1) {
            os.write(buffer, 0, bytesRead);
        }
        is.close();
        os.flush();
        os.close();
    }
}
