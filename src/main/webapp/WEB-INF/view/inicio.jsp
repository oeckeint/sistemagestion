<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<jsp:include page="/WEB-INF/view/comunes/Lang.jsp"></jsp:include>
<!DOCTYPE html>
<html lang="${language}">
    <head>
        <jsp:include page="/WEB-INF/paginas/comunes/contenidoHead.jsp"></jsp:include>
        </head>
        <body>
        <jsp:include page="/WEB-INF/paginas/comunes/cabecero.jsp"></jsp:include>
        <div class="alert alert-warning alert-dismissible fade show" role="alert">
            <div class="container">
                <c:choose>
                    <c:when test="${mensaje eq 'scriptNoValido'}">
                        <fmt:message key="mensaje.error.scriptValor"></fmt:message>
                    </c:when>
                    <c:otherwise>
                        <fmt:message key="mensaje.default"></fmt:message>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>
        <jsp:include page="/WEB-INF/paginas/cliente/inicio/enlaces.jsp"></jsp:include>

        <jsp:include page="/WEB-INF/paginas/comunes/piePagina.jsp"></jsp:include>
    </body>
</html>
