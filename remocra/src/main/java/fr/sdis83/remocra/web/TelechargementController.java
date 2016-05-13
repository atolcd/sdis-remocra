package fr.sdis83.remocra.web;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import fr.sdis83.remocra.service.TelechargementService;
import fr.sdis83.remocra.web.serialize.StreamFileUtils;

@RequestMapping("/telechargement")
@Controller
public class TelechargementController {

    private final Logger logger = Logger.getLogger(getClass());

    @Autowired
    TelechargementService telechargementsService;

    @RequestMapping(value = "/{code}")
    public void downloadPdiDocument(@PathVariable("code") String code, HttpServletResponse response) throws IOException {
        downloadDocument(telechargementsService.getPdiFilePathFromCode(code), code, response);
    }

    @RequestMapping(value = "/document/{code}")
    public void downloadRemocraDocument(@PathVariable("code") String code, HttpServletResponse response) throws IOException {
        downloadDocument(telechargementsService.getRemocraFilePathFromCode(code), code, response);
    }

    @RequestMapping(value = "/show/{code}")
    public void showRemocraDocument(@PathVariable("code") String code, HttpServletResponse response) throws IOException {
        showDocument(telechargementsService.getRemocraFilePathFromCode(code), code, response);
    }

    protected File getFile(String filePath, String code) {
        if (filePath == null) {
            logger.info("Fichier demandé non trouvé dans la base (" + code + ")");
            return null;
        }
        File file = new File(filePath);
        if (!file.exists()) {
            logger.info("Fichier demandé non trouvé sur disque (" + code + ") : " + filePath);
            return null;
        }
        return file;
    }

    protected void downloadDocument(String filePath, String code, HttpServletResponse response) throws IOException {
        File file = getFile(filePath, code);
        if (file == null) {
            response.setStatus(404);
            return;
        }

        String fileName = file.getName();
        response.setHeader("Content-Disposition", "inline; filename=\"" + fileName+"\"");
        response.setHeader("Content-Type", "application/force-download");
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

    protected void showDocument(String filePath, String code, HttpServletResponse response) throws IOException {
        File file = getFile(filePath, code);
        if (file == null) {
            response.setStatus(404);
            return;
        }

        String contentType = StreamFileUtils.getContentTypeFromFile(file);
        if (contentType != null) {
            response.setContentType(contentType);
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
