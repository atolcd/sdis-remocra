package fr.sdis83.remocra.repository;

import static fr.sdis83.remocra.db.model.remocra.Tables.PROCESSUS_ETL;
import static fr.sdis83.remocra.db.model.remocra.Tables.PROCESSUS_ETL_PARAMETRE;
import static fr.sdis83.remocra.db.model.remocra.Tables.PROCESSUS_ETL_PLANIFICATION;
import static fr.sdis83.remocra.db.model.remocra.Tables.PROCESSUS_ETL_PLANIFICATION_PARAMETRE;
import static fr.sdis83.remocra.db.model.remocra.Tables.PROCESSUS_ETL_STATUT;
import static org.quartz.CronScheduleBuilder.cronSchedule;

import java.text.ParseException;
import java.util.List;
import java.util.logging.Level;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import fr.sdis83.remocra.db.model.remocra.tables.pojos.ProcessusEtlPlanification;
import fr.sdis83.remocra.db.model.remocra.tables.pojos.ProcessusEtlPlanificationParametre;
import fr.sdis83.remocra.jobs.ProcessEtlJob;
import fr.sdis83.remocra.quartz.QuartzScheduler;
import fr.sdis83.remocra.service.UtilisateurService;
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
import org.quartz.SimpleScheduleBuilder;
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

  @Autowired
  DSLContext context;


  @Autowired
  private UtilisateurService utilisateurService;

  public ProcessusEtlPlanificationRepository() {

  }

  @PersistenceContext
  private EntityManager entityManager;

  @Bean
  public ProcessusEtlPlanificationRepository processusEtlPlanificationRepository(DSLContext context) throws SchedulerException {
    return new ProcessusEtlPlanificationRepository(context);
  }

  public ProcessusEtlPlanificationRepository(DSLContext context) throws SchedulerException {
    this.context = context;
    try{
      List<ProcessusEtlPlanification> newPlannifs = this.getAll();
      scheduleJobs(newPlannifs);
    }catch(org.jooq.exception.DataAccessException e){
      logger.trace( e);
    }
  }


  public List<ProcessusEtlPlanification> getAll() {
    List<ProcessusEtlPlanification> l = null;
    l = context.select().from(PROCESSUS_ETL_PLANIFICATION).fetchInto(ProcessusEtlPlanification.class);
    return l;
  }

  public int count() {
    return context.fetchCount(context.select().from(PROCESSUS_ETL_PLANIFICATION));
  }


