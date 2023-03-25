<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="security" uri="http://www.springframework.org/security/tags" %>
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
							<fmt:message key="customers.reclamaciones.detallemensaje">
								<fmt:param value="${busqueda.valorActual}"/>
                        		<fmt:param value="${busqueda.filtroActual}"/>
							</fmt:message>
						</c:when>
						<c:when test="${mensaje eq 'registroarchivado'}">
							<fmt:message key="customers.reclamaciones.detalleregistroarchivado1">
								<fmt:param value="${reclamacion.idReclamacion}"/>
							</fmt:message>
						</c:when>
						<c:when test="${mensaje eq 'registrodesarchivado'}">
							<fmt:message key="customers.reclamaciones.detalleregistroarchivado0">
								<fmt:param value="${reclamacion.idReclamacion}"/>
							</fmt:message>
						</c:when>
						<c:when test="${mensaje eq 'nuevoComentario'}">
							<fmt:message key="customers.reclamaciones.detallenuevocomentario">
								<fmt:param value="${reclamacion.idReclamacion}"/>
							</fmt:message>
						</c:when>
					</c:choose>
				</div>
        	</div>
            <div class="container">

                <hr>
                <div class="row justify-content-between p-0">
                    <div class="col-6">
                        <h2 class="m-0"><a href="${pageContext.request.contextPath}/clientes/reclamaciones"><i class="fas fa-arrow-circle-left text-success"></i></a> <fmt:message key="customers.reclamaciones.tittle"/> </h2>
                    </div>
                    <div class="row col-12 col-lg-6 justify-content-sm-evenly mt-3 mt-md-0">
                    	<div class="col-2">
                    		<security:authorize access="hasRole('ADMIN')">
								<c:url var="archivar" value="/clientes/reclamaciones/archivar">
									<c:param name="id" value="${reclamacion.idReclamacion}"/>
								</c:url>
		                    	<c:choose>
		                    		<c:when test="${reclamacion.isDeleted == 0}">
										<c:set var="buttonType" value="danger"/>
										<c:set var="eyeIcon" value="fa-eye-slash"/>
		                    		</c:when>
		                    		<c:when test="${reclamacion.isDeleted == 1}">
										<c:set var="buttonType" value="success"/>
										<c:set var="eyeIcon" value="fa-eye"/>
		                    		</c:when>
		                    	</c:choose>
								<a href="${archivar}" class="btn btn-${buttonType} m-0" id="linkClasify"><i class="fa-solid ${eyeIcon}"></i></a>
	                    	</security:authorize>
                    	</div>
                        <jsp:include page="./formularioBusqueda.jsp" />
                    </div>
                </div>
                <hr>

				<c:set var="comentarios" value="${reclamacion.comentarios}"/>
				<ul class="list-group list-group-flush row">
					<li class="list-group-item col-12">
						<form:form action="${pageContext.request.contextPath}/clientes/reclamaciones/comentar" method="post" id="form-coment">
							<h3 class="col-12 mb-4"><fmt:message key="customers.reclamaciones.comentarios"/></h3>
							<textarea id="comentarios" class="col-12" name="comentario" rows="6" style="resize:none; height:auto; min-height: 40px">${comentarios}</textarea>
							<h3 class="m-2 col-12 d-flex justify-content-end">
								<button id="boton" style="display: none;" type="submit" class="btn btn-success"><i class="fas fa-check-circle"></i> Actualizar comentario</button>
							</h3>
							<input type="hidden" value="${reclamacion.idReclamacion}" name="id">

						</form:form>
					</li>
				</ul>
				<hr class="my-2"/>

				<script>
					const texto = document.getElementById('comentarios');
					const boton = document.getElementById('boton');

					let valorOriginal = texto.value;

					texto.addEventListener('input', function() {
						if (texto.value !== valorOriginal) {
							boton.style.display = 'block';
						} else {
							boton.style.display = 'none';
						}
					});

					var textarea = document.getElementById('comentarios');

					textarea.addEventListener('input', function() {
						this.style.height = 'auto'; // Establece la altura en 'auto' para evitar que el contenido se corte
						this.style.height = Math.max(this.scrollHeight, this.offsetHeight, this.clientHeight) + 'px'; // Establece la altura en funci�n del contenido, tomando en cuenta la altura actual y la altura del contenido m�nimo
					});


				</script>

                <!--Primer Rengl�n-->
                <div class="row justify-content-between container">
                    <div class="list-group col-12 col-md-5 col-lg-5 p-2">
						<h3 class="list-group-item list-group-item-action active text-center h4 py-3">
							<fmt:message key="customers.reclamaciones.titulotabladetalles">
								<fmt:param value="${reclamacion.idReclamacion}"/>
							</fmt:message>
						</h3>
                        <div class="row px-3">
							<c:set var="fullColumn" value="col-12"/>
							<c:set var="leftColumn" value="col-md-5"/>
							<c:set var="rightColumn" value="col-md-6"/>
                            <div class="col-12">
                                <ul class="list-group list-group-flush text-right">
                                   	<div class="row align-items-center justify-content-evenly">
                                    	<div class="${fullColumn} ${leftColumn} text-md-end"><Strong><fmt:message key="customers.reclamaciones.codigoEmpresaEmisora"/></Strong></div>
                                    	<div class="${fullColumn} ${rightColumn}">${reclamacion.codigoEmpresaEmisora}</div>
                                   	</div>
                                   	<hr class="my-2"/>
                                    <div class="row align-items-center justify-content-evenly">
                                    	<div class="${fullColumn} ${leftColumn} text-md-end"><Strong><fmt:message key="customers.reclamaciones.codigoEmpresaDestino"/></Strong></div>
                                    	<div class="${fullColumn} ${rightColumn}">${reclamacion.codigoEmpresaDestino}</div>
                                   	</div>
									<hr class="my-2"/>
									<div class="row align-items-center justify-content-evenly">
										<div class="${fullColumn} ${leftColumn} text-md-end"><Strong><fmt:message key="customers.reclamaciones.codigoDePaso"/></Strong></div>
										<div class="${fullColumn} ${rightColumn}">${reclamacion.codigoDePaso}</div>
									</div>
									<hr class="my-2"/>
									<div class="row align-items-center justify-content-evenly">
										<div class="${fullColumn} ${leftColumn} text-md-end"><Strong><fmt:message key="customers.reclamaciones.codigoSolicitud"/></Strong></div>
										<div class="${fullColumn} ${rightColumn}">${reclamacion.codigoDeSolicitud}</div>
									</div>
									<hr class="my-2"/>
									<div class="row align-items-center justify-content-evenly mt-2">
										<div class="${fullColumn} ${leftColumn} text-md-end"><Strong><fmt:message key="customers.reclamaciones.fechaSolicitud"/></Strong></div>
										<div class="${fullColumn} ${rightColumn}">${reclamacion.fechaSolicitud}</div>
									</div>
									<hr class="my-2"/>
									<div class="row align-items-center justify-content-evenly">
										<div class="${fullColumn} ${leftColumn} text-md-end"><Strong><fmt:message key="customers.reclamaciones.fechaIncidente"/> </Strong></div>
										<div class="${fullColumn} ${rightColumn}">${reclamacion.fechaIncidente}</div>
									</div>
									<hr class="my-2"/>
									<div class="row align-items-center justify-content-evenly">
										<div class="${fullColumn} ${leftColumn} text-md-end"><Strong><fmt:message key="customers.reclamaciones.numeroFacturaATR"/></Strong></div>
										<div class="${fullColumn} ${rightColumn}">${reclamacion.numeroFacturaATR}</div>
									</div>
                                   	<hr class="my-2"/>
									<div class="row align-items-center justify-content-evenly">
										<div class="${fullColumn} ${leftColumn} text-md-end"><Strong><fmt:message key="customers.reclamaciones.idTipoReclamacion"/></Strong></div>
										<div class="${fullColumn} ${rightColumn}">${reclamacion.tipoReclamacion.id}</div>
									</div>
									<hr class="my-2"/>
									<div class="row align-items-center justify-content-evenly">
										<div class="${fullColumn} ${leftColumn} text-md-end"><Strong><fmt:message key="customers.reclamaciones.idSubtipoReclamacion"/></Strong></div>
										<div class="${fullColumn} ${rightColumn}">${reclamacion.subtipoReclamacion.id}</div>
									</div>
									<hr class="my-2"/>
									<div class="row align-items-center justify-content-evenly">
										<div class="${fullColumn} ${leftColumn} text-md-end"><Strong><fmt:message key="customers.reclamaciones.creado"/></Strong></div>
										<div class="${fullColumn} ${rightColumn}">
											<fmt:formatDate value="${reclamacion.createdOn.time}" pattern="yyyy/MM/dd" var="createdOn"/>
											<fmt:formatDate value="${reclamacion.createdOn.time}" pattern="HH:mm:ss" var="createdAt"/>
											<fmt:message key="customers.reclamaciones.fechapor">
												<fmt:param value="${createdOn}"/>
												<fmt:param value="${createdAt}"/>
												<fmt:param value="${reclamacion.createdBy}"/>
											</fmt:message>
										</div>
									</div>
									<hr class="my-2"/>
									<c:if test="${reclamacion.updatedOn != null}">
										<div class="row align-items-center justify-content-evenly">
											<div class="${fullColumn} ${leftColumn} text-md-end"><Strong><fmt:message key="customers.reclamaciones.actualizado"/></Strong></div>
											<div class="${fullColumn} ${rightColumn}">
												<fmt:formatDate value="${reclamacion.updatedOn.time}" pattern="yyyy/MM/dd" var="updatedOn"/>
												<fmt:formatDate value="${reclamacion.updatedOn.time}" pattern="HH:mm:ss" var="updatedAt"/>
												<fmt:message key="customers.reclamaciones.fechapor">
													<fmt:param value="${updatedOn}"/>
													<fmt:param value="${updatedAt}"/>
													<fmt:param value="${reclamacion.updatedBy}"/>
												</fmt:message>
											</div>
										</div>
										<hr class="my-2"/>
									</c:if>
                                   	<div class="row align-items-center justify-content-evenly">
                                    	<div class="${fullColumn} ${leftColumn} text-md-end"><Strong><fmt:message key="customers.reclamaciones.archivado"/></Strong></div>
                                    	<div class="${fullColumn} ${rightColumn}">
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
                                    <c:if test="${ticket.updatedOn != null}">
										<hr class="my-2"/>
                                    	<div class="row align-items-center justify-content-evenly">
	                                    	<div class="${fullColumn} ${leftColumn} text-md-end"><Strong>Actualizado</Strong></div>
	                                    	<div class="${fullColumn} ${rightColumn}"><fmt:formatDate value="${ticket.updatedOn.time}" type="date" dateStyle="long" /> <Strong>(${ticket.updatedBy})</Strong></div>
	                                   	</div>
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
                        <h3 class="list-group-item list-group-item-action active text-center h4"><fmt:message key="customers.reclamaciones.cliente"/> <a href="${detalles}" class="btn btn-danger"><i class="fas fa-eye"></i></a></h3>
                        <div class="row">
	                        <c:url var="detalles" value="/clientes/tickets/detalles">
	                            <c:param name="valor" value="${reclamacion.cliente.idCliente}"/>
	                            <c:param name="filtro" value="Cliente"/>
	                        </c:url>
                            <div class="col-3 col-md-4">
                                <ul class="list-group list-group-flush text-right">
                                	<dt class="list-group-item px-1"><fmt:message key="customers.reclamaciones.idCliente"/></dt>
                                    <dt class="list-group-item px-1">CUPS</dt>
                                    <dt class="list-group-item px-1"><fmt:message key="customers.reclamaciones.nombre"/></dt>
                                    <dt class="list-group-item px-1"><fmt:message key="customers.reclamaciones.tarifa"/></dt>
                                    <dt class="list-group-item px-1"><fmt:message key="customers.reclamaciones.estado"/></dt>
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