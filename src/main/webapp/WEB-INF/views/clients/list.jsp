<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="active" value="clients"/>
<%@ include file="/WEB-INF/jspf/header.jspf" %>

<div class="page-head">
    <div>
        <h1>Clients</h1>
        <p class="muted">Liste des clients enregistrés.</p>
    </div>
</div>

<c:if test="${param.deleted == 'true'}">
    <div class="alert success">Le compte client a été anonymisé. Les contrats et réservations ont été conservés.</div>
</c:if>
<c:if test="${not empty param.error}">
    <div class="alert error"><c:out value="${param.error}"/></div>
</c:if>

<table>
    <thead>
    <tr>
        <th>Nom complet</th>
        <th>CIN</th>
        <th>Email</th>
        <th>Téléphone</th>
        <th>Adresse</th>
        <th>Permis</th>
        <th>Actions</th>
    </tr>
    </thead>
    <tbody>
    <c:forEach var="client" items="${clients}">
        <tr>
            <td><c:out value="${client.prenom} ${client.nom}"/></td>
            <td><c:out value="${client.cin}"/></td>
            <td><c:out value="${client.email}"/></td>
            <td><c:out value="${client.telephone}"/></td>
            <td><c:out value="${client.adresse}"/></td>
            <td><c:out value="${client.numeroPermis}"/></td>
            <td class="actions">
                <form method="post" action="${pageContext.request.contextPath}/clients/delete"
                      onsubmit="return confirm('Anonymiser ce compte ? Les contrats et réservations seront conservés.');">
                    <input type="hidden" name="clientId" value="${client.id}">
                    <button class="btn small danger" type="submit">Supprimer</button>
                </form>
            </td>
        </tr>
    </c:forEach>
    <c:if test="${empty clients}">
        <tr><td colspan="7" class="empty">Aucun client enregistré.</td></tr>
    </c:if>
    </tbody>
</table>

<%@ include file="/WEB-INF/jspf/footer.jspf" %>
