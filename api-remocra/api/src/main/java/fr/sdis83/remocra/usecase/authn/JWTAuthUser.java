package fr.sdis83.remocra.usecase.authn;

import com.google.inject.Inject;
import fr.sdis83.remocra.authn.AuthnModule;
import fr.sdis83.remocra.authn.JWTAuthFilter;
import fr.sdis83.remocra.repository.OrganismesRepository;
import fr.sdis83.remocra.web.model.authn.OrganismeModel;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import org.immutables.value.Value;

import javax.xml.bind.DatatypeConverter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Value.Enclosing
public class JWTAuthUser {

  private final OrganismesRepository organismeRepository;

  private final AuthnModule.Settings settings;

  private static final Logger logger = LoggerFactory.getLogger(JWTAuthFilter.class);

  @Inject
  JWTAuthUser(OrganismesRepository organismeRepository, AuthnModule.Settings settings) {
    this.organismeRepository = organismeRepository;
    this.settings = settings;
  }

  public Response authenticate(String email, String password) {
    OrganismeModel user = organismeRepository.readByEmail(email);
    if (user == null) {
      return ImmutableJWTAuthUser.Response.builder().status(Status.NOT_FOUND).build();
    }

    String encodedPassword = JWTAuthUser.encodePassword(password, user.getSalt());

    if (user.getPassword().equals(encodedPassword)) {
      return ImmutableJWTAuthUser.Response.builder()
          .status(Status.OK)
          .token(generateToken(user.getEmail()))
          .build();
    }

    return ImmutableJWTAuthUser.Response.builder().status(Status.BAD_CREDENTIALS).build();
  }

  private static String encodePassword(String rawPass, String salt) {
    String data = rawPass+"{"+salt+"}";
    MessageDigest messageDigest;
    try {
      messageDigest = MessageDigest.getInstance("MD5");
      messageDigest.update(data.getBytes());
      byte[] messageDigestMD5 = messageDigest.digest();
      StringBuffer stringBuffer = new StringBuffer();
      for (byte bytes : messageDigestMD5) {
        stringBuffer.append(String.format("%02x", bytes & 0xff));
      }
      return stringBuffer.toString();
    } catch (NoSuchAlgorithmException exception) {
      logger.error("Error while encoding password: provided algorithm not found");
    }
    return null;
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

  public String generateToken(String username) {
    String jws =
      Jwts.builder()
        //
        .setIssuer(settings.issuer())
        .setSubject(username)
        .setExpiration(Date.from(Instant.now().plusSeconds(settings.expirationSec())))
        .setIssuedAt(new Date())
        .setId(UUID.randomUUID().toString())
        //
        .signWith(settings.key())
        .compact();
    return jws;
  }

  public String readToken(String jws) throws JwtException {
    return Jwts.parserBuilder()
      //
      .setSigningKey(settings.key())
      .build()
      //
      .parseClaimsJws(jws)
      //
      .getBody()
      .getSubject();
  }
}
