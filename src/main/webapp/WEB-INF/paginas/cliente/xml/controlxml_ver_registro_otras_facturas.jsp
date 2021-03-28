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
                <strong>${mensajeRegistro}</strong>
            </div>
        </div>
            <c:if test="${ documento != null }">
            <div class="container">
                
                <div class="card row">
                    <ul class="list-group list-group-flush">
                        <li class="list-group-item"><h3>Comentarios</h3> ${documento.comentarios}</li>
                    <c:choose>
                        <c:when test="${ documento.errores != 0}">
                        <li class="list-group-item"><h5>Se encontraron errores importantes</h5> Revisar codigos:  <Strong>${documento.errores}</strong></li>
                        </c:when>
                    </c:choose>
                    </ul>
                </div>
                         
                <!--Primer Renglón-->
                <hr>
                <h2 class="display-4">Factura</h2>
                <hr>
                <div class="row justify-content-around">
                    <div class="list-group col-12 col-md-5 col-lg-4 p-2">
                        <h3 class="list-group-item list-group-item-action active text-center h4">Cabecera</h3>
                        <div class="row">
                            <div class="col-3 col-md-4">
                            <ul class="list-group list-group-flush text-right">
                                <dt class="list-group-item px-1">EmpEmisora</dt>
                                <dt class="list-group-item px-1">EmpDestino</dt>
                                <dt class="list-group-item px-1">Proceso</dt>
                                <dt class="list-group-item px-1">Paso</dt>
                                <dt class="list-group-item px-1">Solicitud</dt>
                                <dt class="list-group-item px-1">CUPS</dt>
                            </ul>
                        </div>
                        <div class="col-8 col-md-7">
                            <ul class="list-group list-group-flush">
                                <li class="list-group-item px-1">${documento.cabecera.codEmpEmi}</li>
                                <li class="list-group-item px-1">${documento.cabecera.codEmpDes}</li>
                                <li class="list-group-item px-1">${documento.cabecera.codPro}</li>
                                <li class="list-group-item px-1">${documento.cabecera.codPas}</li>
                                <li class="list-group-item px-1">${documento.cabecera.codSol}</li>
                                <li class="list-group-item px-1">${documento.cabecera.cups}</li>
                            </ul>
                        </div>
                        </div>
                    </div>
                    <div class="list-group col-12 col-md-5 col-lg-4 p-2">
                        <h3 class="list-group-item list-group-item-action active text-center h4">Datos Generales</h3>
                        <div class="row">
                            <div class="col-5">
                            <ul class="list-group list-group-flush text-right">
                                <dt class="list-group-item px-1">CodFiscal</dt>
                                <dt class="list-group-item px-1">TipoFactura</dt>
                                <dt class="list-group-item px-1">Motivo</dt>
                                <dt class="list-group-item px-1">Fecha</dt>
                                <dt class="list-group-item px-1">Comentarios</dt>
                                <dt class="list-group-item px-1">ImpTotal</dt>
                            </ul>
                        </div>
                        <div class="col-7">
                            <ul class="list-group list-group-flush">
                                <li class="list-group-item px-1">${documento.datosGeneralesFactura.codFisFac}</li>
                                <li class="list-group-item px-1">${documento.datosGeneralesFactura.tipFac}</li>
                                <li class="list-group-item px-1">${documento.datosGeneralesFactura.motFac}</li>
                                <li class="list-group-item px-1">${documento.datosGeneralesFactura.fecFac}</li>
                                <li class="list-group-item px-1">${documento.datosGeneralesFactura.com}</li>
                                <li class="list-group-item px-1">${documento.datosGeneralesFactura.impTotFac}</li>                              
                            </ul>
                        </div>
                        </div>
                    </div>
                    <div class="list-group col-12 col-md-5 col-lg-4 p-2">
                        <h3 class="list-group-item list-group-item-action active text-center h4">Repercutibles</h3>
                        <div class="row">
                            <div class="col-5">
                            <ul class="list-group list-group-flush text-right">
                                <dt class="list-group-item px-1">ConRepercutible</dt>
                                <dt class="list-group-item px-1">ImpTot</dt>
                            </ul>
                        </div>
                        <div class="col-7">
                            <ul class="list-group list-group-flush">
                                <li class="list-group-item px-1">${documento.conceptoRepercutible.conRep}</li>
                                <li class="list-group-item px-1">${documento.conceptoRepercutible.impTot}</li>
                            </ul>
                        </div>
                        </div>
                    </div>
                </div>
                            
                <!-- Renglón-->
                <hr>
                <div class="row justify-content-around">
                    <div class="list-group col-12 col-md-5 col-lg-4 p-2">
                        <h3 class="list-group-item list-group-item-action active text-center h4">Excesos Potencia</h3>
                        <div class="row">
                            <div class="col-5">
                            <ul class="list-group list-group-flush text-right">
                                <dt class="list-group-item px-1">Remesa</dt>
                            </ul>
                        </div>
                        <div class="col-7">
                            <ul class="list-group list-group-flush">
                                <li class="list-group-item px-1">${documento.registroFin.idRemesa}</li>
                            </ul>
                        </div>
                        </div>
                    </div>
                    <div class="list-group col-12 col-md-5 col-lg-4 p-2">
                            <h3 class="list-group-item list-group-item-action active text-center h4">Cliente</h3>
                            <div class="row">
                            <div class="col-5">
                                <ul class="list-group list-group-flush text-right">
                                    <dt class="list-group-item px-1">Identificador</dt>
                                    <dt class="list-group-item px-1">CUPS</dt>
                                    <dt class="list-group-item px-1">Nombre</dt>
                                    <dt class="list-group-item px-1">Tarifa</dt>
                                </ul>
                            </div>
                                <div class="col-7">
                                    <ul class="list-group list-group-flush">
                                        <li class="list-group-item px-1">${documento.cliente.idCliente}</li>
                                        <li class="list-group-item px-1">${documento.cliente.cups}</li>
                                        <li class="list-group-item px-1">${documento.cliente.nombreCliente}</li>
                                        <li class="list-group-item px-1">${documento.cliente.tarifa}</li>
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