package fr.sdis83.remocra.util;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Date;

import javax.imageio.ImageIO;

import org.apache.commons.fileupload.FileUploadException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.security.authentication.encoding.MessageDigestPasswordEncoder;
import org.springframework.web.multipart.MultipartFile;

import fr.sdis83.remocra.domain.remocra.Document;
import fr.sdis83.remocra.domain.remocra.Document.TypeDocument;

@Configurable
public class DocumentUtil {

    @Autowired
    private MessageDigestPasswordEncoder messageDigestPasswordEncoder;

    private static DocumentUtil _instance;

    public static DocumentUtil getInstance() {
        if (_instance == null) {
            _instance = new DocumentUtil();
        }
        return _instance;
    }

    private DocumentUtil() {
    }

    public Document createNonPersistedDocument(TypeDocument typeDocument, MultipartFile mf, String depotRepertoire) throws Exception {
        if (mf == null) {
            throw new FileUploadException("Un fichier n'a pas été trouvé");
        }

        String fichier = mf.getOriginalFilename();
        String code = messageDigestPasswordEncoder.encodePassword(new Date().getTime() + mf.getOriginalFilename(), null);
        String repertoire = depotRepertoire + File.separator + code + File.separator;

        saveFileToHD(mf, repertoire, fichier);

        Document d = new Document();
        d.setDateDoc(new Date());
        d.setType(typeDocument);
        d.setCode(code);
        d.setRepertoire(repertoire);
        d.setFichier(fichier);

        return d;
    }

    public Document createNonPersistedDocument(TypeDocument typeDocument, BufferedImage buffImage, String filename, String depotRepertoire) throws SecurityException, IOException,
            FileUploadException {
        if (buffImage == null || buffImage.getHeight() == 0 || buffImage.getWidth() == 0) {
            throw new FileUploadException("Fichier vide");
        }

        String code = messageDigestPasswordEncoder.encodePassword(new Date().getTime() + filename, null);
        String repertoire = depotRepertoire + File.separator + code + File.separator;

        // Création du répertoire d'accueil si nécessaire
        File depotDir = assumeDirExists(repertoire);
        String targetFilePath = depotDir + File.separator + filename;
        String tmpTargetFilePath = targetFilePath + ".tmp";
        if (depotDir.canWrite()) {
            // Copie dans un fichier temporaire
            ImageIO.write(buffImage, "jpg", new File(tmpTargetFilePath));
            File targetTmpFile = new File(tmpTargetFilePath);
            // Fichier définitif : on retire le .tmp
            targetTmpFile.renameTo(new File(targetFilePath));
            // Droits ?
        } else {
            throw new SecurityException("Impossible de créer le fichier " + targetFilePath);
        }

        Document d = new Document();
        d.setDateDoc(new Date());
        d.setType(typeDocument);
        d.setCode(code);
        d.setRepertoire(repertoire);
        d.setFichier(filename);

        return d;
    }

    /**
     * Suppression fichier (et du répertoire conteneur) sur disque du document.
     * 
     * @param document
     * @throws Exception
     */
    public void deleteHDFile(Document document) throws Exception {

        String repertoirePath = document.getRepertoire();
        String fichierPath = repertoirePath + File.separator + document.getFichier();
        File repertoireFile = new File(repertoirePath);

        if (repertoireFile.canWrite()) {
            // Suppression du fichier
            new File(fichierPath).delete();

            // Suppression du répertoire conteneur (sauf s'il n'est pas vide)
            repertoireFile.delete();
        } else {
            throw new SecurityException("Impossible de supprimer le fichier " + fichierPath);
        }
    }

    /**
     * Sauvegarde du fichier sur disque.
     * 
     * @param mf
     * @param repertoire
     * @param fichier
     * 
     * @throws Exception
     */
    private void saveFileToHD(MultipartFile mf, String repertoire, String fichier) throws Exception {

        // Création du répertoire d'accueil si nécessaire
        File depotDir = assumeDirExists(repertoire);

        String targetFilePath = depotDir + File.separator + fichier;
        File targetTmpFile = new File(depotDir + File.separator + fichier + ".tmp");
        if (depotDir.canWrite()) {
            // Copie dans un fichier temporaire
            mf.transferTo(targetTmpFile);
            // Fichier définitif : on retire le .tmp
            targetTmpFile.renameTo(new File(targetFilePath));
            // Droits ?
        } else {
            throw new SecurityException("Impossible de créer le fichier " + targetFilePath);
        }
    }

    /**
     * S'assure que le répertoire existe en le créant si besoin.
     * 
     * @param repertoire
     * @return
     * @throws SecurityException
     *             si le répertoire n'a pas pu être créé
     */
    private File assumeDirExists(String repertoire) throws SecurityException {
        File dir = new File(repertoire);
        if (!dir.exists()) {
            // Créer le répertoire
            if (!dir.mkdirs()) {
                throw new SecurityException("Impossible de créer le répertoire " + repertoire);
            }
        }
        return dir;
    }

}
