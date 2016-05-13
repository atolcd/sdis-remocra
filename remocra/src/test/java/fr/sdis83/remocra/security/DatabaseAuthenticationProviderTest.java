package fr.sdis83.remocra.security;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.EnumSet;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import fr.sdis83.remocra.domain.remocra.Organisme;
import fr.sdis83.remocra.domain.remocra.ProfilOrganisme;
import fr.sdis83.remocra.domain.remocra.ProfilUtilisateur;
import fr.sdis83.remocra.domain.remocra.TypeDroit.TypeDroitEnum;
import fr.sdis83.remocra.domain.remocra.Utilisateur;
import fr.sdis83.remocra.exception.BusinessException;
import fr.sdis83.remocra.security.AccessRight.Permission;

public class DatabaseAuthenticationProviderTest extends DbUnitBaseTest {

    @Autowired
    private DatabaseAuthenticationProvider underTest;

    private ProfileProvider profileProvider;

    @Before
    public void setup() {
        profileProvider = mock(ProfileProvider.class);
        underTest.setProvider(profileProvider);
    }

    @Override
    public String getDataSetPath() {
        return "security/db/DroitsEtUtilisateursDataset.xml";
    }

    @Override
    public String getDeleteDataSetPath() {
        return "security/db/DeleteAllDataSet.xml";
    }

    @Test
    public void testCreateUser() {
        Utilisateur user = new Utilisateur();
        user.setEmail("new@atolcd.com");
        user.setIdentifiant("newuser");
        user.setOrganisme(Organisme.findOrganismesByCode("commune1").getSingleResult());
        user.setProfilUtilisateur(ProfilUtilisateur.findProfilUtilisateur(1L));
        user.setActif(true);
        String password = "admin";
        underTest.encodeNewPasswordForUser(user, password);

        System.out.println(user.getSalt());
        System.out.println(user.getPassword());

        user.merge();

        Authentication auth = underTest.authenticate(new UsernamePasswordAuthenticationToken(user.getIdentifiant(), password));

        assertTrue(auth.isAuthenticated());
    }

    @Test
    public void testUpdateExistingUser() {
        Utilisateur user = Utilisateur.findUtilisateursByIdentifiant("maire").getSingleResult();

        String password = "nouveau password encore";
        underTest.encodeNewPasswordForUser(user, password);

        user.merge();

        Authentication auth = underTest.authenticate(new UsernamePasswordAuthenticationToken(user.getIdentifiant(), password));

        assertTrue(auth.isAuthenticated());
    }

    @Test
    public void testAuthentication_DbUser() {
        assertNotNull(underTest);

        Authentication auth = underTest.authenticate(new UsernamePasswordAuthenticationToken("maire", "admin"));

        assertTrue(auth.isAuthenticated());
    }

    @Test
    public void testAuthentication_DbUser_EmailFail() {
        assertNotNull(underTest);

        try {
            @SuppressWarnings("unused")
            Authentication auth = underTest.authenticate(new UsernamePasswordAuthenticationToken("usertahtdoesnotexist", "admin"));
        } catch (Exception ex) {
            return;
        }
        fail("expected exception");
    }

    @Test
    public void testAuthentication_DbUser_PasswordFail() {
        assertNotNull(underTest);
        try {
            @SuppressWarnings("unused")
            Authentication auth = underTest.authenticate(new UsernamePasswordAuthenticationToken("maire", "adminwrongpassword"));
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            return;
        }
        fail("expected exception");
    }

    @Test
    public void testAuthentication_DbUserHasProfile() throws BusinessException {
        assertNotNull(underTest);

        ArrayList<AccessRight> accessRights = new ArrayList<AccessRight>();
        AccessRight accessRight = new AccessRight(TypeDroitEnum.ADRESSES);
        accessRight.addPermissions(EnumSet.of(Permission.READ));
        accessRights.add(accessRight);
        when(profileProvider.getProfileAccessRights(any(ProfilUtilisateur.class), any(ProfilOrganisme.class))).thenReturn(accessRights);

        Authentication auth = underTest.authenticate(new UsernamePasswordAuthenticationToken("maire", "admin"));

        assertEquals("Authority not found", accessRights.size(), auth.getAuthorities().size());
    }

    @Test
    public void testAuthentication_DbUserWithoutProfile() throws BusinessException {
        assertNotNull(underTest);

        ArrayList<AccessRight> accessRights = new ArrayList<AccessRight>();
        when(profileProvider.getProfileAccessRights(any(ProfilUtilisateur.class), any(ProfilOrganisme.class))).thenReturn(accessRights);

        Authentication auth = underTest.authenticate(new UsernamePasswordAuthenticationToken("maire", "admin"));

        assertEquals("No Authority should not be found", 0, auth.getAuthorities().size());

    }

}
