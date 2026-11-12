package ma.location.servlet;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ma.location.dao.AuthDao;
import ma.location.model.Client;
import ma.location.model.Role;
import ma.location.model.Utilisateur;
import ma.location.service.ClientService;
import ma.location.util.JPAUtil;

import java.io.IOException;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

@WebServlet("/api/auth/*")
public class AuthApiServlet extends HttpServlet {
    private static final ObjectMapper JSON = new ObjectMapper();
    private static final String COOKIE_NAME = "JWT";
    private final ClientService clientService = new ClientService();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String path = request.getPathInfo();
        if (path == null) {
            sendError(response, 404, "NOT_FOUND", "Authentication route not found");
            return;
        }
        switch (path) {
            case "/login" -> login(request, response);
            case "/register" -> register(request, response);
            case "/logout" -> logout(request, response);
            default -> sendError(response, 404, "NOT_FOUND", "Authentication route not found");
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        if ("/me".equals(request.getPathInfo())) {
            authenticatedUser(request, response);
        } else {
            sendError(response, 404, "NOT_FOUND", "Authentication route not found");
        }
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException {
        if ("/password".equals(request.getPathInfo())) {
            changePassword(request, response);
        } else {
            sendError(response, 404, "NOT_FOUND", "Authentication route not found");
        }
    }

    private void login(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Map<String, String> body = readBody(request, response);
        if (body == null) return;
        String email = value(body, "email");
        String password = firstValue(body, "motDePasse", "password");
        if (email == null || password == null || email.isBlank() || password.isBlank()) {
            sendError(response, 400, "BAD_REQUEST", "Email and password are required");
            return;
        }

        EntityManager em = JPAUtil.getEntityManager();
        try {
            AuthDao dao = dao(em);
            Utilisateur user = dao.login(email.trim(), password);
            if (user == null) {
                sendError(response, 401, "UNAUTHORIZED", "Invalid email or password");
                return;
            }
            String token = dao.createToken(user);
            setJwtCookie(request, response, token);
            clientService.openSession(request, user);
            write(response, 200, Map.of("type", "Cookie", "user", userView(user)));
        } finally {
            em.close();
        }
    }

    private void register(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Map<String, String> body = readBody(request, response);
        if (body == null) return;
        String email = value(body, "email");
        String password = firstValue(body, "motDePasse", "password");
        String nom = value(body, "nom");
        String prenom = value(body, "prenom");
        String cin = value(body, "cin");
        if (isBlank(email) || isBlank(password) || isBlank(nom) || isBlank(prenom) || isBlank(cin)) {
            sendError(response, 400, "BAD_REQUEST", "nom, prenom, cin, email and password are required");
            return;
        }
        if (password.length() < 8) {
            sendError(response, 400, "BAD_REQUEST", "Password must contain at least 8 characters");
            return;
        }

        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            AuthDao dao = dao(em);
            if (dao.findByEmail(email) != null) {
                sendError(response, 409, "CONFLICT", "Email is already registered");
                return;
            }
            boolean cinAlreadyUsed = !em.createQuery(
                            "SELECT c FROM Client c WHERE LOWER(c.cin) = LOWER(:cin)", Client.class)
                    .setParameter("cin", cin.trim())
                    .setMaxResults(1)
                    .getResultList()
                    .isEmpty();
            if (cinAlreadyUsed) {
                sendError(response, 409, "CONFLICT", "CIN is already registered");
                return;
            }
            Client client = new Client();
            client.setNom(nom.trim());
            client.setPrenom(prenom.trim());
            client.setCin(cin.trim());
            client.setEmail(email.trim());
            client.setTelephone(value(body, "telephone"));
            client.setAdresse(value(body, "adresse"));
            client.setNumeroPermis(value(body, "numeroPermis"));
            client.setMotDePasse(dao.hashPassword(password));
            client.setToken(UUID.randomUUID().toString().replace("-", "").substring(0, 20));
            client.setRole(Role.USER);
            tx.begin();
            em.persist(client);
            tx.commit();
            String token = dao.createToken(client);
            setJwtCookie(request, response, token);
            clientService.openSession(request, client);
            write(response, 201, Map.of("type", "Cookie", "user", userView(client)));
        } catch (RuntimeException exception) {
            if (tx.isActive()) tx.rollback();
            sendError(response, 409, "CONFLICT", "Unable to create account");
        } finally {
            em.close();
        }
    }

    private void logout(HttpServletRequest request, HttpServletResponse response) throws IOException {
        clearJwtCookie(request, response);
        clientService.closeSession(request);
        write(response, 200, Map.of("message", "Logged out"));
    }

