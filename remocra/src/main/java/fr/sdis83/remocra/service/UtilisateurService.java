package fr.sdis83.remocra.service;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.encoding.MessageDigestPasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import fr.sdis83.remocra.domain.remocra.DdeMdp;
import fr.sdis83.remocra.domain.remocra.EmailModele;
import fr.sdis83.remocra.domain.remocra.EmailModele.EmailModeleEnum;
import fr.sdis83.remocra.domain.remocra.EmailModele.EmailModeleKeys;
import fr.sdis83.remocra.domain.remocra.Organisme;
import fr.sdis83.remocra.domain.remocra.ProfilDroit;
import fr.sdis83.remocra.domain.remocra.ProfilOrganismeUtilisateurDroit;
import fr.sdis83.remocra.domain.remocra.TypeDroit.TypeDroitEnum;
import fr.sdis83.remocra.domain.remocra.Utilisateur;
import fr.sdis83.remocra.exception.BusinessException;
import fr.sdis83.remocra.security.AuthoritiesUtil;
import fr.sdis83.remocra.security.RemocraAuthenticationProvider;
import fr.sdis83.remocra.web.message.ItemFilter;
import fr.sdis83.remocra.web.message.ItemSorting;

@Configuration
public class UtilisateurService {

    private final Logger logger = Logger.getLogger(getClass());

    @Autowired
    private ParamConfService paramConfService;

    @Autowired
    RemocraAuthenticationProvider dbAuthProvider;

    @Autowired
    private MailUtils mailUtils;

    @Autowired
    private AuthoritiesUtil authUtils;

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private MessageDigestPasswordEncoder messageDigestPasswordEncoder;

    public UtilisateurService() {
    }

    @PreAuthorize("isAuthenticated()")
    public Utilisateur getCurrentUtilisateur() {
        String identifiant = AuthService.getCurrentUserIdentifiant();
        Utilisateur utilisateur = this.findUtilisateursWithoutCase(identifiant);
        // Même si les ManyToOne ne sont pas en cascade, il semble que le merge
        // soit quand même fait ... du coup on perd le mot de passe
        // utilisateur.setPassword("");
        return utilisateur;
    }

