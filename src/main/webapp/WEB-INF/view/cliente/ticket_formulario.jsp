<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html>
    <head>
        <jsp:include page="/WEB-INF/paginas/comunes/contenidoHead.jsp"></jsp:include>
    </head>
    <body>
        <!--Cabecero-->
        <jsp:include page="/WEB-INF/paginas/comunes/cabecero.jsp"></jsp:include>
            <div class="alert alert-warning alert-dismissible fade show" role="alert">
                <div class="container">
                	<c:choose>
                		<c:when test="${editar}">
            				<fmt:message key="customers.tickets.edit"></fmt:message>
            			</c:when>
                		<c:when test="${!clienteEncontrado}">
            				<fmt:message key="customers.tickets.create1"></fmt:message>
            			</c:when>
            			<c:when test="${clienteEncontrado}">
            				<fmt:message key="customers.tickets.create2"></fmt:message>
            			</c:when>
        			</c:choose>
            	</div>
        </div>

        <!--Formulario-->
        <form:form action="${pageContext.request.contextPath}/clientes/tickets/agregar?${_csrf.parameterName}=${_csrf.token}" method="post" modelAttribute="clienteTicket" cssClass="was-validated">
            <section class="details mt-5">
                <div class="container">
                	<hr>
	                <div class="row justify-content-between p-0">
	                    <div class="col-6">
	                        <h2 class="m-0"><a href="${pageContext.request.contextPath}/clientes/tickets"><i class="fas fa-arrow-circle-left text-success"></i></a> Nuevo Ticket</h2>
	                    </div>
	                </div>
	                <hr>
                    <div class="row">
                        <div class="col">
                            <div class="card">
                                <div class="card-header">
                                <!--
                                         <div class="row justify-content-end">
                                             <div class="col-2">
                                                 <button type="button" class="btn btn-success btn-block" onclick="document.getElementById('clienteTicket').submit();"><i class="fa-solid fa-magnifying-glass"></i> Buscar</button>
                                             </div>
                                        </div>
                                -->
                                </div>
                                <div class="card-body row">
	                           		<div class="mb-3 col col-md-2">
	                                   	<label for="cups">ID Cliente</label>
	                                       <form:input path="cliente.idCliente" cssClass="form-control" required="true"></form:input>
	                                   </div>
	                               	<div class="mb-3 col">
                                   		<label for="cups">CUPS</label>
										<form:input path="cliente.cups" cssClass="form-control" required="true" disabled="true"></form:input>
									</div>
                                   	<div class="mb-3 col">
                                       	<label for="nombre">Nombre</label>
                                   		<form:input path="cliente.nombreCliente" cssClass="form-control" required="true" disabled="true"></form:input>
                                   </div>
                                   <div class="mb-3 col d-flex align-items-end">
                                   		<button type="submit" class="btn btn-success btn-block" onclick=""><i class="fa-solid fa-magnifying-glass"></i> Buscar</button>
                                   		<input id="prodId" name="idClienteActual" type="hidden" value="${idClienteActual}">
                                   </div>
                                   <c:if test="${clienteEncontrado}">
                                		<hr/>
                                		<div class="mb-3 col-12">
	                                   		<label for="cups">Detalles</label>
	                                   		<form:input path="detalles" cssClass="form-control" required="true"></form:input>
	                                   </div>
	                                   <div class="mb-3 col-12">
	                                   		<label for="cups">Comentarios</label>
	                                   		<form:input path="comentarios" cssClass="form-control" required="true"></form:input>
	                                   </div>
	                                   <div class="mb-3 row">
	                                   		<div class="mb-3 col-3">
	                                   			<label for="estado">Estado</label>
	                                   			<form:select path="ticketEstadoIncidencia.id" cssClass="form-select text-left" required="true">
		                                            <form:options items="${estadosIncidencia}" itemValue="id" itemLabel="detalles"></form:options>
		                                        </form:select>
	                                   		</div>
	                                   		<div class="mb-3 col-3">
	                                   			<label for="cups">Tipo</label>
	                                   			<form:select path="ticketTipoIncidencia.id" cssClass="form-select text-left" required="true">
	                                   				<form:options items="${tiposIncidencia}" itemValue="id" itemLabel="detalles"></form:options>
	                                   			</form:select>
	                                   		</div>
	                                   		<form:hidden path="idTicket"></form:hidden>
	                                   		<div class="mb-3 col-5">
	                                   			<br/>
	                                   			<button class="btn btn-danger">Enviar</button>
	                                   		</div>
	                                   </div>
                                	</c:if>
                                </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </section>
        </form:form>

        <!--Footer-->
        <jsp:include page="/WEB-INF/paginas/comunes/piePagina.jsp"></jsp:include>
    </body>
</html>
