package fr.sdis83.remocra.domain.remocra;

import com.vividsolutions.jts.geom.Geometry;
import flexjson.JSONDeserializer;
import fr.sdis83.remocra.exception.BusinessException;
import fr.sdis83.remocra.web.deserialize.GeometryFactory;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;
import org.hibernate.annotations.Formula;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.activerecord.RooJpaActiveRecord;
import org.springframework.roo.addon.json.RooJson;
import org.springframework.roo.addon.tostring.RooToString;

@RooJavaBean
@RooToString
@RooJson
@RooJpaActiveRecord(
    finders = {
      "findUtilisateursByEmail",
      "findUtilisateursByIdentifiant",
      "findUtilisateursByOrganisme"
    })
public class Utilisateur {

  public static String ROLE_SDIS_USER = "ROLE_SDIS_USER";

  @Version
  @Column(name = "version", columnDefinition = "INTEGER default 1")
  private Integer version;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;

  @ManyToOne private Organisme organisme;

  private String nom;

  private String prenom;

  private String telephone;

  @NotNull private String email;

  @NotNull
  @Column(unique = true)
  private String identifiant;

  @NotNull private String password;

  private String salt;

  @Column(columnDefinition = "BOOLEAN NOT NULL default false ")
  private boolean messageRemocra = false;

  @NotNull @ManyToOne private ProfilUtilisateur profilUtilisateur;

  @NotNull
  @Column(columnDefinition = "boolean default true")
  private Boolean actif;

  @Formula(
      "(select pd.nom||' ('||pd.feuille_de_style_geo_server||')' from remocra.utilisateur u join remocra.organisme o on (u.organisme=o.id) join remocra.profil_organisme_utilisateur_droit popupd on (popupd.profil_organisme=o.profil_organisme and popupd.profil_utilisateur=u.profil_utilisateur) join remocra.profil_droit pd on(popupd.profil_droit=pd.id) where u.profil_utilisateur = popupd.profil_utilisateur and o.profil_organisme = popupd.profil_organisme and u.id = id)")
  private String groupeFnct;

  public static Utilisateur fromJsonToUtilisateur(String json) {
    return new JSONDeserializer<Utilisateur>()
        .use(null, Utilisateur.class)
        .use(Geometry.class, new GeometryFactory())
        .deserialize(json);
  }

  public static Utilisateur findUtilisateurByEmailResult(String email) throws BusinessException {
    try {
      return Utilisateur.findUtilisateursByEmail(email).getSingleResult();
    } catch (NonUniqueResultException ex) {
      throw new BusinessException("Utilisateur non unique", ex);
    } catch (NoResultException ex) {
      throw new BusinessException("Utilisateur inconnu", ex);
    } catch (EmptyResultDataAccessException ex) {
      throw new BusinessException("Utilisateur inconnu", ex);
    }
  }

  public static List<Utilisateur> findUtilisateursByOrganismeResult(Organisme organisme) {
    return Utilisateur.findUtilisateursByOrganisme(organisme).getResultList();
  }

  public static List<Utilisateur> findUtilisateursByOrganismeTypeAndActif(
      Class<? extends Organisme> clazz) {
    return entityManager()
        .createQuery(
            "SELECT u FROM Utilisateur u JOIN u.organisme o WHERE TYPE(o) = :class and u.actif = true and o.actif = true",
            Utilisateur.class)
        .setParameter("class", clazz)
        .getResultList();
  }

  /** Check si l'utilisateur est actif (y compris son organisme) */
  public Boolean isFullyActive() {
    return this.getActif() != null
        && this.getActif()
        && this.getOrganisme() != null
        && this.getOrganisme().getActif() != null
        && this.getOrganisme().getActif();
  }
}
