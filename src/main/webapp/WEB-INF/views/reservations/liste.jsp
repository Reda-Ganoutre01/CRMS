<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="fr" class="h-full">
<head><meta charset="UTF-8"><meta name="viewport" content="width=device-width, initial-scale=1"><title>Mes réservations | CRMS</title><script src="https://cdn.tailwindcss.com"></script></head>
<body class="min-h-full bg-slate-950 text-slate-100">
<main class="mx-auto max-w-6xl px-6 py-12">
    <div class="mb-8 flex flex-col gap-4 sm:flex-row sm:items-end sm:justify-between">
        <div><p class="text-sm font-semibold uppercase tracking-widest text-sky-400">Espace client</p><h1 class="mt-2 text-3xl font-bold">Mes réservations</h1><p class="mt-2 text-slate-400">Suivez l'état de vos demandes de location.</p></div>
        <div class="flex gap-4">
            <a href="${pageContext.request.contextPath}/vehicules" class="text-sm font-medium text-sky-400 hover:text-sky-300">Voir les véhicules</a>
            <a href="${pageContext.request.contextPath}/logout" class="text-sm font-medium text-slate-400 hover:text-white">Déconnexion</a>
        </div>
    </div>
    <c:if test="${param.created != null}"><p class="mb-6 rounded-lg border border-emerald-900/60 bg-emerald-950/50 px-4 py-3 text-sm text-emerald-300">Réservation enregistrée.</p></c:if>
    <div class="overflow-hidden rounded-2xl border border-slate-800 bg-slate-900 shadow-2xl shadow-black/20">
    <div class="overflow-x-auto"><table class="w-full text-left text-sm">
    <thead class="bg-slate-800/80 text-xs uppercase tracking-wider text-slate-400"><tr><th class="px-6 py-4">Véhicule</th><th class="px-6 py-4">Début</th><th class="px-6 py-4">Fin</th><th class="px-6 py-4">Montant</th><th class="px-6 py-4">Statut</th></tr></thead>
    <tbody class="divide-y divide-slate-800">
    <c:forEach var="reservation" items="${reservations}">
        <tr class="transition hover:bg-slate-800/50">
            <td class="whitespace-nowrap px-6 py-4 font-medium text-white">${reservation.vehicule.libelle}</td><td class="px-6 py-4 text-slate-300">${reservation.dateDebut}</td><td class="px-6 py-4 text-slate-300">${reservation.dateFin}</td><td class="px-6 py-4 font-semibold text-sky-300">${reservation.montantTotal}</td><td class="px-6 py-4"><span class="rounded-full bg-sky-950 px-3 py-1 text-xs font-semibold text-sky-300">${reservation.statut}</span></td>
        </tr>
    </c:forEach>
</tbody></table></div></div>
    <c:if test="${empty reservations}">
        <div class="mt-6 rounded-xl border border-slate-800 bg-slate-900 px-6 py-8 text-center">
            <p class="text-slate-300">Vous n'avez encore aucune réservation.</p>
            <a href="${pageContext.request.contextPath}/vehicules" class="mt-4 inline-block rounded-lg bg-sky-500 px-5 py-3 font-semibold text-slate-950 hover:bg-sky-400">Choisir un véhicule</a>
        </div>
    </c:if>
</main>
</body>
</html>
