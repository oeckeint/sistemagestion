<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="security" uri="http://www.springframework.org/security/tags" %>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<jsp:include page="/WEB-INF/view/comunes/Lang.jsp"></jsp:include>
<!DOCTYPE html>
<html lang="${language}">
	<head>
		<jsp:include page="/WEB-INF/paginas/comunes/contenidoHead.jsp"></jsp:include>
	</head>
<body>
	<!--Cabecero-->
	<jsp:include page="/WEB-INF/paginas/comunes/cabecero.jsp"></jsp:include>
	<div class="alert alert-warning alert-dismissible fade show" role="alert">
         <div class="container">
            <c:choose>
            	<c:when test="${mensaje eq 'registrosEncontrados'}">
            		<fmt:message key="customers.tickets.registrosencontrados">
                        <fmt:param value="${busquedaTicket.valor}"/>
                        <fmt:param value="${busquedaTicket.filtro}"/>
                    </fmt:message>
            	</c:when>
            	<c:when test="${error eq 'sinregistro'}">
            		<fmt:message key="customers.tickets.sinresultado">
                        <fmt:param value="${busquedaTicket.valor}"/>
                        <fmt:param value="${busquedaTicket.filtro}"/>
                    </fmt:message>
            	</c:when>
                <c:when test="${param.sinvalor != null}"><fmt:message key="error.sinvalor"/></c:when>
                <c:when test="${param.sinregistro != null}">
                    <fmt:message key="error.sinregistro">
                        <fmt:param value="${param.v}"/>
                        <fmt:param value="${param.f}"/>
                    </fmt:message>
                </c:when>
                <c:when test="${param.idnan != null}">
                    <fmt:message key="error.idnan">
                    	<fmt:param value="${busquedaCliente.filtro}"/>
                    	<fmt:param value="${busquedaCliente.valor}"/>                        	
                    </fmt:message>
                </c:when>
                <c:when test="${param.unknown != null}">
                    <fmt:message key="error.unknown"></fmt:message>
                </c:when>
                <c:otherwise><fmt:message key="customers.tickets.message"/></c:otherwise>
            </c:choose>
        </div>
    </div>
	
	<div class="container">
		<hr>
        <div class="row">
            <div class="col-12">
                <div class="row justify-content-between p-0">
                    <div class="col-12 col-lg-5">
                        <h2 class="m-0"><a href="${pageContext.request.contextPath}/clientes">
                                <i class="fas fa-arrow-circle-left text-success"></i></a> 
                                <fmt:message key="customers.tickets.title"/>
                            <span class="badge bg-success">
                                <c:choose>
                                    <c:when test="${paginaActual < ultimaPagina}">
                                        ${registrosMostrados} / ${totalRegistros}
                                    </c:when>
                                    <c:otherwise>
                                        ${totalRegistros} / ${totalRegistros}
                                    </c:otherwise>
                                </c:choose>
                            </span>
                        </h2>
                    </div>
                    <div class="row col-12 col-lg-6 justify-content-end mt-3 mt-md-0">
                        <jsp:include page="./busqueda_ticket.jsp" />
                    </div>
                </div>
            </div>
        </div>
        <hr>
        <c:choose>
        	<c:when test="${busquedaSinRegistros == true}">
        		No hemos encontrado algun ticket, intente de nuevo su busqueda
        	</c:when>
        	<c:otherwise>
        		<div class="table-responsive">
		        	<table class="table table-hover text-center">
			        	<thead>
				           <tr class="bg-dark text-white">
				               <th scope="col"><fmt:message key="customers.tickets.id"/></th>
				               <th scope="col"><fmt:message key="customers.tickets.details"/></th>
				               <th scope="col"><fmt:message key="customers.tickets.comments"/></th>
				               <th scope="col"><fmt:message key="customers.tickets.status"/></th>
				               <th scope="col"><fmt:message key="customers.tickets.type"/></th>
				               <th scope="col"><fmt:message key="customer.customer"/></th>
				               <th scope="col"><fmt:message key="customers.tickets.actions"/></th>
				           </tr>
				        </thead>
				        <tbody>
				        	<c:forEach var="ticket" items="${tickets}" varStatus="counter">
				        		<c:url var="editar" value="/clientes/tickets/editar">
		                            <c:param name="id" value="${ticket.idTicket}"/>
		                        </c:url>
				        		<c:url var="detalles" value="/clientes/tickets/detalles">
		                            <c:param name="valor" value="${ticket.idTicket}"/>
		                            <c:param name="filtro" value="Ticket"/>
		                        </c:url>
					        	<tr>
					        		<th scope="row">${ticket.idTicket}</th>
					        		<td>${ticket.detalles}</td>
					        			<c:choose>
					        				<c:when test="${ticket.comentarios.length() > 25}">
					        					<td title="${ticket.comentarios}">${fn:substring(ticket.comentarios, 0, 25)}...</td>
					        				</c:when>
					        				<c:otherwise>
					        					<td>${ticket.comentarios}</td>
					        				</c:otherwise>
					        			</c:choose>
					        		<td title="${ticket.ticketEstadoIncidencia.detalles}">${ticket.ticketEstadoIncidencia.id}</td>
					        		<td title="${ticket.ticketTipoIncidencia.detalles}">${ticket.ticketTipoIncidencia.id}</td>
					        		<td title="${ticket.cliente.nombreCliente}">${ticket.cliente.idCliente}</td>
					        		<td>
		                                <button class="btn btn-success" type="button" id="editButton${ticket.idTicket}" onclick="editData('${editar}');">
		                                    <i class="fas fa-edit" id="editIcon${ticket.idTicket}"></i></a> 
		                                </button>
		                                <button class="btn btn-danger" type="button" id="detailButton${ticket.idTicket}" onclick="loadData(${ticket.idTicket}, '${detalles}');">
		                                    <i class="fas fa-eye" id="detailsIcon${ticket.idTicket}"></i>
		                                </button>
		                            </td>
					        	</tr>
							</c:forEach>
				        </tbody>
				        <jsp:include page="../xml/pagination.jsp" />
			        </table>
				</div>
				<jsp:include page="../xml/pagination.jsp" />
        	</c:otherwise>
        </c:choose>
        <hr/>
	</div>
	
	<script>
		function loadData(id, url) {
			window.location = url;
		}
		
		function editData(url){
			window.location = url;
		}
	</script>
	<!--Footer-->
    <jsp:include page="/WEB-INF/paginas/comunes/piePagina.jsp"></jsp:include>
</body>
</html>