<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<c:set var="active" value="accueil"/>
<%@ include file="/WEB-INF/jspf/header.jspf" %>

<h1>Tableau de bord</h1>

<div class="stats">
    <div class="stat">
        <div class="value">${nbVehicules}</div>
        <div class="label">Véhicules au total</div>
    </div>
    <div class="stat green">
        <div class="value">${nbDisponibles}</div>
        <div class="label">Véhicules disponibles</div>
    </div>
    <div class="stat orange">
        <div class="value">${nbClients}</div>
        <div class="label">Clients</div>
    </div>
    <div class="stat violet">
        <div class="value">${nbContratsEnCours}</div>
        <div class="label">Contrats en cours</div>
    </div>
    <div class="stat green">
        <div class="value"><fmt:formatNumber value="${chiffreAffaires}" pattern="#,##0"/> MAD</div>
        <div class="label">Chiffre d'affaires</div>
    </div>
</div>

<div class="page-head">
    <h2>Derniers contrats</h2>
    <a class="btn" href="${pageContext.request.contextPath}/contrats?action=new">+ Nouveau contrat</a>
</div>

<table>
    <thead>
    <tr><th>N°</th><th>Client</th><th>Véhicule</th><th>Période</th><th>Montant</th><th>Statut</th></tr>
    </thead>
    <tbody>
    <c:forEach var="c" items="${derniersContrats}">
        <tr>
            <td>#${c.id}</td>
            <td><c:out value="${c.client.nomComplet}"/></td>
            <td><c:out value="${c.vehicule.libelle}"/></td>
            <td>${c.dateDebut} → ${c.dateFin}</td>
            <td><fmt:formatNumber value="${c.montantTotal}" pattern="#,##0.00"/> MAD</td>
            <td>
                <span class="badge ${c.statut == 'ACTIF' ? 'info' : 'ok'}">${c.statut}</span>
            </td>
        </tr>
    </c:forEach>
    <c:if test="${empty derniersContrats}">
        <tr><td colspan="6" class="empty">Aucun contrat pour le moment.</td></tr>
    </c:if>
    </tbody>
</table>

<%@ include file="/WEB-INF/jspf/footer.jspf" %>