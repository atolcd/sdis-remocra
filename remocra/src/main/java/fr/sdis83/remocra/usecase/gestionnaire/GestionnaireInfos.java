package fr.sdis83.remocra.usecase.gestionnaire;
import fr.sdis83.remocra.db.model.remocra.tables.pojos.Gestionnaire;

import org.springframework.context.annotation.Configuration;



@Configuration
public class GestionnaireInfos {
    private Gestionnaire gestionnaire;
    private boolean gestionnaireHasContact;

    public GestionnaireInfos(){

    }

    public GestionnaireInfos(Gestionnaire gestionnaire, boolean gestionnaireHasContact){
        this.gestionnaire = gestionnaire;
        this.gestionnaireHasContact = gestionnaireHasContact;
    }


    public Gestionnaire getGestionnaire() {
        return gestionnaire;
    }

    public boolean getGestionnaireHasContact() {
        return gestionnaireHasContact;
    }
}
