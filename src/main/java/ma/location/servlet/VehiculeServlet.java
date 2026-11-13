package ma.location.servlet;

import ma.location.dao.VehiculeDAO;
import ma.location.model.Vehicule;
import ma.location.service.ClientService;
import ma.location.util.ImageUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import java.io.IOException;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


@WebServlet("/vehicules")
@MultipartConfig(maxFileSize = 2 * 1024 * 1024) // permet d'envoyer une photo (2 Mo maximum)
public class VehiculeServlet extends HttpServlet {

    private VehiculeDAO dao = new VehiculeDAO();
    private final ClientService clientService = new ClientService();


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = req.getParameter("action");

        if ("new".equals(action)) {
            if (!clientService.hasRole(req, ma.location.model.Role.ADMIN)) {
                resp.sendRedirect(req.getContextPath() + "/login");
                return;
            }
            // Formulaire vide pour ajouter

            req.setAttribute("vehicule", new Vehicule());
            req.getRequestDispatcher("/WEB-INF/views/vehicules/form.jsp").forward(req, resp);

        } else if ("edit".equals(action)) {
            if (!clientService.hasRole(req, ma.location.model.Role.ADMIN)) {
                resp.sendRedirect(req.getContextPath() + "/login");
                return;
            }
            // Formulaire rempli pour modifier
            Long id = Long.parseLong(req.getParameter("id"));
            req.setAttribute("vehicule", dao.findById(id));
            req.getRequestDispatcher("/WEB-INF/views/vehicules/form.jsp").forward(req, resp);

        } else {
            try {
                boolean admin = clientService.hasRole(req, ma.location.model.Role.ADMIN);
                String search = req.getParameter("search");
                search = search == null ? null : search.trim();
                String dateDebut = req.getParameter("dateDebut");
                List<Vehicule> vehicules;
                if (!admin && dateDebut != null && !dateDebut.isBlank()) {
                    LocalDate debut = LocalDate.parse(dateDebut);
                    vehicules = dao.findDisponiblesEntre(debut, search);
                } else {
                    vehicules = dao.findAll(search);
                }
                req.setAttribute("vehicules", vehicules);
                Map<Long, LocalDate> datesDisponibilite = new LinkedHashMap<>();
                for (Vehicule vehicule : vehicules) {
                    datesDisponibilite.put(vehicule.getId(), dao.findDateDisponibilite(vehicule.getId()));
                }
                req.setAttribute("datesDisponibilite", datesDisponibilite);
            } catch (RuntimeException exception) {
                req.setAttribute("error", exception.getMessage());
                List<Vehicule> vehicules = dao.findAll();
                req.setAttribute("vehicules", vehicules);
                Map<Long, LocalDate> datesDisponibilite = new LinkedHashMap<>();
                for (Vehicule vehicule : vehicules) {
                    datesDisponibilite.put(vehicule.getId(), dao.findDateDisponibilite(vehicule.getId()));
                }
                req.setAttribute("datesDisponibilite", datesDisponibilite);
            }
            req.getRequestDispatcher("/WEB-INF/views/vehicules/list.jsp").forward(req, resp);
        }
    }

    // (enregistrer ou supprimer)
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (!clientService.hasRole(req, ma.location.model.Role.ADMIN)) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        String action = req.getParameter("action");

        if ("delete".equals(action)) {
            try {
                Long id = Long.parseLong(req.getParameter("id"));
                Vehicule aSupprimer = dao.findById(id);
                dao.delete(id);
                if (aSupprimer != null) {
                    ImageUtil.supprimer(aSupprimer.getImage()); // on supprime aussi sa photo
                }
                req.getSession().setAttribute("message", "Véhicule supprimé.");
            } catch (Exception e) {
                req.getSession().setAttribute("error", "Suppression impossible : ce véhicule est lié à des contrats.");
            }
            resp.sendRedirect(req.getContextPath() + "/vehicules");
            return;
        }

        // (ajout ou modification)
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

            // Photo (facultative) : si le formulaire en contient une nouvelle
            Part photo = req.getPart("image");
            boolean nouvellePhoto = photo != null && photo.getSize() > 0;
            String anciennePhoto = v.getImage();
            if (nouvellePhoto) {
                v.setImage(ImageUtil.sauvegarder(photo));
            }

            dao.save(v);

            if (nouvellePhoto) {
                ImageUtil.supprimer(anciennePhoto); // l'ancienne photo n'est plus utile
            }
            req.getSession().setAttribute("message", "Véhicule enregistré avec succès.");
            resp.sendRedirect(req.getContextPath() + "/vehicules");

        } catch (Exception e) {
            // Erreur : on réaffiche le formulaire avec un message
            req.setAttribute("error", "Enregistrement impossible : vérifiez les champs (immatriculation unique, nombres valides, photo jpg/png/webp de 2 Mo maximum).");
            req.setAttribute("vehicule", v);
            req.getRequestDispatcher("/WEB-INF/views/vehicules/form.jsp").forward(req, resp);
        }
    }
}