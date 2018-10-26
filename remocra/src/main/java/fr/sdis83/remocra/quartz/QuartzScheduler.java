package fr.sdis83.remocra.quartz;


import fr.sdis83.remocra.repository.ProcessusEtlPlanificationRepository;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.impl.StdScheduler;
import org.quartz.impl.StdSchedulerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

@Configuration
public class QuartzScheduler {

//
//  public QuartzScheduler() {
//    this.launchJob();
//  }
//
//  public void launchJob() {
//    Logger.getLogger(QuartzScheduler.class.getName()).log(Level.INFO, "Chargement des jobs");
//    List<ProcessusEtlPlanification> l = processusEtlPlanificationRepository.getAll();
//    for (ProcessusEtlPlanification p : l) {
//      int index = Integer.parseInt(String.valueOf(p.getId()));
//      try {
//        JobDetail job = JobBuilder.newJob(ProcessEtlJob.class).withIdentity("processetlJob"+index, "group"+index).build();
//        ((ProcessEtlJob)job).setProcessusEtlPlanificationRepository(processusEtlPlanificationRepository);
//        Trigger trigger = TriggerBuilder.newTrigger().withIdentity("simpleTrigger", "group"+index)
//            .withSchedule(SimpleScheduleBuilder.repeatMinutelyForever(Integer.parseInt(p.getExpression()))).build();
//        Scheduler scheduler = new StdSchedulerFactory().getScheduler();
//        scheduler.start();
//        scheduler.scheduleJob(job, trigger);
//
//      } catch (SchedulerException ex) {
//        Logger.getLogger(QuartzScheduler.class.getName()).log(Level.SEVERE, null, ex);
//      }
//    }
//    }
}
