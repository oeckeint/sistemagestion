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
            <strong>${mensajeRegistro}</strong>
        </div>
    </div>
        
    <div class="container">
        <div class="row">
            <div class="col-md-12">
                <div class="row py-4 align-items-center">
                    <div class="col-md-12 col-lg-5">
                        <h2>Listado de clientes <span class="badge badge-success">${totalClientes}</span></h2>
                    </div>
                    <div class="col col-lg-3">
                        <a href="#" class="btn btn-primary btn-block" data-toggle="modal" data-target="#agregarClienteModal"><i class="fas fa-plus"></i> Agregar</a>
                    </div>
                </div>
            </div>
        </div>
        
        <table class="table table-bordered text-center table-fluid" id="myTable">
            <thead class="thead-dark">
                <tr><th>Cliente</th><th>Cups</th><th>Nombre</th><th>Tarifa</th><th>Acciones</th></tr>
            </thead>
            <tbody>
                <c:forEach var="cliente" items="${clientes}">
                    <tr>
                        <td>${cliente.idCliente}</td>
                        <td>${cliente.cups}</td>
                        <td>${cliente.nombreCliente}</td>
                        <td>${cliente.tarifa}</td>
                        <td><a href="${pageContext.request.contextPath}/ControlClientes?accion=editar&idCliente=${cliente.idCliente}" class="btn btn-secondary"><i class="fas fa-edit"></i> Editar</a></td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>
    </div>

    <script>
    $(document).ready( function () {
        $('#myTable').DataTable();
    } );
    </script>
    
    <!--Agregar Cliente modadl-->
    <jsp:include page="/WEB-INF/paginas/cliente/agregarCliente.jsp"></jsp:include>
    
    <!--Footer-->
    <jsp:include page="/WEB-INF/paginas/comunes/piePagina.jsp"></jsp:include>
</body>
</html>