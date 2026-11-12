<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="fr" class="h-full">
<head>
    <meta charset="UTF-8"><meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Inscription | CRMS</title><script src="https://cdn.tailwindcss.com"></script>
</head>
<body class="min-h-full bg-slate-100 text-slate-800">
<main class="flex min-h-screen items-center justify-center px-6 py-12">
    <section class="w-full max-w-2xl">
        <div class="mb-8 text-center"><p class="mb-3 text-sm font-semibold uppercase tracking-[0.3em] text-sky-600">CRMS</p><h1 class="text-3xl font-bold">Créer un compte</h1><p class="mt-2 text-slate-500">Rejoignez votre espace client.</p></div>
        <div class="rounded-2xl border border-slate-200 bg-white p-8 shadow-xl">
            <c:if test="${not empty param.error}"><p class="mb-5 rounded-lg border border-red-900/60 bg-red-950/50 px-4 py-3 text-sm text-red-300">${param.error}</p></c:if>
            <form method="post" action="${pageContext.request.contextPath}/register" class="grid gap-5 sm:grid-cols-2">
                <label class="block text-sm font-medium text-slate-700">Nom<input name="nom" required class="mt-2 w-full rounded-lg border border-slate-300 bg-white px-4 py-3 text-slate-800 outline-none focus:border-sky-500 focus:ring-2 focus:ring-sky-500/30"></label>
                <label class="block text-sm font-medium text-slate-700">Prénom<input name="prenom" required class="mt-2 w-full rounded-lg border border-slate-300 bg-white px-4 py-3 text-slate-800 outline-none focus:border-sky-500 focus:ring-2 focus:ring-sky-500/30"></label>
                <label class="block text-sm font-medium text-slate-700">CIN<input name="cin" required class="mt-2 w-full rounded-lg border border-slate-300 bg-white px-4 py-3 text-slate-800 outline-none focus:border-sky-500 focus:ring-2 focus:ring-sky-500/30"></label>
                <label class="block text-sm font-medium text-slate-700">Email<input type="email" name="email" required class="mt-2 w-full rounded-lg border border-slate-300 bg-white px-4 py-3 text-slate-800 outline-none focus:border-sky-500 focus:ring-2 focus:ring-sky-500/30"></label>
                <label class="block text-sm font-medium text-slate-700">Téléphone<input name="telephone" class="mt-2 w-full rounded-lg border border-slate-300 bg-white px-4 py-3 text-slate-800 outline-none focus:border-sky-500 focus:ring-2 focus:ring-sky-500/30"></label>
                <label class="block text-sm font-medium text-slate-700">Adresse<input name="adresse" class="mt-2 w-full rounded-lg border border-slate-300 bg-white px-4 py-3 text-slate-800 outline-none focus:border-sky-500 focus:ring-2 focus:ring-sky-500/30"></label>
                <label class="block text-sm font-medium text-slate-700">Numéro de permis<input name="numeroPermis" class="mt-2 w-full rounded-lg border border-slate-300 bg-white px-4 py-3 text-slate-800 outline-none focus:border-sky-500 focus:ring-2 focus:ring-sky-500/30"></label>
                <label class="block text-sm font-medium text-slate-700">Mot de passe
                    <input type="password" name="motDePasse" minlength="8" required class="mt-2 w-full rounded-lg border border-slate-300 bg-white px-4 py-3 text-slate-800 outline-none focus:border-sky-500 focus:ring-2 focus:ring-sky-500/30">
                </label>
                <div class="flex items-end sm:col-span-2"><button type="submit" class="w-full rounded-lg bg-sky-600 px-4 py-3 font-semibold text-white transition hover:bg-sky-700">S'inscrire</button></div>
            </form>
            <p class="mt-6 text-center text-sm text-slate-500">Déjà inscrit ?
                <a href="${pageContext.request.contextPath}/login" class="font-semibold text-sky-600 hover:text-sky-700">Se connecter</a>
            </p>
        </div>
    </section>
</main>
</body>
</html>
