package ma.location.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import ma.location.model.Role;
import ma.location.model.Utilisateur;

public class ClientService {

    public static final String USER_ID_ATTRIBUTE = "authenticatedUserId";
    public static final String ROLE_ATTRIBUTE = "authenticatedRole";

    public void openSession(HttpServletRequest request, Utilisateur utilisateur) {
        if (utilisateur == null || utilisateur.getId() == null || utilisateur.getRole() == null) {
            throw new IllegalArgumentException("Utilisateur authentifié requis.");
        }
        HttpSession session = request.getSession(true);
        session.setAttribute(USER_ID_ATTRIBUTE, utilisateur.getId());
        session.setAttribute(ROLE_ATTRIBUTE, utilisateur.getRole().name());
    }

    public void closeSession(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) session.invalidate();
    }

    public Long currentUserId(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        Object value = session == null ? null : session.getAttribute(USER_ID_ATTRIBUTE);
        if (value instanceof Number) return ((Number) value).longValue();
        return null;
    }

    public boolean isAuthenticated(HttpServletRequest request) {
        return currentUserId(request) != null;
    }

    public boolean hasRole(HttpServletRequest request, Role role) {
        if (role == null) return false;
        HttpSession session = request.getSession(false);
        if (session == null) return false;
        Object value = session.getAttribute(ROLE_ATTRIBUTE);
        if (value instanceof Role) return role == value;
        return value instanceof String && role.name().equalsIgnoreCase((String) value);
    }

    public void requireClient(HttpServletRequest request) {
        if (!hasRole(request, Role.USER)) {
            throw new SecurityException("Accès réservé à un client authentifié.");
        }
    }

    public void requireAdmin(HttpServletRequest request) {
        if (!hasRole(request, Role.ADMIN)) {
            throw new SecurityException("Accès réservé à un administrateur authentifié.");
        }
    }
}
