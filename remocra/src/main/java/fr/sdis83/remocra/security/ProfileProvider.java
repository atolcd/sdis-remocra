package fr.sdis83.remocra.security;

import java.util.Collection;
import java.util.EnumSet;
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
import fr.sdis83.remocra.security.AccessRight.Permission;

/**
 * Recupère le profile et les droits d'accès d'un utilisateur en fonction de son
 * profil et de son organisme.
 * 
 * @author bpa
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
            EnumSet<Permission> permissions = parsePermission(droit);
            logger.debug(" Found right : " + permissions + " for right : " + droit.getTypeDroit().getNom());
            if (result.containsKey(droit.getTypeDroit().getValue())) {
                result.get(droit.getTypeDroit().getValue()).addPermissions(permissions);
            } else {
                // Autorise les espaces dans la définition des droits
                AccessRight accessRight = new AccessRight(droit.getTypeDroit().getValue()).addPermissions(permissions);
                result.put(droit.getTypeDroit().getValue(), accessRight);
            }
        }
        return result.values();

    }

    /**
     * Methode de lecture des permission.
     * 
     * @param value
     * @return
     * @throws BusinessException
     */
    private EnumSet<Permission> parsePermission(Droit droit) throws BusinessException {
        EnumSet<Permission> result = EnumSet.noneOf(Permission.class);
        if (droit.isDroitCreate()) {
            result.add(Permission.CREATE);
            result.add(Permission.READ);
        }
        if (droit.isDroitRead()) {
            result.add(Permission.READ);
        }
        if (droit.isDroitUpdate()) {
            result.add(Permission.UPDATE);
            result.add(Permission.READ);
        }
        if (droit.isDroitDelete()) {
            result.add(Permission.DELETE);
            result.add(Permission.READ);
        }
        return result;
    }
}
