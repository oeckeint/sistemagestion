<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <jsp:include page="/WEB-INF/paginas/comunes/contenidoHead.jsp"></jsp:include>
        </head>
        <body>
        <jsp:include page="/WEB-INF/paginas/comunes/cabecero.jsp"></jsp:include>
            <div class="alert alert-warning alert-dismissible fade show" role="alert">
                <div class="container">
                    <strong>${mensaje}</strong>
            </div>
        </div>
        <c:if test="${ cliente != null }">
            <div class="container">

                <hr>
                <div class="row justify-content-between p-0">
                    <div class="col-6">
                        <h2 class="m-0"><a href="javascript:history.back();"><i class="fas fa-arrow-circle-left text-success"></i></a> Detalles cliente</h2>
                    </div>
                    <div class="col-6 row  justify-content-end">
                        <div class="col-3">
                            <a href="${pageContext.request.contextPath}/clientes/formulario" class="btn btn-primary btn-block"><i class="fas fa-plus"></i> Agregar</a>
                        </div>
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
                <hr>

                <!--Primer Renglón-->
                <div class="row justify-content-around">
                    <div class="list-group col-12 col-md-5 col-lg-4 p-2">
                        <h3 class="list-group-item list-group-item-action active text-center h4">Cliente</h3>
                        <div class="row">
                            <div class="col-3 col-md-4">
                                <ul class="list-group list-group-flush text-right">
                                    <dt class="list-group-item px-1">idCliente</dt>
                                    <dt class="list-group-item px-1">CUPS</dt>
                                    <dt class="list-group-item px-1">Nombre</dt>
                                    <dt class="list-group-item px-1">Tarifa</dt>
                                    <dt class="list-group-item px-1">Status</dt>
                                </ul>
                            </div>
                            <div class="col-8 col-md-7">
                                <ul class="list-group list-group-flush">
                                    <li class="list-group-item px-1">${cliente.idCliente}</li>
                                    <li class="list-group-item px-1">${cliente.cups}</li>
                                    <li class="list-group-item px-1">${cliente.nombreCliente}</li>
                                    <li class="list-group-item px-1">${cliente.tarifa}</li>
                                    <li class="list-group-item px-1">${cliente.isDeleted}</li>
                                </ul>
                            </div>
                        </div>
                    </div>
                    <div class="list-group col-12 col-md-5 col-lg-4 p-2">
                        <h3 class="list-group-item list-group-item-action active text-center h4">Contrato</h3>
                        <div class="row">
                            <div class="col-5">
                                <ul class="list-group list-group-flush text-right">
                                    <dt class="list-group-item px-1">Inicio</dt>
                                    <dt class="list-group-item px-1">Fin</dt>
                                    <dt class="list-group-item px-1">Estatus</dt>
                                    <dt class="list-group-item px-1">Producto</dt>
                                    <dt class="list-group-item px-1">Coste Gestion</dt>
                                    <dt class="list-group-item px-1">Alquileres</dt>
                                    <dt class="list-group-item px-1">Id oferta</dt>
                                </ul>
                            </div>
                            <div class="col-7">
                                <ul class="list-group list-group-flush">
                                    <li class="list-group-item px-1">---</li>
                                    <li class="list-group-item px-1">---</li>
                                    <li class="list-group-item px-1">---</li>
                                    <li class="list-group-item px-1">---</li>
                                    <li class="list-group-item px-1">---</li>
                                    <li class="list-group-item px-1">---</li>
                                    <li class="list-group-item px-1">---</li>
                                </ul>
                            </div>
                        </div>
                    </div>
                </div>

                <div class="row justify-content-around">
                    <div class="list-group col-12 col-md-5 col-lg-4 p-2">
                        <h3 class="list-group-item list-group-item-action active text-center h4">Punto de suministro</h3>
                        <div class="row">
                            <div class="col-5">
                                <ul class="list-group list-group-flush text-right">
                                    <dt class="list-group-item px-1">Dir suministro</dt>
                                    <dt class="list-group-item px-1">CP</dt>
                                    <dt class="list-group-item px-1">Población</dt>
                                    <dt class="list-group-item px-1">Provincia</dt>
                                    <dt class="list-group-item px-1">Distribuidora</dt>
                                    <dt class="list-group-item px-1">ATR</dt>
                                    <dt class="list-group-item px-1">Contador</dt>
                                    <dt class="list-group-item px-1">Tipo PM</dt>
                                    <dt class="list-group-item px-1">Modo Lectura</dt>
                                    <dt class="list-group-item px-1">Tarifa</dt>
                                    <dt class="list-group-item px-1">Activats</dt>
                                    <dt class="list-group-item px-1">P1</dt>
                                    <dt class="list-group-item px-1">P2</dt>
                                    <dt class="list-group-item px-1">P3</dt>
                                    <dt class="list-group-item px-1">P4</dt>
                                    <dt class="list-group-item px-1">P5</dt>
                                    <dt class="list-group-item px-1">P6</dt>
                                </ul>
                            </div>
                            <div class="col-7">
                                <ul class="list-group list-group-flush">
                                    <li class="list-group-item px-1">---</li>
                                    <li class="list-group-item px-1">---</li>
                                    <li class="list-group-item px-1">---</li>
                                    <li class="list-group-item px-1">---</li>
                                    <li class="list-group-item px-1">---</li>
                                    <li class="list-group-item px-1">---</li>
                                    <li class="list-group-item px-1">---</li>
                                    <li class="list-group-item px-1">---</li>
                                    <li class="list-group-item px-1">---</li>
                                    <li class="list-group-item px-1">---</li>
                                    <li class="list-group-item px-1">---</li>
                                    <li class="list-group-item px-1">---</li>
                                    <li class="list-group-item px-1">---</li>
                                    <li class="list-group-item px-1">---</li>
                                    <li class="list-group-item px-1">---</li>
                                    <li class="list-group-item px-1">---</li>
                                    <li class="list-group-item px-1">---</li>
                                </ul>
                            </div>
                        </div>

                    </div>
                    <div class="list-group col-12 col-md-5 col-lg-4 p-2">
                        <h3 class="list-group-item list-group-item-action active text-center h4">Datos Generales</h3>
                        <div class="row">
                            <div class="col-5">
                                <ul class="list-group list-group-flush text-right">
                                    <dt class="list-group-item px-1">Titular</dt>
                                    <dt class="list-group-item px-1">Cuenta</dt>
                                    <dt class="list-group-item px-1">CIF</dt>
                                    <dt class="list-group-item px-1">Dom social</dt>
                                    <dt class="list-group-item px-1">CP</dt>
                                    <dt class="list-group-item px-1">Población</dt>
                                    <dt class="list-group-item px-1">Email</dt>
                                    <dt class="list-group-item px-1">CNAES</dt>
                                    <dt class="list-group-item px-1">Grupo</dt>
                                    <dt class="list-group-item px-1">Comercial</dt>
                                </ul>
                            </div>
                            <div class="col-7">
                                <ul class="list-group list-group-flush">
                                    <li class="list-group-item px-1">---</li>
                                    <li class="list-group-item px-1">---</li>
                                    <li class="list-group-item px-1">---</li>
                                    <li class="list-group-item px-1">---</li>
                                    <li class="list-group-item px-1">---</li>
                                    <li class="list-group-item px-1">---</li>
                                    <li class="list-group-item px-1">---</li>
                                    <li class="list-group-item px-1">---</li>
                                    <li class="list-group-item px-1">---</li>
                                    <li class="list-group-item px-1">---</li>
                                </ul>
                            </div>
                        </div>
                    </div>
                </div>

            </div>
        </c:if>    
        <jsp:include page="/WEB-INF/paginas/comunes/piePagina.jsp"></jsp:include>
    </body>
</html>