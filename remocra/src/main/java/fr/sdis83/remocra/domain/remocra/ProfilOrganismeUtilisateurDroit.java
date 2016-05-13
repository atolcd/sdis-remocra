package fr.sdis83.remocra.domain.remocra;

import javax.persistence.Column;
import javax.persistence.EntityManager;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.TypedQuery;
import javax.persistence.UniqueConstraint;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.activerecord.RooJpaActiveRecord;
import org.springframework.roo.addon.json.RooJson;
import org.springframework.roo.addon.tostring.RooToString;

import fr.sdis83.remocra.exception.BusinessException;

@RooJavaBean
@RooToString
@RooJson
@RooJpaActiveRecord
@Table(uniqueConstraints = { @UniqueConstraint(columnNames = { "profil_organisme", "profil_utilisateur" }) })
public class ProfilOrganismeUtilisateurDroit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Version
    @Column(name = "version", columnDefinition = "INTEGER default 1")
    private Integer version;

    @NotNull
    @ManyToOne
    private ProfilOrganisme profilOrganisme;

    @NotNull
    @ManyToOne
    private ProfilUtilisateur profilUtilisateur;

    @NotNull
    @ManyToOne
    private ProfilDroit profilDroit;

    public static ProfilOrganismeUtilisateurDroit findByOrganismeUtilisateur(ProfilOrganisme profilOrganisme, ProfilUtilisateur profilUtilisateur) throws BusinessException {

        EntityManager em = ProfilOrganismeUtilisateurDroit.entityManager();
        TypedQuery<ProfilOrganismeUtilisateurDroit> q = em.createQuery(
                "SELECT o FROM ProfilOrganismeUtilisateurDroit AS o WHERE o.profilUtilisateur = :pu AND o.profilOrganisme = :po", ProfilOrganismeUtilisateurDroit.class);
        q.setParameter("pu", profilUtilisateur);
        q.setParameter("po", profilOrganisme);
        ProfilOrganismeUtilisateurDroit singleResult;
        try {
            singleResult = q.getSingleResult();
        } catch (EmptyResultDataAccessException ex) {
            throw new BusinessException("Aucun profil n'a été trouvé pour le profil organisme : " + profilOrganisme.getNom() + " et le profil utilisateur : "
                    + profilUtilisateur.getNom());
        }
        return singleResult;
    }
}
