<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

        <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/4.5.2/css/bootstrap.min.css">
        <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.5.1/jquery.min.js"></script>
        <script src="https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.16.0/umd/popper.min.js"></script>
        <script src="https://maxcdn.bootstrapcdn.com/bootstrap/4.5.2/js/bootstrap.min.js"></script>

        <section id="clientes">
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
                        <div class="row">                            
                            <input class="form-control" id="myInput" type="text" placeholder="Buscar..">
                            <br>
                            <br>
                            <table class="table table-bordered text-center">
                                <thead class="thead-dark">
                                    <tr>
                                        <th>Id Cliente</th>
                                        <th>CUPS</th>
                                        <th>Nombre</th>
                                        <th>Tarifa</th>
                                        <th>Acciones</th>
                                    </tr>
                                </thead>
                                <tbody id="myTable">
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
                    </div>
                </div>
            </div>
        </section>
        
        <script>
            $(document).ready(function () {
                $("#myInput").on("keyup", function () {
                    var value = $(this).val().toLowerCase();
                    $("#myTable tr").filter(function () {
                        $(this).toggle($(this).text().toLowerCase().indexOf(value) > -1)
                    });
                });
            });
        </script>
        
        <!--Agregar Cliente modadl-->
        <jsp:include page="/WEB-INF/paginas/cliente/agregarCliente.jsp"></jsp:include>