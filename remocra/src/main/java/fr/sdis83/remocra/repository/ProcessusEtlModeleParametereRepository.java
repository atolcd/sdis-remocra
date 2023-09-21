package fr.sdis83.remocra.repository;

import static fr.sdis83.remocra.db.model.remocra.Tables.PROCESSUS_ETL_MODELE_PARAMETRE;
import static fr.sdis83.remocra.db.model.remocra.tables.RequeteModeleParametre.REQUETE_MODELE_PARAMETRE;

import fr.sdis83.remocra.db.model.remocra.tables.pojos.ProcessusEtlModeleParametre;
import java.util.List;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ProcessusEtlModeleParametereRepository {

  @Autowired DSLContext context;

  public ProcessusEtlModeleParametereRepository() {}

  @Bean
  public ProcessusEtlModeleParametereRepository processusEtlModeleParametereRepository(
      DSLContext context) {
    return new ProcessusEtlModeleParametereRepository(context);
  }

  ProcessusEtlModeleParametereRepository(DSLContext context) {
    this.context = context;
  }

  public List<ProcessusEtlModeleParametre> getAll() {
    List<ProcessusEtlModeleParametre> l =
        context
            .select()
            .from(PROCESSUS_ETL_MODELE_PARAMETRE)
            .fetchInto(ProcessusEtlModeleParametre.class);
    return l;
  }

  public int count() {
    return context.fetchCount(context.select().from(REQUETE_MODELE_PARAMETRE));
  }

  public List<ProcessusEtlModeleParametre> getByProcessusEtlModele(Long processusEtlModele) {
    List<ProcessusEtlModeleParametre> l =
        context
            .select()
            .from(PROCESSUS_ETL_MODELE_PARAMETRE)
            .where(PROCESSUS_ETL_MODELE_PARAMETRE.MODELE.eq(processusEtlModele))
            .orderBy(PROCESSUS_ETL_MODELE_PARAMETRE.FORMULAIRE_NUM_ORDRE)
            .fetchInto(ProcessusEtlModeleParametre.class);
    return l;
  }
}
