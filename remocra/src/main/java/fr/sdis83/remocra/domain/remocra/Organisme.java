package fr.sdis83.remocra.domain.remocra;

import java.util.ArrayList;
import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Query;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.activerecord.RooJpaActiveRecord;
import org.springframework.roo.addon.json.RooJson;
import org.springframework.roo.addon.tostring.RooToString;

@RooJavaBean
@RooToString
@RooJson
@RooJpaActiveRecord(finders = {"findOrganismesByCode"})
public class Organisme {

  /**
   * L'inheritenceType SINGLE_TABLE et le GenerationType.Identity sont incompatible avec le driver
   * Postgresql > 8.3 Du coup on reste en 8.3...pour l'instant :
   *
   * <p>http://stackoverflow.com/questions/1333596/postgresql-identity-in-jpa-
   * single-table-hierarchy
   *
   * <p>On utilise maintenant JOINED => On peut passer en postgres 9.x. A tester...
   */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;

  @Version
  @Column(name = "version", columnDefinition = "INTEGER default 1")
  private Integer version;

  @NotNull private String code;

  private String nom;

  private String emailContact;

  @NotNull
  @Column(columnDefinition = "boolean default true")
  private Boolean actif;

  @NotNull @ManyToOne private TypeOrganisme typeOrganisme;

  @NotNull @ManyToOne private ProfilOrganisme profilOrganisme;

  @ManyToOne private ZoneCompetence zoneCompetence;

  @ManyToOne(optional = true)
  private Organisme organismeParent;

  /**
   * Retourne la liste des IDs composée de l'ID d'un organisme ainsi que les IDs de tous ses
   * enfants. La requête est récursive, tous les enfants de tous les degrés sont retournés
   *
   * @param idOrganisme l'ID de l'organisme parent
   * @return Un ArrayList d'entier contenant la liste des IDs de tous les organismes concernés
   */
  public static ArrayList<Integer> getOrganismeAndChildren(int idOrganisme) {
    Query query =
        entityManager()
            .createNativeQuery(
                "WITH RECURSIVE org(id) AS ( "
                    + "   SELECT id FROM remocra.organisme WHERE id=:idOrganisme "
                    + "   UNION ALL "
                    + "   SELECT remocra.organisme.id "
                    + "   FROM org, remocra.organisme "
                    + "   WHERE organisme_parent=org.id "
                    + ") "
                    + "SELECT DISTINCT CAST(id AS INTEGER) id FROM org")
            .setParameter("idOrganisme", idOrganisme);
    ArrayList<Integer> ids = (ArrayList<Integer>) query.getResultList();
    return ids;
  }

  /**
   * Retourne la liste des organismes qui sont contenus ou superposés avec la zone de compétence de
   * l'organisme passé en paramètre
   */
  public static ArrayList<Integer> getOrganismesZC(Long idOrganisme) {
    Query queryOrganismes =
        entityManager()
            .createNativeQuery(
                " SELECT DISTINCT organisme_contenu_id "
                    + " FROM remocra.zone_competence_organisme "
                    + " WHERE organisme_id = :idOrganisme ")
            .setParameter("idOrganisme", idOrganisme);
    ArrayList<Integer> idOrganismes = (ArrayList<Integer>) queryOrganismes.getResultList();
    return idOrganismes;
  }
}