@Transactional
  public void insertProcessusEtl(Long idPlannification, Long idUtilisateur) throws ParseException {
  Instant t = new Instant();
  context.insertInto(PROCESSUS_ETL, PROCESSUS_ETL.MODELE, PROCESSUS_ETL.STATUT, PROCESSUS_ETL.UTILISATEUR, PROCESSUS_ETL.PRIORITE, PROCESSUS_ETL.DEMANDE)
      .values(context.select(PROCESSUS_ETL_PLANIFICATION.MODELE).from(PROCESSUS_ETL_PLANIFICATION).where(PROCESSUS_ETL_PLANIFICATION.ID.eq(idPlannification)).fetchOne().value1()
          ,context.select(PROCESSUS_ETL_STATUT.ID).from(PROCESSUS_ETL_STATUT).where(PROCESSUS_ETL_STATUT.CODE.eq("C")).fetchOne().value1()
          ,Long.valueOf(idUtilisateur)
          ,Long.valueOf(1)
          ,t).execute();
  Long idProcess  = context.select(DSL.max((PROCESSUS_ETL.ID))).from(PROCESSUS_ETL).fetchOne().value1();
  if(idProcess != null){
    List<ProcessusEtlPlanificationParametre> parametres = context.select().from(PROCESSUS_ETL_PLANIFICATION_PARAMETRE)
        .where(PROCESSUS_ETL_PLANIFICATION_PARAMETRE.PROCESSUS_ETL_PLANIFICATION.eq(idPlannification)).fetchInto(ProcessusEtlPlanificationParametre.class);
    for(ProcessusEtlPlanificationParametre parametre : parametres){
      context.insertInto(PROCESSUS_ETL_PARAMETRE, PROCESSUS_ETL_PARAMETRE.PROCESSUS, PROCESSUS_ETL_PARAMETRE.PARAMETRE, PROCESSUS_ETL_PARAMETRE.VALEUR)
          .values(idProcess
              ,parametre.getParametre()
              ,parametre.getValeur()).execute();
    }

  }
  }



  public void rescheduleJobs(List<ProcessusEtlPlanification> toDeletePlannifs, List<ProcessusEtlPlanification> toStartPlannifs) throws SchedulerException {
    killJobs(toDeletePlannifs);
    scheduleJobs(toStartPlannifs);
  }

  public void killJobs(List<ProcessusEtlPlanification> toDeletePlannifs) throws SchedulerException {
    if(toDeletePlannifs.size() != 0){
      java.util.logging.Logger.getLogger(QuartzScheduler.class.getName()).log(Level.INFO, "Arrêt des  jobs");
    } else {
      java.util.logging.Logger.getLogger(QuartzScheduler.class.getName()).log(Level.INFO, "Aucun job à arrêter");
    }
    //Scheduler scheduler = this.getScheduler() != null ? this.getScheduler() : new StdSchedulerFactory().getScheduler();
    if (scheduler==null) {
      java.util.logging.Logger.getLogger(QuartzScheduler.class.getName()).log(Level.INFO, "------------------ Nouveau scheduler (killJobs)");
      scheduler = new StdSchedulerFactory().getScheduler();
    }

    for (ProcessusEtlPlanification p : toDeletePlannifs) {
      java.util.logging.Logger.getLogger(QuartzScheduler.class.getName()).log(Level.INFO, "------------------ DELETE : " + "processusEtlJob" + p.getId().longValue());

      for (String groupName : scheduler.getJobGroupNames()) {
        for (JobKey jobKey : scheduler.getJobKeys(GroupMatcher.jobGroupEquals(groupName))) {
          String jobName = jobKey.getName();
          if (jobName.equals("processusEtlJob" + p.getId().longValue())) {
            scheduler.interrupt(jobKey);
            scheduler.deleteJob(jobKey);
            java.util.logging.Logger.getLogger(QuartzScheduler.class.getName()).log(Level.INFO, "------------------ IS STOPPED : " + "processusEtlJob" + p.getId().longValue());

          }

        }
      }
    }
  }

  public void scheduleJobs(List<ProcessusEtlPlanification> newPlannifs) throws SchedulerException {
    if(newPlannifs.size() != 0){
      java.util.logging.Logger.getLogger(QuartzScheduler.class.getName()).log(Level.INFO, "Chargement des nouveaux jobs");
    } else {
      java.util.logging.Logger.getLogger(QuartzScheduler.class.getName()).log(Level.INFO, "Aucun job a été détecté");
    }

//    Scheduler scheduler = this.getScheduler() != null ? this.getScheduler() : new StdSchedulerFactory().getScheduler();
//    scheduler.start();
//    this.setScheduler(scheduler);
    if (scheduler==null) {
      java.util.logging.Logger.getLogger(QuartzScheduler.class.getName()).log(Level.INFO, "------------------ Nouveau scheduler (scheduleJobs)");
      scheduler = new StdSchedulerFactory().getScheduler();
    }
    if (!scheduler.isStarted()) {
      scheduler.start();
    }
    for (ProcessusEtlPlanification p : newPlannifs) {
      java.util.logging.Logger.getLogger(QuartzScheduler.class.getName()).log(Level.INFO, "Chargement des jobs");
      int index = Integer.parseInt(String.valueOf(p.getId()));
      try {
        JobDataMap j = new JobDataMap();
        j.put("repository", this);
        Long idUtilisateur = 143l;//utilisateurService.getCurrentUtilisateur().getId();
        j.put("idUtilisateur", idUtilisateur);
        JobDetail job = JobBuilder.newJob(ProcessEtlJob.class).withIdentity("processusEtlJob"+index, "group"+index).usingJobData(j).build();
        //((ProcessEtlJob)job).setProcessusEtlPlanificationRepository(this);
        Trigger trigger = TriggerBuilder.newTrigger().withIdentity("simpleTrigger"+index, "group"+index)
            .withSchedule(cronSchedule(p.getExpression())).build();
        scheduler.scheduleJob(job, trigger);
        java.util.logging.Logger.getLogger(QuartzScheduler.class.getName()).log(Level.INFO, "------------------ CREAtion de nouveau job processusEtlJob"+index);

      } catch (SchedulerException ex) {
        java.util.logging.Logger.getLogger(QuartzScheduler.class.getName()).log(Level.SEVERE ,"------------------ ERREUR AU CREATION DE JOB", ex);
      }
    }
  }
@Transactional
  public List<ProcessusEtlPlanification> getPlanifById(Long id) {
    List<ProcessusEtlPlanification> l = null;
    l = context.select().from(PROCESSUS_ETL_PLANIFICATION).where(PROCESSUS_ETL_PLANIFICATION.OBJET_CONCERNE.eq(id)).fetchInto(ProcessusEtlPlanification.class);
    return l;
  }

  @Transactional
  public void deletePlanifs(Long id) {
     context.select().from(PROCESSUS_ETL_PLANIFICATION).where(PROCESSUS_ETL_PLANIFICATION.OBJET_CONCERNE.eq(id)).fetchInto(ProcessusEtlPlanification.class);
  }
}