package fr.sdis83.remocra.security;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import fr.sdis83.remocra.domain.remocra.ProfilOrganisme;
import fr.sdis83.remocra.domain.remocra.ProfilUtilisateur;
import fr.sdis83.remocra.domain.remocra.TypeDroit;
import fr.sdis83.remocra.exception.BusinessException;
import java.util.Collection;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class ProfileProviderTest extends DbUnitBaseTest {

  @Autowired private ProfileProvider underTest;

  @Before
  public void setup() {}

  @Override
  public String getDataSetPath() {
    return "security/db/DroitsEtUtilisateursDataset.xml";
  }

  @Override
  public String getDeleteDataSetPath() {
    return "security/db/DeleteAllDataSet.xml";
  }

  @Test
  public void testProfileProvider_Success() throws BusinessException {
    Collection<AccessRight> result =
        underTest.getProfileAccessRights(
            ProfilUtilisateur.findProfilUtilisateur(1L), ProfilOrganisme.findProfilOrganisme(1L));
    assertEquals(2, result.size());
    boolean found = false;
    for (AccessRight accessRight : result) {
      if (accessRight.getKey().equals(TypeDroit.TypeDroitEnum.REFERENTIELS_C)) found = true;
    }
    assertTrue(found);
  }
}
