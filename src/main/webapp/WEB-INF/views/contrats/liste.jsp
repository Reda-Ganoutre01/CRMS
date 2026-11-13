<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<c:set var="active" value="contrats"/>
<%@ include file="/WEB-INF/jspf/header.jspf" %>

<div class="page-head">
    <div>
        <h1>Gestion des contrats</h1>
        <p class="muted">Suivez les locations actives et clôturez les retours.</p>
    </div>
    <a class="btn secondary" href="${pageContext.request.contextPath}/admin/reservations">Réservations</a>
</div>

<table>
    <thead>
    <tr><th>N°</th><th>Client</th><th>Véhicule</th><th>Dates</th><th>Km départ</th><th>Km retour</th><th>Statut</th><th>Actions</th></tr>
    </thead>
    <tbody>
    <c:forEach var="contrat" items="${derniersContrats}">
        <tr>
            <td>#${contrat.id}</td>
            <td><c:out value="${contrat.client.prenom} ${contrat.client.nom}"/></td>
            <td><c:out value="${contrat.vehicule.libelle}"/></td>
            <td>${contrat.dateDebut} → ${contrat.dateFin}</td>
            <td>${contrat.kmDepart}</td>
            <td>${contrat.kmRetour}</td>
            <td><span class="badge ${contrat.statut == 'ACTIF' ? 'info' : 'ok'}"><c:out value="${contrat.statut}"/></span></td>
            <td class="actions">
                <c:if test="${contrat.statut == 'ACTIF'}">
                    <form method="post" action="${pageContext.request.contextPath}/admin/contrats/cloturer" class="actions">
                        <input type="hidden" name="contratId" value="${contrat.id}">
                        <input type="number" name="kmRetour" min="${contrat.kmDepart}" placeholder="Km retour" required style="width:110px">
                        <button class="btn small success" type="submit">Clôturer</button>
                    </form>
                </c:if>
                <a class="btn small secondary" href="${pageContext.request.contextPath}/admin/contrats/facture?id=${contrat.id}">Facture</a>
                <form method="post" action="${pageContext.request.contextPath}/admin/contrats/supprimer"
                      onsubmit="return confirm('Supprimer ce contrat ? Cette action est irréversible.');">
                    <input type="hidden" name="contratId" value="${contrat.id}">
                    <button class="btn small danger" type="submit">Supprimer</button>
                </form>
            </td>
        </tr>
    </c:forEach>
    <c:if test="${empty derniersContrats}">
        <tr><td colspan="8" class="empty">Aucun contrat enregistré.</td></tr>
    </c:if>
    </tbody>
</table>

<%@ include file="/WEB-INF/jspf/footer.jspf" %>
