package ma.location.servlet;

import ma.location.dao.ClientDAO;
import ma.location.dao.ContratDAO;
import ma.location.dao.VehiculeDAO;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/accueil")
public class HomeServlet extends HttpServlet {

    private VehiculeDAO vehiculeDAO = new VehiculeDAO();
    private ClientDAO clientDAO = new ClientDAO();
    private ContratDAO contratDAO = new ContratDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setAttribute("nbVehicules", vehiculeDAO.count());
        req.setAttribute("nbDisponibles", vehiculeDAO.countDisponibles());
        req.setAttribute("nbClients", clientDAO.count());
        req.setAttribute("nbContratsEnCours", contratDAO.countEnCours());
        req.setAttribute("chiffreAffaires", contratDAO.chiffreAffaires());
        req.setAttribute("derniersContrats", contratDAO.findRecent(5));
        req.getRequestDispatcher("/WEB-INF/views/dashboard.jsp").forward(req, resp);
    }
}