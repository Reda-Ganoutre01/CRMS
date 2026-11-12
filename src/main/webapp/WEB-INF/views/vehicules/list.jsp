<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<c:set var="active" value="vehicules"/>
<%@ include file="/WEB-INF/jspf/header.jspf" %>

<div class="page-head">
    <h1>Véhicules</h1>
    <a class="btn" href="${pageContext.request.contextPath}/vehicules?action=new">+ Ajouter un véhicule</a>
</div>

<table>
    <thead>
    <tr>
        <th>Immatriculation</th><th>Marque / Modèle</th><th>Année</th><th>Carburant</th>
        <th>Km</th><th>Prix / jour</th><th>État</th><th>Actions</th>
    </tr>
    </thead>
    <tbody>
    <c:forEach var="v" items="${vehicules}">
        <tr>
            <td><c:out value="${v.immatriculation}"/></td>
            <td><c:out value="${v.marque}"/> <c:out value="${v.modele}"/></td>
            <td>${v.annee}</td>
            <td><c:out value="${v.carburant}"/></td>
            <td><fmt:formatNumber value="${v.kilometrage}" pattern="#,##0"/></td>
            <td><fmt:formatNumber value="${v.prixJour}" pattern="#,##0.00"/> MAD</td>
            <td>
                <span class="badge ${v.disponible ? 'ok' : 'ko'}">${v.disponible ? 'Disponible' : 'Loué'}</span>
            </td>
            <td class="actions">
                <a class="btn small secondary" href="${pageContext.request.contextPath}/vehicules?action=edit&id=${v.id}">Modifier</a>
                <form method="post" action="${pageContext.request.contextPath}/vehicules"
                      onsubmit="return confirm('Supprimer ce véhicule ?');">
                    <input type="hidden" name="action" value="delete">
                    <input type="hidden" name="id" value="${v.id}">
                    <button class="btn small danger" type="submit">Supprimer</button>
                </form>
            </td>
        </tr>
    </c:forEach>
    <c:if test="${empty vehicules}">
        <tr><td colspan="8" class="empty">Aucun véhicule enregistré.</td></tr>
    </c:if>
    </tbody>
</table>

<%@ include file="/WEB-INF/jspf/footer.jspf" %>