    private void authenticatedUser(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Utilisateur user = currentUser(request);
        if (user == null) returnError(response, 401, "Missing or invalid authentication");
        else write(response, 200, Map.of("user", userView(user)));
    }

    private void changePassword(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Utilisateur user = currentUser(request);
        if (user == null) { returnError(response, 401, "Missing or invalid authentication"); return; }
        Map<String, String> body = readBody(request, response);
        if (body == null) return;
        String oldPassword = firstValue(body, "oldPassword", "ancienMotDePasse");
        String newPassword = firstValue(body, "newPassword", "motDePasse", "password");
        if (isBlank(oldPassword) || newPassword == null || newPassword.length() < 8) {
            sendError(response, 400, "BAD_REQUEST", "Current and new passwords are required; new password must be at least 8 characters");
            return;
        }
        AuthDao dao = daoForRequest();
        boolean passwordMatches;
        try {
            passwordMatches = org.mindrot.jbcrypt.BCrypt.checkpw(oldPassword, user.getMotDePasse());
        } catch (IllegalArgumentException exception) {
            passwordMatches = false;
        }
        if (!passwordMatches) {
            sendError(response, 401, "UNAUTHORIZED", "Current password is incorrect");
            return;
        }
        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Utilisateur managed = em.find(Utilisateur.class, user.getId());
            managed.setMotDePasse(dao.hashPassword(newPassword));
            tx.commit();
            write(response, 200, Map.of("message", "Password changed"));
        } finally {
            if (tx.isActive()) tx.rollback();
            em.close();
        }
    }

    private Utilisateur currentUser(HttpServletRequest request) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            String token = bearer(request);
            if (token == null) return null;
            AuthDao dao = dao(em);
            Claims claims = dao.parseToken(token);
            return dao.findById(Long.valueOf(claims.getSubject()));
        } catch (RuntimeException exception) {
            return null;
        } finally { em.close(); }
    }

    private AuthDao dao(EntityManager em) { AuthDao dao = new AuthDao(); dao.setEntityManager(em); return dao; }
    private AuthDao daoForRequest() { return new AuthDao(); }

    private String bearer(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header != null && header.regionMatches(true, 0, "Bearer ", 0, 7)) return header.substring(7).trim();
        if (request.getCookies() != null) for (Cookie c : request.getCookies()) if (COOKIE_NAME.equals(c.getName())) return c.getValue();
        return null;
    }

    private void setJwtCookie(HttpServletRequest req, HttpServletResponse resp, String token) {
        String path = req.getContextPath().isEmpty() ? "/" : req.getContextPath();
        resp.setHeader("Set-Cookie", COOKIE_NAME + "=" + token + "; Max-Age=7200; Path=" + path + "; HttpOnly; SameSite=Strict" + (req.isSecure() ? "; Secure" : ""));
    }
    private void clearJwtCookie(HttpServletRequest req, HttpServletResponse resp) {
        String path = req.getContextPath().isEmpty() ? "/" : req.getContextPath();
        resp.setHeader("Set-Cookie", COOKIE_NAME + "=; Max-Age=0; Path=" + path + "; HttpOnly; SameSite=Strict" + (req.isSecure() ? "; Secure" : ""));
    }
    private Map<String, Object> userView(Utilisateur u) {
        Map<String, Object> view = new LinkedHashMap<>();
        view.put("id", u.getId()); view.put("nom", u.getNom()); view.put("prenom", u.getPrenom());
        view.put("email", u.getEmail()); view.put("role", u.getRole().name());
        return view;
    }
    private Map<String, String> readBody(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try { return JSON.readValue(req.getInputStream(), new TypeReference<>() {}); }
        catch (Exception e) { sendError(resp, 400, "BAD_REQUEST", "Request body must be valid JSON"); return null; }
    }
    private String value(Map<String, String> b, String key) { String v = b.get(key); return v == null ? null : v.trim(); }
    private String firstValue(Map<String, String> b, String... keys) { for (String k : keys) if (b.get(k) != null) return b.get(k); return null; }
    private boolean isBlank(String s) { return s == null || s.isBlank(); }
    private void write(HttpServletResponse r, int status, Object body) throws IOException { r.setStatus(status); r.setContentType("application/json"); r.setCharacterEncoding("UTF-8"); JSON.writeValue(r.getWriter(), body); }
    private void sendError(HttpServletResponse r, int status, String error, String message) throws IOException { write(r, status, Map.of("timestamp", Instant.now().toString(), "status", status, "error", error, "message", message)); }
    private void returnError(HttpServletResponse r, int status, String message) throws IOException { sendError(r, status, status == 401 ? "UNAUTHORIZED" : "ERROR", message); }
}
