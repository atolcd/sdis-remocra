package fr.sdis83.remocra.repository;

import static fr.sdis83.remocra.db.model.remocra.Tables.PROCESSUS_ETL;
import static fr.sdis83.remocra.db.model.remocra.Tables.PROCESSUS_ETL_PARAMETRE;
import static fr.sdis83.remocra.db.model.remocra.Tables.PROCESSUS_ETL_PLANIFICATION;
import static fr.sdis83.remocra.db.model.remocra.Tables.PROCESSUS_ETL_PLANIFICATION_PARAMETRE;
import static fr.sdis83.remocra.db.model.remocra.Tables.PROCESSUS_ETL_STATUT;
import static fr.sdis83.remocra.db.model.remocra.Tables.UTILISATEUR;
import static org.quartz.CronScheduleBuilder.cronSchedule;

import fr.sdis83.remocra.db.model.remocra.tables.pojos.ProcessusEtlPlanification;
import fr.sdis83.remocra.db.model.remocra.tables.pojos.ProcessusEtlPlanificationParametre;
import fr.sdis83.remocra.jobs.ProcessEtlJob;
import fr.sdis83.remocra.usecase.parametre.ParametreDataProvider;
import java.text.ParseException;
import java.util.List;
import org.apache.log4j.Logger;
import org.joda.time.Instant;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.impl.matchers.GroupMatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.Transactional;

@Configuration
public class ProcessusEtlPlanificationRepository {

  private final Logger logger = Logger.getLogger(getClass());

  private static Scheduler scheduler;

  @Autowired DSLContext context;

  @Autowired ParametreDataProvider parametreDataProvider;

  public ProcessusEtlPlanificationRepository() {}

  @Bean
  public ProcessusEtlPlanificationRepository processusEtlPlanificationRepository(
      DSLContext context, ParametreDataProvider parametreDataProvider) throws SchedulerException {
    return new ProcessusEtlPlanificationRepository(context, parametreDataProvider);
  }

  public ProcessusEtlPlanificationRepository(
      DSLContext context, ParametreDataProvider parametreDataProvider) throws SchedulerException {
    this.context = context;
    this.parametreDataProvider = parametreDataProvider;
    try {
      List<ProcessusEtlPlanification> newPlannifs = this.getAll();
      scheduleJobs(newPlannifs);
    } catch (org.jooq.exception.DataAccessException e) {
      logger.trace(e);
    }
  }

  @Transactional
  public List<ProcessusEtlPlanification> getAll() {
    List<ProcessusEtlPlanification> l = null;
    l =
        context
            .select()
            .from(PROCESSUS_ETL_PLANIFICATION)
            .fetchInto(ProcessusEtlPlanification.class);
    return l;
  }

  public int count() {
    return context.fetchCount(context.select().from(PROCESSUS_ETL_PLANIFICATION));
  }

  @Transactional
  public void insertProcessusEtl(Long idPlannification, Long idUtilisateur) throws ParseException {
    Instant t = new Instant();
    context
        .insertInto(
            PROCESSUS_ETL,
            PROCESSUS_ETL.MODELE,
            PROCESSUS_ETL.STATUT,
            PROCESSUS_ETL.UTILISATEUR,
            PROCESSUS_ETL.PRIORITE,
            PROCESSUS_ETL.DEMANDE)
        .values(
            context
                .select(PROCESSUS_ETL_PLANIFICATION.MODELE)
                .from(PROCESSUS_ETL_PLANIFICATION)
                .where(PROCESSUS_ETL_PLANIFICATION.ID.eq(idPlannification))
                .fetchOne()
                .value1(),
            context
                .select(PROCESSUS_ETL_STATUT.ID)
                .from(PROCESSUS_ETL_STATUT)
                .where(PROCESSUS_ETL_STATUT.CODE.eq("A"))
                .fetchOne()
                .value1(),
            Long.valueOf(idUtilisateur),
            1,
            t)
        .execute();
    Long idProcess =
        context.select(DSL.max((PROCESSUS_ETL.ID))).from(PROCESSUS_ETL).fetchOne().value1();
    if (idProcess != null) {
      List<ProcessusEtlPlanificationParametre> parametres =
          context
              .select()
              .from(PROCESSUS_ETL_PLANIFICATION_PARAMETRE)
              .where(
                  PROCESSUS_ETL_PLANIFICATION_PARAMETRE.PROCESSUS_ETL_PLANIFICATION.eq(
                      idPlannification))
              .fetchInto(ProcessusEtlPlanificationParametre.class);
      for (ProcessusEtlPlanificationParametre parametre : parametres) {
        context
            .insertInto(
                PROCESSUS_ETL_PARAMETRE,
                PROCESSUS_ETL_PARAMETRE.PROCESSUS,
                PROCESSUS_ETL_PARAMETRE.PARAMETRE,
                PROCESSUS_ETL_PARAMETRE.VALEUR)
            .values(idProcess, parametre.getParametre(), parametre.getValeur())
            .execute();
      }
    }
  }

