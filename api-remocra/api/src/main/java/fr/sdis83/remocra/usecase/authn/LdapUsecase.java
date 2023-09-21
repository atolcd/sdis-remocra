package fr.sdis83.remocra.usecase.authn;

import fr.sdis83.remocra.repository.ParamConfRepository;
import java.util.Hashtable;
import javax.inject.Inject;
import javax.naming.Context;
import javax.naming.NameNotFoundException;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.SizeLimitExceededException;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LdapUsecase {

  @Inject ParamConfRepository paramsConfRepository;

  private final Logger logger = LoggerFactory.getLogger(getClass());
  ;

  public enum LdapMethod {
    NONE,
    SIMPLE,
    SEARCHUSER
  }

  @Inject
  public LdapUsecase(ParamConfRepository paramsConfRepository) {
    this.paramsConfRepository = paramsConfRepository;
  }

  private final String cleLdapHost = "PDI_LDAP_URL_HOST";
  private final String cleLdapPort = "PDI_LDAP_URL_PORT";
  private final String cleLdapBaseDn = "PDI_LDAP_URL_BASE_DN";
  private final String cleLdapAdminDn = "PDI_LDAP_ADMIN_DN";
  private final String cleLdapAdminPassword = "PDI_LDAP_ADMIN_PASSWORD";
  private final String cleLdapBaseName = "PDI_LDAP_USER_BASE_NAME";
  private final String cleLdapFilter = "PDI_LDAP_USER_FILTER";

  boolean ldapAuthentication(String username, String password) {
    LdapMethod method = getLdapMethod();
    switch (method) {
      case SIMPLE:
        return ldapAuthenticationDistinguishedName(username, password);
      case SEARCHUSER:
        return LdapAuthenticationSearchUser(username, password);
      default:
        return false;
    }
  }

  public LdapMethod getLdapMethod() {
    String host = getLdapParam(cleLdapHost);

    if (host == null || host.isEmpty()) {
      return LdapMethod.NONE;
    }

    String userBaseName = getLdapParam(cleLdapBaseName);
    if (userBaseName == null || userBaseName.isEmpty()) {
      // Authentification LDAP basée sur couple username/password
      // Possible si dn utilisatetur = identifiant remocra
      return LdapMethod.SIMPLE;
    }

    // Etape de recherche de l'utilisateur pour obtenir son DN
    String dn = getLdapParam(cleLdapAdminDn);
    String password = getLdapParam(cleLdapAdminPassword);
    String userFilter = getLdapParam(cleLdapFilter);
    if (dn == null
        || dn.isEmpty()
        || password == null
        || password.isEmpty()
        || userFilter == null
        || userFilter.isEmpty()) {
      // Il manque les informations de connexion administrateur
      return LdapMethod.NONE;
    }
    return LdapMethod.SEARCHUSER;
  }

  boolean ldapAuthenticationDistinguishedName(String username, String password) {
    Hashtable<String, String> ldapEnv = getLdapEnvironmentSimple();
    if (ldapEnv != null) {
      ldapEnv.put(Context.SECURITY_PRINCIPAL, username);
      ldapEnv.put(Context.SECURITY_CREDENTIALS, password);
      DirContext ctx = null;
      try {
        ctx = new InitialDirContext(ldapEnv);
        // OK
        return true;
      } catch (NamingException e) {
        logger.info("Erreur authent. LDAP (méthode Simple) -> authent. Base", e);
      } finally {
        if (ctx != null) {
          try {
            ctx.close();
          } catch (Exception e) {
            /* Rien */
          }
        }
      }
    }
    // KO
    return false;
  }

  boolean LdapAuthenticationSearchUser(String username, String password) {
    Hashtable<String, String> ldapEnv = getLdapEnvironmentSearchUser();
    if (ldapEnv != null) {
      DirContext ctx;
      try {
        ctx = new InitialDirContext(ldapEnv);
      } catch (NamingException e) {
        logger.info("Impossible de requêter l'annuaire LDAP");
        return false;
      }

      NamingEnumeration<SearchResult> results = null;

      try {
        SearchControls controls = new SearchControls();
        controls.setSearchScope(SearchControls.SUBTREE_SCOPE);
        controls.setCountLimit(1);
        controls.setTimeLimit(5000);

        String ldapUserBaseName = getLdapParam(cleLdapBaseName);
        String ldapUserFilter = getLdapParam(cleLdapFilter);
        ldapUserFilter = ldapUserFilter.replace("[USERNAME]", username);
        results = ctx.search(ldapUserBaseName, ldapUserFilter, controls);

        if (!results.hasMore()) {
          // Utilisateur non trouvé
          return false;
        }
        SearchResult result = (SearchResult) results.next();
        String dn = (String) result.getAttributes().get("distinguishedName").get();

        return ldapAuthenticationDistinguishedName(dn, password);

      } catch (javax.naming.AuthenticationException e) {
        return false;
      } catch (NameNotFoundException e) {
        return false;
      } catch (SizeLimitExceededException e) {
        throw new RuntimeException("Trop de résultats retournés par une requête LDAP", e);
      } catch (NamingException e) {
        throw new RuntimeException(e);
      } finally {
        if (results != null) {
          try {
            results.close();
          } catch (Exception e) {
            /* Rien */
          }
        }
        if (ctx != null) {
          try {
            ctx.close();
          } catch (Exception e) {
            /* Rien */
          }
        }
      }
    }
    return false;
  }

  private String getLdapUrl() {
    String baseDn = getLdapParam(cleLdapBaseDn);
    return "ldap://"
        + getLdapParam(cleLdapHost)
        + ":"
        + getLdapParam(cleLdapPort)
        + (baseDn == null || baseDn.isEmpty() ? "" : "/" + baseDn);
  }

  public Hashtable<String, String> getLdapEnvironmentSimple() {
    Hashtable<String, String> env = new Hashtable<String, String>();
    env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
    env.put(Context.PROVIDER_URL, getLdapUrl());
    // To get rid of the PartialResultException when using Active Directory
    env.put(Context.REFERRAL, "follow");
    // Needed for the Bind (User Authorized to Query the LDAP server)
    env.put(Context.SECURITY_AUTHENTICATION, "simple");
    return env;
  }

  public Hashtable<String, String> getLdapEnvironmentSearchUser() {
    Hashtable<String, String> env = getLdapEnvironmentSimple();
    env.put(Context.SECURITY_PRINCIPAL, getLdapParam(cleLdapAdminDn));
    env.put(Context.SECURITY_CREDENTIALS, getLdapParam(cleLdapAdminPassword));
    return env;
  }

  private String getLdapParam(String cle) {
    return paramsConfRepository.getLdapParam().stream()
        .filter(param -> param.cle.equals(cle))
        .findFirst()
        .get()
        .getValeur();
  }
}
