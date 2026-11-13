package ma.location.filter;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.persistence.EntityManager;
import jakarta.servlet.http.HttpSession;
import ma.location.dao.AuthDao;
import ma.location.service.ClientService;
import ma.location.util.JPAUtil;
import io.jsonwebtoken.Claims;

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

        syncSessionFromJwt(httpRequest);
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

    private void syncSessionFromJwt(HttpServletRequest request) {
        if (request.getCookies() == null) return;
        String token = null;
        for (jakarta.servlet.http.Cookie cookie : request.getCookies()) {
            if ("JWT".equals(cookie.getName())) {
                token = cookie.getValue();
                break;
            }
        }
        if (token == null) return;

        EntityManager em = JPAUtil.getEntityManager();
        try {
            AuthDao dao = new AuthDao();
            dao.setEntityManager(em);
            Claims claims = dao.parseToken(token);
            Long userId = Long.valueOf(claims.getSubject());
            String role = claims.get("role", String.class);
            if (dao.findById(userId) == null || role == null) {
                invalidateSession(request);
                return;
            }
            HttpSession session = request.getSession(true);
            session.setAttribute(ClientService.USER_ID_ATTRIBUTE, userId);
            session.setAttribute(ClientService.ROLE_ATTRIBUTE, role);
        } catch (RuntimeException exception) {
            invalidateSession(request);
        } finally {
            em.close();
        }
    }

    private void invalidateSession(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) session.invalidate();
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
