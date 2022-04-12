<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="security" uri="http://www.springframework.org/security/tags" %>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<jsp:include page="/WEB-INF/view/comunes/Lang.jsp"></jsp:include>
    <!DOCTYPE html>
    <html lang="${language}">
    <head>
        <jsp:include page="/WEB-INF/paginas/comunes/contenidoHead.jsp"></jsp:include>
        </head>
        <body>
            <!--Cabecero-->
        <jsp:include page="/WEB-INF/paginas/comunes/cabecero.jsp"></jsp:include>
            <div class="alert alert-warning alert-dismissible fade show" role="alert">
                <div class="container">
                <c:choose>
                    <c:when test="${param.sinvalor != null}"><fmt:message key="error.sinvalor"/></c:when>
                    <c:when test="${param.sinregistro != null}">
                        <fmt:message key="error.sinregistro">
                            <fmt:param value="${param.v}"/>
                            <fmt:param value="${param.f}"/>
                        </fmt:message>
                    </c:when>
                    <c:when test="${param.idnan != null}">
                        <fmt:message key="error.idnan">
                        	<fmt:param value="${busquedaCliente.filtro}"/>
                        	<fmt:param value="${busquedaCliente.valor}"/>                        	
                        </fmt:message>
                    </c:when>
                    <c:when test="${param.unknown != null}">
                        <fmt:message key="error.unknown"></fmt:message>
                    </c:when>
                    <c:otherwise>${mensaje}</c:otherwise>
                </c:choose>
            </div>
        </div>

        <div class="container">
            <hr>
            <div class="row">
                <div class="col-12">
                    <div class="row justify-content-between p-0">
                        <div class="col-12 col-lg-5">
                            <h2 class="m-0"><a href="${pageContext.request.contextPath}/">
                                    <i class="fas fa-arrow-circle-left text-success"></i></a> 
                                    ${tablaTitulo} 
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
                        <div class="row col-12 col-lg-6 justify-content-end mt-3 mt-md-0">
                            <jsp:include page="../comunes/busquedaCliente.jsp" />
                        </div>
                    </div>
                </div>
            </div>
            <hr>

            <div class="table-responsive">
                <table class="table table-hover text-center">
                    <thead>
                        <tr class="bg-dark text-white">
                            <th scope="col">Cliente</th>
                            <th scope="col">Cups</th>
                            <th scope="col">Nombre</th>
                            <th scope="col">Tarifa</th>
                            <th scope="col">Acciones</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:if test="${documentoResumen != null}">
                            <tr>
                                <td>-R</td>
                                <td>${documentoResumen.idCliente}</td>
                                <td>${documentoResumen.cups}</td>
                                <td>--</td>
                                <td>${documentoResumen.impTotFac}</td>
                                <td>--</td>
                                <td>--</td>
                            </tr>
                        </c:if>
                        <c:forEach var="cliente" items="${clientes}" varStatus="id">
                            <c:url var="updateLink" value="/clientes/editar">
                                <c:param name="idCliente" value="${cliente.idCliente}"/>
                            </c:url>
                            <c:url var="detalles" value="/clientes/detalles">
                                <c:param name="valor" value="${cliente.idCliente}"/>
                                <c:param name="filtro" value="id"/>
                            </c:url>
                            <tr>
                                <th scope="row">${cliente.idCliente}</th>
                                <td>${cliente.cups}</td>
                                <c:choose>
                                    <c:when test="${cliente.nombreCliente.length() > 25}">
                                        <td>${cliente.nombreCliente.substring(0, 25)}</td>
                                    </c:when>
                                    <c:otherwise>
                                        <td>${cliente.nombreCliente}</td>
                                    </c:otherwise>
                                </c:choose>
                                <td>${cliente.tarifa}</td>
                                <td>
                                    <button class="btn btn-success" type="button" id="editButton${id.count}" onclick="editData(${id.count}, '${updateLink}');">
                                        <i class="fas fa-edit" id="editIcon${id.count}"></i></a> 
                                    </button>
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
        </div>

        <!--Footer-->
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

    function editData(id, url) {
        let detailButton = document.querySelector("#editButton" + id);
        let spanDetail = document.createElement("span");
        spanDetail.className = "spinner-border spinner-border-sm detailSpinner" + id;
        detailButton.disabled = true;
        detailButton.removeChild(document.getElementById("editIcon" + id));
        detailButton.appendChild(spanDetail);
        window.location = url;
    }
</script>