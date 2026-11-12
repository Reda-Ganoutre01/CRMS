<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<c:set var="active" value="reservations"/>
<%@ include file="/WEB-INF/jspf/header.jspf" %>

<div class="page-head">
    <div>
        <h1>Mes réservations</h1>
        <p class="muted">Suivez l'état de vos demandes de location.</p>
    </div>
</div>

<c:if test="${param.created != null}">
    <div class="mb-4 rounded-lg bg-emerald-100 px-4 py-3 text-emerald-800">Réservation enregistrée.</div>
</c:if>

<table>
    <thead>
    <tr>
        <th>Véhicule</th>
        <th>Date de début</th>
        <th>Date de fin</th>
        <th>Montant</th>
        <th>Statut</th>
    </tr>
    </thead>
    <tbody>
    <c:forEach var="reservation" items="${reservations}">
        <tr>
            <td><c:out value="${reservation.vehicule.libelle}"/></td>
            <td><c:out value="${reservation.dateDebut}"/></td>
            <td><c:out value="${reservation.dateFin}"/></td>
            <td><fmt:formatNumber value="${reservation.montantTotal}" pattern="#,##0.00"/> MAD</td>
            <td><span class="badge info"><c:out value="${reservation.statut}"/></span></td>
        </tr>
    </c:forEach>
    <c:if test="${empty reservations}">
        <tr><td colspan="5" class="empty">Vous n'avez encore aucune réservation.</td></tr>
    </c:if>
    </tbody>
</table>

<c:if test="${empty reservations}">
    <div class="mt-4 text-center">
        <a class="btn" href="${pageContext.request.contextPath}/vehicules">Choisir un véhicule</a>
    </div>
</c:if>

<%@ include file="/WEB-INF/jspf/footer.jspf" %>
