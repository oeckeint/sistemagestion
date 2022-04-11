<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<!DOCTYPE html>
<html>
    <head>
        <jsp:include page="/WEB-INF/paginas/comunes/contenidoHead.jsp"></jsp:include>
    </head>
    <body>
        <!--Cabecero-->
        <jsp:include page="/WEB-INF/paginas/comunes/cabecero.jsp"></jsp:include>
            <div class="alert alert-warning alert-dismissible fade show" role="alert">
                <div class="container">${mensaje}</div>
        </div>

        <!--Formulario-->
        <form:form action="${pageContext.request.contextPath}/clientes/guardar" method="post" modelAttribute="cliente" cssClass="was-validated">
            <section class="details mt-5">
                <div class="container">
                    <div class="row">
                        <div class="col">
                            <div class="card">
                                <div class="card-header">
                                    <div class="row">
                                        <div class="col-md-6">
                                            <h3><a href="${pageContext.request.contextPath}/clientes"><i class="fas fa-arrow-circle-left text-success"></i></a> ${tablaTitulo} <strong>${cliente.cups}</strong></h3>
                                        </div>
                                        <div class="col-md-6"> 
                                            <div class="row justify-content-end">
                                                <c:if test="${showDelete.equals('y')}">
                                                    <c:url var="deleteLink" value="/clientes/eliminar">
                                                        <c:param name="idCliente" value="${cliente.idCliente}"/>
                                                        <c:param name="cups" value="${cliente.cups}"/>
                                                    </c:url>
                                                    <div class="col-4">
                                                        <a href="${deleteLink}" class="btn btn-danger btn-block" onclick="return confirm('¿Eliminar registro?')"><i class="fas fa-times"></i> Eliminar</a>
                                                    </div>
                                                </c:if>
                                                <div class="col-4">
                                                    <button type="submit" class="btn btn-success btn-block"><i class="fas fa-check"></i> Guardar</button>
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                                <div class="card-body">
                                    	<div class="mb-3">
                                        	<label for="cups">CUPS</label>
                                       		<c:choose>
	                                            <c:when test="${cliente.cups == null}">
	                                                <form:input path="cups" cssClass="form-control" required="true"></form:input>
	                                            </c:when>
	                                            <c:otherwise>
	                                                <form:input path="cups" cssClass="form-control" required="true" disabled="true"></form:input>
	                                                <form:hidden path="cups"></form:hidden>
	                                            </c:otherwise>
                                       		</c:choose>
                                        </div>
                                        <div class="mb-3">
                                            <label for="nombre">Nombre</label>
                                        <form:input path="nombreCliente" cssClass="form-control" required="true"></form:input>
                                        </div>
                                        <div class="mb-3">
                                            <label for="tarifa">Tarifa</label>
                                        <form:select path="tarifa" cssClass="form-select text-left" required="true">
                                            <form:options items="${tarifas}" itemValue="nombreTarifa" itemLabel="nombreTarifa"></form:options>
                                        </form:select>
                                        <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                                        <form:hidden path="idCliente"></form:hidden>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </section>
        </form:form>

        <!--Footer-->
        <jsp:include page="/WEB-INF/view/comunes/footerFormularios.jsp"></jsp:include>
    </body>
</html>
