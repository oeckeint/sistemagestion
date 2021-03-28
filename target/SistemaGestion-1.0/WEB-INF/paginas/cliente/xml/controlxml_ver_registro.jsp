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
                                <dt class="list-group-item px-1">FechaSol</dt>
                                <dt class="list-group-item px-1">CUPS</dt>
                            </ul>
                        </div>
                        <div class="col-8 col-md-7">
                            <ul class="list-group list-group-flush">
                                <li class="list-group-item px-1">${documento.datosCabecera.codigoREEEmpresaEmisora}</li>
                                <li class="list-group-item px-1">${documento.datosCabecera.codigoREEEmpresaDestino}</li>
                                <li class="list-group-item px-1">${documento.datosCabecera.codigoDelProceso}</li>
                                <li class="list-group-item px-1">${documento.datosCabecera.codigoDePaso}</li>
                                <li class="list-group-item px-1">${documento.datosCabecera.codigoDeSolicitud}</li>
                                <li class="list-group-item px-1">${documento.datosCabecera.fechaSolicitud}</li>
                                <li class="list-group-item px-1">${documento.datosCabecera.cups}</li>
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
                                <dt class="list-group-item px-1">CodRec</dt>
                                <dt class="list-group-item px-1">Fecha</dt>
                                <dt class="list-group-item px-1">ProcedenciaD</dt>
                                <dt class="list-group-item px-1">ProcedenciaH</dt>
                            </ul>
                        </div>
                        <div class="col-7">
                            <ul class="list-group list-group-flush">
                                <li class="list-group-item px-1">${documento.datosGeneralesFactura.codigoFiscalFactura}</li>
                                <li class="list-group-item px-1">${documento.datosGeneralesFactura.tipoFactura}</li>
                                <li class="list-group-item px-1">${documento.datosGeneralesFactura.motivoFacturacion}</li>
                                <li class="list-group-item px-1">${documento.datosGeneralesFactura.codigoFacturaRectificadaAnulada}</li>
                                <li class="list-group-item px-1">${documento.datosGeneralesFactura.fechaFactura}</li>
                                
                                <c:choose>
                                    <c:when test="${ documento.datosAeProcedenciaDesde.procedencia == 10 }">
                                        <li class="list-group-item px-1">Telemedida</li>
                                    </c:when>
                                    <c:when test="${ documento.datosAeProcedenciaDesde.procedencia == 11 }">
                                        <li class="list-group-item px-1">TeleCorregida</li>
                                    </c:when>
                                    <c:when test="${ documento.datosAeProcedenciaDesde.procedencia == 20 }">
                                        <li class="list-group-item px-1">TPL</li>
                                    </c:when>
                                    <c:when test="${ documento.datosAeProcedenciaDesde.procedencia == 21 }">
                                        <li class="list-group-item px-1">TPL Corregida</li>
                                    </c:when>
                                    <c:when test="${ documento.datosAeProcedenciaDesde.procedencia == 30 }">
                                        <li class="list-group-item px-1">Visual</li>
                                    </c:when>
                                    <c:when test="${ documento.datosAeProcedenciaDesde.procedencia == 31 }">
                                        <li class="list-group-item px-1">Visual Corr</li>
                                    </c:when>
                                    <c:when test="${ documento.datosAeProcedenciaDesde.procedencia == 40 }">
                                        <li class="list-group-item px-1">Estimada</li>
                                    </c:when>     
                                    <c:when test="${ documento.datosAeProcedenciaDesde.procedencia == 50 }">
                                        <li class="list-group-item px-1">Autolectura</li>
                                    </c:when>
                                    <c:when test="${ documento.datosAeProcedenciaDesde.procedencia == 60 }">
                                        <li class="list-group-item px-1">CCH</li>
                                    </c:when>
                                    <c:otherwise>
                                        <li class="list-group-item px-1">Sin Lectura</li>
                                    </c:otherwise>
                                </c:choose>
                                
                                <c:choose>
                                    <c:when test="${ documento.datosAeProcedenciaHasta.procedencia == 10 }">
                                        <li class="list-group-item px-1">Telemedida</li>
                                    </c:when>
                                    <c:when test="${ documento.datosAeProcedenciaHasta.procedencia == 11 }">
                                        <li class="list-group-item px-1">TeleCorregida</li>
                                    </c:when>
                                    <c:when test="${ documento.datosAeProcedenciaHasta.procedencia == 20 }">
                                        <li class="list-group-item px-1">TPL</li>
                                    </c:when>
                                    <c:when test="${ documento.datosAeProcedenciaHasta.procedencia == 21 }">
                                        <li class="list-group-item px-1">TPL Corregida</li>
                                    </c:when>
                                    <c:when test="${ documento.datosAeProcedenciaHasta.procedencia == 30 }">
                                        <li class="list-group-item px-1">Visual</li>
                                    </c:when>
                                    <c:when test="${ documento.datosAeProcedenciaHasta.procedencia == 31 }">
                                        <li class="list-group-item px-1">Visual Corr</li>
                                    </c:when>
                                    <c:when test="${ documento.datosAeProcedenciaHasta.procedencia == 40 }">
                                        <li class="list-group-item px-1">Estimada</li>
                                    </c:when>     
                                    <c:when test="${ documento.datosAeProcedenciaHasta.procedencia == 50 }">
                                        <li class="list-group-item px-1">Autolectura</li>
                                    </c:when>
                                    <c:when test="${ documento.datosAeProcedenciaHasta.procedencia == 60 }">
                                        <li class="list-group-item px-1">CCH</li>
                                    </c:when>
                                    <c:otherwise>
                                        <li class="list-group-item px-1">Sin Lectura</li>
                                    </c:otherwise>
                                </c:choose>
                                
                            </ul>
                        </div>
                        </div>
                    </div>
                    <div class="list-group col-12 col-md-5 col-lg-4 p-2">
                        <h3 class="list-group-item list-group-item-action active text-center h4">Datos Factura ATR</h3>
                        <div class="row">
                            <div class="col-5">
                            <ul class="list-group list-group-flush text-right">
                                <dt class="list-group-item px-1">TarifaATRFact</dt>
                                <dt class="list-group-item px-1">ModoPotencia</dt>
                                <dt class="list-group-item px-1">MarcaMedida</dt>
                                <dt class="list-group-item px-1">VAsTrafo</dt>
                                <dt class="list-group-item px-1">Perdidas</dt>
                                <dt class="list-group-item px-1">Días</dt>
                            </ul>
                        </div>
                        <div class="col-7">
                            <ul class="list-group list-group-flush">
                                <li class="list-group-item px-1">${documento.datosFacturaAtr.tarifaAtrFact}</li>
                                <li class="list-group-item px-1">${documento.datosFacturaAtr.modoControlPotencia}</li>
                                <li class="list-group-item px-1">${documento.datosFacturaAtr.marcaMedidaConPerdidas}</li>
                                <li class="list-group-item px-1">${documento.datosFacturaAtr.VAsTrafo}</li>
                                <li class="list-group-item px-1">${documento.datosFacturaAtr.porcentajePerdidas}%</li>
                                <li class="list-group-item px-1">${documento.datosFacturaAtr.numeroDias}</li>
                            </ul>
                        </div>
                        </div>
                    </div>
                </div>
                <!--Segundo Renglón-->
                <hr>
                <h2 class="display-4">Potencia</h2>
                <hr>
                <div class="row justify-content-around">
                    <div class="list-group col-12 col-md-5 col-lg-4 p-2">
                        <h3 class="list-group-item list-group-item-action active text-center h4">Excesos Potencia</h3>
                        <div class="row">
                            <div class="col-5">
                            <ul class="list-group list-group-flush text-right">
                                <dt class="list-group-item px-1">Exceso 1</dt>
                                <dt class="list-group-item px-1">Exceso 2</dt>
                                <dt class="list-group-item px-1">Exceso 3</dt>
                                <dt class="list-group-item px-1">Exceso 4</dt>
                                <dt class="list-group-item px-1">Exceso 5</dt>
                                <dt class="list-group-item px-1">Exceso 6</dt>
                                <dt class="list-group-item px-1">Total Excesos</dt>
                            </ul>
                        </div>
                        <div class="col-7">
                            <ul class="list-group list-group-flush">
                                <li class="list-group-item px-1">${documento.datosExcesoPotencia.p1}</li>
                                <li class="list-group-item px-1">${documento.datosExcesoPotencia.p2}</li>
                                <li class="list-group-item px-1">${documento.datosExcesoPotencia.p3}</li>
                                <li class="list-group-item px-1">${documento.datosExcesoPotencia.p4}</li>
                                <li class="list-group-item px-1">${documento.datosExcesoPotencia.p5}</li>
                                <li class="list-group-item px-1">${documento.datosExcesoPotencia.p6}</li>
                                <li class="list-group-item px-1">${documento.datosExcesoPotencia.t}</li>
                            </ul>
                        </div>
                        </div>
                    </div>
                    <div class="list-group col-12 col-md-5 col-lg-4 p-2">
                        <h3 class="list-group-item list-group-item-action active text-center h4">Potencia Contratada</h3>
                        <div class="row">
                            <div class="col-5">
                            <ul class="list-group list-group-flush text-right">
                                <dt class="list-group-item px-1">Potencia 1</dt>
                                <dt class="list-group-item px-1">Potencia 2</dt>
                                <dt class="list-group-item px-1">Potencia 3</dt>
                                <dt class="list-group-item px-1">Potencia 4</dt>
                                <dt class="list-group-item px-1">Potencia 5</dt>
                                <dt class="list-group-item px-1">Potencia 6</dt>
                            </ul>
                        </div>
                        <div class="col-7">
                            <ul class="list-group list-group-flush">
                                <li class="list-group-item px-1">${documento.datosPotenciaContratada.p1}</li>
                                <li class="list-group-item px-1">${documento.datosPotenciaContratada.p2}</li>
                                <li class="list-group-item px-1">${documento.datosPotenciaContratada.p3}</li>
                                <li class="list-group-item px-1">${documento.datosPotenciaContratada.p4}</li>
                                <li class="list-group-item px-1">${documento.datosPotenciaContratada.p5}</li>
                                <li class="list-group-item px-1">${documento.datosPotenciaContratada.p6}</li>
                            </ul>
                        </div>
                        </div>
                    </div>
                    <div class="list-group col-12 col-md-5 col-lg-4 p-2">
                        <h3 class="list-group-item list-group-item-action active text-center h4">Potencia Max demandada</h3>
                        <div class="row">
                            <div class="col-5">
                            <ul class="list-group list-group-flush text-right">
                                <dt class="list-group-item px-1">Potencia 1</dt>
                                <dt class="list-group-item px-1">Potencia 2</dt>
                                <dt class="list-group-item px-1">Potencia 3</dt>
                                <dt class="list-group-item px-1">Potencia 4</dt>
                                <dt class="list-group-item px-1">Potencia 5</dt>
                                <dt class="list-group-item px-1">Potencia 6</dt>
                            </ul>
                        </div>
                        <div class="col-7">
                            <ul class="list-group list-group-flush">
                                <li class="list-group-item px-1">${documento.datosPotenciaMaxDemandada.p1}</li>
                                <li class="list-group-item px-1">${documento.datosPotenciaMaxDemandada.p2}</li>
                                <li class="list-group-item px-1">${documento.datosPotenciaMaxDemandada.p3}</li>
                                <li class="list-group-item px-1">${documento.datosPotenciaMaxDemandada.p4}</li>
                                <li class="list-group-item px-1">${documento.datosPotenciaMaxDemandada.p5}</li>
                                <li class="list-group-item px-1">${documento.datosPotenciaMaxDemandada.p6}</li>
                            </ul>
                        </div>
                        </div>
                    </div>
                </div>                
                <!--Tercer Renglón-->
                <div class="row justify-content-between">
                    <div class="list-group col-12 col-md-5 col-lg-4 p-2">
                        <h3 class="list-group-item list-group-item-action active text-center h4">Potencia a Facturar</h3>
                        <div class="row">
                            <div class="col-5">
                            <ul class="list-group list-group-flush text-right">
                                <dt class="list-group-item px-1">Potencia 1</dt>
                                <dt class="list-group-item px-1">Potencia 2</dt>
                                <dt class="list-group-item px-1">Potencia 3</dt>
                            </ul>
                        </div>
                        <div class="col-7">
                            <ul class="list-group list-group-flush">
                                <li class="list-group-item px-1">${documento.datosPotenciaAFacturar.p1}</li>
                                <li class="list-group-item px-1">${documento.datosPotenciaAFacturar.p2}</li>
                                <li class="list-group-item px-1">${documento.datosPotenciaAFacturar.p3}</li>
                            </ul>
                        </div>
                        </div>
                    </div>
                    <div class="list-group col-12 col-md-5 col-lg-4 p-2">
                        <h3 class="list-group-item list-group-item-action active text-center h4">Potencia Precio</h3>
                        <div class="row">
                            <div class="col-5">
                            <ul class="list-group list-group-flush text-right">
                                <dt class="list-group-item px-1">Precio 1</dt>
                                <dt class="list-group-item px-1">Precio 2</dt>
                                <dt class="list-group-item px-1">Precio 3</dt>
                                <dt class="list-group-item px-1">Precio 4</dt>
                                <dt class="list-group-item px-1">Precio 5</dt>
                                <dt class="list-group-item px-1">Precio 6</dt>
                                
                            </ul>
                        </div>
                        <div class="col-7">
                            <ul class="list-group list-group-flush">
                                <li class="list-group-item px-1">${documento.datosPotenciaPrecio.p1}</li>
                                <li class="list-group-item px-1">${documento.datosPotenciaPrecio.p2}</li>
                                <li class="list-group-item px-1">${documento.datosPotenciaPrecio.p3}</li>
                                <li class="list-group-item px-1">${documento.datosPotenciaPrecio.p4}</li>
                                <li class="list-group-item px-1">${documento.datosPotenciaPrecio.p5}</li>
                                <li class="list-group-item px-1">${documento.datosPotenciaPrecio.p6}</li>
                            </ul>
                        </div>
                        </div>
                    </div>
                    <div class="list-group col-12 col-md-5 col-lg-4 p-2">
                        <h3 class="list-group-item list-group-item-action active text-center h4">Potencia Importe Total</h3>
                        <div class="row">
                            <div class="col-5">
                            <ul class="list-group list-group-flush text-right">
                                <dt class="list-group-item px-1">Importe</dt>
                            </ul>
                        </div>
                        <div class="col-7">
                            <ul class="list-group list-group-flush">
                                <li class="list-group-item px-1">${documento.datosPotenciaImporteTotal.importeTotal}</li>
                            </ul>
                        </div>
                        </div>
                    </div>
                </div>
                <!--Cuarto Renglón-->
                <hr>
                <h2 class="display-4">Energía Activa</h2>
                <hr>
                <div class="row justify-content-around">
                    <div class="list-group col-12 col-md-5 col-lg-4 p-2">
                        <h3 class="list-group-item list-group-item-action active text-center h4"><!--Datos-->Fechas</h3>
                        <div class="row">
                            <div class="col-5">
                            <ul class="list-group list-group-flush text-right">
                                <dt class="list-group-item px-1">Desde 1</dt>
                                <dt class="list-group-item px-1">Hasta 1</dt>
                                <dt class="list-group-item px-1">Desde 2</dt>
                                <dt class="list-group-item px-1">Hasta 2</dt>
                            </ul>
                        </div>
                        <div class="col-7">
                            <ul class="list-group list-group-flush">
                                <li class="list-group-item px-1">${documento.datosEnergiaActiva.fechaDesde1}</li>
                                <li class="list-group-item px-1">${documento.datosEnergiaActiva.fechaHasta1}</li>
                                <li class="list-group-item px-1">${documento.datosEnergiaActiva.fechaDesde2}</li>
                                <li class="list-group-item px-1">${documento.datosEnergiaActiva.fechaHasta2}</li>
                            </ul>
                        </div>
                        </div>
                    </div>
                    <div class="list-group col-12 col-md-5 col-lg-4 p-2">
                        <h3 class="list-group-item list-group-item-action active text-center h4">Valores</h3>
                        <div class="row">
                            <div class="col-5">
                            <ul class="list-group list-group-flush text-right">
                                <dt class="list-group-item px-1">Valor 1</dt>
                                <dt class="list-group-item px-1">Valor 2</dt>
                                <dt class="list-group-item px-1">Valor 3</dt>
                                <dt class="list-group-item px-1">Valor 4</dt>
                                <dt class="list-group-item px-1">Valor 5</dt>
                                <dt class="list-group-item px-1">Valor 6</dt>
                                <dt class="list-group-item px-1">Suma</dt>
                            </ul>
                        </div>
                        <div class="col-7">
                            <ul class="list-group list-group-flush">
                                <li class="list-group-item px-1">${documento.datosEnergiaActivaValores.v1}</li>
                                <li class="list-group-item px-1">${documento.datosEnergiaActivaValores.v2}</li>
                                <li class="list-group-item px-1">${documento.datosEnergiaActivaValores.v3}</li>
                                <li class="list-group-item px-1">${documento.datosEnergiaActivaValores.v4}</li>
                                <li class="list-group-item px-1">${documento.datosEnergiaActivaValores.v5}</li>
                                <li class="list-group-item px-1">${documento.datosEnergiaActivaValores.v6}</li>
                                <li class="list-group-item px-1">${documento.datosEnergiaActivaValores.total}</li>
                            </ul>
                        </div>
                        </div>
                    </div>
                    <div class="list-group col-12 col-md-5 col-lg-4 p-2">
                        <h3 class="list-group-item list-group-item-action active text-center h4">Precio</h3>
                        <div class="row">
                            <div class="col-5">
                            <ul class="list-group list-group-flush text-right">
                                <dt class="list-group-item px-1">Precio 1</dt>
                                <dt class="list-group-item px-1">Precio 2</dt>
                                <dt class="list-group-item px-1">Precio 3</dt>
                                <dt class="list-group-item px-1">Precio 4</dt>
                                <dt class="list-group-item px-1">Precio 5</dt>
                                <dt class="list-group-item px-1">Precio 6</dt>
                            </ul>
                        </div>
                        <div class="col-7">
                            <ul class="list-group list-group-flush">
                                <li class="list-group-item px-1">${documento.datosEnergiaActivaPrecio.p1}</li>
                                <li class="list-group-item px-1">${documento.datosEnergiaActivaPrecio.p2}</li>
                                <li class="list-group-item px-1">${documento.datosEnergiaActivaPrecio.p3}</li>
                                <li class="list-group-item px-1">${documento.datosEnergiaActivaPrecio.p4}</li>
                                <li class="list-group-item px-1">${documento.datosEnergiaActivaPrecio.p5}</li>
                                <li class="list-group-item px-1">${documento.datosEnergiaActivaPrecio.p6}</li>
                            </ul>
                        </div>
                        </div>
                    </div>
                </div>
                            
                            
                <div class="row justify-content-around">
                    <div class="list-group col-12 col-md-5 col-lg-4 p-2">
                        <h3 class="list-group-item list-group-item-action active text-center h4">Importe Total</h3>
                        <div class="row">
                            <div class="col-5">
                            <ul class="list-group list-group-flush text-right">
                                <dt class="list-group-item px-1">Importe</dt>
                            </ul>
                        </div>
                        <div class="col-7">
                            <ul class="list-group list-group-flush">
                                <li class="list-group-item px-1">${documento.datosEnergiaActivaImporteTotal.importeTotal}</li>
                            </ul>
                        </div>
                        </div>
                    </div>
                </div>
                            
                <!-- Renglón-->
                <hr>
                <h2 class="display-4">Otros Datos</h2>
                <hr>
                <div class="row justify-content-around">
                    <div class="list-group col-12 col-md-5 col-lg-4 p-2">
                        <h3 class="list-group-item list-group-item-action active text-center h4">Impuesto Eléctrico</h3>
                        <div class="row">
                            <div class="col-5">
                            <ul class="list-group list-group-flush text-right">
                                <dt class="list-group-item px-1">Importe</dt>
                            </ul>
                        </div>
                        <div class="col-7">
                            <ul class="list-group list-group-flush">
                                <li class="list-group-item px-1">${documento.datosImpuestoElectrico.importe}</li>
                            </ul>
                        </div>
                        </div>
                    </div>
                    <div class="list-group col-12 col-md-5 col-lg-4 p-2">
                        <h3 class="list-group-item list-group-item-action active text-center h4">Alquileres</h3>
                        <div class="row">
                            <div class="col-5">
                            <ul class="list-group list-group-flush text-right">
                                <dt class="list-group-item px-1">Importe</dt>
                            </ul>
                        </div>
                        <div class="col-7">
                            <ul class="list-group list-group-flush">
                                <li class="list-group-item px-1">${documento.datosAlquileres.importe}</li>
                            </ul>
                        </div>
                        </div>
                    </div>
                    <div class="list-group col-12 col-md-5 col-lg-4 p-2">
                        <h3 class="list-group-item list-group-item-action active text-center h4">IVA</h3>
                        <div class="row">
                            <div class="col-5">
                            <ul class="list-group list-group-flush text-right">
                                <dt class="list-group-item px-1">BaseImp</dt>
                            </ul>
                        </div>
                        <div class="col-7">
                            <ul class="list-group list-group-flush">
                                <li class="list-group-item px-1">${documento.datosIva.baseImponible}</li>
                            </ul>
                        </div>
                        </div>
                    </div>
                </div>
                            
                           
                <!-- Renglón-->
                <hr>
                <h2 class="display-4">AE</h2>
                <hr>
                <div class="row justify-content-around">
                    <div class="list-group col-12 col-md-5 col-lg-4 p-2">
                        <h3 class="list-group-item list-group-item-action active text-center h4">AE Consumo</h3>
                        <div class="row">
                            <div class="col-5">
                            <ul class="list-group list-group-flush text-right">
                                <dt class="list-group-item px-1">Consumo 1</dt>
                                <dt class="list-group-item px-1">Consumo 2</dt>
                                <dt class="list-group-item px-1">Consumo 3</dt>
                                <dt class="list-group-item px-1">Consumo 4</dt>
                                <dt class="list-group-item px-1">Consumo 5</dt>
                                <dt class="list-group-item px-1">Consumo 6</dt>
                                <dt class="list-group-item px-1">Suma</dt>
                            </ul>
                        </div>
                        <div class="col-7">
                            <ul class="list-group list-group-flush">
                                <li class="list-group-item px-1">${documento.datosAeConsumo.consumoCalculado1}</li>
                                <li class="list-group-item px-1">${documento.datosAeConsumo.consumoCalculado2}</li>
                                <li class="list-group-item px-1">${documento.datosAeConsumo.consumoCalculado3}</li>
                                <li class="list-group-item px-1">${documento.datosAeConsumo.consumoCalculado4}</li>
                                <li class="list-group-item px-1">${documento.datosAeConsumo.consumoCalculado5}</li>
                                <li class="list-group-item px-1">${documento.datosAeConsumo.consumoCalculado6}</li>
                                <li class="list-group-item px-1">${documento.datosAeConsumo.suma}</li>
                            </ul>
                        </div>
                        </div>
                    </div>
                    <div class="list-group col-12 col-md-5 col-lg-4 p-2">
                        <h3 class="list-group-item list-group-item-action active text-center h4">AE Lectura desde</h3>
                        <div class="row">
                            <div class="col-5">
                            <ul class="list-group list-group-flush text-right">
                                <dt class="list-group-item px-1">Lectura 1</dt>
                                <dt class="list-group-item px-1">Lectura 2</dt>
                                <dt class="list-group-item px-1">Lectura 3</dt>
                                <dt class="list-group-item px-1">Lectura 4</dt>
                                <dt class="list-group-item px-1">Lectura 5</dt>
                                <dt class="list-group-item px-1">Lectura 6</dt>
                            </ul>
                        </div>
                        <div class="col-7">
                            <ul class="list-group list-group-flush">
                                <li class="list-group-item px-1">${documento.datosAeLecturaDesde.lectura1}</li>
                                <li class="list-group-item px-1">${documento.datosAeLecturaDesde.lectura2}</li>
                                <li class="list-group-item px-1">${documento.datosAeLecturaDesde.lectura3}</li>
                                <li class="list-group-item px-1">${documento.datosAeLecturaDesde.lectura4}</li>
                                <li class="list-group-item px-1">${documento.datosAeLecturaDesde.lectura5}</li>
                                <li class="list-group-item px-1">${documento.datosAeLecturaDesde.lectura6}</li>
                            </ul>
                        </div>
                        </div>
                    </div>
                    <div class="list-group col-12 col-md-5 col-lg-4 p-2">
                        <h3 class="list-group-item list-group-item-action active text-center h4">AE Lectura Hasta</h3>
                        <div class="row">
                            <div class="col-5">
                            <ul class="list-group list-group-flush text-right">
                                <dt class="list-group-item px-1">Lectura 1</dt>
                                <dt class="list-group-item px-1">Lectura 2</dt>
                                <dt class="list-group-item px-1">Lectura 3</dt>
                                <dt class="list-group-item px-1">Lectura 4</dt>
                                <dt class="list-group-item px-1">Lectura 5</dt>
                                <dt class="list-group-item px-1">Lectura 6</dt>
                            </ul>
                        </div>
                        <div class="col-7">
                            <ul class="list-group list-group-flush">
                                <li class="list-group-item px-1">${documento.datosAeLecturaHasta.lectura1}</li>
                                <li class="list-group-item px-1">${documento.datosAeLecturaHasta.lectura2}</li>
                                <li class="list-group-item px-1">${documento.datosAeLecturaHasta.lectura3}</li>
                                <li class="list-group-item px-1">${documento.datosAeLecturaHasta.lectura4}</li>
                                <li class="list-group-item px-1">${documento.datosAeLecturaHasta.lectura5}</li>
                                <li class="list-group-item px-1">${documento.datosAeLecturaHasta.lectura6}</li>
                            </ul>
                        </div>
                        </div>
                    </div>
                </div>
                            
                <!-- Renglón-->
                <hr>
                <h2 class="display-4">Reactiva</h2>
                <hr>
                <div class="row justify-content-around">
                    <div class="list-group col-12 col-md-5 col-lg-4 p-2">
                        <h3 class="list-group-item list-group-item-action active text-center h4">R Consumo</h3>
                        <div class="row">
                            <div class="col-5">
                            <ul class="list-group list-group-flush text-right">
                                <dt class="list-group-item px-1">Consumo 1</dt>
                                <dt class="list-group-item px-1">Consumo 2</dt>
                                <dt class="list-group-item px-1">Consumo 3</dt>
                                <dt class="list-group-item px-1">Consumo 4</dt>
                                <dt class="list-group-item px-1">Consumo 5</dt>
                                <dt class="list-group-item px-1">Consumo 6</dt>
                                <dt class="list-group-item px-1">Suma</dt>
                            </ul>
                        </div>
                        <div class="col-7">
                            <ul class="list-group list-group-flush">
                                <li class="list-group-item px-1">${documento.datosRConsumo.consumo1}</li>
                                <li class="list-group-item px-1">${documento.datosRConsumo.consumo2}</li>
                                <li class="list-group-item px-1">${documento.datosRConsumo.consumo3}</li>
                                <li class="list-group-item px-1">${documento.datosRConsumo.consumo4}</li>
                                <li class="list-group-item px-1">${documento.datosRConsumo.consumo5}</li>
                                <li class="list-group-item px-1">${documento.datosRConsumo.consumo6}</li>
                                <li class="list-group-item px-1">${documento.datosRConsumo.suma}</li>
                            </ul>
                        </div>
                        </div>
                    </div>
                    <div class="list-group col-12 col-md-5 col-lg-4 p-2">
                        <h3 class="list-group-item list-group-item-action active text-center h4">R Lectura desde</h3>
                        <div class="row">
                            <div class="col-5">
                            <ul class="list-group list-group-flush text-right">
                                <dt class="list-group-item px-1">Lectura 1</dt>
                                <dt class="list-group-item px-1">Lectura 2</dt>
                                <dt class="list-group-item px-1">Lectura 3</dt>
                                <dt class="list-group-item px-1">Lectura 4</dt>
                                <dt class="list-group-item px-1">Lectura 5</dt>
                                <dt class="list-group-item px-1">Lectura 6</dt>
                            </ul>
                        </div>
                        <div class="col-7">
                            <ul class="list-group list-group-flush">
                                <li class="list-group-item px-1">${documento.datosRLecturaDesde.lectura1}</li>
                                <li class="list-group-item px-1">${documento.datosRLecturaDesde.lectura2}</li>
                                <li class="list-group-item px-1">${documento.datosRLecturaDesde.lectura3}</li>
                                <li class="list-group-item px-1">${documento.datosRLecturaDesde.lectura4}</li>
                                <li class="list-group-item px-1">${documento.datosRLecturaDesde.lectura5}</li>
                                <li class="list-group-item px-1">${documento.datosRLecturaDesde.lectura6}</li>
                            </ul>
                        </div>
                        </div>
                    </div>
                    <div class="list-group col-12 col-md-5 col-lg-4 p-2">
                        <h3 class="list-group-item list-group-item-action active text-center h4">R Lectura Hasta</h3>
                        <div class="row">
                            <div class="col-5">
                            <ul class="list-group list-group-flush text-right">
                                <dt class="list-group-item px-1">Lectura 1</dt>
                                <dt class="list-group-item px-1">Lectura 2</dt>
                                <dt class="list-group-item px-1">Lectura 3</dt>
                                <dt class="list-group-item px-1">Lectura 4</dt>
                                <dt class="list-group-item px-1">Lectura 5</dt>
                                <dt class="list-group-item px-1">Lectura 6</dt>
                            </ul>
                        </div>
                        <div class="col-7">
                            <ul class="list-group list-group-flush">
                                <li class="list-group-item px-1">${documento.datosRLecturaHasta.lectura1}</li>
                                <li class="list-group-item px-1">${documento.datosRLecturaHasta.lectura2}</li>
                                <li class="list-group-item px-1">${documento.datosRLecturaHasta.lectura3}</li>
                                <li class="list-group-item px-1">${documento.datosRLecturaHasta.lectura4}</li>
                                <li class="list-group-item px-1">${documento.datosRLecturaHasta.lectura5}</li>
                                <li class="list-group-item px-1">${documento.datosRLecturaHasta.lectura6}</li>
                            </ul>
                        </div>
                        </div>
                    </div>
                </div>
                            
                <div class="row justify-content-around">
                    <div class="list-group col-12 col-md-5 col-lg-4 p-2">
                        <h3 class="list-group-item list-group-item-action active text-center h4">Importe Total</h3>
                        <div class="row">
                            <div class="col-5">
                            <ul class="list-group list-group-flush text-right">
                                <dt class="list-group-item px-1">Importe</dt>
                            </ul>
                        </div>
                        <div class="col-7">
                            <ul class="list-group list-group-flush">
                                <li class="list-group-item px-1">${documento.reactivaImporteTotal.importeTotal}</li>
                            </ul>
                        </div>
                        </div>
                    </div>
                </div>
                            
                            
                <!-- Renglón-->
                <hr>
                <h2 class="display-4">PM</h2>
                <hr>
                <div class="row justify-content-around">
                    <div class="list-group col-12 col-md-5 col-lg-4 p-2">
                        <h3 class="list-group-item list-group-item-action active text-center h4">PM Consumo</h3>
                        <div class="row">
                            <div class="col-5">
                            <ul class="list-group list-group-flush text-right">
                                <dt class="list-group-item px-1">Consumo 1</dt>
                                <dt class="list-group-item px-1">Consumo 2</dt>
                                <dt class="list-group-item px-1">Consumo 3</dt>
                                <dt class="list-group-item px-1">Consumo 4</dt>
                                <dt class="list-group-item px-1">Consumo 5</dt>
                                <dt class="list-group-item px-1">Consumo 6</dt>
                                <dt class="list-group-item px-1">Suma</dt>
                            </ul>
                        </div>
                        <div class="col-7">
                            <ul class="list-group list-group-flush">
                                <li class="list-group-item px-1">${documento.datosPmConsumo.consumo1}</li>
                                <li class="list-group-item px-1">${documento.datosPmConsumo.consumo2}</li>
                                <li class="list-group-item px-1">${documento.datosPmConsumo.consumo3}</li>
                                <li class="list-group-item px-1">${documento.datosPmConsumo.consumo4}</li>
                                <li class="list-group-item px-1">${documento.datosPmConsumo.consumo5}</li>
                                <li class="list-group-item px-1">${documento.datosPmConsumo.consumo6}</li>
                                <li class="list-group-item px-1">${documento.datosPmConsumo.suma}</li>
                            </ul>
                        </div>
                        </div>
                    </div>
                    <div class="list-group col-12 col-md-5 col-lg-4 p-2">
                        <h3 class="list-group-item list-group-item-action active text-center h4">PM Lectura Hasta</h3>
                        <div class="row">
                            <div class="col-5">
                                <ul class="list-group list-group-flush text-right">
                                    <dt class="list-group-item px-1">Lectura 1</dt>
                                    <dt class="list-group-item px-1">Lectura 2</dt>
                                    <dt class="list-group-item px-1">Lectura 3</dt>
                                    <dt class="list-group-item px-1">Lectura 4</dt>
                                    <dt class="list-group-item px-1">Lectura 5</dt>
                                    <dt class="list-group-item px-1">Lectura 6</dt>
                                </ul>
                            </div>
                            <div class="col-7">
                                <ul class="list-group list-group-flush">
                                    <li class="list-group-item px-1">${documento.datosPmLecturaHasta.lectura1}</li>
                                    <li class="list-group-item px-1">${documento.datosPmLecturaHasta.lectura2}</li>
                                    <li class="list-group-item px-1">${documento.datosPmLecturaHasta.lectura3}</li>
                                    <li class="list-group-item px-1">${documento.datosPmLecturaHasta.lectura4}</li>
                                    <li class="list-group-item px-1">${documento.datosPmLecturaHasta.lectura5}</li>
                                    <li class="list-group-item px-1">${documento.datosPmLecturaHasta.lectura6}</li>
                                </ul>
                            </div>
                        </div>
                    </div>
                </div>
                                
                                
                <!-- Renglón-->
                <hr>
                <h2 class="display-4">Fin de Registro</h2>
                <hr>
                <div class="row justify-content-around">
                    <div class="list-group col-12 col-md-5 col-lg-4 p-2">
                        <h3 class="list-group-item list-group-item-action active text-center h4">Datos</h3>
                        <div class="row">
                            <div class="col-5">
                            <ul class="list-group list-group-flush text-right">
                                <dt class="list-group-item px-1">ImporteTot</dt>
                                <dt class="list-group-item px-1">SaldoFact</dt>
                                <dt class="list-group-item px-1">TotalRecibos</dt>
                                <dt class="list-group-item px-1">FechaValor</dt>
                                <dt class="list-group-item px-1">FechaLimite</dt>
                                <dt class="list-group-item px-1">Remesa</dt>
                            </ul>
                        </div>
                        <div class="col-7">
                            <ul class="list-group list-group-flush">
                                <li class="list-group-item px-1">${documento.datosFinDeRegistro.importeTotal}</li>
                                <li class="list-group-item px-1">${documento.datosFinDeRegistro.saldoTotalFacturacion}</li>
                                <li class="list-group-item px-1">${documento.datosFinDeRegistro.totalRecibos}</li>
                                <li class="list-group-item px-1">${documento.datosFinDeRegistro.fechaValor}</li>
                                <li class="list-group-item px-1">${documento.datosFinDeRegistro.fechaLimitePago}</li>
                                <li class="list-group-item px-1">${documento.datosFinDeRegistro.idRemesa}</li>
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