<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<c:set var="active" value="reservations"/>
<%@ include file="/WEB-INF/jspf/header.jspf" %>

<div class="page-head">
    <div>
        <h1>Gestion des réservations</h1>
        <p class="muted">Validez les demandes clients et démarrez leurs contrats.</p>
    </div>
    <a class="btn secondary" href="${pageContext.request.contextPath}/accueil">Tableau de bord</a>
</div>

<table>
    <thead>
    <tr><th>N°</th><th>Client</th><th>Véhicule</th><th>Période</th><th>Total</th><th>Statut</th><th>Actions</th></tr>
    </thead>
    <tbody>
    <c:forEach var="reservation" items="${reservations}">
        <tr>
            <td>#${reservation.id}</td>
            <td><c:out value="${reservation.client.prenom} ${reservation.client.nom}"/></td>
            <td><c:out value="${reservation.vehicule.libelle}"/></td>
            <td>${reservation.dateDebut} → ${reservation.dateFin}</td>
            <td><fmt:formatNumber value="${reservation.montantTotal}" pattern="#,##0.00"/> MAD</td>
            <td><span class="badge info">${reservation.statut}</span></td>
            <td class="actions">
                <c:if test="${reservation.statut == 'EN_ATTENTE'}">
                    <form method="post" action="${pageContext.request.contextPath}/admin/reservations/valider">
                        <input type="hidden" name="reservationId" value="${reservation.id}">
                        <button class="btn small success" type="submit">Valider</button>
                    </form>
                </c:if>
                <c:if test="${reservation.statut == 'CONFIRMEE'}">
                    <form method="post" action="${pageContext.request.contextPath}/admin/contrats/demarrer">
                        <input type="hidden" name="reservationId" value="${reservation.id}">
                        <button class="btn small" type="submit">Démarrer contrat</button>
                    </form>
                </c:if>
                <form method="post" action="${pageContext.request.contextPath}/admin/reservations/supprimer"
                      onsubmit="return confirm('Supprimer cette réservation ?');">
                    <input type="hidden" name="reservationId" value="${reservation.id}">
                    <button class="btn small danger" type="submit">Supprimer</button>
                </form>
            </td>
        </tr>
    </c:forEach>
    <c:if test="${empty reservations}">
        <tr><td colspan="7" class="empty">Aucune réservation enregistrée.</td></tr>
    </c:if>
    </tbody>
</table>

<%@ include file="/WEB-INF/jspf/footer.jspf" %>
