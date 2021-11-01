<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <jsp:include page="/WEB-INF/paginas/comunes/contenidoHead.jsp"></jsp:include>
        </head>
        <body>
        <jsp:include page="/WEB-INF/paginas/comunes/cabecero.jsp"></jsp:include>
        <div class="alert alert-warning alert-dismissible fade show" role="alert">
            <div class="container"><h5 class="m-0"><a href="javascript:history.back();"><i class="fas fa-arrow-circle-left text-success"></i></a> ${mensaje}</h5></div>
        </div>
        <div class="container">
            <div class="row py-5">
                <div class="col-12 col-md-6 col-lg-4">
                    <a class="btn btn-success btn-block py-3 my-1" href="${pageContext.request.contextPath}/configuraciones/respaldardb">Respaldar DB <span class="badge badge-danger">New!</span></a>
                </div>
                <div class="col-12 col-md-6 col-lg-4">
                    <a class="btn btn-success btn-block py-3 my-1" href="${pageContext.request.contextPath}/configuraciones/restaurardb">Restaurar DB <span class="badge badge-danger">New!</span></a>
                </div>
            </div>
        </div>
        <jsp:include page="/WEB-INF/paginas/comunes/piePagina.jsp"></jsp:include>
    </body>
</html>