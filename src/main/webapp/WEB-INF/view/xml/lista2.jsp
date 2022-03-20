<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
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
                ${mensaje}
            </div>
        </div>

        <div class="container">
            <hr>
            <div class="row justify-content-between p-0">

                <div class="col-6">
                    <h2 class="m-0"><a href="${pageContext.request.contextPath}/">
                            <i class="fas fa-arrow-circle-left text-success"></i></a> 
                            ${tablaTitulo} 
                        <span class="badge badge-success">
                            <c:choose>
                                <c:when test="${paginaActual != ultimaPagina}">
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
                    <div class="col-3">
                        <a href="${pageContext.request.contextPath}/clasificar" class="btn btn-primary btn-block"><i class="fas fa-plus"></i> Clasificar</a>
                    </div>
                    <div class="col-3">
                        <a href="${pageContext.request.contextPath}/procesar" class="btn btn-primary btn-block"><i class="fas fa-plus"></i> Procesar</a>
                    </div>
                    <c:choose>
                        <c:when test="${botonVisible == 'no' }">

                        </c:when>
                        <c:otherwise>
                            <div class="col-5">
                                <form:form action="${pageContext.request.contextPath}/${controller}/busqueda" method="post" id="myForm">
                                    <div class="input-group mb-2">
                                        <div class="input-group-prepend">
                                            <button class="btn btn-primary" type="submit" id="btnSubmit"><i class="fas fa-search"></i></button>
                                        </div>
                                        <input type="text"  name="valor" class="form-control" id="inlineFormInputGroup" placeholder="Buscar" value="${ultimaBusqueda}" required>
                                        <select class="form-select fa" name="filtro">
                                            <option class="fa" value="cliente">&#xf007;</option>
                                            <option class="fa" value="remesa">&#xf621;</option>
                                            <option class="fa" value="codFisFac">&#xf15c;</option>
                                        </select>
                                        <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                                    </div>
                                </form:form>
                            </div>
                        </c:otherwise>
                    </c:choose>
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
                                        <td><a href="${detalles}" class="btn btn-danger"><i class="fas fa-eye"></i></a></td>
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