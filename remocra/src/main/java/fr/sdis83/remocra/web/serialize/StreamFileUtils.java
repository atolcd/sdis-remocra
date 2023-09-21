package fr.sdis83.remocra.web.serialize;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import javax.activation.MimetypesFileTypeMap;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.io.IOUtils;

public class StreamFileUtils {

  public static void streamFileOut(
      HttpServletResponse response, byte[] contenu, String nomFichier) {
    try {
      response.setHeader("Content-Disposition", "attachment;filename=\"" + nomFichier + "\"");
      response.setContentLength(contenu.length);
      OutputStream out = response.getOutputStream();
      IOUtils.copy(new ByteArrayInputStream(contenu), out);
      out.flush();
    } catch (IOException e) {
      e.printStackTrace();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * Retourne le Content-Type adéquat en fonction du fichier file.
   *
   * @param file
   * @return
   */
  public static String getContentTypeFromFile(File file) {
    if (file == null) {
      return null;
    }
    String[] tokens = file.getName().split("\\.(?=[^\\.]+$)");
    String extension = tokens[tokens.length - 1].toLowerCase();
    String contentType = null;
    if ("jpg".equals(extension) || "jpeg".equals(extension)) {
      contentType = "image/jpeg";
    } else if ("png".equals(extension)) {
      contentType = "image/png";
    } else if ("gif".equals(extension)) {
      contentType = "image/gif";
    } else if ("css".equals(extension)) {
      contentType = "text/css";
    } else if ("json".equals(extension)) {
      contentType = "application/json";
    } else if ("js".equals(extension)) {
      contentType = "text/javascript";
    } else if ("html".equals(extension)) {
      contentType = "text/html";
    } else if ("svg".equals(extension)) {
      contentType = "image/svg+xml";
    } else {
      // On essaie de deviner
      contentType = new MimetypesFileTypeMap().getContentType(file);
    }
    return contentType;
  }
}
