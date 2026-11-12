package ma.location.servlet;

import ma.location.dao.VehiculeDAO;
import ma.location.model.Vehicule;
import ma.location.model.Role;
import ma.location.service.ClientService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/vehicules")
public class VehiculeServlet extends HttpServlet {
    private VehiculeDAO dao = new VehiculeDAO();
    private final ClientService clientService = new ClientService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (!clientService.isAuthenticated(req)) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }
        String action = req.getParameter("action");

        if ("new".equals(action) && clientService.hasRole(req, Role.ADMIN)) {
            // Formulaire vide pour ajouter
            req.setAttribute("vehicule", new Vehicule());
            req.getRequestDispatcher("/WEB-INF/views/vehicules/form.jsp").forward(req, resp);

        } else if ("edit".equals(action) && clientService.hasRole(req, Role.ADMIN)) {
            // Formulaire rempli pour modifier
            Long id = Long.parseLong(req.getParameter("id"));
            req.setAttribute("vehicule", dao.findById(id));
            req.getRequestDispatcher("/WEB-INF/views/vehicules/form.jsp").forward(req, resp);

        } else {
            // Par défaut : la liste
            req.setAttribute("vehicules", dao.findAll());
            req.setAttribute("clientView", clientService.hasRole(req, Role.USER));
            req.getRequestDispatcher("/WEB-INF/views/vehicules/list.jsp").forward(req, resp);
        }
    }

    // POST = envoyer un formulaire (enregistrer ou supprimer)
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            clientService.requireAdmin(req);
        } catch (SecurityException exception) {
            resp.sendRedirect(req.getContextPath() + "/accueil?error=" +
                    java.net.URLEncoder.encode("Accès réservé à un administrateur.",
                            java.nio.charset.StandardCharsets.UTF_8));
            return;
        }
        String action = req.getParameter("action");

        if ("delete".equals(action)) {
            try {
                Long id = Long.parseLong(req.getParameter("id"));
                dao.delete(id);
                req.getSession().setAttribute("message", "Véhicule supprimé.");
            } catch (Exception e) {
                req.getSession().setAttribute("error", "Suppression impossible : ce véhicule est lié à des contrats.");
            }
            resp.sendRedirect(req.getContextPath() + "/vehicules");
            return;
        }

        // Sinon : enregistrer (ajout ou modification)
        Vehicule v = new Vehicule();
        String id = req.getParameter("id");
        if (id != null && !id.isEmpty()) {
            v = dao.findById(Long.parseLong(id)); // modification : on récupère l'ancien véhicule
        }

        try {
            v.setImmatriculation(req.getParameter("immatriculation").trim().toUpperCase());
            v.setMarque(req.getParameter("marque").trim());
            v.setModele(req.getParameter("modele").trim());
            v.setAnnee(Integer.parseInt(req.getParameter("annee")));
            v.setCarburant(req.getParameter("carburant"));
            v.setKilometrage(Integer.parseInt(req.getParameter("kilometrage")));
            v.setPrixJour(Double.parseDouble(req.getParameter("prixJour")));

            dao.save(v);
            req.getSession().setAttribute("message", "Véhicule enregistré avec succès.");
            resp.sendRedirect(req.getContextPath() + "/vehicules");

        } catch (Exception e) {
            // Erreur : on réaffiche le formulaire avec un message
            req.setAttribute("error", "Enregistrement impossible : vérifiez les champs (immatriculation unique, nombres valides).");
            req.setAttribute("vehicule", v);
            req.getRequestDispatcher("/WEB-INF/views/vehicules/form.jsp").forward(req, resp);
        }
    }
}