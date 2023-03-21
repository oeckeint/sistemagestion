<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@taglib prefix="security" uri="http://www.springframework.org/security/tags" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<c:choose>
    <c:when test="${documento.class.simpleName eq 'Factura'}">
        <c:set var="isFactura" value="true"/>
    </c:when>
    <c:when test="${documento.class.simpleName eq 'Peaje'}">
        <c:set var="isPeaje" value="true"/>
    </c:when>
</c:choose>
<!DOCTYPE html>
<html>
    <head>
        <jsp:include page="/WEB-INF/paginas/comunes/contenidoHead.jsp"></jsp:include>
        </head>
        <body>
        <jsp:include page="/WEB-INF/paginas/comunes/cabecero.jsp"></jsp:include>
            <div class="alert alert-warning alert-dismissible fade show" role="alert">
                <div class="container">
                	<c:choose>
                		<c:when test="${!clienteEncontrado}">
            				<fmt:message key="customers.tickets.create1"></fmt:message>
            			</c:when>
            			<c:when test="${clienteEncontrado}">
            				<fmt:message key="customers.tickets.create1"></fmt:message>
            			</c:when>
            			<c:otherwise>
            				<fmt:message key="customers.tickets.edit"></fmt:message>
            			</c:otherwise>
        			</c:choose>
            	</div>
        </div>
        <c:if test="${ documento != null }">
            <div class="container">
                <hr>
                <div class="row justify-content-between p-0">
                    <div class="col-6">
                        <h2 class="m-0">
                            <a id="backList" href="${pageContext.request.contextPath}/facturas"><i class="fas fa-arrow-circle-left text-success"></i></a>
                            <c:choose>
                                <c:when test="${isFactura}">Factura</c:when>
                                <c:when test="${isPeaje}">Peaje</c:when>
                            </c:choose>
                        </h2>
                    </div>
                    <div class="col-6 row justify-content-end">
                        <security:authorize access="hasRole('ADMIN')">
                            <div class="col-1">
                                <c:choose>
                                    <c:when test="${documento.isDeleted == 0}">
                                        <a href="${pageContext.request.contextPath}/${controller}/archivar?codFisFac=${documento.codFisFac}" class="btn btn-danger m-0" id="linkClasify"><i class="fa-solid fa-eye-slash"></i></a>
                                    </c:when>
                                    <c:when test="${documento.isDeleted == 1}">
                                        <a href="${pageContext.request.contextPath}/${controller}/archivar?codFisFac=${documento.codFisFac}" class="btn btn-success m-0" id="linkClasify"><i class="fa-solid fa-eye"></i></a>
                                    </c:when>
                                </c:choose>
                            </div>
                        </security:authorize>
                        <jsp:include page="../comunes/BotonesXML.jsp" />
                    </div>
                </div>
                <hr>
                <div class="card row">
                    <ul class="list-group list-group-flush">
                        <li class="list-group-item"><h3>Comentarios</h3> ${documento.comentarios}</li>
                        <security:authorize access="hasRole('ADMIN')">
                            <form:form  action="${pageContext.request.contextPath}/${controller}/comentar" method="post" id="form-coment">
                                <li class="list-group-item">
                                    <h3 class="m-2"><button class="btn btn-success" type="submit" id="btnSubmit" value="comentar"><i class="fas fa-check-circle"></i> Actualizar comentario</button></h3>
                                    <textarea name="comentario" cols="80" rows="5">${documento.comentarios}</textarea>
                                    <input type="hidden" value="${documento.codFisFac}" name="codFisFac">
                                </li>
                            </form:form>
                        </security:authorize>
                        <c:choose>
                            <c:when test="${documento.idError ne ''}">
                                <li class="list-group-item"><h5>Se encontraron errores importantes</h5> Revisar codigos:  <Strong>${documento.idError}</strong></li>
                            </c:when>
                        </c:choose>
                    </ul>
                </div>

                <!--Primer Renglón-->
                <hr>
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
                                    <dt class="list-group-item px-1">FechaSol</dt>
                                    <dt class="list-group-item px-1">CUPS</dt>
                                    <dt class="list-group-item px-1">Archivado</dt>
                                </ul>
                            </div>
                            <div class="col-8 col-md-7">
                                <ul class="list-group list-group-flush">
                                    <li class="list-group-item px-1">${documento.codEmpEmi}</li>
                                    <li class="list-group-item px-1">${documento.codEmpDes}</li>
                                    <li class="list-group-item px-1">${documento.codPro}</li>
                                    <li class="list-group-item px-1">${documento.codPas}</li>
                                    <li class="list-group-item px-1">${documento.codSol}</li>
                                    <li class="list-group-item px-1">${documento.fecSol}</li>
                                    <li class="list-group-item px-1">${documento.cups}</li>
                                    <li class="list-group-item px-1">
                                        <c:choose>
                                            <c:when test="${documento.isDeleted == 0}">No</c:when>
                                            <c:when test="${documento.isDeleted == 1}">Si</c:when>
                                        </c:choose>
                                    </li>
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
                                    <dt class="list-group-item px-1">ProcedenciaD</dt>
                                    <dt class="list-group-item px-1">ProcedenciaH</dt>
                                    <c:if test="${documento.codFacRecAnu ne ''}">
                                   		<dt class="list-group-item px-1">Factura que rectifica</dt>
                                    </c:if>
                                </ul>
                            </div>
                            <div class="col-7">
                                <ul class="list-group list-group-flush">
                                    <li class="list-group-item px-1">${documento.codFisFac}</li>
                                    <li class="list-group-item px-1">${documento.impTotFac}</li>
                                    <li class="list-group-item px-1">${documento.tipFac}</li>
                                    <li class="list-group-item px-1">${documento.motFac}</li>
                                    <li class="list-group-item px-1">${documento.fecFac}</li>

                                    <c:choose>
                                        <c:when test="${ documento.aeProDes == 10 }">
                                            <li class="list-group-item px-1">Telemedida</li>
                                            </c:when>
                                            <c:when test="${ documento.aeProDes == 11 }">
                                            <li class="list-group-item px-1">TeleCorregida</li>
                                            </c:when>
                                            <c:when test="${ documento.aeProDes == 20 }">
                                            <li class="list-group-item px-1">TPL</li>
                                            </c:when>
                                            <c:when test="${ documento.aeProDes == 21 }">
                                            <li class="list-group-item px-1">TPL Corregida</li>
                                            </c:when>
                                            <c:when test="${ documento.aeProDes == 30 }">
                                            <li class="list-group-item px-1">Visual</li>
                                            </c:when>
                                            <c:when test="${ documento.aeProDes == 31 }">
                                            <li class="list-group-item px-1">Visual Corr</li>
                                            </c:when>
                                            <c:when test="${ documento.aeProDes == 40 }">
                                            <li class="list-group-item px-1">Estimada</li>
                                            </c:when>     
                                            <c:when test="${ documento.aeProDes == 50 }">
                                            <li class="list-group-item px-1">Autolectura</li>
                                            </c:when>
                                            <c:when test="${ documento.aeProDes == 60 }">
                                            <li class="list-group-item px-1">CCH</li>
                                            </c:when>
                                            <c:otherwise>
                                            <li class="list-group-item px-1">Sin Lectura</li>
                                            </c:otherwise>
                                        </c:choose>

                                    <c:choose>
                                        <c:when test="${ documento.aeProHas == 10 }">
                                            <li class="list-group-item px-1">Telemedida</li>
                                            </c:when>
                                            <c:when test="${ documento.aeProHas == 11 }">
                                            <li class="list-group-item px-1">TeleCorregida</li>
                                            </c:when>
                                            <c:when test="${ documento.aeProHas == 20 }">
                                            <li class="list-group-item px-1">TPL</li>
                                            </c:when>
                                            <c:when test="${ documento.aeProHas == 21 }">
                                            <li class="list-group-item px-1">TPL Corregida</li>
                                            </c:when>
                                            <c:when test="${ documento.aeProHas == 30 }">
                                            <li class="list-group-item px-1">Visual</li>
                                            </c:when>
                                            <c:when test="${ documento.aeProHas == 31 }">
                                            <li class="list-group-item px-1">Visual Corr</li>
                                            </c:when>
                                            <c:when test="${ documento.aeProHas == 40 }">
                                            <li class="list-group-item px-1">Estimada</li>
                                            </c:when>     
                                            <c:when test="${ documento.aeProHas == 50 }">
                                            <li class="list-group-item px-1">Autolectura</li>
                                            </c:when>
                                            <c:when test="${ documento.aeProHas == 60 }">
                                            <li class="list-group-item px-1">CCH</li>
                                            </c:when>
                                            <c:otherwise>
                                            <li class="list-group-item px-1">Sin Lectura</li>
                                            </c:otherwise>
                                        </c:choose>
                                    <c:if test="${documento.codFacRecAnu ne ''}">
	                                    <c:url var="detalles" value="/${controller}/detalles">
					                        <c:param name="codFisFac" value="${documento.codFacRecAnu}"/>
					                    </c:url>
                                   		<li class="list-group-item px-1">
                                   			<a href="${detalles}">${documento.codFacRecAnu}</a>
                                   		</li>
                                    </c:if>
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
                                    <li class="list-group-item px-1">${documento.tarAtrFac}</li>
                                    <li class="list-group-item px-1">${documento.modConPot}</li>
                                    <li class="list-group-item px-1">${documento.marMedPer}</li>
                                    <li class="list-group-item px-1">${documento.vasTra}</li>
                                    <li class="list-group-item px-1">${documento.porPer}</li>
                                    <li class="list-group-item px-1">${documento.numDias}</li>
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
                                    <li class="list-group-item px-1">${documento.excPot1}</li>
                                    <li class="list-group-item px-1">${documento.excPot2}</li>
                                    <li class="list-group-item px-1">${documento.excPot3}</li>
                                    <li class="list-group-item px-1">${documento.excPot4}</li>
                                    <li class="list-group-item px-1">${documento.excPot5}</li>
                                    <li class="list-group-item px-1">${documento.excPot6}</li>
                                    <li class="list-group-item px-1">${documento.excImpTot}</li>
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
                                    <li class="list-group-item px-1">${documento.potCon1}</li>
                                    <li class="list-group-item px-1">${documento.potCon2}</li>
                                    <li class="list-group-item px-1">${documento.potCon3}</li>
                                    <li class="list-group-item px-1">${documento.potCon4}</li>
                                    <li class="list-group-item px-1">${documento.potCon5}</li>
                                    <li class="list-group-item px-1">${documento.potCon6}</li>
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
                                    <li class="list-group-item px-1">${documento.potMax1}</li>
                                    <li class="list-group-item px-1">${documento.potMax2}</li>
                                    <li class="list-group-item px-1">${documento.potMax3}</li>
                                    <li class="list-group-item px-1">${documento.potMax4}</li>
                                    <li class="list-group-item px-1">${documento.potMax5}</li>
                                    <li class="list-group-item px-1">${documento.potMax6}</li>
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
                                    <li class="list-group-item px-1">${documentopotFac1}</li>
                                    <li class="list-group-item px-1">${documentopotFac2}</li>
                                    <li class="list-group-item px-1">${documentopotFac3}</li>
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
                                    <li class="list-group-item px-1">${documento.potPre1}</li>
                                    <li class="list-group-item px-1">${documento.potPre2}</li>
                                    <li class="list-group-item px-1">${documento.potPre3}</li>
                                    <li class="list-group-item px-1">${documento.potPre4}</li>
                                    <li class="list-group-item px-1">${documento.potPre5}</li>
                                    <li class="list-group-item px-1">${documento.potPre6}</li>
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
                                    <li class="list-group-item px-1">${documento.potImpTot}</li>
                                </ul>
                            </div>
                        </div>
                    </div>
                	<div class="row justify-content-around">
                        <div class="list-group col-12 col-md-5 col-lg-4 p-2">
                            <h3 class="list-group-item list-group-item-action active text-center h4">Termino Potencia</h3>
                            <div class="row">
                                <div class="col-5">
                                    <ul class="list-group list-group-flush text-right">
                                        <dt class="list-group-item px-1">Fecha Desde</dt>
                                        <dt class="list-group-item px-1">Fecha Hasta</dt>
                                    </ul>
                                </div>
                                <div class="col-7">
                                    <ul class="list-group list-group-flush">
                                        <li class="list-group-item px-1">${documento.tpFechaDesde}</li>
                                        <li class="list-group-item px-1">${documento.tpFechaHasta}</li>
                                    </ul>
                                </div>
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
                                    <li class="list-group-item px-1">${documento.eaFecDes1}</li>
                                    <li class="list-group-item px-1">${documento.eaFecHas1}</li>
                                    <li class="list-group-item px-1">${documento.eaFecDes2}</li>
                                    <li class="list-group-item px-1">${documento.eaFecHas2}</li>
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
                                    <li class="list-group-item px-1">${documento.eaVal1}</li>
                                    <li class="list-group-item px-1">${documento.eaVal2}</li>
                                    <li class="list-group-item px-1">${documento.eaVal3}</li>
                                    <li class="list-group-item px-1">${documento.eaVal4}</li>
                                    <li class="list-group-item px-1">${documento.eaVal5}</li>
                                    <li class="list-group-item px-1">${documento.eaVal6}</li>
                                    <li class="list-group-item px-1">${documento.eaValSum}</li>
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
                                    <li class="list-group-item px-1">${documento.eaPre1}</li>
                                    <li class="list-group-item px-1">${documento.eaPre2}</li>
                                    <li class="list-group-item px-1">${documento.eaPre3}</li>
                                    <li class="list-group-item px-1">${documento.eaPre4}</li>
                                    <li class="list-group-item px-1">${documento.eaPre5}</li>
                                    <li class="list-group-item px-1">${documento.eaPre6}</li>
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
                                    <li class="list-group-item px-1">${documento.eaImpTot}</li>
                                </ul>
                            </div>
                        </div>
                    </div>
                </div>

                <!-- Renglón-->
                <c:if test="${controller!='facturas'}">
                    <hr>
                    <h2 class="display-4">Cargos</h2>
                    <hr>
                    <div class="row justify-content-around">
                        <div class="list-group col-12 col-md-5 col-lg-4 p-2">
                            <h3 class="list-group-item list-group-item-action active text-center h4">Cargos 01</h3>
                            <div class="row">
                                <div class="col-5">
                                    <ul class="list-group list-group-flush text-right">
                                        <dt class="list-group-item px-1">Cargo 1</dt>
                                        <dt class="list-group-item px-1">Cargo 2</dt>
                                        <dt class="list-group-item px-1">Cargo 3</dt>
                                        <dt class="list-group-item px-1">Cargo 4</dt>
                                        <dt class="list-group-item px-1">Cargo 5</dt>
                                        <dt class="list-group-item px-1">Cargo 6</dt>
                                        <dt class="list-group-item px-1">Imp Tot</dt>
                                    </ul>
                                </div>
                                <div class="col-7">
                                    <ul class="list-group list-group-flush">
                                        <li class="list-group-item px-1">${documento.car1_01}</li>
                                        <li class="list-group-item px-1">${documento.car2_01}</li>
                                        <li class="list-group-item px-1">${documento.car3_01}</li>
                                        <li class="list-group-item px-1">${documento.car4_01}</li>
                                        <li class="list-group-item px-1">${documento.car5_01}</li>
                                        <li class="list-group-item px-1">${documento.car6_01}</li>
                                        <li class="list-group-item px-1">${documento.carImpTot_01}</li>
                                    </ul>
                                </div>
                            </div>
                        </div>
                        <div class="list-group col-12 col-md-5 col-lg-4 p-2">
                            <h3 class="list-group-item list-group-item-action active text-center h4">Cargos 02</h3>
                            <div class="row">
                                <div class="col-5">
                                    <ul class="list-group list-group-flush text-right">
                                        <dt class="list-group-item px-1">Cargo 1</dt>
                                        <dt class="list-group-item px-1">Cargo 2</dt>
                                        <dt class="list-group-item px-1">Cargo 3</dt>
                                        <dt class="list-group-item px-1">Cargo 4</dt>
                                        <dt class="list-group-item px-1">Cargo 5</dt>
                                        <dt class="list-group-item px-1">Cargo 6</dt>
                                        <dt class="list-group-item px-1">Imp Tot</dt>
                                    </ul>
                                </div>
                                <div class="col-7">
                                    <ul class="list-group list-group-flush">
                                        <li class="list-group-item px-1">${documento.car1_02}</li>
                                        <li class="list-group-item px-1">${documento.car2_02}</li>
                                        <li class="list-group-item px-1">${documento.car3_02}</li>
                                        <li class="list-group-item px-1">${documento.car4_02}</li>
                                        <li class="list-group-item px-1">${documento.car5_02}</li>
                                        <li class="list-group-item px-1">${documento.car6_02}</li>
                                        <li class="list-group-item px-1">${documento.carImpTot_02}</li>
                                    </ul>
                                </div>
                            </div>
                        </div>
                    </div>
                </c:if>

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
                                    <li class="list-group-item px-1">${documento.ieImp}</li>
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
                                    <li class="list-group-item px-1">${documento.aImpFac}</li>
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
                                    <li class="list-group-item px-1">${documento.iBasImp}</li>
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
                                    <li class="list-group-item px-1">${documento.aeCon1}</li>
                                    <li class="list-group-item px-1">${documento.aeCon2}</li>
                                    <li class="list-group-item px-1">${documento.aeCon3}</li>
                                    <li class="list-group-item px-1">${documento.aeCon4}</li>
                                    <li class="list-group-item px-1">${documento.aeCon5}</li>
                                    <li class="list-group-item px-1">${documento.aeCon6}</li>
                                    <li class="list-group-item px-1">${documento.aeConSum}</li>
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
                                    <li class="list-group-item px-1">${documento.aeLecDes1}</li>
                                    <li class="list-group-item px-1">${documento.aeLecDes2}</li>
                                    <li class="list-group-item px-1">${documento.aeLecDes3}</li>
                                    <li class="list-group-item px-1">${documento.aeLecDes4}</li>
                                    <li class="list-group-item px-1">${documento.aeLecDes5}</li>
                                    <li class="list-group-item px-1">${documento.aeLecDes6}</li>
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
                                    <li class="list-group-item px-1">${documento.aeLecHas1}</li>
                                    <li class="list-group-item px-1">${documento.aeLecHas2}</li>
                                    <li class="list-group-item px-1">${documento.aeLecHas3}</li>
                                    <li class="list-group-item px-1">${documento.aeLecHas4}</li>
                                    <li class="list-group-item px-1">${documento.aeLecHas5}</li>
                                    <li class="list-group-item px-1">${documento.aeLecHas6}</li>
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
                                    <li class="list-group-item px-1">${documento.rCon1}</li>
                                    <li class="list-group-item px-1">${documento.rCon2}</li>
                                    <li class="list-group-item px-1">${documento.rCon3}</li>
                                    <li class="list-group-item px-1">${documento.rCon4}</li>
                                    <li class="list-group-item px-1">${documento.rCon5}</li>
                                    <li class="list-group-item px-1">${documento.rCon6}</li>
                                    <li class="list-group-item px-1">${documento.rConSum}</li>
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
                                    <li class="list-group-item px-1">${documento.rLecDes1}</li>
                                    <li class="list-group-item px-1">${documento.rLecDes2}</li>
                                    <li class="list-group-item px-1">${documento.rLecDes3}</li>
                                    <li class="list-group-item px-1">${documento.rLecDes4}</li>
                                    <li class="list-group-item px-1">${documento.rLecDes5}</li>
                                    <li class="list-group-item px-1">${documento.rLecDes6}</li>
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
                                    <li class="list-group-item px-1">${documento.rLecHas1}</li>
                                    <li class="list-group-item px-1">${documento.rLecHas2}</li>
                                    <li class="list-group-item px-1">${documento.rLecHas3}</li>
                                    <li class="list-group-item px-1">${documento.rLecHas4}</li>
                                    <li class="list-group-item px-1">${documento.rLecHas5}</li>
                                    <li class="list-group-item px-1">${documento.rLecHas6}</li>
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
                                    <li class="list-group-item px-1">${documento.rImpTot}</li>
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
                                    <li class="list-group-item px-1">${documento.pmCon1}</li>
                                    <li class="list-group-item px-1">${documento.pmCon2}</li>
                                    <li class="list-group-item px-1">${documento.pmCon3}</li>
                                    <li class="list-group-item px-1">${documento.pmCon4}</li>
                                    <li class="list-group-item px-1">${documento.pmCon5}</li>
                                    <li class="list-group-item px-1">${documento.pmCon6}</li>
                                    <li class="list-group-item px-1">${documento.pmConSum}</li>
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
                                    <li class="list-group-item px-1">${documento.pmLecHas1}</li>
                                    <li class="list-group-item px-1">${documento.pmLecHas2}</li>
                                    <li class="list-group-item px-1">${documento.pmLecHas3}</li>
                                    <li class="list-group-item px-1">${documento.pmLecHas4}</li>
                                    <li class="list-group-item px-1">${documento.pmLecHas5}</li>
                                    <li class="list-group-item px-1">${documento.pmLecHas6}</li>
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
                                    <c:if test="${documento.remesaPago != null}">
                                        <dt class="list-group-item px-1">Remesa pago</dt>
                                    </c:if>
                                    <dt class="list-group-item px-1">Estado pago</dt>
                                </ul>
                            </div>
                            <div class="col-7">
                                <ul class="list-group list-group-flush">
                                    <li class="list-group-item px-1">${documento.rfImpTot}</li>
                                    <li class="list-group-item px-1">${documento.rfSalTotFac}</li>
                                    <li class="list-group-item px-1">${documento.rfTotRec}</li>
                                    <li class="list-group-item px-1">${documento.rfFecVal}</li>
                                    <li class="list-group-item px-1">${documento.rfFecLimPag}</li>
                                    <li class="list-group-item px-1">${documento.rfIdRem}</li>
                                    <c:if test="${documento.remesaPago != null}">
                                        <li class="list-group-item px-1">${documento.remesaPago}</li>
                                    </c:if>
                                    <li class="list-group-item px-1">${documento.estadoPago}</li>
                                </ul>
                            </div>
                        </div>
                    </div>
                    <c:url var="detalles" value="/clientes/detalles">
                        <c:param name="valor" value="${cliente.idCliente}"/>
                        <c:param name="filtro" value="id"/>
                    </c:url>
                    <div class="list-group col-12 col-md-5 col-lg-4 p-2">
                        <h3 class="list-group-item list-group-item-action active text-center h4">Cliente <a href="${detalles}" class="btn btn-danger"><i class="fas fa-eye"></i></a></h3>
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
                    <c:catch var="nullPointerException">
                        <c:if test="${documento.energiaExcedentaria != null}">
                            <div class="list-group col-12 col-md-5 col-lg-4 p-2">
                                <h3 class="list-group-item list-group-item-action active text-center h4">Energia Excedentaria</h3>
                                <div class="row">
                                    <div class="col-5">
                                        <ul class="list-group list-group-flush text-right">
                                            <dt class="list-group-item px-1">Excedentaria 01</dt>
                                            <dt class="list-group-item px-1">Excedentaria 02</dt>
                                            <dt class="list-group-item px-1">Excedentaria 03</dt>
                                            <dt class="list-group-item px-1">Excedentaria 04</dt>
                                            <dt class="list-group-item px-1">Excedentaria 05</dt>
                                            <dt class="list-group-item px-1">Excedentaria 06</dt>
                                            <dt class="list-group-item px-1">Total</dt>
                                        </ul>
                                    </div>
                                    <div class="col-7">
                                        <ul class="list-group list-group-flush">
                                            <li class="list-group-item px-1">${documento.energiaExcedentaria.energiaExcedentaria01}</li>
                                            <li class="list-group-item px-1">${documento.energiaExcedentaria.energiaExcedentaria02}</li>
                                            <li class="list-group-item px-1">${documento.energiaExcedentaria.energiaExcedentaria03}</li>
                                            <li class="list-group-item px-1">${documento.energiaExcedentaria.energiaExcedentaria04}</li>
                                            <li class="list-group-item px-1">${documento.energiaExcedentaria.energiaExcedentaria05}</li>
                                            <li class="list-group-item px-1">${documento.energiaExcedentaria.energiaExcedentaria06}</li>
                                            <li class="list-group-item px-1">${documento.energiaExcedentaria.valorTotalEnergiaExcedentaria}</li>
                                        </ul>
                                    </div>
                                </div>
                            </div>
                        </c:if>
                    </c:catch>
                </div>


            </div>
        </c:if>    
        <jsp:include page="/WEB-INF/paginas/comunes/piePagina.jsp"></jsp:include>
    </body>
</html>
<script>
	<security:authorize access="hasRole('ADMIN')">
		Mousetrap.bind(['alt+shift+1'], function(){location.href= path + "/clasificar";});
		Mousetrap.bind(['alt+shift+2'], function(){location.href= path + "/procesar";});
		Mousetrap.bind(['alt+shift+3'], function(){document.getElementById('form-coment').submit();});
		Mousetrap.bind(['alt+shift+4'], function(){document.getElementById('linkClasify').click();});
	</security:authorize>
</script>