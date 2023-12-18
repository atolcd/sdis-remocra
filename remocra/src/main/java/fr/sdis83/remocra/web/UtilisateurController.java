package fr.sdis83.remocra.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import flexjson.JSONSerializer;
import flexjson.transformer.DateTransformer;
import fr.sdis83.remocra.domain.remocra.Organisme;
import fr.sdis83.remocra.domain.remocra.TypeDroit.TypeDroitEnum;
import fr.sdis83.remocra.domain.remocra.Utilisateur;
import fr.sdis83.remocra.exception.BusinessException;
import fr.sdis83.remocra.security.AccessRight;
import fr.sdis83.remocra.security.AuthoritiesUtil;
import fr.sdis83.remocra.service.UtilisateurService;
import fr.sdis83.remocra.util.ExceptionUtils;
import fr.sdis83.remocra.web.message.ItemFilter;
import fr.sdis83.remocra.web.message.ItemSorting;
import fr.sdis83.remocra.web.serialize.ext.AbstractExtListSerializer;
import fr.sdis83.remocra.web.serialize.ext.AbstractExtObjectSerializer;
import fr.sdis83.remocra.web.serialize.ext.SuccessErrorExtSerializer;
import fr.sdis83.remocra.xml.UserInformation;
import java.io.StringWriter;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import org.apache.log4j.Logger;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@RequestMapping("/utilisateurs")
@Controller
public class UtilisateurController {

  private final Logger logger = Logger.getLogger(getClass());
  private final ObjectMapper objectMapper = new ObjectMapper();

  @Autowired private UtilisateurService utilisateurService;

  @Autowired private AuthoritiesUtil authUtils;

  @RequestMapping(headers = "Accept=application/json")
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<java.lang.String> listJson(
      @RequestParam(value = "page", required = false) Integer page,
      final @RequestParam(value = "start", required = false) Integer start,
      final @RequestParam(value = "limit", required = false) Integer limit,
      @RequestParam(value = "sort", required = false) String sorts,
      @RequestParam(value = "filter", required = false) String filters) {
    final List<ItemSorting> sortList = ItemSorting.decodeJson(sorts);
    final List<ItemFilter> itemFilterList = ItemFilter.decodeJson(filters);
    if (!authUtils.hasRight(TypeDroitEnum.UTILISATEUR_FILTER_ALL_R)) {
      if (authUtils.hasRight(TypeDroitEnum.UTILISATEUR_FILTER_ORGANISME_UTILISATEUR_R)) {
        itemFilterList.add(
            new ItemFilter(
                "organismeId",
                String.valueOf(utilisateurService.getCurrentUtilisateur().getOrganisme().getId())));
      } else {
        itemFilterList.add(
            new ItemFilter(
                "id", String.valueOf(utilisateurService.getCurrentUtilisateur().getId())));
      }
    }

    return new AbstractExtListSerializer<Utilisateur>("Utilisateurs retrieved.") {

      @Override
      protected JSONSerializer additionnalIncludeExclude(JSONSerializer serializer) {
        return serializer
            .include(
                "data.organisme.organismeParent.id",
                "data.organisme.organismeParent.nom",
                "data.organisme.organismeParent.code")
            .exclude("data.organisme.zoneCompetence.geometrie", "data.organisme.organismeParent.*");
      }

      @Override
      protected List<Utilisateur> getRecords() {
        return utilisateurService.findUtilisateurs(start, limit, sortList, itemFilterList);
      }

      @Override
      protected Long countRecords() {
        return utilisateurService.countUtilisateurs(itemFilterList);
      }
    }.serialize();
  }

  @RequestMapping(value = "/current", headers = "Accept=application/json")
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<java.lang.String> showCurrentJson() {

    return new AbstractExtObjectSerializer<Utilisateur>("Utilisateur retrieved") {

      @Override
      protected Utilisateur getRecord() {
        return utilisateurService.getCurrentUtilisateur();
      }

      @Override
      protected JSONSerializer additionnalIncludeExclude(JSONSerializer serializer) {
        return serializer.transform(new DateTransformer("MM/dd/yy"), Date.class);
      }
    }.serialize();
  }

