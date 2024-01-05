package fr.sdis83.remocra.repository;

import static fr.sdis83.remocra.db.model.remocra.Tables.TYPE_HYDRANT_IMPORTCTP_ERREUR;

import fr.sdis83.remocra.db.model.remocra.tables.pojos.TypeHydrantImportctpErreur;
import java.util.Map;
import org.apache.log4j.Logger;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TypeHydrantImportCtpErreurRepository {
  private final Logger logger = Logger.getLogger(getClass());

  @Autowired DSLContext context;

  /**
   * Retourne la liste de tous les objets sous forme de Map dont la clé est le *code*
   *
   * @return Map<String, TypeHydrantImportctpErreur>
   */
  public Map<String, TypeHydrantImportctpErreur> getAll() {
    return context
        .selectFrom(TYPE_HYDRANT_IMPORTCTP_ERREUR)
        .fetchMap(TYPE_HYDRANT_IMPORTCTP_ERREUR.CODE, TypeHydrantImportctpErreur.class);
  }
}
