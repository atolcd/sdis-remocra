package fr.sdis83.remocra.domain.remocra;

import java.util.HashMap;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.TypedQuery;
import javax.persistence.Version;

import org.apache.commons.lang3.StringUtils;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.activerecord.RooJpaActiveRecord;
import org.springframework.roo.addon.json.RooJson;
import org.springframework.roo.addon.tostring.RooToString;
import org.springframework.transaction.annotation.Transactional;

@RooJavaBean
@RooToString
@RooJpaActiveRecord(finders = { "findEmailModelesByCode" })
@RooJson
public class EmailModele implements ITypeReference {

    /**
     * Clés remplaçables dans un modèle
     * 
     */
    public static enum EmailModeleKeys {
        IDENTIFIANT, URL_SITE, EMAIL, MOT_DE_PASSE, CODE, NOM_ORGANISME;
    }

    public static class EmailModeleKeyMap extends HashMap<EmailModeleKeys, String> {

        private static final long serialVersionUID = 1L;

        public EmailModeleKeyMap add(EmailModeleKeys key, String value) {
            this.put(key, value);
            return this;
        }
    }

    /**
     * Modèles de message
     */
    public static enum EmailModeleEnum {
        UTILISATEUR_MAIL_INSCRIPTION("UTILISATEUR_MAIL_INSCRIPTION"), UTILISATEUR_MAIL_MOT_DE_PASSE_PERDU("UTILISATEUR_MAIL_MOT_DE_PASSE_PERDU"), UTILISATEUR_MAIL_MOT_DE_PASSE(
                "UTILISATEUR_MAIL_MOT_DE_PASSE"), CREATION_RCI("CREATION_RCI"), DEPOT_DELIB("DEPOT_DELIB"), DEPOT_DECLAHYDRANT("DEPOT_DECLAHYDRANT"), DEPOT_RECEPTRAVAUX("DEPOT_RECEPTRAVAUX");

        private final String value;

        EmailModeleEnum(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

    }

    public static EmailModeleKeyMap emptyKeyMap() {
        return new EmailModeleKeyMap();
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Version
    @Column(name = "version", columnDefinition = "INTEGER default 1")
    private Integer version;

    private String code;

    private String objet;

    private String corps;

    public EmailModeleEnum getValue() {
        try {
            return EmailModeleEnum.valueOf(code);
        } catch (IllegalArgumentException ex) {
            return null;
        }
    }

    @Transactional
    public static EmailModele findByValue(EmailModeleEnum value) {
        TypedQuery<EmailModele> findEmailModeleByCode = EmailModele.findEmailModelesByCode(value.getValue());
        EmailModele singleResult;
        try {
            singleResult = findEmailModeleByCode.getSingleResult();
        } catch (EmptyResultDataAccessException ex) {

            // Essaye de créer la valeur
            EmailModele emailModele = new EmailModele();
            emailModele.setCode(value.getValue());
            emailModele.setObjet("TEMP_OBJET_" + value.getValue());
            emailModele.setCorps("TEMP_CORPS_" + value.getValue());
            EmailModele attached = emailModele.merge();
            return attached;
        }
        return singleResult;
    }

    public boolean isAllowedByUser(Utilisateur user) {
        if (EmailModeleEnum.UTILISATEUR_MAIL_MOT_DE_PASSE.equals(this.getValue())) {
            // Envoi d'un nouveau mot de passe toujour autorisé
            return true;
        }

        return true;
    }

    public String getObjetReplaced(EmailModeleKeyMap args) {
        return replaceOccurences(args, getObjet());
    }

    public String getCorpsReplaced(EmailModeleKeyMap args) {
        return replaceOccurences(args, getCorps());
    }

    public String replaceOccurences(EmailModeleKeyMap args, String stringToReplace) {
        if (stringToReplace != null && args != null && args.size() != 0) {
            for (EmailModeleKeys key : args.keySet()) {
                stringToReplace = stringToReplace.replace("[" + key + "]", StringUtils.stripToEmpty(args.get(key)));
            }
        }
        return stringToReplace;
    }
}
