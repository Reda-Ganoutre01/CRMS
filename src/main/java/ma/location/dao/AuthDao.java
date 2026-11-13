package ma.location.dao;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import io.github.cdimascio.dotenv.Dotenv;
import jakarta.persistence.EntityManager;
import ma.location.model.Utilisateur;
import org.mindrot.jbcrypt.BCrypt;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.List;

public class AuthDao extends GenericDAO<Utilisateur> {

    private static final Duration TOKEN_LIFETIME = Duration.ofHours(2);
    private static final int BCRYPT_ROUNDS = 12;

    private EntityManager entityManager;

    public AuthDao() {
        super(Utilisateur.class);
    }

    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public Utilisateur findById(Long id) {
        return entityManager == null || id == null ? null : entityManager.find(Utilisateur.class, id);
    }

    public Utilisateur findByEmail(String email) {
        if (entityManager == null || email == null || email.isBlank()) {
            return null;
        }
        List<Utilisateur> users = entityManager.createQuery(
                        "SELECT u FROM Utilisateur u WHERE LOWER(u.email) = LOWER(:email)",
                        Utilisateur.class)
                .setParameter("email", email.trim())
                .setMaxResults(1)
                .getResultList();
        return users.isEmpty() ? null : users.get(0);
    }


    public Utilisateur login(String email, String motDePasse) {
        if (entityManager == null) {
            throw new IllegalStateException("EntityManager must be configured before login");
        }
        if (email == null || email.isBlank() || motDePasse == null || motDePasse.isBlank()) {
            return null;
        }

        Utilisateur utilisateur = findByEmail(email);
        if (utilisateur == null) return null;
        try {
            return BCrypt.checkpw(motDePasse, utilisateur.getMotDePasse())
                    ? utilisateur
                    : null;
        } catch (IllegalArgumentException exception) {
            return null;
        }
    }

    public String hashPassword(String motDePasse) {
        if (motDePasse == null || motDePasse.isBlank()) {
            throw new IllegalArgumentException("Password must not be empty");
        }
        return BCrypt.hashpw(motDePasse, BCrypt.gensalt(BCRYPT_ROUNDS));
    }

    public String createToken(Utilisateur utilisateur) {
        if (utilisateur == null || utilisateur.getId() == null) {
            throw new IllegalArgumentException("A persisted user is required to create a token");
        }

        Instant now = Instant.now();
        return Jwts.builder()
                .subject(String.valueOf(utilisateur.getId()))
                .claim("email", utilisateur.getEmail())
                .claim("role", utilisateur.getRole().name())
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plus(TOKEN_LIFETIME)))
                .signWith(getSigningKey())
                .compact();
    }

    public Claims parseToken(String token) {
        if (token == null || token.isBlank()) {
            throw new JwtException("JWT token is missing");
        }
        try {
            Jws<Claims> parsed = Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token);
            return parsed.getPayload();
        } catch (JwtException | IllegalArgumentException exception) {
            throw new JwtException("Invalid JWT token", exception);
        }
    }

    public boolean isTokenValid(String token) {
        try {
            parseToken(token);
            return true;
        } catch (JwtException | IllegalArgumentException exception) {
            return false;
        }
    }

    private SecretKey getSigningKey() {
        String secret = System.getenv("JWT_SECRET");
        if (secret == null || secret.isBlank()) {
            secret = System.getProperty("JWT_SECRET");
        }
        if (secret == null || secret.isBlank()) {
            secret = loadDotenvSecret();
        }
        if (secret == null || secret.getBytes(StandardCharsets.UTF_8).length < 32) {
            throw new IllegalStateException(
                    "JWT_SECRET must be configured with at least 32 characters");
        }
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    private String loadDotenvSecret() {
        Path directory = Paths.get("").toAbsolutePath().normalize();
        Path catalinaBase = propertyPath("catalina.base");
        Path catalinaHome = propertyPath("catalina.home");
        Path[] roots = {catalinaBase, catalinaHome, directory};
        for (Path root : roots) {
            directory = root;
            for (int level = 0; level < 8 && directory != null; level++) {
                String secret = Dotenv.configure()
                        .directory(directory.toString())
                        .ignoreIfMissing()
                        .ignoreIfMalformed()
                        .load()
                        .get("JWT_SECRET");
                if (secret != null && !secret.isBlank()) return secret;
                directory = directory.getParent();
            }
        }
        return null;
    }

    private Path propertyPath(String name) {
        String value = System.getProperty(name);
        return value == null || value.isBlank() ? null : Paths.get(value).toAbsolutePath().normalize();
    }
}
