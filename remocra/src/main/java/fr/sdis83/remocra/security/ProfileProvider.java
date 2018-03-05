package fr.sdis83.remocra.security;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.Transactional;

import fr.sdis83.remocra.domain.remocra.Droit;
import fr.sdis83.remocra.domain.remocra.ProfilDroit;
import fr.sdis83.remocra.domain.remocra.ProfilOrganisme;
import fr.sdis83.remocra.domain.remocra.ProfilOrganismeUtilisateurDroit;
import fr.sdis83.remocra.domain.remocra.ProfilUtilisateur;
import fr.sdis83.remocra.domain.remocra.TypeDroit.TypeDroitEnum;
import fr.sdis83.remocra.exception.BusinessException;

/**
 * Recupère le profile et les droits d'accès d'un utilisateur en fonction de son
 * profil et de son organisme.
 * 
 */
@Configuration
public class ProfileProvider {

    @Bean
    public ProfileProvider profileProvider() {
        return new ProfileProvider();
    }

    private final Logger logger = Logger.getLogger(getClass());

    /**
     * Récupère les droits correspondant à un profil et un organisme.
     * 
     * La lecture des droits se fait dans le fichier (en minuscules) dans le
     * dossier ressource : security/profiles/[TypeOrganisme]_[Profil].properties
     * 
     * @param profil
     * @param organisme
     * @return
     * @throws BusinessException
     */
    @Transactional
    public Collection<AccessRight> getProfileAccessRights(ProfilUtilisateur profilUtilisateur, ProfilOrganisme profilOrganisme) throws BusinessException {

        ProfilDroit profil = ProfilOrganismeUtilisateurDroit.findByOrganismeUtilisateur(profilOrganisme, profilUtilisateur).getProfilDroit();

        if (profil == null) {

            String message = "ProfileProvider : le profil de droit n'est pas renseigné pour le profil organisme " + profilOrganisme.getNom() + " et le profil utilisateur "
                    + profilUtilisateur.getNom();
            logger.error(message);
            throw new BusinessException(message);
        }

        // Récupération des droits
        Map<TypeDroitEnum, AccessRight> result = new HashMap<TypeDroitEnum, AccessRight>();
        for (Droit droit : profil.getDroits()) {
            result.put(droit.getTypeDroit().getValue(), new AccessRight(droit.getTypeDroit().getValue()));
        }
        return result.values();

    }
}
