<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@taglib prefix="security" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<c:set var="language" value="${not empty param.language ? param.language : not empty language ? language : 'es'}" scope="session"/>
<fmt:setLocale value="${language}"/>
<fmt:setBundle basename="labels"/>
<!DOCTYPE html>
<html>
    <head>
        <jsp:include page="/WEB-INF/paginas/comunes/contenidoHead.jsp"></jsp:include>
        </head>
        <body>
            <!--Cabecero-->
        <jsp:include page="/WEB-INF/paginas/comunes/cabecero.jsp"></jsp:include>
            <div class="alert alert-warning alert-dismissible fade show" role="alert">
                <div class="container">
                    <c:choose>
                        <c:when test="${mensaje eq 'registrosEncontrados'}">
                            <fmt:message key="customers.reclamaciones.iniciodatosfiltradosmensaje">
                                <fmt:param value="${busqueda.valorActual}"/>
                                <fmt:param value="${busqueda.filtroActual}"/>
                            </fmt:message>
                        </c:when>
                        <c:when test="${mensaje eq 'sinregistro'}">
                            <fmt:message key="customers.reclamaciones.iniciosindatosfiltradosmensaje">
                                <fmt:param value="${busqueda.valorActual}"/>
                                <fmt:param value="${busqueda.filtroActual}"/>
                            </fmt:message>
                        </c:when>
                        <c:otherwise>
                            <fmt:message key="customers.reclamaciones.iniciomensaje"></fmt:message>
                        </c:otherwise>
                    </c:choose>
                </div>
            </div>

        <div class="container">
            <hr>
            <div class="row justify-content-between p-0">

                <div class="col-6">
                    <h2 class="m-0">
                        <a href="${pageContext.request.contextPath}/"><i class="fas fa-arrow-circle-left text-success"></i></a>
                        <fmt:message key="customers.reclamaciones.tittle"/>
                        <span class="badge bg-success">
                            <c:choose>
                                <c:when test="${paginaActual < ultimaPagina}">
                                    ${registrosMostrados} / ${totalRegistros}
                                </c:when>
                                <c:otherwise>
                                    ${totalRegistros} / ${totalRegistros}
                                </c:otherwise>
                            </c:choose>
                        </span>
                    </h2>
                </div>
                <div class="col-6 row justify-content-end">
                    <jsp:include page="formularioBusqueda.jsp" />
                </div>
            </div>
            <hr>
            <c:choose>
                <c:when test="${contenidoVisible == 'no' }">

                </c:when>
                <c:otherwise>
                    <div class="table-responsive">
                        <table class="table table-hover text-center">
                            <thead>
                                <tr class="bg-dark text-white">
                                    <th scope="col"><fmt:message key="customers.reclamaciones.titulotablalista"/></th>
                                    <th scope="col"><fmt:message key="customers.reclamaciones.cliente"/> </th>
                                    <th scope="col"><fmt:message key="customers.reclamaciones.codigoDePaso"/> </th>
                                    <th scope="col"><fmt:message key="customers.reclamaciones.fechaSolicitud"/></th>
                                    <th scope="col"><fmt:message key="customers.reclamaciones.codigoSolicitud"/></th>
                                    <th scope="col"><fmt:message key="customers.reclamaciones.acciones"/></th>
                                </tr>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach var="reclamacion" items="${reclamaciones}" varStatus="id">
                                    <c:url var="detalles" value="/clientes/reclamaciones/detalles">
                                        <c:param name="valor" value="${reclamacion.idReclamacion}"/>
                                        <c:param name="filtro" value="reclamacion"/>
                                    </c:url>
                                    <c:url var="detallesCliente" value="/clientes/detalles">
                                        <c:param name="valor" value="${reclamacion.cliente.idCliente}"/>
                                        <c:param name="filtro" value="id"/>
                                    </c:url>
                                    <tr>
                                        <td>${reclamacion.idReclamacion}</td>
                                        <td><a href="${detallesCliente}" class="btn btn-success">${reclamacion.cliente.idCliente}</a></td>
                                        <td>${reclamacion.codigoDePaso}</td>
                                        <td>${reclamacion.fechaSolicitud}</td>
                                        <td>${reclamacion.codigoDeSolicitud}</td>
                                        <td>
                                            <button class="btn btn-danger" type="button" id="detailButton${id.count}" onclick="loadData(${id.count}, '${detalles}');">
                                                <i class="fas fa-eye" id="detailsIcon${id.count}"></i>
                                            </button>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                            <jsp:include page="../xml/pagination.jsp" />
                        </table>
                    </div>
                    <jsp:include page="../xml/pagination.jsp" />
                </c:otherwise>
            </c:choose>
        </div>
        <jsp:include page="/WEB-INF/paginas/comunes/piePagina.jsp"></jsp:include>
    </body>
</html>

<script>
    function loadData(id, url) {
        let detailButton = document.querySelector("#detailButton" + id);
        let spanDetail = document.createElement("span");
        spanDetail.className = "spinner-border spinner-border-sm detailSpinner" + id;
        detailButton.disabled = true;
        detailButton.removeChild(document.getElementById("detailsIcon" + id));
        detailButton.appendChild(spanDetail);
        window.location = url;
    }
    
    <security:authorize access="hasRole('ADMIN')">
		Mousetrap.bind(['alt+shift+1'], function(){location.href= path + "/clasificar";});
		Mousetrap.bind(['alt+shift+2'], function(){location.href= path + "/procesar";});
	</security:authorize>
</script>
