package ma.location.servlet;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
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
import java.util.UUID;

@WebServlet({"/login", "/register", "/logout"})
public class AuthServlet extends HttpServlet {
    private final ClientService clientService = new ClientService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        switch (request.getServletPath()) {
            case "/login" -> forward(request, response, "/WEB-INF/views/auth/login.jsp");
            case "/register" -> forward(request, response, "/WEB-INF/views/auth/register.jsp");
            case "/logout" -> logout(request, response);
            default -> response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        switch (request.getServletPath()) {
            case "/login" -> login(request, response);
            case "/register" -> register(request, response);
            case "/logout" -> logout(request, response);
            default -> response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    private void login(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String email = request.getParameter("email");
        String password = request.getParameter("motDePasse");
        EntityManager em = JPAUtil.getEntityManager();
        try {
            AuthDao dao = dao(em);
            Utilisateur utilisateur = dao.login(email, password);
            if (utilisateur == null) {
                redirectError(response, request, "/login", "Email ou mot de passe invalide.");
                return;
            }
            clientService.openSession(request, utilisateur);
            request.getSession().setAttribute("message", "Connexion réussie.");
            String redirect = request.getParameter("redirect");
            if (redirect == null || !redirect.startsWith("/") || redirect.startsWith("//")) {
                redirect = "/accueil";
            }
            response.sendRedirect(request.getContextPath() + redirect);
        } finally {
            em.close();
        }
    }

    private void register(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String email = trim(request.getParameter("email"));
        String password = request.getParameter("motDePasse");
        String nom = trim(request.getParameter("nom"));
        String prenom = trim(request.getParameter("prenom"));
        String cin = trim(request.getParameter("cin"));
        if (isBlank(email) || isBlank(password) || isBlank(nom) || isBlank(prenom) || isBlank(cin)) {
            redirectError(response, request, "/register", "Tous les champs obligatoires doivent être renseignés.");
            return;
        }
        if (password.length() < 8) {
            redirectError(response, request, "/register", "Le mot de passe doit contenir au moins 8 caractères.");
            return;
        }

        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            AuthDao dao = dao(em);
            if (dao.findByEmail(email) != null) {
                redirectError(response, request, "/register", "Cet email est déjà utilisé. Utilisez une autre adresse.");
                return;
            }
            boolean cinAlreadyUsed = !em.createQuery(
                            "select c from Client c where lower(c.cin) = lower(:cin)", Client.class)
                    .setParameter("cin", cin)
                    .setMaxResults(1)
                    .getResultList()
                    .isEmpty();
            if (cinAlreadyUsed) {
                redirectError(response, request, "/register", "Ce CIN est déjà utilisé. Vérifiez le numéro saisi.");
                return;
            }
            Client client = new Client();
            client.setNom(nom);
            client.setPrenom(prenom);
            client.setCin(cin);
            client.setEmail(email);
            client.setTelephone(trim(request.getParameter("telephone")));
            client.setAdresse(trim(request.getParameter("adresse")));
            client.setNumeroPermis(trim(request.getParameter("numeroPermis")));
            client.setMotDePasse(dao.hashPassword(password));
            client.setToken(UUID.randomUUID().toString().replace("-", "").substring(0, 20));
            client.setRole(Role.USER);
            tx.begin();
            em.persist(client);
            tx.commit();
            clientService.openSession(request, client);
            request.getSession().setAttribute("message", "Compte créé avec succès.");
            response.sendRedirect(request.getContextPath() + "/accueil");
        } catch (RuntimeException exception) {
            if (tx.isActive()) tx.rollback();
            redirectError(response, request, "/register", "Impossible de créer le compte.");
        } finally {
            em.close();
        }
    }

    private void logout(HttpServletRequest request, HttpServletResponse response) throws IOException {
        clientService.closeSession(request);
        request.getSession(true).setAttribute("message", "Déconnexion réussie.");
        response.sendRedirect(request.getContextPath() + "/login");
    }

    private AuthDao dao(EntityManager em) {
        AuthDao dao = new AuthDao();
        dao.setEntityManager(em);
        return dao;
    }

    private void forward(HttpServletRequest request, HttpServletResponse response, String view)
            throws ServletException, IOException {
        request.getRequestDispatcher(view).forward(request, response);
    }

    private void redirectError(HttpServletResponse response, HttpServletRequest request,
                               String path, String message) throws IOException {
        response.sendRedirect(request.getContextPath() + path + "?error=" +
                java.net.URLEncoder.encode(message, java.nio.charset.StandardCharsets.UTF_8));
    }

    private String trim(String value) {
        return value == null ? null : value.trim();
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
