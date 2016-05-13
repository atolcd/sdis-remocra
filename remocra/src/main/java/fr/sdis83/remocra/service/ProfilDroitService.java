package fr.sdis83.remocra.service;

import java.util.List;

import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.Transactional;

import fr.sdis83.remocra.domain.remocra.Droit;
import fr.sdis83.remocra.domain.remocra.ProfilDroit;

@Configuration
public class ProfilDroitService extends AbstractService<ProfilDroit> {

    public ProfilDroitService() {
        super(ProfilDroit.class);
    }

    /**
     * Copie les droits d'un profil de droits vers un autre.
     * 
     * @param from
     * @param to
     */
    @Transactional
    public void copyDroits(final Long from, Long to) {
        ProfilDroit fromPd = ProfilDroit.findProfilDroit(from);
        ProfilDroit toPd = ProfilDroit.findProfilDroit(to);
        List<Droit> fromDroits = Droit.findDroitsByProfilDroitEquals(fromPd).getResultList();

        // Vidage des droits actuels
        toPd.getDroits().clear();
        toPd.merge();

        // Copie des droits
        for (Droit d : fromDroits) {
            Droit cp = new Droit();
            cp.setProfilDroit(toPd);
            cp.setTypeDroit(d.getTypeDroit());
            cp.setDroitCreate(d.isDroitCreate());
            cp.setDroitRead(d.isDroitRead());
            cp.setDroitUpdate(d.isDroitUpdate());
            cp.setDroitDelete(d.isDroitDelete());
            cp.persist();
        }
        toPd.merge();
    }

    /**
     * Vide les droits du profil de droits.
     * 
     * @param concerned
     */
    @Transactional
    public void clearDroits(final Long concerned) {
        ProfilDroit pd = ProfilDroit.findProfilDroit(concerned);

        // Vidage des droits actuels
        pd.getDroits().clear();
        pd.merge();
    }

}
