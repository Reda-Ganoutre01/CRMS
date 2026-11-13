package ma.location.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ma.location.dao.ClientDAO;
import ma.location.model.Client;
import ma.location.model.Role;
import ma.location.service.ClientService;

import java.io.IOException;
import java.util.UUID;

@WebServlet({"/clients", "/clients/delete"})
public class ClientServlet extends HttpServlet {

    private final ClientDAO clientDAO = new ClientDAO();
    private final ClientService clientService = new ClientService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (!clientService.hasRole(request, Role.ADMIN)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN,
                    "Accès réservé aux administrateurs.");
            return;
        }
        request.setAttribute("clients", clientDAO.findAll());
        request.getRequestDispatcher("/WEB-INF/views/clients/list.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        if (!clientService.hasRole(request, Role.ADMIN)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN,
                    "Accès réservé aux administrateurs.");
            return;
        }

        try {
            Long clientId = Long.valueOf(request.getParameter("clientId"));
            Client client = clientDAO.findById(clientId);
            if (client == null) {
                throw new IllegalArgumentException("Client introuvable.");
            }

            String token = UUID.randomUUID().toString().replace("-", "");
            client.setNom("Compte");
            client.setPrenom("Supprimé");
            client.setEmail("deleted-" + token + "@deleted.local");
            client.setMotDePasse(token);
            client.setCin("DELETED-" + token.substring(0, 12));
            client.setTelephone(null);
            client.setAdresse(null);
            client.setNumeroPermis(null);
            client.setRole(Role.USER);
            clientDAO.save(client);
            response.sendRedirect(request.getContextPath() + "/clients?deleted=true");
        } catch (RuntimeException exception) {
            response.sendRedirect(request.getContextPath() + "/clients?error=" +
                    java.net.URLEncoder.encode(exception.getMessage(), java.nio.charset.StandardCharsets.UTF_8));
        }
    }
}
