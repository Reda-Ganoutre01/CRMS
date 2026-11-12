<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="fr" class="h-full">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Connexion | CRMS</title>
    <script src="https://cdn.tailwindcss.com"></script>
</head>
<body class="min-h-full bg-slate-100 text-slate-800">
<main class="flex min-h-screen items-center justify-center px-6 py-12">
    <section class="w-full max-w-md">
        <div class="mb-8 text-center">
            <p class="mb-3 text-sm font-semibold uppercase tracking-[0.3em] text-sky-600">CRMS</p>
            <h1 class="text-3xl font-bold tracking-tight">Bienvenue</h1>
            <p class="mt-2 text-slate-500">Connectez-vous à votre espace de location.</p>
        </div>
        <div class="rounded-2xl border border-slate-200 bg-white p-8 shadow-xl">
            <c:if test="${not empty param.error}">
                <p class="mb-5 rounded-lg border border-red-900/60 bg-red-950/50 px-4 py-3 text-sm text-red-300">${param.error}</p>
            </c:if>
            <c:if test="${param.deleted == 'true'}">
                <p class="mb-5 rounded-lg border border-emerald-200 bg-emerald-50 px-4 py-3 text-sm text-emerald-700">Votre compte a été supprimé. Vos contrats et réservations ont été conservés.</p>
            </c:if>
            <form method="post" action="${pageContext.request.contextPath}/login" class="space-y-5">
                <label class="block text-sm font-medium text-slate-700">Email
                    <input type="email" name="email" required class="mt-2 w-full rounded-lg border border-slate-300 bg-white px-4 py-3 text-slate-800 outline-none transition focus:border-sky-500 focus:ring-2 focus:ring-sky-500/30">
                </label>
                <label class="block text-sm font-medium text-slate-700">Mot de passe
                    <input type="password" name="motDePasse" required class="mt-2 w-full rounded-lg border border-slate-300 bg-white px-4 py-3 text-slate-800 outline-none transition focus:border-sky-500 focus:ring-2 focus:ring-sky-500/30">
                </label>
                <button type="submit" class="w-full rounded-lg bg-sky-600 px-4 py-3 font-semibold text-white transition hover:bg-sky-700 focus:outline-none focus:ring-2 focus:ring-sky-400 focus:ring-offset-2">Se connecter</button>
            </form>
            <p class="mt-6 text-center text-sm text-slate-400">Pas encore de compte ?
                <a href="${pageContext.request.contextPath}/register" class="font-semibold text-sky-400 hover:text-sky-300">Créer un compte</a>
            </p>
        </div>
    </section>
</main>
</body>
</html>
