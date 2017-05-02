package fr.sdis83.remocra.service;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.Transactional;

import fr.sdis83.remocra.domain.pdi.ModeleTraitement;
import fr.sdis83.remocra.domain.pdi.PdiVueCombo;
import fr.sdis83.remocra.domain.pdi.Statut;
import fr.sdis83.remocra.domain.pdi.Traitement;
import fr.sdis83.remocra.domain.pdi.TraitementParametre;
import fr.sdis83.remocra.domain.pdi.TraitementParametrePK;
import fr.sdis83.remocra.domain.remocra.ExportModele;
import fr.sdis83.remocra.domain.remocra.Utilisateur;
import fr.sdis83.remocra.exception.BusinessException;

@Configuration
public class TraitementsService {

    @Autowired
    private UtilisateurService utilisateurService;

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public Traitement createTraitement(Integer idmodele, String json) throws BusinessException {

        Utilisateur currentUtilisateur = utilisateurService.getCurrentUtilisateur();

        Traitement newTraitement = new Traitement();
        newTraitement.setIdmodele(ModeleTraitement.findModeleTraitement(idmodele));
        newTraitement.setIdutilisateur(Integer.valueOf(currentUtilisateur.getId().intValue()));
        newTraitement.setDemande(new Date());
        newTraitement.setIdstatut(Statut.findStatut(new Integer(1)));

        entityManager.persist(newTraitement);

        Collection<TraitementParametre> lstTraitParam = TraitementParametre.fromJsonArrayToTraitementParametres(json);

        for (TraitementParametre param : lstTraitParam) {
            TraitementParametrePK pkTrtParam = new TraitementParametrePK(param.getIdparametre().getIdparametre(), newTraitement.getIdtraitement());

            param.setId(pkTrtParam);
            entityManager.persist(param);
        }

        entityManager.flush();

        return newTraitement;
    }

    public ExportModele getExportModeleFromCode(String code) {
        return ExportModele.findExportModelesByCode(code).getSingleResult();
    }

    public List<PdiVueCombo> getComboValues(String nomvue) {
        return getComboValues(nomvue, null);
    }

    public List<PdiVueCombo> getComboValues(String nomvue, String query) {
        List<PdiVueCombo> lstResult = new ArrayList<PdiVueCombo>();

        String whereClause = query == null || query.isEmpty() ? ""
                : "lower(v.libelle) like lower('%" + query + "%')";
        @SuppressWarnings("unchecked")
        List<Object[]> results = entityManager.createNativeQuery("SELECT v.id, v.libelle FROM pdi." + nomvue + " as v"
                + (whereClause.isEmpty() ? "" : " where " + whereClause)).getResultList();

        for (Object[] result : results) {
            lstResult.add(new PdiVueCombo(((BigInteger) result[0]), (String) result[1]));
        }

        return lstResult;
    }
}