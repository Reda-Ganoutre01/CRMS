<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="fr" class="h-full">
<head>
    <meta charset="UTF-8"><meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Inscription | CRMS</title><script src="https://cdn.tailwindcss.com"></script>
</head>
<body class="min-h-full bg-slate-950 text-slate-100">
<main class="flex min-h-screen items-center justify-center px-6 py-12">
    <section class="w-full max-w-2xl">
        <div class="mb-8 text-center"><p class="mb-3 text-sm font-semibold uppercase tracking-[0.3em] text-sky-400">CRMS</p><h1 class="text-3xl font-bold">Créer un compte</h1><p class="mt-2 text-slate-400">Rejoignez votre espace client.</p></div>
        <div class="rounded-2xl border border-slate-800 bg-slate-900 p-8 shadow-2xl shadow-black/30">
            <c:if test="${not empty param.error}"><p class="mb-5 rounded-lg border border-red-900/60 bg-red-950/50 px-4 py-3 text-sm text-red-300">${param.error}</p></c:if>
            <form method="post" action="${pageContext.request.contextPath}/register" class="grid gap-5 sm:grid-cols-2">
                <label class="block text-sm font-medium text-slate-300">Nom<input name="nom" required class="mt-2 w-full rounded-lg border border-slate-700 bg-slate-800 px-4 py-3 text-white outline-none focus:border-sky-500 focus:ring-2 focus:ring-sky-500/30"></label>
                <label class="block text-sm font-medium text-slate-300">Prénom<input name="prenom" required class="mt-2 w-full rounded-lg border border-slate-700 bg-slate-800 px-4 py-3 text-white outline-none focus:border-sky-500 focus:ring-2 focus:ring-sky-500/30"></label>
                <label class="block text-sm font-medium text-slate-300">CIN<input name="cin" required class="mt-2 w-full rounded-lg border border-slate-700 bg-slate-800 px-4 py-3 text-white outline-none focus:border-sky-500 focus:ring-2 focus:ring-sky-500/30"></label>
                <label class="block text-sm font-medium text-slate-300">Email<input type="email" name="email" required class="mt-2 w-full rounded-lg border border-slate-700 bg-slate-800 px-4 py-3 text-white outline-none focus:border-sky-500 focus:ring-2 focus:ring-sky-500/30"></label>
                <label class="block text-sm font-medium text-slate-300">Téléphone<input name="telephone" class="mt-2 w-full rounded-lg border border-slate-700 bg-slate-800 px-4 py-3 text-white outline-none focus:border-sky-500 focus:ring-2 focus:ring-sky-500/30"></label>
                <label class="block text-sm font-medium text-slate-300">Adresse<input name="adresse" class="mt-2 w-full rounded-lg border border-slate-700 bg-slate-800 px-4 py-3 text-white outline-none focus:border-sky-500 focus:ring-2 focus:ring-sky-500/30"></label>
                <label class="block text-sm font-medium text-slate-300">Numéro de permis<input name="numeroPermis" class="mt-2 w-full rounded-lg border border-slate-700 bg-slate-800 px-4 py-3 text-white outline-none focus:border-sky-500 focus:ring-2 focus:ring-sky-500/30"></label>
                <label class="block text-sm font-medium text-slate-300">Mot de passe
                    <input type="password" name="motDePasse" minlength="8" required class="mt-2 w-full rounded-lg border border-slate-700 bg-slate-800 px-4 py-3 text-white outline-none focus:border-sky-500 focus:ring-2 focus:ring-sky-500/30">
                </label>
                <div class="flex items-end sm:col-span-2"><button type="submit" class="w-full rounded-lg bg-sky-500 px-4 py-3 font-semibold text-slate-950 transition hover:bg-sky-400">S'inscrire</button></div>
            </form>
            <p class="mt-6 text-center text-sm text-slate-400">Déjà inscrit ?
                <a href="${pageContext.request.contextPath}/login" class="font-semibold text-sky-400 hover:text-sky-300">Se connecter</a>
            </p>
        </div>
    </section>
</main>
</body>
</html>
