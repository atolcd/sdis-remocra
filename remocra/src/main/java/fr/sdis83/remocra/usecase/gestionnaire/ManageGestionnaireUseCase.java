package fr.sdis83.remocra.usecase.gestionnaire;

import fr.sdis83.remocra.db.model.remocra.tables.pojos.Contact;
import fr.sdis83.remocra.db.model.remocra.tables.pojos.Gestionnaire;
import fr.sdis83.remocra.repository.GestionnaireRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class ManageGestionnaireUseCase {
    @Autowired
    GestionnaireRepository gestionnaireRepository;

    public ManageGestionnaireUseCase(){}

    @Transactional
    public List<GestionnaireInfos> fetchGestionnaireData(){
        List<Long> listeGestionnaire = gestionnaireRepository.getGestionnaireIds();
        List<GestionnaireInfos> listeGestionnairesInfos = new ArrayList<>();
        for (Long id : listeGestionnaire){
            Gestionnaire gestionnaire = gestionnaireRepository.getGestionnaire(id);
            boolean hasContact = ((gestionnaireRepository.getContactGestionnaire(id).size() == 0) ? false : true);
            listeGestionnairesInfos.add(new GestionnaireInfos(gestionnaire, hasContact));
        }
        return listeGestionnairesInfos;
    }
}
