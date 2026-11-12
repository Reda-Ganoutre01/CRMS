<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="fr" class="h-full">
<head><meta charset="UTF-8"><meta name="viewport" content="width=device-width, initial-scale=1"><title>Nouvelle réservation | CRMS</title><script src="https://cdn.tailwindcss.com"></script></head>
<body class="min-h-full bg-slate-950 text-slate-100">
<main class="mx-auto max-w-3xl px-6 py-12">
    <a href="${pageContext.request.contextPath}/accueil" class="text-sm font-medium text-sky-400 hover:text-sky-300">← Retour au tableau de bord</a>
    <div class="mt-8 rounded-2xl border border-slate-800 bg-slate-900 p-8 shadow-2xl shadow-black/20">
        <div class="mb-8"><p class="text-sm font-semibold uppercase tracking-widest text-sky-400">Réservation</p><h1 class="mt-2 text-3xl font-bold">Réserver ${vehicule.libelle}</h1><p class="mt-2 text-slate-400">Choisissez votre période de location.</p></div>
        <c:if test="${not empty param.error or not empty requestScope.error}">
            <p class="mb-6 rounded-lg border border-red-900/60 bg-red-950/50 px-4 py-3 text-sm text-red-300">
                <c:out value="${not empty requestScope.error ? requestScope.error : param.error}"/>
            </p>
        </c:if>
        <form method="post" action="${pageContext.request.contextPath}/reservations/creer" class="space-y-6">
            <input type="hidden" name="vehiculeId" value="${vehicule.id}">
            <div class="grid gap-5 sm:grid-cols-2">
                <label class="block text-sm font-medium text-slate-300">Date de début<input type="date" name="dateDebut" value="${param.dateDebut}" required class="mt-2 w-full rounded-lg border border-slate-700 bg-slate-800 px-4 py-3 text-white outline-none focus:border-sky-500 focus:ring-2 focus:ring-sky-500/30"></label>
                <label class="block text-sm font-medium text-slate-300">Date de fin<input type="date" name="dateFin" value="${param.dateFin}" required class="mt-2 w-full rounded-lg border border-slate-700 bg-slate-800 px-4 py-3 text-white outline-none focus:border-sky-500 focus:ring-2 focus:ring-sky-500/30"></label>
            </div>
            <label class="block text-sm font-medium text-slate-300">Offre promotionnelle
                <select name="offreId" class="mt-2 w-full rounded-lg border border-slate-700 bg-slate-800 px-4 py-3 text-white outline-none focus:border-sky-500 focus:ring-2 focus:ring-sky-500/30">
                    <option value="">Aucune offre</option><c:forEach var="offre" items="${offres}"><option value="${offre.id}" ${param.offreId == offre.id ? 'selected' : ''}>${offre.titre} (-${offre.pourcentageRemise}%)</option></c:forEach>
                </select>
            </label>
            <button type="submit" class="w-full rounded-lg bg-sky-500 px-4 py-3 font-semibold text-slate-950 transition hover:bg-sky-400 sm:w-auto sm:px-8">Envoyer la réservation</button>
        </form>
    </div>
</main>
</body>
</html>
