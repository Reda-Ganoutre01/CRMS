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

<table>
    <thead>
    <tr>
        <th>Nom complet</th>
        <th>CIN</th>
        <th>Email</th>
        <th>Téléphone</th>
        <th>Adresse</th>
        <th>Permis</th>
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
        </tr>
    </c:forEach>
    <c:if test="${empty clients}">
        <tr><td colspan="6" class="empty">Aucun client enregistré.</td></tr>
    </c:if>
    </tbody>
</table>

<%@ include file="/WEB-INF/jspf/footer.jspf" %>
