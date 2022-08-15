<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
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
                        <h2 class="m-0"><a href="${pageContext.request.contextPath}/clientes"><i class="fas fa-arrow-circle-left text-success"></i></a> Detalles cliente</h2>
                    </div>
                    <div class="row col-12 col-lg-6 justify-content-sm-evenly mt-3 mt-md-0">
                        <jsp:include page="../comunes/busquedaCliente.jsp" />
                    </div>
                </div>
                <hr>

                <!--Primer Renglón-->
                <div class="row justify-content-around">
                    <div class="list-group col-12 col-md-5 col-lg-5 p-2">
                        <h3 class="list-group-item list-group-item-action active text-center h4">Cliente</h3>
                        <div class="row">
                            <div class="col-3 col-md-4">
                                <ul class="list-group list-group-flush text-right">
                                    <dt class="list-group-item px-1">idCliente</dt>
                                    <dt class="list-group-item px-1">CUPS</dt>
                                    <dt class="list-group-item px-1">Nombre</dt>
                                    <dt class="list-group-item px-1">Tarifa</dt>
                                    <dt class="list-group-item px-1">Archivado</dt>
                                    <dt class="list-group-item px-1">No. Tickets</dt>
                                </ul>
                            </div>
                            <div class="col-8 col-md-7">
                                <ul class="list-group list-group-flush">
                                    <li class="list-group-item px-1">${cliente.idCliente}</li>
                                    <li class="list-group-item px-1">${cliente.cups}</li>
                                    <li class="list-group-item px-1">${cliente.nombreCliente}</li>
                                    <li class="list-group-item px-1">${cliente.tarifa}</li>
                                    <li class="list-group-item px-1">
                                        <c:choose>
                                            <c:when test="${cliente.isDeleted == 0}">
                                                No
                                            </c:when>
                                            <c:when test="${cliente.isDeleted == 1}">
                                                Si
                                            </c:when>
                                        </c:choose>
                                    </li>
                                    <li class="list-group-item px-1">${cliente.clienteTickets.size()}</li>
                                </ul>
                            </div>
                        </div>
                    </div>
                    <c:if test="${cliente.clienteContrato != null}">
                        <div class="list-group col-12 col-md-5 col-lg-5 p-2">
                            <h3 class="list-group-item list-group-item-action active text-center h4">Contrato</h3>
                            <div class="row">
                                <div class="col-5">
                                    <ul class="list-group list-group-flush text-right">
                                        <dt class="list-group-item px-1">Inicio</dt>
                                        <dt class="list-group-item px-1">Fin</dt>
                                        <dt class="list-group-item px-1">Activado</dt>
                                        <dt class="list-group-item px-1">Producto</dt>
                                        <dt class="list-group-item px-1">Coste Gestion</dt>
                                        <dt class="list-group-item px-1">Alquileres</dt>
                                        <dt class="list-group-item px-1">Id oferta</dt>
                                    </ul>
                                </div>
                                <div class="col-7">
                                    <ul class="list-group list-group-flush">
                                        <li class="list-group-item px-1">${cliente.clienteContrato.inicioContrato}</li>
                                        <li class="list-group-item px-1">${cliente.clienteContrato.finContrato}</li>
                                        <li class="list-group-item px-1">${cliente.clienteContrato.activado}</li>
                                        <li class="list-group-item px-1">${cliente.clienteContrato.producto}</li>
                                        <li class="list-group-item px-1">${cliente.clienteContrato.costeGestion}</li>
                                        <li class="list-group-item px-1">${cliente.clienteContrato.alquieres}</li>
                                        <li class="list-group-item px-1">${cliente.clienteContrato.idOferta}</li>
                                    </ul>
                                </div>
                            </div>
                        </div>
                    </c:if>
                </div>

                <div class="row justify-content-around">
                    <c:if test="${cliente.clientePuntoSuministro != null}">
                        <div class="list-group col-12 col-md-5 col-lg-5 p-2">
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
                                        <dt class="list-group-item px-1">Activado</dt>
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
                                        <li class="list-group-item px-1">${cliente.clientePuntoSuministro.direccionSuministro}</li>
                                        <li class="list-group-item px-1">${cliente.clientePuntoSuministro.codigoPostal}</li>
                                        <li class="list-group-item px-1">${cliente.clientePuntoSuministro.poblacion}</li>
                                        <li class="list-group-item px-1">${cliente.clientePuntoSuministro.provincia}</li>
                                        <li class="list-group-item px-1">${cliente.clientePuntoSuministro.distribuidora}</li>
                                        <li class="list-group-item px-1">${cliente.clientePuntoSuministro.atr}</li>
                                        <li class="list-group-item px-1">${cliente.clientePuntoSuministro.contador}</li>
                                        <li class="list-group-item px-1">${cliente.clientePuntoSuministro.tipoPM}</li>
                                        <li class="list-group-item px-1">${cliente.clientePuntoSuministro.modoLectura}</li>
                                        <li class="list-group-item px-1">${cliente.clientePuntoSuministro.tarifa}</li>
                                        <li class="list-group-item px-1">${cliente.clientePuntoSuministro.activado}</li>
                                        <li class="list-group-item px-1">${cliente.clientePuntoSuministro.p1}</li>
                                        <li class="list-group-item px-1">${cliente.clientePuntoSuministro.p2}</li>
                                        <li class="list-group-item px-1">${cliente.clientePuntoSuministro.p3}</li>
                                        <li class="list-group-item px-1">${cliente.clientePuntoSuministro.p4}</li>
                                        <li class="list-group-item px-1">${cliente.clientePuntoSuministro.p5}</li>
                                        <li class="list-group-item px-1">${cliente.clientePuntoSuministro.p6}</li>
                                    </ul>
                                </div>
                            </div>
                        </div>
                    </c:if>
                    <c:if test="${cliente.clienteDatosGenerales != null}">
                        <div class="list-group col-12 col-md-5 col-lg-5 p-2">
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
                                        <li class="list-group-item px-1">${cliente.clienteDatosGenerales.titular}</li>
                                        <li class="list-group-item px-1">${cliente.clienteDatosGenerales.cuenta}</li>
                                        <li class="list-group-item px-1">${cliente.clienteDatosGenerales.cif}</li>
                                        <li class="list-group-item px-1">${cliente.clienteDatosGenerales.dominioSocial}</li>
                                        <li class="list-group-item px-1">${cliente.clienteDatosGenerales.codigoPostal}</li>
                                        <li class="list-group-item px-1">${cliente.clienteDatosGenerales.poblacion}</li>
                                        <li class="list-group-item px-1">${cliente.clienteDatosGenerales.email}</li>
                                        <li class="list-group-item px-1">${cliente.clienteDatosGenerales.cnae}</li>
                                        <li class="list-group-item px-1">${cliente.clienteDatosGenerales.grupo}</li>
                                        <li class="list-group-item px-1">${cliente.clienteDatosGenerales.comercial}</li>
                                    </ul>
                                </div>
                            </div>
                        </div>
                    </c:if>
                </div>

            </div>
        </c:if>    
        <jsp:include page="/WEB-INF/paginas/comunes/piePagina.jsp"></jsp:include>
    </body>
</html>