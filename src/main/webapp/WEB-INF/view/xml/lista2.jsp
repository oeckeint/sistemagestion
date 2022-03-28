<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
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
                    <c:when test="${param.error != null}"><fmt:message key="login.badcredentials"/></c:when>
                    <c:when test="${param.logout != null}"><fmt:message key="login.logout.message"/></c:when>
                    <c:otherwise>${mensaje}</c:otherwise>
                </c:choose>
            </div>
        </div>

        <div class="container">
            <hr>
            <div class="row justify-content-between p-0">

                <div class="col-6">
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
                <div class="col-6 row justify-content-end">
                    <jsp:include page="../comunes/BotonesXML.jsp" />
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
                                    <th scope="col">#Reg</th>
                                    <th scope="col">Cliente</th>
                                    <th scope="col">Cups</th>
                                    <th scope="col">FHasta</th>
                                    <th scope="col">ImpPot</th>
                                    <th scope="col">ImpEneAct</th>
                                    <th scope="col">CodFiscal</th>
                                    <th scope="col">ImpFac</th>
                                    <th scope="col">EA</th>
                                    <th scope="col">Detalles</th>
                                </tr>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach var="documento" items="${documentos}" varStatus="id">
                                    <c:url var="detalles" value="/${controller}/detalles">
                                        <c:param name="codFisFac" value="${documento.codFisFac}"/>
                                    </c:url>
                                    <c:url var="detallesCliente" value="/clientes/detalles">
                                        <c:param name="idCliente" value="${documento.idCliente}"/>
                                    </c:url>
                                    <tr>
                                        <th scope="row">${id.count}</th>
                                        <td><a href="${detallesCliente}" class="btn btn-success">${documento.idCliente}</i></a></td>
                                        <td>${documento.cups}</td>
                                        <td>
                                            <c:choose>
                                                <c:when test="${documento.eaFecHas2 eq ''}">${documento.eaFecHas1}</c:when>
                                                <c:otherwise>${documento.eaFecHas2}</c:otherwise>
                                            </c:choose>
                                        </td>
                                        <td>${documento.potImpTot}</td>
                                        <td>${documento.eaImpTot}</td>
                                        <td>${documento.codFisFac}</td>
                                        <td>${documento.rfImpTot}</td>
                                        <td>${documento.eaValSum}</td>
                                        <td>
                                            <button class="btn btn-danger" type="button" id="detailButton${id.count}" onclick="loadData(${id.count}, '${detalles}');">
                                                <i class="fas fa-eye" id="detailsIcon${id.count}"></i>
                                            </button>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                            <jsp:include page="pagination.jsp" />
                        </table>
                    </div>
                    <jsp:include page="pagination.jsp" />
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
</script>