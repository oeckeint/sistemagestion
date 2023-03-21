<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
    <head>
        <jsp:include page="/WEB-INF/paginas/comunes/contenidoHead.jsp"></jsp:include>
        </head>
        <body>
        <jsp:include page="/WEB-INF/paginas/comunes/cabecero.jsp"></jsp:include>

        <jsp:include page="/WEB-INF/paginas/cliente/xml/formulario.jsp"></jsp:include>

        <c:forEach var="archivoErroneo" items="${archivosErroneos}">
            <div class="alert alert-danger" role="alert">
                <div class="container">
                    ${archivoErroneo}
                </div>
            </div>
        </c:forEach>

        <jsp:include page="/WEB-INF/paginas/comunes/piePagina.jsp"></jsp:include>
    </body>
</html>