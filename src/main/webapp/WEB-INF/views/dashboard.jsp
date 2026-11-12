<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<c:set var="active" value="accueil"/>
<%@ include file="/WEB-INF/jspf/header.jspf" %>

<h1 class="mb-4 text-2xl font-bold text-slate-800">Tableau de bord</h1>

<div class="mb-6 grid gap-4 sm:grid-cols-2 lg:grid-cols-5">
    <div class="rounded-xl border-l-4 border-sky-400 bg-white p-4 shadow">
        <div class="text-2xl font-bold">${nbVehicules}</div>
        <div class="text-sm text-slate-500">Véhicules au total</div>
    </div>
    <div class="rounded-xl border-l-4 border-emerald-500 bg-white p-4 shadow">
        <div class="text-2xl font-bold">${nbDisponibles}</div>
        <div class="text-sm text-slate-500">Véhicules disponibles</div>
    </div>
    <div class="rounded-xl border-l-4 border-amber-500 bg-white p-4 shadow">
        <div class="text-2xl font-bold">${nbClients}</div>
        <div class="text-sm text-slate-500">Clients</div>
    </div>
    <div class="rounded-xl border-l-4 border-violet-500 bg-white p-4 shadow">
        <div class="text-2xl font-bold">${nbContratsEnCours}</div>
        <div class="text-sm text-slate-500">Contrats en cours</div>
    </div>
    <div class="rounded-xl border-l-4 border-emerald-500 bg-white p-4 shadow">
        <div class="text-2xl font-bold"><fmt:formatNumber value="${chiffreAffaires}" pattern="#,##0"/> MAD</div>
        <div class="text-sm text-slate-500">Chiffre d'affaires</div>
    </div>
</div>

<div class="mb-4 flex flex-wrap items-center justify-between gap-3">
    <h2 class="text-xl font-bold text-slate-800">Derniers contrats</h2>
    <a class="inline-flex items-center rounded-lg font-medium text-white transition px-4 py-2 text-sm bg-sky-600 hover:bg-sky-700" href="${pageContext.request.contextPath}/admin/contrats">Voir tous les contrats</a>
</div>

<div class="overflow-x-auto rounded-xl bg-white shadow">
<table class="min-w-full text-sm">
    <thead class="bg-slate-100 text-left text-slate-600">
    <tr><th class="px-4 py-3 font-semibold">N°</th><th class="px-4 py-3 font-semibold">Client</th><th class="px-4 py-3 font-semibold">Véhicule</th><th class="px-4 py-3 font-semibold">Période</th><th class="px-4 py-3 font-semibold">Montant</th><th class="px-4 py-3 font-semibold">Statut</th></tr>
    </thead>
    <tbody>
    <c:forEach var="c" items="${derniersContrats}">
        <tr class="border-t">
            <td class="px-4 py-3">#${c.id}</td>
            <td class="px-4 py-3"><c:out value="${c.client.nomComplet}"/></td>
            <td class="px-4 py-3"><c:out value="${c.vehicule.libelle}"/></td>
            <td class="px-4 py-3">${c.dateDebut} → ${c.dateFin}</td>
            <td class="px-4 py-3"><fmt:formatNumber value="${c.montantTotal}" pattern="#,##0.00"/> MAD</td>
            <td class="px-4 py-3">
                <span class="rounded-full px-2.5 py-1 text-xs font-semibold ${c.statut.name() == 'ACTIF' ? 'bg-sky-100 text-sky-700' : 'bg-emerald-100 text-emerald-700'}">${c.statut}</span>
            </td>
        </tr>
    </c:forEach>
    <c:if test="${empty derniersContrats}">
        <tr class="border-t"><td colspan="6" class="px-4 py-8 text-center text-slate-400">Aucun contrat pour le moment.</td></tr>
    </c:if>
    </tbody>
</table>
</div>

<%@ include file="/WEB-INF/jspf/footer.jspf" %>