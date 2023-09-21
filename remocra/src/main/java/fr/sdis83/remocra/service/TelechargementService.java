package fr.sdis83.remocra.service;

import fr.sdis83.remocra.domain.pdi.Telechargement;
import fr.sdis83.remocra.domain.remocra.CourrierDocument;
import fr.sdis83.remocra.domain.remocra.Document;
import fr.sdis83.remocra.domain.remocra.Utilisateur;
import java.io.File;
import java.util.Date;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.dao.EmptyResultDataAccessException;

@Configuration
public class TelechargementService {
  @Autowired private UtilisateurService utilisateurService;

  public String getPdiFilePathFromCode(String code) {
    try {
      Telechargement t = Telechargement.findTelechargementsByCodeEquals(code).getSingleResult();
      if (t == null) {
        return null;
      }
      return t.getRessource();
    } catch (EmptyResultDataAccessException e) {
      return null;
    } catch (Exception e) {
      return null;
    }
  }

  public String getRemocraFilePathFromCode(String code) {
    try {
      Document d = Document.findDocumentsByCodeEquals(code).getSingleResult();
      if (d == null) {
        return null;
      }
      return d.getRepertoire() + File.separator + d.getFichier();
    } catch (EmptyResultDataAccessException e) {
      return null;
    } catch (Exception e) {
      return null;
    }
  }

  public String getCourrierFilePathFromCode(String code) {
    try {
      CourrierDocument cd =
          CourrierDocument.findCourrierDocumentsByCodeEquals(code).getSingleResult();
      if (cd == null) {
        return null;
      }
      Document d = cd.getDocument();
      return d.getRepertoire() + File.separator + d.getFichier();
    } catch (EmptyResultDataAccessException e) {
      return null;
    } catch (Exception e) {
      return null;
    }
  }

  public boolean setCourrierAccuseFromCode(String code) {
    try {
      CourrierDocument cd =
          CourrierDocument.findCourrierDocumentsByCodeEquals(code).getSingleResult();
      Utilisateur u = utilisateurService.getCurrentUtilisateur();
      Long idUtilisateur = u.getId();
      Long idOrganisme = u.getOrganisme().getId();
      Long idDestinataire = cd.getIdDestinataire();
      String typeDestinataire = cd.getTypeDestinataire().toString();
      if (cd == null) {
        return false;
      } else if (cd.getAccuse() == null
          && ((idUtilisateur.equals(idDestinataire)
                  && "UTILISATEUR".equalsIgnoreCase(typeDestinataire))
              || (idOrganisme.equals(idDestinataire)
                  && "ORGANISME".equalsIgnoreCase(typeDestinataire)))) {
        cd.setAccuse(new Date());
        cd.persist();
      }
      return true;
    } catch (Exception e) {
      return false;
    }
  }
}