    @PreAuthorize("isAuthenticated()")
    public Long getCurrentZoneCompetenceId() {
        String identifiant = AuthService.getCurrentUserIdentifiant();
        Object o = entityManager.createNativeQuery("SELECT o.zone_competence FROM remocra.organisme o JOIN remocra.utilisateur u on u.organisme = o.id WHERE u.identifiant = '"+identifiant+"'").getSingleResult();
        return Long.valueOf(o.toString());
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

    public Utilisateur get(Long id) {
        Utilisateur user = Utilisateur.findUtilisateur(id);
        user.setPassword("");
        return user;
    }

    @PreAuthorize("isAuthenticated()")
    public ProfilDroit getCurrentProfilDroit() throws BusinessException {

        Utilisateur utilisateur = getCurrentUtilisateur();

        return ProfilOrganismeUtilisateurDroit.findByOrganismeUtilisateur(utilisateur.getOrganisme().getProfilOrganisme(), utilisateur.getProfilUtilisateur()).getProfilDroit();
    }

    /**
     * Mise à jour des données du profil de l'utilisateur
     * 
     * @param utilisateur
     * @return
     */
    public Utilisateur updateProfil(Utilisateur utilisateur) throws BusinessException {

        Utilisateur attached = Utilisateur.findUtilisateur(utilisateur.getId());
        String plainPassword = utilisateur.getPassword();
        if (!StringUtils.isEmpty(plainPassword) && this.checkPasswordValidity(plainPassword)) {
            dbAuthProvider.encodeNewPasswordForUser(attached, utilisateur.getPassword());

            // Email de confirmation
            mailUtils.envoiEmailWithModele(
                    EmailModele.findByValue(EmailModeleEnum.UTILISATEUR_MAIL_MOT_DE_PASSE),
                    getSystemUtilisateur(),
                    utilisateur,
                    EmailModele.emptyKeyMap().add(EmailModeleKeys.EMAIL, utilisateur.getEmail()).add(EmailModeleKeys.MOT_DE_PASSE, plainPassword)
                            .add(EmailModeleKeys.IDENTIFIANT, utilisateur.getIdentifiant()).add(EmailModeleKeys.URL_SITE, paramConfService.getUrlSite()));
        }
        attached.setMessageRemocra(utilisateur.isMessageRemocra());

        return attached.merge();
    }

    /**
     * Mise à jour des données de l'utilisateur
     * 
     * @param utilisateur
     * @return
     */
    public Utilisateur update(Utilisateur utilisateur) {

        Utilisateur attached = Utilisateur.findUtilisateur(utilisateur.getId());
        // Pas de mise à jour du mot de passe
        // if (!StringUtils.isEmpty(utilisateur.getPassword())) {
        // dbAuthProvider.encodeNewPasswordForUser(attached,
        // utilisateur.getPassword());
        // }
        attached.setEmail(utilisateur.getEmail());
        attached.setIdentifiant(utilisateur.getIdentifiant());
        attached.setNom(utilisateur.getNom());
        attached.setPrenom(utilisateur.getPrenom());
        attached.setTelephone(utilisateur.getTelephone());
        attached.setMessageRemocra(utilisateur.isMessageRemocra());
        attached.setProfilUtilisateur(utilisateur.getProfilUtilisateur());
        if (utilisateur.getOrganisme() != null) {
            attached.setOrganisme(Organisme.findOrganisme(utilisateur.getOrganisme().getId()));
        }
        attached.setActif(utilisateur.getActif());

        return attached.merge();
    }

    public void create(Utilisateur utilisateur) throws BusinessException {

        String plainPassword = utilisateur.getPassword();
        if (StringUtils.isEmpty(plainPassword)) {
            throw new BusinessException("Impossible de créer un utilisateur sans mot de passe.");
        }
        dbAuthProvider.encodeNewPasswordForUser(utilisateur, utilisateur.getPassword());

        // Email de confirmation
        mailUtils.envoiEmailWithModele(
                EmailModele.findByValue(EmailModeleEnum.UTILISATEUR_MAIL_INSCRIPTION),
                getSystemUtilisateur(),
                utilisateur,
                EmailModele.emptyKeyMap().add(EmailModeleKeys.EMAIL, utilisateur.getEmail()).add(EmailModeleKeys.MOT_DE_PASSE, plainPassword)
                        .add(EmailModeleKeys.IDENTIFIANT, utilisateur.getIdentifiant()).add(EmailModeleKeys.URL_SITE, paramConfService.getUrlSite()));

        utilisateur.persist();
    }

    /**
     * Vérifie la validité du mot de passe en fonction de paramètre COMPLEXITE_PASSWORD
     *  -> libre: valide si au moins 1 caractère
     *  -> complexe: valise si au moins 9 caractères, au moins 1 chiffre et au moins 1 lettre
     * @param plainPwd
     * @return TRUE si le password est conforme, FALSE le cas échéant
     */
    public Boolean checkPasswordValidity(String plainPwd){
        if("complexe".equals(paramConfService.getComplexitePassword())){
            return (plainPwd.length() >= 9 && plainPwd.matches("^(?=.*[0-9])(?=.*[a-zA-Z])(.+)$"));
        }
        else{
            return plainPwd.length() > 0;
        }
    }

    @Transactional
    public void resetPasswordFromDde(String code, String plainPwd) throws BusinessException {
        // Récupération de la demande
        DdeMdp demande = DdeMdp.findDdeMdpsByCodeEquals(code).getSingleResult();

        // Mise à jour du mot de passe de l'utilisateur
        this.resetPassword(demande.getUtilisateur(), plainPwd);

        // Suppression de la demande
        demande.remove();
    }

    @Transactional
    public void resetPassword(Utilisateur utilisateur, String plainPassword) throws BusinessException {
        dbAuthProvider.encodeNewPasswordForUser(utilisateur, plainPassword);

        // Email de confirmation
        mailUtils.envoiEmailWithModele(
                EmailModele.findByValue(EmailModeleEnum.UTILISATEUR_MAIL_MOT_DE_PASSE),
                getSystemUtilisateur(),
                utilisateur,
                EmailModele.emptyKeyMap().add(EmailModeleKeys.EMAIL, utilisateur.getEmail()).add(EmailModeleKeys.MOT_DE_PASSE, plainPassword)
                        .add(EmailModeleKeys.IDENTIFIANT, utilisateur.getIdentifiant()).add(EmailModeleKeys.URL_SITE, paramConfService.getUrlSite()));

        utilisateur.merge();
    }

    @Transactional
    public void newDemandeMdp(String identifiant) throws BusinessException {
        Utilisateur utilisateur = this.findUtilisateursWithoutCase(identifiant);
        DdeMdp demande = new DdeMdp();

        demande.setCode(messageDigestPasswordEncoder.encodePassword(new Date().getTime() + identifiant, null));
        demande.setUtilisateur(utilisateur);
        demande.setDateDemande(new Date());
        demande.persist();

        // Email de confirmation
        mailUtils.envoiEmailWithModele(
                EmailModele.findByValue(EmailModeleEnum.UTILISATEUR_MAIL_MOT_DE_PASSE_PERDU),
                getSystemUtilisateur(),
                utilisateur,
                EmailModele.emptyKeyMap().add(EmailModeleKeys.EMAIL, utilisateur.getEmail()).add(EmailModeleKeys.IDENTIFIANT, utilisateur.getIdentifiant())
                        .add(EmailModeleKeys.URL_SITE, paramConfService.getUrlSite()).add(EmailModeleKeys.CODE, demande.getCode()));

        utilisateur.merge();
    }

    /**
     * Cherche les utilisateurs répondant aux critères de filtrages.
     * 
     * @param firstResult
     * @param maxResults
     * @param itemSortings
     * @param itemFilters
     * @return
     */
    public List<Utilisateur> findUtilisateurs(int firstResult, int maxResults, List<ItemSorting> itemSortings, List<ItemFilter> itemFilters) {
        CriteriaBuilder cBuilder = Utilisateur.entityManager().getCriteriaBuilder();
        CriteriaQuery<Utilisateur> itemQuery = cBuilder.createQuery(Utilisateur.class);
        Root<Utilisateur> from = itemQuery.from(Utilisateur.class);
        // make predicates
        Predicate[] predicates = makeFilterPredicates(from, itemFilters);
        // make orders
        List<Order> orders = makeOrders(from, itemSortings);
        // get the items
        itemQuery.select(from).where(predicates).orderBy(orders);
        TypedQuery<Utilisateur> itemTypedQuery = Utilisateur.entityManager().createQuery(itemQuery);
        List<Utilisateur> resultList = itemTypedQuery.setFirstResult(firstResult).setMaxResults(maxResults).getResultList();
        return resultList;
    }

    /**
     * Compte les utilisateur répondant aux critères de filtrage.
     * 
     * @param itemFilters
     * @return
     */
    public Long countUtilisateurs(List<ItemFilter> itemFilters) {
        CriteriaBuilder cBuilder = Utilisateur.entityManager().getCriteriaBuilder();
        CriteriaQuery<Long> totalQuery = cBuilder.createQuery(Long.class);
        Root<Utilisateur> from = totalQuery.from(Utilisateur.class);
        // make predicates
        Predicate[] predicates = makeFilterPredicates(from, itemFilters);
        // get total number of items.
        totalQuery.select(cBuilder.count(from));
        totalQuery.where(predicates);
        TypedQuery<Long> longTypedQuery = Utilisateur.entityManager().createQuery(totalQuery);
        return longTypedQuery.getSingleResult();
    }

    public List<Order> makeOrders(Root<Utilisateur> from, List<ItemSorting> itemSortings) {
        ArrayList<Order> orders = new ArrayList<Order>();
        CriteriaBuilder cBuilder = Utilisateur.entityManager().getCriteriaBuilder();
        if (itemSortings != null && !itemSortings.isEmpty()) {
            for (ItemSorting itemSorting : itemSortings) {
                if ("organismeId".equals(itemSorting.getFieldName())) {
                    Expression<String> cpPath = from.join("organisme").get("nom");
                    orders.add(itemSorting.isDesc() ? cBuilder.desc(cpPath) : cBuilder.asc(cpPath));
                } else if ("profilUtilisateurId".equals(itemSorting.getFieldName())) {
                    Expression<String> cpPath = from.join("profilUtilisateur").get("nom");
                    orders.add(itemSorting.isDesc() ? cBuilder.desc(cpPath) : cBuilder.asc(cpPath));
                } else {
                    orders.add(itemSorting.isDesc() ? cBuilder.desc(from.get(itemSorting.getFieldName())) : cBuilder.asc(from.get(itemSorting.getFieldName())));
                }
            }
        }
        return orders;
    }

    public Predicate[] makeFilterPredicates(Root<Utilisateur> from, List<ItemFilter> itemFilters) {
        List<Predicate> predicateList = new ArrayList<Predicate>();
        boolean doNotAddSecurityPredicate = false;
        if (itemFilters != null && !itemFilters.isEmpty()) {
            for (ItemFilter itemFilter : itemFilters) {
                if ("identifiant".equals(itemFilter.getFieldName())) {
                    CriteriaBuilder cBuilder = Utilisateur.entityManager().getCriteriaBuilder();
                    Expression<String> cpPath = from.get("identifiant");
                    Predicate predicat = cBuilder.like(cBuilder.upper(cpPath), "%" + itemFilter.getValue().toUpperCase(Locale.FRANCE) + "%");
                    predicateList.add(predicat);
                } else if ("nom".equals(itemFilter.getFieldName())) {
                    CriteriaBuilder cBuilder = Utilisateur.entityManager().getCriteriaBuilder();
                    Expression<String> cpPath = from.get("nom");
                    Predicate predicat = cBuilder.like(cBuilder.upper(cpPath), "%" + itemFilter.getValue().toUpperCase(Locale.FRANCE) + "%");
                    predicateList.add(predicat);
                } else if ("organismeId".equals(itemFilter.getFieldName())) {
                    predicateList.add(organismeFieldPredicate(from, itemFilter));
                } else if ("organismeCode".equals(itemFilter.getFieldName())) {
                    predicateList.add(organismeCodeFieldPredicate(from, itemFilter));
                } else if ("organismeType".equals(itemFilter.getFieldName())) {
                    Predicate p = organismeTypeFieldPredicate(from, itemFilter);
                    if (p != null) {
                        predicateList.add(p);
                    }
                } else if ("organismeTypeCodes".equals(itemFilter.getFieldName())) {
                    predicateList.add(organismeTypeCodesFieldPredicate(from, itemFilter));
                } else if ("hasRight".equals(itemFilter.getFieldName())) {
                    predicateList.add(hasRightPredicate(from, itemFilter));
                } else if ("actif".equals(itemFilter.getFieldName())) {
                    Predicate p = null;
                    try {
                        p = actifFieldPredicate(from, itemFilter);
                    } catch (Exception e) {
                        // Le parametre n'est pas traduisible en booleen, on
                        // n'ajoute pas le parametre...
                    }
                    if (p != null) {
                        predicateList.add(p);
                    }
                } else if ("dnasp".equals(itemFilter.getFieldName()) && "true".equals(itemFilter.getValue()) && authUtils.hasRight(TypeDroitEnum.RCI_C)) {
                    // On choisit de ne pas filtrer sur les droits utilisateurs
                    // Permis dans les RCCI uniquement (dans un premier temps au moins)
                    doNotAddSecurityPredicate = true;
                }
            }

        }
        // Add security predicate
        Predicate predicate = doNotAddSecurityPredicate ? null : isAllowedUtilisateurPredicate(from);
        if (predicate != null) {
            predicateList.add(predicate);
        }

        Predicate[] predicates = new Predicate[0];
        predicates = predicateList.toArray(predicates);

        return predicates;
    }

    private Predicate isAllowedUtilisateurPredicate(Root<Utilisateur> utilisateurPath) {

        CriteriaBuilder cBuilder = Utilisateur.entityManager().getCriteriaBuilder();
        Utilisateur currentUtilisateur = getCurrentUtilisateur();

        Predicate p = cBuilder.disjunction(); // ne renvoi rien par defaut.

        if (authUtils.hasRight(TypeDroitEnum.UTILISATEUR_FILTER_ALL_R) || authUtils.hasRight(TypeDroitEnum.UTILISATEUR_FILTER_ALL_C)) {
            // Tous les utilisateurs
            p = cBuilder.or(cBuilder.conjunction());

        } else if (authUtils.hasRight(TypeDroitEnum.UTILISATEUR_FILTER_ORGANISME_UTILISATEUR_R) || authUtils.hasRight(TypeDroitEnum.UTILISATEUR_FILTER_ORGANISME_UTILISATEUR_C)) {
            // Utilisateurs du même organisme
            p = cBuilder.or(p, cBuilder.equal(utilisateurPath.join("organisme").get("id"), currentUtilisateur.getOrganisme().getId()));

        } else {
            // Utilisateur connecté uniquement
            p = cBuilder.or(p, cBuilder.equal(utilisateurPath.get("id"), currentUtilisateur.getId()));
        }

        return p;

    }

    private Predicate actifFieldPredicate(Root<Utilisateur> from, ItemFilter itemFilter) throws ParseException {
        CriteriaBuilder cBuilder = Utilisateur.entityManager().getCriteriaBuilder();

        Boolean value = parseBoolean(itemFilter.getValue());

        Expression<Boolean> actifPath = from.get("actif");
        Predicate actifUtilisateur = cBuilder.isTrue(actifPath);

        Expression<Boolean> orgActifPath = from.join("organisme").get("actif");
        Predicate actifLaboratoire = cBuilder.isTrue(orgActifPath);
        if (value) {
            return cBuilder.and(actifUtilisateur, actifLaboratoire);
        } else {

            return cBuilder.not(cBuilder.and(actifUtilisateur, actifLaboratoire));
        }
    }

    /*
     * Boolean.parseBoolean() renvoi par défaut false. Or on ne veux pas ajouter
     * de valeur false si la valeur est mauvaise...
     */
    private Boolean parseBoolean(String value) throws ParseException {
        if ("true".equals(value.toLowerCase())) {
            return Boolean.TRUE;
        } else if ("false".equals(value.toLowerCase())) {
            return Boolean.FALSE;
        } else {
            throw new ParseException("Cannot parse '" + value + "' as boolean", 0);
        }
    }

    private Predicate organismeFieldPredicate(Root<Utilisateur> from, ItemFilter itemFilter) {
        Expression<String> cpPath = from.join("organisme").get("id");
        CriteriaBuilder cBuilder = Utilisateur.entityManager().getCriteriaBuilder();
        Predicate equal = cBuilder.equal(cpPath, itemFilter.getValue());
        return equal;
    }

    private Predicate organismeCodeFieldPredicate(Root<Utilisateur> from, ItemFilter itemFilter) {
        Expression<String> cpPath = from.join("organisme").get("code");
        CriteriaBuilder cBuilder = Utilisateur.entityManager().getCriteriaBuilder();
        Predicate equal = cBuilder.equal(cpPath, itemFilter.getValue());
        return equal;
    }

    private Predicate organismeTypeFieldPredicate(Root<Utilisateur> from, ItemFilter itemFilter) {
        Expression<String> cpPath = from.join("organisme").join("typeOrganisme").get("id");
        CriteriaBuilder cBuilder = Utilisateur.entityManager().getCriteriaBuilder();
        Predicate equal = cBuilder.equal(cpPath, itemFilter.getValue());
        return equal;
    }

    /**
     * Filtrage par types d'organismes codes séparés par des %
     * 
     * @param from
     * @param itemFilter
     * @return
     */
    private Predicate organismeTypeCodesFieldPredicate(Root<Utilisateur> from, ItemFilter itemFilter) {
        Expression<String> cpPath = from.join("organisme").join("typeOrganisme").get("code");
        CriteriaBuilder cBuilder = Utilisateur.entityManager().getCriteriaBuilder();
        Predicate p = cBuilder.disjunction(); // ne renvoi rien par defaut.

        String codes = itemFilter.getValue();
        String[] splitted = codes.split("%");

        for (String code : splitted) {
            p = cBuilder.or(p, cBuilder.equal(cpPath, code));
        }
        return p;
    }

    /**
     * Filtrage par type de droit. Valeur : "Code droit".
     * Exemple : RCI_C
     * 
     * @param from
     * @param itemFilter
     * @return
     */
    private Predicate hasRightPredicate(Root<Utilisateur> from, ItemFilter itemFilter) {
        String typeDroitCode = itemFilter.getValue();

        EntityManager entityManager = Utilisateur.entityManager();
        String sql = "select distinct profil_utilisateur from remocra.profil_organisme_utilisateur_droit where profil_droit in ("
                + "select profil_droit from remocra.droit where type_droit=(select id from remocra.type_droit where code='" + typeDroitCode + "')"
                + ")";
        Query query = entityManager.createNativeQuery(sql);
        @SuppressWarnings("unchecked")
        List<Long> results = query.getResultList();

        Expression<String> cpPath = from.join("profilUtilisateur").get("id");
        Predicate p = cpPath.in(results);

        return p;
    }


    /**
     * find user without case sensitive
     */
    public Utilisateur findUtilisateursWithoutCase( String username) {
        TypedQuery<Utilisateur> q = Utilisateur.entityManager().createQuery("SELECT u FROM Utilisateur AS u WHERE LOWER(u.identifiant) = :identifiant", Utilisateur.class);
        q.setParameter("identifiant", username.toLowerCase());
        return  q.getSingleResult();
    }
}
