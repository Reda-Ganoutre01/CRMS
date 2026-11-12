<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="active" value="profile"/>
<%@ include file="/WEB-INF/jspf/header.jspf" %>

<div class="page-head">
    <div>
        <h1>Mon profil</h1>
        <p class="muted">Modifiez vos informations personnelles et vos accès.</p>
    </div>
</div>

<div class="grid gap-6 lg:grid-cols-[minmax(0,2fr)_minmax(260px,1fr)]">
    <form method="post" action="${pageContext.request.contextPath}/profile" class="card">
        <div class="mb-5 flex items-center gap-4 border-b border-slate-200 pb-5">
            <div class="flex h-16 w-16 items-center justify-center rounded-full bg-sky-100 text-2xl text-sky-700">👤</div>
            <div>
                <h2 class="text-xl font-bold text-slate-800"><c:out value="${profile.nomComplet}"/></h2>
                <p class="text-sm text-slate-500"><c:out value="${profile.role}"/></p>
            </div>
        </div>

        <div class="grid gap-4 sm:grid-cols-2">
            <label class="block text-sm font-medium text-slate-700">Nom
                <input name="nom" required value="<c:out value='${profile.nom}'/>" class="mt-1 w-full rounded-lg border border-slate-300 px-3 py-2">
            </label>
            <label class="block text-sm font-medium text-slate-700">Prénom
                <input name="prenom" required value="<c:out value='${profile.prenom}'/>" class="mt-1 w-full rounded-lg border border-slate-300 px-3 py-2">
            </label>
            <label class="block text-sm font-medium text-slate-700">Email
                <input type="email" name="email" required value="<c:out value='${profile.email}'/>" class="mt-1 w-full rounded-lg border border-slate-300 px-3 py-2">
            </label>
            <label class="block text-sm font-medium text-slate-700">CIN
                <input disabled value="<c:out value='${profile.cin}'/>" class="mt-1 w-full rounded-lg border border-slate-200 bg-slate-100 px-3 py-2 text-slate-500">
            </label>
            <label class="block text-sm font-medium text-slate-700">Téléphone
                <input name="telephone" value="<c:out value='${profile.telephone}'/>" class="mt-1 w-full rounded-lg border border-slate-300 px-3 py-2">
            </label>
            <label class="block text-sm font-medium text-slate-700">Numéro de permis
                <input name="numeroPermis" value="<c:out value='${profile.numeroPermis}'/>" class="mt-1 w-full rounded-lg border border-slate-300 px-3 py-2">
            </label>
        </div>
        <label class="mt-4 block text-sm font-medium text-slate-700">Adresse
            <input name="adresse" value="<c:out value='${profile.adresse}'/>" class="mt-1 w-full rounded-lg border border-slate-300 px-3 py-2">
        </label>
        <label class="mt-4 block text-sm font-medium text-slate-700">Nouveau mot de passe
            <input type="password" name="motDePasse" minlength="8" placeholder="Laisser vide pour conserver l'actuel" class="mt-1 w-full rounded-lg border border-slate-300 px-3 py-2">
        </label>
        <button type="submit" class="btn mt-6">Enregistrer les modifications</button>
    </form>

    <aside class="card h-fit border border-red-100">
        <h2 class="text-lg font-bold text-slate-800">Supprimer le compte</h2>
        <p class="mt-2 text-sm text-slate-500">Vos contrats et réservations seront conservés, mais vos informations personnelles seront anonymisées.</p>
        <form method="post" action="${pageContext.request.contextPath}/profile/delete" class="mt-5"
              onsubmit="return confirm('Voulez-vous vraiment supprimer votre compte ? Cette action est irréversible. Vos contrats et réservations seront conservés.');">
            <button type="submit" class="btn danger w-full">Supprimer mon compte</button>
        </form>
    </aside>
</div>

<%@ include file="/WEB-INF/jspf/footer.jspf" %>
