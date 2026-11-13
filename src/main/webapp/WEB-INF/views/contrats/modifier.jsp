<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="active" value="contrats"/>
<%@ include file="/WEB-INF/jspf/header.jspf" %>

<div class="page-head">
    <div>
        <h1>Modifier le contrat #<c:out value="${contrat.id}"/></h1>
        <p class="muted">Mettez à jour les informations de la location.</p>
    </div>
    <a class="btn secondary" href="${pageContext.request.contextPath}/admin/contrats">Retour aux contrats</a>
</div>

<form method="post" action="${pageContext.request.contextPath}/admin/contrats/modifier" class="card max-w-2xl">
    <input type="hidden" name="contratId" value="${contrat.id}">
    <div class="mb-4 grid gap-4 sm:grid-cols-2">
        <label class="block text-sm font-medium text-slate-700">Client
            <input disabled value="<c:out value='${contrat.client.nomComplet}'/>" class="mt-1 w-full rounded-lg border border-slate-200 bg-slate-100 px-3 py-2 text-slate-500">
        </label>
        <label class="block text-sm font-medium text-slate-700">Véhicule
            <input disabled value="<c:out value='${contrat.vehicule.libelle}'/>" class="mt-1 w-full rounded-lg border border-slate-200 bg-slate-100 px-3 py-2 text-slate-500">
        </label>
        <label class="block text-sm font-medium text-slate-700">Date de début
            <input type="date" name="dateDebut" required value="${contrat.dateDebut}" class="mt-1 w-full rounded-lg border border-slate-300 px-3 py-2">
        </label>
        <label class="block text-sm font-medium text-slate-700">Date de fin
            <input type="date" name="dateFin" required value="${contrat.dateFin}" class="mt-1 w-full rounded-lg border border-slate-300 px-3 py-2">
        </label>
        <label class="block text-sm font-medium text-slate-700">Montant total (MAD)
            <input type="number" name="montantTotal" min="0" step="0.01" required value="${contrat.montantTotal}" class="mt-1 w-full rounded-lg border border-slate-300 px-3 py-2">
        </label>
        <label class="block text-sm font-medium text-slate-700">Kilométrage de départ
            <input type="number" name="kmDepart" min="0" value="${contrat.kmDepart}" class="mt-1 w-full rounded-lg border border-slate-300 px-3 py-2">
        </label>
        <label class="block text-sm font-medium text-slate-700">Kilométrage de retour
            <input type="number" name="kmRetour" min="0" value="${contrat.kmRetour}" class="mt-1 w-full rounded-lg border border-slate-300 px-3 py-2">
        </label>
    </div>
    <button class="btn" type="submit">Enregistrer les modifications</button>
</form>

<%@ include file="/WEB-INF/jspf/footer.jspf" %>
