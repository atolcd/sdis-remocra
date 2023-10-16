package fr.sdis83.remocra.repository;

import static fr.sdis83.remocra.db.model.remocra.Tables.DROIT;
import static fr.sdis83.remocra.db.model.remocra.Tables.PROFIL_DROIT;
import static fr.sdis83.remocra.db.model.remocra.Tables.PROFIL_ORGANISME_UTILISATEUR_DROIT;
import static fr.sdis83.remocra.db.model.remocra.Tables.PROFIL_UTILISATEUR;
import static fr.sdis83.remocra.db.model.remocra.Tables.TYPE_DROIT;
import static fr.sdis83.remocra.db.model.remocra.Tables.UTILISATEUR;

import java.util.Arrays;
import java.util.List;
import javax.inject.Inject;
import org.jooq.DSLContext;

public class TypeDroitRepository {

  private final DSLContext context;
  private final TransactionManager transactionManager;

  private static String DROIT_MOBILE = "Mobile";

  @Inject
  public TypeDroitRepository(DSLContext context, TransactionManager transactionManager) {
    this.context = context;
    this.transactionManager = transactionManager;
  }

  /**
   * Renvoie tous les droits liés à l'application mobile en fonction de l'utilisateur connecté
   *
   * @param idUtilisateur : id de l'utilisateur
   */
  public List<String> getDroitsUtilisateurMobile(Long idUtilisateur) {
    return context
        .select(TYPE_DROIT.CODE)
        .from(TYPE_DROIT)
        .join(DROIT)
        .on(DROIT.TYPE_DROIT.eq(TYPE_DROIT.ID))
        .join(PROFIL_DROIT)
        .on(PROFIL_DROIT.ID.eq(DROIT.PROFIL_DROIT))
        .join(PROFIL_ORGANISME_UTILISATEUR_DROIT)
        .on(PROFIL_ORGANISME_UTILISATEUR_DROIT.PROFIL_DROIT.eq(PROFIL_DROIT.ID))
        .join(PROFIL_UTILISATEUR)
        .on(PROFIL_UTILISATEUR.ID.eq(PROFIL_ORGANISME_UTILISATEUR_DROIT.PROFIL_UTILISATEUR))
        .join(UTILISATEUR)
        .on(UTILISATEUR.PROFIL_UTILISATEUR.eq(PROFIL_UTILISATEUR.ID))
        .where(TYPE_DROIT.CATEGORIE.eq(DROIT_MOBILE))
        .and(UTILISATEUR.ID.eq(idUtilisateur))
        .fetchInto(String.class);
  }

  public List<String> getDroitsUtilisateurVisite(Long idUtilisateur) {
    return context
        .select(TYPE_DROIT.CODE)
        .from(TYPE_DROIT)
        .join(DROIT)
        .on(DROIT.TYPE_DROIT.eq(TYPE_DROIT.ID))
        .join(PROFIL_DROIT)
        .on(PROFIL_DROIT.ID.eq(DROIT.PROFIL_DROIT))
        .join(PROFIL_ORGANISME_UTILISATEUR_DROIT)
        .on(PROFIL_ORGANISME_UTILISATEUR_DROIT.PROFIL_DROIT.eq(PROFIL_DROIT.ID))
        .join(PROFIL_UTILISATEUR)
        .on(PROFIL_UTILISATEUR.ID.eq(PROFIL_ORGANISME_UTILISATEUR_DROIT.PROFIL_UTILISATEUR))
        .join(UTILISATEUR)
        .on(UTILISATEUR.PROFIL_UTILISATEUR.eq(PROFIL_UTILISATEUR.ID))
        .where(TYPE_DROIT.CODE.in(TypeDroitsPourMobile.getTypeDroitVisite()))
        .and(UTILISATEUR.ID.eq(idUtilisateur))
        .fetchInto(String.class);
  }

  public enum TypeDroitsPourMobile {
    CREATION_PEI_MOBILE("CREATION_PEI_MOBILE"),
    CREATION_GESTIONNAIRE_MOBILE("CREATION_GESTIONNAIRE_MOBILE"),

    // Types droit pour les visites
    HYDRANTS_CONTROLE_C("HYDRANTS_CONTROLE_C"), // CTRL
    HYDRANTS_RECONNAISSANCE_C("HYDRANTS_RECONNAISSANCE_C"), // RECO
    HYDRANTS_ANOMALIES_C("HYDRANTS_ANOMALIES_C"), // NP
    HYDRANTS_RECEPTION_C("HYDRANTS_RECEPTION_C"), // RECEP
    HYDRANTS_CREATION_C("HYDRANTS_CREATION_C"); // CREA

    private String codeDroitMobile;

    TypeDroitsPourMobile(String codeDroitMobile) {
      this.codeDroitMobile = codeDroitMobile;
    }

    public String getCodeDroitMobile() {
      return codeDroitMobile;
    }

    public static List<String> getTypeDroitVisite() {
      return Arrays.asList(
          HYDRANTS_CONTROLE_C.codeDroitMobile,
          HYDRANTS_ANOMALIES_C.codeDroitMobile,
          HYDRANTS_RECONNAISSANCE_C.codeDroitMobile,
          HYDRANTS_RECEPTION_C.codeDroitMobile,
          HYDRANTS_CREATION_C.codeDroitMobile);
    }
  }
}
