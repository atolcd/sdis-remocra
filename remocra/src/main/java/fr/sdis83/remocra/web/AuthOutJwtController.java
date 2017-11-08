package fr.sdis83.remocra.web;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Date;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.util.UriComponentsBuilder;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;

import fr.sdis83.remocra.domain.remocra.Utilisateur;
import fr.sdis83.remocra.service.ParamConfService;

/**
 * <p>
 * <u><b>Authentication à un service tiers via un jeton jwt.</u></b>
 * </p>
 * <p>
 * Exemple d'utilisation : authentification prevarisc.
 * </p>
 * 
 * <p>
 * Génération des clés aux formats pem et der (RSA 2048-bit, PKCS#8) :
 * 
 * <pre>
KEYNAME=remocra
mkdir -p /var/remocra/keys
cd /var/remocra/keys
openssl genrsa -out ${KEYNAME}_key.pem 2048
openssl pkcs8 -topk8 -inform PEM -outform DER -in ${KEYNAME}_key.pem -out ${KEYNAME}_key.der -nocrypt
openssl rsa -in ${KEYNAME}_key.pem -pubout -outform DER -out ${KEYNAME}_pub_key.der
openssl rsa -in ${KEYNAME}_key.pem -pubout -outform PEM -out  ${KEYNAME}_pub_key.pem
 * </pre>
 * </p>
 * 
 * <p>
 * Configuration :
 * <ul>
 * <li>SORTIE_JWT_CLEPUBLIQUE : Clé publique (.der) pour les jetons jwt</li>
 * <li>SORTIE_JWT_CLEPRIVEE : Clé privée (.der) pour les jetons jwt</li>
 * <li>SORTIE_JWT_ISSUER : Service générateur des jetons jwt (remocra par
 * défaut)</li>
 * <li>SORTIE_JWT_VALIDITE_SEC : Durée de validité des jetons jwt en secondes
 * (30 par défaut)</li>
 * </ul>
 * </p>
 * 
 * <p>
 * Exemples d'URLs (dans index-pri.json ou ailleurs) :
 * <ul>
 * <li>https://remocra.sdisxx.fr/remocra/sso/out/jwt?service=prevarisc&url=https%3A%2F%2Fprevarisc.sdisxx.fr%2Fsession%2Flogin</li>
 * <li>https://remocra.sdisxx.fr/remocra/sso/out/jwt?service=prevarisc&url=https%3A%2F%2Fprevarisc.sdisxx.fr%2Fsearch%2Fdossier%3Fobjet%3D%26page%3D1
 * (permis si le plugin prevarisc JwtAuth est prioritaire)</li>
 * </ul>
 * </p>
 */
@RequestMapping("/sso/out/jwt")
@Controller
public class AuthOutJwtController extends AbstractRemocraController {

    private final Logger logger = Logger.getLogger(getClass());

    @Autowired
    protected ParamConfService paramConfService;

    @PersistenceContext
    protected EntityManager entityManager;

    /**
     * <p>
     * Crée un jeton signé avec le issuer <i>service</i> et redirige
     * l'utilisateur vers l'url <i>url</i> à laquelle on a ajouté le jeton
     * <i>jwt</i>.
     * <p>
     * Exemple de jeton :
     * 
     * <pre>
      Header : { "alg": "RS256", "typ": "JWT" }
      Payload : { "exp": 1510071739, "sub": "sdis-adm-app", "iss": "remocra", "aud": "prevarisc", "iat": 1510071709 }
     * </pre>
     * </p>
     * 
     * @param request
     * @param url
     *            Url du service externe vers lequel rediriger l'utilisateur.
     * @param service
     *            service concerné (issuer du ticket)
     * @return
     */
    @RequestMapping(value = "", method = RequestMethod.GET)
    @PreAuthorize("isAuthenticated()")
    public ModelAndView connectTo(HttpServletRequest request, final @RequestParam(value = "url", required = true) String url,
            final @RequestParam(value = "service", required = true) String service) {
        Utilisateur user = utilisateurService.getCurrentUtilisateur();
        String token = createToken(user.getIdentifiant(), service);

        UriComponentsBuilder b = UriComponentsBuilder.fromHttpUrl(url);
        b.queryParam("jwt", token);
        return new ModelAndView("redirect:" + b.build().toUriString());
    }

    protected String createToken(String username, String audience) {
        try {
            RSAPublicKey publicKey = (RSAPublicKey) getPublicKey(paramConfService.getJwtOutPublicKey());
            RSAPrivateKey privateKey = (RSAPrivateKey) getPrivateKey(paramConfService.getJwtOutPrivateKey());

            Algorithm algorithm = Algorithm.RSA256(publicKey, privateKey);
            return JWT.create()
                    // Issuer
                    .withIssuer(paramConfService.getJwtOutIssuer()).withIssuedAt(new Date(System.currentTimeMillis()))
                    // ExpiresAt
                    .withExpiresAt(new Date(System.currentTimeMillis() + paramConfService.getJwtOutValidite() * 1000))
                    // Subject
                    .withSubject(username)
                    // Audience
                    .withAudience(audience)
                    // Sign
                    .sign(algorithm);
        } catch (Exception e) {
            String errorMsg = "Erreur lors du calcul du jeton";
            logger.error(errorMsg, e);
            throw new RuntimeException(errorMsg);
        }
    }

    protected static PrivateKey getPrivateKey(String filename) throws Exception {
        byte[] keyBytes = Files.readAllBytes(Paths.get(filename));
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        return kf.generatePrivate(spec);
    }

    protected static PublicKey getPublicKey(String filename) throws Exception {
        byte[] keyBytes = Files.readAllBytes(Paths.get(filename));
        X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        return kf.generatePublic(spec);
    }
}