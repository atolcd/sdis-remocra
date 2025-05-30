package fr.sdis83.remocra.repository;

import static fr.sdis83.remocra.db.model.remocra.Tables.HYDRANT;
import static fr.sdis83.remocra.db.model.remocra.Tables.HYDRANT_TOURNEES;
import static fr.sdis83.remocra.db.model.remocra.Tables.TOURNEE;

import fr.sdis83.remocra.web.model.mobilemodel.ImmutableTourneeModel;
import fr.sdis83.remocra.web.model.mobilemodel.TourneeModel;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.RecordMapper;
import org.jooq.impl.DSL;
import org.jooq.impl.SQLDataType;

public class TourneeRepository {

  private final DSLContext context;
  private final TransactionManager transactionManager;

  @Inject
  public TourneeRepository(DSLContext context, TransactionManager transactionManager) {
    this.context = context;
    this.transactionManager = transactionManager;
  }

  /**
   * Permet de retourner la liste des tournées disponibles dans REMOcRA en fonction de : *
   * l'utilisateur et donc de son organisme * de si un autre utilisateur est en train de la faire ou
   * non
   *
   * @param idsOrganisme : liste des ids de l'organisme de l'utilisateur et ces organismes enfants
   * @return la liste des tournées disponibles
   */
  public List<TourneeModel> getTourneesDisponibles(List<Long> idsOrganisme) {
    return context
        .select(TOURNEE.ID, TOURNEE.NOM, TOURNEE.AFFECTATION, TOURNEE.RESERVATION)
        .from(TOURNEE)
        .where(TOURNEE.RESERVATION.isNull())
        .and(TOURNEE.AFFECTATION.in(idsOrganisme))
        .and(TOURNEE.ETAT.isNull().or(TOURNEE.ETAT.lessThan(100)))
        .fetch(
            (RecordMapper<Record, TourneeModel>)
                record ->
                    ImmutableTourneeModel.builder()
                        .idRemocra(record.get(TOURNEE.ID))
                        .nom(record.get(TOURNEE.NOM))
                        .reservation(record.get(TOURNEE.RESERVATION))
                        .affectation(record.get(TOURNEE.AFFECTATION))
                        .listeHydrant(List.of())
                        .build());
  }

  /**
   * Retourne les tournées qui ont leurs id dans la liste passée en paramètre
   *
   * @param idsTournees liste des idTournee
   * @return la liste des objets TourneeModel
   */
  public List<TourneeModel> getTourneesByIds(List<Long> idsTournees) {
    return context
        .select(TOURNEE.ID, TOURNEE.NOM, TOURNEE.RESERVATION, TOURNEE.AFFECTATION)
        .from(TOURNEE)
        .where(TOURNEE.ID.in(idsTournees))
        .fetch(
            (RecordMapper<Record, TourneeModel>)
                record ->
                    ImmutableTourneeModel.builder()
                        .idRemocra(record.get(TOURNEE.ID))
                        .nom(record.get(TOURNEE.NOM))
                        .reservation(record.get(TOURNEE.RESERVATION))
                        .affectation(record.get(TOURNEE.AFFECTATION))
                        .listeHydrant(List.of())
                        .build());
  }

  /**
   * Permet de récupérer les hydrants associés aux tournées passées en paramètre
   *
   * @param idsTournees : id des tournées dont on veut connaître les hydrants
   * @return une map <idTournee, List<idHydrant>>
   */
  public Map<Long, List<Long>> getHydrantsByTournee(List<Long> idsTournees) {
    return context
        .select(HYDRANT_TOURNEES.TOURNEES, HYDRANT.ID)
        .from(HYDRANT)
        .join(HYDRANT_TOURNEES)
        .on(HYDRANT_TOURNEES.HYDRANT.eq(HYDRANT.ID))
        .where(HYDRANT_TOURNEES.TOURNEES.in(idsTournees))
        .fetchGroups(HYDRANT_TOURNEES.TOURNEES, HYDRANT.ID);
  }

  /**
   * Permet de réserver une tournée
   *
   * @param idUtilisateur id de l'utilisateur connecté
   */
  public int reserveTournees(List<Long> idsTournees, Long idUtilisateur) {
    return transactionManager.transactionResult(
        () ->
            context
                .update(TOURNEE)
                .set(TOURNEE.RESERVATION, idUtilisateur)
                .where(TOURNEE.ID.in(idsTournees))
                .execute());
  }

  public boolean checkExist(Long idTournee) {
    return context.fetchExists(context.selectFrom(TOURNEE).where(TOURNEE.ID.eq(idTournee)));
  }

  public void setTourneesFinies(List<Long> idsTournee) {
    context
        .update(TOURNEE)
        .set(TOURNEE.RESERVATION, DSL.val(null, SQLDataType.BIGINT))
        .set(TOURNEE.ETAT, 100)
        .where(TOURNEE.ID.in(idsTournee))
        .execute();
  }

  public boolean annuleReservation(Long idTournee, Long idUtilisateur) {
    int res =
        transactionManager.transactionResult(
            () ->
                context
                    .update(TOURNEE)
                    .set(TOURNEE.RESERVATION, DSL.val(null, SQLDataType.BIGINT))
                    .where(TOURNEE.ID.eq(idTournee))
                    .and(TOURNEE.RESERVATION.eq(idUtilisateur))
                    .execute());
    return res == 1;
  }
}
