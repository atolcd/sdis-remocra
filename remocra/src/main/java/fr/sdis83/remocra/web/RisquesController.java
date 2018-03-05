package fr.sdis83.remocra.web;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import fr.sdis83.remocra.service.ParamConfService;

@RequestMapping("/risques")
@Controller
public class RisquesController {

    private final Logger logger = Logger.getLogger(getClass());

    @Autowired
    private ParamConfService paramConfService;

    @RequestMapping(value = "/express")
    @PreAuthorize("hasRight('RISQUES_KML_R')")
    public void risqueExpress(HttpServletResponse response) throws IOException {

        String filePath = paramConfService.getPdiCheminKml() + "/risques.kml";
        File file = new File(filePath);
        if (!file.exists()) {
            logger.info("Fichier des risques express non trouvé : " + filePath);
            response.setStatus(404);
            return;
        }

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
