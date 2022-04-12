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
                        <span class="badge bg-success">
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
                                    <th>Cliente</th>
                                    <th>Cups</th>
                                    <th>CodFiscal</th>
                                    <th>ImpTot</th>
                                    <th>Remesa</th>
                                    <th>Acciones</th>
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
                                <c:forEach var="documento" items="${documentos}" varStatus="id">
                                    <c:url var="detalles" value="/${controller}/detalles">
                                        <c:param name="codFisFac" value="${documento.codFisFac}"/>
                                    </c:url>
                                    <c:url var="detallesCliente" value="/clientes/detalles">
                                        <c:param name="idCliente" value="${documento.idCliente}"/>
                                    </c:url>
                                    <tr>
                                        <td><a href="${detallesCliente}" class="btn btn-success">${documento.idCliente}</i></a></td>
                                        <td>${documento.cups}</td>
                                        <td>${documento.codFisFac}</td>
                                        <td>${documento.impTotFac}</td>
                                        <td>${documento.idRem}</td>
                                        <td><a href="${detalles}" class="btn btn-danger" id="details"><i class="fas fa-eye"></i></a></td>
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

        <script>            
            $("#details").click(function () {
                $(this).prop("disabled", true); //deshabilitamos el botón
                $(this).html(
                        `<span class="spinner-border spinner-border-sm" role="status" aria-hidden="true"></span>  ${etiquetaBoton}` //añadimos el spinner
                        );
            });

            $("#details").click(function () {
                $(this).prop("disabled", true); //deshabilitamos el botón
                $(this).html(
                        `<span class="spinner-border spinner-border-sm" role="status" aria-hidden="true"></span>  ${etiquetaBoton}` //añadimos el spinner
                        );
                document.getElementById("myForm").submit();
            });

        </script>

        <jsp:include page="/WEB-INF/paginas/comunes/piePagina.jsp"></jsp:include>
    </body>
</html>