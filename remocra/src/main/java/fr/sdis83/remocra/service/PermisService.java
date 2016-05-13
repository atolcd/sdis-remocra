package fr.sdis83.remocra.service;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.Transactional;

import fr.sdis83.remocra.domain.remocra.Permis;

@Configuration
public class PermisService {

    @Autowired
    private UtilisateurService utilisateurService;

    @Transactional
    public void setUpToDateInfo(Permis permis) {
        permis.getGeometrie().setSRID(2154);
        permis.setDateModification(new Date());
        permis.setInstructeur(utilisateurService.getCurrentUtilisateur());
    }

    @Transactional
    public Permis updatePermis(Permis permis) {
        Permis attached = Permis.findPermis(permis.getId());

        attached.setComplement(permis.getComplement());
        attached.setVoie(permis.getVoie());
        attached.setAnnee(permis.getAnnee());
        attached.setAvis(permis.getAvis());
        attached.setCommune(permis.getCommune());
        attached.setGeometrie(permis.getGeometrie());
        attached.setInterservice(permis.getInterservice());
        attached.setNom(permis.getNom());
        attached.setNumero(permis.getNumero());
        attached.setObservations(permis.getObservations());
        attached.setParcelleCadastrale(permis.getParcelleCadastrale());
        attached.setSectionCadastrale(permis.getSectionCadastrale());
        attached.setServiceInstructeur(permis.getServiceInstructeur());
        attached.setDatePermis(permis.getDatePermis());

        setUpToDateInfo(attached);
        
        return attached.merge();
    }

    @Transactional
    public Permis createPermis(Permis permis) {

        setUpToDateInfo(permis);

        permis.persist();
        return permis;
    }

}
