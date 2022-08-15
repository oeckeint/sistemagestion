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
                <div class="container"><strong>${mensaje}</strong></div>
        </div>
            <div class="container">

                <hr>
                <div class="row justify-content-between p-0">
                    <div class="col-6">
                        <h2 class="m-0"><a href="${pageContext.request.contextPath}/clientes/tickets"><i class="fas fa-arrow-circle-left text-success"></i></a> Detalles ticket</h2>
                    </div>
                    <div class="row col-12 col-lg-6 justify-content-sm-evenly mt-3 mt-md-0">
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
                        <h3 class="list-group-item list-group-item-action active text-center h4">Ticket #${ticket.idTicket}  <a href="${editar}" class="btn btn-danger"><i class="fas fa-edit"></i></a></h3>
                        <div class="row">
                            <div class="col-3 col-md-4">
                                <ul class="list-group list-group-flush text-right">
                                    <dt class="list-group-item px-1">Detalle</dt>
                                    <dt class="list-group-item px-1">Comentarios</dt>
                                    <dt class="list-group-item px-1">Tipo</dt>
                                    <dt class="list-group-item px-1">Estado</dt>
                                </ul>
                            </div>
                            <div class="col-8 col-md-7">
                                <ul class="list-group list-group-flush">
                                    <li class="list-group-item px-1">${ticket.detalles}</li>
                                    <li class="list-group-item px-1">${ticket.comentarios}</li>
                                    <li class="list-group-item px-1">${ticket.ticketTipoIncidencia.id} -> ${ticket.ticketTipoIncidencia.detalles}</li>
                                    <li class="list-group-item px-1">${ticket.ticketEstadoIncidencia.id} -> ${ticket.ticketEstadoIncidencia.detalles}</li>
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
                            <div class="col-3 col-md-4">
                                <ul class="list-group list-group-flush text-right">
                                	<dt class="list-group-item px-1">idCliente</dt>
                                    <dt class="list-group-item px-1">CUPS</dt>
                                    <dt class="list-group-item px-1">Nombre</dt>
                                    <dt class="list-group-item px-1">Tarifa</dt>
                                    <dt class="list-group-item px-1">Estado</dt>
                                    <dt class="list-group-item px-1">No. Tickets</dt>
                                </ul>
                            </div>
                            <div class="col-8 col-md-7">
                                <ul class="list-group list-group-flush">
                                	<li class="list-group-item px-1">${ticket.cliente.idCliente}</li>
                                    <li class="list-group-item px-1">${ticket.cliente.cups}</li>
                                    <li class="list-group-item px-1">${ticket.cliente.nombreCliente}</li>
                                    <li class="list-group-item px-1">${ticket.cliente.tarifa}</li>
                                    <li class="list-group-item px-1">${ticket.cliente.isDeleted}</li>
                                    <li class="list-group-item px-1">${ticket.cliente.clienteTickets.size()}</li>
                                </ul>
                            </div>
                        </div>
                    </div>
                </div>
				
				<hr>
            </div>
        <jsp:include page="/WEB-INF/paginas/comunes/piePagina.jsp"></jsp:include>
    </body>
</html>