package fr.sdis83.remocra.web;

import fr.sdis83.remocra.domain.remocra.Document;
import fr.sdis83.remocra.domain.remocra.Document.TypeDocument;
import fr.sdis83.remocra.service.UtilisateurService;
import fr.sdis83.remocra.usecase.parametre.ParametreDataProvider;
import fr.sdis83.remocra.util.DocumentUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.multipart.MultipartFile;

public abstract class AbstractRemocraController {

  @Autowired protected UtilisateurService utilisateurService;

  // Sera utile par héritage
  @Autowired protected ParametreDataProvider parametreProvider;

  // ------------------------------
  // --- Methodes de gestion des documents
  // ------------------------------

  protected Document createNonPersistedDocument(
      TypeDocument typeDocument, MultipartFile mf, String depotRepertoire) throws Exception {
    return DocumentUtil.getInstance().createNonPersistedDocument(typeDocument, mf, depotRepertoire);
  }
}
