package fr.sdis83.remocra.repository;

import static fr.sdis83.remocra.db.model.remocra.Tables.DOCUMENT;
import static fr.sdis83.remocra.db.model.remocra.Tables.HYDRANT_DOCUMENT;

import fr.sdis83.remocra.db.model.remocra.tables.pojos.Document;
import org.joda.time.Instant;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DocumentRepository {

  @Autowired DSLContext context;

  DocumentRepository(DSLContext context) {
    this.context = context;
  }

  public DocumentRepository() {}

  @Bean
  public DocumentRepository documentRepository(DSLContext context) {
    return new DocumentRepository(context);
  }

  public Document getDocument(String nomFichier) {
    return context
        .selectFrom(DOCUMENT)
        .where(DOCUMENT.FICHIER.eq(nomFichier))
        .fetchOneInto(Document.class);
  }

  /**
   * Insert le document en base
   *
   * @param nom du document
   * @param repertoire où le document est stocké
   * @return le pojo Document qui est créé
   */
  public Document insertDocument(String nom, String repertoire) {
    // On met le nom comme code auquel on enlève les points + les espaces
    String code = nom.replaceAll("\\W", "");
    return context
        .insertInto(DOCUMENT)
        .set(DOCUMENT.CODE, code)
        .set(DOCUMENT.DATE, new Instant())
        .set(DOCUMENT.FICHIER, nom)
        .set(DOCUMENT.REPERTOIRE, repertoire)
        .set(DOCUMENT.TYPE, "HYDRANT")
        .set(DOCUMENT.DATE_DOC, new Instant())
        .returning()
        .fetchOne()
        .into(Document.class);
  }

  /**
   * Insert le lien entre un hydrant et un document en base
   *
   * @param idDocument
   * @param idHydrant
   */
  public void insertLienDocumentHydrant(Long idDocument, Long idHydrant) {
    context
        .insertInto(HYDRANT_DOCUMENT)
        .set(HYDRANT_DOCUMENT.DOCUMENT, idDocument)
        .set(HYDRANT_DOCUMENT.HYDRANT, idHydrant)
        .execute();
  }
}
