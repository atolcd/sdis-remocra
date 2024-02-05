package fr.sdis83.remocra.usecase.authn;

import com.google.inject.Inject;
import fr.sdis83.remocra.authn.AuthnModule;
import fr.sdis83.remocra.authn.JWTAuthFilter;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AuthCommun {

  private static final Logger logger = LoggerFactory.getLogger(JWTAuthFilter.class);

  private final AuthnModule.Settings settings;

  @Inject
  public AuthCommun(AuthnModule.Settings settings) {
    this.settings = settings;
  }

  public static String encodePassword(String rawPass, String salt) {
    String data = rawPass + "{" + salt + "}";
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

  public String generateToken(String username, Integer nbHeureValideMobile) {
    String jws =
        Jwts.builder()
            //
            .setIssuer(settings.issuer())
            .setSubject(username)
            .setExpiration(
                Date.from(
                    Instant.now()
                        .plus(
                            nbHeureValideMobile != null
                                ? Duration.ofHours(nbHeureValideMobile)
                                : Duration.ofSeconds(settings.expirationSec()))))
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
