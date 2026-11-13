<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<c:set var="active" value="vehicules"/>
<%@ include file="/WEB-INF/jspf/header.jspf" %>

<div class="mb-4 flex flex-wrap items-center justify-between gap-3">
    <h1 class="text-2xl font-bold text-slate-800">Véhicules</h1>
    <%-- Seul l'administrateur peut ajouter --%>
    <c:if test="${sessionScope.authenticatedRole == 'ADMIN'}">
        <a class="inline-flex items-center rounded-lg font-medium text-white transition px-4 py-2 text-sm bg-sky-600 hover:bg-sky-700" href="${pageContext.request.contextPath}/vehicules?action=new">+ Ajouter un véhicule</a>
    </c:if>
</div>

<%-- Une carte par véhicule --%>
<div class="grid gap-5 sm:grid-cols-2 lg:grid-cols-3">
    <c:forEach var="v" items="${vehicules}">
        <div class="overflow-hidden rounded-xl bg-white shadow">

            <%-- Photo (ou une image par défaut s'il n'y en a pas) --%>
            <c:choose>
                <c:when test="${not empty v.image}">
                    <img class="h-44 w-full object-cover" src="${pageContext.request.contextPath}/images/cars/${v.image}"
                         alt="<c:out value='${v.marque} ${v.modele}'/>">
                </c:when>
                <c:otherwise>
                    <div class="flex h-44 items-center justify-center bg-slate-200 text-5xl">🚗</div>
                </c:otherwise>
            </c:choose>

            <div class="p-4">
                <div class="flex items-start justify-between gap-2">
                    <h2 class="text-lg font-semibold"><c:out value="${v.marque}"/> <c:out value="${v.modele}"/></h2>
                    <span class="rounded-full px-2.5 py-1 text-xs font-semibold ${v.disponible ? 'bg-emerald-100 text-emerald-700' : 'bg-red-100 text-red-700'}">${v.disponible ? 'Disponible' : 'Loué'}</span>
                </div>

                <p class="mt-1 text-sm text-slate-500">
                    ${v.annee} · <c:out value="${v.carburant}"/> · <fmt:formatNumber value="${v.kilometrage}" pattern="#,##0"/> km
                </p>

                <%-- L'immatriculation est visible seulement par l'administrateur --%>
                <c:if test="${sessionScope.authenticatedRole == 'ADMIN'}">
                    <p class="text-xs text-slate-400"><c:out value="${v.immatriculation}"/></p>
                </c:if>

                <p class="mt-2 text-xl font-bold text-sky-700">
                    <fmt:formatNumber value="${v.prixJour}" pattern="#,##0"/> MAD
                    <span class="text-sm font-normal text-slate-500">/ jour</span>
                </p>

                <div class="mt-4 flex flex-wrap gap-2">
                    <c:choose>
                        <%-- Administrateur : modifier et supprimer --%>
                        <c:when test="${sessionScope.authenticatedRole == 'ADMIN'}">
                            <a class="inline-flex items-center rounded-lg font-medium text-white transition px-3 py-1.5 text-xs bg-slate-500 hover:bg-slate-600" href="${pageContext.request.contextPath}/vehicules?action=edit&id=${v.id}">Modifier</a>
                            <form method="post" action="${pageContext.request.contextPath}/vehicules"
                                  onsubmit="return confirm('Supprimer ce véhicule ?');">
                                <input type="hidden" name="action" value="delete">
                                <input type="hidden" name="id" value="${v.id}">
                                <button class="inline-flex items-center rounded-lg font-medium text-white transition px-3 py-1.5 text-xs bg-red-600 hover:bg-red-700" type="submit">Supprimer</button>
                            </form>
                        </c:when>
                        <%-- Visiteur ou client : réserver (le login est demandé si besoin) --%>
                        <c:when test="${v.disponible}">
                            <a class="inline-flex items-center rounded-lg font-medium text-white transition px-4 py-2 text-sm bg-emerald-600 hover:bg-emerald-700" href="${pageContext.request.contextPath}/reservations/nouvelle?vehiculeId=${v.id}">Réserver</a>
                        </c:when>
                    </c:choose>
                </div>
            </div>
        </div>
    </c:forEach>
</div>

<c:if test="${empty vehicules}">
    <div class="rounded-xl bg-white p-8 text-center text-slate-400 shadow">Aucun véhicule enregistré.</div>
</c:if>

<%@ include file="/WEB-INF/jspf/footer.jspf" %>