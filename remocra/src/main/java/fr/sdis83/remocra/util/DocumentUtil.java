package fr.sdis83.remocra.util;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.util.Date;
import java.util.logging.Level;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;

/*import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfSignatureAppearance;
import com.itextpdf.text.pdf.PdfStamper;*/
import fr.opensagres.xdocreport.converter.ConverterRegistry;
import fr.opensagres.xdocreport.converter.ConverterTypeTo;
import fr.opensagres.xdocreport.converter.IConverter;
import fr.opensagres.xdocreport.converter.Options;
import fr.opensagres.xdocreport.core.document.DocumentKind;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.security.authentication.encoding.MessageDigestPasswordEncoder;
import org.springframework.web.multipart.MultipartFile;

import fr.sdis83.remocra.domain.remocra.Document;
import fr.sdis83.remocra.domain.remocra.Document.TypeDocument;
import fr.sdis83.remocra.web.serialize.StreamFileUtils;

@Configurable
public class DocumentUtil {

    protected final Logger logger = Logger.getLogger(getClass());

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

    public Document createNonPersistedDocument(TypeDocument typeDocument, MultipartFile mf, String depotRepertoire)
            throws Exception {
        if (mf == null) {
            throw new FileUploadException("Un fichier n'a pas été trouvé");
        }

        String fichier = mf.getOriginalFilename();
        String code = messageDigestPasswordEncoder.encodePassword(new Date().getTime() + mf.getOriginalFilename(),
                null);
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

    public Document createNonPersistedDocument(TypeDocument typeDocument, File f, String depotRepertoire)
            throws Exception {
        if (f == null) {
            throw new FileUploadException("Un fichier n'a pas été trouvé");
        }

        String fichier = f.getName();
        String code = messageDigestPasswordEncoder.encodePassword(new Date().getTime() + fichier,
                null);
        String repertoire = depotRepertoire + File.separator + code + File.separator;

        saveFileToHD(f, repertoire, fichier);

        Document d = new Document();
        d.setDateDoc(new Date());
        d.setType(typeDocument);
        d.setCode(code);
        d.setRepertoire(repertoire);
        d.setFichier(fichier);

        return d;
    }

    public Document createNonPersistedDocument(TypeDocument typeDocument, BufferedImage buffImage, String filename,
            String depotRepertoire) throws SecurityException, IOException, FileUploadException {
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
    private void saveFileToHD(File f, String repertoire, String fichier) throws Exception {

        // Création du répertoire d'accueil si nécessaire
        File depotDir = assumeDirExists(repertoire);

        String targetFilePath = depotDir + File.separator + fichier;
        File targetTmpFile = new File(depotDir + File.separator + fichier + ".tmp");
        if (depotDir.canWrite()) {
            // Copie dans un fichier temporaire
            f.renameTo(targetTmpFile);
            // Fichier définitif : on retire le .tmp
            targetTmpFile.renameTo(new File(targetFilePath));
            // Droits ?
        } else {
            throw new SecurityException("Impossible de créer le fichier " + targetFilePath);
        }
    }

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
    public File assumeDirExists(String repertoire) throws SecurityException {
        File dir = new File(repertoire);
        if (!dir.exists()) {
            // Créer le répertoire
            if (!dir.mkdirs()) {
                throw new SecurityException("Impossible de créer le répertoire " + repertoire);
            }
        }
        return dir;
    }

    public File getFile(String filePath, String code) {
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

    public void downloadDocument(String filePath, String code, HttpServletResponse response) throws IOException {
        File file = getFile(filePath, code);
        if (file == null) {
            response.setStatus(404);
            return;
        }

        String fileName = file.getName();
        response.setHeader("Content-Disposition", "inline; filename=\"" + fileName + "\"");
        response.setHeader("Content-Type", "application/force-download");
        setContentLengthHeader(response, file);

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

    public void showDocument(String filePath, String code, HttpServletResponse response) throws IOException {
        File file = getFile(filePath, code);
        if (file == null) {
            response.setStatus(404);
            return;
        }

        String contentType = StreamFileUtils.getContentTypeFromFile(file);
        if (contentType != null) {
            response.setContentType(contentType);
        }
        setContentLengthHeader(response, file);

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

    public void setContentLengthHeader(HttpServletResponse response, File file) {
        if (response != null && file != null) {
            long length = file.length();
            if (length <= Integer.MAX_VALUE) {
                response.setContentLength((int) length);
            } else {
                response.addHeader("Content-Length", Long.toString(length));
            }
        }
    }

    public String getSousType(MultipartFile f){
        String type = f.getContentType().split("/")[0];
        if(type.equals("image")){
            return Document.SousTypeDocument.IMAGE.toString();
        }else if (type.equals("video") || type.equals("audio")){
            return Document.SousTypeDocument.MEDIA.toString();
        }
        return Document.SousTypeDocument.AUTRE.toString();
    }

    public void generePdf(FileInputStream textFileInputStream, FileOutputStream pdfFileOutputStream){
        //Transformation du ott en pdf
        try {
            Options options = Options.getFrom(DocumentKind.ODT).to(ConverterTypeTo.PDF);
            IConverter converter = ConverterRegistry.getRegistry().getConverter(options);
            InputStream in = textFileInputStream;
            OutputStream out = pdfFileOutputStream;
            final java.util.logging.Logger app = java.util.logging.Logger.getLogger("org.odftoolkit.odfdom.pkg.OdfXMLFactory");
            app.setLevel(Level.WARNING);
            converter.convert(in, out, options);

            textFileInputStream.close();
            pdfFileOutputStream.close();
            in.close();
            out.close();
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    /*public void signPdf(String cheminPdf, String cheminCertificat, String mdpCertificat){
        try{
            //Création d'un keyStore
            KeyStore ks = KeyStore.getInstance("pkcs12");
            //Chargement du certificat dans le store
            ks.load(new FileInputStream(cheminCertificat), mdpCertificat.toCharArray());
            String alias = (String)ks.aliases().nextElement();
            //Récupération de la clé privée
            PrivateKey key = (PrivateKey)ks.getKey(alias, mdpCertificat.toCharArray());
            //Récupération de la chaine de certificats
            Certificate[] chaine = ks.getCertificateChain(alias);

            //Lecture du document
            PdfReader pdfReader = new PdfReader(cheminPdf);
            File outputFile = new File(cheminPdf.replace(".", "_signed."));

            //Création du tampon de signature
            PdfStamper pdfStamper = PdfStamper.createSignature(pdfReader, null, '\0', outputFile);
            PdfSignatureAppearance sap = pdfStamper.getSignatureAppearance();
            sap.setCrypto(key, chaine, null, PdfSignatureAppearance.SELF_SIGNED);
            pdfStamper.setFormFlattening(true);
            pdfStamper.close();

            //Suppression du PDF signé et renommage pdf signé
            new File(cheminPdf).delete();
            outputFile.renameTo(new File(cheminPdf));

        } catch (Exception e){
            e.printStackTrace();
        }
    }*/
}