  @RequestMapping(
      value = "/current/getRight/{typeDroit}",
      method = RequestMethod.GET,
      headers = "Accept=application/json")
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<java.lang.String> getAccreditationByUserRightCode(
      @PathVariable("typeDroit") TypeDroitEnum typeDroit) {
    try {
      return new ResponseEntity<>(
          objectMapper.writeValueAsString(authUtils.hasRight(typeDroit)), HttpStatus.OK);
    } catch (Exception e) {
      this.logger.error(e.getMessage(), e);
      return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @RequestMapping(value = "/current/xml", headers = "Accept=application/xml")
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<java.lang.String> showCurrentXML() {

    List<AccessRight> rights = authUtils.getCurrentRights();
    Utilisateur user = utilisateurService.getCurrentUtilisateur();
    String userName = user.getNom() + ' ' + user.getPrenom();
    UserInformation lstRights = new UserInformation();
    lstRights.setLoggedAgent(userName);
    lstRights.setRights(rights);
    JAXBContext jaxbContext;
    StringWriter stringWriter = new StringWriter();
    try {
      jaxbContext = JAXBContext.newInstance(lstRights.getClass());
      Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
      jaxbMarshaller.marshal(lstRights, stringWriter);
    } catch (JAXBException e) {
      logger.error(e);
    }

    HttpHeaders responseHeaders = new HttpHeaders();
    return new ResponseEntity<String>(stringWriter.toString(), responseHeaders, HttpStatus.OK);
  }

  @RequestMapping(
      value = "/current/{id}",
      method = RequestMethod.PUT,
      headers = "Accept=application/json")
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<java.lang.String> updateProfilFromJson(
      @PathVariable("id") Long id, @RequestBody String json) {

    final Utilisateur record = Utilisateur.fromJsonToUtilisateur(json);

    // Pas l'utilisateur connecté
    if (record.getId().longValue()
        != utilisateurService.getCurrentUtilisateur().getId().longValue()) {
      throw new AccessDeniedException("L'utilisateur n'est pas autorisé à modifier cette donnée");
    }
    return new AbstractExtObjectSerializer<Utilisateur>("Utilisateur updated") {

      @Override
      protected Utilisateur getRecord() throws BusinessException {
        return utilisateurService.updateProfil(record);
      }

      @Override
      protected JSONSerializer additionnalIncludeExclude(JSONSerializer serializer) {
        return serializer.transform(new DateTransformer("MM/dd/yy"), Date.class);
      }
    }.serialize();
  }

  @RequestMapping(value = "/{id}", method = RequestMethod.PUT, headers = "Accept=application/json")
  @PreAuthorize(
      "hasRight('UTILISATEUR_FILTER_ALL_C') or hasRight('UTILISATEUR_FILTER_ORGANISME_UTILISATEUR_C')")
  public ResponseEntity<java.lang.String> updateFromJson(
      @PathVariable("id") Long id, @RequestBody String json) {

    final Utilisateur record = Utilisateur.fromJsonToUtilisateur(json);
    if (!authUtils.hasRight(TypeDroitEnum.UTILISATEUR_FILTER_ALL_C)) {
      Organisme organisme = utilisateurService.getCurrentUtilisateur().getOrganisme();
      if (organisme.getId().longValue() != record.getOrganisme().getId().longValue()) {
        throw new AccessDeniedException("L'utilisateur n'est pas autorisé à modifier cette donnée");
      }
    }
    return new AbstractExtObjectSerializer<Utilisateur>("Utilisateur updated") {

      @Override
      protected Utilisateur getRecord() {
        return utilisateurService.update(record);
      }

      @Override
      protected JSONSerializer additionnalIncludeExclude(JSONSerializer serializer) {
        return serializer.transform(new DateTransformer("MM/dd/yy"), Date.class);
      }
    }.serialize();
  }

  @RequestMapping(method = RequestMethod.POST, headers = "Accept=application/json")
  @PreAuthorize(
      "hasRight('UTILISATEUR_FILTER_ALL_C') or hasRight('UTILISATEUR_FILTER_ORGANISME_UTILISATEUR_C')")
  public ResponseEntity<java.lang.String> createFromJson(final @RequestBody String json) {
    final Utilisateur record = Utilisateur.fromJsonToUtilisateur(json);
    if (!authUtils.hasRight(TypeDroitEnum.UTILISATEUR_FILTER_ALL_C)) {
      Organisme organisme = utilisateurService.getCurrentUtilisateur().getOrganisme();
      if (record.getOrganisme().getId().longValue() != organisme.getId().longValue()) {
        throw new AccessDeniedException("L'utilisateur n'est pas autorisé à modifier cette donnée");
      }
    }
    try {
      record.setOrganisme(Organisme.findOrganisme(record.getOrganisme().getId()));

      final Utilisateur returned = record.merge();

      // Mot de passe aléatoire en clair
      String password = null;
      do {
        password = UUID.randomUUID().toString().replace("-", "").substring(0, 9);
      } while (!utilisateurService.checkPasswordValidity(password));
      returned.setPassword(password);

      utilisateurService.create(returned);

      return new AbstractExtObjectSerializer<Utilisateur>("Utilisateur created.") {
        @Override
        protected Utilisateur getRecord() throws BusinessException {
          return returned;
        }
      }.serialize();

    } catch (Exception e) {
      // Est-ce une exception liée à une contrainte ?
      ConstraintViolationException e2 =
          ExceptionUtils.getNestedExceptionWithClass(e, ConstraintViolationException.class);
      if (e2 != null) {
        // Contrainte spécifique "utilisateur_identifiant_key" ?
        String msg =
            "utilisateur_identifiant_key".equals(e2.getConstraintName())
                ? "L'identifiant \"" + record.getIdentifiant() + "\" est déjà utilisé."
                : "Une contrainte n'est pas respectée";
        return new SuccessErrorExtSerializer(false, msg + "<br/>Veuillez modifier votre saisie.")
            .serialize();
      }

      // Autre exception : message générique
      logger.error(e.getMessage(), e);
      return new SuccessErrorExtSerializer(false, e.getMessage()).serialize();
    }
  }

  protected String findUsernameConnected() throws BusinessException {
    SecurityContext sc = SecurityContextHolder.getContext();
    Authentication aut = sc.getAuthentication();
    if (aut == null || !aut.isAuthenticated() || "anonymousUser".equals(aut.getPrincipal())) {
      throw new BusinessException("Anonymous users are not allowed");
    }
    User user = (User) (aut.getPrincipal());
    String userName = user.getUsername();
    return userName;
  }

  // --------------------------------------------------
  // - GESTION DES MOTS DE PASSE
  // --------------------------------------------------
  /**
   * Enregistrement d'une demande de réinitialisation de mot de passe.
   *
   * @param identifiant
   * @return
   */
  @RequestMapping(
      value = "/lostpassword",
      method = RequestMethod.POST,
      headers = "Accept=application/json")
  public ResponseEntity<java.lang.String> lostPassword(
      final @RequestParam(value = "identifiant", required = true) String identifiant) {
    try {
      utilisateurService.newDemandeMdp(identifiant);
    } catch (Exception e) {
      return new SuccessErrorExtSerializer(
              false, "Une erreur est survenue lors de l'enregistrement de la demande")
          .serialize();
    }
    return new SuccessErrorExtSerializer(
            true, "La demande de réinitialisation de mot de passe a été transmise")
        .serialize();
  }

  /**
   * Mise à jour du mot de passe d'un utilisateur qui est passé par l'interface de perte de mot de
   * passe.
   *
   * @param code le code de la demande
   * @param plainPwd
   * @return
   */
  @RequestMapping(
      value = "/resetpwd",
      method = RequestMethod.POST,
      headers = "Accept=application/json")
  public ResponseEntity<java.lang.String> resetPassword(
      @RequestParam(value = "code", required = true) String code,
      final @RequestParam(value = "pwd", required = true) String plainPwd) {
    try {
      utilisateurService.resetPasswordFromDde(code, plainPwd);
    } catch (Exception e) {
      return new SuccessErrorExtSerializer(
              false, "Une erreur est survenue lors de la mise à jour du mot de passe")
          .serialize();
    }
    return new SuccessErrorExtSerializer(true, "Le mot de passe a été mis à jour").serialize();
  }

  /**
   * Récupération du dashboard Id
   *
   * @return
   */
  @RequestMapping(
      value = "/dashboard",
      method = RequestMethod.GET,
      headers = "Accept=application/json")
  public ResponseEntity<String> getDashboardId() {
    try {
      String dashboardId = utilisateurService.getDashboardId();
      return new ResponseEntity<String>(dashboardId, HttpStatus.OK);
    } catch (Exception e) {
      return new SuccessErrorExtSerializer(
              false, "Une erreur est survenue lors de la récupération du  tableau de bord")
          .serialize();
    }
  }

  /**
   * Permet de savoir s'il s'agit d'un utilisateur LDAP ou remocra
   *
   * @param identifiant de l'utilisateur
   * @return true si c'est un utilisateur LDAP sinon false
   */
  @RequestMapping(
      value = "/ldapPassword",
      method = RequestMethod.GET,
      headers = "Accept=application/json")
  public ResponseEntity<String> isLdapPassword(
      final @RequestParam(value = "identifiant", required = true) String identifiant) {
    try {
      Boolean isLdapPassword = utilisateurService.isLdapPassword(identifiant);
      return new ResponseEntity(isLdapPassword.toString(), HttpStatus.OK);
    } catch (Exception e) {
      return new SuccessErrorExtSerializer(
              false, "Erreur lors du changement de la récupération du mdp")
          .serialize();
    }
  }
}
