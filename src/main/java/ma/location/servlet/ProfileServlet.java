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
import ma.location.service.ClientService;
import ma.location.util.JPAUtil;
import org.mindrot.jbcrypt.BCrypt;

import java.io.IOException;
import java.util.UUID;

@WebServlet({"/profile", "/profile/delete"})
public class ProfileServlet extends HttpServlet {

    private final ClientService clientService = new ClientService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (!requireAuthentication(request, response)) return;

        Client client = findClient(request);
        if (client == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Profil introuvable.");
            return;
        }
        request.setAttribute("profile", client);
        request.getRequestDispatcher("/WEB-INF/views/profile.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        if (!requireAuthentication(request, response)) return;

        if ("/profile/delete".equals(request.getServletPath())) {
            deleteAccount(request, response);
            return;
        }
        updateProfile(request, response);
    }

    private void updateProfile(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        String nom = trim(request.getParameter("nom"));
        String prenom = trim(request.getParameter("prenom"));
        String email = trim(request.getParameter("email"));
        String telephone = trim(request.getParameter("telephone"));
        String adresse = trim(request.getParameter("adresse"));
        String numeroPermis = trim(request.getParameter("numeroPermis"));
        String password = request.getParameter("motDePasse");

        if (isBlank(nom) || isBlank(prenom) || isBlank(email)) {
            redirectError(response, request, "Le nom, le prénom et l'email sont obligatoires.");
            return;
        }
        if (password != null && !password.isBlank() && password.length() < 8) {
            redirectError(response, request, "Le nouveau mot de passe doit contenir au moins 8 caractères.");
            return;
        }

        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            Client client = em.find(Client.class, clientService.currentUserId(request));
            if (client == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Profil introuvable.");
                return;
            }
            Client duplicate = em.createQuery(
                            "select c from Client c where lower(c.email) = lower(:email) and c.id <> :id",
                            Client.class)
                    .setParameter("email", email)
                    .setParameter("id", client.getId())
                    .setMaxResults(1)
                    .getResultStream()
                    .findFirst()
                    .orElse(null);
            if (duplicate != null) {
                redirectError(response, request, "Cet email est déjà utilisé.");
                return;
            }

            tx.begin();
            client.setNom(nom);
            client.setPrenom(prenom);
            client.setEmail(email);
            client.setTelephone(telephone);
            client.setAdresse(adresse);
            client.setNumeroPermis(numeroPermis);
            if (password != null && !password.isBlank()) {
                client.setMotDePasse(new AuthDao().hashPassword(password));
            }
            tx.commit();
            request.getSession().setAttribute("message", "Profil mis à jour.");
            response.sendRedirect(request.getContextPath() + "/profile");
        } catch (RuntimeException exception) {
            if (tx.isActive()) tx.rollback();
            redirectError(response, request, "Impossible de mettre à jour le profil.");
        } finally {
            em.close();
        }
    }

    private void deleteAccount(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            Client client = em.find(Client.class, clientService.currentUserId(request));
            if (client == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Profil introuvable.");
                return;
            }

            String deletedId = UUID.randomUUID().toString().replace("-", "");
            tx.begin();
            client.setNom("Compte");
            client.setPrenom("supprimé");
            client.setEmail("deleted-" + deletedId + "@deleted.local");
            client.setMotDePasse(new AuthDao().hashPassword(UUID.randomUUID().toString()));
            client.setToken(deletedId.substring(0, 20));
            client.setCin("DELETED-" + deletedId.substring(0, 12));
            client.setTelephone(null);
            client.setAdresse(null);
            client.setNumeroPermis(null);
            tx.commit();

            clientService.closeSession(request);
            response.sendRedirect(request.getContextPath() + "/login?deleted=true");
        } catch (RuntimeException exception) {
            if (tx.isActive()) tx.rollback();
            request.getSession().setAttribute("error", "Impossible de supprimer le compte.");
            response.sendRedirect(request.getContextPath() + "/profile");
        } finally {
            em.close();
        }
    }

    private Client findClient(HttpServletRequest request) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.find(Client.class, clientService.currentUserId(request));
        } finally {
            em.close();
        }
    }

    private boolean requireAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        if (!clientService.isAuthenticated(request)) {
            response.sendRedirect(request.getContextPath() + "/login");
            return false;
        }
        return true;
    }

    private void redirectError(HttpServletResponse response, HttpServletRequest request, String message)
            throws IOException {
        request.getSession().setAttribute("error", message);
        response.sendRedirect(request.getContextPath() + "/profile");
    }

    private String trim(String value) {
        return value == null ? null : value.trim();
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
