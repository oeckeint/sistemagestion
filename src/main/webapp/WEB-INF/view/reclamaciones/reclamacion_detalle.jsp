<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
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
					<c:choose>
						<c:when test="${mensaje eq 'registroEncontrado'}">
							<fmt:message key="customers.tickets.registrosencontrados">
								<fmt:param value="${busquedaTicket.valor}"/>
                        		<fmt:param value="${busquedaTicket.filtro}"/>
							</fmt:message>
						</c:when>
					</c:choose>
				</div>
        	</div>
            <div class="container">

                <hr>
                <div class="row justify-content-between p-0">
                    <div class="col-6">
                        <h2 class="m-0"><a href="${pageContext.request.contextPath}/"><i class="fas fa-arrow-circle-left text-success"></i></a> <fmt:message key="customers.reclamaciones.tittle"/> </h2>
                    </div>
                    <div class="row col-12 col-lg-6 justify-content-sm-evenly mt-3 mt-md-0">
                    	<div class="col-2">
                    		<c:url var="archivar" value="/clientes/tickets/archivar">                        <span class="badge bg-success">
                            <c:choose>
								<c:when test="${paginaActual < ultimaPagina}">
									${registrosMostrados} / ${totalRegistros}
								</c:when>
								<c:otherwise>
									${totalRegistros} / ${totalRegistros}
								</c:otherwise>
							</c:choose>
                        </span>
                    			<c:param name="id" value="${reclamacion.idReclamacion}"/>
                    		</c:url>
                    		<security:authorize access="hasRole('ADMIN')">
		                    	<c:choose>
		                    		<c:when test="${reclamacion.isDeleted == 0}">
		                    			<a href="${archivar}" class="btn btn-danger m-0" id="linkClasify"><i class="fa-solid fa-eye-slash"></i></a>
		                    		</c:when>
		                    		<c:when test="${ticket.isDeleted == 1}">
		                    			<a href="${archivar}" class="btn btn-success m-0" id="linkClasify"><i class="fa-solid fa-eye"></i></a>
		                    		</c:when>
		                    	</c:choose>
	                    	</security:authorize>
                    	</div>
                        <jsp:include page="./busqueda_ticket.jsp" />
                    </div>
                </div>
                <hr>

                <!--Primer Renglón-->
                <div class="row justify-content-around">
                    <div class="list-group col-12 col-md-5 col-lg-5 p-2">
                    	<c:url var="editar" value="/clientes/tickets/editar">
                            <c:param name="id" value="${ticket.idTicket}"/>
                        </c:url>
                        <h3 class="list-group-item list-group-item-action active text-center h4">Reclamacion #${reclamacion.idReclamacion}  <a href="${editar}" class="btn btn-danger"><i class="fas fa-edit"></i></a></h3>
                        <div class="row px-3">
                            <div class="col-12">
                                <ul class="list-group list-group-flush text-right">
                                    <div class="row align-items-center justify-content-evenly mt-2">
                                    	<div class="col-12 col-md-3 text-md-end"><Strong><fmt:message key="customers.reclamaciones.fechaSolicitud"/></Strong></div>
                                    	<div class="col-12 col-md-8 ">${reclamacion.fechaSolicitud}</div>
                                   	</div>
                                   	<hr class="my-2"/>
                                    <div class="row align-items-center justify-content-evenly">
                                    	<div class="col-12 col-md-3 text-md-end"><Strong><fmt:message key="customers.reclamaciones.fechaIncidente"/> </Strong></div>
                                    	<div class="col-12 col-md-8 ">${reclamacion.fechaIncidente}</div>
                                   	</div>
                                   	<hr class="my-2"/>
                                   	<div class="row align-items-center justify-content-evenly">
                                    	<div class="col-12 col-md-3 text-md-end"><Strong><fmt:message key="customers.reclamaciones.codigoEmpresaEmisora"/></Strong></div>
                                    	<div class="col-12 col-md-8 ">${reclamacion.codigoEmpresaEmisora}</div>
                                   	</div>
                                   	<hr class="my-2"/>
                                    <div class="row align-items-center justify-content-evenly">
                                    	<div class="col-12 col-md-3 text-md-end"><Strong><fmt:message key="customers.reclamaciones.codigoEmpresaDestino"/></Strong></div>
                                    	<div class="col-12 col-md-8 ">${reclamacion.codigoEmpresaDestino}</div>
                                   	</div>
									<hr class="my-2"/>
									<div class="row align-items-center justify-content-evenly">
										<div class="col-12 col-md-3 text-md-end"><Strong><fmt:message key="customers.reclamaciones.numeroFacturaATR"/></Strong></div>
										<div class="col-12 col-md-8 ">${reclamacion.numeroFacturaATR}</div>
									</div>
                                   	<hr class="my-2"/>
                                   	<div class="row align-items-center justify-content-evenly">
                                    	<div class="col-12 col-md-3 text-md-end"><Strong>Archivado</Strong></div>
                                    	<div class="col-12 col-md-8 ">
											<c:choose>
												<c:when test="${reclamacion.isDeleted == 0}">
													<fmt:message key="customers.tickets.desarchivado">
														<fmt:param value="${busquedaTicket.valor}"/>
													</fmt:message>
												</c:when>
												<c:when test="${reclamacion.isDeleted == 1}">
													<fmt:message key="customers.tickets.archivado">
														<fmt:param value="${busquedaTicket.valor}"/>
													</fmt:message>
												</c:when>
											</c:choose>
										</div>
                                   	</div>
                                   	<hr class="my-2"/>
                                    <c:if test="${ticket.updatedOn != null}">
                                    	<div class="row align-items-center justify-content-evenly">
	                                    	<div class="col-12 col-md-3 text-md-end"><Strong>Actualizado</Strong></div>
	                                    	<div class="col-12 col-md-8 "><fmt:formatDate value="${ticket.updatedOn.time}" type="date" dateStyle="long" /> <Strong>(${ticket.updatedBy})</Strong></div>
	                                   	</div>
	                                   	<hr class="my-2"/>
                                    </c:if>
                                </ul>
                            </div>
                        </div>
                    </div>
                    
                    <div class="list-group col-12 col-md-5 col-lg-5 p-2">
                    	<c:url var="detalles" value="/clientes/detalles">
	                        <c:param name="valor" value="${ticket.cliente.idCliente}"/>
	                        <c:param name="filtro" value="id"/>
	                    </c:url>
                        <h3 class="list-group-item list-group-item-action active text-center h4">Cliente <a href="${detalles}" class="btn btn-danger"><i class="fas fa-eye"></i></a></h3>
                        <div class="row">
	                        <c:url var="detalles" value="/clientes/tickets/detalles">
	                            <c:param name="valor" value="${reclamacion.cliente.idCliente}"/>
	                            <c:param name="filtro" value="Cliente"/>
	                        </c:url>
                            <div class="col-3 col-md-4">
                                <ul class="list-group list-group-flush text-right">
                                	<dt class="list-group-item px-1">idCliente</dt>
                                    <dt class="list-group-item px-1">CUPS</dt>
                                    <dt class="list-group-item px-1">Nombre</dt>
                                    <dt class="list-group-item px-1">Tarifa</dt>
                                    <dt class="list-group-item px-1">Estado</dt>
                                    <dt class="list-group-item px-1"><a href="${detalles}">No. Tickets</a></dt>
                                </ul>
                            </div>
                            <div class="col-8 col-md-7">
                                <ul class="list-group list-group-flush">
                                	<li class="list-group-item px-1">${reclamacion.cliente.idCliente}</li>
                                    <li class="list-group-item px-1">${reclamacion.cliente.cups}</li>
                                    <li class="list-group-item px-1">${reclamacion.cliente.nombreCliente}</li>
                                    <li class="list-group-item px-1">${reclamacion.cliente.tarifa}</li>
                                    <li class="list-group-item px-1">${reclamacion.cliente.isDeleted}</li>
                                    <li class="list-group-item px-1">${reclamacion.cliente.clienteTickets.size()}</li>
                                </ul>
                            </div>
                        </div>
                    </div>
				<hr>
            </div>
          </div>
        <jsp:include page="/WEB-INF/paginas/comunes/piePagina.jsp"></jsp:include>
    </body>
</html>