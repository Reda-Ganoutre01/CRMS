<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<c:set var="active" value="contrats"/>
<%@ include file="/WEB-INF/jspf/header.jspf" %>

<div class="page-head">
    <div>
        <h1>Contrats</h1>
        <p class="muted">Historique des contrats de location.</p>
    </div>
</div>

<table>
    <thead>
    <tr><th>N°</th><th>Client</th><th>Véhicule</th><th>Période</th><th>Montant</th><th>Statut</th><th></th></tr>
    </thead>
    <tbody>
    <c:forEach var="contrat" items="${derniersContrats}">
        <tr>
            <td>#${contrat.id}</td>
            <td><c:out value="${contrat.client.prenom} ${contrat.client.nom}"/></td>
            <td><c:out value="${contrat.vehicule.libelle}"/></td>
            <td>${contrat.dateDebut} → ${contrat.dateFin}</td>
            <td><fmt:formatNumber value="${contrat.montantTotal}" pattern="#,##0.00"/> MAD</td>
            <td><span class="badge info">${contrat.statut}</span></td>
            <td><a class="btn small" href="${pageContext.request.contextPath}/admin/contrats/facture?id=${contrat.id}">Facture</a></td>
        </tr>
    </c:forEach>
    <c:if test="${empty derniersContrats}">
        <tr><td colspan="7" class="empty">Aucun contrat pour le moment.</td></tr>
    </c:if>
    </tbody>
</table>

<%@ include file="/WEB-INF/jspf/footer.jspf" %>
