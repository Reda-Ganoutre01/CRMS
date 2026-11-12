<%@ page contentType="text/html; charset=UTF-8" %>
<!DOCTYPE html>
<html lang="fr" class="h-full">
<head><meta charset="UTF-8"><meta name="viewport" content="width=device-width, initial-scale=1"><title>Facture | CRMS</title><script src="https://cdn.tailwindcss.com"></script><style>@media print {.no-print{display:none!important} body{background:white!important}.invoice{box-shadow:none!important;border:0!important}}</style></head>
<body class="min-h-full bg-slate-100 text-slate-900">
<main class="mx-auto max-w-3xl px-6 py-12">
<div class="invoice rounded-2xl border border-slate-200 bg-white p-8 shadow-xl sm:p-12">
<div class="flex items-start justify-between border-b border-slate-200 pb-8"><div><p class="text-sm font-bold uppercase tracking-[0.3em] text-sky-600">CRMS</p><h1 class="mt-3 text-3xl font-bold">Facture de location</h1></div><span class="rounded-full bg-emerald-100 px-3 py-1 text-xs font-bold uppercase text-emerald-700">${contrat.statut}</span></div>
<div class="grid gap-6 border-b border-slate-200 py-8 sm:grid-cols-2"><div><p class="text-xs font-semibold uppercase text-slate-500">Client</p><p class="mt-1 font-semibold">${contrat.client.prenom} ${contrat.client.nom}</p></div><div><p class="text-xs font-semibold uppercase text-slate-500">Véhicule</p><p class="mt-1 font-semibold">${contrat.vehicule.libelle}</p></div><div><p class="text-xs font-semibold uppercase text-slate-500">Période</p><p class="mt-1">${contrat.dateDebut} au ${contrat.dateFin}</p></div><div><p class="text-xs font-semibold uppercase text-slate-500">Kilométrage</p><p class="mt-1">${contrat.kmDepart} km → ${contrat.kmRetour} km</p></div></div>
<div class="flex items-end justify-between pt-8"><span class="text-slate-500">Jours facturés : ${contrat.nbJours}</span><div class="text-right"><p class="text-sm text-slate-500">Total</p><h2 class="text-3xl font-bold text-sky-600">${contrat.montantTotal}</h2></div></div>
</div>
<button type="button" onclick="window.print()" class="no-print mt-6 rounded-lg bg-slate-900 px-5 py-3 font-semibold text-white transition hover:bg-slate-700">Imprimer la facture</button>
</main>
</body>
</html>
