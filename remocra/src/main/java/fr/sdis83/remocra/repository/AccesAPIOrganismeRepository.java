package fr.sdis83.remocra.repository;

import static fr.sdis83.remocra.db.model.remocra.Tables.DDE_API;
import static fr.sdis83.remocra.db.model.remocra.Tables.EMAIL;
import static fr.sdis83.remocra.db.model.remocra.Tables.EMAIL_MODELE;
import static fr.sdis83.remocra.db.model.remocra.Tables.ORGANISME;
import static fr.sdis83.remocra.db.model.remocra.Tables.PARAM_CONF;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import org.joda.time.Instant;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.encoding.MessageDigestPasswordEncoder;

@Configuration
public class AccesAPIOrganismeRepository {

  @Autowired DSLContext context;

  @Autowired private MessageDigestPasswordEncoder messageDigestPasswordEncoder;

  public AccesAPIOrganismeRepository() {}

  @Bean
  public AccesAPIOrganismeRepository clefAPIOrganismeRepository(DSLContext context) {
    return new AccesAPIOrganismeRepository(context);
  }

  AccesAPIOrganismeRepository(DSLContext context) {
    this.context = context;
  }

  public void newDdeApi(Long idOrganisme) {
    // On génère un code pour le mail
    String code =
        messageDigestPasswordEncoder.encodePassword(
            new Date().getTime() + String.valueOf(idOrganisme), null);
    context
        .insertInto(DDE_API, DDE_API.ORGANISME, DDE_API.CODE, DDE_API.DATE_DEMANDE)
        .values(idOrganisme, code, new Instant())
        .execute();
    insertMailClefAPI(idOrganisme, code);
  }

  // Insertion du mail pour créer un accès à l'API dans la table des mails
  public void insertMailClefAPI(Long idOrganisme, String code) {

    String corpsMail =
        context
            .select(EMAIL_MODELE.CORPS)
            .from(EMAIL_MODELE)
            .where(EMAIL_MODELE.CODE.eq("ACCES_API"))
            .fetchOne(EMAIL_MODELE.CORPS);
    String objetMail =
        context
            .select(EMAIL_MODELE.OBJET)
            .from(EMAIL_MODELE)
            .where(EMAIL_MODELE.CODE.eq("ACCES_API"))
            .fetchOne(EMAIL_MODELE.OBJET);

    CharSequence key = "[URL_API]";
    CharSequence value =
        context
                .select(PARAM_CONF.VALEUR)
                .from(PARAM_CONF)
                .where(PARAM_CONF.CLE.eq("PDI_URL_SITE"))
                .fetchOne(PARAM_CONF.VALEUR)
            + "#organismesAPI/index/code/"
            + code;
    corpsMail = corpsMail.replace(key, value);

    String nomDestinataire =
        context
            .select(ORGANISME.NOM)
            .from(ORGANISME)
            .where(ORGANISME.ID.eq(idOrganisme))
            .fetchOne(ORGANISME.NOM);
    String emailDestinataire =
        context
            .select(ORGANISME.EMAIL_CONTACT)
            .from(ORGANISME)
            .where(ORGANISME.ID.eq(idOrganisme))
            .fetchOne(ORGANISME.EMAIL_CONTACT);
    ;
    String nomExpediteur =
        context
            .select(PARAM_CONF.VALEUR)
            .from(PARAM_CONF)
            .where(PARAM_CONF.CLE.eq("PDI_SMTP_EME_NAME"))
            .fetchOne(PARAM_CONF.VALEUR);
    String mailExpediteur =
        context
            .select(PARAM_CONF.VALEUR)
            .from(PARAM_CONF)
            .where(PARAM_CONF.CLE.eq("PDI_SMTP_EME_MAIL"))
            .fetchOne(PARAM_CONF.VALEUR);

    context
        .insertInto(
            EMAIL,
            EMAIL.CORPS,
            EMAIL.DESTINATAIRE,
            EMAIL.DESTINATAIRE_EMAIL,
            EMAIL.EXPEDITEUR,
            EMAIL.EXPEDITEUR_EMAIL,
            EMAIL.OBJET)
        .values(
            corpsMail, nomDestinataire, emailDestinataire, nomExpediteur, mailExpediteur, objetMail)
        .execute();
  }

  public boolean getCodeValidity(String code) {
    // On regarde si le code du lien est toujours valide avec la date
    String dateDemandeString =
        context
            .select(DDE_API.DATE_DEMANDE)
            .from(DDE_API)
            .where(DDE_API.CODE.eq(code))
            .fetchOneInto(Instant.class)
            .toString();
    Date dateDemande = null;
    try {
      dateDemande = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(dateDemandeString);
    } catch (ParseException e) {
      e.printStackTrace();
    }
    Calendar c = Calendar.getInstance();
    c.setTime(dateDemande);
    c.add(Calendar.DATE, 1);
    dateDemande = c.getTime();

    // On regarde si le lien a déjà été utilisé
    boolean used =
        context
            .select(DDE_API.UTILISE)
            .from(DDE_API)
            .where(DDE_API.CODE.eq(code))
            .fetchOneInto(Boolean.class);

    return (new Date().before(dateDemande) && !used) ? true : false;
  }

  public void setPasswordOrganisme(String code, String password) throws Exception {
    if (this.checkPasswordValidity(password)) {
      // On applique un salt sur le password et on enregistre en base
      String salt = new BigInteger(40, new SecureRandom()).toString(32);
      String pwdSalt = messageDigestPasswordEncoder.encodePassword(password, salt);

      Long idOrganisme =
          context
              .select(DDE_API.ORGANISME)
              .from(DDE_API)
              .where(DDE_API.CODE.eq(code))
              .fetchOneInto(Long.class);

      context
          .update(ORGANISME)
          .set(ORGANISME.PASSWORD, pwdSalt)
          .set(ORGANISME.SALT, salt)
          .where(ORGANISME.ID.eq(idOrganisme))
          .execute();

      // On indique que le lien a été utilisé
      context.update(DDE_API).set(DDE_API.UTILISE, true).execute();
    } else {
      throw new Exception("Format du mot de passe incorrect");
    }
  }

  /*valide si au moins 9 caractères, au moins 1 chiffre et au moins 1 lettre
   */
  public Boolean checkPasswordValidity(String plainPwd) {
    return (plainPwd.length() >= 9 && plainPwd.matches("^(?=.*[0-9])(?=.*[a-zA-Z])(.+)$"));
  }
}
