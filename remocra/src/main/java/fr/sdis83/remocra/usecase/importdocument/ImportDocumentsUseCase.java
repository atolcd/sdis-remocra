package fr.sdis83.remocra.usecase.importdocument;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import fr.sdis83.remocra.db.model.remocra.tables.pojos.Document;
import fr.sdis83.remocra.repository.DocumentRepository;
import fr.sdis83.remocra.repository.HydrantRepository;
import fr.sdis83.remocra.repository.ParamConfRepository;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.math3.util.Pair;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ImportDocumentsUseCase {

  @Autowired ParamConfRepository paramConfRepository;

  @Autowired DocumentRepository documentRepository;

  @Autowired HydrantRepository hydrantRepository;

  public ImportDocumentsUseCase() {}

  private final Logger logger = Logger.getLogger(getClass());

  public static enum Severity {
    INFO,
    WARN,
    ERROR
  }

  // Définition des constantes qui vont servir à récupérer les dossiers / fichiers
  public static final String NOM_FICHIER_CSV = "data.csv";
  public static final String NOM_DOSSIER_AVEC_DOCUMENTS = "documents";

  private CsvMapper csvMapper = new CsvMapper();

  public List<Pair<Severity, String>> execute() {
    // On va chercher dans la table param_conf l'emplacement des éléments
    Path cheminRoot =
        Paths.get(paramConfRepository.getByCle(ParamConfRepository.DOSSIER_ROOT).getValeur());
    Path cheminIntegration =
        Paths.get(
            cheminRoot.toString(),
            paramConfRepository.getByCle(ParamConfRepository.DOSSIER_INTEGRATION).getValeur());
    Path cheminSauvegardeDocuments =
        Paths.get(
            cheminRoot.toString(),
            paramConfRepository
                .getByCle(ParamConfRepository.DOSSIER_SAUVEGARDE_DOCUMENTS)
                .getValeur());

    // On crée une liste de pair de Sevrity et String qui va regrouper toutes les erreurs que
    // l'intégration a rencontrées
    List<Pair<Severity, String>> listeErreurs = new ArrayList<>();

    // On regarde si tous les chemins qu'on va utiliser existent bien
    File depotDir = new File(cheminSauvegardeDocuments.toString());
    if (!depotDir.exists()) {
      logger.info("Le fichier de sauvegarde des documents n'existe pas.");
      // Créer le répertoire
      if (!depotDir.mkdir()) {
        listeErreurs.add(error("Impossible de créer le répertoire " + cheminSauvegardeDocuments));
      }
    }

    if (!Files.exists(cheminIntegration)) {
      listeErreurs.add(error("Le dossier " + cheminIntegration + " n'existe pas."));
      return listeErreurs;
    }
    if (!Files.exists(Paths.get(cheminIntegration.toString(), NOM_DOSSIER_AVEC_DOCUMENTS))) {
      listeErreurs.add(error("Le dossier " + NOM_DOSSIER_AVEC_DOCUMENTS + " n'existe pas."));
      return listeErreurs;
    }
    if (!Files.exists(Paths.get(cheminIntegration.toString(), NOM_FICHIER_CSV))) {
      listeErreurs.add(error("Le fichier csv " + NOM_FICHIER_CSV + " n'existe pas."));
      return listeErreurs;
    }

    File folder = new File(cheminIntegration.toString());
    List<File> files = Arrays.asList(folder.listFiles());

    // On définit les fichiers datas
    List<DataCsvImport> listeDocumentWithHydrant = null;
    List<File> listeFichiers = new ArrayList<>();

    if (files.isEmpty()) {
      listeErreurs.add(warn("Le dossier est vide"));
      return listeErreurs;
    }

    for (File file : files) {
      if (file.getName().equals(NOM_FICHIER_CSV)) {
        try {
          CsvSchema csvSchema =
              csvMapper.typedSchemaFor(DataCsvImport.class).withHeader().withColumnSeparator(',');

          MappingIterator<DataCsvImport> dataCsvImport =
              csvMapper
                  .readerWithTypedSchemaFor(DataCsvImport.class)
                  .with(csvSchema)
                  .readValues(file);

          listeDocumentWithHydrant = dataCsvImport.readAll();

        } catch (Exception e) {
          listeErreurs.add(error("Le format du fichier " + NOM_FICHIER_CSV + " est incorrect."));
        }
      }
    }

    // Si la liste est vide, on ne peut rien faire, on renvoie l'erreur
    if (listeDocumentWithHydrant.isEmpty()) {
      listeErreurs.add(error("Le fichier " + NOM_FICHIER_CSV + " est vide"));
      return listeErreurs;
    }

    for (DataCsvImport data : listeDocumentWithHydrant) {
      // On vérifie si l'hydrant existe en base
      Long idHydrant = hydrantRepository.getIdHydrantByNumero(data.getNumeroHydrant());
      if (idHydrant == null) {
        listeErreurs.add(error("Le numéro d'hydrant n'existe pas : " + data.getNumeroHydrant()));
        continue;
      }
      if (!Files.exists(
          Paths.get(
              cheminIntegration.toString(), NOM_DOSSIER_AVEC_DOCUMENTS, data.getDocument()))) {
        listeErreurs.add(error("Le document " + data.getDocument() + " n'existe pas"));
      }

      Document docExistant = documentRepository.getDocument(data.getDocument());
      if (docExistant != null) {
        listeErreurs.add(warn("Le document " + data.getDocument() + " existe déjà en base"));
      } else {
        docExistant =
            documentRepository.insertDocument(
                data.getDocument(), cheminSauvegardeDocuments.toString());
        // puis on le copie dans le répertoire cible
        try {
          Files.copy(
              Paths.get(
                  cheminIntegration.toString(), NOM_DOSSIER_AVEC_DOCUMENTS, data.getDocument()),
              Paths.get(cheminSauvegardeDocuments.toString(), data.getDocument()));
        } catch (IOException e) {
          listeErreurs.add(
              error(
                  "Impossible de copier le fichier "
                      + data.getDocument()
                      + " dans le répertoire "
                      + cheminSauvegardeDocuments));
        }
      }

      if (docExistant == null) {
        listeErreurs.add(
            error(
                "Impossible de trouver le fichier " + data.getDocument() + " dans le répertoire."));
        continue;
      }
      // Si tout va bien, on insert
      documentRepository.insertLienDocumentHydrant(docExistant.getId(), idHydrant);
    }

    if (listeErreurs.isEmpty()) {
      listeErreurs.add(info("L'intégration des documents a été effectuée avec succès."));
    }

    return listeErreurs;
  }

  public Pair<Severity, String> info(String str) {
    return new Pair(Severity.INFO, str);
  }

  public Pair<Severity, String> warn(String str) {
    return new Pair(Severity.WARN, str);
  }

  public Pair<Severity, String> error(String str) {
    return new Pair(Severity.ERROR, str);
  }
}
