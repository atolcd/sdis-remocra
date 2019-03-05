package fr.sdis83.remocra.domain.remocra;


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
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;

@RooJavaBean
@RooToString
@RooJson
@RooJpaActiveRecord(finders = { "findOrganismesByCode" })
public class Organisme {

    /**
     * L'inheritenceType SINGLE_TABLE et le GenerationType.Identity sont
     * incompatible avec le driver Postgresql > 8.3 Du coup on reste en
     * 8.3...pour l'instant :
     * 
     * http://stackoverflow.com/questions/1333596/postgresql-identity-in-jpa-
     * single-table-hierarchy
     * 
     * On utilise maintenant JOINED => On peut passer en postgres 9.x. A
     * tester...
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Version
    @Column(name = "version", columnDefinition = "INTEGER default 1")
    private Integer version;

    @NotNull
    private String code;

    private String nom;

    private String emailContact;

    @NotNull
    @Column(columnDefinition = "boolean default true")
    private Boolean actif;

    @NotNull
    @ManyToOne
    private TypeOrganisme typeOrganisme;

    @NotNull
    @ManyToOne
    private ProfilOrganisme profilOrganisme;

    @ManyToOne
    private ZoneCompetence zoneCompetence;

    @ManyToOne(optional = true)
    private Organisme organismeParent;

    /**
     * Retourne la liste des IDs composée de l'ID d'un organisme ainsi que les IDs de tous ses enfants.
     * La requête est récursive, tous les enfants de tous les degrés sont retournés
     * @param idOrganisme l'ID de l'organisme parent
     * @return Un ArrayList d'entier contenant la liste des IDs de tous les organismes concernés
     */
    public static ArrayList<Integer> getOrganismeAndChildren(int idOrganisme){
        Query query = entityManager().createNativeQuery("with recursive organisme(id, nom) as" +
            "(" +
                "select id, nom " +
                "from remocra.organisme d " +
                "where id=:idOrganisme " +

                "union all " +

                "select d.id, d.nom " +
                "from remocra.organisme d, remocra.organisme o " +
                "where o.id=d.organisme_parent " +
            ") " +
            "select distinct CAST(id AS INTEGER) from organisme").setParameter("idOrganisme", idOrganisme);
        ArrayList<Integer> ids = (ArrayList<Integer>) query.getResultList();
        return ids;
    }

}