  public void rescheduleJobs(
      List<ProcessusEtlPlanification> toDeletePlannifs,
      List<ProcessusEtlPlanification> toStartPlannifs)
      throws SchedulerException {
    killJobs(toDeletePlannifs);
    scheduleJobs(toStartPlannifs);
  }

  public void killJobs(List<ProcessusEtlPlanification> toDeletePlannifs) throws SchedulerException {
    if (toDeletePlannifs.size() != 0) {
      logger.info("Arrêt des  jobs");
    } else {
      logger.info("Aucun job à arrêter");
    }
    // Scheduler scheduler = this.getScheduler() != null ? this.getScheduler() : new
    // StdSchedulerFactory().getScheduler();
    if (scheduler == null) {
      logger.info("------------------ Nouveau scheduler [killJobs]");
      scheduler = new StdSchedulerFactory().getScheduler();
    }

    for (ProcessusEtlPlanification p : toDeletePlannifs) {
      logger.info(
          "------------------ DELETE : "
              + "processusEtlJob"
              + p.getId().longValue()
              + "[killJobs]");

      for (String groupName : scheduler.getJobGroupNames()) {
        for (JobKey jobKey : scheduler.getJobKeys(GroupMatcher.jobGroupEquals(groupName))) {
          String jobName = jobKey.getName();
          if (jobName.equals("processusEtlJob" + p.getId().longValue())) {
            scheduler.interrupt(jobKey);
            scheduler.deleteJob(jobKey);
            logger.info(
                "------------------ IS STOPPED : "
                    + "processusEtlJob"
                    + p.getId().longValue()
                    + "[killJobs]");
          }
        }
      }
    }
  }

  public void scheduleJobs(List<ProcessusEtlPlanification> newPlannifs) throws SchedulerException {
    if (newPlannifs.size() != 0) {
      logger.info("Chargement des nouveaux jobs [scheduleJobs]");
    } else {
      logger.info("Aucun nouveau job n'a été détecté [scheduleJobs]");
    }

    //    Scheduler scheduler = this.getScheduler() != null ? this.getScheduler() : new
    // StdSchedulerFactory().getScheduler();
    //    scheduler.start();
    //    this.setScheduler(scheduler);
    if (scheduler == null) {
      logger.info("------------------ Nouveau scheduler [scheduleJobs]");
      scheduler = new StdSchedulerFactory().getScheduler();
    }
    if (!scheduler.isStarted()) {
      scheduler.start();
    }
    for (ProcessusEtlPlanification p : newPlannifs) {
      logger.info("Chargement des jobs [scheduleJobs]");
      int index = Integer.parseInt(String.valueOf(p.getId()));
      try {
        JobDataMap j = new JobDataMap();
        j.put("repository", this);
        // On relance les process avec le user hors ligne
        String userName = parametreDataProvider.get().getProcessOfflineUser();
        Long idUtilisateur = findOfflineUser(userName);
        j.put("idUtilisateur", idUtilisateur);
        JobDetail job =
            JobBuilder.newJob(ProcessEtlJob.class)
                .withIdentity("processusEtlJob" + index, "group" + index)
                .usingJobData(j)
                .build();
        // ((ProcessEtlJob)job).setProcessusEtlPlanificationRepository(this);
        Trigger trigger =
            TriggerBuilder.newTrigger()
                .withIdentity("simpleTrigger" + index, "group" + index)
                .withSchedule(cronSchedule(p.getExpression()))
                .build();
        scheduler.scheduleJob(job, trigger);
        logger.info(
            "------------------ Création de nouveau job processusEtlJob"
                + index
                + "[scheduleJobs]");

      } catch (SchedulerException ex) {
        logger.error("------------------ ERREUR AU CREATION DE JOB  [scheduleJobs]" + ex);
      }
    }
  }

  @Transactional
  public List<ProcessusEtlPlanification> getPlanifById(Long id) {
    List<ProcessusEtlPlanification> l = null;
    l =
        context
            .select()
            .from(PROCESSUS_ETL_PLANIFICATION)
            .where(PROCESSUS_ETL_PLANIFICATION.OBJET_CONCERNE.eq(id))
            .fetchInto(ProcessusEtlPlanification.class);
    return l;
  }

  @Transactional
  public void deletePlanifs(Long id) {
    context
        .deleteFrom(PROCESSUS_ETL_PLANIFICATION)
        .where(PROCESSUS_ETL_PLANIFICATION.OBJET_CONCERNE.eq(id));
  }

  /** find user without case sensitive */
  public Long findOfflineUser(String username) {
    Long id =
        context
            .select(UTILISATEUR.ID)
            .from(UTILISATEUR)
            .where(UTILISATEUR.IDENTIFIANT.lower().eq(username.toLowerCase()))
            .fetchOne(UTILISATEUR.ID);
    return id;
  }
}
