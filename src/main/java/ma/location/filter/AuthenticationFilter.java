package ma.location.filter;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ma.location.service.ClientService;

import java.io.IOException;

@WebFilter("/*")
public class AuthenticationFilter implements Filter {

    private final ClientService clientService = new ClientService();

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        String path = httpRequest.getRequestURI().substring(httpRequest.getContextPath().length());

        if (isPublicPath(path) || clientService.isAuthenticated(httpRequest)) {
            chain.doFilter(request, response);
            return;
        }

        if (path.startsWith("/api/")) {
            httpResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Authentification requise.");
            return;
        }

        httpResponse.sendRedirect(httpRequest.getContextPath() + "/login");
    }

    private boolean isPublicPath(String path) {
        return path.isEmpty()
                || "/".equals(path)
                || "/index.jsp".equals(path)
                || "/login".equals(path)
                || "/register".equals(path)
                || "/logout".equals(path)
                || path.startsWith("/api/auth/")
                || path.startsWith("/css/")
                || path.startsWith("/js/")
                || path.startsWith("/images/")
                || path.startsWith("/favicon");
    }
}
