<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
    <head>
        <meta charset="utf-8" />
        <meta http-equiv="X-UA-Compatible" content="IE=edge">
        <meta name="viewport" content="width=device-width, initial-scale=1">
        <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.2.1/css/bootstrap.min.css" integrity="sha384-GJzZqFGwb1QTTN6wy59ffF1BuGJpLSa9DkKMp0DgiMDm4iYMj70gZWKYbI706tWS" crossorigin="anonymous">
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
                    ${mensajeRegistro}
            </div>
        </div>

        <div class="container">
            <div class="row align-items-center py-3">

                <div class="col-6">
                    <h2 class="m-0">${tituloTabla} <span class="badge badge-success">${totalRegistros}</span></h2>
                </div>

                <div class="col-6 row">
                    <div class="col">
                        <a href="${pageContext.request.contextPath}/EvaluacionXML" class="btn btn-primary btn-block"><i class="fas fa-plus"></i> Evaluar</a>
                    </div>
                    <div class="col">
                        <a href="${pageContext.request.contextPath}/ProcesamientoXML" class="btn btn-primary btn-block"><i class="fas fa-plus"></i> Procesar</a>
                    </div>
                    <c:choose>
                        <c:when test="${botonVisible == 'no' }">

                        </c:when>
                        <c:otherwise>
                            <div class="col">
                                <form action="${pageContext.request.contextPath}/${nombreServlet}" method="post" id="myForm">
                                    <div class="input-group mb-2">
                                        <div class="input-group-prepend">
                                            <button class="btn btn-primary" type="submit" id="btnSubmit"><i class="fas fa-search"></i></button>
                                        </div>
                                        <input type="text"  name="valor" class="form-control" id="inlineFormInputGroup" placeholder="Buscar">
                                        <select class="form-select fa" name="filtro">
                                            <option class="fa" value="cliente">&#xf007;</option>
                                            <option class="fa" value="remesa">&#xf621;</option>
                                        </select>
                                    </div>
                                </form>
                            </div>
                        </c:otherwise>
                    </c:choose>
                </div>

            </div>
            <c:choose>
                <c:when test="${contenidoVisible == 'no' }">

                </c:when>
                <c:otherwise>
                    <table class="table table-bordered text-center table-fluid" id="myTable">
                        <thead class="thead-dark">
                            <tr>
                                <th>#Reg</th>
                                <th>Cliente</th>
                                <th>CodFiscal</th>
                                <th>Fecha</th>
                                <th>ImpTot</th>
                                <th>Remesa</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:if test="${documentoResumen != null}">
                                <tr>
                                    <td>-R</td>
                                    <td>${documentoResumen.cliente.idCliente}</td>
                                </tr>
                            </c:if>
                            <c:forEach var="documento" items="${documentos}" varStatus="id">
                                <tr>
                                    <td>${id.count}</td>
                                    <td>${documento.cliente.idCliente}</td>
                                    <td>${documento.cliente.cups}</td>
                                    <td>${documento.datosGeneralesFactura.codFisFac}</td>
                                    <td>${documento.datosGeneralesFactura.fecFac}</td>
                                    <td>${documento.registroFin.idRemesa}</td>
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