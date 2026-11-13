<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Erreur | CRMS</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <script src="https://cdn.tailwindcss.com"></script>
</head>
<body class="min-h-screen bg-slate-100 text-slate-800">
<main class="relative flex min-h-screen items-center justify-center overflow-hidden px-5 py-12">
    <div class="pointer-events-none absolute -left-24 -top-24 h-72 w-72 rounded-full bg-sky-200/50 blur-3xl"></div>
    <div class="pointer-events-none absolute -bottom-32 -right-24 h-96 w-96 rounded-full bg-indigo-200/40 blur-3xl"></div>

    <section class="relative w-full max-w-xl rounded-2xl border border-slate-200 bg-white p-8 text-center shadow-xl sm:p-12">
        <div class="mx-auto flex h-16 w-16 items-center justify-center rounded-2xl bg-sky-100 text-3xl text-sky-600">
            !
        </div>
        <p class="mt-6 text-sm font-bold uppercase tracking-[0.3em] text-sky-600">CRMS</p>
        <div class="mt-3 flex items-center justify-center gap-3">
            <h1 class="text-6xl font-black tracking-tight text-slate-900">
                <c:choose>
                    <c:when test="${not empty requestScope['jakarta.servlet.error.status_code']}">
                        <c:out value="${requestScope['jakarta.servlet.error.status_code']}"/>
                    </c:when>
                    <c:otherwise>500</c:otherwise>
                </c:choose>
            </h1>
            <span class="rounded-full bg-red-100 px-3 py-1 text-xs font-bold uppercase tracking-wider text-red-700">Erreur</span>
        </div>
        <h2 class="mt-4 text-2xl font-bold text-slate-800">Une erreur est survenue</h2>
        <p class="mx-auto mt-3 max-w-md text-slate-500">
            <c:choose>
                <c:when test="${not empty requestScope['jakarta.servlet.error.message']}">
                    <c:out value="${requestScope['jakarta.servlet.error.message']}"/>
                </c:when>
                <c:otherwise>La page demandée est indisponible pour le moment.</c:otherwise>
            </c:choose>
        </p>
        <div class="mt-8 flex flex-col justify-center gap-3 sm:flex-row">
            <a class="btn" href="${pageContext.request.contextPath}/">Retour à l'accueil</a>
            <a class="btn secondary" href="${pageContext.request.contextPath}/login">Se connecter</a>
        </div>
        <p class="mt-8 border-t border-slate-100 pt-5 text-xs text-slate-400">Si le problème persiste, contactez l'administrateur.</p>
    </section>
</main>
</body>
</html>
