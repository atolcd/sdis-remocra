package fr.sdis83.remocra.repository;

import javax.inject.Inject;
import org.jooq.ContextTransactionalCallable;
import org.jooq.DSLContext;
import org.jooq.TransactionalRunnable;

public class TransactionManager {

  private final DSLContext context;

  @Inject
  public TransactionManager(DSLContext context) {
    this.context = context;
  }

  public void transaction(TransactionalRunnable request) {
    context.transaction(request);
  }

  public <T> T transactionResult(ContextTransactionalCallable<T> transactional) {
    return context.transactionResult(configuration -> transactional.run());
  }
}
