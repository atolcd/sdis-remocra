package fr.sdis83.remocra.service;

import com.vividsolutions.jts.geom.Point;
import fr.sdis83.remocra.GlobalConstants;
import fr.sdis83.remocra.domain.remocra.Document;
import fr.sdis83.remocra.domain.remocra.EmailModele;
import fr.sdis83.remocra.domain.remocra.EmailModele.EmailModeleEnum;
import fr.sdis83.remocra.domain.remocra.EmailModele.EmailModeleKeys;
import fr.sdis83.remocra.domain.remocra.Rci;
import fr.sdis83.remocra.domain.remocra.RciDocument;
import fr.sdis83.remocra.domain.remocra.Utilisateur;
import fr.sdis83.remocra.domain.utils.RemocraDateHourFormat;
import fr.sdis83.remocra.exception.BusinessException;
import fr.sdis83.remocra.util.DocumentUtil;
import fr.sdis83.remocra.util.GeometryUtil;
import fr.sdis83.remocra.web.message.ItemFilter;
import fr.sdis83.remocra.web.message.ItemSorting;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.sql.DataSource;
import org.apache.log4j.Logger;
import org.cts.IllegalCoordinateException;
import org.cts.crs.CRSException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Configuration
public class RciService extends AbstractService<Rci> {

  private final Logger logger = Logger.getLogger(getClass());

  @Autowired private UtilisateurService utilisateurService;

  @Autowired protected ParamConfService paramConfService;

  @Autowired private MailUtils mailUtils;

  @Autowired DataSource dataSource;

  public RciService() {
    super(Rci.class);
  }

  @Override
  protected Predicate processFilterItem(
      CriteriaQuery<?> itemQuery,
      Map<String, Object> parameters,
      Root<Rci> from,
      ItemFilter itemFilter) {
    CriteriaBuilder cBuilder = this.getCriteriaBuilder();
    Predicate predicat = null;
    if ("id".equals(itemFilter.getFieldName())) {
      Expression<String> cpPath = from.get("id");
      predicat = cBuilder.equal(cpPath, itemFilter.getValue());
    } else {
      logger.info(
          "processFilterItem non traité "
              + itemFilter.getFieldName()
              + " ("
              + itemFilter.getValue()
              + ")");
    }
    return predicat;
  }

  @Override
  protected boolean processItemSortings(
      ArrayList<Order> orders, ItemSorting itemSorting, CriteriaBuilder cBuilder, Root<Rci> from) {
    return super.processItemSortings(orders, itemSorting, cBuilder, from);
  }

  public List<Rci> findByBBOX(String bbox) {
    // Pas de restriction au territoire de compétence
    TypedQuery<Rci> query =
        entityManager
            .createQuery(
                "SELECT o FROM Rci o where contains (transform(:filter, :srid), geometrie) = true",
                Rci.class)
            .setParameter("filter", GeometryUtil.geometryFromBBox(bbox))
            .setParameter("srid", GlobalConstants.SRID_2154);
    return query.getResultList();
  }

  public List<Rci> findAll() {
    // Pas de restriction au territoire de compétence
    TypedQuery<Rci> query = entityManager.createQuery("SELECT o FROM Rci o", Rci.class);
    return query.getResultList();
  }

  @Override
  @Transactional
  public Rci setUpInformation(Rci attached, Map<String, MultipartFile> files, Object... params)
      throws Exception {

    // Géométrie
    attached.getGeometrie().setSRID(GlobalConstants.SRID_2154);

    // Coordonnées DFCI
    try {
      attached.setCoordDFCI(
          GeometryUtil.findCoordDFCIFromGeom(dataSource, attached.getGeometrie()));
    } catch (Exception e) {
      logger.debug("Impossible de calculer les coordonnées DFCI", e);
    }

    // Date de modification
    attached.setDateModification(new Date());

    // Utilisateur
    attached.setUtilisateur(utilisateurService.getCurrentUtilisateur());

    // Cas de la création : envoi de mail
    if (attached.getId() == null) {
      String emailDestinataire = paramConfService.getEmailCreationRci();
      EmailModele emailModele = EmailModele.findByValue(EmailModeleEnum.CREATION_RCI);

      Utilisateur utilisateur = attached.getUtilisateur();
      String nomOrganisme = utilisateur.getOrganisme().getNom();

      // Diffusion information par mail si toutes les informations sont
      // présentes (destinataire et modèle)
      if (emailDestinataire == null) {
        logger.debug("Création RCI : destinataire non renseigné. Pas d'envoi d'email");
      } else if (emailModele == null
          || emailModele.getObjet() == null
          || emailModele.getObjet().startsWith("TEMP_OBJET")) {
        logger.debug("Création RCI : modèle d'email non renseigné. Pas d'envoi d'email");
      } else {
        String rciCode =
            new RemocraDateHourFormat().format(attached.getDateIncendie())
                + " - "
                + attached.getCoordDFCI();
        mailUtils.envoiEmailWithModele(
            emailModele,
            getSystemUtilisateur(),
            emailDestinataire,
            EmailModele.emptyKeyMap()
                .add(EmailModeleKeys.URL_SITE, paramConfService.getUrlSite())
                .add(EmailModeleKeys.CODE, rciCode)
                .add(EmailModeleKeys.NOM_ORGANISME, nomOrganisme)
                .add(EmailModeleKeys.IDENTIFIANT, utilisateur.getIdentifiant()));
      }
    }

    return super.setUpInformation(attached, files, params);
  }

  @Override
  protected void beforeDelete(Rci attached) {
    for (RciDocument rciD : attached.getRciDocuments()) {
      try {
        deleteDocument(rciD.getId());
      } catch (Exception e) {
        logger.error(e.getMessage(), e);
      }
    }
    super.beforeDelete(attached);
  }

  public void deleteDocument(Long id) throws Exception {
    RciDocument attached = entityManager.find(RciDocument.class, id);
    Document d = attached.getDocument();

    // Ici, le fait de supprimer le Document provoque la suppression du
    // RciDocument en cascade
    entityManager.remove(d);
    entityManager.flush();

    // Nettoyage du disque
    DocumentUtil.getInstance().deleteHDFile(d);
  }

  // Point
  public void deplacer(Long id, Point point, Integer srid)
      throws CRSException, IllegalCoordinateException, BusinessException {
    Rci r = Rci.findRci(id);
    if (r == null) {
      BusinessException e = new BusinessException("Le départ n'existe pas en base");
      logger.error(e.getMessage());
      throw e;
    }
    r.setDateModification(new Date());
    point.setSRID(srid);
    r.setGeometrie(point);

    // Coordonnées DFCI
    try {
      r.setCoordDFCI(GeometryUtil.findCoordDFCIFromGeom(dataSource, r.getGeometrie()));
    } catch (Exception e) {
      logger.debug("Impossible de calculer les coordonnées DFCI", e);
    }

    r.persist();
  }

  public Utilisateur getSystemUtilisateur() throws BusinessException {
    Long sysUId = paramConfService.getSystemUtilisateurId();
    if (sysUId == null) {
      BusinessException e = new BusinessException("L'utilisateur système n'a pas été paramétré");
      logger.error(e.getMessage(), e);
      throw e;
    }
    Utilisateur u = Utilisateur.findUtilisateur(sysUId);
    if (u == null) {
      BusinessException e = new BusinessException("L'utilisateur système n'a pas été trouvé");
      logger.error(e.getMessage(), e);
      throw e;
    }
    return u;
  }
}
