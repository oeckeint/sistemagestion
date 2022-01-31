<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<!DOCTYPE html>
<html>
    <head>
        <meta charset="utf-8" />
        <meta http-equiv="X-UA-Compatible" content="IE=edge">
        <meta name="viewport" content="width=device-width, initial-scale=1">
        <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.2.1/css/bootstrap.min.css" integrity="sha384-GJzZqFGwb1QTTN6wy59ffF1BuGJpLSa9DkKMp0DgiMDm4iYMj70gZWKYbI706tWS" crossorigin="anonymous">
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css" rel="stylesheet" integrity="sha384-1BmE4kWBq78iYhFldvKuhfTAU6auU8tT94WrHftjDbrCEXSU1oBoqyl2QvZ6jIW3" crossorigin="anonymous">
        <script src="https://code.jquery.com/jquery-3.3.1.slim.min.js" integrity="sha384-q8i/X+965DzO0rT7abK41JStQIAqVgRVzpbzo5smXKp4YfRvH+8abtTE1Pi6jizo" crossorigin="anonymous"></script>
        <script src="https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.14.6/umd/popper.min.js" integrity="sha384-wHAiFfRlMFy6i5SRaxvfOCifBUQy1xHdJ/yoi7FRNXMRBu5WHdZYu1hA6ZOblgut" crossorigin="anonymous"></script>
        <script src="https://stackpath.bootstrapcdn.com/bootstrap/4.2.1/js/bootstrap.min.js" integrity="sha384-B0UglyR+jN6CkvvICOB2joaf5I4l3gm9GU6Hc1og6Ls7i6U/mkkaduKaBhlAXv9k" crossorigin="anonymous"></script>

        <script src="http://code.jquery.com/jquery-3.3.1.min.js"  integrity="sha256-FgpCb/KJQlLNfOu91ta32o/NMZxltwRo8QtmkMRdAu8=" crossorigin="anonymous"></script>
        <link rel="stylesheet" href="https://cdn.datatables.net/1.10.19/css/jquery.dataTables.min.css">
        <script src="https://cdn.datatables.net/1.10.19/js/jquery.dataTables.min.js" ></script>
        <link rel="stylesheet" href="https://use.fontawesome.com/releases/v5.15.1/css/all.css" integrity="sha384-vp86vTRFVJgpjF9jiIGPEEqYqlDwgyBgEF109VFjmqGmIY/Y4HV4d3Gp2irVfcrp" crossorigin="anonymous">
        <title>${tituloPagina}</title>
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
                    <h2 class="m-0"><a href="javascript:history.back();"><i class="fas fa-arrow-circle-left text-success"></i></a> ${tablaTitulo} <span class="badge badge-success">${totalRegistros}</span></h2>
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
                    <table class="table table-bordered text-center table-fluid" id="myTable">
                        <thead class="thead-dark">
                            <tr>
                                <th>#Reg</th>
                                <th>Cliente</th>
                                <th>Cups</th>
                                <th>FHasta</th>
                                <th>ImpPot</th>
                                <th>ImpEneAct</th>
                                <th>CodFiscal</th>
                                <th>ImpFac</th>
                                <th>EA</th>
                                <th>Detalles</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:if test="${documentoResumen != null}">
                                <tr>
                                    <td>-R</td>
                                    <td>${documentoResumen.idCliente}</td>
                                    <td>${documentoResumen.cups}</td>
                                    <td>--</td>
                                    <td>${documentoResumen.potImpTot}</td>
                                    <td>${documentoResumen.eaImpTot}</td>
                                    <td>--</td>
                                    <td>${documentoResumen.rfImpTot}</td>
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
                                    <td>${id.count}</td>
                                    <td><a href="${detallesCliente}" class="btn btn-success">${documento.idCliente}</i></a></td>
                                    <td>${documento.cups}</td>
                                    <td>${documento.eaFecHas1}</td>
                                    <td>${documento.potImpTot}</td>
                                    <td>${documento.eaImpTot}</td>
                                    <td>${documento.codFisFac}</td>
                                    <td>${documento.rfImpTot}</td>
                                    <td>${documento.eaValSum}</td>
                                    <td><a href="${detalles}" class="btn btn-danger"><i class="fas fa-eye"></i></a></td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </c:otherwise>
            </c:choose>
        </div>

        <script>
            $(document).ready(function () {
                $('#myTable').DataTable();
            });
        </script>

        <script>
            $(".btn").click(function () {
                $(this).prop("disabled", true); //deshabilitamos el botón
                $(this).html(
                        `<span class="spinner-border spinner-border-sm" role="status" aria-hidden="true"></span>  ${etiquetaBoton}` //añadimos el spinner
                        );
            });

            $("#btnSubmit").click(function () {
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