<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="security" uri="http://www.springframework.org/security/tags" %>
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
                <div class="container">${mensaje}</div>
        </div>

        <div class="container">
            <hr>
            <div class="row">
                <div class="col-12">
                    <div class="row justify-content-between p-0">
                        <div class="col-md-12 col-lg-5">
                            <h2 class="m-0"><a href="javascript:history.back();"><i class="fas fa-arrow-circle-left text-success"></i></a> Listado de clientes <span class="badge badge-success">${totalClientes}</span></h2>
                        </div>
                        <div class="col-6 row  justify-content-end">
                            <security:authorize access="hasRole('ADMIN')">
                                <div class="col-3">
                                    <a href="${pageContext.request.contextPath}/clientes/formulario" class="btn btn-primary btn-block"><i class="fas fa-plus"></i> Agregar</a>
                                </div>
                            </security:authorize>
                            <div class="col-5">
                                <form action="${pageContext.request.contextPath}/clientes/busqueda" method="post" id="myForm">
                                    <div class="input-group mb-2">
                                        <div class="input-group-prepend">
                                            <button class="btn btn-primary" type="submit" id="btnSubmit"><i class="fas fa-search"></i></button>
                                        </div>
                                        <input type="text"  name="valor" class="form-control" id="inlineFormInputGroup" placeholder="Buscar id cliente" value="${ultimaBusqueda}" required>
                                    </div>
                                </form>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
            <hr>

            <table class="table table-bordered text-center table-fluid" id="myTable">
                <thead class="thead-dark">
                    <tr><th>Cliente</th><th>Cups</th><th>Nombre</th><th>Tarifa</th><th>Acciones</th></tr>
                </thead>
                <tbody>
                    <c:forEach var="cliente" items="${clientes}">
                        <c:url var="updateLink" value="/clientes/editar">
                            <c:param name="idCliente" value="${cliente.idCliente}"/>
                        </c:url>
                        <c:url var="detalles" value="/clientes/detalles">
                            <c:param name="idCliente" value="${cliente.idCliente}"/>
                        </c:url>
                        <tr>
                            <td>${cliente.idCliente}</td>
                            <td>${cliente.cups}</td>
                            <c:choose>
                                <c:when test="${cliente.nombreCliente.length() > 15}">
                                    <td>${cliente.nombreCliente.substring(0, 15)}</td>
                                </c:when>
                                <c:otherwise>
                                    <td>${cliente.nombreCliente}</td>
                                </c:otherwise>
                            </c:choose>
                            <td>${cliente.tarifa}</td>
                            <td><a href="${updateLink}" class="btn btn-success"><i class="fas fa-edit"></i></a>  <a href="${detalles}" class="btn btn-danger"><i class="fas fa-eye"></i></a></td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
        </div>

        <script>
            $(document).ready(function () {
                $('#myTable').DataTable();
            });
        </script>

        <!--Footer-->
        <jsp:include page="/WEB-INF/paginas/comunes/piePagina.jsp"></jsp:include>
    </body>
</html>