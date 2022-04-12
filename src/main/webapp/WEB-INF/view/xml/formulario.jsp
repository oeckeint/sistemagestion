<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<c:set var="language" value="${not empty param.language ? param.language : not empty language ? language : 'es'}" scope="session"/>
<fmt:setLocale value="${language}"/>
<fmt:setBundle basename="labels"/>
<!DOCTYPE html>
<html lang="${language}">
    <head>
        <jsp:include page="/WEB-INF/paginas/comunes/contenidoHead.jsp"></jsp:include>
        <meta http-equiv = "Content-Type" content = "multipart/form-data; charset = utf-8" />
    </head>
    <body>
        <jsp:include page="/WEB-INF/paginas/comunes/cabecero.jsp"></jsp:include>

        <jsp:include page="/WEB-INF/paginas/cliente/xml/formulario.jsp"></jsp:include>
        
        <jsp:include page="../comunes/mensajeError.jsp"></jsp:include>

        <jsp:include page="/WEB-INF/paginas/comunes/piePagina.jsp"></jsp:include>
    </body>
</html>