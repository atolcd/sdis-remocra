package fr.sdis83.remocra.authn;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.servlet.RequestScoped;
import com.typesafe.config.Config;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.Principal;
import javax.inject.Provider;
import javax.ws.rs.core.SecurityContext;
import org.immutables.value.Value;
import org.jboss.resteasy.spi.ResteasyProviderFactory;

@Value.Enclosing
public class AuthnModule extends AbstractModule {

  @Value.Immutable
  public interface Settings {
    Key key();

    String issuer();

    int expirationSec();
  }

  public static AuthnModule create(Config config) {
    Key key = Keys.hmacShaKeyFor(config.getString("secret").getBytes(StandardCharsets.UTF_8));

    return new AuthnModule(
        ImmutableAuthnModule.Settings.builder()
            .key(key)
            .issuer(config.getString("issuer"))
            .expirationSec(config.getInt("expirationSec"))
            .build());
  }

  private final Settings settings;

  public AuthnModule(Settings settings) {
    this.settings = settings;
  }

  @Override
  protected void configure() {
    bind(Settings.class).toInstance(settings);
  }

  @Provides
  @CurrentUser
  @RequestScoped
  UserPrincipal provideUserPrincipal() {
    SecurityContext context =
        ResteasyProviderFactory.getInstance().getContextData(SecurityContext.class);

    Principal principal = context.getUserPrincipal();
    if (principal instanceof UserPrincipal) {
      return (UserPrincipal) principal;
    }
    return null;
  }

  @Provides
  @CurrentUser
  @RequestScoped
  Long provideUserId(@CurrentUser Provider<UserPrincipal> currentUserPrincipal) {
    UserPrincipal principal = currentUserPrincipal.get();
    if (principal == null) {
      return null;
    }
    return principal.getUserId();
  }

  @Provides
  @CurrentUser
  @RequestScoped
  UserInfo provideUserInfo(@CurrentUser Provider<UserPrincipal> currentUserPrincipal) {
    UserPrincipal principal = currentUserPrincipal.get();
    if (principal == null) {
      return null;
    }
    return principal.getUserInfo();
  }
}
