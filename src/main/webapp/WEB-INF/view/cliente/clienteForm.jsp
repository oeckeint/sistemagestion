<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<!DOCTYPE html>
<html>
    <head>
        <meta charset="utf-8" />
        <meta http-equiv="X-UA-Compatible" content="IE=edge">
        <meta name="viewport" content="width=device-width, initial-scale=1">
        <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.2.1/css/bootstrap.min.css" integrity="sha384-GJzZqFGwb1QTTN6wy59ffF1BuGJpLSa9DkKMp0DgiMDm4iYMj70gZWKYbI706tWS" crossorigin="anonymous">
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css" rel="stylesheet" integrity="sha384-1BmE4kWBq78iYhFldvKuhfTAU6auU8tT94WrHftjDbrCEXSU1oBoqyl2QvZ6jIW3" crossorigin="anonymous">
        <script src="https://code.jquery.com/jquery-3.3.1.slim.min.js" integrity="sha384-q8i/X+965DzO0rT7abK41JStQIAqVgRVzpbzo5smXKp4YfRvH+8abtTE1Pi6jizo" crossorigin="anonymous"></script>
        <script src="https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.14.6/umd/popper.min.js" integrity="sha384-wHAiFfRlMFy6i5SRaxvfOCifBUQy1xHdJ/yoi7FRNXMRBu5WHdZYu1hA6ZOblgut" crossorigin="anonymous"></script>
        <script src="https://stackpath.bootstrapcdn.com/bootstrap/4.2.1/js/bootstrap.min.js" integrity="sha384-B0UglyR+jN6CkvvICOB2joaf5I4l3gm9GU6Hc1og6Ls7i6U/mkkaduKaBhlAXv9k" crossorigin="anonymous"></script>

        <script src="http://code.jquery.com/jquery-3.3.1.min.js"  integrity="sha256-FgpCb/KJQlLNfOu91ta32o/NMZxltwRo8QtmkMRdAu8=" crossorigin="anonymous"></script>
        <link rel="stylesheet" href="https://cdn.datatables.net/1.10.19/css/jquery.dataTables.min.css">
        <script src="https://cdn.datatables.net/1.10.19/js/jquery.dataTables.min.js" ></script>
        <link rel="stylesheet" href="https://use.fontawesome.com/releases/v5.15.1/css/all.css" integrity="sha384-vp86vTRFVJgpjF9jiIGPEEqYqlDwgyBgEF109VFjmqGmIY/Y4HV4d3Gp2irVfcrp" crossorigin="anonymous">
        <title>${tituloPagina}</title>
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
                                    <div class="form-group">
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
                                        <form:hidden path="idCliente"></form:hidden>
                                        </div>
                                        <div class="form-group">
                                            <label for="nombre">Nombre</label>
                                        <form:input path="nombreCliente" cssClass="form-control" required="true"></form:input>
                                        </div>
                                        <div class="form-group">
                                            <label for="tarifa">Tarifa</label>
                                        <form:select path="tarifa" cssClass="custom-select" required="true">
                                            <form:options items="${tarifas}" itemValue="nombreTarifa" itemLabel="nombreTarifa"></form:options>
                                        </form:select>
                                        <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
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
