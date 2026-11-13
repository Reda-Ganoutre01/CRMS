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
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@WebFilter("/*")
public class AuthenticationFilter implements Filter {

    private final ClientService clientService = new ClientService();

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        String path = httpRequest.getRequestURI().substring(httpRequest.getContextPath().length());

        if (isPublic(path) || clientService.isAuthenticated(httpRequest)) {
            chain.doFilter(request, response);
            return;
        }

        String returnPath = path;
        if (httpRequest.getQueryString() != null) {
            returnPath += "?" + httpRequest.getQueryString();
        }
        httpResponse.sendRedirect(httpRequest.getContextPath() + "/login?redirect=" +
                URLEncoder.encode(returnPath, StandardCharsets.UTF_8));
    }

    private boolean isPublic(String path) {
        return path.isEmpty()
                || "/".equals(path)
                || "/index.jsp".equals(path)
                || "/accueil".equals(path)
                || "/vehicules".equals(path)
                || "/login".equals(path)
                || "/register".equals(path)
                || "/logout".equals(path)
                || path.startsWith("/css/")
                || path.startsWith("/images/")
                || path.startsWith("/api/");
    }
}
