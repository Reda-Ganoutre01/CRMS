<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="active" value="vehicules"/>
<%@ include file="/WEB-INF/jspf/header.jspf" %>

<h1 class="mb-4 text-2xl font-bold text-slate-800">${empty vehicule.id ? 'Nouveau véhicule' : 'Modifier le véhicule'}</h1>

<form class="max-w-2xl rounded-xl bg-white p-6 shadow" method="post" enctype="multipart/form-data" action="${pageContext.request.contextPath}/vehicules">
    <input type="hidden" name="action" value="save">
    <input type="hidden" name="id" value="${vehicule.id}">

    <div class="grid gap-4 sm:grid-cols-2">
        <div class="mb-4">
            <label class="mb-1 block text-sm font-medium text-slate-700" for="immatriculation">Immatriculation</label>
            <input class="w-full rounded-lg border border-slate-300 bg-white px-3 py-2 text-sm focus:border-sky-500 focus:outline-none focus:ring-1 focus:ring-sky-500" id="immatriculation" name="immatriculation" required maxlength="20"
                   placeholder="12345-A-6" value="<c:out value='${vehicule.immatriculation}'/>">
        </div>
        <div class="mb-4">
            <label class="mb-1 block text-sm font-medium text-slate-700" for="carburant">Carburant</label>
            <select class="w-full rounded-lg border border-slate-300 bg-white px-3 py-2 text-sm focus:border-sky-500 focus:outline-none focus:ring-1 focus:ring-sky-500" id="carburant" name="carburant">
                <c:forEach var="carb" items="${['Diesel','Essence','Hybride','Électrique']}">
                    <option value="${carb}" ${vehicule.carburant == carb ? 'selected' : ''}>${carb}</option>
                </c:forEach>
            </select>
        </div>
    </div>

    <div class="grid gap-4 sm:grid-cols-2">
        <div class="mb-4">
            <label class="mb-1 block text-sm font-medium text-slate-700" for="marque">Marque</label>
            <input class="w-full rounded-lg border border-slate-300 bg-white px-3 py-2 text-sm focus:border-sky-500 focus:outline-none focus:ring-1 focus:ring-sky-500" id="marque" name="marque" required value="<c:out value='${vehicule.marque}'/>">
        </div>
        <div class="mb-4">
            <label class="mb-1 block text-sm font-medium text-slate-700" for="modele">Modèle</label>
            <input class="w-full rounded-lg border border-slate-300 bg-white px-3 py-2 text-sm focus:border-sky-500 focus:outline-none focus:ring-1 focus:ring-sky-500" id="modele" name="modele" required value="<c:out value='${vehicule.modele}'/>">
        </div>
    </div>

    <div class="grid gap-4 sm:grid-cols-2">
        <div class="mb-4">
            <label class="mb-1 block text-sm font-medium text-slate-700" for="annee">Année</label>
            <input class="w-full rounded-lg border border-slate-300 bg-white px-3 py-2 text-sm focus:border-sky-500 focus:outline-none focus:ring-1 focus:ring-sky-500" id="annee" name="annee" type="number" min="1990" max="2100" required
                   value="${vehicule.annee == 0 ? 2024 : vehicule.annee}">
        </div>
        <div class="mb-4">
            <label class="mb-1 block text-sm font-medium text-slate-700" for="kilometrage">Kilométrage</label>
            <input class="w-full rounded-lg border border-slate-300 bg-white px-3 py-2 text-sm focus:border-sky-500 focus:outline-none focus:ring-1 focus:ring-sky-500" id="kilometrage" name="kilometrage" type="number" min="0" required value="${vehicule.kilometrage}">
        </div>
    </div>

    <div class="mb-4">
        <label class="mb-1 block text-sm font-medium text-slate-700" for="prixJour">Prix par jour (MAD)</label>
        <input class="w-full rounded-lg border border-slate-300 bg-white px-3 py-2 text-sm focus:border-sky-500 focus:outline-none focus:ring-1 focus:ring-sky-500" id="prixJour" name="prixJour" type="number" min="0" step="0.01" required value="${vehicule.prixJour}">
    </div>

    <div class="mb-4">
        <label class="mb-1 block text-sm font-medium text-slate-700" for="image">Photo du véhicule (jpg, png ou webp, 2 Mo maximum)</label>
        <%-- Photo actuelle (en modification) --%>
        <c:if test="${not empty vehicule.image}">
            <img class="mb-2 h-32 rounded-lg object-cover"
                 src="${pageContext.request.contextPath}/images/${vehicule.image}"
                 alt="Photo actuelle"
                 onerror="this.onerror=null; this.src='${pageContext.request.contextPath}/images/car-default.svg';">
        </c:if>
        <c:if test="${empty vehicule.image}">
            <img class="mb-2 h-32 w-48 rounded-lg bg-slate-200 object-contain p-4"
                 src="${pageContext.request.contextPath}/images/car-default.svg" alt="Icône de voiture par défaut">
        </c:if>
        <input class="block w-full text-sm text-slate-600 file:mr-3 file:rounded-lg file:border-0 file:bg-sky-600 file:px-4 file:py-2 file:text-sm file:font-medium file:text-white hover:file:bg-sky-700"
               id="image" name="image" type="file" accept="image/png,image/jpeg,image/webp">
    </div>

    <div class="mt-2 flex gap-3">
        <button class="inline-flex items-center rounded-lg font-medium text-white transition px-4 py-2 text-sm bg-emerald-600 hover:bg-emerald-700" type="submit">Enregistrer</button>
        <a class="inline-flex items-center rounded-lg font-medium text-white transition px-4 py-2 text-sm bg-slate-500 hover:bg-slate-600" href="${pageContext.request.contextPath}/vehicules">Annuler</a>
    </div>
</form>

<%@ include file="/WEB-INF/jspf/footer.jspf" %>