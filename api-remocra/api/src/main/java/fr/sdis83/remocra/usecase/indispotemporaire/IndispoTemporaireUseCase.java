package fr.sdis83.remocra.usecase.indispotemporaire;

import fr.sdis83.remocra.authn.CurrentUser;
import fr.sdis83.remocra.authn.UserInfo;
import fr.sdis83.remocra.db.model.remocra.tables.pojos.Commune;
import fr.sdis83.remocra.db.model.remocra.tables.pojos.HydrantIndispoTemporaire;
import fr.sdis83.remocra.db.model.remocra.tables.pojos.TypeHydrantIndispoStatut;
import fr.sdis83.remocra.repository.IndispoTemporaireRepository;
import fr.sdis83.remocra.usecase.pei.PeiUseCase;
import fr.sdis83.remocra.web.exceptions.ResponseException;
import fr.sdis83.remocra.web.model.indispotemporaire.IndispoTemporaireForm;
import fr.sdis83.remocra.web.model.indispotemporaire.IndispoTemporaireModel;
import fr.sdis83.remocra.web.model.pei.PeiModel;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.ws.rs.core.Response;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static fr.sdis83.remocra.db.model.remocra.Tables.HYDRANT;
import static fr.sdis83.remocra.db.model.remocra.Tables.HYDRANT_INDISPO_TEMPORAIRE;
import static fr.sdis83.remocra.db.model.remocra.Tables.HYDRANT_INDISPO_TEMPORAIRE_HYDRANT;
import static fr.sdis83.remocra.db.model.remocra.Tables.TYPE_HYDRANT_INDISPO_STATUT;

public class IndispoTemporaireUseCase {

  @Inject
  IndispoTemporaireRepository indispoTemporaireRepository;

  private final DSLContext context;

  @Inject @CurrentUser
  Provider<UserInfo> currentUser;

  @Inject
  PeiUseCase peiUseCase;

  @Inject
  public IndispoTemporaireUseCase(DSLContext context) {
    this.context = context;
  }

  public List<IndispoTemporaireModel> getAll(String organismeApi, String hydrant, String statut, Integer limit, Integer start) {
    return this.indispoTemporaireRepository.getAll(organismeApi, hydrant, statut, limit, start);
  }

  public HydrantIndispoTemporaire addIndispoTemporaire(IndispoTemporaireForm indispoForm) throws ResponseException {
    try {

      fr.sdis83.remocra.db.model.remocra.tables.pojos.TypeHydrantIndispoStatut statut = context
        .select(TYPE_HYDRANT_INDISPO_STATUT.ID)
        .from(TYPE_HYDRANT_INDISPO_STATUT)
        .where(TYPE_HYDRANT_INDISPO_STATUT.CODE.equalIgnoreCase(indispoForm.statut()))
        .fetchOneInto(TypeHydrantIndispoStatut.class);
      if(statut == null) {
        throw new ResponseException(Response.Status.BAD_REQUEST, "3000 : Le statut de l'indisponibilité temporaire ne correspond à aucune valeur connue");
      }

      SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");

      HydrantIndispoTemporaire indispo = new HydrantIndispoTemporaire();
      indispo.setStatut(statut.getId());

      // Vérifications sur les dates
      if(indispoForm.date_debut() != null) {
        Date d = simpleDateFormat.parse(indispoForm.date_debut());
        indispo.setDateDebut(simpleDateFormat.parse(indispoForm.date_debut()).toInstant());
      }

      if(indispoForm.date_fin() != null) {
        indispo.setDateFin(simpleDateFormat.parse(indispoForm.date_fin()).toInstant());
      }

      if(indispo.getDateDebut() != null && indispo.getDateFin() != null && !indispo.getDateDebut().isBefore(indispo.getDateFin())) {
        throw new ResponseException(Response.Status.BAD_REQUEST, "3100 : La date de fin ne peut être égale ou antérieure à la date de début");
      }

      indispo.setMotif(indispoForm.motif());
      indispo.setBasculeAutoIndispo(indispoForm.bascule_auto_indispo());
      indispo.setBasculeAutoDispo(indispoForm.bascule_auto_dispo());
      indispo.setMelAvantIndispo(indispoForm.mel_avant_indispo());
      indispo.setMelAvantDispo(indispoForm.mel_avant_dispo());

      // On vérifie que les PEI sont bien accessibles par l'organisme
      for(String s : indispoForm.hydrants()) {
        if(!peiUseCase.isPeiAccessible(s)) {
          throw new ResponseException(Response.Status.BAD_REQUEST, "3101 : Au moins 1 hydrants transmis n'existe pas ou n'est pas accessible à votre organisme");
        }
      }

      List<Long> communes = context.select(HYDRANT.COMMUNE)
        .from(HYDRANT)
        .where(HYDRANT.NUMERO.in(indispoForm.hydrants()))
        .groupBy(HYDRANT.COMMUNE)
        .fetchInto(Long.class);

      if(communes.size() > 1) {
        throw new ResponseException(Response.Status.BAD_REQUEST, "3102 : Les hydrants transmis sont situés sur des communes différentes");
      }

      return this.indispoTemporaireRepository.addIndispoTemporaire(indispo, indispoForm.hydrants());
    } catch (ParseException e) {
      throw new ResponseException(Response.Status.BAD_REQUEST, "3001 : Une date spécifiée n'existe pas ou ne respecte pas le format YYYY-MM-DD hh:mm");
    }

  }

  /**
   * Vérifie si une indisponibilité temporaire est accessible à l'utilisateur
   * Si au moins un PEI composant l'IT est accessible, l'indispo temporaire est entièrement accessible
   * @param id L'identifiant de l'indisponibilité temporaire
   */
  private boolean isIndispoTemporaireAccessible(Long id) {
    List<String> hydrants = context
      .select(HYDRANT.NUMERO)
      .from(HYDRANT)
      .join(HYDRANT_INDISPO_TEMPORAIRE_HYDRANT).on(HYDRANT_INDISPO_TEMPORAIRE_HYDRANT.HYDRANT.eq(HYDRANT.ID))
      .join(HYDRANT_INDISPO_TEMPORAIRE).on(HYDRANT_INDISPO_TEMPORAIRE_HYDRANT.INDISPONIBILITE.eq(HYDRANT_INDISPO_TEMPORAIRE.ID))
      .where(HYDRANT_INDISPO_TEMPORAIRE.ID.eq(id))
      .fetchInto(String.class);

    boolean isAccessible = false;
    for(String s : hydrants) {
      if(this.peiUseCase.isPeiAccessible(s)) {
        isAccessible = true;
      }
    }
    return isAccessible;
  }
}
