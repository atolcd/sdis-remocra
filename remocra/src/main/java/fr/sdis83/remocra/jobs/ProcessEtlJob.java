package fr.sdis83.remocra.jobs;

import fr.sdis83.remocra.repository.ProcessusEtlPlanificationRepository;
import org.apache.log4j.Logger;
import org.quartz.InterruptableJob;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.UnableToInterruptJobException;

public class ProcessEtlJob implements InterruptableJob {

  private final Logger logger = Logger.getLogger(getClass());

  @Override
  public void execute(JobExecutionContext ctx) throws JobExecutionException {
    Long idProcessPlan = Long.valueOf(ctx.getJobDetail().getKey().getName().substring(15));
    Long idUtilisateur =
        Long.valueOf(String.valueOf(ctx.getJobDetail().getJobDataMap().get("idUtilisateur")));
    ProcessusEtlPlanificationRepository processusEtlPlanificationRepository =
        (ProcessusEtlPlanificationRepository) ctx.getJobDetail().getJobDataMap().get("repository");
    logger.info("Execution du job" + idProcessPlan);
    try {
      processusEtlPlanificationRepository.insertProcessusEtl(idProcessPlan, idUtilisateur);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Override
  public void interrupt() throws UnableToInterruptJobException {}
}
