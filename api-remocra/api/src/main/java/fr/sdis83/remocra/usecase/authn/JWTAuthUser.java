package fr.sdis83.remocra.usecase.authn;

import com.google.inject.Inject;
import fr.sdis83.remocra.repository.OrganismesRepository;
import fr.sdis83.remocra.web.model.authn.OrganismeModel;
import java.util.Optional;
import org.immutables.value.Value;

@Value.Enclosing
public class JWTAuthUser {

  private final OrganismesRepository organismeRepository;

  private final AuthCommun authCommun;

  @Inject
  JWTAuthUser(OrganismesRepository organismeRepository, AuthCommun authCommun) {
    this.organismeRepository = organismeRepository;
    this.authCommun = authCommun;
  }

  public Response authenticate(String email, String password) {
    OrganismeModel user = organismeRepository.readByEmail(email);
    if (user == null) {
      return ImmutableJWTAuthUser.Response.builder().status(Status.NOT_FOUND).build();
    }
    String encodedPassword = authCommun.encodePassword(password, user.getSalt());

    if (user.getPassword().equals(encodedPassword)) {
      return ImmutableJWTAuthUser.Response.builder()
          .status(Status.OK)
          .token(authCommun.generateToken(user.getEmail()))
          .build();
    }

    return ImmutableJWTAuthUser.Response.builder().status(Status.BAD_CREDENTIALS).build();
  }

  @Value.Immutable
  public interface Response {
    @Value.Parameter
    Status status();

    Optional<String> token();
  }

  public enum Status {
    OK,
    NOT_FOUND,
    BAD_CREDENTIALS
  }
}
