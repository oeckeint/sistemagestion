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
            <c:if test="${ documento != null }">
            <div class="container">
                <hr>
                <div class="row justify-content-between p-0">
                    <div class="col-6">
                        <h2 class="m-0"><a href="javascript:history.back();"><i class="fas fa-arrow-circle-left text-success"></i></a> Factura <span class="badge badge-danger">Improved!</span></h2>
                    </div>
                    <div class="col-6 row justify-content-end">
                        <div class="col-3">
                            <a href="${pageContext.request.contextPath}/clasificar" class="btn btn-primary btn-block"><i class="fas fa-plus"></i> Clasificar</a>
                        </div>
                        <div class="col-3">
                            <a href="${pageContext.request.contextPath}/procesar" class="btn btn-primary btn-block"><i class="fas fa-plus"></i> Procesar</a>
                        </div>
                        <div class="col-5">
                            <form action="${pageContext.request.contextPath}/${controller}/busqueda" method="post" id="myForm">
                                <div class="input-group mb-2">
                                    <div class="input-group-prepend">
                                        <button class="btn btn-primary" type="submit" id="btnSubmit"><i class="fas fa-search"></i></button>
                                    </div>
                                    <input type="text"  name="valor" class="form-control" id="inlineFormInputGroup" placeholder="Buscar" value="${ultimaBusqueda}" required>
                                    <select class="form-select fa" name="filtro">
                                        <option class="fa" value="cliente">&#xf007;</option>
                                        <option class="fa" value="remesa">&#xf621;</option>
                                        <option class="fa" value="codFisFac" selected="true">&#xf15c;</option>
                                    </select>
                                </div>
                            </form>
                        </div>
                    </div>
                </div>
                <hr>
                <div class="card row">
                    <ul class="list-group list-group-flush">
                        <li class="list-group-item"><h3>Comentarios</h3> ${documento.com}</li>
                        <c:if test="${documento.idErr != ''}">
                            <li class="list-group-item"><h5>Se encontraron errores importantes</h5> Revisar codigos:  <Strong>${documento.idErr}</strong></li>
                        </c:if>
                    </ul>
                </div>
                         
                <!--Primer Renglón-->
                <div class="row justify-content-around">
                    <div class="list-group col-12 col-md-5 col-lg-4 p-2">
                        <h3 class="list-group-item list-group-item-action active text-center h4">Cabecera</h3>
                        <div class="row">
                            <div class="col-3 col-md-4">
                            <ul class="list-group list-group-flush text-right">
                                    <dt class="list-group-item px-1">Emisora</dt>
                                    <dt class="list-group-item px-1">Destino</dt>
                                <dt class="list-group-item px-1">Proceso</dt>
                                <dt class="list-group-item px-1">Paso</dt>
                                <dt class="list-group-item px-1">Solicitud</dt>
                                <dt class="list-group-item px-1">CUPS</dt>
                            </ul>
                        </div>
                        <div class="col-8 col-md-7">
                            <ul class="list-group list-group-flush">
                                <li class="list-group-item px-1">${documento.codEmpEmi}</li>
                                <li class="list-group-item px-1">${documento.codEmpDes}</li>
                                <li class="list-group-item px-1">${documento.codPro}</li>
                                <li class="list-group-item px-1">${documento.codPas}</li>
                                <li class="list-group-item px-1">${documento.codSol}</li>
                                <li class="list-group-item px-1">${documento.cups}</li>
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
                                <dt class="list-group-item px-1">ImporteTotal</dt>
                                <dt class="list-group-item px-1">TipoFactura</dt>
                                <dt class="list-group-item px-1">Motivo</dt>
                                <dt class="list-group-item px-1">Fecha</dt>
                                <dt class="list-group-item px-1">ImpTotal</dt>
                            </ul>
                        </div>
                        <div class="col-7">
                            <ul class="list-group list-group-flush">
                                <li class="list-group-item px-1">${documento.codFisFac}</li>
                                <li class="list-group-item px-1">${documento.impTotFac}</li>
                                <li class="list-group-item px-1">${documento.tipFac}</li>
                                <li class="list-group-item px-1">${documento.motFac}</li>
                                <li class="list-group-item px-1">${documento.fecFac}</li>
                                <li class="list-group-item px-1">${documento.rfImpTot}</li>                              
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
                                <li class="list-group-item px-1">${documento.conRep}</li>
                                <li class="list-group-item px-1">${documento.impTotConRep}</li>
                            </ul>
                        </div>
                        </div>
                    </div>
                </div>
                            
                <!-- Renglón-->
                <hr>
                <div class="row justify-content-around">
                    <div class="list-group col-12 col-md-5 col-lg-4 p-2">
                        <h3 class="list-group-item list-group-item-action active text-center h4">Fin Registro</h3>
                        <div class="row">
                            <div class="col-5">
                            <ul class="list-group list-group-flush text-right">
                                <dt class="list-group-item px-1">Remesa</dt>
                                <dt class="list-group-item px-1">Remesa pago</dt>
                                <dt class="list-group-item px-1">Estado pago</dt>
                            </ul>
                        </div>
                        <div class="col-7">
                            <ul class="list-group list-group-flush">
                                <li class="list-group-item px-1">${documento.idRem}</li>
                                <li class="list-group-item px-1">${documento.remesaPago}</li>
                                <li class="list-group-item px-1">${documento.estadoPago}</li>
                            </ul>
                        </div>
                        </div>
                    </div>
                    <c:url var="detalles" value="/clientes/detalles">
                            <c:param name="idCliente" value="${cliente.idCliente}"/>
                    </c:url>
                    <div class="list-group col-12 col-md-5 col-lg-4 p-2">
                            <h3 class="list-group-item list-group-item-action active text-center h4">Cliente <a href="${detalles}" class="btn btn-danger"><i class="fas fa-eye"></i></a> <span class="badge badge-danger">New!</span></h3>
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
                                        <li class="list-group-item px-1">${cliente.idCliente}</li>
                                        <li class="list-group-item px-1">${cliente.cups}</li>
                                        <li class="list-group-item px-1">${cliente.nombreCliente}</li>
                                        <li class="list-group-item px-1">${cliente.tarifa}</li>
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