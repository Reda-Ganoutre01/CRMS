<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="active" value="vehicules"/>
<%@ include file="/WEB-INF/jspf/header.jspf" %>

<h1>${empty vehicule.id ? 'Nouveau véhicule' : 'Modifier le véhicule'}</h1>

<form class="card form-card" method="post" action="${pageContext.request.contextPath}/vehicules">
    <input type="hidden" name="action" value="save">
    <input type="hidden" name="id" value="${vehicule.id}">

    <div class="form-row">
        <div class="field">
            <label for="immatriculation">Immatriculation</label>
            <input id="immatriculation" name="immatriculation" required maxlength="20"
                   placeholder="12345-A-6" value="<c:out value='${vehicule.immatriculation}'/>">
        </div>
        <div class="field">
            <label for="carburant">Carburant</label>
            <select id="carburant" name="carburant">
                <c:forEach var="carb" items="${['Diesel','Essence','Hybride','Électrique']}">
                    <option value="${carb}" ${vehicule.carburant == carb ? 'selected' : ''}>${carb}</option>
                </c:forEach>
            </select>
        </div>
    </div>

    <div class="form-row">
        <div class="field">
            <label for="marque">Marque</label>
            <input id="marque" name="marque" required value="<c:out value='${vehicule.marque}'/>">
        </div>
        <div class="field">
            <label for="modele">Modèle</label>
            <input id="modele" name="modele" required value="<c:out value='${vehicule.modele}'/>">
        </div>
    </div>

    <div class="form-row">
        <div class="field">
            <label for="annee">Année</label>
            <input id="annee" name="annee" type="number" min="1990" max="2100" required
                   value="${vehicule.annee == 0 ? 2024 : vehicule.annee}">
        </div>
        <div class="field">
            <label for="kilometrage">Kilométrage</label>
            <input id="kilometrage" name="kilometrage" type="number" min="0" required value="${vehicule.kilometrage}">
        </div>
    </div>

    <div class="field">
        <label for="prixJour">Prix par jour (MAD)</label>
        <input id="prixJour" name="prixJour" type="number" min="0" step="0.01" required value="${vehicule.prixJour}">
    </div>

    <div class="form-actions">
        <button class="btn success" type="submit">Enregistrer</button>
        <a class="btn secondary" href="${pageContext.request.contextPath}/vehicules">Annuler</a>
    </div>
</form>

<%@ include file="/WEB-INF/jspf/footer.jspf" %>