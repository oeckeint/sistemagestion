<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<jsp:include page="/WEB-INF/view/comunes/Lang.jsp"></jsp:include>
<!DOCTYPE html>
<html lang="${language}">
    <head>
        <jsp:include page="/WEB-INF/paginas/comunes/contenidoHead.jsp"></jsp:include>
        <meta http-equiv = "Content-Type" content = "multipart/form-data; charset = utf-8" />
    </head>
    <body>
        <jsp:include page="/WEB-INF/paginas/comunes/cabecero.jsp"></jsp:include>

        <jsp:include page="/WEB-INF/paginas/cliente/xml/formulario.jsp"></jsp:include>
        
        <c:forEach var="archivoErroneo" items="${archivosErroneos}">
            <div class="alert alert-danger" role="alert">
                <div class="container">${archivoErroneo}</div>
            </div>
        </c:forEach>

        <jsp:include page="/WEB-INF/paginas/comunes/piePagina.jsp"></jsp:include>
    </body>
</html>