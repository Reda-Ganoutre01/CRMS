package ma.location.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ma.location.dao.ClientDAO;

import java.io.IOException;

@WebServlet("/clients")
public class ClientServlet extends HttpServlet {

    private final ClientDAO clientDAO = new ClientDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setAttribute("clients", clientDAO.findAll());
        request.getRequestDispatcher("/WEB-INF/views/clients/list.jsp").forward(request, response);
    }
}
