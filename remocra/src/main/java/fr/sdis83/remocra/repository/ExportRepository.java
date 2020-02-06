package fr.sdis83.remocra.repository;

import static fr.sdis83.remocra.db.model.remocra.Tables.DOCUMENT;
import static fr.sdis83.remocra.db.model.remocra.Tables.PROCESSUS_ETL_DOCUMENT;
import static org.jooq.impl.DSL.row;

import java.io.File;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import fr.sdis83.remocra.db.model.remocra.tables.pojos.Document;
import fr.sdis83.remocra.db.model.remocra.tables.pojos.ProcessusEtlDocument;
import org.apache.log4j.Logger;
import org.joda.time.Instant;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.dao.EmptyResultDataAccessException;

@Configuration
public class ExportRepository {
  private final Logger logger = Logger.getLogger(getClass());


  @Autowired
  DSLContext context;


  public ExportRepository() {

  }

  @PersistenceContext
  private EntityManager entityManager;

  @Bean
  public ExportRepository exportRepository(DSLContext context) {
    return new ExportRepository(context);
  }

  ExportRepository(DSLContext context) {
    this.context = context;
  }

  public String getExportFilePathFromCode(String code) {
    try {
      ProcessusEtlDocument ped = context.select().from(PROCESSUS_ETL_DOCUMENT).where(PROCESSUS_ETL_DOCUMENT.CODE.eq(code)).fetchInto(ProcessusEtlDocument.class).get(0);
      if (ped == null) {
        return null;
      }
      Document d = context.select().from(DOCUMENT).join(PROCESSUS_ETL_DOCUMENT).on(DOCUMENT.ID.eq(PROCESSUS_ETL_DOCUMENT.DOCUMENT)).where(PROCESSUS_ETL_DOCUMENT.CODE.eq(code)).fetchInto(Document.class).get(0);
      if (d == null) {
        return null;
      }
      return d.getRepertoire() + File.separator + d.getFichier();
    } catch (EmptyResultDataAccessException e) {
      return null;
    } catch (Exception e) {
      return null;
    }
  }


  public boolean setExportAccuseFromCode(String code) {
    try {
      ProcessusEtlDocument ped = context.select().from(PROCESSUS_ETL_DOCUMENT).where(PROCESSUS_ETL_DOCUMENT.CODE.eq(code)).fetchInto(ProcessusEtlDocument.class).get(0);

      if (ped == null) {
        return false;
      } else if (ped.getAccuse() == null) {
        ped.setAccuse(new Instant());
        context.update(PROCESSUS_ETL_DOCUMENT)
            .set(PROCESSUS_ETL_DOCUMENT.ACCUSE
                ,new Instant())
            .where(PROCESSUS_ETL_DOCUMENT.ID.eq(ped.getId())).execute();
      }
      return true;
    } catch (Exception e) {
      return false;
    }
  }


}
