package ma.location.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ma.location.dao.ClientDAO;
import ma.location.service.ClientService;

import java.io.IOException;

@WebServlet({"/clients", "/clients/*"})
public class ClientServlet extends HttpServlet {

    private final ClientDAO clientDAO = new ClientDAO();
    private final ClientService clientService = new ClientService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (!requireAdmin(request, response)) return;
        request.setAttribute("clients", clientDAO.findAll());
        request.getRequestDispatcher("/WEB-INF/views/clients/list.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        if (!requireAdmin(request, response)) return;
        if (!"/supprimer".equals(request.getPathInfo())) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        try {
            clientDAO.delete(Long.valueOf(request.getParameter("clientId")));
            request.getSession().setAttribute("message", "Client supprimé.");
        } catch (RuntimeException exception) {
            request.getSession().setAttribute("error",
                    "Suppression impossible : ce client est lié à des réservations ou des contrats.");
        }
        response.sendRedirect(request.getContextPath() + "/clients");
    }

    private boolean requireAdmin(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            clientService.requireAdmin(request);
            return true;
        } catch (SecurityException exception) {
            response.sendRedirect(request.getContextPath() + "/login");
            return false;
        }
    }
}
