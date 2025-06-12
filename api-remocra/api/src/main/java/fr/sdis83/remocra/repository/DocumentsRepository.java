package fr.sdis83.remocra.repository;

import static fr.sdis83.remocra.db.model.remocra.Tables.DOCUMENT;
import static fr.sdis83.remocra.db.model.remocra.Tables.HYDRANT_DOCUMENT;

import fr.sdis83.remocra.util.GlobalConstants;
import java.time.Instant;
import javax.inject.Inject;
import org.jooq.DSLContext;

public class DocumentsRepository {

  private final DSLContext context;

  @Inject
  public DocumentsRepository(DSLContext context) {
    this.context = context;
  }

  public Long insertDocumentTypeHydrant(
      String path, String code, Instant instant, String nomPhoto) {
    return context
        .insertInto(DOCUMENT)
        .set(DOCUMENT.CODE, code)
        .set(DOCUMENT.DATE_DOC, instant)
        .set(DOCUMENT.FICHIER, nomPhoto)
        .set(DOCUMENT.REPERTOIRE, path)
        .set(DOCUMENT.TYPE, GlobalConstants.TypeDocument.TYPE_DOCUMENT_HYDRANT.getTypeDocument())
        .returning(DOCUMENT.ID)
        .fetchOne()
        .getValue(DOCUMENT.ID);
  }

  public Boolean getDocument(String path, String code, Instant instant, String nomPhoto) {
    return context.fetchExists(
        context
            .selectFrom(DOCUMENT)
            .where(DOCUMENT.CODE.eq(code))
            .and(DOCUMENT.REPERTOIRE.eq(path))
            .and(DOCUMENT.DATE_DOC.eq(instant))
            .and(DOCUMENT.FICHIER.eq(nomPhoto)));
  }

  public void insertHydrantDocument(Long idHydrant, Long idDocument) {
    context
        .insertInto(HYDRANT_DOCUMENT)
        .set(HYDRANT_DOCUMENT.HYDRANT, idHydrant)
        .set(HYDRANT_DOCUMENT.DOCUMENT, idDocument)
        .onConflictDoNothing()
        .execute();
  }
}
